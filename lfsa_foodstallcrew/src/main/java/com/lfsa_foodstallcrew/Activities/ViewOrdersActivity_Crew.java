package com.lfsa_foodstallcrew.Activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lfsa_foodstallcrew.Adapters.PagerViewAdapter_Crew;
import com.lfsa_foodstallcrew.MainActivity_Crew;
import com.lfsa_foodstallcrew.R;

public class ViewOrdersActivity_Crew extends AppCompatActivity {


    private TextView mPendingLabel, mAcceptedLabel, mHistoryLabel;
    private ViewPager mMainPager;
    private PagerViewAdapter_Crew mPagerViewAdapterCrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders_crew);

        setTitle("View Orders");

        mPendingLabel = (TextView) findViewById(R.id.pendingLabel);
        mAcceptedLabel = (TextView) findViewById(R.id.acceptedLabel);
        mHistoryLabel = (TextView) findViewById(R.id.historyLabel);

        mMainPager = (ViewPager) findViewById(R.id.mainPager);
        mMainPager.setOffscreenPageLimit(2);

        mPagerViewAdapterCrew = new PagerViewAdapter_Crew(getSupportFragmentManager());
        mMainPager.setAdapter(mPagerViewAdapterCrew);

        mPendingLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(0);
            }
        });

        mAcceptedLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(1);
            }
        });

        mHistoryLabel.setOnClickListener(new View.OnClickListener() {
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

            mAcceptedLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mAcceptedLabel.setTextSize(16);

            mHistoryLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mHistoryLabel.setTextSize(16);
        }

        if(position == 1) {
            mAcceptedLabel.setTextColor(getResources().getColor(R.color.input_login));
            mAcceptedLabel.setTextSize(18);

            mPendingLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mPendingLabel.setTextSize(16);

            mHistoryLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mHistoryLabel.setTextSize(16);
        }

        if(position == 2) {
            mHistoryLabel.setTextColor(getResources().getColor(R.color.input_login));
            mHistoryLabel.setTextSize(18);

            mPendingLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mPendingLabel.setTextSize(16);

            mAcceptedLabel.setTextColor(getResources().getColor(R.color.input_register_bg));
            mAcceptedLabel.setTextSize(16);

        }
    }

    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(ViewOrdersActivity_Crew.this, MainActivity_Crew.class);
        startActivity(intent);
    }
}
