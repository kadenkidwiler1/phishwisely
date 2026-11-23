package com.ckindustries.phishwisely;

import org.junit.Test;
import java.util.Arrays;
import com.ckindustries.phishwisely.data.ScoreCalculator;
import com.ckindustries.phishwisely.data.entity.ChallengeAttempt;
import static org.junit.Assert.*;


public class ScoreCalculatorTest {

    @Test
    public void bestAttemptCountsOnlyOnceButAccuracyIncludesRetries() {

        // Create the first attempt for Challenge 101:
        // 10 points, 1 correct answer out of 2.
        ChallengeAttempt first =
                new ChallengeAttempt(1, 101, 10, 1, 2, 1);

        // Create a second attempt for Challenge 101:
        // 20 points, 2 correct answers out of 2.
        ChallengeAttempt retry =
                new ChallengeAttempt(1, 101, 20, 2, 2, 2);

        // Create an attempt for Challenge 102:
        // 10 points, 1 correct answer out of 1.
        ChallengeAttempt another =
                new ChallengeAttempt(1, 102, 10, 1, 1, 3);

        // Calculate overall statistics using all three attempts.
        ScoreCalculator.Summary summary =
                ScoreCalculator.summarize(
                        Arrays.asList(first, retry, another)
                );

        // Passes if total points use only the best attempt
        // per challenge (20 + 10 = 30).
        assertEquals(30, summary.points);

        // Passes if only two unique challenges are counted
        // as completed, despite Challenge 101 being attempted twice.
        assertEquals(2, summary.completedChallenges);

        // Passes if all three attempts are counted,
        // including the retry.
        assertEquals(3, summary.attemptCount);

        // Passes if correct answers include all attempts
        // (1 + 2 + 1 = 4).
        assertEquals(4, summary.correctAnswers);

        // Passes if total answers include all attempts
        // (2 + 2 + 1 = 5).
        assertEquals(5, summary.totalAnswers);

        // Passes if accuracy is 80%:
        // (4 correct / 5 total) * 100 = 80%.
        assertEquals(80, summary.accuracy());
    }
}
