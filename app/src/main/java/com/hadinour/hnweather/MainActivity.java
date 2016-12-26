package com.hadinour.hnweather;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.hadinour.hnweather.Service.Autocomplete.Cities;
import com.hadinour.hnweather.Service.Autocomplete.City;
import com.hadinour.hnweather.Service.Autocomplete.LocationsFinderService;
import com.hadinour.hnweather.Service.Weather.ConditionResponse;
import com.hadinour.hnweather.Service.Weather.WeatherInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static GoogleApiClient googleApiClient;
    private static Location lastLocation;
    private static LocationsFinderService locationsFinderService = LocationsFinderService.retrofit.create(LocationsFinderService.class);
    private static WeatherInterface weatherInterface = WeatherInterface.weatherRertorfit.create(WeatherInterface.class);

    private List<City> foundCities;
    private ListViewFragment citiesList = new ListViewFragment();
    private static final @IdRes int CONTENT_VIEW_ID = 10101010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View content = getLayoutInflater().inflate(R.layout.activity_main, null);
        content.setId(CONTENT_VIEW_ID);

        setContentView(content);
        createGAC();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                query = query.trim();
                searchFor(query);
                searchView.setQuery(query, false);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() == false) {
                    Log.d("WeatherFinder", newText);
                    locationsFinderService.cities(newText).enqueue(new Callback<Cities>() {
                        @Override
                        public void onResponse(Call<Cities> call, Response<Cities> response) {
                            foundCities = response.body().getRESULTS();
                            ArrayList<String> cities = (ArrayList<String>) getCities(foundCities);
                            getFragmentManager().executePendingTransactions();
                            if (citiesList.isAdded() == false) {
                                Bundle bundle = new Bundle();
                                bundle.putStringArrayList("foundCities", cities);
                                citiesList.setArguments(bundle);
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.add(CONTENT_VIEW_ID, citiesList);
                                fragmentTransaction.commit();
                            } else if (citiesList.isAdded()){
                                citiesList.updateAdapter(cities);
                            }
                        }

                        @Override
                        public void onFailure(Call<Cities> call, Throwable t) {
                            Log.d("WeatherFinder", t.toString());
                        }
                    });
                } else {
                    if (citiesList.isAdded()){
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.remove(citiesList).commit();
                    }
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void searchFor(String q) {
        Log.d("DEBUG", "search Entered: " + q);
    }

    private void createGAC() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }


    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    15);
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {

            String lat = String.valueOf(lastLocation.getLatitude());
            String lng =String.valueOf(lastLocation.getLongitude());
            Log.d("Debug Location", "lat: " + lat + ", long: " + lng);
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<android.location.Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0)
                Log.d("Debug Location", addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());

        } else {
            lastLocation = new Location(""); //set Sarajevo as default location.
            lastLocation.setLatitude(43.84864d);
            lastLocation.setLongitude(18.35644);
        }
        getLastLocationWeatherCondition();
    }

    private void getLastLocationWeatherCondition() {
        String loc = "" + lastLocation.getLatitude() + "," + lastLocation.getLongitude();
        weatherInterface.condition(loc).enqueue(new Callback<ConditionResponse>() {
            @Override
            public void onResponse(Call<ConditionResponse> call, Response<ConditionResponse> response) {
                ConditionResponse conditionResponse = response.body();

            }

            @Override
            public void onFailure(Call<ConditionResponse> call, Throwable t) {
                Log.d("WeatherTag", t.toString());
            }

        });
    }

    private List<String> getCities(List<City> cities) {
        List<String> citiesList = new ArrayList<String>();
        for (City city: cities) {
            citiesList.add(city.getName());
        }
        return citiesList;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("suspended: " , String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("WTF: ", connectionResult.toString());
    }

    public static int getContentViewId() {
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH ? android.R.id.content : R.id.action_bar_activity_content;
    }

}