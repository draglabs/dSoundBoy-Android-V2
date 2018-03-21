package com.draglabs.dsoundboy.dsoundboy.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.draglabs.dsoundboy.dsoundboy.R

/**
 * Allows band to enter info about itself. Not currently used.
 */
class EnterInfoActivity : AppCompatActivity() {

    private var email: EditText? = null
    private var description: EditText? = null
    private var artistName: EditText? = null
    private var venue: EditText? = null

    /**
     * onCreate method
     * @param savedInstanceState the saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_info)

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeView()
    }

    /**
     * Initializes all the EditTexts
     */
    private fun initializeView() {
        email = findViewById<View>(R.id.band_email_text) as EditText
        description = findViewById<View>(R.id.band_description_text) as EditText
        artistName = findViewById<View>(R.id.artist_name_text) as EditText
        venue = findViewById<View>(R.id.venue_text) as EditText

        Toast.makeText(this, email.toString(), Toast.LENGTH_LONG).show()
        Toast.makeText(this, description.toString(), Toast.LENGTH_LONG).show()
        Toast.makeText(this, artistName.toString(), Toast.LENGTH_LONG).show()
        Toast.makeText(this, venue.toString(), Toast.LENGTH_LONG).show()
        //email!!.setText(PrefUtils.getArtistEmail(this))
        //description!!.setText(PrefUtils.getRecordingDescription(this))
        //artistName!!.setText(PrefUtils.getArtistName(this))
        //venue!!.setText(PrefUtils.getRecordingVenue(this))
    }

    /**
     * Saves band info to app storage
     * @param view the view calling this method
     */
    fun clickSave(view: View) {
        val artistEmailText = email!!.text.toString()
        val recordingDescriptionText = description!!.text.toString()
        val artistNameText = artistName!!.text.toString()
        val recordingVenueText = venue!!.text.toString()

        //val bandInfoModel = BandInfoModel(artistEmailText, recordingDescriptionText, artistNameText, recordingVenueText)
        //PrefUtils.setBandInfo(this, bandInfoModel) // no longer needed

        val sendBandInfo = Intent(this, TestNavActivity::class.java)

        Toast.makeText(this, "Band Data Saved.", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "Go back and click \"Submit\" when done recording.", Toast.LENGTH_SHORT).show()
        startActivity(sendBandInfo)
    }
}
