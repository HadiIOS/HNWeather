package com.hadinour.hnweather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hadinour.hnweather.Service.City;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hadinour on 12/18/16.
 */

public class ListViewFragment extends Fragment {
    ArrayAdapter<String> citiesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.basic_layout_view, container, false);
        ArrayList<String> cities = getArguments().getStringArrayList("foundCities");
        citiesAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, cities);
        ListView listView = (ListView) rootView.findViewById(R.id.cityList);
        listView.setAdapter(citiesAdapter);
        return rootView;
    }


    void updateAdapter(List<String> cities) {
        if (citiesAdapter == null)
            citiesAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item);

        if (citiesAdapter.isEmpty() == false)
            citiesAdapter.clear();

        if (cities.size() > 0)
            for (String name : cities)
                citiesAdapter.add(name);

        citiesAdapter.notifyDataSetChanged();
    }
}

