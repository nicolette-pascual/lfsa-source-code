package com.lfsa.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lfsa.Fragments.AcceptedOrdersFragment;
import com.lfsa.Fragments.BulkOrdersFragment;
import com.lfsa.Fragments.PendingOrdersFragment;

public class PagerViewAdapter extends FragmentPagerAdapter {


    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PendingOrdersFragment pendingFragment = new PendingOrdersFragment();
                return pendingFragment;
            case 1:
                BulkOrdersFragment bulkFragment = new BulkOrdersFragment();
                return bulkFragment;
            case 2:
                AcceptedOrdersFragment historyFragment = new AcceptedOrdersFragment();
                return historyFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
