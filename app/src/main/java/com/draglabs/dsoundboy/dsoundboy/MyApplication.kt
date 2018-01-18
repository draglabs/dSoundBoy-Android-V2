/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by davrukin on 1/11/2018.
 * @author Daniel Avrukin
 */

class MyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        initializeRealm()
    }

    private fun initializeRealm() {
        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this)

        val config = RealmConfiguration.Builder().build() // TYPE: RealmConfiguration // Creates the minimal configuration
        // Realm.getPath gets path of the Realm file

        Realm.setDefaultConfiguration(config)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
