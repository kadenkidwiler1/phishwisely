package com.ckindustries.phishwisely.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ckindustries.phishwisely.data.dao.AnswerDao;
import com.ckindustries.phishwisely.data.dao.AttemptDao;
import com.ckindustries.phishwisely.data.dao.ChallengeDao;
import com.ckindustries.phishwisely.data.dao.OrganizationDao;
import com.ckindustries.phishwisely.data.dao.QuestionDao;
import com.ckindustries.phishwisely.data.dao.UserDao;
import com.ckindustries.phishwisely.data.entity.AnswerRecord;
import com.ckindustries.phishwisely.data.entity.Challenge;
import com.ckindustries.phishwisely.data.entity.ChallengeAttempt;
import com.ckindustries.phishwisely.data.entity.Organization;
import com.ckindustries.phishwisely.data.entity.Question;
import com.ckindustries.phishwisely.data.entity.User;

@Database(entities = {Organization.class, User.class, Challenge.class, Question.class,
        ChallengeAttempt.class, AnswerRecord.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract OrganizationDao organizationDao();

    public abstract UserDao userDao();

    public abstract ChallengeDao challengeDao();

    public abstract QuestionDao questionDao();

    public abstract AttemptDao attemptDao();

    public abstract AnswerDao answerDao();

    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                                    "phishwisely.db")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    SeedData.populate(db);
                                }
                            }).build();
                }
            }
        }
        return INSTANCE;
    }
}
