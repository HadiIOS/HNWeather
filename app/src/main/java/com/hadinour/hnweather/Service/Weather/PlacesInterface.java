package com.hadinour.hnweather.Service.Weather;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlacesInterface {
    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.84864,18.35644&key=AIzaSyBCNiZ4TXYhtcZJ3t5lONTjzuNikwJ2pes

    @GET("json?key=AIzaSyBCNiZ4TXYhtcZJ3t5lONTjzuNikwJ2pes")
    Call<Places> getPlaces (
            @Query("location") String location
    );

    static final Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static final Retrofit placesRetrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
}
