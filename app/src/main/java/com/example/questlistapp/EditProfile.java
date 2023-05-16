package com.example.questlistapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    private EditText nameEditText;
    private EditText ageEditText;
    private EditText phoneEditText;
    private EditText emailEditText;

    private ProfileDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.editname);
        ageEditText = findViewById(R.id.editage);
        phoneEditText = findViewById(R.id.editphone);
        emailEditText = findViewById(R.id.editemail);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
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

}
