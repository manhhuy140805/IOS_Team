package com.manhhuy.myapplication.ui.Activitys;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayoutMediator;
import com.manhhuy.myapplication.adapter.HomeAdapter;
import com.manhhuy.myapplication.databinding.ActivityHomeBinding;


public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private final String[] tabtitles = new String[] { "Home", "Event", "Rewards", "Profile" };

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

        HomeAdapter homeAdapter = new HomeAdapter(this);

        binding.viewPager.setAdapter(homeAdapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            tab.setText(tabtitles[position]);
        }).attach();

        // GIẢI THÍCH: Xử lý nút Back theo cách modern (không dùng onBackPressed cũ nữa)
        // OnBackPressedCallback cho phép kiểm soát hành vi back button tốt hơn
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Nếu đang ở tab đầu tiên (Home), cho phép thoát app
                if (binding.viewPager.getCurrentItem() == 0) {
                    finish(); // Thoát activity
                } else {
                    // Nếu không, quay về tab trước đó
                    binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() - 1);
                }
            }
        });
    }
}