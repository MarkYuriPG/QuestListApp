package com.example.questlistapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {

    private EditText nameEditText;
    private EditText ageEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private Button editprofile, cancel;
    private ImageView image;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int IMAGE_REQUEST_CODE = 3;
    private Uri imgUri;
    private ProfileDatabaseHelper databaseHelper;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.caramel)));

        cancel = findViewById(R.id.CancelButton);
        editprofile = findViewById(R.id.editprofile);
        nameEditText = findViewById(R.id.editname);
        ageEditText = findViewById(R.id.editage);
        phoneEditText = findViewById(R.id.editphone);
        emailEditText = findViewById(R.id.editemail);
        image = findViewById(R.id.imageAvatarView);
        Button saveButton = findViewById(R.id.saveButton);

        if (imgUri != null) {
            image.setImageURI(imgUri);
        } else {
            // Set the default image if no image URI is available
            image.setImageResource(R.drawable.baseline_person_24);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String age = ageEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String email = emailEditText.getText().toString();
                // Create an intent to start the ProfileView activity

                Intent intent = new Intent(EditProfile.this, ProfileView.class);

                // Add the data as extras to the intent
//                nameEditText.getText();
//                saveProfile();

                intent.putExtra("name", name);
                intent.putExtra("age", age);
                intent.putExtra("phone", phone);
                intent.putExtra("email", email);

                if (imgUri != null) {
                    intent.putExtra("imageUri", imgUri.toString());
                }

                setResult(RESULT_OK, intent);
                //startActivity(intent);

                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                /*PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.image_source_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.cameraOption:
                                // Handle the camera option
                                takePictureFromCamera();
                                return true;
                            case R.id.galleryOption:
                                // Handle the gallery option
                                choosePictureFromGallery();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();*/
            }
        });
           // databaseHelper = new ProfileDatabaseHelper(this);
    }
    public void choosePictureFromGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoIntent.setType("image/*");
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    public void takePictureFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void saveProfile() {
        String name = nameEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();

        // Get a writable database
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Create a new ContentValues object and put the profile information into it
        ContentValues values = new ContentValues();
        values.put(ProfileDatabaseHelper.COLUMN_NAME, name);
        values.put(ProfileDatabaseHelper.COLUMN_AGE, age);
        values.put(ProfileDatabaseHelper.COLUMN_PHONE, phone);
        values.put(ProfileDatabaseHelper.COLUMN_EMAIL, email);

        // Update the profile information in the database
        db.update(ProfileDatabaseHelper.TABLE_PROFILES, values,
                ProfileDatabaseHelper.COLUMN_NAME + "=?", new String[]{name});

        // Close the database connection
        db.close();

        // Finish the activity and return to the profile view
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            // Process the selected image
            image.setImageURI(imageUri);
            imgUri = imageUri;
        }
    }

}
