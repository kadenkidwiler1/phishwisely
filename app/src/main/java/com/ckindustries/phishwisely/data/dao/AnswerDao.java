package com.ckindustries.phishwisely.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import com.ckindustries.phishwisely.data.entity.AnswerRecord;
import java.util.List;

@Dao
public interface AnswerDao {
    @Insert void insertAll(List<AnswerRecord> answers);
}
