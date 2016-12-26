package com.hadinour.hnweather.Service.Weather;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by hadinour on 12/26/16.
 */

public interface WeatherInterface {

    @GET("conditions/q/{latlong}.json")
    Call<ConditionResponse> condition (
            @Path("latlong") String location
    );

    public static final Retrofit weatherRertorfit = new Retrofit.Builder()
            .baseUrl("http://api.wunderground.com/api/d038a27fa8b1c894/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
