package org.theseinitiatives.smarthouseapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.theseinitiatives.smarthouseapp.R;
import org.theseinitiatives.smarthouseapp.adapter.DeviceAdapter;
import org.theseinitiatives.smarthouseapp.adapter.HomeAdapter;
import org.theseinitiatives.smarthouseapp.application.App;
import org.theseinitiatives.smarthouseapp.helper.ItemTouchHelperAdapter;
import org.theseinitiatives.smarthouseapp.helper.OnStartDragListener;
import org.theseinitiatives.smarthouseapp.helper.SimpleItemTouchHelperCallback;
import org.theseinitiatives.smarthouseapp.room.Device;
import org.theseinitiatives.smarthouseapp.room.Home;
import org.theseinitiatives.smarthouseapp.viewmodel.DeviceViewModel;
import org.theseinitiatives.smarthouseapp.viewmodel.HomeViewModel;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeActivity extends AppCompatActivity implements OnStartDragListener {

    public static final String TAG = "HomeActivity";

    private ItemTouchHelper mItemTouchHelper;

    private RecyclerView deviceRV;
    private DeviceAdapter deviceAdapter;
    private DeviceViewModel deviceViewModel;
    private View deviceRVContainer;

    private RecyclerView homeRV;
    private HomeAdapter homeAdapter;
    private HomeViewModel homeViewModel;
    private View homeRVContainer;

    private View backLayout;

    FloatingActionButton fab_main;
    ExtendedFloatingActionButton fab_device;
    ExtendedFloatingActionButton fab_home;
//    ExtendedFloatingActionButton fab_url;
    boolean isFABOpen = false;
    ImageView refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        backLayout = findViewById(R.id.empty_home);
        refreshButton = findViewById(R.id.refresh_list);
        homeRVContainer = findViewById(R.id.homeRV);
        deviceRVContainer = findViewById(R.id.deviceRV);

        deviceViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(App.getInstance())).get(DeviceViewModel.class);
        deviceViewModel.getListDevices().observe(this, getDevices);

        deviceAdapter = new DeviceAdapter(this);
        deviceRV = (RecyclerView) findViewById(R.id.device_list);
        deviceRV.setHasFixedSize(true);
        deviceRV.setAdapter(deviceAdapter);
        deviceRV.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) deviceAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(deviceRV);

        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(App.getInstance())).get(HomeViewModel.class);
        homeViewModel.getListHomes().observe(this, getHomes);

        homeAdapter = new HomeAdapter(this);
        homeRV = (RecyclerView) findViewById(R.id.home_list);
        homeRV.setHasFixedSize(true);
        homeRV.setAdapter(homeAdapter);
        homeRV.setLayoutManager(new LinearLayoutManager(this));

        fab_main = findViewById(R.id.fab);
        fab_device = findViewById(R.id.fab_dns);
        fab_home = findViewById(R.id.fab_ip);
//        fab_url = findViewById(R.id.fab_url);
        fab_main.setOnClickListener(view -> {
            if(!isFABOpen){
                showFABMenu();
            }else{
                closeFABMenu();
            }
        });

        fab_device.setOnClickListener(view -> {
            closeFABMenu();
            openScanDeviceActivity();
        });
        fab_home.setOnClickListener(view -> {
            closeFABMenu();
            openScanHomeActivity();
        });
//        fab_url.setOnClickListener(view -> {
//            closeFABMenu();
//        });
        refreshButton.setOnClickListener(view -> {
            refreshList();
        });
    }

    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private final Observer<List<Device>> getDevices = new Observer<List<Device>>() {
        @Override
        public void onChanged(List<Device> devices) {
            if (devices != null) {
                deviceAdapter.setData(devices);
                if (deviceAdapter.getItemCount() > 0) {
                    backLayout.setVisibility(View.GONE);
                    deviceRVContainer.setVisibility(View.VISIBLE);
                } else {
                    deviceRVContainer.setVisibility(View.GONE);
                }
                if (homeAdapter.getItemCount() == 0 && deviceAdapter.getItemCount() == 0) {
                    backLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private final Observer<List<Home>> getHomes = new Observer<List<Home>>() {
        @Override
        public void onChanged(List<Home> homes) {
            if (homes != null) {
                homeAdapter.setData(homes);
                if (homeAdapter.getItemCount() > 0) {
                    backLayout.setVisibility(View.GONE);
                    homeRVContainer.setVisibility(View.VISIBLE);
                } else {
                    homeRVContainer.setVisibility(View.GONE);
                }
                if (homeAdapter.getItemCount() == 0 && deviceAdapter.getItemCount() == 0) {
                    backLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


    private void showFABMenu(){
        isFABOpen=true;
        fab_main.animate().translationY(-getResources().getDimension(R.dimen.standard_0));
        fab_device.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        fab_home.animate().translationY(-getResources().getDimension(R.dimen.standard_150));
//        fab_url.animate().translationY(-getResources().getDimension(R.dimen.standard_225));
        fab_device.setVisibility(View.VISIBLE);
        fab_home.setVisibility(View.VISIBLE);
//        fab_url.setVisibility(View.VISIBLE);
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab_main.animate().translationY(0);
        fab_device.animate().translationY(0);
        fab_home.animate().translationY(0);
//        fab_url.animate().translationY(0);
        fab_device.setVisibility(View.INVISIBLE);
        fab_home.setVisibility(View.INVISIBLE);
//        fab_url.setVisibility(View.INVISIBLE);
    }

    private void openScanDeviceActivity(){
        Intent intent = new Intent(this, ScanDeviceActivity.class);
        startActivity(intent);
    }

    private void openScanHomeActivity(){
        Intent intent = new Intent(this, ScanHomeActivity.class);
        startActivity(intent);
    }

    private void refreshList(){
        deviceAdapter.notifyDataSetChanged();
    }

}
