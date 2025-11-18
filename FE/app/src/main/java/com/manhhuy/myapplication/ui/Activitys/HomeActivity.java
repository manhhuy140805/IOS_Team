package com.manhhuy.myapplication.ui.Activitys;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayoutMediator;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.HomeAdapter;
import com.manhhuy.myapplication.databinding.ActivityHomeBinding;
import com.manhhuy.myapplication.utils.ZoomOutPageTransformer;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    private final String[] tabTitles = new String[] { "Home", "Event", "Rewards", "Applicants", "Profile" };
    private final int[] tabIcons = new int[] {
            R.drawable.ic_home,
            R.drawable.ic_event,
            R.drawable.ic_rewards,
            R.drawable.ic_applicants,
            R.drawable.ic_profile
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
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
        HomeAdapter homeAdapter = new HomeAdapter(this);
        binding.viewPager.setAdapter(homeAdapter);
        binding.viewPager.setOffscreenPageLimit(5); // Pre-load all tabs for smooth transition
        binding.viewPager.setPageTransformer(new ZoomOutPageTransformer()); // Add smooth page transition

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
                    // Ở tab đầu → thoát Activity
                    finish();
                } else {
                    // Lùi về tab trước
                    binding.viewPager.setCurrentItem(currentItem - 1, true);
                }
            }
        });
    }
}
