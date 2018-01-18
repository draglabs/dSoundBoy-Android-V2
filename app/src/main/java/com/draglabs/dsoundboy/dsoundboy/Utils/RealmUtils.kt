/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.Models.ResponseModelKt
import com.draglabs.dsoundboy.dsoundboy.Models.UserModel
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

/**
 * Created by davrukin on 1/11/2018.
 * @author Daniel Avrukin
 */
class RealmUtils {

    private object userVars {
        val UUID = "UUID"
        val LAT = "lat"
        val LNG = "lng"
        val FB_NAME = "fbName"
        val FB_EMAIL = "fbEmail"
        val FB_IMAGE = "fbImage"
    }

    private object jamVars {
        val ID = "id"
        val LINK = "link"
        val LOCATION = "location"
        val NAME = "name"
    }

    fun startRealm(): Realm {
        return Realm.getDefaultInstance()
    }

    fun initializeUserModel(realm: Realm, context: Context) {
        realm.beginTransaction()
        val person = realm.createObject(UserModel::class.java, 1)
        val prefUtils = PrefUtilsKt.Functions()
        person.UUID = prefUtils.retrieveUUID(context)
        // add other attributes here
        realm.commitTransaction()
    }

    fun initializeJamViewModel(realm: Realm, jamID: String, link: String, location: String, name: String, key: Int) {
        realm.beginTransaction()
        val jam = realm.createObject(JamViewModel::class.java, key)
        jam.jamID = jamID
        jam.link = link
        jam.location = location
        jam.name = name
        realm.commitTransaction()
    }

    fun storeJams(realm: Realm, jams: ArrayList<JamViewModel>) {
        realm.beginTransaction()

        for (jam in jams) {
            val realmJam = realm.createObject(JamViewModel::class.java)
            realmJam.jamID = jam.jamID
            realmJam.name = jam.name
            realmJam.location = jam.location
            realmJam.link = jam.link
        }

        realm.commitTransaction()
    }

    fun storeJams(jams: ArrayList<JamViewModel>) {
        val realm = startRealm()
        realm.beginTransaction()

        for (jam in jams) {
            val realmJam = realm.createObject(JamViewModel::class.java)
            realmJam.jamID = jam.jamID
            realmJam.name = jam.name
            realmJam.location = jam.location
            realmJam.link = jam.link
        }

        realm.commitTransaction()
        closeRealm(realm)
    }

    fun retrieveJam(realm: Realm, key: Int): JamViewModel {
        val jams = realm.where(JamViewModel::class.java).findAll()
        return jams[key]!!
    }

    fun retrieveJams(realm: Realm): RealmResults<JamViewModel>? {

        val jams = realm.where(JamViewModel::class.java).findAll()
        return jams
    }
    /**
     * Attributes able to edit: "name", "link", "location"
     */
    fun editJam(realm: Realm, key: Int, attribute: String, newValue: String) {
        val jams = realm.where(JamViewModel::class.java).findAll()
        val jam = jams[key]!! // makes sure that there exists the jam I want to access, currently non-null asserted call
        realm.beginTransaction()
        when (attribute) {
            "name" -> jam.name = newValue
            "link" -> jam.link = newValue
            "location" -> jam.location = newValue
        }
        realm.commitTransaction()
    }

    fun retrieveJamWithID(realm: Realm, jamID: String): JamViewModel {
        val jams = realm.where(JamViewModel::class.java).equalTo("jamID", jamID).findAll()
        val jam = jams[0]
        return jam!!
    }

    fun closeRealm(realm: Realm) {
        realm.close()
    }

    fun addUserToRealm(realm: Realm, attribute: String, value: String) {
        realm.executeTransaction {
            val user = realm.createObject(UserModel::class.java)
            when (attribute) {
                userVars.UUID -> user.UUID = value
                userVars.LAT -> user.lat = value
                userVars.LNG -> user.lng = value
                userVars.FB_NAME -> user.fbName = value
                userVars.FB_EMAIL -> user.fbEmail = value
                userVars.FB_IMAGE -> user.fbImage = value
            }
        }
        realm.close()
    }

    fun getAttributeFromUser(context: Context, realm: Realm, attribute: String) {
        val query = realm.where(UserModel::class.java) // TYPE: RealmQuery<UserModel>
        query.equalTo(userVars.UUID, PrefUtilsKt.Functions().retrieveUUID(context))
        val result = query.findFirst() // TYPE: RealmResults<UserModel>
    }

    fun addJamToRealm(realm: Realm, attribute: String, value: String) {
        realm.executeTransaction {
            val jam = realm.createObject(JamViewModel::class.java)
            when (attribute) {
                jamVars.ID -> jam.jamID = value
                jamVars.LINK -> jam.link = value
                jamVars.LOCATION -> jam.location = value
                jamVars.NAME -> jam.name = value
            }
        }
        realm.close()
    }

}