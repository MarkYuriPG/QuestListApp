package com.example.questlistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class MPinActivity extends AppCompatActivity {

    private EditText mpinEditText;

    private Password password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpin);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.caramel)));


        mpinEditText = findViewById(R.id.mpinEditText);
        Button submitButton = findViewById(R.id.EnterButton);

        mpinEditText = findViewById(R.id.mpinEditText);
        Button generateButton = findViewById(R.id.generatepassword);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredMpin = mpinEditText.getText().toString();
                if (validateMpin(enteredMpin)) {
                    // Proceed to the next screen or feature
                    Intent mainAct = new Intent(MPinActivity.this, MainActivity.class);
                    startActivity(mainAct);
                } else {
                    Toast.makeText(MPinActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String generatedPassword = generatePassword();
                mpinEditText.setText(generatedPassword);
                Toast.makeText(MPinActivity.this, "Password : "+ generatedPassword , Toast.LENGTH_SHORT).show();
            }
        });
//        generateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String generatedPassword = generatePassword();
//                mpinEditText.setText(generatedPassword);
//                Toast.makeText(MPinActivity.this, "Password : "+ generatedPassword , Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public boolean validateMpin(String mpin) {
        // Implement your validation logic here, such as checking against a stored value or verifying with an API
        return mpin.equals("user1234");
    }
    public String generatePassword() {
        // Define the characters that can be used in the password
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

        // Set the desired length of the password
        int length = 8;

        // Create a StringBuilder to store the generated password
        StringBuilder password = new StringBuilder();

        // Generate the password by randomly selecting characters from the defined set
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            char randomChar = characters.charAt(randomIndex);
            password.append(randomChar);
        }

        // Return the generated password
        return password.toString();
    }


}