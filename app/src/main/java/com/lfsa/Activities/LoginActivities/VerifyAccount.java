package com.lfsa.Activities.LoginActivities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lfsa.R;

public class VerifyAccount extends AppCompatActivity {

    private TextView txt_email, txt_status, txt_uid;
    private Button btn_sendVerification, btn_proceedToLOGIN;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);
        setTitle("Verify Account");

        btn_sendVerification = (Button) findViewById(R.id.btn_sendVerification);
        btn_proceedToLOGIN = (Button) findViewById(R.id.btn_proceedToLOGIN);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txt_email = (TextView) findViewById(R.id.txt_email);
        setInfo();
        btn_proceedToLOGIN.setVisibility(View.GONE);


        btn_sendVerification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().getCurrentUser()
                        .sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(VerifyAccount.this, "Verification Email Sent to " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                    btn_proceedToLOGIN.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                                else{
                                    Toast.makeText(VerifyAccount.this, "Failed to send", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            }
                        });
            }
        });
        btn_proceedToLOGIN.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().getCurrentUser()
                        .reload()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                setInfo();
                                Intent newActivity = new Intent(VerifyAccount.this, LoginActivity.class);
                                startActivity(newActivity);
                                finish();
                            }
                        });
            }
        }));
    }

    private void setInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        txt_email.setText(new StringBuilder().append(user.getEmail()));
    }
}
