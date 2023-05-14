package com.example.questlistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MPinActivity extends AppCompatActivity {

    private EditText mpinEditText;

    private Password password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpin);


        mpinEditText = findViewById(R.id.mpinEditText);
        Button submitButton = findViewById(R.id.EnterButton);

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
    }

    private boolean validateMpin(String mpin) {
        // Implement your validation logic here, such as checking against a stored value or verifying with an API
        return mpin.equals("user1234");
    }

}