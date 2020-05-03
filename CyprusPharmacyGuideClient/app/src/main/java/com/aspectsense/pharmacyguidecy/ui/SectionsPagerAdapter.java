package com.aspectsense.pharmacyguidecy.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aspectsense.pharmacyguidecy.R;

/**
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int [] TAB_TITLES = new int [] { R.string.On_call_now, R.string.Next_day, R.string.All };

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return OnCallNowFragment.newInstance();
            case 1: return NextDayFragment.newInstance();
            case 2: return AllPharmaciesFragment.newInstance();
            default: throw new RuntimeException("Invalid tab position: " + position);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}