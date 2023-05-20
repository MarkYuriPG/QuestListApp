package com.example.questlistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;

public class CalendarView extends AppCompatActivity {

    android.widget.CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.caramel)));


        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new android.widget.CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull android.widget.CalendarView view, int year, int month, int dayOfMonth) {
                // Display the selected date in a toast message
                String message = "Selected date: " + dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(CalendarView.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
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
                Intent home = new Intent(this, MainActivity.class);
                startActivity(home);
                break;
            case R.id.Profile:
                Intent profile = new Intent(this, ProfileView.class);
                startActivity(profile);
                break;
            case R.id.MapItem:
                Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:10.316720, 123.890710"));
                startActivity(map);
                break;

            case R.id.CalendarItem:
                break;

            case R.id.Settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;

            case R.id.Exit:
                finish();
                System.exit(0);
                break;
        }
        return true;
    }
}