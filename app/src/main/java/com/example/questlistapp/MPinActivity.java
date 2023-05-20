package com.example.questlistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.questlistapp.Utils.PasswordDb;

import java.util.Objects;

public class MPinActivity extends AppCompatActivity {

    private EditText mpinEditText;
    private PasswordDb passwordDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpin);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.caramel)));

        passwordDb = new PasswordDb(this);

        passwordDb.getWritableDatabase();

       // password.setPassword(passwordDb.getPassword());

        mpinEditText = findViewById(R.id.mpinEditText);
        Button submitButton = findViewById(R.id.EnterButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredPassword = mpinEditText.getText().toString();
                if (validateMpin(enteredPassword, passwordDb)) {
                    // Proceed to the next screen or feature
                    Intent mainAct = new Intent(MPinActivity.this, MainActivity.class);
                    startActivity(mainAct);
                } else {
                    Toast.makeText(MPinActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validateMpin(String mpin, PasswordDb passwordDb) {
        // Implement your validation logic here, such as checking against a stored value or verifying with an API
        return mpin.equals(passwordDb.getPassword());
    }

}