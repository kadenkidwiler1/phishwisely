package com.ckindustries.phishwisely;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ckindustries.phishwisely.data.AppDatabase;
import com.ckindustries.phishwisely.data.ScoreCalculator;
import com.ckindustries.phishwisely.data.entity.ChallengeAttempt;
import com.ckindustries.phishwisely.data.entity.User;
import com.ckindustries.phishwisely.util.DatabaseExecutor;

import java.util.List;

public class DashboardActivity extends AuthenticatedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_dashboard); // Partner's dashboard retained.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        ImageButton profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> startActivity(new Intent(this, ChallengeActivity.class)));
        findViewById(R.id.viewLeaderboardButton).setOnClickListener(v ->
                startActivity(new Intent(this, LeaderboardActivity.class)));
        findViewById(R.id.resetProgressButton).setOnClickListener(v ->
                new AlertDialog.Builder(this).setTitle("Reset progress?")
                        .setMessage("Delete all your challenge attempts and scores? This cannot be undone.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Reset", (d, which) -> resetProgress()).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId() > 0) refresh();
    }

    private void refresh() {
        long userId = currentUserId();
        DatabaseExecutor.IO.execute(() -> {
            AppDatabase db = AppDatabase.get(this);
            User user = db.userDao().findById(userId);
            if (user == null) {
                runOnUiThread(this::redirectToLogin);
                return;
            }
            List<ChallengeAttempt> attempts = db.attemptDao().forUser(userId);
            ScoreCalculator.Summary sum = ScoreCalculator.summarize(attempts);
            int total = db.challengeDao().emailCount();
            List<ScoreCalculator.Standing> board = ScoreCalculator.leaderboard(db.userDao().getAll(), db.attemptDao().getAll());
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                ((TextView) findViewById(R.id.greetingText)).setText("Welcome Back, " + user.displayName);
                ((TextView) findViewById(R.id.pointsValue)).setText(String.valueOf(sum.points));
                ((TextView) findViewById(R.id.accuracyValue)).setText(sum.accuracy() + "%");
                ((TextView) findViewById(R.id.completedValue)).setText(sum.completedChallenges + "/" + total);
                ((TextView) findViewById(R.id.emailProgressText)).setText(sum.completedChallenges + " of " + total + " completed");
                int rank = 0;
                for (int i = 0; i < board.size(); i++)
                    if (board.get(i).userId == userId) rank = i + 1;
                ((TextView) findViewById(R.id.leaderboardUserRank)).setText("Your Rank: #" + rank);
                int[] names = {R.id.leaderboardName1, R.id.leaderboardName2, R.id.leaderboardName3};
                int[] scores = {R.id.leaderboardPoints1, R.id.leaderboardPoints2, R.id.leaderboardPoints3};
                for (int i = 0; i < 3; i++) {
                    ((TextView) findViewById(names[i])).setText(i < board.size() ? board.get(i).displayName : "—");
                    ((TextView) findViewById(scores[i])).setText(i < board.size() ? board.get(i).points + " pts" : "");
                }
            });
        });
    }

    private void resetProgress() {
        long id = currentUserId();
        DatabaseExecutor.IO.execute(() -> {
            AppDatabase.get(this).attemptDao().clearForUser(id);
            runOnUiThread(() -> {
                Toast.makeText(this, "Progress reset", Toast.LENGTH_SHORT).show();
                refresh();
            });
        });
    }

    private void redirectToLogin() {
        new com.ckindustries.phishwisely.session.UserSession(this).signOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
