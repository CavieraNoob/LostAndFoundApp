package com.example.lostandfoundapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    RadioGroup radioGroupType;
    RadioButton radioLost, radioFound;
    EditText editName, editPhone, editDescription, editDate, editLocation;
    Spinner spinnerCategory;
    Button btnSelectImage, btnSave, btnGetCurrentLocation;
    ImageView imagePreview;

    DatabaseHelper databaseHelper;
    Uri selectedImageUri;
    String savedImagePath;

    private FusedLocationProviderClient fusedLocationClient;

    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;

    private static final int IMAGE_PICK_CODE = 100;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 200;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 300;

    String[] categories = {"Electronics", "Pets", "Wallets", "Keys", "Bags", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        databaseHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }

        radioGroupType = findViewById(R.id.radioGroupType);
        radioLost = findViewById(R.id.radioLost);
        radioFound = findViewById(R.id.radioFound);

        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editDescription = findViewById(R.id.editDescription);
        editDate = findViewById(R.id.editDate);
        editLocation = findViewById(R.id.editLocation);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        imagePreview = findViewById(R.id.imagePreview);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        spinnerCategory.setAdapter(adapter);

        btnSelectImage.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> saveAdvert());

        editLocation.setFocusable(false);
        editLocation.setClickable(true);

        editLocation.setOnClickListener(v -> openPlaceAutocomplete());

        editLocation.setOnLongClickListener(v -> {
            editLocation.setText("");
            selectedLatitude = 0.0;
            selectedLongitude = 0.0;
            Toast.makeText(this, "Location cleared", Toast.LENGTH_SHORT).show();
            return true;
        });

        btnGetCurrentLocation.setOnClickListener(v -> getCurrentLocation());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void openPlaceAutocomplete() {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        );

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fields
        ).build(this);

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
            return;
        }

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {
            if (location != null) {
                selectedLatitude = location.getLatitude();
                selectedLongitude = location.getLongitude();

                String addressText = getAddressFromLatLng(selectedLatitude, selectedLongitude);

                if (addressText != null && !addressText.isEmpty()) {
                    editLocation.setText(addressText);
                } else {
                    editLocation.setText(selectedLatitude + ", " + selectedLongitude);
                }

                Toast.makeText(this, "Current location selected", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Unable to get current location. Please set emulator location and try again.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Location error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
            savedImagePath = copyImageToInternalStorage(selectedImageUri);
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                String address = place.getAddress();
                LatLng latLng = place.getLatLng();

                if (address != null) {
                    editLocation.setText(address);
                } else {
                    editLocation.setText(place.getName());
                }

                if (latLng != null) {
                    selectedLatitude = latLng.latitude;
                    selectedLongitude = latLng.longitude;
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR && data != null) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Location error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String copyImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            File imageFile = new File(
                    getFilesDir(),
                    "image_" + System.currentTimeMillis() + ".jpg"
            );

            FileOutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return imageFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveAdvert() {
        int selectedTypeId = radioGroupType.getCheckedRadioButtonId();

        if (selectedTypeId == -1) {
            Toast.makeText(this, "Please select Lost or Found", Toast.LENGTH_SHORT).show();
            return;
        }

        String type;

        if (selectedTypeId == R.id.radioLost) {
            type = "Lost";
        } else {
            type = "Found";
        }

        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty()
                || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLatitude == 0.0 && selectedLongitude == 0.0) {
            Toast.makeText(this, "Please select a location or use current location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
        ).format(new Date());

        Advert advert = new Advert(
                0,
                type,
                name,
                phone,
                description,
                date,
                location,
                category,
                savedImagePath,
                timestamp,
                selectedLatitude,
                selectedLongitude
        );

        boolean inserted = databaseHelper.insertAdvert(advert);

        if (inserted) {
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}