package com.manhhuy.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.manhhuy.myapplication.ui.Activities.Fragment.Common.MeFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Organization.AcceptApplicantFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Common.EventManageFragment;

public class OrganizationAdapter extends FragmentStateAdapter {

    public OrganizationAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EventManageFragment(); // Sự kiện
            case 1:
                return new AcceptApplicantFragment(); // Thông báo / Duyệt đơn
            case 2:
                return new MeFragment(); // Cá nhân
            default:
                return new EventManageFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Organization has 3 tabs
    }
}

