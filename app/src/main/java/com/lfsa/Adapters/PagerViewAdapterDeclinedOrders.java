package com.lfsa.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lfsa.Fragments.JefceesDeclinedOrderFragment;
import com.lfsa.Fragments.JefceesHistoryFragment;
import com.lfsa.Fragments.ScoopsDeclinedOrderFragment;
import com.lfsa.Fragments.ScoopsHistoryFragment;
import com.lfsa.Fragments.TempuraSamDeclinedOrderFragment;
import com.lfsa.Fragments.TempuraSamHistoryFragment;

public class PagerViewAdapterDeclinedOrders extends FragmentPagerAdapter {
    public PagerViewAdapterDeclinedOrders(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                JefceesDeclinedOrderFragment jefceesDeclinedOrderFragment = new JefceesDeclinedOrderFragment();
                return jefceesDeclinedOrderFragment;
            case 1:
                ScoopsDeclinedOrderFragment scoopsDeclinedOrderFragment = new ScoopsDeclinedOrderFragment();
                return scoopsDeclinedOrderFragment;
            case 2:
                TempuraSamDeclinedOrderFragment tempuraSamDeclinedOrderFragment = new TempuraSamDeclinedOrderFragment();
                return tempuraSamDeclinedOrderFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
