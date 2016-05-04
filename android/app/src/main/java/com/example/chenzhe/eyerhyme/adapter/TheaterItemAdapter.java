package com.example.chenzhe.eyerhyme.adapter;

import com.example.chenzhe.eyerhyme.activity.TheaterListActivity;
import com.example.chenzhe.eyerhyme.model.TheaterItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jay on 2016/5/3.
 */

public class TheaterItemAdapter extends ArrayAdapter<TheaterItem> {
    private int resourceId;
    public TheaterItemAdapter(Context context, int textViewResourceId, List<TheaterItem> objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        TheaterItem theaterItem = getItem(position);
        RelativeLayout newView;
        if(convertView == null){
            newView = new RelativeLayout(getContext());
            LayoutInflater inflater = (LayoutInflater)getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(resourceId, newView, true);
        }
        else{
            newView = (RelativeLayout)convertView;
        }
        TextView name = (TextView)newView.findViewById(R.id.theater_name);
        TextView address = (TextView)newView.findViewById(R.id.theater_address);
        TextView lowestPrice = (TextView)newView.findViewById(R.id.theater_lowest_price);
        TextView score = (TextView)newView.findViewById(R.id.theater_score);
        TextView distance = (TextView)newView.findViewById(R.id.theater_distance);
        name.setText(theaterItem.getName());
        address.setText(theaterItem.getLocation());
        lowestPrice.setText("¥"+theaterItem.getLowest_price()+"起");
        score.setText(""+theaterItem.getGrade());
        int t_distance = TheaterListActivity.getDistance(TheaterListActivity.longitude,
                TheaterListActivity.latitude, theaterItem.getLongitude(), theaterItem.getLatitude());
        String distance_str;
        if (t_distance < 1000)
            distance_str = t_distance+"m";
        else
            distance_str = (int)Math.floor(1.0*t_distance / 1000) + "km";
        distance.setText(distance_str);
        return newView;
    }
}