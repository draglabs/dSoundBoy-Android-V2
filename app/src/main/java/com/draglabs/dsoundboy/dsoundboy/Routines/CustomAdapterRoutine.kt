/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Routines

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.widget.ShareActionProvider
import android.widget.Toast
import com.draglabs.dsoundboy.dsoundboy.Activities.EditJamActivity
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutilsKt
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtilsKt

/**
 * Created by davrukin on 1/18/18.
 * @author Daniel Avrukin
 */
class CustomAdapterRoutine {

    fun clickEdit(context: Context, jam: JamViewModel) {
        Toast.makeText(context, "Edit Button Clicked", Toast.LENGTH_LONG).show()
        val intent = Intent(context, EditJamActivity::class.java)

        intent.putExtra("jamID", jam.jamID)
        intent.putExtra("jamName", jam.name)
        intent.putExtra("jamLocation", jam.location) // add notes field too
        intent.putExtra("jamNotes", jam.notes)

        context.startActivity(intent)
        // TODO: open up a new activity that shows the edit fields, and saves them by calling the UpdateJam API
        // TODO: run edit jam api function
    }

    fun clickExport(context: Context, jamID: String, link: String) {
        Toast.makeText(context, "Export Button Clicked", Toast.LENGTH_LONG).show()
        // TODO: calls the Compressor API to generate a link if this one is "not working", otherwise send that link, maybe append extra info text to is
        //val progressBar = export_progress_bar
        //progressBar.animate().start()

        LogUtils.debug("Export JamID", jamID)
        LogUtils.debug("Export Link", link)

        APIutilsKt.JamFunctions.getJamDetails(context, jamID)

        val newLink = checkLink(context, jamID, link)
        LogUtils.debug("NewLink upon first click", newLink)

        //progressBar.animate().cancel()
        Toast.makeText(context, "Email sent!", Toast.LENGTH_LONG).show()
    }

    fun clickShare(context: Context, jam: JamViewModel) {
        Toast.makeText(context, "Share Button Clicked", Toast.LENGTH_LONG).show()
        // TODO: add a share menu where I can share to multiple apps
        var newLink = checkLink(context, jam.jamID, jam.link)

        if (newLink == "") {
            newLink = "Link not yet exported."
        }

        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.data = Uri.parse("smsto:")
        sendIntent.type = "vnd.android-dir/mms-sms"
        sendIntent.putExtra("address", "(555) 555-5555")
        sendIntent.putExtra("sms_body", "Here's a link to your new jam! Text it to download anywhere. $newLink")
        context.startActivity(sendIntent)
    }

    private fun checkLink(context: Context, jamID: String, link: String): String {
        return if (link == "not working") {
            // TODO: run it through the get jam details for the appropriate jam and extract that link
            val newLink = performApiCalls(context, jamID, 1)
            return if (newLink == "") {
                APIutilsKt.JamFunctions.performCompressor(context, jamID)
                performApiCalls(context, jamID, 2)
            } else {
                newLink // this return is currently just temporary
            }
        } else {
            link
        }
    }

    private fun performApiCalls(context: Context, jamID: String, instance: Int): String {
        APIutilsKt.JamFunctions.getJamDetails(context, jamID)
        val newLink = PrefUtilsKt.Functions().retrieveLink(context) // add suspend & async
        LogUtils.debug("Retrieved Link $instance", newLink)
        return newLink
    }

}