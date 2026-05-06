package com.example.lostandfoundapp;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class CreateAdvertActivity extends AppCompatActivity {

    RadioGroup radioGroupType;
    RadioButton radioLost, radioFound;
    EditText editName, editPhone, editDescription, editDate, editLocation;
    Spinner spinnerCategory;
    Button btnSelectImage, btnSave;
    ImageView imagePreview;

    DatabaseHelper databaseHelper;
    Uri selectedImageUri;
    String savedImagePath;

    private static final int IMAGE_PICK_CODE = 100;

    String[] categories = {"Electronics", "Pets", "Wallets", "Keys", "Bags", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        databaseHelper = new DatabaseHelper(this);

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
        imagePreview = findViewById(R.id.imagePreview);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        spinnerCategory.setAdapter(adapter);

        btnSelectImage.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> saveAdvert());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
            savedImagePath = copyImageToInternalStorage(selectedImageUri);
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
                timestamp
        );

        boolean inserted = databaseHelper.insertAdvert(advert);

        if (inserted) {
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
        }
    }
}