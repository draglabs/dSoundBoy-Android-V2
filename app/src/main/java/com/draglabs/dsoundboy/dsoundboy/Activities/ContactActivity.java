package com.draglabs.dsoundboy.dsoundboy.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Routines.ContactRoutine;

/**
 * Lets the user send a support email to DragLabs
 * Created by davrukin
 */
public class ContactActivity extends AppCompatActivity {

    private EditText email;
    private EditText subject;
    private EditText body;

    /**
     * onCreate method
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        email = (EditText)findViewById(R.id.band_email);
        subject = (EditText)findViewById(R.id.contact_subject);
        body = (EditText)findViewById(R.id.contact_body);
        email.setText(null);
        subject.setText(null);
        body.setText(null);
    }

    /**
     * Listener function when the "Send" button is clicked
     * @param view the view calling the function
     */
    public void clickSend(View view) {
        new ContactRoutine().doStuff(this, view, email, subject, body);
    }
}
