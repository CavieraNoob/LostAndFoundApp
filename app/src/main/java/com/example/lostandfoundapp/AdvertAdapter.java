package com.example.lostandfoundapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdvertAdapter extends ArrayAdapter<Advert> {

    public AdvertAdapter(Context context, ArrayList<Advert> adverts) {
        super(context, 0, adverts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Advert advert = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_advert, parent, false);
        }

        ImageView itemImage = convertView.findViewById(R.id.itemImage);
        TextView itemTitle = convertView.findViewById(R.id.itemTitle);
        TextView itemCategory = convertView.findViewById(R.id.itemCategory);
        TextView itemTimestamp = convertView.findViewById(R.id.itemTimestamp);

        if (advert != null) {
            itemTitle.setText(advert.getType() + ": " + advert.getName());
            itemCategory.setText("Category: " + advert.getCategory());
            itemTimestamp.setText("Posted: " + advert.getTimestamp());

            try {
                if (advert.getImageUri() != null && !advert.getImageUri().isEmpty()) {
                    itemImage.setImageURI(Uri.fromFile(new java.io.File(advert.getImageUri())));
                }
            } catch (Exception e) {
                itemImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        return convertView;
    }
}