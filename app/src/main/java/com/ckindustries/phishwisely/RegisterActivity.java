package com.ckindustries.phishwisely;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime()); // ime = on screen keyboard, edge-to-edge prevents screen from shrinking when keyboard is on the screen. this line allows all input fields to be seen when keyboard is open
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, Math.max(systemBars.bottom, ime.bottom)); //bottom padding grows to the height of the keyboard
            return insets;
        });
        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> finish());

        Button registerButton = findViewById(R.id.registerButton);
        EditText emailField = findViewById(R.id.regEmailInput);
        EditText passwordField = findViewById(R.id.regPasswordInput);
        EditText confPasswordField = findViewById(R.id.regConfirmPasswordInput);
        EditText displayNameField = findViewById(R.id.regDisplayNameInput);
        EditText orgInputField = findViewById(R.id.regOrganizationInput);

        registerButton.setOnClickListener(v -> {
            String regUserEmail = emailField.getText().toString().trim();
            String regUserPassword = passwordField.getText().toString();
            String regConfUserPassword = confPasswordField.getText().toString();
            String regUserDisplayName = displayNameField.getText().toString().trim();
            String regOrganizationCode = orgInputField.getText().toString().trim();

            if (regUserEmail.isEmpty()) {
                emailField.setError("Email is required");
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(regUserEmail).matches()) {
                emailField.setError("Please provide an email");
                return;
            }

            if (regUserPassword.isEmpty()) {
                passwordField.setError("Password is required");
                return;
            } else if (regUserPassword.length() < 8) {
                passwordField.setError("Password must be more than 8 characters");
                return;
            } else if (!regUserPassword.matches(".*\\d.*")) {
                passwordField.setError("Password must contain a number (0-9)");
                return;
            }

            if(regConfUserPassword.isEmpty()) {
                confPasswordField.setError("You must confirm your password");
                return;
            } else if (!regUserPassword.equals(regConfUserPassword)){
                confPasswordField.setError("Passwords do not match");
                return;
            }
            if(regUserDisplayName.isEmpty()) {
                displayNameField.setError("Display name is required");
                return;
            }
            if(regOrganizationCode.isEmpty()) {
                orgInputField.setError("Organization code is required");
                return;
            }

        });
    }
}