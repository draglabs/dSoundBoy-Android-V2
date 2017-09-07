package com.draglabs.dsoundboy.dsoundboy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Contact extends AppCompatActivity {

    private Intent intent;
    private TextView contact;
    private Button send;

    private EditText email;
    private EditText subject;
    private EditText body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        intent = getIntent();
        contact = (TextView)findViewById(R.id.send_text);
        send = (Button)findViewById(R.id.send_email);

        email = (EditText)findViewById(R.id.band_email);
        subject = (EditText)findViewById(R.id.contact_subject);
        body = (EditText)findViewById(R.id.contact_body);
        email.setText(null);
        subject.setText(null);
        body.setText(null);
    }

    public void clickSend(View view) {
        String emailText = email.getText().toString();
        String subjectText = subject.getText().toString();
        String bodyText = body.getText().toString();

        if (emailText != null && subjectText != null && bodyText != null) { //TODO: shows "sending" even when null
            Snackbar.make(view, "Sending email!", Snackbar.LENGTH_LONG).show();
            sendEmail(emailText, subjectText, bodyText);
        } else if (emailText == null && (subjectText != null && bodyText != null)) {
            Snackbar.make(view, "Please enter an email address.", Snackbar.LENGTH_LONG).show();
        } else if (subjectText == null && (emailText != null && bodyText != null)) {
            Snackbar.make(view, "Please enter a subject.", Snackbar.LENGTH_LONG).show();
        } else if (bodyText == null && (subjectText != null && emailText != null)) {
            Snackbar.make(view, "Please enter a message.", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(view, "Please enter information.", Snackbar.LENGTH_LONG).show();
        }
    }

    private void sendEmail(String email, String subject, String body) {
        Toast.makeText(this, email + "\n" + subject + "\n" + body, Toast.LENGTH_LONG).show();

        // TODO: append [ANDROID] to subject line
        APIutils.sendEmail(email, "[ANDROID] " + subject, body);
        // TODO: send to Shyam a JSON object with HTTP POST
    }
}
