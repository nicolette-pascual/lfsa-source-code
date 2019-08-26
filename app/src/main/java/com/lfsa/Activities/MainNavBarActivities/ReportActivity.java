package com.lfsa.Activities.MainNavBarActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lfsa.MainActivity;
import com.lfsa.R;

public class ReportActivity extends AppCompatActivity {

    EditText txtSubject, txtMessage;
    TextView txtReminder;
    Spinner spinner;
    String uid, customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setTitle("Send A Report");

        txtSubject = findViewById(com.lfsa_foodstallcrew.R.id.txtSubject);
        txtSubject.setEnabled(false);
        txtMessage = findViewById(com.lfsa_foodstallcrew.R.id.txtBody);
        txtReminder = findViewById(com.lfsa_foodstallcrew.R.id.txtReminder);
        spinner = findViewById(com.lfsa_foodstallcrew.R.id.spinner);

        customer = getIntent().getStringExtra("customer");

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        txtSubject.setEnabled(false);
                        txtSubject.setText("LFSA User Report");
                        txtMessage.setText("");
                        txtReminder.setText("*Please provide the customer's username or the food stall name.");
                        txtMessage.setHint("Enter your report about an another user or a Food Stall");
                        break;
                    case 1:
                        txtSubject.setEnabled(false);
                        txtSubject.setText("LFSA App Error/Bug");
                        txtMessage.setText("");
                        txtReminder.setText("*Please provide a detailed report of the error or bug you experienced.");
                        txtMessage.setHint("Type your report about a certain LFSA error or bug here...");
                        break;
                    case 2:
                        txtSubject.setEnabled(false);
                        txtSubject.setText("LFSA Comments/Suggestion");
                        txtMessage.setText("");
                        txtReminder.setText("");
                        txtMessage.setHint("Type your comments or suggestions here...");
                        break;
                    case 3:
                        txtSubject.setEnabled(true);
                        txtSubject.setText("");
                        txtSubject.setHint("Enter the subject of your report here");
                        txtMessage.setText("");
                        txtReminder.setText("");
                        txtMessage.setHint("Type your report here...");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void btnSend(View view) {
        String subject = txtSubject.getText().toString();
        String message = txtMessage.getText().toString();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"pmm.dlsud@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT   , message+" \n\n\n---LFSA: "+customer+" (ID: "+uid+")---");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ReportActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReportActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
