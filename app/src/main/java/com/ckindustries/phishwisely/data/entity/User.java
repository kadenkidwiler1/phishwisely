package com.ckindustries.phishwisely.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users",
        indices = {@Index(value = "email", unique = true), @Index("organizationId")},
        foreignKeys = {@ForeignKey(entity = Organization.class, parentColumns = "id",
                childColumns = "organizationId", onDelete = ForeignKey.RESTRICT)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String email;
    public String displayName;
    public String firstName;
    public String lastName;
    public long organizationId;
    public String passwordHash;
    public String passwordSalt;
    public long createdAt;

    public User(String email, String displayName, String firstName, String lastName,
                long organizationId, String passwordHash, String passwordSalt, long createdAt) {
        this.email = email;
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organizationId = organizationId;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.createdAt = createdAt;
    }
}
