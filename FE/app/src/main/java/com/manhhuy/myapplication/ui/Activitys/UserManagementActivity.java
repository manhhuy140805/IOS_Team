package com.manhhuy.myapplication.ui.Activitys;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.UserAdapter;
import com.manhhuy.myapplication.adapter.OrganizationAdapter;
import com.manhhuy.myapplication.model.User;
import com.manhhuy.myapplication.model.Organization;

import java.util.ArrayList;
import java.util.List;


public class UserManagementActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener, OrganizationAdapter.OnOrganizationActionListener {

    private RecyclerView rvUserList, rvOrgList;
    private UserAdapter userAdapter;
    private OrganizationAdapter organizationAdapter;
    private List<User> userList;
    private List<User> filteredUserList;
    private List<Organization> organizationList;
    private List<Organization> filteredOrgList;

    private TextView tvTotalUsers, tvActiveUsers, tvLockedUsers;
    private TextView tvTotalOrgs, tvActiveOrgs, tvLockedOrgs;
    private EditText etSearch, etOrgSearch;
    private ImageView btnBack;
    private LinearLayout tabUsers, tabOrganizations, llOrgStats, llUserStats;
    private TextView btnFilterAll, btnFilterActive, btnFilterLocked, btnFilterPending;
    private TextView btnOrgFilterAll, btnOrgFilterActive, btnOrgFilterLocked, btnOrgFilterPending;
    private View cvOrgSearch, hvOrgFilter, cvUserSearch, hvUserFilter;

