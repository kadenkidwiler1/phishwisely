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


        // ==========
        // Easy
        // ==========

        addChallenge(
                db,
                101,
                "EMAIL",
                "Easy",
                "Suspicious Password Reset",
                "This is the challenge description", // description (placeholder)
                "IT Help Desk",
                "alerts@company-verify.example",
                "Reset your password now",
                "Your account expires in 30 minutes. Visit https://signin.company-verify.example/reset and enter your password to avoid suspension.",
                10);
        addQuestion(
                db,
                1,
                101,
                "FLAG",
                "Flag the clearest suspicious part of this email.",
                "The sender domain company-verify.example",
                "The subject mentions a password",
                "The email uses a greeting",
                "The message has a deadline",
                0,
                "The sender domain is not the organization’s verified domain. Always check it independently.");
        addQuestion(
                db,
                2,
                101,
                "MULTIPLE_CHOICE",
                "What should you do first?",
                "Open the link quickly",
                "Reply with your password",
                "Use your known company sign-in portal",
                "Forward to all colleagues",
                2,
                "Go to a trusted portal directly rather than following an unsolicited sign-in link.");





        // ==========
        // Medium
        // ==========

        addChallenge(
                db,
                201,
                "EMAIL",
                "Medium",
                "MFA Approval Fatigue",
                "This is the challenge description", // description (placeholder)
                "Access Security",
                "mfa@security-notice.example",
                "Approve your sign-in",
                "We detected a pending sign-in. Approve the MFA request that appears on your device to keep your session active.",
                20);
        addQuestion(
                db,
                3,
                201,
                "MULTIPLE_CHOICE",
                "You did not attempt to sign in. What should you do?",
                "Deny and report the request",
                "Approve to dismiss it",
                "Share the one-time code by email",
                "Ignore repeated requests forever",
                0,
                "Unexpected MFA prompts can signal a stolen password or attacker fatigue attempt.");
        addQuestion(
                db,
                4,
                201,
                "FLAG",
                "Which phrase asks you to perform the unsafe action?",
                "Approve the MFA request",
                "We detected a pending sign-in",
                "Security notice",
                "Keep your session active",
                0,
                "Never approve authentication requests you did not initiate.");
        addQuestion(
                db,
                5,
                201,
                "MULTIPLE_CHOICE",
                "What else is appropriate after repeated unexpected MFA prompts?",
                "Report to security and reset credentials via a trusted route",
                "Turn off MFA permanently",
                "Give your password to the sender",
                "Reply to the alert with your OTP",
                0,
                "Report the activity and use official account-recovery channels.");


        // ==========
        // Hard
        // ==========

        addChallenge(
                db,
                301,
                "EMAIL",
                "Hard",
                "QR Code",
                "This is the challenge description", // description (placeholder)
                "Bill Gates",
                "billgates@microsoft.com",
                "Confidential board packet",
                "Use the QR code in the attached PDF to sign in and view the confidential board packet. Do not share this request with the IT team until the board meeting ends.",
                30);
        addQuestion(
                db,
                6,
                301,
                "MULTIPLE_CHOICE",
                "Why is the QR code concerning?",
                "It can hide a destination that is difficult to inspect",
                "QR codes cannot contain links",
                "A PDF cannot be malicious",
                "Executives always use QR codes",
                0,
                "A QR image can conceal a credential-harvesting destination.");
        addQuestion(
                db,
                7,
                301,
                "FLAG",
                "Which line should be reported immediately?",
                "Do not share this request with the IT team",
                "Confidential board packet",
                "Executive Office",
                "View a PDF",
                0,
                "Secrecy instructions that bypass established verification are a hallmark of social engineering.");
        addQuestion(
                db,
                8,
                301,
                "MULTIPLE_CHOICE",
                "What is an appropriate verification channel?",
                "An independently known executive assistant or IT contact",
                "Reply to the suspicious email",
                "Scan the QR code first",
                "Use the PDF contact number",
                0,
                "Use a trusted contact path that does not rely on information inside the suspicious message.");
        addQuestion(
                db,
                9,
                301,
                "MULTIPLE_CHOICE",
                "Why might attackers claim the request is confidential?",
                "To discourage scrutiny and reporting",
                "To prove legal approval",
                "To show the message is encrypted",
                "To reduce the file size",
                0,
                "Authority and secrecy can pressure targets into skipping controls.");
        addQuestion(
                db,
                10,
                301,
                "MULTIPLE_CHOICE",
                "How should the suspicious message be handled?",
                "Report with the original email and avoid scanning the code",
                "Forward it broadly",
                "Approve the login",
                "Delete all records immediately",
                0,
                "Preserve evidence and report using the organization’s established process.");


    }



    private static void addChallenge(SupportSQLiteDatabase db, int id, String category, String difficulty,
                                     String title, String description, String sender, String senderEmail, String subject,
                                     String body, int points) {
        db.execSQL("INSERT INTO challenges (id,category,difficulty,title,description,sender,senderEmail,subject,body,pointsPerQuestion) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?)",
                new Object[]{id, category, difficulty, title, description, sender, senderEmail, subject, body, points});
    }

    private static void addQuestion(SupportSQLiteDatabase db, int id, int challengeId, String type,
                                    String prompt, String a, String b, String c, String d,
                                    int correctIndex, String explanation) {
        db.execSQL("INSERT INTO questions (id,challengeId,type,prompt,optionA,optionB,optionC,optionD,correctIndex,explanation) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?)",
                new Object[]{id, challengeId, type, prompt, a, b, c, d, correctIndex, explanation});
    }
}
