package com.ckindustries.phishwisely;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ckindustries.phishwisely.data.AppDatabase;
import com.ckindustries.phishwisely.data.entity.AnswerRecord;
import com.ckindustries.phishwisely.data.entity.Challenge;
import com.ckindustries.phishwisely.data.entity.ChallengeAttempt;
import com.ckindustries.phishwisely.data.entity.Question;
import com.ckindustries.phishwisely.util.DatabaseExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Each exercise is a fictional email followed by questions or suspicious-part flags.
 */
public class ChallengePlayActivity extends AuthenticatedActivity {
    public static final String EXTRA_CHALLENGE_ID = "challenge_id";
    private Challenge challenge;
    private List<Question> questions;
    private final List<Integer> selected = new ArrayList<>();
    private final List<Boolean> correctness = new ArrayList<>();
    private int index = 0;
    private boolean locked = false;
    private Button next;
    private Button[] choices;
    private TextView feedback;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_challenge_play);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        findViewById(R.id.playBackButton).setOnClickListener(v -> finish());
        choices = new Button[]{findViewById(R.id.choiceA), findViewById(R.id.choiceB),
                findViewById(R.id.choiceC), findViewById(R.id.choiceD)};
        feedback = findViewById(R.id.answerFeedback);
        next = findViewById(R.id.nextQuestionButton);
        next.setEnabled(false);
        for (int i = 0; i < choices.length; i++) {
            final int pos = i;
            choices[i].setOnClickListener(v -> choose(pos));
        }
        next.setOnClickListener(v -> {
            if (!locked) return;
            index++;
            if (index >= questions.size()) saveAttempt();
            else showQuestion();
        });
        int id = getIntent().getIntExtra(EXTRA_CHALLENGE_ID, -1);
        DatabaseExecutor.IO.execute(() -> {
            AppDatabase db = AppDatabase.get(this);
            Challenge loaded = db.challengeDao().findById(id);
            List<Question> loadedQuestions = db.questionDao().forChallenge(id);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (loaded == null || loadedQuestions.isEmpty() || !"EMAIL".equals(loaded.category)) {
                    Toast.makeText(this, "Email challenge not found", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                challenge = loaded;
                questions = loadedQuestions;
                ((TextView) findViewById(R.id.playTitle)).setText(loaded.title);
                ((TextView) findViewById(R.id.emailFrom)).setText("From: " + loaded.sender + " <" + loaded.senderEmail + ">");
                ((TextView) findViewById(R.id.emailSubject)).setText("Subject: " + loaded.subject);
                ((TextView) findViewById(R.id.emailBody)).setText(loaded.body);
                showQuestion();
            });
        });
    }

    private void showQuestion() {
        locked = false;
        Question q = questions.get(index);
        ((TextView) findViewById(R.id.questionCounter)).setText("QUESTION " + (index + 1) + " OF " + questions.size() +
                "  •  " + challenge.difficulty.toUpperCase());
        ((TextView) findViewById(R.id.questionPrompt)).setText(
                ("FLAG".equals(q.type) ? "Flag suspicious content: " : "") + q.prompt);
        String[] labels = {q.optionA, q.optionB, q.optionC, q.optionD};
        for (int i = 0; i < 4; i++) {
            choices[i].setText((char) ('A' + i) + ".  " + labels[i]);
            choices[i].setEnabled(true);
            choices[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.card_navy)));
            choices[i].setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        feedback.setText("");
        next.setEnabled(false);
        next.setText(index == questions.size() - 1 ? "Finish Challenge" : "Next Question");
    }

    private void choose(int choice) {
        if (locked) return;
        locked = true;
        Question q = questions.get(index);
        boolean correct = (choice == q.correctIndex);
        selected.add(choice);
        correctness.add(correct);
        for (int i = 0; i < choices.length; i++) choices[i].setEnabled(false);
        choices[q.correctIndex].setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF277B63));
        if (!correct)
            choices[choice].setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF8A293A));
        feedback.setText((correct ? "Correct. " : "Incorrect. ") + q.explanation);
        feedback.setTextColor(ContextCompat.getColor(this, R.color.white));
        next.setEnabled(true);
    }

    private void saveAttempt() {
        next.setEnabled(false);
        final long uid = currentUserId();
        final int scorePerQuestion = challenge.pointsPerQuestion;
        int right = 0;
        for (boolean b : correctness) if (b) right++;
        final int rightCount = right;
        final int score = rightCount * scorePerQuestion;
        DatabaseExecutor.IO.execute(() -> {
            AppDatabase db = AppDatabase.get(this);
            db.runInTransaction(() -> {
                ChallengeAttempt attempt = new ChallengeAttempt(uid, challenge.id, score, rightCount, questions.size(), System.currentTimeMillis());
                long attemptId = db.attemptDao().insert(attempt);
                List<AnswerRecord> records = new ArrayList<>();
                for (int i = 0; i < questions.size(); i++)
                    records.add(new AnswerRecord(attemptId, questions.get(i).id, selected.get(i), correctness.get(i)));
                db.answerDao().insertAll(records);
            });
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                new AlertDialog.Builder(this).setTitle("Challenge complete")
                        .setMessage("Score: " + score + " points\nAccuracy: " +
                                Math.round(100f * rightCount / questions.size()) + "%")
                        .setCancelable(false)
                        .setPositiveButton("Back to challenges", (dialog, which) -> finish())
                        .show();
            });
        });
    }
}
