package com.example.questlistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.widget.Switch;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private Button changePasswordButton;

    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.caramel)));

        changePasswordButton = (Button) findViewById(R.id.ChangePasswordButton);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changePasswordIntent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(changePasswordIntent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.Home:
                Intent home = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(home);
                finish();
                break;
            case R.id.Profile:
                Intent profile = new Intent(this, ProfileView.class);
                startActivity(profile);
                finish();
                break;
            case R.id.MapItem:
                Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:10.316720, 123.890710"));
                startActivity(map);
                finish();
                break;

            case R.id.CalendarItem:
                Intent calendar = new Intent(this, CalendarView.class);
                startActivity(calendar);
                finish();
                break;

            case R.id.Settings:
                break;

            case R.id.Exit:
                finish();
                System.exit(0);
                break;
        }
        return true;
    }
}