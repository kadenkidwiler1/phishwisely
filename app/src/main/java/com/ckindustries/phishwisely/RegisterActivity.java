package com.ckindustries.phishwisely;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ckindustries.phishwisely.data.AccountRepository;
import com.ckindustries.phishwisely.security.Passwords;
import com.ckindustries.phishwisely.session.UserSession;
import com.ckindustries.phishwisely.util.DatabaseExecutor;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, keyboard.bottom));
            return insets;
        });
        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> finish());
        EditText email = findViewById(R.id.regEmailInput);
        EditText password = findViewById(R.id.regPasswordInput);
        EditText confirm = findViewById(R.id.regConfirmPasswordInput);
        EditText display = findViewById(R.id.regDisplayNameInput);
        EditText first = findViewById(R.id.regFirstNameInput);
        EditText last = findViewById(R.id.regLastNameInput);
        EditText org = findViewById(R.id.regOrganizationInput);
        Button register = findViewById(R.id.registerButton);
        register.setOnClickListener(v -> {
            String address = email.getText().toString().trim();
            String secret = password.getText().toString();
            String displayName = display.getText().toString().trim();
            String firstName = first.getText().toString().trim();
            String lastName = last.getText().toString().trim();
            String code = org.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(address).matches()) {
                email.setError("Enter a valid email");
                return;
            }
            String policy = Passwords.validate(secret);
            if (policy != null) {
                password.setError(policy);
                return;
            }
            if (!secret.equals(confirm.getText().toString())) {
                confirm.setError("Passwords do not match");
                return;
            }
            if (firstName.isEmpty()) {
                first.setError("First name required");
                return;
            }
            if (lastName.isEmpty()) {
                last.setError("Last name required");
                return;
            }
            if (displayName.isEmpty()) {
                display.setError("Display name required");
                return;
            }
            if (code.isEmpty()) {
                org.setError("Organization code required");
                return;
            }
            register.setEnabled(false);
            DatabaseExecutor.IO.execute(() -> {
                try {
                    AccountRepository.Registration result = new AccountRepository(this)
                            .register(address, secret, displayName, firstName, lastName, code);
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        register.setEnabled(true);
                        if (result.error != null) {
                            if (result.error.toLowerCase().contains("email"))
                                email.setError(result.error);
                            else org.setError(result.error);
                        } else {
                            new UserSession(this).signIn(result.user.id);
                            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (Exception ex) {
                    runOnUiThread(() -> {
                        register.setEnabled(true);
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }
}
