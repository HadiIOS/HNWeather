package com.hadinour.hnweather;

import com.google.android.gms.vision.text.Text;
import com.hadinour.hnweather.Service.Autocomplete.City;
import com.hadinour.hnweather.Service.Weather.Condition;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hadinour on 1/8/17.
 */

public class ExtraInfoAdapter extends ArrayAdapter<Condition>{
    public ExtraInfoAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ExtraInfoAdapter(Context context, int resource, List<Condition> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Condition condition = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.extra_list_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.extraItemTextView);
        ImageView ivCondition = (ImageView) convertView.findViewById(R.id.extraItemImageView);
        populateData(condition, position, ivCondition, tvName);
        // Populate the data into the template view using the data object

        // Return the completed view to render on screen
        return convertView;
    }

    private void populateData(Condition condition, int pos, ImageView imageView, TextView textView) {
        switch (pos) {
            case 1: {
                //condition
                textView.setText(condition.getTemp_c() + "˚");
                imageView.setImageResource(R.drawable.temp);
                break;
            }
            case 2: {
                textView.setText(condition.getVisibility_km() + " Km");
                imageView.setImageResource(R.drawable.visibilty);
                break;
            }
            case 3: {
                String windChill = condition.getWindchill_c();
                if (windChill.equals("NA")) {
                    windChill = "Unknown";
                } else {
                    windChill = windChill + "˚";
                }
                textView.setText(windChill);
                imageView.setImageResource(R.drawable.windchill);
                break;
            }
            case 4: {
                textView.setText(condition.getRelative_humidity());
                imageView.setImageResource(R.drawable.humidity);
                break;
            }
            case 0: {
                textView.setText(condition.getIcon());
                Picasso.with(getContext()).load(condition.getIcon_url()).into(imageView);
//                imageView.setImageResource(R.drawable.temp);
                break;
            }
        }
    }

    public void refreshEvents(List<Condition> conditions) {
        this.clear();
        this.addAll(conditions);
        notifyDataSetChanged();
    }

}
