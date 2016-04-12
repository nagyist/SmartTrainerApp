package com.smarttrainer.smarttrainer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ld on 3/5/16.
 */
public class FragPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "EXERCISE", "CHALLENGE", "SUMMARY" };
    private Context context;

    public FragPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return MainPageFrag.newInstance(position + 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
