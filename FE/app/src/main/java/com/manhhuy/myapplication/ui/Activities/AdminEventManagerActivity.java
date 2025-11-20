package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.admin.event.EventManagerAdapter;
import com.manhhuy.myapplication.adapter.admin.event.OnEventActionListener;
import com.manhhuy.myapplication.databinding.ActivityEventManagerBinding;
import com.manhhuy.myapplication.model.EventPost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AdminEventManagerActivity extends AppCompatActivity implements OnEventActionListener {

    private ActivityEventManagerBinding binding;
    private EventManagerAdapter adapter;
    private List<EventPost> eventList;

    private String currentStatusFilter = "all";
    private String currentCategoryFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        loadSampleData();
        setupTabListeners();
        setupChipListeners();
        updateStatistics();
    }

    private void setupRecyclerView() {
        eventList = new ArrayList<>();
        adapter = new EventManagerAdapter(this, eventList, this);
        binding.recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewEvents.setAdapter(adapter);
    }

    private void loadSampleData() {
        eventList.clear();

        // Sample Event 1
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
        eventList.add(event1);

        // Sample Event 2
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
        eventList.add(event2);

        // Sample Event 3
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
        eventList.add(event3);

        // Sample Event 4
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
        eventList.add(event4);

        adapter.notifyDataSetChanged();
    }

    private void setupTabListeners() {
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
    }

    private void updateTabUI(android.widget.TextView selectedTab) {
        // Reset tất cả tabs
        resetTabStyle(binding.tabAll);
        resetTabStyle(binding.tabActivity);
        resetTabStyle(binding.tabComplete);

        // Highlight tab được chọn
        selectedTab.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
        selectedTab.setTextColor(getResources().getColor(R.color.app_green_primary));
    }

    private void resetTabStyle(android.widget.TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_category_tab_unselected_reward);
        tab.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void setupChipListeners() {
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

    private void updateChipUI(android.widget.TextView selectedChip) {
        // Reset tất cả chips
        resetChipStyle(binding.chipEnvironment);
        resetChipStyle(binding.chipEducation);
        resetChipStyle(binding.chipHealth);

        // Highlight chip được chọn
        selectedChip.setBackgroundResource(R.drawable.bg_chip_selected_event);
        selectedChip.setTextColor(getResources().getColor(R.color.app_green_primary));
    }

    private void resetChipStyle(android.widget.TextView chip) {
        chip.setBackgroundResource(R.drawable.bg_chip_unselected_event);
        chip.setTextColor(getResources().getColor(R.color.text_primary));
    }

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

    private List<EventPost> getAllEvents() {
        List<EventPost> allEvents = new ArrayList<>();

        // Sample Event 1
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

    @Override
    public void onViewClick(EventPost event) {
        Toast.makeText(this, "Xem: " + event.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(EventPost event) {
        Toast.makeText(this, "Sửa: " + event.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(EventPost event) {
        Toast.makeText(this, "Xóa: " + event.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
