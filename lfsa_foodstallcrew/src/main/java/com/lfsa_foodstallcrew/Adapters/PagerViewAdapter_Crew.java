package com.lfsa_foodstallcrew.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lfsa_foodstallcrew.Fragments.AcceptedOrdersFragment_Crew;
import com.lfsa_foodstallcrew.Fragments.BulkOrderFragment_Crew;
import com.lfsa_foodstallcrew.Fragments.PendingOrdersFragment_Crew;

public class PagerViewAdapter_Crew extends FragmentPagerAdapter {

    public PagerViewAdapter_Crew(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PendingOrdersFragment_Crew pendingOrdersFragmentCrew = new PendingOrdersFragment_Crew();
                return pendingOrdersFragmentCrew;
            case 1:
                AcceptedOrdersFragment_Crew acceptedOrdersFragmentCrew = new AcceptedOrdersFragment_Crew();
                return acceptedOrdersFragmentCrew;
            case 2:
                BulkOrderFragment_Crew transHistoryFragment = new BulkOrderFragment_Crew();
                return transHistoryFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
