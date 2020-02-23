package org.theseinitiatives.smarthouseapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.theseinitiatives.smarthouseapp.R;
import org.theseinitiatives.smarthouseapp.adapter.DeviceAdapter;
import org.theseinitiatives.smarthouseapp.helper.IPScannerHelper;

public class ScanIpActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private DeviceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ip);

        mAdapter = new DeviceAdapter(this, DeviceAdapter.SCAN_MODE);
        recyclerView = (RecyclerView) findViewById(R.id.scan_result);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        startDiscover();
    }

    public void startDiscover() {
        IPScannerHelper.scan();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
