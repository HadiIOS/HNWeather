package com.hadinour.hnweather;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.hadinour.hnweather.Service.Autocomplete.Cities;
import com.hadinour.hnweather.Service.Autocomplete.City;
import com.hadinour.hnweather.Service.Autocomplete.LocationsFinderService;
import com.hadinour.hnweather.Service.Weather.ConditionResponse;
import com.hadinour.hnweather.Service.Weather.WeatherInterface;
import com.hadinour.hnweather.Service.PlacePhotos.*;
import com.hadinour.hnweather.Utilities.BaseUtilities;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;

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

    private static TextView tempTV;
    private static TextView lastUpdateTV;

    private android.location.Address address;
    private static List<City> foundCities;
    private ListViewFragment citiesList = new ListViewFragment();
    private static ConditionResponse conditionResponse;
    private static final @IdRes int CONTENT_VIEW_ID = 10101010;
    final String weatherTag = "Weather Tag";

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
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
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

        } else {
            lastLocation = new Location(""); //set Sarajevo as default location.
            lastLocation.setLatitude(43.84864d);
            lastLocation.setLongitude(18.35644);
        }

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<android.location.Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0)
            address = addresses.get(0);
            Log.d("Debug Location", addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());

        getLastLocationWeatherCondition();
    }

    private void getLastLocationWeatherCondition() {
        String loc = "" + lastLocation.getLatitude() + "," + lastLocation.getLongitude();
        weatherInterface.condition(loc).enqueue(new Callback<ConditionResponse>() {
            @Override
            public void onResponse(Call<ConditionResponse> call, Response<ConditionResponse> response) {
                conditionResponse = response.body();
                initElements();
            }

            @Override
            public void onFailure(Call<ConditionResponse> call, Throwable t) {
                 Log.d(weatherTag, t.toString());
            }

        });
    }

    private void initElements() {
        placePhotosTask();
        tempTV = (TextView) findViewById(R.id.temp_textView);
        lastUpdateTV = (TextView) findViewById(R.id.lastUpdate_title);
        tempTV.setText(conditionResponse.getCurrent_observation().getTemp_c() + "˚");
        lastUpdateTV.setText(conditionResponse.getCurrent_observation().getObservation_time() + "\n" + getLocationName());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lastUpdateTV.setText(getLocationName());
            }
        }, 2000);

    }


    private void placePhotosTask() {

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        final Point size = BaseUtilities.getUtilities().getDeviceSize(this);
        final View root = findViewById(android.R.id.content);
        final Activity that = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                new PhotoTask(size.x * 2, size.y * 2, googleApiClient, that) {
                    @Override
                    protected void onPreExecute() {
                        // Display a temporary image to show while bitmap is loading.
                    }

                    @Override
                    protected void onPostExecute(AttributedPhoto attributedPhoto) {
                        if (attributedPhoto != null) {
                            // Photo has been loaded, display it.
//                    mImageView.setImageBitmap(attributedPhoto.bitmap);
                            Drawable d = new BitmapDrawable(getResources(), attributedPhoto.bitmap);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                root.setBackground(d);

                            } else  {
                                root.setBackgroundDrawable(d);
                            }
                        }
                    }
                }.execute(getPlaceID());

            }
        }).start();
    }

    private List<String> getCities(List<City> cities) {
        List<String> citiesList = new ArrayList<String>();
        for (City city: cities) {
            citiesList.add(city.getName());
        }
        return citiesList;
    }

    private String getLocationName() {
        String subCountry = address.getLocality() + ", ";
        if (subCountry.length() < 3)
            subCountry = address.getFeatureName() + ", ";
        if (subCountry.length() < 3)
            subCountry = "";
        return  subCountry + address.getCountryName();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("suspended: " , String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("WTF: ", connectionResult.toString());
    }
    
    private String getPlaceID() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "ChIJ0Ztx7bHLWEcRPrOH3qbNLlY";

        }

        final String[] placeId = {"ChIJ0Ztx7bHLWEcRPrOH3qbNLlY"};


        PendingResult<PlaceLikelihoodBuffer> pResult = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);
        PlaceLikelihoodBuffer placeLikelihoods = pResult.await();
        for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
            Log.i("Place", String.format("Place '%s' has likelihood: %g",
                    placeLikelihood.getPlace().getName(),
                    placeLikelihood.getLikelihood()));
            placeId[0] = placeLikelihood.getPlace().getId();
        }
        placeLikelihoods.release();
        return placeId[0];
    }
}