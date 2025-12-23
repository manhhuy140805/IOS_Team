package com.manhhuy.myapplication.ui.Activities;

import android.content.SharedPreferences;
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

    public static final String PREFS_AI_SEARCH = "ai_search_prefs";
    public static final String KEY_AI_QUERY = "ai_query";

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
        binding.viewPager.setOffscreenPageLimit(1); // Giảm cache fragment

        tabTitles = new String[] { "Trang chủ", "Tìm kiếm", "Đổi thưởng", "Thông báo", "Cá nhân" };
        tabIcons = new int[] {
                R.drawable.ic_home,
                R.drawable.ic_search,
                R.drawable.ic_rewards,
                R.drawable.ic_notification,
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

    public void switchToSearchTab() {
        // Clear any previous AI query
        clearAiQuery();
        binding.viewPager.setCurrentItem(1, true);
    }

    // Switch to search with AI query - used from Home
    public void switchToSearchWithAiQuery(String aiQuery) {
        // Save query to SharedPreferences for SearchFragment to read
        SharedPreferences prefs = getSharedPreferences(PREFS_AI_SEARCH, MODE_PRIVATE);
        prefs.edit().putString(KEY_AI_QUERY, aiQuery).apply();
        binding.viewPager.setCurrentItem(1, true);
    }

    public void clearAiQuery() {
        SharedPreferences prefs = getSharedPreferences(PREFS_AI_SEARCH, MODE_PRIVATE);
        prefs.edit().remove(KEY_AI_QUERY).apply();
    }

    public void switchToRedeemTab() {
        binding.viewPager.setCurrentItem(2, true);
    }

    public void switchToNofiticationTab() {
        binding.viewPager.setCurrentItem(3, true);
    }
}
