package com.safiyaakhtar.gifty.Adapter;

/**
 * Created by SafiyaAkhtar on 11/26/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.safiyaakhtar.gifty.R;
import com.squareup.picasso.Picasso;

public class CustomGridViewHomeActivity extends BaseAdapter {

    private final String[] gridViewString;
    private final String[] gridViewImageId;
    private Context mContext;


    public CustomGridViewHomeActivity(Context context, String[] gridViewString, String[] gridViewImageId) {
        mContext = context;
        this.gridViewImageId = gridViewImageId;
        this.gridViewString = gridViewString;
    }

    @Override
    public int getCount() {
        return gridViewString.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater ad = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            gridViewAndroid = new View(mContext);
            gridViewAndroid = inflater.inflate(R.layout.category_grid_view, null);
        } else {
            gridViewAndroid = (View) convertView;
        }

        TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.txt_category);
        ImageView imageViewAndroid = (ImageView) gridViewAndroid.findViewById(R.id.category_image);
        textViewAndroid.setText(gridViewString[position]);
        // imageViewAndroid.setImageResource(gridViewImageId[i]);
        Picasso.with(mContext).load(gridViewImageId[position]).resize(170, 220).into(imageViewAndroid);
        return gridViewAndroid;
    }
}