/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable

/**
 * Created by davrukin on 2/5/2018.
 * @author Daniel Avrukin
 */

@RealmClass
open class RecordingModel(
    @PrimaryKey var filePath: String,
    var jamID: String,
    var jamName: String,
    var startTime: String,
    var endTime: String,
    var didUpload: Boolean
) : Serializable, RealmObject() {
    constructor() : this("", "", "", "", "", false)

    override fun toString(): String {
        return "filePath: $filePath; jamID: $jamID; jamName: $jamName; startTime: $startTime; endTime: $endTime; didUpload: $didUpload"
    }
}