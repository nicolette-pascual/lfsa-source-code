package com.lfsa_foodstallcrew.Activities.LoginActivities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lfsa_foodstallcrew.MainActivity_Crew;
import com.lfsa_foodstallcrew.R;

public class SplashScreenActivity_Crew extends AppCompatActivity {
    private static int SPLASH_TIME = 3000; //This is 4 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_crew);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mySuperIntent = new Intent(SplashScreenActivity_Crew.this, MainActivity_Crew.class);
                startActivity(mySuperIntent);
                finish();
            }
        }, SPLASH_TIME);
    }
}
