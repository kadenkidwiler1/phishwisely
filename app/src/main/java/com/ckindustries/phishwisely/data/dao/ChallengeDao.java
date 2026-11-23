package com.ckindustries.phishwisely.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.ckindustries.phishwisely.data.entity.Challenge;
import java.util.List;

@Dao
public interface ChallengeDao {
    @Query("SELECT * FROM challenges WHERE category = 'EMAIL' ORDER BY CASE difficulty WHEN 'Easy' THEN 1 WHEN 'Medium' THEN 2 ELSE 3 END, id")
    List<Challenge> getEmailChallenges();
    @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
    Challenge findById(int id);
    @Query("SELECT COUNT(*) FROM challenges WHERE category = 'EMAIL'")
    int emailCount();
}
