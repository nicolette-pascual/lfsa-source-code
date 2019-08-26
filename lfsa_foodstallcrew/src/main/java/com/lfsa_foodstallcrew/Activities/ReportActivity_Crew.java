package com.lfsa_foodstallcrew.Activities;

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
import com.lfsa_foodstallcrew.MainActivity_Crew;
import com.lfsa_foodstallcrew.R;

public class ReportActivity_Crew extends AppCompatActivity {

    EditText txtSubject, txtMessage;
    TextView txtReminder;
    Spinner spinner;
    String uid, currentFoodStall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_crew);

        setTitle("Send A Report");

        txtSubject = findViewById(R.id.txtSubject);
        txtSubject.setEnabled(false);
        txtMessage = findViewById(R.id.txtBody);
        txtReminder = findViewById(R.id.txtReminder);
        spinner = findViewById(R.id.spinner);

        currentFoodStall = getIntent().getStringExtra("FOODSTALL");

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        txtSubject.setEnabled(false);
                        txtSubject.setText("LFSA User Report");
                        txtMessage.setText("");
                        txtReminder.setText("*Please provide the customer's username.");
                        txtMessage.setHint("Enter your report about a user");
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
        i.putExtra(Intent.EXTRA_TEXT   , message+" \n\n\n---LFSA: "+currentFoodStall+" (ID: "+uid+")---");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ReportActivity_Crew.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(ReportActivity_Crew.this, MainActivity_Crew.class);
        startActivity(intent);
    }

}
