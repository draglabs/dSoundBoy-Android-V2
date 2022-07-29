package com.draglabs.dsoundboy.dsoundboy.routines

import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView

/**
 * Shows DragLabs About information
 * Created by davrukin on 11/1/17.
 * @author Daniel Avrukin
 */
class AboutRoutine {

    /**
     * Makes the text of the TextView clickable
     * <p>
     * Opens a browser window
     */
    fun doStuff(companyURL: TextView) {
        companyURL.isClickable = true
        companyURL.movementMethod = LinkMovementMethod.getInstance()
        val text = "<a href='http://www.draglabs.com'>draglabs.com</a>"
        @Suppress("DEPRECATION")
        companyURL.text = Html.fromHtml(text)
    }
}