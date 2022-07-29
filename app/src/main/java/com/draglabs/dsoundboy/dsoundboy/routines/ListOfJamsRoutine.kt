/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.routines

import android.content.Context
import com.draglabs.dsoundboy.dsoundboy.models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.utils.APIutilsKt
import com.draglabs.dsoundboy.dsoundboy.utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.utils.RealmUtils
import io.realm.Realm

/**
 * Created by davrukin on 1/9/2018.
 * @author Daniel Avrukin
 */
class ListOfJamsRoutine {

    fun getJams(context: Context, realm: Realm): ArrayList<JamViewModel>? {
        //APIutilsKt().performGetUserActivityArray(context)
        APIutilsKt.UserFunctions.getUserActivity(context)
        //val itemsList = PrefUtilsKt.Functions().retrieveJams(context)
        //val realm = RealmUtils.Functions.startRealm()
        val itemsList = RealmUtils.JamViewModelUtils.Retrieve.retrieveJams(realm)
        LogUtils.debug("ItemsList in GetJams", itemsList.toString())
        //val list = FileUtils().stringToArrayList(itemsList)

        //realm.close() // error was saying that the instance was already closed making it unusable, therefore disabled this line
        return itemsList
    }

}