package org.theseinitiatives.smarthouseapp.helper;

import android.content.Context;
import android.util.Log;

import com.github.druk.rxdnssd.RxDnssd;
import com.github.druk.rxdnssd.RxDnssdBindable;

import org.theseinitiatives.smarthouseapp.adapter.DeviceAdapter;
import org.theseinitiatives.smarthouseapp.adapter.HomeAdapter;

import java.net.InetAddress;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxDnssdHelper {
    public static final String TAG = "RxDnssdHelper";
    private Context mContext;
    private Subscription subscription;
    private String serviceType;

    public RxDnssdHelper(Context mContext, String serviceType) {
        this.mContext = mContext;
        this.serviceType = serviceType;
    }

    public void scanDeviceService(DeviceAdapter mAdapter){
        Log.d(TAG, "scanDeviceService: started");
        RxDnssd rxdnssd = new RxDnssdBindable(mContext);
        subscription = rxdnssd.browse(getServiceType(serviceType), "local.")
                .compose(rxdnssd.resolve())
                .compose(rxdnssd.queryRecords())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mAdapter::addDataFromService, throwable -> Log.e("TAG", "error", throwable));
    }

    public void scanHomeService(HomeAdapter mAdapter){
        Log.d(TAG, "scanHomeService: started");
        RxDnssd rxdnssd = new RxDnssdBindable(mContext);
        subscription = rxdnssd.browse(getServiceType(serviceType), "local.")
                .compose(rxdnssd.resolve())
                .compose(rxdnssd.queryRecords())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mAdapter::addDataFromService, throwable -> Log.e("TAG", "error", throwable));
    }

    private String getServiceType(String serviceType){
        String retType = "";
        String[] types = serviceType.split("\\.");
        if (types.length == 1){
            if (types[0].charAt(0)=='_'){
                retType += types[0];
            } else {
                retType += "_".concat(types[0]);
            }
            retType += "._tcp";
        } else {
            if (types[0].charAt(0)=='_'){
                retType += types[0];
            } else {
                retType += "_".concat(types[0]);
            }
            if (types[1].charAt(0)=='_'){
                retType += ".".concat(types[1]);
            } else {
                retType += "._".concat(types[1]);
            }
        }

        return retType;
    }

    public void stopScan(){
        if (subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
