package com.draglabs.dsoundboy.dsoundboy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class About extends AppCompatActivity {

    private Intent intent;
    private TextView about;
    private TextView companyURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Get the Intent that started this activity and extract the string
        intent = getIntent();

        // Capture the layout's TextView and set the string as its text
        about = (TextView)findViewById(R.id.about);

        /*companyURL = (TextView)findViewById(R.id.companyURL);
        companyURL.setClickable(true);
        companyURL.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.draglabs.com'>draglabs.com</a>";
        companyURL.setText(Html.fromHtml(text));*/

    }
}
