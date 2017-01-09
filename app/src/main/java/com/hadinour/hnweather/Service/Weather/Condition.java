package com.hadinour.hnweather.Service.Weather;

/**
 * Created by hadinour on 12/26/16.
 */

public class Condition {
    String observation_time;
    String weather;
    String temperature_string;
    float temp_f;
    float temp_c;
    String relative_humidity;//humidity
    String wind_string;
    String windchill_string;
    String windchill_f;
    String windchill_c;//windchill
    String feelslike_string;
    String feelslike_f;
    String feelslike_c;//weather
    String visibility_mi; //visibility
    String visibility_km;
    String icon;
    String icon_url; //condition

    public String getObservation_time() {
        return observation_time;
    }

    public String getWeather() {
        return weather;
    }

    public String getTemperature_string() {
        return temperature_string;
    }

    public float getTemp_f() {
        return temp_f;
    }

    public float getTemp_c() {
        return temp_c;
    }

    public String getRelative_humidity() {
        return relative_humidity;
    }

    public String getWind_string() {
        return wind_string;
    }

    public String getWindchill_string() {
        return windchill_string;
    }

    public String getWindchill_f() {
        return windchill_f;
    }

    public String getWindchill_c() {
        return windchill_c;
    }

    public String getFeelslike_string() {
        return feelslike_string;
    }

    public String getFeelslike_f() {
        return feelslike_f;
    }

    public String getFeelslike_c() {
        return feelslike_c;
    }

    public String getVisibility_mi() {
        return visibility_mi;
    }

    public String getVisibility_km() {
        return visibility_km;
    }

    public String getIcon() {
        return icon;
    }

    public String getIcon_url() {
        return icon_url;
    }
}
