/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.utils.APIutilsKt

/**
 * Shows a page the user can use to edit the current jam.
 * Editable fields include the jam's name, location, and notes.
 * Changes are synchronized with the dlsAPI and with the local Realm database.
 * @author Daniel Avrukin
 */
class EditJamActivity : AppCompatActivity() {

    private lateinit var upperIntent: Intent

    private var jamID: String? = null
    private var jamName: String? = null
    private var jamLocation: String? = null
    private var jamNotes: String? = null

    private lateinit var enteredJamName: EditText
    private lateinit var enteredJamLocation: EditText
    private lateinit var enteredJamNotes: EditText

    private lateinit var doneButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_jam)

        setValues()
        setFields()

        setText()
        setListener()
    }

    private fun setValues() {
        upperIntent = intent

        jamID = upperIntent.getStringExtra("jamID")
        jamName = upperIntent.getStringExtra("jamName")
        jamLocation = upperIntent.getStringExtra("jamLocation")
        jamNotes = upperIntent.getStringExtra("jamNotes")
    }

    private fun setFields() {
        enteredJamName = findViewById<EditText>(R.id.entered_jam_name)
        enteredJamLocation = findViewById<EditText>(R.id.entered_jam_location)
        enteredJamNotes = findViewById<EditText>(R.id.entered_jam_notes)

        doneButton = findViewById<Button>(R.id.edit_jam_button_done)
    }

    private fun setText() {
        enteredJamName.setText(jamName)
        enteredJamLocation.setText(jamLocation)
        enteredJamNotes.setText(jamNotes)
    }

    private fun setListener() {
        doneButton.setOnClickListener {
            submitChanges()
            onBackPressed()
        }
    }

    private fun submitChanges() {
        APIutilsKt.JamFunctions.performUpdateJam(jamID, enteredJamName.text.toString(), enteredJamLocation.text.toString(), enteredJamNotes.text.toString())
    }
}
