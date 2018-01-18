/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Models

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
    @PrimaryKey var UUID: String,
    var lat: String,
    var lng: String,
    var fbName: String,
    var fbEmail: String,
    var fbImage: String,
    var localPath: String
) : Serializable, RealmObject() {
    constructor() : this("", "", "", "", "", "", "")
}