package com.lfsa_foodstallcrew.Activities.LoginActivities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lfsa_foodstallcrew.Activities.ReportActivity_Crew;
import com.lfsa_foodstallcrew.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpActivity_Crew extends AppCompatActivity {

    private EditText  txt_stallname, txt_foodstallOwner;
    private CheckBox cb1, cb2, cb3;
    private Button btn_register;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private String meals = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_crew);

        setTitle("Sign Up - Crew");

        auth = FirebaseAuth.getInstance();

        btn_register = (Button) findViewById(R.id.btn_register);
        txt_stallname = (EditText) findViewById(R.id.txt_stallname);
        txt_foodstallOwner = (EditText) findViewById(R.id.txt_foodstallOwner);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stallName = txt_stallname.getText().toString().trim();
                String stallOwner = txt_foodstallOwner.getText().toString().trim();


                if (TextUtils.isEmpty(stallName)) {
                    Toast.makeText(getApplicationContext(), "Please enter the Food Stall name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(stallOwner)) {
                    Toast.makeText(getApplicationContext(), "Please enter the Food Stall owner.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(cb1.isChecked()){
                    meals += "" + cb1.getText().toString();
                }
                if(cb2.isChecked()){
                    meals += ", " + cb2.getText().toString();
                }
                if(cb3.isChecked()){
                    meals += ", " + cb3.getText().toString();
                }

                sendApplication(stallName, stallOwner, meals);
            }
        });
    }

    private void sendApplication(String stallName, String stallOwner, String meals) {
        String message = "Food Stall Name: "+stallName+"\n\n"+
                "Food Stall Owner: "+stallOwner+"\n\n"+
                "Meals Offered: "+meals;

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"pmm.dlsud@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "LFSA Food Stall Crew Account Application");
        i.putExtra(Intent.EXTRA_TEXT   , message+" \n\n\n---Please attach your FOOD STALL PERMIT image---");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SignUpActivity_Crew.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(SignUpActivity_Crew.this, LoginActivity_Crew.class));
    }

}

