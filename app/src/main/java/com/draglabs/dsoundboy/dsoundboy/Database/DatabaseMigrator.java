package com.draglabs.dsoundboy.dsoundboy.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by davrukin on 12/28/2017.
 */

public class DatabaseMigrator {

    public DatabaseMigrator(Context context) {
        Room.databaseBuilder(context, AppDatabase.class, "user").addMigrations(MIGRATION_1_2, MIGRATION_2_3).build();
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `user` (`id` INTEGER, " + "`name` TEXT, PRIMARY KEY(`id`))");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user " + "ADD COLUMN ");
        }
    };

    public static Migration getMigration12() {
        return MIGRATION_1_2;
    }

    public static Migration getMigration23() {
        return MIGRATION_2_3;
    }
}
