/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import android.os.Environment
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.Models.RecordingModel
import com.draglabs.dsoundboy.dsoundboy.Models.UserModel
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import java.io.File
import java.util.*

/**
 * Created by davrukin on 1/11/2018.
 * @author Daniel Avrukin
 */
class RealmUtils {

    object UserVars {
        const val UUID = "UUID"
        const val LAT = "lat"
        const val LNG = "lng"
        const val FB_ID = "fbID"
        const val FB_ACCESSTOKEN = "fbAccessToken"
        const val FB_NAME = "fbName"
        const val FB_EMAIL = "fbEmail"
        const val FB_IMAGE = "fbImage"
    }

    object JamVars {
        const val JAM_ID = "jamID"
        const val LINK = "link"
        const val LOCATION = "location"
        const val NAME = "name"
        const val NOTES = "notes"
        const val PIN = "pin"
    }

    object JamViewModelUtils {
        object Initialize {
            fun initializeJamViewModel(jamID: String, link: String, location: String, name: String, notes: String, pin: String) {
                val realm = Functions.startRealm()

                val jam = realm.createObject(JamViewModel::class.java, jamID)
                jam.jamID = jamID
                jam.link = link
                jam.location = location
                jam.name = name
                jam.notes = notes
                jam.pin = pin

                Functions.closeRealm(realm)
            }
        }
        object Store {
            // DO NOT MODIFY //
            // DO NOT MODIFY //
            // DO NOT MODIFY //
            fun storeJams(jams: ArrayList<JamViewModel>) {
                val realm = Functions.startRealm()

                for (jam in jams) {
                    val testObject = realm.where(JamViewModel::class.java).equalTo(JamVars.JAM_ID, jam.jamID).findFirst()
                    if (testObject == null) {
                        val realmJam = realm.createObject(JamViewModel::class.java, jam.jamID)
                        //realmJam.jamID = jam.jamID
                        realmJam.name = jam.name
                        realmJam.location = jam.location
                        realmJam.link = jam.link
                        realmJam.notes = jam.notes
                        realmJam.pin = jam.pin
                    } else {
                        testObject.name = jam.name
                        testObject.location = jam.location
                        testObject.link = jam.link
                        testObject.notes = jam.notes
                        testObject.pin = jam.pin
                    }
                }

                Functions.closeRealm(realm)
            }

            fun storeJam(jam: JamViewModel) {
                val realm = Functions.startRealm()

                val testObject = realm.where(JamViewModel::class.java).equalTo(JamVars.JAM_ID, jam.jamID).findFirst()
                if (testObject == null) {
                    val realmJam = realm.createObject(JamViewModel::class.java, jam.jamID)
                    //realmJam.jamID = jam.jamID
                    realmJam.name = jam.name
                    realmJam.location = jam.location
                    realmJam.link = jam.link
                    realmJam.notes = jam.notes
                    realmJam.pin = jam.pin
                } else {
                    testObject.name = jam.name
                    testObject.location = jam.location
                    testObject.link = jam.link
                    testObject.notes = jam.notes
                    testObject.pin = jam.pin
                }

                Functions.closeRealm(realm)
            }

            fun addJamToRealm(attribute: String, value: String) {
                val realm = Functions.startRealm()

                val jam = realm.createObject(JamViewModel::class.java)
                when (attribute) {
                    JamVars.JAM_ID -> jam.jamID = value
                    JamVars.LINK -> jam.link = value
                    JamVars.LOCATION -> jam.location = value
                    JamVars.NAME -> jam.name = value
                    JamVars.NOTES -> jam.notes = value
                    JamVars.PIN -> jam.pin = value
                }

                Functions.closeRealm(realm)
            }
        }
        object Retrieve {
            fun retrieveJam(jamID: String): JamViewModel {
                val realm = Functions.startRealm()

                val jam = realm.where(JamViewModel::class.java).equalTo(JamVars.JAM_ID, jamID).findFirst()!!

                Functions.closeRealm(realm)
                return jam
            }

            fun retrieveJams(realm: Realm): ArrayList<JamViewModel>? {
                //val realm = Functions.startRealm()

                //val jams = realm.where(JamViewModel::class.java).findAll()

                //Functions.closeRealm(realm)

                //val list = ArrayList<JamViewModel>()
                /*if (jams != null) {
                    val reversedJams = jams.reversed()
                    list += reversedJams
                }*/

                /*val currentJamID = PrefUtilsKt.Functions().retrieveJamID(context)
                val currentJam = RealmUtils.JamViewModelUtils.Retrieve.retrieveJamWithID(currentJamID)*/

                val list = ArrayList<JamViewModel>()
                //val currentRealm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    val jams = it.where(JamViewModel::class.java).findAll()
                    if (jams != null) {
                        val reversedJams = jams.reversed()
                        list += reversedJams
                    }
                }
                //currentRealm.close()

