package com.example.questlistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.questlistapp.Utils.PasswordDb;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button cancelButton, confirmButton,generateButton;
    private PasswordDb passwordDb;
    private EditText currPassword, newPassword, confirmPassword;
    private Switch showPassword;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.caramel)));

        passwordDb = new PasswordDb(this);
        passwordDb.getWritableDatabase();

        cancelButton = (Button) findViewById(R.id.CancelButton);
        confirmButton = (Button) findViewById(R.id.ConfirmButton);
        generateButton = (Button) findViewById(R.id.GeneratePasswordButton);

        currPassword = (EditText) findViewById(R.id.currentPasswordField);
        newPassword = (EditText) findViewById(R.id.newPasswordField);
        confirmPassword = (EditText) findViewById(R.id.confirmPasswordField);

        showPassword = (Switch)findViewById(R.id.showPasswordSwitch);

        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show the password as plain text
                currPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                // Show the password as masked characters
                currPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            // Move the cursor to the end of the password field
            currPassword.setSelection(currPassword.length());
            newPassword.setSelection(newPassword.length());
            confirmPassword.setSelection(confirmPassword.length());
        });

        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        generateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.CancelButton:
                finish();
                break;

            case R.id.ConfirmButton:
                String current = currPassword.getText().toString();
                String password1 = newPassword.getText().toString();
                String password2 = confirmPassword.getText().toString();
                if(validateCurrentPassword(current, passwordDb))
                {
                    validateChangePassword(password1, password2, passwordDb);
                    Intent settings = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                    startActivity(settings);
                    break;
                }else {
                    Toast.makeText(ChangePasswordActivity.this, "Incorrect current password.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.GeneratePasswordButton:
                newPassword.setText(generatePassword());
                Toast.makeText(ChangePasswordActivity.this, "Password generated.", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    public boolean validateCurrentPassword(String password, PasswordDb db)
    {
        if(password.equals(db.getPassword()))
        {
            return true;
        }

        return false;
    }

    public void validateChangePassword(String password1, String password2, PasswordDb db)
    {
        if(password1.equals(password2))
        {
            db.updatePassword(password2);
            Toast.makeText(ChangePasswordActivity.this, "Change password success.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(ChangePasswordActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}