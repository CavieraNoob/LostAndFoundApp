package com.example.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShowItemsActivity extends AppCompatActivity {

    Spinner spinnerFilter;
    ListView listViewItems;

    DatabaseHelper databaseHelper;
    ArrayList<Advert> advertList;
    AdvertAdapter advertAdapter;

    String[] filterCategories = {"All", "Electronics", "Pets", "Wallets", "Keys", "Bags", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        spinnerFilter = findViewById(R.id.spinnerFilter);
        listViewItems = findViewById(R.id.listViewItems);

        databaseHelper = new DatabaseHelper(this);

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                filterCategories
        );
        spinnerFilter.setAdapter(filterAdapter);

        loadAllItems();

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedCategory = filterCategories[position];

                if (selectedCategory.equals("All")) {
                    loadAllItems();
                } else {
                    loadItemsByCategory(selectedCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        listViewItems.setOnItemClickListener((parent, view, position, id) -> {
            Advert selectedAdvert = advertList.get(position);

            Intent intent = new Intent(ShowItemsActivity.this, ItemDetailActivity.class);
            intent.putExtra("advert_id", selectedAdvert.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (spinnerFilter != null && spinnerFilter.getSelectedItem() != null) {
            String selectedCategory = spinnerFilter.getSelectedItem().toString();

            if (selectedCategory.equals("All")) {
                loadAllItems();
            } else {
                loadItemsByCategory(selectedCategory);
            }
        }
    }

    private void loadAllItems() {
        advertList = databaseHelper.getAllAdverts();
        advertAdapter = new AdvertAdapter(this, advertList);
        listViewItems.setAdapter(advertAdapter);
    }

    private void loadItemsByCategory(String category) {
        advertList = databaseHelper.getAdvertsByCategory(category);
        advertAdapter = new AdvertAdapter(this, advertList);
        listViewItems.setAdapter(advertAdapter);
    }
}