package org.theseinitiatives.smarthouseapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.theseinitiatives.smarthouseapp.R;
import org.theseinitiatives.smarthouseapp.adapter.HomeDeviceAdapter;
import org.theseinitiatives.smarthouseapp.application.App;
import org.theseinitiatives.smarthouseapp.helper.ItemTouchHelperAdapter;
import org.theseinitiatives.smarthouseapp.helper.SimpleItemTouchHelperCallback;
import org.theseinitiatives.smarthouseapp.room.Home;
import org.theseinitiatives.smarthouseapp.room.HomeDevice;
import org.theseinitiatives.smarthouseapp.viewmodel.HomeDeviceViewModel;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeDeviceActivity extends AppCompatActivity {

    public final static String HOME_EXTRA = "home_extra";

    private Home home;

    private ItemTouchHelper mItemTouchHelper;

    private RecyclerView deviceRV;
    private HomeDeviceAdapter deviceAdapter;
    private HomeDeviceViewModel deviceViewModel;
    private View deviceRVContainer;
    private View backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_device);
        home = getIntent().getParcelableExtra(HOME_EXTRA);
        setTitle();

        backLayout = findViewById(R.id.empty_home);

        deviceRVContainer = findViewById(R.id.deviceRV);
        deviceViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(App.getInstance())).get(HomeDeviceViewModel.class);
        deviceViewModel.getListHomeDevices().observe(this, getDevices);

        deviceAdapter = new HomeDeviceAdapter(this, home);
        deviceAdapter.getHomeDeviceRepository().setListHomeDevices(home.getId());
        deviceRV = (RecyclerView) findViewById(R.id.device_list);
        deviceRV.setHasFixedSize(true);
        deviceRV.setAdapter(deviceAdapter);
        deviceRV.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) deviceAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(deviceRV);
    }

    private void setTitle(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(home.getName());
        }
    }

    private final Observer<List<HomeDevice>> getDevices = new Observer<List<HomeDevice>>() {
        @Override
        public void onChanged(List<HomeDevice> devices) {
            if (devices != null) {
                deviceAdapter.setData(devices);
                if (deviceAdapter.getItemCount() > 0) {
                    backLayout.setVisibility(View.GONE);
                    deviceRVContainer.setVisibility(View.VISIBLE);
                } else {
                    backLayout.setVisibility(View.VISIBLE);
                    deviceRVContainer.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        deviceAdapter.saveData();
    }
}
