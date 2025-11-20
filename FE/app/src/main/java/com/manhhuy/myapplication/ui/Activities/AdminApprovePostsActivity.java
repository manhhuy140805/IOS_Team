package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.EventPostAdapter;
import com.manhhuy.myapplication.model.EventPost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AdminApprovePostsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPosts;
    private EventPostAdapter adapter;
    private List<EventPost> allPosts;
    private List<EventPost> filteredPosts;
    
    private TextView btnBack, btnMenu, btnLoadMore;
    private TextView tabAll, tabPending, tabApproved, tabRejected;
    private EditText searchInput;
    
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_posts);
        
        initViews();
        setupRecyclerView();
        loadSampleData();
        setupListeners();
    }

    private void initViews() {
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnMenu);
        btnLoadMore = findViewById(R.id.btnLoadMore);
        tabAll = findViewById(R.id.tabAll);
        tabPending = findViewById(R.id.tabPending);
        tabApproved = findViewById(R.id.tabApproved);
        tabRejected = findViewById(R.id.tabRejected);
        searchInput = findViewById(R.id.searchInput);
    }

    private void setupRecyclerView() {
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new EventPostAdapter(new ArrayList<>(), new EventPostAdapter.OnItemClickListener() {
            @Override
            public void onApproveClick(EventPost post, int position) {
                handleApprove(post, position);
            }

            @Override
            public void onRejectClick(EventPost post, int position) {
                handleReject(post, position);
            }

            @Override
            public void onStatisticsClick(EventPost post, int position) {
                Toast.makeText(AdminApprovePostsActivity.this, 
                    "Xem thống kê: " + post.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(EventPost post, int position) {
                Toast.makeText(AdminApprovePostsActivity.this, 
                    "Chỉnh sửa: " + post.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReviewClick(EventPost post, int position) {
                Toast.makeText(AdminApprovePostsActivity.this, 
                    "Xem lại: " + post.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        
        recyclerViewPosts.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnMenu.setOnClickListener(v -> 
            Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show());
        
        btnLoadMore.setOnClickListener(v -> 
            Toast.makeText(this, "Đang tải thêm...", Toast.LENGTH_SHORT).show());
        
        // Tab listeners
        tabAll.setOnClickListener(v -> filterPosts("all"));
        tabPending.setOnClickListener(v -> filterPosts("pending"));
        tabApproved.setOnClickListener(v -> filterPosts("approved"));
        tabRejected.setOnClickListener(v -> filterPosts("rejected"));
    }

    private void loadSampleData() {
        allPosts = new ArrayList<>();
        
        // Sample pending post 1
        EventPost post1 = new EventPost();
        post1.setId(1);
        post1.setTitle("Tree Planting Drive");
        post1.setOrganizationName("Forest Guardians VN");
        post1.setOrganizationInitials("FG");
        post1.setOrganizationColor("#C3F0CA");
        post1.setTags(Arrays.asList("Environment", "Outdoor"));
        post1.setEventDate(new Date());
        post1.setLocation("Hanoi");
        post1.setRewardPoints(100);
        post1.setPostedBy("Nguyễn Minh Tâm");
        post1.setPostedTime("2 giờ trước");
        post1.setStatus("pending");
        allPosts.add(post1);
        
        // Sample approved post
        EventPost post2 = new EventPost();
        post2.setId(2);
        post2.setTitle("Teach English to Kids");
        post2.setOrganizationName("Education For All");
        post2.setOrganizationInitials("EA");
        post2.setOrganizationColor("#FEE140");
        post2.setTags(Arrays.asList("Education", "Teaching"));
        post2.setEventDate(new Date());
        post2.setLocation("HCM City");
        post2.setRewardPoints(150);
        post2.setPostedBy("Lê Văn An");
        post2.setPostedTime("1 ngày trước");
        post2.setStatus("approved");
        post2.setReviewedBy("Admin Hòa");
        post2.setReviewedTime("1 ngày trước");
        allPosts.add(post2);
        
        // Sample pending post 2
        EventPost post3 = new EventPost();
        post3.setId(3);
        post3.setTitle("Animal Shelter Helper");
        post3.setOrganizationName("Paws & Claws");
        post3.setOrganizationInitials("PC");
        post3.setOrganizationColor("#FAB1A0");
        post3.setTags(Arrays.asList("Animals", "Care"));
        post3.setEventDate(new Date());
        post3.setLocation("Hai Phong");
        post3.setRewardPoints(120);
        post3.setPostedBy("Trần Thu Hà");
        post3.setPostedTime("5 giờ trước");
        post3.setStatus("pending");
        allPosts.add(post3);
        
        // Sample rejected post
        EventPost post4 = new EventPost();
        post4.setId(4);
        post4.setTitle("Community Garden");
        post4.setOrganizationName("Green Community");
        post4.setOrganizationInitials("CG");
        post4.setOrganizationColor("#DFE6E9");
        post4.setTags(Arrays.asList("Garden", "Community"));
        post4.setEventDate(new Date());
        post4.setLocation("Da Nang");
        post4.setRewardPoints(80);
        post4.setPostedBy("Phạm Minh Quân");
        post4.setPostedTime("3 ngày trước");
        post4.setStatus("rejected");
        post4.setReviewedBy("Admin Linh");
        post4.setReviewedTime("3 ngày trước");
        post4.setRejectionReason("Thông tin không đầy đủ, thiếu địa chỉ cụ thể và thời gian");
        allPosts.add(post4);
        
        filterPosts("all");
    }

    private void filterPosts(String filter) {
        currentFilter = filter;
        
        // Update tab UI
        updateTabUI();
        
        // Filter posts
        filteredPosts = new ArrayList<>();
        for (EventPost post : allPosts) {
            if (filter.equals("all") || post.getStatus().equals(filter)) {
                filteredPosts.add(post);
            }
        }
        
        adapter.updateData(filteredPosts);
    }

    private void updateTabUI() {
        // Reset all tabs
        tabAll.setBackgroundResource(R.drawable.tab_unselected);
        tabAll.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tabPending.setBackgroundResource(R.drawable.tab_unselected);
        tabPending.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tabApproved.setBackgroundResource(R.drawable.tab_unselected);
        tabApproved.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tabRejected.setBackgroundResource(R.drawable.tab_unselected);
        tabRejected.setTextColor(getResources().getColor(android.R.color.darker_gray));
        
        // Highlight selected tab
        switch (currentFilter) {
            case "all":
                tabAll.setBackgroundResource(R.drawable.tab_selected);
                tabAll.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "pending":
                tabPending.setBackgroundResource(R.drawable.tab_selected);
                tabPending.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "approved":
                tabApproved.setBackgroundResource(R.drawable.tab_selected);
                tabApproved.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "rejected":
                tabRejected.setBackgroundResource(R.drawable.tab_selected);
                tabRejected.setTextColor(getResources().getColor(android.R.color.white));
                break;
        }
    }

    private void handleApprove(EventPost post, int position) {
        post.setStatus("approved");
        post.setReviewedBy("Admin Hòa");
        post.setReviewedTime("Vừa xong");
        adapter.notifyItemChanged(position);
        Toast.makeText(this, "Đã duyệt: " + post.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void handleReject(EventPost post, int position) {
        // In real app, show dialog to input rejection reason
        post.setStatus("rejected");
        post.setReviewedBy("Admin Hòa");
        post.setReviewedTime("Vừa xong");
        post.setRejectionReason("Thông tin không đầy đủ");
        adapter.notifyItemChanged(position);
        Toast.makeText(this, "Đã từ chối: " + post.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
