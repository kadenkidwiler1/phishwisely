package com.ckindustries.phishwisely.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.ckindustries.phishwisely.data.entity.Question;
import java.util.List;

@Dao
public interface QuestionDao {
    @Query("SELECT * FROM questions WHERE challengeId = :challengeId ORDER BY id")
    List<Question> forChallenge(int challengeId);
}
