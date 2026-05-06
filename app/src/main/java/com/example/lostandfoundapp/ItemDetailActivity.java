package com.example.lostandfoundapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailActivity extends AppCompatActivity {

    ImageView detailImage;
    TextView detailTitle, detailCategory, detailPhone, detailDate,
            detailLocation, detailTimestamp, detailDescription;
    Button btnRemove;

    DatabaseHelper databaseHelper;
    int advertId;
    Advert advert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailCategory = findViewById(R.id.detailCategory);
        detailPhone = findViewById(R.id.detailPhone);
        detailDate = findViewById(R.id.detailDate);
        detailLocation = findViewById(R.id.detailLocation);
        detailTimestamp = findViewById(R.id.detailTimestamp);
        detailDescription = findViewById(R.id.detailDescription);
        btnRemove = findViewById(R.id.btnRemove);

        databaseHelper = new DatabaseHelper(this);

        advertId = getIntent().getIntExtra("advert_id", -1);

        if (advertId == -1) {
            Toast.makeText(this, "Advert not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAdvertDetails();

        btnRemove.setOnClickListener(v -> {
            boolean deleted = databaseHelper.deleteAdvert(advertId);

            if (deleted) {
                Toast.makeText(this, "Advert removed", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to remove advert", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAdvertDetails() {
        advert = databaseHelper.getAdvertById(advertId);

        if (advert == null) {
            Toast.makeText(this, "Advert not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        detailTitle.setText(advert.getType() + ": " + advert.getName());
        detailCategory.setText("Category: " + advert.getCategory());
        detailPhone.setText("Phone: " + advert.getPhone());
        detailDate.setText("Date: " + advert.getDate());
        detailLocation.setText("Location: " + advert.getLocation());
        detailTimestamp.setText("Posted: " + advert.getTimestamp());
        detailDescription.setText("Description:\n" + advert.getDescription());

        try {
            if (advert.getImageUri() != null && !advert.getImageUri().isEmpty()) {
                detailImage.setImageURI(Uri.fromFile(new java.io.File(advert.getImageUri())));
            }
        } catch (Exception e) {
            detailImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}