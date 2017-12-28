package com.draglabs.dsoundboy.dsoundboy.Database;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

/**
 * Created by davrukin on 12/27/2017.
 */

public class DatabaseInitializer {

    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static User addUser(final AppDatabase db, User user) {
        db.userDao().insertAll(user);
        return user;
    }

    private static void populateWithTestData(AppDatabase db) {
        User user = new User();
        user.setName("John Smith");
        user.setPersonalEmail("john.smith@contoso.net");
        addUser(db, user);

        List<User> users = db.userDao().getAllUsers();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + users.size());
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final AppDatabase db;

        PopulateDbAsync(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(this.db);
            return null;
        }
    }
}
