package com.ckindustries.phishwisely.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.ckindustries.phishwisely.data.entity.User;
import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User findById(long id);
    @Query("SELECT * FROM users ORDER BY displayName COLLATE NOCASE")
    List<User> getAll();
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(User user);
    @Update int update(User user);
    @Delete int delete(User user);
}
