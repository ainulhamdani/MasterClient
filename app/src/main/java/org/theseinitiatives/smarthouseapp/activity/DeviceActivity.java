package org.theseinitiatives.smarthouseapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.theseinitiatives.smarthouseapp.R;
import org.theseinitiatives.smarthouseapp.application.App;
import org.theseinitiatives.smarthouseapp.data.DeviceType;
import org.theseinitiatives.smarthouseapp.helper.WlinkHelper;
import org.theseinitiatives.smarthouseapp.room.Device;
import org.theseinitiatives.smarthouseapp.repository.DeviceRepository;
import org.theseinitiatives.smarthouseapp.service.ApiService;
import org.theseinitiatives.smarthouseapp.service.EWlinkApi;

import java.io.IOException;

import static org.theseinitiatives.smarthouseapp.adapter.DeviceAdapter.API_INFO;
import static org.theseinitiatives.smarthouseapp.adapter.DeviceAdapter.API_SWITCH;
import static org.theseinitiatives.smarthouseapp.adapter.DeviceAdapter.API_WIFI;

public class DeviceActivity extends AppCompatActivity {

    public final static String DEVICE_EXTRA = "device_extra";

    private Device device;
    private DeviceRepository deviceRepository;

    private ImageView deviceIcon;

    private EWlinkApi eWlinkApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        deviceIcon = findViewById(R.id.deviceIcon);
        device = getIntent().getParcelableExtra(DEVICE_EXTRA);
        eWlinkApi = ApiService.getTempClient(device.getUrl()).create(EWlinkApi.class);
        getDeviceInfo(deviceIcon);

        setTitle();

        deviceRepository = new DeviceRepository(App.getInstance());
        Log.d("TAG", "check:: onCreate: device URL = "+device.getUrl());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_edit_name:
                AlertDialog.Builder edtDialog = new AlertDialog.Builder(this);
                edtDialog.setMessage("Edit Device Name");

                LayoutInflater inflaterName = getLayoutInflater();
                View viewName = inflaterName.inflate(R.layout.dialog_name, null);
                final EditText input = viewName.findViewById(R.id.deviceName);
                input.setText(device.getName());

                edtDialog.setView(viewName);

                edtDialog.setCancelable(true);

                edtDialog.setPositiveButton(
                        "Save",
                        (dialog, id) -> {
                            device.setName(input.getText().toString());
                            setTitle();
                            deviceRepository.insertDevice(device);
                        });

                edtDialog.setNegativeButton(
                        "Cancel",
                        (dialog, id) -> dialog.cancel());

                AlertDialog alertEdit = edtDialog.create();
                alertEdit.show();

                final Button buttonEdit = alertEdit.getButton(AlertDialog.BUTTON_POSITIVE);
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // my validation condition
                        if (input.getText().length() > 0) {
                            buttonEdit.setEnabled(true);
                        } else {
                            buttonEdit.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                break;
            case R.id.action_edit_wifi:
                AlertDialog.Builder edtWifiDialog = new AlertDialog.Builder(this);
                LayoutInflater inflaterWifi = getLayoutInflater();
                View viewWifi = inflaterWifi.inflate(R.layout.dialog_wifi, null);
                final EditText ssid = viewWifi.findViewById(R.id.wifi_ssid);
                final EditText password = viewWifi.findViewById(R.id.wifi_pw);
                final Button defaultWifi = viewWifi.findViewById(R.id.wifiDefault);
                defaultWifi.setOnClickListener(view -> {
                    ssid.setText("sonoffDiy");
                    password.setText("20170618sn");
                });

                edtWifiDialog.setView(viewWifi);

                edtWifiDialog.setCancelable(true);

                edtWifiDialog.setPositiveButton(
                        "Save",
                        (dialog, id) -> {
                            String ssidName = ssid.getText().toString();
                            String wifiPass = password.getText().toString();
                            changeDeviceWifi(ssidName,wifiPass);
                        });

                edtWifiDialog.setNegativeButton(
                        "Cancel",
                        (dialog, id) -> dialog.cancel());

                AlertDialog alertWifiEdit = edtWifiDialog.create();
                alertWifiEdit.show();

                final Button buttonWifi = alertWifiEdit.getButton(AlertDialog.BUTTON_POSITIVE);
                buttonWifi.setEnabled(false);
                ssid.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // my validation condition
                        if (ssid.getText().length() > 0) {
                            buttonWifi.setEnabled(true);
                        } else {
                            buttonWifi.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                break;
            case R.id.action_del:
                AlertDialog.Builder delDialog = new AlertDialog.Builder(this);
                delDialog.setMessage("Are you sure want to delete?");
                delDialog.setCancelable(true);

                delDialog.setPositiveButton(
                        "Yes",
                        (dialog, id) -> {
                            deviceRepository.deleteDevice(device);
                            finish();
                        });

                delDialog.setNegativeButton(
                        "No",
                        (dialog, id) -> dialog.cancel());

                AlertDialog alertDel = delDialog.create();
                alertDel.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTitle(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(device.getName());
        }
    }

    private void changeDeviceWifi(String ssid, String passowrd){
        Call<ResponseBody> call = eWlinkApi.setWifi(WlinkHelper.WifiChange(device.getId(),ssid,passowrd));
        call.enqueue(new ApiCallback(null, null, API_WIFI));
    }

    private void getDeviceInfo(View view){
        Call<ResponseBody> call = eWlinkApi.getInfo(WlinkHelper.Info(device.getId()));
        call.enqueue(new ApiCallback(view, null, API_INFO));
    }

    private class ApiCallback implements Callback<ResponseBody> {

        String mode;
        View view;
        String status;

        public ApiCallback(View view, String status, String mode) {
            this.view = view;
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
                            device.setStatus(status);
                            ImageView icon = (ImageView)view;
                            if (status.equals(WlinkHelper.Switch.ON)){
                                icon.setImageDrawable(DeviceType.getDeviceDrawable(getApplicationContext(), DeviceType.LAMP_ON));
                            } else {
                                icon.setImageDrawable(DeviceType.getDeviceDrawable(getApplicationContext(), DeviceType.LAMP_OFF));
                            }
                        } else if (mode.equals(API_INFO)){
                            JSONObject data = new JSONObject(body.getString("data"));
                            ImageView icon = (ImageView)view;
                            String status = data.getString("switch");
                            if (status.equals(WlinkHelper.Switch.ON)){
                                icon.setImageDrawable(DeviceType.getDeviceDrawable(getApplicationContext(), DeviceType.LAMP_ON));
                            } else {
                                icon.setImageDrawable(DeviceType.getDeviceDrawable(getApplicationContext(), DeviceType.LAMP_OFF));
                            }
                            icon.setOnClickListener(new DeviceClickListener());
                        } else if (mode.equals(API_WIFI)){
                            deviceRepository.deleteDevice(device);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private class DeviceClickListener implements View.OnClickListener {
            @Override
            public void onClick (View view) {
                switch (view.getId()){
                    case R.id.deviceIcon:
                        //action
                        if (device.getStatus().equals("on")){
                            Call<ResponseBody> call = eWlinkApi.setSwitch(WlinkHelper.SwitchOff(device.getId()));
                            call.enqueue(new ApiCallback(view, "off", API_SWITCH));
                        } else {
                            Call<ResponseBody> call = eWlinkApi.setSwitch(WlinkHelper.SwitchOn(device.getId()));
                            call.enqueue(new ApiCallback(view, "on", API_SWITCH));
                        }

                        break;
                    default:
                        break;
                }

            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    }
}
