package com.manhhuy.myapplication.ui.Activitys;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.CategoriesAdapter;
import com.manhhuy.myapplication.adapter.EventAdapter;
import com.manhhuy.myapplication.databinding.ActivityHomeBinding;
import com.manhhuy.myapplication.model.Category;
import com.manhhuy.myapplication.model.Event;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

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
        categories.add(new Category("Environment", 128, R.drawable.ic_plant));
        categories.add(new Category("Education", 93, R.drawable.ic_book));
        categories.add(new Category("Healthcare", 67, R.drawable.ic_coffee));
        categories.add(new Category("Animal Care", 45, R.drawable.ic_heart));
        categories.add(new Category("Technology", 56, R.drawable.ic_download));
        
        categoriesAdapter.setCategories(categories);

        // Load events data
        List<Event> events = new ArrayList<>();
        events.add(new Event(
                "Beach Cleanup Drive 2024",
                "Green Vietnam Organization",
                "Environment",
                R.drawable.ic_launcher_background,
                "200 points",
                "Vung Tau Beach"
        ));
        events.add(new Event(
                "Teach English to Street Kids",
                "Education For All Foundation",
                "Teaching",
                R.drawable.ic_launcher_background,
                "150 points",
                "Ho Chi Minh City"
        ));
        events.add(new Event(
                "Medical Camp in Rural Area",
                "Health Volunteers International",
                "Healthcare",
                R.drawable.ic_launcher_background,
                "300 points",
                "Rural District, Binh Duong"
        ));
        events.add(new Event(
                "Animal Shelter Care",
                "Paws and Whiskers",
                "Animal Care",
                R.drawable.ic_launcher_background,
                "180 points",
                "District 2, HCMC"
        ));
        
        eventAdapter.setEvents(events);
    }
}
