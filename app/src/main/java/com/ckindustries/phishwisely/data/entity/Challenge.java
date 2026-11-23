package com.ckindustries.phishwisely.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "challenges")
public class Challenge {
    @PrimaryKey
    public int id;
    public String category;
    public String difficulty;
    public String title;
    public String description;
    public String sender;
    public String senderEmail;
    public String subject;
    public String body;
    public int pointsPerQuestion;

    public Challenge(int id, String category, String difficulty, String title, String description,
                     String sender, String senderEmail, String subject, String body, int pointsPerQuestion) {
        this.id = id;
        this.category = category;
        this.difficulty = difficulty;
        this.title = title;
        this.description = description;
        this.sender = sender;
        this.senderEmail = senderEmail;
        this.subject = subject;
        this.body = body;
        this.pointsPerQuestion = pointsPerQuestion;
    }
}
