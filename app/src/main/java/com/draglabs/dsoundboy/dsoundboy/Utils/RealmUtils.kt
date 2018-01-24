/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.Models.UserModel
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

/**
 * Created by davrukin on 1/11/2018.
 * @author Daniel Avrukin
 */
class RealmUtils {

    private object UserVars {
        const val UUID = "UUID"
        const val LAT = "lat"
        const val LNG = "lng"
        const val FB_NAME = "fbName"
        const val FB_EMAIL = "fbEmail"
        const val FB_IMAGE = "fbImage"
    }

    private object JamVars {
        const val ID = "id"
        const val LINK = "link"
        const val LOCATION = "location"
        const val NAME = "name"
        const val NOTES = "notes"
    }

    fun startRealm(): Realm {
        return Realm.getDefaultInstance()
    }

    fun initializeUserModel(context: Context) {
        val realm = startRealm()
        realm.beginTransaction()

        val person = realm.createObject(UserModel::class.java)
        val prefUtils = PrefUtilsKt.Functions()
        person.UUID = prefUtils.retrieveUUID(context) // also sets the user's primary key
        // add other attributes here

        realm.commitTransaction()
        closeRealm(realm)
    }

    fun initializeUserModel(realm: Realm, context: Context) {
        realm.beginTransaction()
        val person = realm.createObject(UserModel::class.java)
        val prefUtils = PrefUtilsKt.Functions()
        person.UUID = prefUtils.retrieveUUID(context) // also sets the user's primary key
        // add other attributes here
        realm.commitTransaction()
    }

    fun initializeJamViewModel(jamID: String, link: String, location: String, name: String, notes: String) {
        val realm = startRealm()

        realm.beginTransaction()
        val jam = realm.createObject(JamViewModel::class.java, jamID)
        jam.jamID = jamID
        jam.link = link
        jam.location = location
        jam.name = name
        jam.notes = notes
        realm.commitTransaction()

        closeRealm(realm)
    }

    fun initializeJamViewModel(realm: Realm, jamID: String, link: String, location: String, name: String, notes: String) {
        realm.beginTransaction()
        val jam = realm.createObject(JamViewModel::class.java, jamID)
        jam.jamID = jamID
        jam.link = link
        jam.location = location
        jam.name = name
        jam.notes = notes
        realm.commitTransaction()
    }

    fun storeJams(jams: ArrayList<JamViewModel>) {
        val realm = startRealm()
        realm.beginTransaction()

        for (jam in jams) {
            val testObject = realm.where(JamViewModel::class.java).equalTo("jamID", jam.jamID).findFirst()
            if (testObject == null) {
                val realmJam = realm.createObject(JamViewModel::class.java, jam.jamID)
                //realmJam.jamID = jam.jamID
                realmJam.name = jam.name
                realmJam.location = jam.location
                realmJam.link = jam.link
                realmJam.notes = jam.notes
            } else {
                testObject.name = jam.name
                testObject.location = jam.location
                testObject.link = jam.link
                testObject.notes = jam.notes
            }
        }

        realm.commitTransaction()
        closeRealm(realm)
    }

    fun storeJams(realm: Realm, jams: ArrayList<JamViewModel>) {
        realm.beginTransaction()

        for (jam in jams) {
            val realmJam = realm.createObject(JamViewModel::class.java)
            realmJam.jamID = jam.jamID
            realmJam.name = jam.name
            realmJam.location = jam.location
            realmJam.link = jam.link
            realmJam.notes = jam.notes
        }

        realm.commitTransaction()
    }

    fun retrieveJam(realm: Realm, jamID: String): JamViewModel {
        return realm.where(JamViewModel::class.java).equalTo("jamID", jamID).findFirst()!!
    }

    fun retrieveJam(jamID: String): JamViewModel {
        val realm = startRealm()
        val jam = realm.where(JamViewModel::class.java).equalTo("jamID", jamID).findFirst()!!
        closeRealm(realm)
        return jam
    }

    fun retrieveJams(realm: Realm): RealmResults<JamViewModel>? {
        return realm.where(JamViewModel::class.java).findAll()
    }

    fun retrieveJams(): RealmResults<JamViewModel>? {
        val realm = startRealm()
        val jams = realm.where(JamViewModel::class.java).findAll()
        closeRealm(realm)
        return jams
    }
    /**
     * Attributes able to edit: "name", "link", "location"
     */
    fun editJam(realm: Realm, jamID: String, attribute: String, newValue: String) {
        val jam = retrieveJamWithID(realm, jamID)
        //val jam = jams // makes sure that there exists the jam I want to access, currently non-null asserted call
        realm.beginTransaction()
        when (attribute) {
            JamVars.NAME -> jam.name = newValue
            JamVars.LINK -> jam.link = newValue
            JamVars.LOCATION -> jam.location = newValue
            JamVars.NOTES -> jam.notes = newValue
        }
        realm.commitTransaction()
    }

    fun editJam(jamID: String, jamName: String, jamLocation: String, jamLink: String, jamNotes: String) {
        val realm = startRealm()
        realm.beginTransaction()

        val jam = realm.where(JamViewModel::class.java).equalTo("jamID", jamID).findFirst()
        jam!!.name = jamName
        jam.location = jamLocation
        jam.link = jamLink
        jam.notes = jamNotes

        realm.commitTransaction()
        closeRealm(realm)
    }

    fun retrieveJamWithID(realm: Realm, jamID: String): JamViewModel {
        return realm.where(JamViewModel::class.java).equalTo("jamID", jamID).findFirst()!!
    }

    fun closeRealm(realm: Realm) {
        realm.close()
    }

    fun addUserToRealm(realm: Realm, attribute: String, value: String) {
        realm.executeTransaction {
            val user = realm.createObject(UserModel::class.java)
            when (attribute) {
                UserVars.UUID -> user.UUID = value
                UserVars.LAT -> user.lat = value
                UserVars.LNG -> user.lng = value
                UserVars.FB_NAME -> user.fbName = value
                UserVars.FB_EMAIL -> user.fbEmail = value
                UserVars.FB_IMAGE -> user.fbImage = value
            }
        }
        realm.close()
    }

    fun getAttributeFromUser(context: Context, realm: Realm, attribute: String) {
        val query = realm.where(UserModel::class.java) // TYPE: RealmQuery<UserModel>
        query.equalTo(UserVars.UUID, PrefUtilsKt.Functions().retrieveUUID(context))
        val result = query.findFirst() // TYPE: RealmResults<UserModel>
    }

    fun addJamToRealm(realm: Realm, attribute: String, value: String) {
        realm.executeTransaction {
            val jam = realm.createObject(JamViewModel::class.java)
            when (attribute) {
                JamVars.ID -> jam.jamID = value
                JamVars.LINK -> jam.link = value
                JamVars.LOCATION -> jam.location = value
                JamVars.NAME -> jam.name = value
                JamVars.NOTES -> jam.notes = value
            }
        }
        realm.close()
    }

}