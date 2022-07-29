package com.draglabs.dsoundboy.dsoundboy.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.draglabs.dsoundboy.dsoundboy.R
import com.draglabs.dsoundboy.dsoundboy.routines.AboutRoutine

/**
 * Shows information about the company
 * @author Daniel Avrukin
 */
class AboutActivity : AppCompatActivity() {

    /**
     * onCreate method for AboutActivity
     * @param savedInstanceState the state of the saved instance
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        AboutRoutine().doStuff(findViewById<View>(R.id.companyURL) as TextView)
    }
}
