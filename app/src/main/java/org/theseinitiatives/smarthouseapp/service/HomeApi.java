package org.theseinitiatives.smarthouseapp.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface HomeApi {

    @GET("/switch/all/on")
    Call<ResponseBody> setSwitchAllOn();

    @GET("/switch/all/off")
    Call<ResponseBody> setSwitchAllOff();

    @GET
    Call<ResponseBody> setSwitch(@Url String url);

    @GET("/delete/:id")
    Call<ResponseBody> deleteDevice();

    @GET("/wifi/:id")
    Call<ResponseBody> setWifi();

    @GET("/info/:id")
    Call<ResponseBody> getDeviceInfo();

    @GET("/device/getall")
    Call<ResponseBody> getAllDevices();

    @GET("/startup/:id/:state")
    Call<ResponseBody> setStartup();

    @GET("/browser/on")
    Call<ResponseBody> setDnsBrowserOn();

    @GET("/browser/off")
    Call<ResponseBody> setDnsBrowserOff();
}
