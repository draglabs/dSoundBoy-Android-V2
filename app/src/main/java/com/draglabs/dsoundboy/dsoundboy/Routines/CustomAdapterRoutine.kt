/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Routines

import android.content.Context
import android.widget.Toast
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutilsKt
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtilsKt

/**
 * Created by davrukin on 1/18/18.
 * @author Daniel Avrukin
 */
class CustomAdapterRoutine {

    fun clickEdit(context: Context, jamID: String) {
        Toast.makeText(context, "Edit Button Clicked", Toast.LENGTH_LONG).show()
        // TODO: open up a new activity that shows the edit fields, and saves them by calling the UpdateJam API
        // TODO: run edit jam api function
    }

    fun clickExport(context: Context, jamID: String, link: String) {
        Toast.makeText(context, "Export Button Clicked", Toast.LENGTH_LONG).show()
        // TODO: calls the Compressor API to generate a link if this one is "not working", otherwise send that link, maybe append extra info text to is
        val newLink = checkLink(context, jamID, link)
    }

    fun clickShare(context: Context, jamID: String, link: String) {
        Toast.makeText(context, "Share Button Clicked", Toast.LENGTH_LONG).show()
        // TODO: add a share menu where I can share to multiple apps
        val newLink = checkLink(context, jamID, link)
    }

    private fun checkLink(context: Context, jamID: String, link: String): String {
        if (link == "not working") {
            // TODO: run it through the get jam details for the appropriate jam and extract that link
            APIutilsKt().performGetJamDetails(context, jamID)
            val newLink = PrefUtilsKt.Functions().retrieveLink(context)
            LogUtils.debug("Retrieved Link", newLink)
            return newLink // this return is currently just temporary
        } else {
            return link
        }
    }

}