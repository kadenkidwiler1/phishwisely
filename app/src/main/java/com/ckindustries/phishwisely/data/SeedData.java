package com.ckindustries.phishwisely.data;

import android.database.Cursor;

import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ckindustries.phishwisely.security.Passwords;

/**
 * Seed the initial database with a demo account, class account, their respective organizations, and initial challenge set
 */
final class SeedData {
    private SeedData() {
    }

    static void populate(SupportSQLiteDatabase db) {

        // ==============================
        // Organization 1: CK Industries
        // ==============================

        db.execSQL(
                "INSERT INTO organizations (name, signupCode) VALUES (?, ?)", new Object[]{"CK Industries", "CKI"});

        long ckOrganizationId;

        try (Cursor cursor = db.query(
                "SELECT id FROM organizations WHERE signupCode = ?",
                new Object[]{"CKI"})) {

            if (!cursor.moveToFirst()) {
                throw new IllegalStateException("CK Industries organization seed failed");
            }

            ckOrganizationId = cursor.getLong(0);
        }

        // Generate a unique salt for the demo user
        String ckSalt = Passwords.makeSalt();

        db.execSQL(
                "INSERT INTO users (email, displayName, firstName, lastName, " +
                        "organizationId, passwordHash, passwordSalt, createdAt) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",

                new Object[]{
                        "demo@phishwisely.com",
                        "CK Demo",
                        "CK",
                        "Demo",
                        ckOrganizationId,
                        Passwords.hash("Demo123!", ckSalt),
                        ckSalt,
                        System.currentTimeMillis()
                }
        );


        // ========================
        // Organization 2: COSC412
        // ========================

        db.execSQL(
                "INSERT INTO organizations (name, signupCode) VALUES (?, ?)",
                new Object[]{"COSC412", "COSC412"}
        );

        long coscOrganizationId;

        try (Cursor cursor = db.query(
                "SELECT id FROM organizations WHERE signupCode = ?",
                new Object[]{"COSC412"})) {

            if (!cursor.moveToFirst()) {
                throw new IllegalStateException("Classroom organization seed failed");
            }

            coscOrganizationId = cursor.getLong(0);
        }

        // Generate a unique (but separate) salt for the class user
        String coscSalt = Passwords.makeSalt();

        db.execSQL(
                "INSERT INTO users (email, displayName, firstName, lastName, " +
                        "organizationId, passwordHash, passwordSalt, createdAt) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",

                new Object[]{
                        "student@cosc412.towson.edu",
                        "COSC412 Student",
                        "COSC412",
                        "Student",
                        coscOrganizationId,
                        Passwords.hash("Tigers2026!", coscSalt),
                        coscSalt,
                        System.currentTimeMillis()
                }
        );


        addChallenge(
                db,
                101,
                "EMAIL",
                "Easy",
                "TEST",
                "Help Desk",
                "TEST@microsoft.com",
                "testsubject",
                "This is a fake email.",
                10);
        addQuestion(
                db,
                1,
                101,
                "FLAG",
                "Guess the correct answer",
                "b",
                "c",
                "a",
                "d",
                0,
                "Because.");
        addQuestion(
                db,
                2,
                101,
                "FLAG",
                "Guess what number I'm thinking of",
                "10",
                "42",
                "7",
                "18",
                1,
                "It's the answer to the meaning of life.");
        addChallenge(
                db,
                102,
                "EMAIL",
                "Easy",
                "TEST",
                "test2",
                "TEST2@microsoft.com",
                "testsubject2",
                "This is a super real email. Please give me a giftcard",
                10);
        addQuestion(
                db,
                3,
                102,
                "MULTIPLE_CHOICE",
                "Choose the correct answer.",
                "Send them a giftcard",
                "Ask for their venmo instead",
                "Report this email to your IT team and do not send them anything",
                "Ask them how many giftcards they need since they didn't specify it",
                2,
                "Report this email and do not send them anything");
    }

    private static void addChallenge(SupportSQLiteDatabase db, int id, String category, String difficulty,
                                     String title, String sender, String senderEmail, String subject,
                                     String body, int points) {
        db.execSQL("INSERT INTO challenges (id,category,difficulty,title,sender,senderEmail,subject,body,pointsPerQuestion) " +
                        "VALUES (?,?,?,?,?,?,?,?,?)",
                new Object[]{id, category, difficulty, title, sender, senderEmail, subject, body, points});
    }

    private static void addQuestion(SupportSQLiteDatabase db, int id, int challengeId, String type,
                                    String prompt, String a, String b, String c, String d,
                                    int correctIndex, String explanation) {
        db.execSQL("INSERT INTO questions (id,challengeId,type,prompt,optionA,optionB,optionC,optionD,correctIndex,explanation) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?)",
                new Object[]{id, challengeId, type, prompt, a, b, c, d, correctIndex, explanation});
    }
}
