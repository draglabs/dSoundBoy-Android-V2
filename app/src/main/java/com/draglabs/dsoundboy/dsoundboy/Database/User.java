package com.draglabs.dsoundboy.dsoundboy.Database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by davrukin on 12/27/2017.
 */

@Entity(tableName = "user", indices = {@Index(value = {"uuid"}, unique = true)}) // are indices necessary?
public class User extends RoomDatabase {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "uuid")
    private String UUID;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "personal_email")
    private String personalEmail;

    @ColumnInfo(name = "facebook_email")
    private String facebookEmail;

    @ColumnInfo(name = "facebook_id")
    private String facebookID;

    @ColumnInfo(name = "facebook_access_token")
    private String facebookAccessToken;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getFacebookEmail() {
        return facebookEmail;
    }

    public void setFacebookEmail(String facebookEmail) {
        this.facebookEmail = facebookEmail;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public String getFacebookAccessToken() {
        return facebookAccessToken;
    }

    public void setFacebookAccessToken(String facebookAccessToken) {
        this.facebookAccessToken = facebookAccessToken;
    }

    /**
     * Creates the open helper to access the database. Generated class already implements this
     * method.
     * Note that this method is called when the RoomDatabase is initialized.
     *
     * @param config The configuration of the Room database.
     * @return A new SupportSQLiteOpenHelper to be used while connecting to the database.
     */
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    /**
     * Called when the RoomDatabase is created.
     * <p>
     * This is already implemented by the generated code.
     *
     * @return Creates a new InvalidationTracker.
     */
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
