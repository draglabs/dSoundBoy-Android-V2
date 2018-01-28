/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.Serializable

/**
 * Created by davrukin on 1/3/2018.
 * @author Daniel Avrukin
 */
@RealmClass
open class JamViewModel(
       // @PrimaryKey var id: Int,
        @PrimaryKey var jamID: String,
        var name: String,
        var location: String,
        var link: String,
        var notes: String,
        var pin: String
) : Serializable, RealmObject() {
    constructor() : this("", "", "", "", "", "")

    override fun toString(): String {
        return "jamID: $jamID; name: $name; location: $location; link: $link; notes: $notes; pin: $pin"
    }
}