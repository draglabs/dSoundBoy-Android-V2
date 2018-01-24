/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ProgressBar
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.Routines.ListOfJamsRoutine
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtilsKt
import com.draglabs.dsoundboy.dsoundboy.Utils.RealmUtils
import com.draglabs.dsoundboy.dsoundboy.ViewAdapters.CustomAdapter
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_list_of_jams.*
import java.util.*

class ListOfJamsActivity : AppCompatActivity() {

    // TODO: use kotlin coroutine to run getJams() async and await response in order to populate the cards

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var customAdapter: CustomAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var realm: Realm

    //private var progressBar = findViewById<ProgressBar>(R.id.export_progress_bar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_list_of_jams)
        setSupportActionBar(toolbar)

        realm = Realm.getDefaultInstance()
        val jams = getJams(realm) // show the view, populate data as it shows
        val listOfJams = prepareList(jams)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        customAdapter = CustomAdapter(this, listOfJams)
        recyclerView.adapter = customAdapter

        /*val cardView = findViewById<RecyclerView>(R.id.card_view) // type error here, CardView vs. RecyclerView; perform more research

        val adapter = CustomAdapter(this, list)
        cardView.adapter = adapter

        val orientation: Int = resources.configuration.orientation

        cardView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cardView.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            cardView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        }*/
    }

    private fun prepareList(jams: RealmResults<JamViewModel>?): ArrayList<JamViewModel> {
        // TODO: go into custom view adapter or inflater to set the buttons and views to the pertinent functions with the pertinent variables
        //list.add((JamViewModel("This is a test jam", "name", "location", "link")))
        //list.add((JamViewModel("The buttons do nothing", "name", "location", "link")))
        val list = ArrayList<JamViewModel>() // TODO: for this method, maybe make it suspend -> async so that it populates after it has loaded for a better ux?
        //list.add(JamViewModel("jamID", "name", "location", "link"))
        //val dummyArray = getDummyData()
        //list.add(JamViewModel(dummyArray[0], dummyArray[1], dummyArray[2], dummyArray[3]. dummyData[4]))

        if (jams != null) {
            val reversedJams = jams.reversed()
            // does this operation take a lot of time? maybe add the results in the other order instead?
            list += reversedJams
        }

        return list
    }

    private fun getJams(realm: Realm): RealmResults<JamViewModel>? {
        return ListOfJamsRoutine().getJams(realm, this)
    }

    private fun getDummyData(): Array<String> {
        val jamID = PrefUtilsKt.Functions().retrieveJamID(this)
        val jamName = PrefUtilsKt.Functions().retrieveJamName(this)
        val jamLocation = "Starbucks Coffee, Redwood City, CA"
        val jamLink = PrefUtilsKt.Functions().retrieveLink(this)
        val jamNotes = "Hi"
        return arrayOf(jamID, jamName, jamLocation, jamLink, jamNotes)
    }
}
