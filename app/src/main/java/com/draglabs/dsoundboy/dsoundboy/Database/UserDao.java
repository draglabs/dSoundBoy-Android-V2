/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2017. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by davrukin on 12/27/2017.
 * @author Daniel Avrukin
 *
 */

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<UserModel> getAllUsers();

    @Query("SELECT * FROM user where name LIKE :name")
    UserModel findByName(String name);

    @Query("SELECT * FROM user where facebookID like :facebookID")
    UserModel findByFacebookID(String facebookID);

    @Query("SELECT * FROM user where uuid like :uuid")
    UserModel findByUUID(String uuid);

    @Query("SELECT COUNT (*) from user")
    int countUsers();

    @Insert
    void insertAll(UserModel... userModels);

    @Insert
    void insert(UserModel userModel);

    @Delete
    void delete(UserModel userModel);

}
