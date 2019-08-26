package com.lfsa.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lfsa.Fragments.JefceesHistoryFragment;
import com.lfsa.Fragments.ScoopsHistoryFragment;
import com.lfsa.Fragments.TempuraSamHistoryFragment;

public class PagerViewAdapterTransHist extends FragmentPagerAdapter {

    public PagerViewAdapterTransHist(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                JefceesHistoryFragment jefceesHistoryFragment = new JefceesHistoryFragment();
                return jefceesHistoryFragment;
            case 1:
                ScoopsHistoryFragment scoopsHistoryFragment = new ScoopsHistoryFragment();
                return scoopsHistoryFragment;
            case 2:
                TempuraSamHistoryFragment tempuraSamHistoryFragment = new TempuraSamHistoryFragment();
                return tempuraSamHistoryFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
