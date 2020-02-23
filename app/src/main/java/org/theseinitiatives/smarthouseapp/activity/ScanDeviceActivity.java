package org.theseinitiatives.smarthouseapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.github.druk.rxdnssd.BonjourService;

import org.theseinitiatives.smarthouseapp.R;
import org.theseinitiatives.smarthouseapp.adapter.DeviceAdapter;
import org.theseinitiatives.smarthouseapp.helper.RxDnssdHelper;

import java.util.ArrayList;

public class ScanDeviceActivity extends AppCompatActivity {

    RxDnssdHelper rxDnssdHelper;
    ArrayList<BonjourService> services = new ArrayList<>();

    private DeviceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_dns);
        rxDnssdHelper = new RxDnssdHelper(this,"ewelink");

        mAdapter = new DeviceAdapter(this, DeviceAdapter.SCAN_MODE);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.scan_result);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        startDiscover();
    }


    public void startDiscover() {
        rxDnssdHelper.scanDeviceService(mAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        rxDnssdHelper.stopScan();
    }
}
