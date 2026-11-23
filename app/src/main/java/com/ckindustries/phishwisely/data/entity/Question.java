package com.ckindustries.phishwisely.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions", indices = {@Index("challengeId")},
        foreignKeys = {@ForeignKey(entity = Challenge.class, parentColumns = "id",
                childColumns = "challengeId", onDelete = ForeignKey.CASCADE)})
public class Question {
    @PrimaryKey
    public int id;
    public int challengeId;
    public String type; // MULTIPLE_CHOICE or FLAG_SUSPICIOUS_PART
    public String prompt;
    public String optionA;
    public String optionB;
    public String optionC;
    public String optionD;
    public int correctIndex; // 0=A, 1=B, 2=C, 3=D
    public String explanation;

    public Question(int id, int challengeId, String type, String prompt,
                    String optionA, String optionB, String optionC, String optionD,
                    int correctIndex, String explanation) {
        this.id = id;
        this.challengeId = challengeId;
        this.type = type;
        this.prompt = prompt;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }
}
