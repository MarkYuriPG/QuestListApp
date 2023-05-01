package com.example.questlistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.Objects;

public class Calendar extends AppCompatActivity {

    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.caramel)));


        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Display the selected date in a toast message
                String message = "Selected date: " + dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(Calendar.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}