package com.example.questlistapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    private EditText nameEditText;
    private EditText ageEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private Button editprofile;

    private ProfileDatabaseHelper databaseHelper;
    private int IMAGE_REQUEST_CODE;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        editprofile = findViewById(R.id.editprofile);
        nameEditText = findViewById(R.id.editname);
        ageEditText = findViewById(R.id.editage);
        phoneEditText = findViewById(R.id.editphone);
        emailEditText = findViewById(R.id.editemail);

        Button saveButton = findViewById(R.id.saveButton);
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
                startActivity(intent);

                finish();
            }
        });
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });

        databaseHelper = new ProfileDatabaseHelper(this);
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
        }
    }
}
