package com.manhhuy.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.manhhuy.myapplication.ui.Activities.Fragment.Admin.AdminApprovePostsFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Admin.AdminEventFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Admin.AdminRewardFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Admin.AdminUserFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Common.HomeFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Common.MeFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.User.RedeemFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Common.SearchFragment;
import com.manhhuy.myapplication.utils.MockUserManager;

public class HomeAdapter extends FragmentStateAdapter {
    private final MockUserManager.Role role;

    public HomeAdapter(FragmentActivity fragmentActivity, MockUserManager.Role role) {
        super(fragmentActivity);
        this.role = role;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (role == MockUserManager.Role.ADMIN) {
            switch (position) {
                case 0:
                    return new AdminUserFragment();
                case 1:
                    return new AdminEventFragment();
                case 2:
                    return new AdminRewardFragment();
                case 3:
                    return new AdminApprovePostsFragment();
                case 4:
                    return new MeFragment();
                default:
                    return new AdminUserFragment();
            }
        } else {
            // Customer
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new SearchFragment();
                case 2:
                    return new RedeemFragment();
                case 3:
                    return new MeFragment();
                default:
                    return new HomeFragment();
            }
        }
    }

    @Override
    public int getItemCount() {
        // Admin has 5 tabs, Customer has 4 tabs
        return role == MockUserManager.Role.ADMIN ? 5 : 4;
    }
}
