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
        addChallenge(
                db,
                102,
                "EMAIL",
                "Easy",
                "Unclaimed Funds",
                "You received an email sent to your company email address about unclaimed funds. Awesome!",
                "Josh Smith",
                "claims@newyork.refundclaims.ru",
                "Claim your funds",
                "You have unclaimed funds from the State of New York. You have 48 hours to claim the funds before they are property of the state. Visit https://rb.gy/2a2lus to claim.",
                10);
        addQuestion(
                db,
                3,
                102,
                "FLAG",
                "Which part of the email is the biggest phishing red flag?",
                "Josh Smith as the sender",
                "An obfuscated and confusing URL",
                "The email references a state you haven't visited",
                "There's nothing wrong with this email",
                1,
                "An obfuscated URL makes it difficult to determine what the destination address will be");
        addQuestion(
                db,
                4,
                102,
                "MULTIPLE_CHOICE",
                "You accidentally clicked the link! What should be done next?",
                "Investigate the website's source code",
                "You quickly realized your mistake and closed the tab, so you're safe",
                "Close the tab and immediately report this incident to your IT department",
                "Raise your fist in anger as you got rick-rolled yet again",
                2,
                "Your IT department must be notified so that they can investigate the extent of potential damages");





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
                5,
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
                6,
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
                7,
                201,
                "MULTIPLE_CHOICE",
                "What else is appropriate after repeated unexpected MFA prompts?",
                "Report to security and reset credentials via a trusted route",
                "Turn off MFA permanently",
                "Give your password to the sender",
                "Reply to the alert with your OTP",
                0,
                "Report the activity and use official account-recovery channels.");
        addChallenge(
                db,
                202,
                "EMAIL",
                "Medium",
                "Shared Document Invite",
                "You work at Microsoft and have a project due by end-of-day. You receive an email asking you to collaborate on a document that seems vaguely relevant to what you're working on.",
                "Michael Moore",
                "mmoore@mícrosoft.com",
                "Moore, Michael has invited you to 'Project Financing'",
                "A spreadsheet has been shared with you. Open https://spreadsheet.mícrosoft.com?invite=Y2K and sign in to access. The link expires in 15 minutes.",
                10);
        addQuestion(
                db,
                8,
                202,
                "MULTIPLE_CHOICE",
                "Is there anything particularly concerning with this email?",
                "You don't usually interact with Michael often",
                "Some invites may be time-sensitive so this should be fine",
                "The format of the email looks like what I'd expect of an invitation so it must be safe",
                "The 'i' in microsoft looks a little weird, maybe?",
                3,
                "This is categorized as a Homograph attack; the sender is impersonating a trusted sender by making it look visually similar");
        addQuestion(
                db,
                9,
                202,
                "FLAG",
                "Flag a different phishing tactic being used by the sender",
                "The link expires in 15 minutes.",
                "A spreadsheet has been shared with you.",
                "Open https://spreadsheet.mĩcrosoft.com?invite=Y2K",
                "sign in to access",
                0,
                "Creating a sense of urgency by imposing a time limit entices users to perform actions they might have otherwise not performed.");


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
                10,
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
                11,
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
                12,
                301,
                "MULTIPLE_CHOICE",
                "What is an appropriate verification channel?",
                "Scan the QR code to find an appropriate channel",
                "Reply to the suspicious email",
                "An independently known executive assistant or IT contact",
                "Use the PDF contact number",
                2,
                "Use a trusted contact path that does not rely on information from within the suspicious message.");
        addQuestion(
                db,
                13,
                301,
                "MULTIPLE_CHOICE",
                "Why might attackers claim the request is confidential?",
                "It contains classified information",
                "To discourage scrutiny and reporting",
                "To show the message is encrypted",
                "To make you feel like you're important",
                1,
                "Authority and secrecy can pressure targets into skipping security protocols.");
        addQuestion(
                db,
                14,
                301,
                "MULTIPLE_CHOICE",
                "How should the suspicious message be handled?",
                "Delete all record of it immediately",
                "Forward it to your colleagues",
                "Approve the login",
                "Report with the original email and do not scan the code",
                3,
                "Preserve evidence and report using the organization’s established process.");
        addChallenge(
                db,
                302,
                "EMAIL",
                "Hard",
                "Meeting Minutes",
                "You work at Microsoft and have a project due by end-of-day. You receive an email from a colleague asking you to examine an attached document.",
                "Michael Moore",
                "mmoore@microsoft.com",
                "Project Financing Meeting Minutes",
                "Hi, I know you're busy but our department just had an urgent meeting on project financing. I've attached the meeting minutes, can you please give me your thoughts on this and respond by end-of-day?",
                10);
        addQuestion(
                db,
                15,
                302,
                "FLAG",
                "Flag the suspicious part of the message",
                "mmoore@microsoft.com",
                "I've attached the meeting minutes",
                "respond by end-of-day",
                "Nothing is immediately suspicious about this email.",
                3,
                "While a homograph attack could be possible, nothing is particularly suspicious about this email at first glance");
        addQuestion(
                db,
                16,
                302,
                "MULTIPLE_CHOICE",
                "You open the attached 'MeetingMinutes.odt' document. It took a little longer to load than expected and you noticed a very brief flash across your screen, but maybe it was just a weird screen thing. The contents of the document seem pretty vague. What's your next step?",
                "Respond to Michael with your thoughts",
                "Report the screen flashing to your IT department immediately",
                "Restart your computer to fix the screen flickering issue",
                "Call Michael",
                1,
                "The flashing screen could have been the result of an embedded macro within the document, executing a malicious script on your device. .odt files are legitimate document files but may contain embedded macros. This must be reported immediately.");
        addQuestion(
                db,
                17,
                302,
                "MULTIPLE_CHOICE",
                "You catch up with Michael later in the day and he denies sending you any emails today. What can you reasonably conclude?",
                "Michael is being a jerk",
                "Michael's email address may have been spoofed or compromised",
                "Michael likely just forgot, no big deal",
                "Michael is the phisher",
                1,
                "Michael's denial suggests that the email was not authorized by him and the account is potentially compromised");
        addQuestion(
                db,
                18,
                302,
                "MULTIPLE_CHOICE",
                "Jane calls you to wonder why you're sending her irrelevant meeting minutes. What can you reasonably conclude?",
                "Michael must have sent the email to Jane using my name",
                "Jane has confused me for another similarly named person at Microsoft",
                "My account credentials have potentially been compromised",
                "Jane is looking for someone to blame for a failed project",
                2,
                "Since Jane received an email appearing to come from you, your credentials may have been compromised. Report this to your IT department immediately");


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
