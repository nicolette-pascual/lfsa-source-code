package com.lfsa_foodstallcrew.Activities.LoginActivities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lfsa_foodstallcrew.MainActivity_Crew;
import com.lfsa_foodstallcrew.R;

public class ResetPasswordActivity_Crew extends AppCompatActivity {
    private EditText inputEmail;
    private Button btnReset;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_crew);

        setTitle("Reset Password");

        user = FirebaseAuth.getInstance().getCurrentUser();
        inputEmail = (EditText) findViewById(R.id.txt_email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        if(user != null){
            //user_email = getIntent().getExtras().getString("user_email");
            inputEmail.setText(user.getEmail());
            inputEmail.setEnabled(false);
        }



        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Please enter your registered Address", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity_Crew.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else if(!task.isSuccessful()){
                                    Toast.makeText(ResetPasswordActivity_Crew.this, "Your Email is not yet Registered!", Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    Toast.makeText(ResetPasswordActivity_Crew.this, "Input Error.", Toast.LENGTH_SHORT).show();
                                }


                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ResetPasswordActivity_Crew.this, MainActivity_Crew.class));
    }
}

