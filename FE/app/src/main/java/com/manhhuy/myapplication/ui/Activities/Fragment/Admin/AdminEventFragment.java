package com.manhhuy.myapplication.ui.Activities.Fragment.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.admin.event.EventManagerAdapter;
import com.manhhuy.myapplication.adapter.admin.event.OnEventActionListener;
import com.manhhuy.myapplication.databinding.ActivityEventManagerBinding;
import com.manhhuy.myapplication.model.EventPost;
import com.manhhuy.myapplication.ui.Activities.AddEventActivity;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AdminEventFragment extends Fragment implements OnEventActionListener {

    private ActivityEventManagerBinding binding;
    private EventManagerAdapter adapter;
    private List<EventPost> eventList;

    private String currentStatusFilter = "all";
    private String currentCategoryFilter = "all";

    public AdminEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityEventManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        loadSampleData();
        setupListeners();
        updateStatistics();
    }

    private void setupRecyclerView() {
        eventList = new ArrayList<>();
        adapter = new EventManagerAdapter(getContext(), eventList, this);
        binding.recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewEvents.setAdapter(adapter);
    }

    private void setupListeners() {
        // Back button - Hide in fragment
        binding.btnBack.setVisibility(View.GONE);

        // Add New Event Button
        binding.btnAddReward.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEventActivity.class);
            startActivity(intent);
        });

        // Status Tabs
        binding.tabAll.setOnClickListener(v -> {
            currentStatusFilter = "all";
            updateTabUI(binding.tabAll);
            applyFilters();
        });

        binding.tabActivity.setOnClickListener(v -> {
            currentStatusFilter = "active";
            updateTabUI(binding.tabActivity);
            applyFilters();
        });

        binding.tabComplete.setOnClickListener(v -> {
            currentStatusFilter = "completed";
            updateTabUI(binding.tabComplete);
            applyFilters();
        });

        // Category Chips
        binding.chipEnvironment.setOnClickListener(v -> {
            currentCategoryFilter = "Môi trường";
            updateChipUI(binding.chipEnvironment);
            applyFilters();
        });

        binding.chipEducation.setOnClickListener(v -> {
            currentCategoryFilter = "Giáo dục";
            updateChipUI(binding.chipEducation);
            applyFilters();
        });

        binding.chipHealth.setOnClickListener(v -> {
            currentCategoryFilter = "Y tế";
            updateChipUI(binding.chipHealth);
            applyFilters();
        });
    }

    // --- Navigation & Actions ---

    @Override
    public void onViewClick(EventPost event) {
        Intent intent = new Intent(getContext(), DetailEventActivity.class);
        intent.putExtra("EVENT_ID", event.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(EventPost event) {
        Intent intent = new Intent(getContext(), AddEventActivity.class); // Reusing AddEvent for Edit
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("IS_EDIT_MODE", true);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(EventPost event) {
        // Show confirmation dialog here in real app
        Toast.makeText(getContext(), "Đã xóa sự kiện: " + event.getTitle(), Toast.LENGTH_SHORT).show();
        // Remove from list and update adapter
        // eventList.remove(event);
        // adapter.notifyDataSetChanged();
    }

    // --- UI Helpers ---

    private void updateTabUI(android.widget.TextView selectedTab) {
        resetTabStyle(binding.tabAll);
        resetTabStyle(binding.tabActivity);
        resetTabStyle(binding.tabComplete);

        selectedTab.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
        selectedTab.setTextColor(getResources().getColor(R.color.app_green_primary));
    }

    private void resetTabStyle(android.widget.TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_category_tab_unselected_reward);
        tab.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void updateChipUI(android.widget.TextView selectedChip) {
        resetChipStyle(binding.chipEnvironment);
        resetChipStyle(binding.chipEducation);
        resetChipStyle(binding.chipHealth);

        selectedChip.setBackgroundResource(R.drawable.bg_chip_selected_event);
        selectedChip.setTextColor(getResources().getColor(R.color.app_green_primary));
    }

    private void resetChipStyle(android.widget.TextView chip) {
        chip.setBackgroundResource(R.drawable.bg_chip_unselected_event);
        chip.setTextColor(getResources().getColor(R.color.text_primary));
    }

    // --- Data & Logic ---

    private void applyFilters() {
        eventList.clear();
        for (EventPost event : getAllEvents()) {
            boolean matchesStatus = currentStatusFilter.equals("all") ||
                    event.getStatus().equals(currentStatusFilter);
            boolean matchesCategory = currentCategoryFilter.equals("all") ||
                    (event.getTags() != null && event.getTags().contains(currentCategoryFilter));

            if (matchesStatus && matchesCategory) {
                eventList.add(event);
            }
        }
        adapter.notifyDataSetChanged();
        updateStatistics();
    }

    private void updateStatistics() {
        List<EventPost> allEvents = getAllEvents();
        int total = allEvents.size();
        int active = 0;
        int completed = 0;

        for (EventPost event : allEvents) {
            if ("active".equals(event.getStatus())) {
                active++;
            } else if ("completed".equals(event.getStatus())) {
                completed++;
            }
        }

        binding.tvTotalEvents.setText(String.valueOf(total));
        binding.tvActiveEvents.setText(String.valueOf(active));
        binding.tvCompletedEvents.setText(String.valueOf(completed));
    }

    private void loadSampleData() {
        eventList.clear();
        eventList.addAll(getAllEvents());
        adapter.notifyDataSetChanged();
    }

    private List<EventPost> getAllEvents() {
        List<EventPost> allEvents = new ArrayList<>();

        EventPost event1 = new EventPost();
        event1.setId(1);
        event1.setTitle("Beach Cleanup");
        event1.setOrganizationName("Green Vietnam");
        event1.setLocation("Vũng Tàu");
        event1.setRewardPoints(50);
        event1.setStatus("active");
        event1.setTags(Arrays.asList("Môi trường", "Ngoài trời"));
        event1.setCurrentParticipants(15);
        event1.setMaxParticipants(20);
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2025, 9, 28);
        event1.setEventDate(cal1.getTime());
        allEvents.add(event1);

        EventPost event2 = new EventPost();
        event2.setId(2);
        event2.setTitle("Tree Planting Day");
        event2.setOrganizationName("Eco Warriors");
        event2.setLocation("Hà Nội");
        event2.setRewardPoints(75);
        event2.setStatus("active");
        event2.setTags(Arrays.asList("Môi trường", "Cộng đồng"));
        event2.setCurrentParticipants(8);
        event2.setMaxParticipants(15);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(2025, 10, 5);
        event2.setEventDate(cal2.getTime());
        allEvents.add(event2);

        EventPost event3 = new EventPost();
        event3.setId(3);
        event3.setTitle("Education Workshop");
        event3.setOrganizationName("Learn Together");
        event3.setLocation("TP.HCM");
        event3.setRewardPoints(40);
        event3.setStatus("completed");
        event3.setTags(Arrays.asList("Giáo dục", "Trong nhà"));
        event3.setCurrentParticipants(25);
        event3.setMaxParticipants(25);
        Calendar cal3 = Calendar.getInstance();
        cal3.set(2025, 9, 15);
        event3.setEventDate(cal3.getTime());
        allEvents.add(event3);

        EventPost event4 = new EventPost();
        event4.setId(4);
        event4.setTitle("Health Checkup Camp");
        event4.setOrganizationName("Care Foundation");
        event4.setLocation("Đà Nẵng");
        event4.setRewardPoints(60);
        event4.setStatus("active");
        event4.setTags(Arrays.asList("Y tế", "Cộng đồng"));
        event4.setCurrentParticipants(12);
        event4.setMaxParticipants(30);
        Calendar cal4 = Calendar.getInstance();
        cal4.set(2025, 10, 12);
        event4.setEventDate(cal4.getTime());
        allEvents.add(event4);

        return allEvents;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
