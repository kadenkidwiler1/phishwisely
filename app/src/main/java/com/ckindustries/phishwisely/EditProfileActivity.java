package com.ckindustries.phishwisely;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ckindustries.phishwisely.data.AccountRepository;
import com.ckindustries.phishwisely.data.AppDatabase;
import com.ckindustries.phishwisely.data.entity.User;
import com.ckindustries.phishwisely.security.Passwords;
import com.ckindustries.phishwisely.util.DatabaseExecutor;

public class EditProfileActivity extends AuthenticatedActivity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return insets;
        });
        findViewById(R.id.editProfileBackButton).setOnClickListener(v -> finish());
        long id = currentUserId();
        DatabaseExecutor.IO.execute(() -> {
            User user = AppDatabase.get(this).userDao().findById(id);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (user == null) {
                    finish();
                    return;
                }
                ((EditText) findViewById(R.id.editFirstName)).setText(user.firstName);
                ((EditText) findViewById(R.id.editLastName)).setText(user.lastName);
                ((EditText) findViewById(R.id.editDisplayName)).setText(user.displayName);
                ((EditText) findViewById(R.id.editEmail)).setText(user.email);
            });
        });
        Button save = findViewById(R.id.saveProfileButton);
        save.setOnClickListener(v -> {
            EditText f = findViewById(R.id.editFirstName), l = findViewById(R.id.editLastName);
            EditText d = findViewById(R.id.editDisplayName), e = findViewById(R.id.editEmail);
            EditText current = findViewById(R.id.editCurrentPassword);
            EditText newer = findViewById(R.id.editNewPassword), confirm = findViewById(R.id.editConfirmPassword);
            String first = f.getText().toString().trim(), last = l.getText().toString().trim();
            String display = d.getText().toString().trim(), email = e.getText().toString().trim();
            String currentPass = current.getText().toString(), newPass = newer.getText().toString();
            if (first.isEmpty()) {
                f.setError("Required");
                return;
            }
            if (last.isEmpty()) {
                l.setError("Required");
                return;
            }
            if (display.isEmpty()) {
                d.setError("Required");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                e.setError("Invalid email");
                return;
            }
            if (currentPass.isEmpty()) {
                current.setError("Current password required for every change");
                return;
            }
            if (!newPass.isEmpty()) {
                String err = Passwords.validate(newPass);
                if (err != null) {
                    newer.setError(err);
                    return;
                }
                if (!newPass.equals(confirm.getText().toString())) {
                    confirm.setError("Passwords do not match");
                    return;
                }
            }
            save.setEnabled(false);
            DatabaseExecutor.IO.execute(() -> {
                try {
                    AccountRepository.ProfileResult result = new AccountRepository(this).updateProfile(
                            currentUserId(), currentPass, email, first, last, display, newPass);
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        save.setEnabled(true);
                        if (result.error != null) {
                            Toast.makeText(this, result.error, Toast.LENGTH_LONG).show();
                            if (result.error.contains("password")) current.setError(result.error);
                            else if (result.error.contains("Email")) e.setError(result.error);
                        } else {
                            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (Exception ex) {
                    runOnUiThread(() -> {
                        save.setEnabled(true);
                        Toast.makeText(this, "Could not save profile", Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }
}
