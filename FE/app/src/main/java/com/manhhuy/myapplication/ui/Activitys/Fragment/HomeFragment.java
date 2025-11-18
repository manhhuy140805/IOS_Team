package com.manhhuy.myapplication.ui.Activitys.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manhhuy.myapplication.R;

public class HomeFragment extends Fragment {
    private ActivityHomeBinding binding;
    private CategoriesAdapter categoriesAdapter;
    private EventAdapter eventAdapter;

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

        setupCategoriesRecyclerView();
        setupEventsRecyclerView();
        loadData();
    }

    private void setupCategoriesRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.categoriesRecyclerView.setLayoutManager(layoutManager);

        categoriesAdapter = new CategoriesAdapter(new ArrayList<>());
        binding.categoriesRecyclerView.setAdapter(categoriesAdapter);
    }

    private void setupEventsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.eventsRecyclerView.setLayoutManager(layoutManager);

        eventAdapter = new EventAdapter(new ArrayList<>());
        binding.eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void loadData() {
        // Load categories data
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Environment", 128, R.drawable.ic_launcher_background));
        categories.add(new Category("Education", 93, R.drawable.ic_launcher_background));
        categories.add(new Category("Healthcare", 67, R.drawable.ic_launcher_background));
        categories.add(new Category("Animal Care", 45, R.drawable.ic_launcher_background));

        categoriesAdapter.setCategories(categories);

        // Load events data
        List<Event> events = new ArrayList<>();
        events.add(new Event(
                "Beach Cleanup",
                "Green Vietnam",
                "Environment",
                R.drawable.ic_launcher_background,
                "200,000 VND/day",
                "Vung Tau"));
        events.add(new Event(
                "Teach English to Kids",
                "Education For All",
                "Teaching",
                R.drawable.ic_launcher_background,
                "150,000 VND/shift",
                "Ho Chi Minh City"));

        eventAdapter.setEvents(events);
    }

}