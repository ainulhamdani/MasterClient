package org.theseinitiatives.smarthouseapp.helper;

import org.json.JSONException;
import org.json.JSONObject;
import org.theseinitiatives.smarthouseapp.data.WlinkData;

import java.util.HashMap;

public class WlinkHelper {

    public static class Switch {
        public static final String SWITCH = "switch";
        public static final String ON = "on";
        public static final String OFF = "off";
    }

    public static class Startup {
        public static final String STARTUP = "startup";
        public static final String ON = "on";
        public static final String OFF = "off";
        public static final String STAY = "stay";
    }

    public static class Wifi {
        public static final String SSID = "ssid";
        public static final String PASSWORD = "password";
    }

    public static WlinkData SwitchOn(String deviceId){
        HashMap<String,String> data = new HashMap<>();
        data.put(Switch.SWITCH,Switch.ON);
        return new WlinkData(deviceId, data);
    }

    public static WlinkData SwitchOff(String deviceId){
        HashMap<String,String> data = new HashMap<>();
        data.put(Switch.SWITCH,Switch.OFF);
        return new WlinkData(deviceId, data);
    }

    public static WlinkData StartupOn(String deviceId){
        HashMap<String,String> data = new HashMap<>();
        data.put(Startup.STARTUP,Startup.ON);
        return new WlinkData(deviceId, data);
    }

    public static WlinkData StartupOff(String deviceId){
        HashMap<String,String> data = new HashMap<>();
        data.put(Startup.STARTUP,Startup.OFF);
        return new WlinkData(deviceId, data);
    }

    public static WlinkData StartupStay(String deviceId){
        HashMap<String,String> data = new HashMap<>();
        data.put(Startup.STARTUP,Startup.STAY);
        return new WlinkData(deviceId, data);
    }

    public static WlinkData WifiChange(String deviceId, String ssid, String password){
        HashMap<String,String> data = new HashMap<>();
        data.put(Wifi.SSID,ssid);
        data.put(Wifi.PASSWORD,password);
        return new WlinkData(deviceId, data);
    }

    public static WlinkData Info(String deviceId) {
        HashMap<String,String> data = new HashMap<>();
        return new WlinkData(deviceId, data);
    }
}
