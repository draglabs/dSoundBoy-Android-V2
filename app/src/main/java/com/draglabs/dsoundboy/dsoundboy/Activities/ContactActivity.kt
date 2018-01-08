package com.draglabs.dsoundboy.dsoundboy.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText

import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Routines.ContactRoutine

/**
 * Lets the user send a support email to DragLabs
 * Created by davrukin
 * @author Daniel Avrukin
 */
class ContactActivity : AppCompatActivity() {

    private var email: EditText? = null
    private var subject: EditText? = null
    private var body: EditText? = null

    /**
     * onCreate method
     * @param savedInstanceState the saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        email = findViewById<View>(R.id.band_email) as EditText
        subject = findViewById<View>(R.id.contact_subject) as EditText
        body = findViewById<View>(R.id.contact_body) as EditText
        email!!.text = null
        subject!!.text = null
        body!!.text = null
    }

    /**
     * Listener function when the "Send" button is clicked
     * @param view the view calling the function
     */
    fun clickSend(view: View) {
        ContactRoutine().doStuff(this, view, email!!, subject!!, body!!)
    }
}
