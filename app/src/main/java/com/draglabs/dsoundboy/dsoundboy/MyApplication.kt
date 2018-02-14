/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration

/**
 * Created by davrukin on 1/11/2018.
 * @author Daniel Avrukin
 */

class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initializeFacebook()
        initializeRealm()
    }

    private fun initializeFacebook() {
        FacebookSdk.setApplicationId(getString(R.string.com_facebook_sdk_ApplicationId))
        @Suppress("DEPRECATION")
        FacebookSdk.sdkInitialize(applicationContext)
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        }
        AppEventsLogger.activateApp(this)
    }

    private fun initializeRealm() {
        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this)

        /*val config = RealmConfiguration.Builder()
            .schemaVersion(1)
            .migration(MyMigration())
            .build()*/ // TYPE: RealmConfiguration // Creates the minimal configuration

        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        // Realm.getPath gets path of the Realm file

        Realm.setDefaultConfiguration(config)
    }

    class MyMigration: RealmMigration {
        override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
            val schema = realm!!.schema

            if (oldVersion.equals(0)) {
                schema.create("JamViewModel")
                    .addField("notes", String::class.java)
                //oldVersion++
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
