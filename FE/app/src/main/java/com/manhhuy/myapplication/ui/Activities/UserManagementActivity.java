package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.admin.userOrganations.OnOrganizationActionListener;
import com.manhhuy.myapplication.adapter.admin.userOrganations.OnUserActionListener;
import com.manhhuy.myapplication.adapter.admin.userOrganations.OrganizationAdapter;
import com.manhhuy.myapplication.adapter.admin.userOrganations.UserAdapter;
import com.manhhuy.myapplication.databinding.ActivityUserManagementBinding;
import com.manhhuy.myapplication.model.Organization;
import com.manhhuy.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity
        implements OnUserActionListener, OnOrganizationActionListener {

    private ActivityUserManagementBinding binding;
    private UserAdapter userAdapter;
    private OrganizationAdapter organizationAdapter;

    private final List<User> userList = new ArrayList<>();
    private final List<User> filteredUserList = new ArrayList<>();
    private final List<Organization> organizationList = new ArrayList<>();
    private final List<Organization> filteredOrgList = new ArrayList<>();

    private String currentUserFilter = "Tất cả";
    private String currentOrgFilter = "Tất cả";
    private boolean isUsersTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        loadSampleData();
        setupListeners();
        updateStatistics();
    }

    private void setupRecyclerView() {
        // Setup User RecyclerView
        userAdapter = new UserAdapter(filteredUserList, this);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUserList.setAdapter(userAdapter);

        // Setup Organization RecyclerView
        organizationAdapter = new OrganizationAdapter(filteredOrgList, this);
        binding.rvOrgList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrgList.setAdapter(organizationAdapter);
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
        binding.btnBack.setOnClickListener(v -> finish());

        // Tab click listeners
        binding.tabUsers.setOnClickListener(v -> switchToUsersTab());
        binding.tabOrganizations.setOnClickListener(v -> switchToOrganizationsTab());

        // User Search functionality
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Organization Search functionality
        binding.etOrgSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrganizations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // User Filter buttons
        binding.btnFilterAll.setOnClickListener(v -> setUserFilter("Tất cả", binding.btnFilterAll));
        binding.btnFilterActive.setOnClickListener(v -> setUserFilter("Hoạt động", binding.btnFilterActive));
        binding.btnFilterLocked.setOnClickListener(v -> setUserFilter("Bị khóa", binding.btnFilterLocked));
        binding.btnFilterPending.setOnClickListener(v -> setUserFilter("Chờ xác thực", binding.btnFilterPending));

        // Organization Filter buttons
        binding.btnOrgFilterAll.setOnClickListener(v -> setOrgFilter("Tất cả", binding.btnOrgFilterAll));
        binding.btnOrgFilterActive.setOnClickListener(v -> setOrgFilter("Hoạt động", binding.btnOrgFilterActive));
        binding.btnOrgFilterLocked.setOnClickListener(v -> setOrgFilter("Bị khóa", binding.btnOrgFilterLocked));
        binding.btnOrgFilterPending.setOnClickListener(v -> setOrgFilter("Chờ xác thực", binding.btnOrgFilterPending));
    }

    private void switchToUsersTab() {
        isUsersTab = true;
        // Hide organization UI
        binding.llOrgStats.setVisibility(View.GONE);
        binding.cvOrgSearch.setVisibility(View.GONE);
        binding.hvOrgFilter.setVisibility(View.GONE);
        binding.rvOrgList.setVisibility(View.GONE);

        // Show user UI
        binding.llUserStats.setVisibility(View.VISIBLE);
        binding.cvUserSearch.setVisibility(View.VISIBLE);
        binding.hvUserFilter.setVisibility(View.VISIBLE);
        binding.rvUserList.setVisibility(View.VISIBLE);

        // Update tab colors
        updateTabColors();
    }

    private void switchToOrganizationsTab() {
        isUsersTab = false;
        // Hide user UI
        binding.llUserStats.setVisibility(View.GONE);
        binding.cvUserSearch.setVisibility(View.GONE);
        binding.hvUserFilter.setVisibility(View.GONE);
        binding.rvUserList.setVisibility(View.GONE);

        // Show organization UI
        binding.llOrgStats.setVisibility(View.VISIBLE);
        binding.cvOrgSearch.setVisibility(View.VISIBLE);
        binding.hvOrgFilter.setVisibility(View.VISIBLE);
        binding.rvOrgList.setVisibility(View.VISIBLE);

        // Update tab colors
        updateTabColors();
    }

    private void updateTabColors() {
        // Cập nhật màu sắc và indicator cho tabs
        if (isUsersTab) {
            // Users tab active
            ((ImageView) binding.tabUsers.getChildAt(0)).setColorFilter(getColor(R.color.app_green_primary));
            ((TextView) binding.tabUsers.getChildAt(1)).setTextColor(getColor(R.color.app_green_primary));

            // Organizations tab inactive
            ((ImageView) binding.tabOrganizations.getChildAt(0)).setColorFilter(getColor(R.color.text_secondary));
            ((TextView) binding.tabOrganizations.getChildAt(1)).setTextColor(getColor(R.color.text_secondary));
        } else {
            // Organizations tab active
            ((ImageView) binding.tabOrganizations.getChildAt(0)).setColorFilter(getColor(R.color.app_green_primary));
            ((TextView) binding.tabOrganizations.getChildAt(1)).setTextColor(getColor(R.color.app_green_primary));

            // Users tab inactive
            ((ImageView) binding.tabUsers.getChildAt(0)).setColorFilter(getColor(R.color.text_secondary));
            ((TextView) binding.tabUsers.getChildAt(1)).setTextColor(getColor(R.color.text_secondary));
        }
    }

    private void setUserFilter(String filter, TextView selectedButton) {
        currentUserFilter = filter;

        // Reset tất cả filter buttons
        resetFilterButton(binding.btnFilterAll);
        resetFilterButton(binding.btnFilterActive);
        resetFilterButton(binding.btnFilterLocked);
        resetFilterButton(binding.btnFilterPending);

        // Highlight button được chọn
        selectedButton.setBackgroundResource(R.drawable.bg_filter_selected);
        selectedButton.setTextColor(getColor(R.color.white));

        // Apply filter
        filterUsers(binding.etSearch.getText().toString());
    }

    private void setOrgFilter(String filter, TextView selectedButton) {
        currentOrgFilter = filter;
        // Reset all buttons
        resetFilterButton(binding.btnOrgFilterAll);
        resetFilterButton(binding.btnOrgFilterActive);
        resetFilterButton(binding.btnOrgFilterLocked);
        resetFilterButton(binding.btnOrgFilterPending);

        // Highlight selected button
        selectedButton.setBackgroundResource(R.drawable.bg_filter_selected);
        selectedButton.setTextColor(getColor(R.color.white));

        // Apply filter
        filterOrganizations(binding.etOrgSearch.getText().toString());
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

        binding.tvTotalUsers.setText(String.valueOf(totalUsers));
        binding.tvActiveUsers.setText(String.valueOf(activeUsers));
        binding.tvLockedUsers.setText(String.valueOf(lockedUsers));

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

        binding.tvTotalOrgs.setText(String.valueOf(totalOrgs));
        binding.tvActiveOrgs.setText(String.valueOf(activeOrgs));
        binding.tvLockedOrgs.setText(String.valueOf(lockedOrgs));
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
