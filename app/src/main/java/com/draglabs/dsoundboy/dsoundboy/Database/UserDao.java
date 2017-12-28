package com.draglabs.dsoundboy.dsoundboy.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by davrukin on 12/27/2017.
 */

@Dao
public interface UserDao {

    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("SELECT * FROM User where name LIKE :name")
    User findByName(String name);

    @Query("SELECT * FROM User where facebookID like :facebook_id")
    User findByFacebookID(String facebookID);

    @Query("SELECT * FROM User where UUID like :uuid")
    User findByUUID(String UUID);

    @Query("SELECT COUNT (*) from User")
    int countUsers();

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

}
