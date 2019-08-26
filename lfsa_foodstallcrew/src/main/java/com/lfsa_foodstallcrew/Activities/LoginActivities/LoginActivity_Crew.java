package com.lfsa_foodstallcrew.Activities.LoginActivities;

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
import com.lfsa_foodstallcrew.GettersSetters.FoodStallCrew;
import com.lfsa_foodstallcrew.MainActivity_Crew;
import com.lfsa_foodstallcrew.R;

public class LoginActivity_Crew extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    private EditText txt_email, txt_password;
    private Button btn_login,btn_reset_password, btnCustomer, btn_signup;
    private ProgressBar progressBar;
    DatabaseReference databaseReferencece;
    String uid;
    FoodStallCrew foodStallCrew = new FoodStallCrew();
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_crew);

        setTitle("Login - Crew");

        user=FirebaseAuth.getInstance().getCurrentUser();

        databaseReferencece = FirebaseDatabase.getInstance().getReference();

        //JUST FOR CHECKING KUNG NAKA LOG OUT TALAGA... CAN BE DELETED IF YALL WANT
        if(user!=null){

            Log.i("a user is logged in: ", String.valueOf(user));
            sendToMain();

            uid = user.getUid();

        }
        else{
            Log.i("Username", "there is no user");
        }


        auth = FirebaseAuth.getInstance();

        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_password = (EditText) findViewById(R.id.txt_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_apply);
        btn_reset_password = (Button) findViewById(R.id.btn_reset_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

       btn_signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent newActivity = new Intent(LoginActivity_Crew.this, SignUpActivity_Crew.class);
                startActivity(newActivity);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                checkLogin();
            }
        });

       btn_reset_password.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent newActivity = new Intent(LoginActivity_Crew.this, ResetPasswordActivity_Crew.class);
                startActivity(newActivity);
            }
        });

       btnCustomer = findViewById(R.id.btn_Customer);
       btnCustomer.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               try {
                   finish();
                   startActivity(new Intent(LoginActivity_Crew.this, Class.forName("com.lfsa.Activities.LoginActivities.LoginActivity")));
               } catch (ClassNotFoundException e) {
                   e.printStackTrace();
               }

               finish();
           }
       });

        if(!isNetworkAvailable()){
            Toast.makeText(LoginActivity_Crew.this, "Please connect to the internet to start using LFSA. ", Toast.LENGTH_SHORT).show();
            //FirebaseAuth.getInstance().signOut();
        }


    }

    private void sendToMain() {
        Intent intent = new Intent(LoginActivity_Crew.this, MainActivity_Crew.class);
        startActivity(intent);
        finish();
    }

    private void checkLogin(){
        String email = txt_email.getText().toString().trim();
        String pass = txt_password.getText().toString().trim();


        if(isNetworkAvailable()){
            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(task.isSuccessful()){
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            uid = user.getUid();

                            databaseReferencece = FirebaseDatabase.getInstance().getReference();

                            databaseReferencece.addListenerForSingleValueEvent((new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String accType = dataSnapshot.child("Users").child(uid).child("Account_Type").getValue(String.class);
                                    if(accType.equals("Crew")){
                                        finish();
                                        Toast.makeText(LoginActivity_Crew.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        Intent newActivity = new Intent(LoginActivity_Crew.this, MainActivity_Crew.class);
                                        startActivity(newActivity);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(LoginActivity_Crew.this, "Sorry. Only the FoodStall Crews are allowed to use this application", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
//                                    Toast.makeText(LoginActivity_Crew.this, "Network Connection Error. ", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }));
                        }else{
                            Toast.makeText(LoginActivity_Crew.this, "Failed to Log In.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });

            }

            else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity_Crew.this, "You have entered an invalid email and/or password.", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
            }
        }

        else {
            Toast.makeText(LoginActivity_Crew.this, "Please connect to the internet to start using LFSA. ", Toast.LENGTH_SHORT).show();
            //FirebaseAuth.getInstance().signOut();
        }

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
        Intent intent = new Intent(LoginActivity_Crew.this, LoginActivity_Crew.class);
        startActivity(intent);

    }


    public void termsAndConditionTextClicked(View view) {
        setContentView(R.layout.activity_termsandcondition_crew);
        setTitle("Terms and Conditions");
    }
}
