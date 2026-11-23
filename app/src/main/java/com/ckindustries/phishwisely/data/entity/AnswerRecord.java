package com.ckindustries.phishwisely.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "answer_records", indices = {@Index("attemptId"), @Index("questionId")},
        foreignKeys = {
                @ForeignKey(entity = ChallengeAttempt.class, parentColumns = "id", childColumns = "attemptId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Question.class, parentColumns = "id", childColumns = "questionId", onDelete = ForeignKey.CASCADE)})
public class AnswerRecord {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long attemptId;
    public int questionId;
    public int selectedIndex;
    public boolean correct;

    public AnswerRecord(long attemptId, int questionId, int selectedIndex, boolean correct) {
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.selectedIndex = selectedIndex;
        this.correct = correct;
    }
}
