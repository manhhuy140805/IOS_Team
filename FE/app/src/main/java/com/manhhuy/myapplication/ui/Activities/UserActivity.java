package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayoutMediator;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.UserAdapter;
import com.manhhuy.myapplication.databinding.ActivityUserBinding;
import com.manhhuy.myapplication.utils.ZoomOutPageTransformer;

public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding binding;

    private String[] tabTitles;
    private int[] tabIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupViewPager();
        setupBackPress();
    }

    private void setupViewPager() {
        UserAdapter userAdapter = new UserAdapter(this);
        binding.viewPager.setAdapter(userAdapter);
        binding.viewPager.setPageTransformer(new ZoomOutPageTransformer());
        
        // Tab titles and icons for User
        tabTitles = new String[] { "Trang chủ", "Tìm kiếm", "Đổi thưởng", "Chứng nhận", "Cá nhân" };
        tabIcons = new int[] {
                R.drawable.ic_home,
                R.drawable.ic_search,
                R.drawable.ic_rewards,
                R.drawable.ic_medal,
                R.drawable.ic_profile
        };

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    tab.setText(tabTitles[position]);
                    tab.setIcon(tabIcons[position]);
                }).attach();
    }

    private void setupBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int currentItem = binding.viewPager.getCurrentItem();
                if (currentItem == 0) {
                    finish();
                } else {
                    binding.viewPager.setCurrentItem(currentItem - 1, true);
                }
            }
        });
    }
}