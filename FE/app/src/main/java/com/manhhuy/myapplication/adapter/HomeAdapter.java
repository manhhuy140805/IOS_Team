package com.manhhuy.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.manhhuy.myapplication.ui.Activitys.Fragment.AcceptApplicantFragment;
import com.manhhuy.myapplication.ui.Activitys.Fragment.HomeFragment;
import com.manhhuy.myapplication.ui.Activitys.Fragment.SearchFragment;
import com.manhhuy.myapplication.ui.Activitys.Fragment.RedeemFragment;
import com.manhhuy.myapplication.ui.Activitys.Fragment.MeFragment;

public class HomeAdapter extends FragmentStateAdapter {
    public static final int NUM_PAGES = 5;

    public HomeAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new RedeemFragment();
            case 3:
                return AcceptApplicantFragment.newInstance();
            case 4:
                return new MeFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
