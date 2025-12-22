package com.manhhuy.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.manhhuy.myapplication.ui.Activities.Fragment.Common.HomeFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Common.MeFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.Common.SearchFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.User.CertificateFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.User.NotificationFragment;
import com.manhhuy.myapplication.ui.Activities.Fragment.User.RedeemFragment;

public class UserAdapter extends FragmentStateAdapter {

    public UserAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment(); // Trang chủ
            case 1:
                return new SearchFragment(); // Tìm kiếm
            case 2:
                return new RedeemFragment(); // Đổi thưởng
            case 3:
                return new NotificationFragment(); // Thông báo
            case 4:
                return new MeFragment(); // Cá nhân
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5; // User has 5 tabs
    }
}
