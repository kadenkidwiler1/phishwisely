package com.ckindustries.phishwisely.session;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores only an account ID; passwords and hashes stay out of preferences.
 */
public final class UserSession {
    private static final String KEY = "logged_in_user_id_v2";
    private final SharedPreferences preferences;

    public UserSession(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences("phishwisely_session_v2", Context.MODE_PRIVATE);
    }

    public void signIn(long userId) {
        preferences.edit().putLong(KEY, userId).apply();
    }

    public long userId() {
        // Handle stale integer preferences gracefully, even when upgrading an old install.
        Object raw = preferences.getAll().get(KEY);
        return raw instanceof Number ? ((Number) raw).longValue() : -1L;
    }

    public boolean isSignedIn() {
        return userId() > 0;
    }

    public void signOut() {
        preferences.edit().remove(KEY).apply();
    }
}
