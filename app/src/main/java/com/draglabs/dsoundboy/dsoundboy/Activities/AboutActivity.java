package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Routines.AboutRoutine;

/**
 * Shows information about the company
 */
public class AboutActivity extends AppCompatActivity {

    /**
     * onCreate method for AboutActivity
     * @param savedInstanceState the state of the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        new AboutRoutine().doStuff((TextView)findViewById(R.id.companyURL));
    }
}
