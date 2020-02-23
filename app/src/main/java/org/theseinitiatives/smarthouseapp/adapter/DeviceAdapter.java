package org.theseinitiatives.smarthouseapp.adapter;

import android.content.Context;
import android.content.Intent;
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
import org.theseinitiatives.smarthouseapp.activity.DeviceActivity;
import org.theseinitiatives.smarthouseapp.application.App;
import org.theseinitiatives.smarthouseapp.data.DeviceType;
import org.theseinitiatives.smarthouseapp.helper.ItemTouchHelperAdapter;
import org.theseinitiatives.smarthouseapp.helper.ItemTouchHelperViewHolder;
import org.theseinitiatives.smarthouseapp.helper.WlinkHelper;
import org.theseinitiatives.smarthouseapp.repository.HomeDeviceRepository;
import org.theseinitiatives.smarthouseapp.room.Device;
import org.theseinitiatives.smarthouseapp.repository.DeviceRepository;
import org.theseinitiatives.smarthouseapp.room.HomeDevice;
import org.theseinitiatives.smarthouseapp.service.ApiService;
import org.theseinitiatives.smarthouseapp.service.EWlinkApi;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> implements ItemTouchHelperAdapter {

    public static String SCAN_MODE = "scan";
    public static String DEVICE_MODE = "device";
    public static String API_SWITCH = "api_switch";
    public static String API_INFO = "api_info";
    public static String API_WIFI = "api_wifi";
    public static String API_STARTUP = "api_startup";

    private Context context;
    private ArrayList<Device> mDataset = new ArrayList<>();
    private String mode;

    private DeviceRepository deviceRepository;
    private HomeDeviceRepository homeDeviceRepository;

    public DeviceAdapter(Context context) {
        this.context = context;
        this.mode = DEVICE_MODE;
        deviceRepository = new DeviceRepository(App.getInstance());
        homeDeviceRepository = new HomeDeviceRepository(App.getInstance());
    }

    public DeviceAdapter(Context context, String mode) {
        this.context = context;
        this.mode = mode;
        deviceRepository = new DeviceRepository(App.getInstance());
        homeDeviceRepository = new HomeDeviceRepository(App.getInstance());
    }

    public void setData(List<Device> devices){
        mDataset.clear();
        mDataset.addAll(devices);
        notifyDataSetChanged();
    }

    public void setDataFromService(List<BonjourService> services){
        mDataset.clear();
        for (BonjourService service : services){
            Device device = new Device();
            device.setId(Objects.requireNonNull(service.getTxtRecords().get("id")));
            device.setName(service.getServiceName());
            device.setType(DeviceType.LAMP);
            for(InetAddress inetAddress : service.getInetAddresses()){
                device.setUrl(inetAddress.getHostAddress());
            }
            device.setStatus("off");
            if (!isDeviceExist(device))
                isDeviceExistInHomeDevice(device);
        }

        notifyDataSetChanged();
    }

    private boolean isDeviceExist(Device device){
        for (Device dev : mDataset){
            if (dev.getId().equals(device.getId()))
                return true;
        }
        return false;
    }

    private void isDeviceExistInHomeDevice(Device device){
        new getHomeDeviceByIdAsyncTask(device).execute();
    }



    private class getHomeDeviceByIdAsyncTask extends AsyncTask<Void, Void, HomeDevice> {
        private Device homeDevice;

        getHomeDeviceByIdAsyncTask(Device homeDevice) {
            this.homeDevice = homeDevice;
        }

        @Override
        protected HomeDevice doInBackground(Void... voids) {
            return homeDeviceRepository.getHomeDevice(homeDevice.getId());
        }

        @Override
        protected void onPostExecute(HomeDevice dev){
            if (dev==null){
                mDataset.add(homeDevice);
                notifyDataSetChanged();
            }
        }
    }

    public void addDataFromService(BonjourService service){
        Device device = new Device();
        String id = service.getTxtRecords().get("id");
        if (id!=null){
            device.setId(id);
            device.setName(service.getServiceName());
            device.setType(DeviceType.LAMP);
            for(InetAddress inetAddress : service.getInetAddresses()){
                Log.d("TAG", "check::addDataFromService: inetAddress = "+inetAddress.getHostAddress());
                device.setUrl("http://"+inetAddress.getHostAddress()+":"+service.getPort());
            }
            device.setStatus("off");
            if (!isDeviceExist(device))
                isDeviceExistInHomeDevice(device);

            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_layout, parent, false);
        DeviceAdapter.DeviceViewHolder vh = new DeviceAdapter.DeviceViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.deviceName.setText(mDataset.get(position).getName());
        holder.deviceIcon.setImageDrawable(DeviceType.getDeviceDrawable(context,mDataset.get(position).getType()));
        if (mode.equals(DEVICE_MODE)){
            holder.deviceIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.lamp_ofl));
            holder.itemView.setOnClickListener(new DeviceClickListener(position));
            holder.actionAdd.setVisibility(View.GONE);
            holder.actionDelete.setOnClickListener(new DeviceClickListener(position));
            getDeviceInfo(holder.deviceIcon,position);
        } else if (mode.equals(SCAN_MODE)){
            holder.actionDelete.setVisibility(View.GONE);
            holder.actionAdd.setOnClickListener(new DeviceClickListener(position));
            checkDeviceExist(mDataset.get(position));
        }
    }

    private void checkDeviceExist(Device device){
        new getDeviceByIdAsyncTask(device).execute();
    }

    private class getDeviceByIdAsyncTask extends AsyncTask<Void, Void, Device> {
        private Device device;

        getDeviceByIdAsyncTask(Device device) {
            this.device = device;
        }

        @Override
        protected Device doInBackground(Void... voids) {
            return deviceRepository.getDevice(device.getId());
        }

        @Override
        protected void onPostExecute(Device dev){
            if (dev!=null){
                mDataset.remove(device);
                notifyDataSetChanged();
            }
        }
    }

    public class DeviceClickListener implements View.OnClickListener {
        private int position;

        DeviceClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick (View view) {
            Device device = mDataset.get(position);
            switch (view.getId()){
                case R.id.deviceIcon:
                    //action
                    final EWlinkApi eWlinkApi = ApiService.getTempClient(mDataset.get(position).getUrl()).create(EWlinkApi.class);
                    if (mDataset.get(position).getStatus().equals("on")){
                        Call<ResponseBody> call = eWlinkApi.setSwitch(WlinkHelper.SwitchOff(mDataset.get(position).getId()));
                        call.enqueue(new ApiCallback(view, position, "off", API_SWITCH));
                    } else {
                        Call<ResponseBody> call = eWlinkApi.setSwitch(WlinkHelper.SwitchOn(mDataset.get(position).getId()));
                        call.enqueue(new ApiCallback(view, position, "on", API_SWITCH));
                    }

                    break;

                case R.id.deviceView:
                    Intent intent = new Intent(context, DeviceActivity.class);
                    intent.putExtra(DeviceActivity.DEVICE_EXTRA, device);
                    context.startActivity(intent);
                    break;

                case R.id.addAction:
                    deviceRepository.insertDevice(device);
                    onItemDismiss(position);
                    break;

                case R.id.deleteAction:
                    AlertDialog.Builder delDialog = new AlertDialog.Builder(context);
                    delDialog.setMessage("Are you sure want to delete?");
                    delDialog.setCancelable(true);

                    delDialog.setPositiveButton(
                            "Yes",
                            (dialog, id) -> deviceRepository.deleteDevice(device));

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
                            icon.setOnClickListener(new DeviceClickListener(position));
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
        Device prev = mDataset.remove(fromPosition);
        mDataset.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView deviceName;
        ImageView deviceIcon;
        ImageView actionAdd;
        ImageView actionDelete;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceIcon = itemView.findViewById(R.id.deviceIcon);
            deviceName = itemView.findViewById(R.id.deviceName);
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

    private void getDeviceInfo(View view, int position){
        final EWlinkApi eWlinkApi = ApiService.getTempClient(mDataset.get(position).getUrl()).create(EWlinkApi.class);
        Call<ResponseBody> call = eWlinkApi.getInfo(WlinkHelper.Info(mDataset.get(position).getId()));
        call.enqueue(new ApiCallback(view, position, null, API_INFO));
    }
}
