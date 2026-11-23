package com.ckindustries.phishwisely;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ckindustries.phishwisely.data.AppDatabase;
import com.ckindustries.phishwisely.data.ScoreCalculator;
import com.ckindustries.phishwisely.util.DatabaseExecutor;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class LeaderboardActivity extends AuthenticatedActivity {
    private LinearLayout container;

    private int dp(int n) {
        return Math.round(n * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_leaderboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        findViewById(R.id.leaderboardBackButton).setOnClickListener(v -> finish());
        container = findViewById(R.id.fullLeaderboardList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId() > 0) load();
    }

    private void load() {
        long id = currentUserId();
        DatabaseExecutor.IO.execute(() -> {
            AppDatabase db = AppDatabase.get(this);
            List<ScoreCalculator.Standing> standings = ScoreCalculator.leaderboard(db.userDao().getAll(), db.attemptDao().getAll());
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                container.removeAllViews();
                int rank = 1;
                for (ScoreCalculator.Standing standing : standings) {
                    MaterialCardView card = new MaterialCardView(this);
                    card.setRadius(dp(16));
                    card.setCardElevation(0);
                    card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_navy));
                    LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
                    cp.bottomMargin = dp(10);
                    container.addView(card, cp);
                    TextView text = new TextView(this);
                    text.setPadding(dp(18), dp(16), dp(18), dp(16));
                    text.setText(rank + ".  " + standing.displayName + "   •   " + standing.points + " pts" +
                            (standing.userId == id ? "   (you)" : ""));
                    text.setTextSize(18);
                    text.setTextColor(ContextCompat.getColor(this, R.color.white));
                    if (standing.userId == id) text.setTypeface(null, Typeface.BOLD);
                    card.addView(text);
                    rank++;
                }
            });
        });
    }
}
