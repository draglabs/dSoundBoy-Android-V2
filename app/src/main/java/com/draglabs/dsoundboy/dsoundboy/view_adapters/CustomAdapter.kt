/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.view_adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.activities.ListOfJamsActivity
import com.draglabs.dsoundboy.dsoundboy.extensions.inflate
import com.draglabs.dsoundboy.dsoundboy.models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.routines.CustomAdapterRoutine
import com.draglabs.dsoundboy.dsoundboy.utils.LogUtils

/**
 * Created by davrukin on 1/3/2018.
 * @author Daniel Avrukin
 */
class CustomAdapter(private val context: Context, private val list: ArrayList<JamViewModel>): RecyclerView.Adapter<CustomAdapter.JamHolder>() {
// I guess this represents a single card
    class JamHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(view: View) {
            LogUtils.debug("RecyclerView", "Clicked!")
            val context = itemView.context
            val showCardIntent = Intent(context, ListOfJamsActivity::class.java)
            showCardIntent.putExtra(VIEW_KEY, jam)
            context.startActivity(showCardIntent)
        }

        var textJamInfo: TextView
        var editButton: Button
        var exportButton: Button
        var shareButton: ImageButton
        var pinView: TextView

        var view: View = itemView
        var jam: JamViewModel? = null

        init {
            textJamInfo = itemView.findViewById<TextView>(R.id.text_jam_info)
            editButton = itemView.findViewById<Button>(R.id.jam_view_edit_button)
            exportButton = itemView.findViewById<Button>(R.id.jam_view_export_button)
            shareButton = itemView.findViewById<ImageButton>(R.id.jam_view_share_button)
            pinView = itemView.findViewById<TextView>(R.id.text_recycler_jam_pin)
            // need api key to use map https://developers.google.com/maps/documentation/android-api/current-place-tutorial

            view.setOnClickListener(this)
        }

        fun bindJam(jamViewModel: JamViewModel) {
            this.jam = jamViewModel
            view.findViewById<TextView>(R.id.text_jam_info).text = jamViewModel.name
            view.findViewById<TextView>(R.id.text_recycler_jam_pin).text = jamViewModel.pin
            // add the other attributes here too
        }

        companion object {
            private const val VIEW_KEY = "JAM"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.JamHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return JamHolder(inflatedView)

        /*val view : View = LayoutInflater.from(parent.context).inflate(R.layout.activity_list_of_jams, parent, false)
        val card = view.findViewById(R.id.card_view) as CardView
        card.maxCardElevation = 2.0F
        card.radius = 5.0F
        return JamHolder(view)*/
    }

    override fun onBindViewHolder(holder: CustomAdapter.JamHolder, position: Int) {
        //var progressBar = view.findViewById<ProgressBar>(R.id.export_progress_bar)
        //progressBar.animate().cancel()
        val jam: JamViewModel? = list[position]
        if (jam != null) {
            holder.textJamInfo.text = jam.name
            holder.editButton.setOnClickListener { CustomAdapterRoutine().clickEdit(context, jam) }
            holder.exportButton.setOnClickListener { CustomAdapterRoutine().clickExport(context, jam.jamID, jam.link) }
            holder.shareButton.setOnClickListener { CustomAdapterRoutine().clickShare(context, jam) }
        }
        // TODO: call compressor api here with the user id and the jam id, retrieve the link, store it, and retrieve it when utilizing the share menu
        // TODO: edit opens up a dialog or a new activity where the fields can be edited and they then send an UpdateJam request
        // TODO: the export button sends an email?
        // TODO: need to write docs for the Compressor API

        holder.bindJam(jam!!)
    }

    private fun showPopupMenu(view: View) {
        // inflate menu
        val popup = PopupMenu(context, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.menu_list_of_jams, popup.menu)
        popup.setOnMenuItemClickListener(null)
        popup.show()
    }

    override fun getItemCount(): Int {
        return list.size
    }

}