package org.theseinitiatives.smarthouseapp.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.druk.rxdnssd.BonjourService;

import org.json.JSONException;
import org.json.JSONObject;
import org.theseinitiatives.smarthouseapp.R;
import org.theseinitiatives.smarthouseapp.application.App;
import org.theseinitiatives.smarthouseapp.data.DeviceType;
import org.theseinitiatives.smarthouseapp.helper.ItemTouchHelperAdapter;
import org.theseinitiatives.smarthouseapp.helper.ItemTouchHelperViewHolder;
import org.theseinitiatives.smarthouseapp.helper.WlinkHelper;
import org.theseinitiatives.smarthouseapp.repository.HomeDeviceRepository;
import org.theseinitiatives.smarthouseapp.room.Home;
import org.theseinitiatives.smarthouseapp.room.HomeDevice;
import org.theseinitiatives.smarthouseapp.service.ApiService;
import org.theseinitiatives.smarthouseapp.service.HomeApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeDeviceAdapter extends RecyclerView.Adapter<HomeDeviceAdapter.HomeDeviceViewHolder> implements ItemTouchHelperAdapter {

    public static String API_SWITCH = "api_switch";
    public static String API_INFO = "api_info";
    public static String API_WIFI = "api_wifi";
    public static String API_STARTUP = "api_startup";

    private Context context;
    private Home home;
    private ArrayList<HomeDevice> mDataset = new ArrayList<>();

    private HomeDeviceRepository homeDeviceRepository;

    public HomeDeviceAdapter(Context context, Home home) {
        this.context = context;
        this.home = home;
        homeDeviceRepository = new HomeDeviceRepository(App.getInstance());
    }

    public HomeDeviceRepository getHomeDeviceRepository(){
        return homeDeviceRepository;
    }

    public void setData(List<HomeDevice> homeDevices){
        mDataset.clear();
        mDataset.addAll(homeDevices);
        notifyDataSetChanged();
    }

    private boolean isHomeDeviceExist(HomeDevice homeDevice){
        for (HomeDevice dev : mDataset){
            if (dev.getId().equals(homeDevice.getId()))
                return true;
        }
        return false;
    }

    public void addDataFromService(BonjourService service){
        HomeDevice homeDevice = new HomeDevice();
        String id = service.getTxtRecords().get("id");
        if (id!=null){
            homeDevice.setId(id);
            homeDevice.setName(service.getServiceName());
            homeDevice.setStatus("off");
            if (!isHomeDeviceExist(homeDevice))
                mDataset.add(homeDevice);

            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public HomeDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_device_list_layout, parent, false);
        HomeDeviceAdapter.HomeDeviceViewHolder vh = new HomeDeviceAdapter.HomeDeviceViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeDeviceViewHolder holder, int position) {
        holder.homeDeviceName.setText(mDataset.get(position).getName());
        if (mDataset.get(position).getStatus().equals("off")){
            holder.homeDeviceIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.lamp_off));
        }else{
            holder.homeDeviceIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.lamp_on));
        }
        holder.itemView.setOnClickListener(new HomeDeviceClickListener(position));
        holder.homeDeviceIcon.setOnClickListener(new HomeDeviceClickListener(position));
        holder.actionAdd.setVisibility(View.GONE);
        holder.actionDelete.setOnClickListener(new HomeDeviceClickListener(position));
        getHomeDeviceInfo(holder.homeDeviceIcon,position);
    }

    private void checkHomeDeviceExist(HomeDevice homeDevice){
        new getHomeDeviceByIdAsyncTask(homeDevice).execute();
    }

    private class getHomeDeviceByIdAsyncTask extends AsyncTask<Void, Void, HomeDevice> {
        private HomeDevice homeDevice;

        getHomeDeviceByIdAsyncTask(HomeDevice homeDevice) {
            this.homeDevice = homeDevice;
        }

        @Override
        protected HomeDevice doInBackground(Void... voids) {
            return homeDeviceRepository.getHomeDevice(homeDevice.getId());
        }

        @Override
        protected void onPostExecute(HomeDevice dev){
            if (dev!=null){
                mDataset.remove(homeDevice);
                notifyDataSetChanged();
            }
        }
    }

    public class HomeDeviceClickListener implements View.OnClickListener {
        private int position;

        HomeDeviceClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick (View view) {
            HomeDevice homeDevice = mDataset.get(position);
            switch (view.getId()){
                case R.id.homeDeviceIcon:
                    // TODO homedeviciIcon click action
                    final HomeApi homeApi = ApiService.getTempClient(home.getUrl()).create(HomeApi.class);
                    Log.d("TAG", "onClick: status = "+homeDevice.getStatus());
                    String url = "/switch/"+homeDevice.getId();
                    if (homeDevice.getStatus().equals("off")){
                        url = url.concat("/on");
                    } else {
                        url = url.concat("/off");
                    }

                    Call<ResponseBody> call = homeApi.setSwitch(url);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            ImageView icon = (ImageView)view;
                            if (homeDevice.getStatus().equals("off")){
                                Log.d("TAG", "onResponse: the lamp turned on");
                                homeDevice.setStatus("on");
                                icon.setImageDrawable(DeviceType.getDeviceDrawable(context, DeviceType.LAMP_ON));
                            }else{
                                Log.d("TAG", "onResponse: the lamp turned off");
                                homeDevice.setStatus("off");
                                icon.setImageDrawable(DeviceType.getDeviceDrawable(context, DeviceType.LAMP_OFF));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                    break;

                case R.id.homeDeviceView:
                    // TODO homeDeviceView click action
                    break;

                case R.id.addAction:
                    homeDeviceRepository.insertHomeDevice(homeDevice);
                    onItemDismiss(position);
                    break;

                case R.id.deleteAction:
                    AlertDialog.Builder delDialog = new AlertDialog.Builder(context);
                    delDialog.setMessage("Are you sure want to delete?");
                    delDialog.setCancelable(true);

                    delDialog.setPositiveButton(
                            "Yes",
                            (dialog, id) -> homeDeviceRepository.deleteHomeDevice(homeDevice));

                    delDialog.setNegativeButton(
                            "No",
                            (dialog, id) -> dialog.cancel());

                    AlertDialog alert11 = delDialog.create();
                    alert11.show();
                    break;
                default:
                    break;
            }

        }
    }

    private class ApiCallback implements Callback<ResponseBody> {

        String mode;
        View view;
        int position;
        String status;

        public ApiCallback(View view, int position, String status, String mode) {
            this.view = view;
            this.position = position;
            this.status = status;
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.body() != null) {
                try {
                    JSONObject body = new JSONObject(response.body().string());
                    if (body.getInt("error")==0){
                        if (mode.equals(API_SWITCH)){
                            mDataset.get(position).setStatus(status);
                            ImageView icon = (ImageView)view;
                            if (status.equals(WlinkHelper.Switch.ON)){
                                icon.setImageDrawable(DeviceType.getDeviceDrawable(context, DeviceType.LAMP_ON));
                            } else {
                                icon.setImageDrawable(DeviceType.getDeviceDrawable(context, DeviceType.LAMP_OFF));
                            }
                        } else if (mode.equals(API_INFO)){
                            JSONObject data = new JSONObject(body.getString("data"));
                            ImageView icon = (ImageView)view;
                            String status = data.getString("switch");
                            if (status.equals(WlinkHelper.Switch.ON)){
                                icon.setImageDrawable(DeviceType.getDeviceDrawable(context, DeviceType.LAMP_ON));
                            } else {
                                icon.setImageDrawable(DeviceType.getDeviceDrawable(context, DeviceType.LAMP_OFF));
                            }
                            icon.setOnClickListener(new HomeDeviceClickListener(position));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        HomeDevice prev = mDataset.remove(fromPosition);
        mDataset.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public class HomeDeviceViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView homeDeviceName;
        ImageView homeDeviceIcon;
        ImageView actionAdd;
        ImageView actionDelete;

        public HomeDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            homeDeviceIcon = itemView.findViewById(R.id.homeDeviceIcon);
            homeDeviceName = itemView.findViewById(R.id.homeDeviceName);
            actionAdd = itemView.findViewById(R.id.addAction);
            actionDelete = itemView.findViewById(R.id.deleteAction);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }
    }

    public void saveData(){
        HomeDevice [] homeDevices = mDataset.toArray(new HomeDevice[mDataset.size()]);
        homeDeviceRepository.insertHomeDevice(homeDevices);
    }

    private void getHomeDeviceInfo(View view, int position){
        // TODO getHomeDeviceInfo
//        final EWlinkApi eWlinkApi = ApiService.getTempClient(mDataset.get(position).getUrl()).create(EWlinkApi.class);
//        Call<ResponseBody> call = eWlinkApi.getInfo(WlinkHelper.Info(mDataset.get(position).getId()));
//        call.enqueue(new ApiCallback(view, position, null, API_INFO));
    }
}
