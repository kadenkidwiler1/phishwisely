package com.ckindustries.phishwisely.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "organizations", indices = {@Index(value = "signupCode", unique = true)})
public class Organization {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public String signupCode;

    public Organization(String name, String signupCode) {
        this.name = name;
        this.signupCode = signupCode;
    }
}
