package com.lfsa.Activities.LoginActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.lfsa.Activities.MainNavBarActivities.ResetPasswordActivity;
import com.lfsa.MainActivity;
import com.lfsa.R;
import com.lfsa_foodstallcrew.MainActivity_Crew;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private EditText txt_email, txt_password;
    private Button btn_login, btn_signup, btn_reset_password, btnCrew;
    private ProgressBar progressBar;

    DatabaseReference databaseReference, mUserDB;
    FirebaseUser user;
    public String uid, token_id, new_token_id;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        auth = FirebaseAuth.getInstance();

        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_password = (EditText) findViewById(R.id.txt_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_reset_password = (Button) findViewById(R.id.btn_reset_password);
        btnCrew = findViewById(R.id.btn_Crew);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDB.keepSynced(true);
        user = FirebaseAuth.getInstance().getCurrentUser();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent newActivity = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(newActivity);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                checkLogin();
            }
        });


        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent newActivity = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(newActivity);
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("users");

        //===Si colette may gawa neto====
        if (auth.getCurrentUser() != null) {
            // User is logged in
        }

        btnCrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivity = new Intent(LoginActivity.this, MainActivity_Crew.class);
                startActivity(newActivity);
                finish();
            }
        });

        if(!isNetworkAvailable()){
            Toast.makeText(LoginActivity.this, "Please connect to the internet to start using LFSA. ", Toast.LENGTH_SHORT).show();
            //FirebaseAuth.getInstance().signOut();

        }

    }

    private void checkLogin(){
        String email = txt_email.getText().toString().trim();
        String pass = txt_password.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        if(isNetworkAvailable()){
            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(task.isSuccessful()){
                            checkIfEmailVerified();
                        }else{
                            Toast.makeText(LoginActivity.this, "Invalid email and/or password.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(LoginActivity.this, "You have entered an invalid email and/or password.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                FirebaseAuth.getInstance().signOut();
            }
        }

        else {
            Toast.makeText(LoginActivity.this, "Please connect to the internet to start using LFSA. ", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            //FirebaseAuth.getInstance().signOut();
        }


    }
    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            user = FirebaseAuth.getInstance().getCurrentUser();
            uid = user.getUid();

            databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String accType = dataSnapshot.child("Users").child(uid).child("Account_Type").getValue(String.class);
                    if(accType.equals("Customer")){
                        getTokenID(uid);
                        FirebaseMessaging.getInstance().subscribeToTopic("users");
                        progressBar.setVisibility(View.GONE);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Sorry! Only customers are allowed to use this application", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        FirebaseAuth.getInstance().signOut();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    Intent newActivity = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(newActivity);
                }
            }));

        }
        else
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Please verify your email to login.", Toast.LENGTH_SHORT).show();
            Intent newActivity = new Intent(LoginActivity.this, VerifyAccount.class);
            startActivity(newActivity);

        }
    }


    public void termsAndConditionTextClicked(View view) {
        setContentView(R.layout.activity_termsandcondition);
        setTitle("Terms and Conditions");
    }


    public void getTokenID(final String userID){
        databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                token_id = dataSnapshot.child("Users").child(userID).child("Token_ID").getValue(String.class);
                new_token_id = FirebaseInstanceId.getInstance().getToken();

                if(token_id.equals("none")){
                    Login();
                }

                else {
                    if(token_id.equals(new_token_id)){
                        Login();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Your account is already logged in on another device.", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        }));
    }

    public void Login() {
        setTokenID(new_token_id);
        Toast.makeText(LoginActivity.this, "Successfully logged in.", Toast.LENGTH_SHORT).show();
        finish();
        Intent newActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(newActivity);
    }

    public void setTokenID(final String tokenID) {

        mUserDB.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mUserDB.child(uid).child("Token_ID").setValue(tokenID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
