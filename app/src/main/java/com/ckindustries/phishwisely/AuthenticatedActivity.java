package com.ckindustries.phishwisely;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.ckindustries.phishwisely.session.UserSession;

/**
 * Shared navigation protection for screens requiring login.
 */
public abstract class AuthenticatedActivity extends AppCompatActivity {
    protected long currentUserId() {
        return new UserSession(this).userId();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUserId() <= 0) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
