package org.theseinitiatives.smarthouseapp.helper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.theseinitiatives.smarthouseapp.service.ApiService;
import org.theseinitiatives.smarthouseapp.service.EWlinkApi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IPScannerHelper {

    static ArrayList<String> subnets = new ArrayList<>();

    public static void scan(){
        subnets = new ArrayList<>();

        Enumeration<NetworkInterface> enumNetworkInterfaces = null;
        try {
            enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while(enumNetworkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
            Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();

            while (enumInetAddress.hasMoreElements()) {
                InetAddress inetAddress = enumInetAddress.nextElement();
                if (inetAddress.isSiteLocalAddress()) {
                    subnets.add(getSubnetAddress(inetAddress.getHostAddress()));
                }
            }
        }
        new Thread(new Runnable(){
            @Override
            public void run() {
                // Do network action in this function
                Log.d("TAG", "check:: run: checkHosts");
                for (String subnet : subnets){
                    checkHosts(subnet);
                }

            }
        }).start();
    }

    private String getSubnetAddress(int address)
    {
        String ipString = String.format(
                "%d.%d.%d",
                (address & 0xff),
                (address >> 8 & 0xff),
                (address >> 16 & 0xff));

        return ipString;
    }

    private static String getSubnetAddress(String address)
    {
        String[] ip = address.split("\\.");

        return String.format(
                "%s.%s.%s",
                (ip[0]),
                (ip[1]),
                (ip[2]));
    }

    private static void checkHosts(String subnet)
    {
        try
        {
            int timeout=50;
            for (int i=1;i<255;i++)
            {
                String host=subnet + "." + i;
                Log.d("TAG", "checkhost: "+host);
                if (InetAddress.getByName(host).isReachable(timeout))
                {
                    Log.d("TAG", "checkHosts() :: "+host + " is reachable");
                    final EWlinkApi eWlinkApi = ApiService.getTempClient("http://"+host+":8081").create(EWlinkApi.class);
                    Call<ResponseBody> call = eWlinkApi.getInfo(WlinkHelper.Info(""));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            JSONObject resJson = null;
                            try {
                                resJson = new JSONObject(response.body().string());
                                Log.d("TAG", "check:: onResponse: resJson = "+resJson);
                                if (resJson.getInt("error")==0){
                                    JSONObject data = new JSONObject(resJson.getString("data"));
                                    if (data.getString("switch").equals(WlinkHelper.Switch.OFF)){
//                                        Call<ResponseBody> call2 = eWlinkApi.setSwitch(WlinkHelper.SwitchOn(""));
//                                        call2.enqueue(new Callback<ResponseBody>() {
//                                            @Override
//                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                                            }
//
//                                            @Override
//                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                                            }
//                                        });
                                    }else if (data.getString("switch").equals(WlinkHelper.Switch.ON)) {
//                                        Call<ResponseBody> call2 = eWlinkApi.setSwitch(WlinkHelper.SwitchOff(""));
//                                        call2.enqueue(new Callback<ResponseBody>() {
//                                            @Override
//                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                                            }
//
//                                            @Override
//                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                                            }
//                                        });
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            }
            Log.d("TAG", "checkHosts: finished");
        }
        catch (UnknownHostException e)
        {
            Log.d("TAG", "checkHosts() :: UnknownHostException e : "+e);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.d("TAG", "checkHosts() :: IOException e : "+e);
            e.printStackTrace();
        }
    }
}
