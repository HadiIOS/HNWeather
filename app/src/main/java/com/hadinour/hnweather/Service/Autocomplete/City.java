package com.hadinour.hnweather.Service.Autocomplete;

import android.location.Location;

import java.util.List;

/**
 * Created by hadinour on 12/17/16.
 */

public class City {
    String name;
    double lat;
    double lon;

    @Override
    public String toString() {
        return name + "at " + lat + ", " + lon;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
    public Location getLocation() {
        Location location = new Location(name);
        location.setLatitude(lat);
        location.setLongitude(lon);
        return location;
    }
}

