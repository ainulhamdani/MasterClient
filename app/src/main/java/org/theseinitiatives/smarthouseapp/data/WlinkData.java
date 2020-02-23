package org.theseinitiatives.smarthouseapp.data;

import org.json.JSONObject;

import java.util.HashMap;

public class WlinkData {
    private String deviceid;
    private HashMap<String,String> data;

    public WlinkData(String deviceid, HashMap<String,String> data) {
        this.deviceid = deviceid;
        this.data = data;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public HashMap<String,String> getData() {
        return data;
    }
}
