/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Routines

import android.content.Context
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.Utils.*
import io.realm.Realm
import io.realm.RealmResults

/**
 * Created by davrukin on 1/9/2018.
 * @author Daniel Avrukin
 */
class ListOfJamsRoutine {

    fun getJams(realm: Realm, context: Context): RealmResults<JamViewModel>? {
        //APIutilsKt().performGetUserActivityArray(context)
        APIutilsKt.UserFunctions.getUserActivity(context)
        //val itemsList = PrefUtilsKt.Functions().retrieveJams(context)
        //val realm = RealmUtils().startRealm()
        val itemsList = RealmUtils().retrieveJams(realm)
        LogUtils.debug("ItemsList in GetJams", itemsList.toString())
        //val list = FileUtils().stringToArrayList(itemsList)

        //realm.close() // error was saying that the instance was already closed making it unusable, therefore disabled this line
        return itemsList
    }

}