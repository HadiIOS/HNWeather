package com.hadinour.hnweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.hadinour.hnweather.Service.Autocomplete.Cities;
import com.hadinour.hnweather.Service.Autocomplete.City;
import com.hadinour.hnweather.Service.Autocomplete.LocationsFinderService;
import com.hadinour.hnweather.Service.Weather.Condition;
import com.hadinour.hnweather.Service.Weather.ConditionResponse;
import com.hadinour.hnweather.Service.Weather.PlacesInterface;
import com.hadinour.hnweather.Service.Weather.WeatherInterface;
import com.hadinour.hnweather.Service.PlacePhotos.*;
import com.hadinour.hnweather.Utilities.BaseUtilities;
import com.hadinour.hnweather.Utilities.OnSwipeTouchListner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private static PlacesInterface placesInterface = PlacesInterface.placesRetrofit.create(PlacesInterface.class);
    private static TextView tempTV;
    private static TextView lastUpdateTV;
    public static SearchView searchView;
    private android.location.Address address;
    private static List<City> foundCities;
    private ListViewFragment listViewFragment;
    private static ConditionResponse conditionResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listViewFragment = (ListViewFragment) getFragmentManager().findFragmentById(R.id.listViewFragment);
        findViewById(R.id.activity_main).setOnTouchListener(new OnSwipeTouchListner(MainActivity.this){
            public void onSwipeTop() {
                //show something to add
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                //refresh data
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });
        createGAC();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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

            public void setFragment(List<City> cities){
                listViewFragment.updateAdapter(cities);
                if (listViewFragment.getView().getHeight() > 0) {
                    BaseUtilities.getUtilities().setBackgroundOfView(listViewFragment.getView(), ContextCompat.getDrawable(MainActivity.this, R.color.White), MainActivity.this);

                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() == false) {
                    Log.d("WeatherFinder", newText);
                    locationsFinderService.cities(newText).enqueue(new Callback<Cities>() {
                        @Override
                        public void onResponse(Call<Cities> call, Response<Cities> response) {
                            foundCities = response.body().getRESULTS();
                            setFragment(foundCities);
                        }

                        @Override
                        public void onFailure(Call<Cities> call, Throwable t) {
                            Log.d("WeatherFinder", t.toString());
                        }
                    });
                } else {
                    setFragment(new ArrayList<City>());
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

        preGetWeatherLocation(lastLocation);
    }

    public void preGetWeatherLocation(Location location) {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<android.location.Address> addresses = getAdresses(0, gcd, location);
        if (addresses.size() > 0)
            address = addresses.get(0);
        Log.d("Debug Location", addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());

        getWeatherForLocation(location);
    }

    public void getWeatherForLocation(Location location) {
        final String loc = "" + location.getLatitude() + "," + location.getLongitude();
        weatherInterface.condition(loc).enqueue(new Callback<ConditionResponse>() {
            @Override
            public void onResponse(Call<ConditionResponse> call, Response<ConditionResponse> response) {
                conditionResponse = response.body();
                initElements(loc);
            }

            @Override
            public void onFailure(Call<ConditionResponse> call, Throwable t) {
            }

        });
    }

    private void initElements(String loc) {
        placePhotosTask(loc);
        tempTV = (TextView) findViewById(R.id.temp_textView);
        lastUpdateTV = (TextView) findViewById(R.id.lastUpdate_title);
        tempTV.setText(conditionResponse.getCurrent_observation().getFeelslike_c() + "˚");
        lastUpdateTV.setText(conditionResponse.getCurrent_observation().getObservation_time() + "\n" + getLocationName());
        mobi.parchment.widget.adapterview.listview.ListView extraListView = (mobi.parchment.widget.adapterview.listview.ListView) findViewById(R.id.extraList);
        if (extraListView.getAdapter() == null) {
            ExtraInfoAdapter extraInfoAdapter = new ExtraInfoAdapter(MainActivity.this, R.id.extraList, new ArrayList<>(Collections.nCopies(5, conditionResponse.getCurrent_observation())));
            extraListView.setAdapter(extraInfoAdapter);
        } else {
            ((ExtraInfoAdapter)extraListView.getAdapter()).refreshEvents(new ArrayList<>(Collections.nCopies(5, conditionResponse.getCurrent_observation())));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lastUpdateTV.setText(getLocationName());
            }
        }, 60000);

    }


    private void placePhotosTask(final String location) {

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        final Point size = BaseUtilities.getUtilities().getDeviceSize(this);
        final View root = findViewById(android.R.id.content);


        new Thread(new Runnable() {
            @Override
            public void run() {
                String placeID = "ChIJ0Ztx7bHLWEcRPrOH3qbNLlY";
                try {
                    placeID = getPlaceID(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new PhotoTask(size.x * 2, size.y * 2, googleApiClient, MainActivity.this) {
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
                            BaseUtilities.getUtilities().setBackgroundOfView(root, d, getBaseContext());

                        }
                    }
                }.execute(placeID);

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
        if (subCountry.equals("null, "))
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
    
    private String getPlaceID(String location) throws IOException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "ChIJ0Ztx7bHLWEcRPrOH3qbNLlY";

        }

        String placeId = "ChIJ0Ztx7bHLWEcRPrOH3qbNLlY";
        try {
            com.hadinour.hnweather.Service.Weather.Places places = placesInterface.getPlaces(location).execute().body();
            if (places.results.size() > 0)
                placeId = places.results.get(0).getPlace_id();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return placeId;
    }

    List<android.location.Address> getAdresses(int counter, Geocoder gcd, Location location) {
        //Geocoder sometimes returns null to addresses.
        //so I made a counter with attempts to be more sure :).
        int c = counter;
        if (c == 3) return null;
        List<android.location.Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses == null) {
            c++;
            getAdresses(c, gcd, location);
        }

        return addresses;
    }
}