                return list
            }

            fun retrieveJamPinWithID(jamID: String): String {
                val realm = Functions.startRealm()

                val jam = retrieveJamWithID(realm, jamID)

                Functions.closeRealm(realm)
                return jam.pin
            }

            fun retrieveJamWithID(realm: Realm, jamID: String): JamViewModel {
                return realm.where(JamViewModel::class.java).equalTo(JamVars.JAM_ID, jamID).findFirst()!!
            }

            fun retrieveJamWithID(jamID: String): JamViewModel {
                val realm = Functions.startRealm()

                val jam = realm.where(JamViewModel::class.java).equalTo(JamVars.JAM_ID, jamID).findFirst()!!

                Functions.closeRealm(realm)
                return jam
            }
        }
        object Edit {
            /**
             * Attributes able to edit: "name", "link", "location"
             */
            fun editJam(jamID: String, attribute: String, newValue: String) {
                //val jam = jams // makes sure that there exists the jam I want to access, currently non-null asserted call
                val realm = Functions.startRealm()

                val jam = JamViewModelUtils.Retrieve.retrieveJamWithID(realm, jamID)
                when (attribute) {
                    JamVars.NAME -> jam.name = newValue
                    JamVars.LINK -> jam.link = newValue
                    JamVars.LOCATION -> jam.location = newValue
                    JamVars.NOTES -> jam.notes = newValue
                    JamVars.PIN -> jam.pin = newValue

                }
                Functions.closeRealm(realm)
            }

