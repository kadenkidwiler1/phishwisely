package com.ckindustries.phishwisely.data;

import com.ckindustries.phishwisely.data.entity.ChallengeAttempt;
import com.ckindustries.phishwisely.data.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Scores use the best attempt per challenge, so retries cannot farm leaderboard points.
 */
public final class ScoreCalculator {
    private ScoreCalculator() {
    }

    public static final class Summary {
        public int points;
        public int correctAnswers;
        public int totalAnswers;
        public int completedChallenges;
        public int attemptCount;

        public int accuracy() {
            return totalAnswers == 0 ? 0 : Math.round(100f * correctAnswers / totalAnswers);
        }
    }

    public static Summary summarize(List<ChallengeAttempt> attempts) {
        Summary s = new Summary();
        Map<Integer, Integer> best = new HashMap<>();
        Set<Integer> completed = new HashSet<>();
        for (ChallengeAttempt a : attempts) {
            completed.add(a.challengeId);
            best.put(a.challengeId, Math.max(best.containsKey(a.challengeId) ? best.get(a.challengeId) : 0, a.score));
            s.correctAnswers += a.correctCount;
            s.totalAnswers += a.questionCount;
            s.attemptCount++;
        }
        for (int p : best.values()) s.points += p;
        s.completedChallenges = completed.size();
        return s;
    }

    public static final class Standing {
        public long userId;
        public String displayName;
        public int points;

        public Standing(long userId, String displayName, int points) {
            this.userId = userId;
            this.displayName = displayName;
            this.points = points;
        }
    }

    public static List<Standing> leaderboard(List<User> users, List<ChallengeAttempt> attempts) {
        Map<Long, List<ChallengeAttempt>> byUser = new HashMap<>();
        for (ChallengeAttempt a : attempts) {
            if (!byUser.containsKey(a.userId)) byUser.put(a.userId, new ArrayList<>());
            byUser.get(a.userId).add(a);
        }
        List<Standing> result = new ArrayList<>();
        for (User user : users) {
            List<ChallengeAttempt> each = byUser.get(user.id);
            int score = each == null ? 0 : summarize(each).points;
            result.add(new Standing(user.id, user.displayName, score));
        }
        result.sort((a, b) -> {
            int pointsOrder = Integer.compare(b.points, a.points);
            return pointsOrder != 0 ? pointsOrder : a.displayName.compareToIgnoreCase(b.displayName);
        });
        return result;
    }
}
