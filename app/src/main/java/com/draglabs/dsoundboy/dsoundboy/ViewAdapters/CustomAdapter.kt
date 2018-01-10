/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.ViewAdapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.draglabs.dsoundboy.dsoundboy.Models.JamViewModel
import com.draglabs.dsoundboy.dsoundboy.R

/**
 * Created by davrukin on 1/3/2018.
 * @author Daniel Avrukin
 */
class CustomAdapter(private val context: Context, private val list: List<JamViewModel>): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
// I guess this represents a single card
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textJamInfo: TextView
        var editButton: Button
        var exportButton: Button
        var shareButton: ImageButton

        init {
            textJamInfo = itemView.findViewById<TextView>(R.id.text_jam_info)
            editButton = itemView.findViewById<Button>(R.id.jam_view_edit_button)
            exportButton = itemView.findViewById<Button>(R.id.jam_view_export_button)
            shareButton = itemView.findViewById<ImageButton>(R.id.jam_view_share_button)
            // need api key to use map https://developers.google.com/maps/documentation/android-api/current-place-tutorial
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.activity_list_of_jams, parent, false)
        val card = view.findViewById(R.id.card_view) as CardView
        card.maxCardElevation = 2.0F
        card.radius = 5.0F
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val jam: JamViewModel = list[position]
        holder.textJamInfo.text = jam.name
        // TODO: call compressor api here with the user id and the jam id, retrieve the link, store it, and retrieve it when utilizing the share menu
        // TODO: edit opens up a dialog or a new activity where the fields can be edited and they then send an UpdateJam request
        // TODO: the export button sends an email?
        // TODO: need to write docs for the Compressor API
        holder.editButton.setOnClickListener { Toast.makeText(context, "Edit Button Clicked", Toast.LENGTH_LONG).show() }
        holder.exportButton.setOnClickListener { Toast.makeText(context, "Export Button Clicked", Toast.LENGTH_LONG).show() }
        holder.shareButton.setOnClickListener { Toast.makeText(context, "Share Button Clicked", Toast.LENGTH_LONG).show() }
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