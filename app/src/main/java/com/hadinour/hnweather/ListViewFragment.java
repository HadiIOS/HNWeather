package com.hadinour.hnweather;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hadinour.hnweather.Service.Autocomplete.Cities;
import com.hadinour.hnweather.Service.Autocomplete.City;
import com.hadinour.hnweather.Utilities.BaseUtilities;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hadinour on 12/18/16.
 */

public class ListViewFragment extends Fragment {
    CitiesAdapter citiesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.basic_layout_view, container, false);

        citiesAdapter = new CitiesAdapter(getActivity(), R.layout.list_item, new ArrayList<City>());
        ListView listView = (ListView) rootView.findViewById(R.id.cityList);
        listView.setAdapter(citiesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = citiesAdapter.getItem(position);
                MainActivity mainActivity = ((MainActivity)getActivity());
                mainActivity.searchView.setQuery("", false);
                mainActivity.searchView.clearFocus();
                mainActivity.preGetWeatherLocation(city.getLocation());
            }
        });
        if (rootView.getHeight() > 0 && rootView.getWidth() > 0)
            BaseUtilities.getUtilities().setBackgroundOfView(rootView, ContextCompat.getDrawable(getActivity(), R.color.White), getActivity());


        return rootView;
    }


    void updateAdapter(List<City> cities) {
        if (null == citiesAdapter)
            citiesAdapter = new CitiesAdapter(getActivity(), R.layout.list_item, new ArrayList<City>());

        if (!citiesAdapter.isEmpty())
            citiesAdapter.clear();

        if (cities.size() > 0)
            for (City city : cities)
                citiesAdapter.add(city);

        citiesAdapter.notifyDataSetChanged();
    }
}



