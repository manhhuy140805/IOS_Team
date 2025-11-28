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
import com.manhhuy.myapplication.adapter.OrganizationAdapter;
import com.manhhuy.myapplication.databinding.ActivityOrganizationBinding;
import com.manhhuy.myapplication.utils.ZoomOutPageTransformer;

public class OrganizationActivity extends AppCompatActivity {

    private ActivityOrganizationBinding binding;

    private String[] tabTitles;
    private int[] tabIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityOrganizationBinding.inflate(getLayoutInflater());
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
        OrganizationAdapter organizationAdapter = new OrganizationAdapter(this);
        binding.viewPager.setAdapter(organizationAdapter);
        binding.viewPager.setPageTransformer(new ZoomOutPageTransformer());
        
        // Tab titles and icons for Organization
        tabTitles = new String[] { "Sự kiện", "Thông báo", "Cá nhân" };
        tabIcons = new int[] {
                R.drawable.ic_event,
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
}