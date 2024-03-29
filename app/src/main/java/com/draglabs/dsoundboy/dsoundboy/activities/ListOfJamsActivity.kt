  /*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.routines.ListOfJamsRoutine
import com.draglabs.dsoundboy.dsoundboy.utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.utils.PrefUtilsKt
import com.draglabs.dsoundboy.dsoundboy.utils.RealmUtils
import com.draglabs.dsoundboy.dsoundboy.view_adapters.CustomAdapter
import io.realm.Realm
import io.realm.RealmResults

  /**
 * Shows the cards for all the jams
 * @author Daniel Avrukin
 */
class ListOfJamsActivity : AppCompatActivity() {

    // TODO: use kotlin coroutine to run getJams() async and await response in order to populate the cards

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var customAdapter: CustomAdapter
    private lateinit var recyclerView: RecyclerView

    var realm: Realm = Realm.getDefaultInstance()
    private val REALM_TAG = "__REALM__"

    //private var progressBar = findViewById<ProgressBar>(R.id.export_progress_bar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_list_of_jams)
        //setSupportActionBar(toolbar)

        //val listOfJams = getJams(realm) // show the view, populate data as it shows

        setLayouts()
        setAdapters()
        setActivityTitle()

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


    private fun setLayouts() {
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        swipeRefreshLayout = SwipeRefreshLayout(this)
        swipeRefreshLayout.setOnRefreshListener { refreshItems() }
    }

    private fun setAdapters() {
        val listOfJams = getJams(realm)
        customAdapter = CustomAdapter(this, listOfJams!!)
        recyclerView.adapter = customAdapter
    }

    private fun setActivityTitle() {
        val usersName = RealmUtils.UserModelUtils.Retrieve.retrieveUser(realm).fbName
        LogUtils.debug("User's Name in Jams View", usersName)
        this.title = "$usersName's Jams"
    }

    private fun refreshItems() {
        // load items
        // show the view, populate data as it shows
        //val listOfJams = prepareList(jams)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager // TODO: doesn't refresh, does it indefinitely

        setAdapters()
        // load complete
        onItemsLoadComplete()
    }


    private fun onItemsLoadComplete() {
        // update the adapter and notify data set changed
        Toast.makeText(this, "Jams Updated", Toast.LENGTH_LONG).show()
        // stop refresh animation
        swipeRefreshLayout.isRefreshing = false
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

    private fun getJams(realm: Realm): ArrayList<JamViewModel>? {
        return ListOfJamsRoutine().getJams(this, realm)
    }

    private fun getDummyData(): Array<String?> {
        val jamID = PrefUtilsKt.Functions().retrieveJamID(this)
        val jamName = PrefUtilsKt.Functions().retrieveJamName(this)
        val jamLocation = "Starbucks Coffee, Redwood City, CA"
        val jamLink = PrefUtilsKt.Functions().retrieveLink(this)
        val jamNotes = "Hi"
        return arrayOf(jamID, jamName, jamLocation, jamLink, jamNotes)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun getSystemService(name: String): Any {
        return if (REALM_TAG == name) {
            realm
        } else {
            super.getSystemService(name)
        }
    }
}
