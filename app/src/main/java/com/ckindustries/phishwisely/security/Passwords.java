package com.ckindustries.phishwisely.security;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * PBKDF2 salted password hashes; never store plaintext passwords.
 */
public final class Passwords {
    private static final int ITERATIONS = 160_000;
    private static final int KEY_LENGTH_BITS = 256;

    private Passwords() {
    }

    public static String validate(String value) {
        if (value == null || value.length() < 8) return "Use at least 8 characters";
        if (!value.matches("(?s).*\\d.*")) return "Include at least one number";
        if (!value.matches("(?s).*[a-z].*")) return "Include a lowercase letter";
        if (!value.matches("(?s).*[A-Z].*")) return "Include an uppercase letter";
        if (!value.matches("(?s).*[^A-Za-z0-9].*")) return "Include a symbol";
        return null;
    }

    public static String makeSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static String hash(String password, String encodedSalt) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                Base64.decode(encodedSalt, Base64.NO_WRAP), ITERATIONS, KEY_LENGTH_BITS);
        try {
            byte[] hashed = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.encodeToString(hashed, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing unavailable", e);
        } finally {
            spec.clearPassword();
        }
    }

    public static boolean verify(String password, String salt, String expectedHash) {
        if (password == null || salt == null || expectedHash == null) return false;
        byte[] actual = Base64.decode(hash(password, salt), Base64.NO_WRAP);
        byte[] expected = Base64.decode(expectedHash, Base64.NO_WRAP);
        return MessageDigest.isEqual(actual, expected);
    }
}
