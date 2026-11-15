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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView registerLink = findViewById(R.id.registerLink);
        registerLink.setOnClickListener(v -> {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                });



            EditText emailField = findViewById(R.id.emailInput);
            EditText passwordField = findViewById(R.id.passwordInput);
            Button loginButton = findViewById(R.id.loginButton);

            loginButton.setOnClickListener(v -> {
                String userEmail = emailField.getText().toString().trim();
                String userPassword = passwordField.getText().toString();


                if (userEmail.isEmpty()) {
                    emailField.setError("Email is required");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    emailField.setError("Please provide an email");
                    return;
                }

                if (userPassword.isEmpty()) {
                    passwordField.setError("Password is required");
                    return;
                }
            });
        }
    }

