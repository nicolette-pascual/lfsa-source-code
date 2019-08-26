package com.lfsa.Activities.MainNavBarActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lfsa.MainActivity;
import com.lfsa.R;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        setTitle("About Us");
    }

    public void onBackPressed() {

        Intent intent = new Intent(AboutUs.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
