package com.ckindustries.phishwisely;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ckindustries.phishwisely.data.AccountRepository;
import com.ckindustries.phishwisely.data.AppDatabase;
import com.ckindustries.phishwisely.data.entity.Organization;
import com.ckindustries.phishwisely.data.entity.User;
import com.ckindustries.phishwisely.session.UserSession;
import com.ckindustries.phishwisely.util.DatabaseExecutor;

public class ProfileActivity extends AuthenticatedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return insets;
        });
        ImageButton back = findViewById(R.id.profileBackButton);
        back.setOnClickListener(v -> finish());
        Button logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(v -> {
            new UserSession(this).signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.editProfileButton).setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));
        findViewById(R.id.deleteAccountButton).setOnClickListener(v -> confirmDelete());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId() > 0) loadProfile();
    }

    private void loadProfile() {
        long id = currentUserId();
        DatabaseExecutor.IO.execute(() -> {
            AppDatabase db = AppDatabase.get(this);
            User u = db.userDao().findById(id);
            Organization org = u == null ? null : db.organizationDao().findById(u.organizationId);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (u == null) {
                    new UserSession(this).signOut();
                    finish();
                    return;
                }
                ((TextView) findViewById(R.id.greetingText)).setText(u.displayName);
                ((TextView) findViewById(R.id.profileNameValue)).setText("Name: " + u.firstName + " " + u.lastName);
                ((TextView) findViewById(R.id.profileEmailValue)).setText("Email: " + u.email);
                ((TextView) findViewById(R.id.profileDisplayname)).setText("Display Name: " + u.displayName);
                ((TextView) findViewById(R.id.profileOrganizationValue)).setText("Organization: " + (org == null ? "Unknown" : org.name));
            });
        });
    }

    private void confirmDelete() {
        EditText password = new EditText(this);
        password.setHint("Current password");
        password.setSingleLine(true);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        container.setPadding(pad, 0, pad, 0);
        container.addView(password);
        new AlertDialog.Builder(this).setTitle("Delete account")
                .setMessage("This permanently deletes your local account and all progress. Enter your current password to confirm.")
                .setView(container).setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    String secret = password.getText().toString();
                    if (secret.isEmpty()) {
                        Toast.makeText(this, "Password required", Toast.LENGTH_LONG).show();
                        return;
                    }
                    long id = currentUserId();
                    DatabaseExecutor.IO.execute(() -> {
                        boolean removed = new AccountRepository(this).deleteWithPassword(id, secret);
                        runOnUiThread(() -> {
                            if (!removed) {
                                Toast.makeText(this, "Incorrect password", Toast.LENGTH_LONG).show();
                                return;
                            }
                            new UserSession(this).signOut();
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
                    });
                }).show();
    }
}
