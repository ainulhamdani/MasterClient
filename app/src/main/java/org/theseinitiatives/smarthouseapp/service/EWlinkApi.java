package org.theseinitiatives.smarthouseapp.service;

import org.theseinitiatives.smarthouseapp.data.WlinkData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EWlinkApi {

    @POST("/zeroconf/switch")
    Call<ResponseBody> setSwitch(
            @Body WlinkData body
    );

    @POST("/zeroconf/startup")
    Call<ResponseBody> setStartup(
            @Body WlinkData body
    );

    @POST("/zeroconf/signal_strength")
    Call<ResponseBody> getSignalStrength(
            @Body WlinkData body
    );

    @POST("/zeroconf/wifi")
    Call<ResponseBody> setWifi(
            @Body WlinkData body
    );

    @POST("/zeroconf/info")
    Call<ResponseBody> getInfo(
            @Body WlinkData body
    );
}
