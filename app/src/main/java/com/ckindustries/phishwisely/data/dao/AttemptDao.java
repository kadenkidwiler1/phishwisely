package com.ckindustries.phishwisely.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.ckindustries.phishwisely.data.entity.ChallengeAttempt;
import java.util.List;

@Dao
public interface AttemptDao {
    @Insert long insert(ChallengeAttempt attempt);
    @Query("SELECT * FROM challenge_attempts WHERE userId = :userId ORDER BY completedAt DESC, id DESC")
    List<ChallengeAttempt> forUser(long userId);
    @Query("SELECT * FROM challenge_attempts WHERE userId = :userId AND challengeId = :challengeId ORDER BY completedAt DESC, id DESC")
    List<ChallengeAttempt> forUserChallenge(long userId, int challengeId);
    @Query("SELECT * FROM challenge_attempts")
    List<ChallengeAttempt> getAll();
    @Query("DELETE FROM challenge_attempts WHERE userId = :userId")
    void clearForUser(long userId);
}
