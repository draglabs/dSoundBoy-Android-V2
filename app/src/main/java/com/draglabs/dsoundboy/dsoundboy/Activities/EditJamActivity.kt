/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutilsKt

/**
 * @author Daniel Avrukin
 */
class EditJamActivity : AppCompatActivity() {

    private lateinit var upperIntent: Intent

    private lateinit var jamID: String
    private lateinit var jamName: String
    private lateinit var jamLocation: String

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
    }

    private fun setListener() {
        doneButton.setOnClickListener {
            submitChanges()
            onBackPressed()
        }
    }

    private fun submitChanges() {
        APIutilsKt().performUpdateJam(jamID, enteredJamName.text.toString(), enteredJamLocation.text.toString(), enteredJamNotes.text.toString())
    }
}
