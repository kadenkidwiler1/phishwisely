package com.ckindustries.phishwisely.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.ckindustries.phishwisely.data.entity.Organization;

@Dao
public interface OrganizationDao {
    @Query("SELECT * FROM organizations WHERE signupCode = :code LIMIT 1")
    Organization findBySignupCode(String code);

    @Query("SELECT * FROM organizations WHERE id = :id LIMIT 1")
    Organization findById(long id);

    @Insert long insert(Organization organization);
}
