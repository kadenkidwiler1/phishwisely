package com.ckindustries.phishwisely;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ckindustries.phishwisely.data.AppDatabase;
import com.ckindustries.phishwisely.data.entity.Challenge;
import com.ckindustries.phishwisely.data.entity.ChallengeAttempt;
import com.ckindustries.phishwisely.util.DatabaseExecutor;
import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends the partner's original Challenge screen; no SMS or voice categories.
 */
public class ChallengeActivity extends AuthenticatedActivity {
    private LinearLayout list;

    private int dp(int size) {
        return Math.round(size * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_challenge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        findViewById(R.id.challengeBackButton).setOnClickListener(v -> finish());
        list = findViewById(R.id.challengeListContainer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId() > 0) loadChallenges();
    }

    private void loadChallenges() {
        long id = currentUserId();
        DatabaseExecutor.IO.execute(() -> {
            AppDatabase db = AppDatabase.get(this);
            List<Challenge> challenges = db.challengeDao().getEmailChallenges();
            List<ChallengeAttempt> attempts = db.attemptDao().forUser(id);
            Map<Integer, ChallengeAttempt> latest = new HashMap<>();
            Map<Integer, Integer> best = new HashMap<>();
            Map<Integer, Integer> count = new HashMap<>();
            for (ChallengeAttempt a : attempts) {
                if (!latest.containsKey(a.challengeId)) latest.put(a.challengeId, a);
                best.put(a.challengeId, Math.max(best.containsKey(a.challengeId) ? best.get(a.challengeId) : 0, a.score));
                count.put(a.challengeId, count.containsKey(a.challengeId) ? count.get(a.challengeId) + 1 : 1);
            }
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                list.removeAllViews();
                for (Challenge c : challenges) {
                    ChallengeAttempt previous = latest.get(c.id);
                    int attemptsCount = count.containsKey(c.id) ? count.get(c.id) : 0;
                    int bestScore = best.containsKey(c.id) ? best.get(c.id) : 0;
                    addChallengeCard(c, previous, attemptsCount, bestScore);
                }
            });
        });
    }

    private void addChallengeCard(Challenge challenge, ChallengeAttempt last, int attempts, int best) {
        MaterialCardView card = new MaterialCardView(this);
        card.setRadius(dp(20));
        card.setCardElevation(0);
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_navy));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(-1, -2);
        cardLp.bottomMargin = dp(12);
        list.addView(card, cardLp);
        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(dp(18), dp(14), dp(18), dp(14));
        card.addView(inner);
        TextView difficulty = new TextView(this);
        difficulty.setText(challenge.difficulty.toUpperCase() + "  •  EMAIL PHISHING");
        difficulty.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));
        difficulty.setTextSize(12);
        difficulty.setTypeface(null, Typeface.BOLD);
        inner.addView(difficulty);
        TextView title = new TextView(this);
        title.setText(challenge.title);
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(ContextCompat.getColor(this, R.color.white));
        LinearLayout.LayoutParams headingLp = new LinearLayout.LayoutParams(-1, -2);
        headingLp.topMargin = dp(5);
        inner.addView(title, headingLp);
        TextView stats = new TextView(this);
        int percent = last == null ? 0 : Math.round(100f * last.correctCount / Math.max(last.questionCount, 1));
        stats.setText(attempts == 0 ? "Not attempted  •  Score: 0  •  Accuracy: —" :
                "Best score: " + best + "  •  Last accuracy: " + percent + "%  •  Attempts: " + attempts);
        stats.setTextColor(ContextCompat.getColor(this, R.color.grey));
        stats.setTextSize(14);
        LinearLayout.LayoutParams statsLp = new LinearLayout.LayoutParams(-1, -2);
        statsLp.topMargin = dp(9);
        inner.addView(stats, statsLp);
        TextView action = new TextView(this);
        action.setText("START CHALLENGE  ›");
        action.setTextSize(14);
        action.setTypeface(null, Typeface.BOLD);
        action.setTextColor(ContextCompat.getColor(this, R.color.accent_blue));
        LinearLayout.LayoutParams actionLp = new LinearLayout.LayoutParams(-2, -2);
        actionLp.topMargin = dp(12);
        inner.addView(action, actionLp);
        card.setClickable(true);
        card.setFocusable(true);
        card.setContentDescription(challenge.difficulty + " challenge " + challenge.title + ", " + stats.getText());
        card.setOnClickListener(v -> {
            Intent i = new Intent(this, ChallengePlayActivity.class);
            i.putExtra(ChallengePlayActivity.EXTRA_CHALLENGE_ID, challenge.id);
            startActivity(i);
        });
    }
}
