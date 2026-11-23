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
import com.ckindustries.phishwisely.session.UserSession;
import com.ckindustries.phishwisely.util.DatabaseExecutor;

/**
 * Partner's original activity_login.xml is intentionally unchanged.
 */
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, keyboard.bottom));
            return insets;
        });
        TextView registerLink = findViewById(R.id.registerLink);
        registerLink.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        EditText email = findViewById(R.id.emailInput);
        EditText password = findViewById(R.id.passwordInput);
        Button login = findViewById(R.id.loginButton);

        // A restored session is only accepted if the account still exists in Room.
        UserSession session = new UserSession(this);
        if (session.isSignedIn()) {
            long id = session.userId();
            DatabaseExecutor.IO.execute(() -> {
                boolean exists = com.ckindustries.phishwisely.data.AppDatabase.get(this).userDao().findById(id) != null;
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    if (exists) openDashboard();
                    else session.signOut();
                });
            });
        }
        login.setOnClickListener(v -> {
            String address = email.getText().toString().trim();
            String secret = password.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(address).matches()) {
                email.setError("Enter a valid email");
                return;
            }
            if (secret.isEmpty()) {
                password.setError("Password is required");
                return;
            }
            login.setEnabled(false);
            DatabaseExecutor.IO.execute(() -> {
                try {
                    com.ckindustries.phishwisely.data.entity.User user =
                            new AccountRepository(this).signIn(address, secret);
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        login.setEnabled(true);
                        if (user == null) {
                            password.setError("Invalid email or password");
                            return;
                        }
                        new UserSession(this).signIn(user.id);
                        openDashboard();
                    });
                } catch (Exception ex) {
                    runOnUiThread(() -> {
                        login.setEnabled(true);
                        Toast.makeText(this, "Unable to open local database", Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }

    private void openDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
