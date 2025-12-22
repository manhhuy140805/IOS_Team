package com.manhhuy.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.manhhuy.myapplication.ui.Activities.Fragment.Admin.AdminApprovePostsFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Admin.AdminFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Admin.AdminRedeemRequestFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Common.EventManageFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Admin.AdminRewardFragment;

import com.manhhuy.myapplication.ui.Activities.Fragment.Common.MeFragment;

public class AdminAdapter extends FragmentStateAdapter {

    public AdminAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminFragment();
            case 1:
                return new EventManageFragment();
            case 2:
                return new AdminRewardFragment();
            case 3:
                return new AdminRedeemRequestFragment();
            case 4:
                return new AdminApprovePostsFragment();
            case 5:
                return new MeFragment();
            default:
                return new AdminFragment();
        }
    }

    @Override
    public int getItemCount() {
        // Admin has 6 tabs
        return 6;
    }
}
