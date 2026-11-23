package com.ckindustries.phishwisely.data;

import android.content.Context;

import java.util.Locale;

import com.ckindustries.phishwisely.data.entity.User;
import com.ckindustries.phishwisely.data.entity.Organization;
import com.ckindustries.phishwisely.security.Passwords;

/**
 * Invoke every method on DatabaseExecutor.IO, not on the UI thread.
 */
public final class AccountRepository {
    private final AppDatabase database;

    public AccountRepository(Context context) {
        database = AppDatabase.get(context);
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public User signIn(String email, String password) {
        User user = database.userDao().findByEmail(normalizeEmail(email));
        return user != null && Passwords.verify(password, user.passwordSalt, user.passwordHash)
                ? user : null;
    }

    public static final class Registration {
        public final User user;
        public final String error;

        Registration(User user, String error) {
            this.user = user;
            this.error = error;
        }
    }

    public Registration register(String email, String password, String displayName,
                                 String firstName, String lastName, String organizationCode) {
        email = normalizeEmail(email);
        if (database.userDao().findByEmail(email) != null)
            return new Registration(null, "Email already registered");
        Organization org = database.organizationDao().findBySignupCode(organizationCode.trim());
        if (org == null) return new Registration(null, "Invalid organization signup code");
        String salt = Passwords.makeSalt();
        User user = new User(email, displayName, firstName, lastName,
                org.id, Passwords.hash(password, salt), salt, System.currentTimeMillis());
        long id = database.userDao().insert(user);
        if (id < 1) return new Registration(null, "Email already registered");
        user.id = id;
        return new Registration(user, null);
    }

    public static final class ProfileResult {
        public final String error;
        public final User user;

        ProfileResult(String error, User user) {
            this.error = error;
            this.user = user;
        }
    }

    public ProfileResult updateProfile(long userId, String currentPassword, String email,
                                       String firstName, String lastName, String displayName,
                                       String newPassword) {
        User user = database.userDao().findById(userId);
        if (user == null) return new ProfileResult("Account not found", null);
        if (!Passwords.verify(currentPassword, user.passwordSalt, user.passwordHash))
            return new ProfileResult("Current password is incorrect", null);
        email = normalizeEmail(email);
        User other = database.userDao().findByEmail(email);
        if (other != null && other.id != user.id)
            return new ProfileResult("Email already registered", null);
        if (!newPassword.isEmpty()) {
            String policy = Passwords.validate(newPassword);
            if (policy != null) return new ProfileResult(policy, null);
        }
        user.email = email;
        user.firstName = firstName;
        user.lastName = lastName;
        user.displayName = displayName;
        if (!newPassword.isEmpty()) {
            user.passwordSalt = Passwords.makeSalt();
            user.passwordHash = Passwords.hash(newPassword, user.passwordSalt);
        }
        try {
            int updated = database.userDao().update(user);
            return updated == 1 ? new ProfileResult(null, user) : new ProfileResult("Update failed", null);
        } catch (android.database.sqlite.SQLiteConstraintException ex) {
            return new ProfileResult("Email already registered", null);
        }
    }

    public boolean deleteWithPassword(long userId, String password) {
        User user = database.userDao().findById(userId);
        if (user == null || !Passwords.verify(password, user.passwordSalt, user.passwordHash))
            return false;
        return database.userDao().delete(user) == 1;
    }
}
