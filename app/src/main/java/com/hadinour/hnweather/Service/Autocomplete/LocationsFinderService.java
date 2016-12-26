package com.hadinour.hnweather.Service.Autocomplete;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hadinour on 12/17/16.
 */

public interface LocationsFinderService {
    @GET("/aq")
    Call<Cities> cities(@Query("query") String cityQuery);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://autocomplete.wunderground.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
