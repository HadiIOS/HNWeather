package com.hadinour.hnweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hadinour.hnweather.Service.Autocomplete.City;
import com.hadinour.hnweather.Utilities.BaseUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by hadinour on 12/18/16.
 */

public class CitiesAdapter extends ArrayAdapter<City> {
    public CitiesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CitiesAdapter(Context context, int resource, List<City> items) {
        super(context, resource, items);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        City city = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null || convertView.getHeight() < 1) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        if (!BaseUtilities.getUtilities().validate(city.getLat(), city.getLon())) return new View(getContext());
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvCitiesListITem);
        // Populate the data into the template view using the data object
        tvName.setText(city.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}