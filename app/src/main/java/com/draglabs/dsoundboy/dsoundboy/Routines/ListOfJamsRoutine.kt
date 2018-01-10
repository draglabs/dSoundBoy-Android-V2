/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Routines

import android.content.Context
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutilsKt
import com.draglabs.dsoundboy.dsoundboy.Utils.FileUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtilsKt
import java.util.*

/**
 * Created by davrukin on 1/9/2018.
 * @author Daniel Avrukin
 */
class ListOfJamsRoutine {

    fun getJams(context: Context): ArrayList<JamViewModel> {
        APIutilsKt().performGetUserActivity(context)

        val itemsList = PrefUtilsKt.Functions().retrieveJams(context)
        val list = FileUtils().stringToArrayList(itemsList)

        return list
    }

}