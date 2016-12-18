package com.hadinour.hnweather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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

public class ListViewDialog extends DialogFragment {
    ArrayAdapter<String> citiesAdapter;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.basic_layout_view, null);

        citiesAdapter = new ArrayAdapter<>(getActivity(), R.layout.basic_layout_view);

        ListView listView = (ListView) v.findViewById(R.id.cityList);
        listView.setAdapter(citiesAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getArguments().getInt("title")).setView(v);

        return builder.create();
    }


    void updateAdapter(List<String> cities) {
        if (citiesAdapter == null)
            citiesAdapter = new ArrayAdapter<>(getActivity(), R.layout.basic_layout_view);

        if (citiesAdapter.isEmpty() == false)
            citiesAdapter.clear();

        if (cities.size() > 0)
            for (String name : cities)
                citiesAdapter.add(name);

        cities.notifyAll();
    }




}

