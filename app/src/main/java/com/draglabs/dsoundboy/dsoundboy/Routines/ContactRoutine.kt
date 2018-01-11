package com.draglabs.dsoundboy.dsoundboy.Routines

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.EditText
import android.widget.Toast

/**
 * Sends an email to accounts@draglabs.com
 * Created by davrukin on 11/1/17.
 */
class ContactRoutine {

    /**
     * Checks boolean values of text and sends when EditTexts are completed
     * @param context the app context
     * @param view the app view
     * @param email the email address from which it's being sent
     * @param subject the email subject
     * @param body the email body
     */
    fun doStuff(context: Context, view: View, email: EditText, subject: EditText, body: EditText) {
        val emailText = email.text.toString()
        val subjectText = subject.text.toString()
        val bodyText = body.text.toString()

        if (emailText != null && subjectText != null && bodyText != null) { //TODO: shows "sending" even when null
            Snackbar.make(view, "Sending email!", Snackbar.LENGTH_LONG).show()
            sendEmail(context, emailText, subjectText, bodyText)
        } else if (emailText == null && subjectText != null && bodyText != null) {
            Snackbar.make(view, "Please enter an email address.", Snackbar.LENGTH_LONG).show()
        } else if (subjectText == null && emailText != null && bodyText != null) {
            Snackbar.make(view, "Please enter a subject.", Snackbar.LENGTH_LONG).show()
        } else if (bodyText == null && subjectText != null && emailText != null) {
            Snackbar.make(view, "Please enter a message.", Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(view, "Please enter information.", Snackbar.LENGTH_LONG).show()
        }
    }

    /**
     * Sends the email after EditTexts have been filled
     * @param context the app context
     * @param email the email address from which it's being sent
     * @param subject the email subject
     * @param body the email body
     */
    private fun sendEmail(context: Context, email: String, subject: String, body: String) {
        Toast.makeText(context, email + "\n" + subject + "\n" + body, Toast.LENGTH_LONG).show()

        //APIutils.sendEmail(email, "[ANDROID] " + subject, body)
    }

}