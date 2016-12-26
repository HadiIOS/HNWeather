package com.hadinour.hnweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hadinour.hnweather.Service.Autocomplete.City;

import java.util.ArrayList;

/**
 * Created by hadinour on 12/18/16.
 */

public class CitiesAdapter extends ArrayAdapter<City> {
    public CitiesAdapter(Context context, ArrayList<City> cities) {
        super(context, 0, cities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        City city = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.basic_layout_view, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.text_list_view);
        // Populate the data into the template view using the data object
        tvName.setText(city.getName());
        // Return the completed view to render on screen
        return convertView;
    }

}