    private String currentUserFilter = "Tất cả";
    private String currentOrgFilter = "Tất cả";
    private boolean isUsersTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        initViews();
        setupRecyclerView();
        loadSampleData();
        setupListeners();
        updateStatistics();
    }

    private void initViews() {
        // Statistics
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        tvLockedUsers = findViewById(R.id.tvLockedUsers);
        tvTotalOrgs = findViewById(R.id.tvTotalOrgs);
        tvActiveOrgs = findViewById(R.id.tvActiveOrgs);
        tvLockedOrgs = findViewById(R.id.tvLockedOrgs);

        // Search
        etSearch = findViewById(R.id.etSearch);
        etOrgSearch = findViewById(R.id.etOrgSearch);

        // Back button
        btnBack = findViewById(R.id.btnBack);

        // Tabs
        tabUsers = findViewById(R.id.tabUsers);
        tabOrganizations = findViewById(R.id.tabOrganizations);

        // Organization stats container
        llOrgStats = findViewById(R.id.llOrgStats);

        // User stats container
        llUserStats = findViewById(R.id.llUserStats);

        // Search and filter containers
        cvOrgSearch = findViewById(R.id.cvOrgSearch);
        hvOrgFilter = findViewById(R.id.hvOrgFilter);
        cvUserSearch = findViewById(R.id.cvUserSearch);
        hvUserFilter = findViewById(R.id.hvUserFilter);

        // Filter buttons for Users
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterActive = findViewById(R.id.btnFilterActive);
        btnFilterLocked = findViewById(R.id.btnFilterLocked);
        btnFilterPending = findViewById(R.id.btnFilterPending);

        // Filter buttons for Organizations
        btnOrgFilterAll = findViewById(R.id.btnOrgFilterAll);
        btnOrgFilterActive = findViewById(R.id.btnOrgFilterActive);
        btnOrgFilterLocked = findViewById(R.id.btnOrgFilterLocked);
        btnOrgFilterPending = findViewById(R.id.btnOrgFilterPending);

        // RecyclerViews
        rvUserList = findViewById(R.id.rvUserList);
        rvOrgList = findViewById(R.id.rvOrgList);
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>();
        userAdapter = new UserAdapter(filteredUserList, this);
        rvUserList.setLayoutManager(new LinearLayoutManager(this));
        rvUserList.setAdapter(userAdapter);

        organizationList = new ArrayList<>();
        filteredOrgList = new ArrayList<>();
        organizationAdapter = new OrganizationAdapter(filteredOrgList, this);
        rvOrgList.setLayoutManager(new LinearLayoutManager(this));
        rvOrgList.setAdapter(organizationAdapter);
    }

    private void loadSampleData() {
        loadUserData();
        loadOrganizationData();
    }

    private void loadUserData() {
        userList.clear();
        
        // Sample data matching the design
        userList.add(new User("1", "Nguyễn Văn An", "an.nguyen@email.com", 
                "15/10/2024", 12, "48h", "Hoạt động", null));
        
        userList.add(new User("2", "Lê Thị Hương", "huong.le@email.com", 
                "20/09/2024", 25, "102h", "Hoạt động", null));
        
        userList.add(new User("3", "Trần Minh Khang", "khang.tran@email.com", 
                "05/08/2024", 8, null, "Bị khóa", "Spam"));
        
        userList.add(new User("4", "Phạm Thị Lan", "lan.pham@email.com", 
                "24/10/2025", 0, null, "Chờ xác thực", null));
        
        userList.add(new User("5", "Đỗ Văn Tùng", "tung.do@email.com", 
                "12/07/2024", 18, "76h", "Hoạt động", null));

        // Add more sample users
        for (int i = 6; i <= 15; i++) {
            String status = i % 3 == 0 ? "Bị khóa" : (i % 5 == 0 ? "Chờ xác thực" : "Hoạt động");
            String violation = status.equals("Bị khóa") ? "Spam" : null;
            userList.add(new User(String.valueOf(i), "User " + i, "user" + i + "@email.com",
                    "01/0" + (i % 9 + 1) + "/2024", i * 2, (i * 10) + "h", status, violation));
        }

        filteredUserList.clear();
        filteredUserList.addAll(userList);
        userAdapter.updateList(filteredUserList);
    }

    private void loadOrganizationData() {
        organizationList.clear();

        // Sample organization data
        organizationList.add(new Organization("1", "Quỹ Từ Tâm Việt", "contact@tumtam.org.vn",
                "10/05/2020", 145, "Hoạt động", null));

        organizationList.add(new Organization("2", "Tổ Chức Tình Nguyện Xanh", "info@xanh.org.vn",
                "15/03/2019", 287, "Hoạt động", null));

        organizationList.add(new Organization("3", "Trung Tâm Hỗ Trợ Cộng Đồng", "support@hoho.org.vn",
                "20/08/2021", 92, "Hoạt động", null));

        organizationList.add(new Organization("4", "Quỹ Giáo Dục Tương Lai", "education@future.org.vn",
                "12/01/2022", 156, "Chờ xác thực", null));

        organizationList.add(new Organization("5", "Tổ Chức Chăm Sóc Sức Khỏe", "health@care.org.vn",
                "05/11/2018", 78, "Bị khóa", "Nội dung không phù hợp"));

        organizationList.add(new Organization("6", "Quỹ Bảo Vệ Môi Trường", "eco@green.org.vn",
                "22/06/2020", 203, "Hoạt động", null));

        organizationList.add(new Organization("7", "Tổ Chức Hỗ Trợ Trẻ Em", "children@support.org.vn",
                "18/04/2019", 134, "Hoạt động", null));

        organizationList.add(new Organization("8", "Quỹ Phát Triển Kỹ Năng", "skills@dev.org.vn",
                "30/09/2021", 167, "Hoạt động", null));

        filteredOrgList.clear();
        filteredOrgList.addAll(organizationList);
        organizationAdapter.updateList(filteredOrgList);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Tab click listeners
        tabUsers.setOnClickListener(v -> switchToUsersTab());
        tabOrganizations.setOnClickListener(v -> switchToOrganizationsTab());

        // User Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Organization Search functionality
        etOrgSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrganizations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // User Filter buttons
        btnFilterAll.setOnClickListener(v -> setUserFilter("Tất cả", btnFilterAll));
        btnFilterActive.setOnClickListener(v -> setUserFilter("Hoạt động", btnFilterActive));
        btnFilterLocked.setOnClickListener(v -> setUserFilter("Bị khóa", btnFilterLocked));
        btnFilterPending.setOnClickListener(v -> setUserFilter("Chờ xác thực", btnFilterPending));

        // Organization Filter buttons
        btnOrgFilterAll.setOnClickListener(v -> setOrgFilter("Tất cả", btnOrgFilterAll));
        btnOrgFilterActive.setOnClickListener(v -> setOrgFilter("Hoạt động", btnOrgFilterActive));
        btnOrgFilterLocked.setOnClickListener(v -> setOrgFilter("Bị khóa", btnOrgFilterLocked));
        btnOrgFilterPending.setOnClickListener(v -> setOrgFilter("Chờ xác thực", btnOrgFilterPending));
    }

    private void switchToUsersTab() {
        isUsersTab = true;
        // Hide organization UI
        llOrgStats.setVisibility(View.GONE);
        cvOrgSearch.setVisibility(View.GONE);
        hvOrgFilter.setVisibility(View.GONE);
        rvOrgList.setVisibility(View.GONE);

        // Show user UI
        llUserStats.setVisibility(View.VISIBLE);
        cvUserSearch.setVisibility(View.VISIBLE);
        hvUserFilter.setVisibility(View.VISIBLE);
        rvUserList.setVisibility(View.VISIBLE);

        // Update tab colors
        updateTabColors();
    }

    private void switchToOrganizationsTab() {
        isUsersTab = false;
        // Hide user UI
        llUserStats.setVisibility(View.GONE);
        cvUserSearch.setVisibility(View.GONE);
        hvUserFilter.setVisibility(View.GONE);
        rvUserList.setVisibility(View.GONE);

        // Show organization UI
        llOrgStats.setVisibility(View.VISIBLE);
        cvOrgSearch.setVisibility(View.VISIBLE);
        hvOrgFilter.setVisibility(View.VISIBLE);
        rvOrgList.setVisibility(View.VISIBLE);

        // Update tab colors
        updateTabColors();
    }

    private void updateTabColors() {
        if (isUsersTab) {
            // Users tab active
            ((ImageView) tabUsers.getChildAt(0)).setColorFilter(getColor(R.color.green_primary));
            ((TextView) tabUsers.getChildAt(1)).setTextColor(getColor(R.color.green_primary));

            // Organizations tab inactive
            ((ImageView) tabOrganizations.getChildAt(0)).setColorFilter(getColor(R.color.text_secondary));
            ((TextView) tabOrganizations.getChildAt(1)).setTextColor(getColor(R.color.text_secondary));
        } else {
            // Organizations tab active
            ((ImageView) tabOrganizations.getChildAt(0)).setColorFilter(getColor(R.color.green_primary));
            ((TextView) tabOrganizations.getChildAt(1)).setTextColor(getColor(R.color.green_primary));

            // Users tab inactive
            ((ImageView) tabUsers.getChildAt(0)).setColorFilter(getColor(R.color.text_secondary));
            ((TextView) tabUsers.getChildAt(1)).setTextColor(getColor(R.color.text_secondary));
        }
    }

    private void setUserFilter(String filter, TextView selectedButton) {
        currentUserFilter = filter;

        // Reset all buttons
        resetFilterButton(btnFilterAll);
        resetFilterButton(btnFilterActive);
        resetFilterButton(btnFilterLocked);
        resetFilterButton(btnFilterPending);

        // Highlight selected button
        selectedButton.setBackgroundResource(R.drawable.bg_filter_selected);
        selectedButton.setTextColor(getColor(R.color.white));

        // Apply filter
        filterUsers(etSearch.getText().toString());
    }

    private void setOrgFilter(String filter, TextView selectedButton) {
        currentOrgFilter = filter;
        // Reset all buttons
        resetFilterButton(btnOrgFilterAll);
        resetFilterButton(btnOrgFilterActive);
        resetFilterButton(btnOrgFilterLocked);
        resetFilterButton(btnOrgFilterPending);

        // Highlight selected button
        selectedButton.setBackgroundResource(R.drawable.bg_filter_selected);
        selectedButton.setTextColor(getColor(R.color.white));

        // Apply filter
        filterOrganizations(etOrgSearch.getText().toString());
    }

    private void resetFilterButton(TextView button) {
        button.setBackgroundResource(R.drawable.bg_filter_unselected);
        button.setTextColor(getColor(R.color.text_secondary));
    }

    private void filterUsers(String searchText) {
        filteredUserList.clear();

        for (User user : userList) {
            boolean matchesSearch = searchText.isEmpty() ||
                    user.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(searchText.toLowerCase());

            boolean matchesFilter = currentUserFilter.equals("Tất cả") ||
                    user.getStatus().equals(currentUserFilter);

            if (matchesSearch && matchesFilter) {
                filteredUserList.add(user);
            }
        }

        userAdapter.updateList(filteredUserList);
    }

    private void filterOrganizations(String searchText) {
        filteredOrgList.clear();

        for (Organization org : organizationList) {
            boolean matchesSearch = searchText.isEmpty() ||
                    org.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    org.getEmail().toLowerCase().contains(searchText.toLowerCase());

            boolean matchesFilter = currentOrgFilter.equals("Tất cả") ||
                    org.getStatus().equals(currentOrgFilter);

            if (matchesSearch && matchesFilter) {
                filteredOrgList.add(org);
            }
        }

        organizationAdapter.updateList(filteredOrgList);
    }

    private void updateStatistics() {
        int totalUsers = userList.size();
        int activeUsers = 0;
        int lockedUsers = 0;

        for (User user : userList) {
            if (user.getStatus().equals("Hoạt động")) {
                activeUsers++;
            } else if (user.getStatus().equals("Bị khóa")) {
                lockedUsers++;
            }
        }

        tvTotalUsers.setText(String.valueOf(totalUsers));
        tvActiveUsers.setText(String.valueOf(activeUsers));
        tvLockedUsers.setText(String.valueOf(lockedUsers));

        int totalOrgs = organizationList.size();
        int activeOrgs = 0;
        int lockedOrgs = 0;

        for (Organization org : organizationList) {
            if (org.getStatus().equals("Hoạt động")) {
                activeOrgs++;
            } else if (org.getStatus().equals("Bị khóa")) {
                lockedOrgs++;
            }
        }

        tvTotalOrgs.setText(String.valueOf(totalOrgs));
        tvActiveOrgs.setText(String.valueOf(activeOrgs));
        tvLockedOrgs.setText(String.valueOf(lockedOrgs));
    }

    @Override
    public void onViewClick(User user) {
        Toast.makeText(this, "Xem chi tiết: " + user.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement view user details
    }

    @Override
    public void onLockUnlockClick(User user) {
        String action = user.getStatus().equals("Bị khóa") ? "mở khóa" : "khóa";
        
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn " + action + " tài khoản " + user.getName() + "?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    if (user.getStatus().equals("Bị khóa")) {
                        user.setStatus("Hoạt động");
                        user.setViolationType(null);
                    } else {
                        user.setStatus("Bị khóa");
                    }
                    userAdapter.notifyDataSetChanged();
                    updateStatistics();
                    Toast.makeText(this, "Đã " + action + " tài khoản", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDeleteClick(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa tài khoản " + user.getName() + "? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    userList.remove(user);
                    filteredUserList.remove(user);
                    userAdapter.notifyDataSetChanged();
                    updateStatistics();
                    Toast.makeText(this, "Đã xóa tài khoản", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onViewClick(Organization organization) {
        Toast.makeText(this, "Xem chi tiết: " + organization.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLockUnlockClick(Organization organization) {
        String action = organization.getStatus().equals("Bị khóa") ? "mở khóa" : "khóa";
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn " + action + " tổ chức " + organization.getName() + "?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    if (organization.getStatus().equals("Bị khóa")) {
                        organization.setStatus("Hoạt động");
                        organization.setViolationType(null);
                    } else {
                        organization.setStatus("Bị khóa");
                    }
                    organizationAdapter.notifyDataSetChanged();
                    updateStatistics();
                    Toast.makeText(this, "Đã " + action + " tổ chức", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDeleteClick(Organization organization) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa tổ chức " + organization.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    organizationList.remove(organization);
                    filteredOrgList.remove(organization);
                    organizationAdapter.notifyDataSetChanged();
                    updateStatistics();
                    Toast.makeText(this, "Đã xóa tổ chức", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
