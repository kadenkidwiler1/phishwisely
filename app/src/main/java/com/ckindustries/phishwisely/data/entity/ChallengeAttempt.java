package com.ckindustries.phishwisely.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "challenge_attempts",
        indices = {@Index("userId"), @Index("challengeId")},
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Challenge.class, parentColumns = "id", childColumns = "challengeId", onDelete = ForeignKey.CASCADE)})
public class ChallengeAttempt {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    public int challengeId;
    public int score;
    public int correctCount;
    public int questionCount;
    public long completedAt;

    public ChallengeAttempt(long userId, int challengeId, int score, int correctCount,
                            int questionCount, long completedAt) {
        this.userId = userId;
        this.challengeId = challengeId;
        this.score = score;
        this.correctCount = correctCount;
        this.questionCount = questionCount;
        this.completedAt = completedAt;
    }
}