            fun editJam(jamID: String, jamName: String, jamLocation: String, jamLink: String, jamNotes: String, jamPIN: String) {
                val realm = Functions.startRealm()

                val jam = realm.where(JamViewModel::class.java).equalTo(JamVars.JAM_ID, jamID).findFirst()
                jam!!.name = jamName
                jam.location = jamLocation
                jam.link = jamLink
                jam.notes = jamNotes
                jam.pin = jamPIN

                Functions.closeRealm(realm)
            }
        }
    }

    object UserModelUtils {
        object Initialize {

            fun initializeUserModel(realm: Realm, context: Context, fbID: String, fbAccessToken: String) {
                //val realm = Functions.startRealm()

                if (realm.isInTransaction) {
                    realm.commitTransaction()
                }

                realm.beginTransaction()

                val user = realm.where(UserModel::class.java).equalTo(UserVars.FB_ID, fbID).findFirst()
                if (user == null) { // if there is no such user
                    val newUser = realm.createObject(UserModel::class.java, PrefUtilsKt.Functions().retrieveUUID(context))
                    newUser.fbID = fbID
                    newUser.fbAccessToken = fbAccessToken
                } else { // otherwise modify the access token // TODO: add other fields?
                    user.fbAccessToken = fbAccessToken
                }

                realm.commitTransaction()

                //Functions.closeRealm(realm)
            }

            fun initializeUserModel(context: Context, fbID: String, fbAccessToken: String) {
                val realm = Functions.startRealm()

                val person = realm.createObject(UserModel::class.java)
                person.UUID = PrefUtilsKt.Functions().retrieveUUID(context) // also sets the user's primary key
                person.fbID = fbID
                person.fbAccessToken = fbAccessToken
                // add other attributes here

                Functions.closeRealm(realm)
            }
        }
        object Store {
            fun storeUUID(UUID: String) {
                val realm = Functions.startRealm()

                val user = realm.where(UserModel::class.java).findFirst()
                user!!.UUID = UUID

                Functions.closeRealm(realm)
            }

            fun addUserToRealm(attribute: String, value: String) {
                val realm = Functions.startRealm()

                val user = realm.createObject(UserModel::class.java)
                when (attribute) {
                    UserVars.UUID -> user.UUID = value
                    UserVars.LAT -> user.lat = value
                    UserVars.LNG -> user.lng = value
                    UserVars.FB_ID -> user.fbID = value
                    UserVars.FB_ACCESSTOKEN -> user.fbAccessToken = value
                    UserVars.FB_NAME -> user.fbName = value
                    UserVars.FB_EMAIL -> user.fbEmail = value
                    UserVars.FB_IMAGE -> user.fbImage = value
                }

                Functions.closeRealm(realm)
            }
        }
        object Retrieve {
            fun retrieveUser(realm: Realm): UserModel {
                //val realm = Functions.startRealm()

                val user = realm.where(UserModel::class.java).findFirst()

                //Functions.closeRealm(realm)

                return if (user != null) {
                    user
                } else {
                    UserModel()
                }
            }
        }
        object Edit {
            fun editUserFbInfoByUUID(UUID: String, fbID: String, fbAccessToken: String) {
                val realm = Functions.startRealm()

                val user = realm.where(UserModel::class.java).equalTo(UserVars.UUID, UUID).findFirst()
                user!!.fbID = fbID
                user.fbAccessToken = fbAccessToken

                Functions.closeRealm(realm)
            }
            fun editUserFbInfoByFbID(fbID: String, fbAccessToken: String) {
                val realm = Functions.startRealm()

                val user = realm.where(UserModel::class.java).equalTo(UserVars.FB_ID, fbID).findFirst()
                user!!.fbAccessToken = fbAccessToken

                Functions.closeRealm(realm)
            }
        }
    }

    object RecordingModelUtils {
        object Initialize {
            fun initializeRecordingModel(realm: Realm, filepath: String, jamID: String, jamName: String, startTime: String, endTime: String, didUpload: Boolean) {
                var realmNew = realm
                if (realmNew.isClosed) {
                    realmNew = Realm.getDefaultInstance()
                }
                LogUtils.debug("Entering Function", "initializeRecordingModel")
                realmNew.beginTransaction()

                val testObject = realmNew.where(RecordingModel::class.java).equalTo("filePath", filepath).findFirst()
                LogUtils.debug("testObject", "$testObject")
                if (testObject == null) {
                    val recording = realmNew.createObject(RecordingModel::class.java, filepath)
                    recording.jamID = jamID
                    recording.jamName = jamName
                    recording.startTime = startTime
                    recording.endTime = endTime
                    recording.didUpload = didUpload
                    LogUtils.debug("Initializing Recording Model", "$recording")
                    //createListener(recording, realmNew)
                } else {
                    testObject.jamID = jamID
                    testObject.jamName = jamName
                    testObject.startTime = startTime
                    testObject.endTime = endTime
                    testObject.didUpload = didUpload
                    LogUtils.debug("Initializing Recording Model", "$testObject")
                    //createListener(testObject, realmNew)
                }

                realmNew.commitTransaction()
                realmNew.close()
            }

            private fun createListener(recording: RecordingModel, realm: Realm) {
                recording.addChangeListener(RealmChangeListener {
                    if (recording.didUpload) {
                        val path = recording.filePath
                        Delete.deleteRecordingModel(recording)
                        recording.deleteFromRealm()
                        // doesn't necessarily need to be deleted, it can just be flagged away
                        // delete record if the file is not existing anymore locally
                    }
                })
                LogUtils.debug("Listener Created for Recording Model", "$recording")
            }
        }
        object Store {
            fun storeRecordingModel(realm: Realm, filepath: String, jamID: String, jamName: String, startTime: String, endTime: String, didUpload: Boolean) {
                LogUtils.debug("Entering Function", "storeRecordingModel")
                Initialize.initializeRecordingModel(realm, filepath, jamID, jamName, startTime, endTime, didUpload)
            }
        }
        object Retrieve {
            fun retrieveRecordingModel(realm: Realm, filepath: String): RecordingModel {
                realm.beginTransaction()

                val recording = realm.where(RecordingModel::class.java).equalTo("filePath", filepath).findFirst()

                return if (recording != null) {
                    val path = recording.filePath
                    val file = File(path)
                    if (file.exists()) {
                        realm.commitTransaction()
                        recording
                    } else {
                        recording.deleteFromRealm()
                        realm.commitTransaction()
                        RecordingModel()
                    }
                } else {
                    realm.commitTransaction()
                    RecordingModel() // return empty RecordingModel otherwise, so it's not null
                }

            }

            fun retrieveRecordingModels(realm: Realm): RealmResults<RecordingModel> {
                LogUtils.debug("Entering Function", "retrieveRecordingModels")
                //realm.beginTransaction()

                val recordings = realm.where(RecordingModel::class.java).findAll()
                //realm.commitTransaction()
                LogUtils.debug("Recording Models", "$recordings")

                return recordings // TODO: Start recording on headphone button press
            }

            fun retrieveRecordingModelByUploadStatus(realm: Realm, didUpload: Boolean): RealmResults<RecordingModel> {
                LogUtils.debug("Entering Function", "retrieveRecordingModelsByUploadStatus")
                //realm.beginTransaction()

                val recordings = realm.where(RecordingModel::class.java).beginGroup().equalTo("didUpload", didUpload).endGroup().findAll()
                LogUtils.debug("Retrieving Recording Models by Status", "$recordings")

                //realm.commitTransaction()

                return recordings
            }
        }
        object Edit {
            fun editRecordingModel() {

            }

            fun recordingDidUpload(realm: Realm, didUpload: Boolean) {
                realm.beginTransaction()

                val recording = realm.where(RecordingModel::class.java).equalTo("didUpload", didUpload).findFirst()

                if (recording != null) {
                    recording.didUpload = didUpload
                }

                realm.commitTransaction()
            }

            fun setRecordingAsUploaded(realm: Realm, filepath: String) {
                realm.beginTransaction()

                val recording = realm.where(RecordingModel::class.java).equalTo("filePath", filepath).findFirst()
                recording!!.didUpload = true
                LogUtils.debug("Recording Set As Uploaded", "$recording")

                realm.commitTransaction()
            }
        }
        object Delete {
            fun deleteRecordingModel(realm: Realm, filepath: String) {
                realm.beginTransaction()

                val recording = realm.where(RecordingModel::class.java).equalTo("filePath", filepath).findFirst()
                if (recording != null) {
                    val file = File(filepath)
                    file.delete()
                    recording.deleteFromRealm()
                }

                realm.commitTransaction()
            }

            fun deleteRecordingModel(recording: RecordingModel) {
                val file = File(recording.filePath)
                file.delete()
                LogUtils.debug("Recording Deleted From File & Realm", "$recording")
                //recording.deleteFromRealm()
            }
        }

        fun collectGarbage() {
            val realm = Realm.getDefaultInstance()

            val tag = "collectGarbage"
            LogUtils.logEnteringFunction(tag)
            val models = Retrieve.retrieveRecordingModels(realm)
            val size = models.size

            LogUtils.debug("$tag Models", "$models")
            LogUtils.debug("$tag Models Size", "$size")

            val rootRecordingPath = "${Environment.getExternalStorageDirectory()}/dSoundBoyRecordings/"
            val folder = File(rootRecordingPath)
            LogUtils.debug(tag, "Root Folder: $folder")
            folder.walk().forEach {
                val model = realm.where(RecordingModel::class.java).equalTo("filePath", it.path).findFirst()
                LogUtils.debug(tag, "Model Found: $model")
                if (model != null) {
                    val uploaded = model.didUpload
                    LogUtils.debug(tag, "Model Did Upload? $uploaded")
                    if (uploaded) {
                        LogUtils.debug(tag, "Deleting File: $it")
                        it.delete()
                        LogUtils.debug(tag, "File Deleted")
                        realm.executeTransaction { model.deleteFromRealm() }
                        LogUtils.debug(tag, "Removed model from Realm")
                    }
                }
            }

            for ((count, model) in models.withIndex()) {
                val filepath = model.filePath
                val file = File(filepath)
                val exists = file.exists()
                LogUtils.debug("$tag Model Exists?", "filepath: $file\nexists: $exists")
                if (!exists) {
                    LogUtils.debug("$tag Deleting Model", "count: $count\nfilepath: $filepath\nmodel: $model")
                    realm.executeTransaction { model.deleteFromRealm() }
                    LogUtils.debug("$tag Model Deleted", "count: $count")
                }
            }

            realm.close()
        }
    }

    object Functions {
        fun startRealm(): Realm {
            val realm = Realm.getDefaultInstance()
            if (realm.isInTransaction) {
                realm.commitTransaction()
            }
            realm.beginTransaction()
            return realm
        }

        fun closeRealm(realm: Realm) {
            if (realm.isInTransaction) {
                realm.commitTransaction()
            }
            if (!realm.isClosed) {
                realm.close()
            }
        }

        fun getAttributeFromUser(context: Context, realm: Realm, attribute: String) {
            val query = realm.where(UserModel::class.java) // TYPE: RealmQuery<UserModel>
            query.equalTo(UserVars.UUID, PrefUtilsKt.Functions().retrieveUUID(context))
            val result = query.findFirst() // TYPE: RealmResults<UserModel>
        }
    }





}