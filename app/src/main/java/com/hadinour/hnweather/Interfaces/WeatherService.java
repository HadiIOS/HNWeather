package com.hadinour.hnweather.Interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hadinour on 10/29/16.
 */

public interface WeatherService {
    @GET("v1/public/yql")
    Call<String> getWeather(@Query("q") String query);

    @GET("?method=flickr.photos.search&api_key=2af3b7fb370d9f358636702332edd743&group_id=1463451%40N25&lat=43.8562983&lon=18.4131&format=json&auth_token=72157675768575156-586a42d660481701&api_sig=d19bc20bcccd6815975990e48eb7b9cd")
    Call<String> getBackgroundImage();
}
