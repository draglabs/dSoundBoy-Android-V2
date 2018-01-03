/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Activities

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.ViewAdapters.CustomAdapter
import kotlinx.android.synthetic.main.activity_list_of_jams.*
import java.util.*

class ListOfJamsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_list_of_jams)
        setSupportActionBar(toolbar)

        val list = ArrayList<JamViewModel>()
        prepareList(list)

        val cardView = findViewById<RecyclerView>(R.id.card_view)

        val adapter = CustomAdapter(this, list)
        cardView.adapter = adapter

        val orientation: Int = resources.configuration.orientation

        cardView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cardView.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            cardView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun prepareList(list: ArrayList<JamViewModel>) {
        // TODO: add items to list by doing API call to GetUserActivity, displaying jams, and mapping the buttons to the pertinent functions
        list.add((JamViewModel("This is a test jam")))
        list.add((JamViewModel("The buttons do nothing")))
    }
}
