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

    public static UserModel createAndAddUserWithUUID(final AppDatabase db, String UUID) {
        UserModel userModel = new UserModel();
        userModel.setUUID(UUID);
        db.userDao().insert(userModel);

        List<UserModel> userModels = db.userDao().getAllUsers();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + userModels.size());
        return userModel;
    }

    public static UserModel addUUIDtoUser(final AppDatabase db, UserModel userModel, String UUID) {
        userModel.setUUID(UUID);
        db.userDao().insert(userModel);

        List<UserModel> userModels = db.userDao().getAllUsers();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + userModels.size());
        return userModel;
    }

    private static UserModel addUser(final AppDatabase db, UserModel userModel) {
        db.userDao().insertAll(userModel);

        List<UserModel> userModels = db.userDao().getAllUsers();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + userModels.size());
        return userModel;
    }

    private static void populateWithTestData(AppDatabase db) {
        UserModel userModel = new UserModel();
        userModel.setName("John Smith");
        userModel.setPersonalEmail("john.smith@contoso.net");
        addUser(db, userModel);

        List<UserModel> userModels = db.userDao().getAllUsers();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + userModels.size());
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
