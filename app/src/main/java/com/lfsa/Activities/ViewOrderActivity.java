package com.lfsa.Activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lfsa.Adapters.PagerViewAdapter;
import com.lfsa.MainActivity;
import com.lfsa.R;
import com.lfsa_foodstallcrew.Activities.ViewOrdersActivity_Crew;

public class ViewOrderActivity extends AppCompatActivity {

    private TextView mPendingLabel, mBulkLabel, mAcceptedLabel;
    private ViewPager mMainPager;
    private PagerViewAdapter mPagerViewAdapter;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        setTitle("View Orders");

        mPendingLabel = (TextView) findViewById(R.id.pendingLabel);
        mBulkLabel = (TextView) findViewById(R.id.bulkLabel);
        mAcceptedLabel = (TextView) findViewById(R.id.acceptedLabel);

        mMainPager = (ViewPager) findViewById(R.id.mainPager);
        mMainPager.setOffscreenPageLimit(2);

        mPagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mPagerViewAdapter);

        mPendingLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(0);
            }
        });

        mBulkLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(1);
            }
        });

        mAcceptedLabel.setOnClickListener(new View.OnClickListener() {
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

    private void changeTabs(int position) {

        if(position == 0) {
            mPendingLabel.setTextColor(getResources().getColor(R.color.input_login));
            mPendingLabel.setTextSize(18);

            mBulkLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mBulkLabel.setTextSize(12);

            mAcceptedLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mAcceptedLabel.setTextSize(12);
        }

        if(position == 1) {
            mBulkLabel.setTextColor(getResources().getColor(R.color.input_login));
            mBulkLabel.setTextSize(18);

            mPendingLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mPendingLabel.setTextSize(12);

            mAcceptedLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mAcceptedLabel.setTextSize(12);
        }

        if(position == 2) {
            mAcceptedLabel.setTextColor(getResources().getColor(R.color.input_login));
            mAcceptedLabel.setTextSize(18);

            mPendingLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mPendingLabel.setTextSize(12);

            mBulkLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mBulkLabel.setTextSize(12);

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(ViewOrderActivity.this, MainActivity.class));
    }
}
