package com.draglabs.dsoundboy.dsoundboy.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by davrukin on 12/27/2017.
 */

@Database(entities = {UserModel.class}, version=1)
@TypeConverters({DatabaseConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database").build();
        }
        return INSTANCE;
    }

    public static AppDatabase getINSTANCE() {
        return INSTANCE;
    }

    public static void setINSTANCE(AppDatabase INSTANCE) {
        AppDatabase.INSTANCE = INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
