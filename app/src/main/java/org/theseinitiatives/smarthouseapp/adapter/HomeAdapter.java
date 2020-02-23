package org.theseinitiatives.smarthouseapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.druk.rxdnssd.BonjourService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.theseinitiatives.smarthouseapp.R;
import org.theseinitiatives.smarthouseapp.activity.HomeDeviceActivity;
import org.theseinitiatives.smarthouseapp.application.App;
import org.theseinitiatives.smarthouseapp.helper.ItemTouchHelperAdapter;
import org.theseinitiatives.smarthouseapp.helper.ItemTouchHelperViewHolder;
import org.theseinitiatives.smarthouseapp.repository.HomeDeviceRepository;
import org.theseinitiatives.smarthouseapp.room.Home;
import org.theseinitiatives.smarthouseapp.repository.HomeRepository;
import org.theseinitiatives.smarthouseapp.room.HomeDevice;
import org.theseinitiatives.smarthouseapp.service.ApiService;
import org.theseinitiatives.smarthouseapp.service.HomeApi;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> implements ItemTouchHelperAdapter {

    public static String SCAN_MODE = "scan";
    public static String HOME_MODE = "home";

    private Context context;
    private ArrayList<Home> mDataset = new ArrayList<>();
    private String mode;

    private HomeRepository homeRepository;
    private HomeDeviceRepository homeDeviceRepository;

    public HomeAdapter(Context context){
        this.context = context;
        this.mode = HOME_MODE;
        homeRepository = new HomeRepository(App.getInstance());
        homeDeviceRepository = new HomeDeviceRepository(App.getInstance());
    }

    public HomeAdapter(Context context, String mode){
        this.context = context;
        this.mode = mode;
        homeRepository = new HomeRepository(App.getInstance());
        homeDeviceRepository = new HomeDeviceRepository(App.getInstance());
    }

    public void setData(List<Home> homes){
        mDataset.clear();
        mDataset.addAll(homes);
        notifyDataSetChanged();
    }

    private boolean isHomeExist(Home home){
        for (Home dev : mDataset){
            if (dev.getId().equals(home.getId()))
                return true;
        }
        return false;
    }

    public void addDataFromService(BonjourService service){
        Home home = new Home();
        String id = service.getTxtRecords().get("id");
        if (id!=null){
            home.setId(id);
            home.setName(service.getServiceName());
            home.setType("hub");
            for(InetAddress inetAddress : service.getInetAddresses()){
                Log.d("TAG", "check::addDataFromService: inetAddress = "+inetAddress.getHostAddress());
                Log.d("TAG", "check::addDataFromService: getPort = "+service.getPort());
                home.setUrl("http://"+inetAddress.getHostAddress()+":"+service.getPort());
            }
            if (!isHomeExist(home))
                mDataset.add(home);

            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_list_layout, parent, false);
        HomeViewHolder vh = new HomeViewHolder(context, v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, final int position) {
        holder.homeName.setText(mDataset.get(position).getName());
        holder.homeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.home));
        if (mode.equals(HOME_MODE)){
            holder.itemView.setOnClickListener(new HomeClickListener(position));
            holder.actionAdd.setVisibility(View.GONE);
            holder.actionDelete.setOnClickListener(new HomeClickListener(position));
            getHomeInfo(holder.homeIcon,position);
        } else if (mode.equals(SCAN_MODE)){
            holder.actionDelete.setVisibility(View.GONE);
            holder.actionAdd.setOnClickListener(new HomeClickListener(position));
            checkHomeExist(mDataset.get(position));
        }
    }

    private void checkHomeExist(Home home){
        new getHomeByIdAsyncTask(home).execute();
    }

    private class getHomeByIdAsyncTask extends AsyncTask<Void, Void, Home> {
        private Home home;

        getHomeByIdAsyncTask(Home home) {
            this.home = home;
        }

        @Override
        protected Home doInBackground(Void... voids) {
            return homeRepository.getHome(home.getId());
        }

        @Override
        protected void onPostExecute(Home dev){
            if (dev!=null){
                mDataset.remove(home);
                notifyDataSetChanged();
            }
        }
    }

    public class HomeClickListener implements View.OnClickListener {
        private int position;

        HomeClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick (View view) {
            Home home = mDataset.get(position);
            switch (view.getId()){
                case R.id.homeView:
                    Intent intent = new Intent(context, HomeDeviceActivity.class);
                    intent.putExtra(HomeDeviceActivity.HOME_EXTRA, home);
                    context.startActivity(intent);
                    break;

                case R.id.addAction:
                    homeRepository.insertHome(home);
                    final HomeApi homeApi = ApiService.getTempClient(home.getUrl()).create(HomeApi.class);
                    Call<ResponseBody> call = homeApi.getAllDevices();
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                JSONArray body = new JSONArray(response.body().string());
                                ArrayList<HomeDevice> homeDevices = new ArrayList<>();
                                for (int i = 0; i < body.length(); i++) {
                                    JSONObject homeDeviceObj = body.getJSONObject(i);
                                    HomeDevice homeDevice = new HomeDevice();
                                    homeDevice.setId(homeDeviceObj.getString("id"));
                                    homeDevice.setHomeId(home.getId());
                                    homeDevice.setName(homeDeviceObj.getString("name"));
                                    homeDevice.setServiceTypeName(homeDeviceObj.getString("ServiceType_name"));
                                    homeDevice.setType(homeDeviceObj.getString("type"));
                                    homeDevice.setStatus("off");
                                    homeDevices.add(homeDevice);
                                }
                                HomeDevice [] homeDevicesArray = homeDevices.toArray(new HomeDevice[homeDevices.size()]);
                                homeDeviceRepository.insertHomeDevice(homeDevicesArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                    onItemDismiss(position);
                    break;

                case R.id.deleteAction:
                    AlertDialog.Builder delDialog = new AlertDialog.Builder(context);
                    delDialog.setMessage("Are you sure want to delete?");
                    delDialog.setCancelable(true);

                    delDialog.setPositiveButton(
                            "Yes",
                            (dialog, id) -> homeRepository.deleteHome(home));

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

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Home prev = mDataset.remove(fromPosition);
        mDataset.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        TextView homeName;
        ImageView homeIcon;
        ImageView actionAdd;
        ImageView actionDelete;

        HomeViewHolder(final Context context, @NonNull View itemView) {
            super(itemView);
            homeIcon = itemView.findViewById(R.id.homeIcon);
            homeName = itemView.findViewById(R.id.homeName);
            actionAdd = itemView.findViewById(R.id.addAction);
            actionDelete = itemView.findViewById(R.id.deleteAction);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundResource(R.drawable.gradient);
        }
    }

    private void getHomeInfo(View view, int position){
        // TODO add getHomeInfo implementation
//        final EWlinkApi eWlinkApi = ApiService.getTempClient(mDataset.get(position).getUrl()).create(EWlinkApi.class);
//        Call<ResponseBody> call = eWlinkApi.getInfo(WlinkHelper.Info(mDataset.get(position).getId()));
//        call.enqueue(new ApiCallback(view, position, null, API_INFO));
    }
}