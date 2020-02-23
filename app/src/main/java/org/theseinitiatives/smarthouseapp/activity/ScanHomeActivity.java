package org.theseinitiatives.smarthouseapp.activity;

import android.os.Bundle;

import com.github.druk.rxdnssd.BonjourService;

import org.theseinitiatives.smarthouseapp.R;
import org.theseinitiatives.smarthouseapp.adapter.HomeAdapter;
import org.theseinitiatives.smarthouseapp.helper.RxDnssdHelper;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScanHomeActivity extends AppCompatActivity {

    RxDnssdHelper rxDnssdHelper;
    ArrayList<BonjourService> services = new ArrayList<>();

    private HomeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_home);
        rxDnssdHelper = new RxDnssdHelper(this,"smarthub");

        mAdapter = new HomeAdapter(this, HomeAdapter.SCAN_MODE);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.scan_result);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        startDiscover();
    }


    public void startDiscover() {
        rxDnssdHelper.scanHomeService(mAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        rxDnssdHelper.stopScan();
    }
}
