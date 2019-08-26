package com.lfsa.Activities.LoginActivities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lfsa.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText  txt_firstname, txt_lastname, txt_email, txt_password, txt_confirm_pass;
    private Button btn_register, btn_already_registered;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    DatabaseReference databaseReferencece;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        auth = FirebaseAuth.getInstance();

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_already_registered = (Button) findViewById(R.id.btn_already_registered);
        txt_firstname = (EditText) findViewById(R.id.txt_firstname);
        txt_lastname = (EditText) findViewById(R.id.txt_lastname);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_password = (EditText) findViewById(R.id.txt_password);
        txt_confirm_pass = (EditText) findViewById(R.id.txt_confirm_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btn_already_registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        if(!isNetworkAvailable()){
            Toast.makeText(SignUpActivity.this, "Please connect to the internet to start using LFSA. ", Toast.LENGTH_SHORT).show();
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = txt_email.getText().toString().trim();
                String password = txt_password.getText().toString().trim();
                String confPass = txt_confirm_pass.getText().toString().trim();

                if(isNetworkAvailable()) {
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Please enter your email address", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (password.length() < 6) {
                        Toast.makeText(getApplicationContext(), "Password too short(Minimum of 6 characters)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!password.equals(confPass)) {
                        Toast.makeText(getApplicationContext(), "The passwords didn't match. Please Try Again.", Toast.LENGTH_SHORT).show();
                        txt_password.setText("");
                        txt_confirm_pass.setText("");
                    }

                    else {
                        progressBar.setVisibility(View.VISIBLE);
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);

                                        if (!task.isSuccessful()) {

                                            Toast.makeText(SignUpActivity.this, "Authentication failed. " + task.getException(),
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Intent newActivity = new Intent(SignUpActivity.this, VerifyAccount.class);
                                            startActivity(newActivity);
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                            String firstname = txt_firstname.getText().toString();
                                            String lastname = txt_lastname.getText().toString();
                                            String accountType = "Customer";
                                            String email = txt_email.getText().toString();
                                            String token_id = FirebaseInstanceId.getInstance().getToken();


                                            String user_id = auth.getCurrentUser().getUid();
                                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                                            Map newPost = new HashMap();
                                            newPost.put("Name", firstname + " " + lastname);
                                            newPost.put("Account_Type", accountType);
                                            newPost.put("Email", email);
                                            newPost.put("Token_ID", token_id);

                                            current_user_db.setValue(newPost);
                                        }
                                    }
                                });
                    }
                }

                else {
                    Toast.makeText(SignUpActivity.this, "Please connect to the internet to start using LFSA. ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void termsAndConditionTextClicked(View view) {
        setContentView(R.layout.activity_termsandcondition);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
    }
}

