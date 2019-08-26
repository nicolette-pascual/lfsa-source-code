package com.lfsa.Activities.MainNavBarActivities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lfsa.Adapters.PagerViewAdapterDeclinedOrders;
import com.lfsa.Adapters.PagerViewAdapterTransHist;
import com.lfsa.MainActivity;
import com.lfsa.R;

public class DeclinedOrdersActivity extends AppCompatActivity {

    private TextView jeffceesLabel, scoopsLabel, tempuraSamLabel;
    private ViewPager mMainPager;
    private PagerViewAdapterDeclinedOrders mPagerViewAdapter;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declined_orders);

        setTitle("Declined Orders");

        jeffceesLabel = findViewById(R.id.jeffceesLabel);
        scoopsLabel = findViewById(R.id.scoopsLabel);
        tempuraSamLabel = findViewById(R.id.tempuraSamLabel);

        mMainPager = (ViewPager) findViewById(R.id.mainPager);
        mMainPager.setOffscreenPageLimit(2);

        mPagerViewAdapter = new PagerViewAdapterDeclinedOrders(getSupportFragmentManager());
        mMainPager.setAdapter(mPagerViewAdapter);

        jeffceesLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(0);
            }
        });

        scoopsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(1);
            }
        });

        tempuraSamLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(2);
            }
        });

        mMainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeclinedOrdersActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void changeTabs(int position) {

        if(position == 0) {
            jeffceesLabel.setTextColor(getResources().getColor(R.color.input_login));
            jeffceesLabel.setTextSize(20);

            scoopsLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            scoopsLabel.setTextSize(16);

            tempuraSamLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            tempuraSamLabel.setTextSize(16);
        }

        if(position == 1) {
            scoopsLabel.setTextColor(getResources().getColor(R.color.input_login));
            scoopsLabel.setTextSize(20);

            jeffceesLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            jeffceesLabel.setTextSize(16);

            tempuraSamLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            tempuraSamLabel.setTextSize(16);
        }

        if(position == 2) {
            tempuraSamLabel.setTextColor(getResources().getColor(R.color.input_login));
            tempuraSamLabel.setTextSize(20);

            jeffceesLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            jeffceesLabel.setTextSize(16);

            scoopsLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            scoopsLabel.setTextSize(16);

        }

    }
}

