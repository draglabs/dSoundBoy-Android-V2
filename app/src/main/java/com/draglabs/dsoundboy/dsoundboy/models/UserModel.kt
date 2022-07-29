/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable

/**
 * Created by davrukin on 1/11/2018.
 * @author Daniel Avrukin
 */
@RealmClass
open class UserModel(
    @PrimaryKey var UUID: String?,
    var lat: String,
    var lng: String,
    var fbID: String,
    var fbAccessToken: String,
    var fbName: String,
    var fbEmail: String,
    var fbImage: String,
    var localPath: String
) : Serializable, RealmObject() {
    constructor() : this("", "", "", "", "", "", "", "", "")

    override fun toString(): String {
        return "UUID: $UUID; lat: $lat; lng: $lng; fbID: $fbID; fbAccessToken: $fbAccessToken; fbName: $fbName; fbEmail: $fbEmail; fbImage: $fbImage; localPath: $localPath"
    }
}