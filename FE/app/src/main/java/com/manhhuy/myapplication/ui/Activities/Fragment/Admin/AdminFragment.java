package com.manhhuy.myapplication.ui.Activities.Fragment.Admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.admin.userOrganations.OnOrganizationActionListener;
import com.manhhuy.myapplication.adapter.admin.userOrganations.OnUserActionListener;
import com.manhhuy.myapplication.adapter.admin.userOrganations.OrganizationAdapter;
import com.manhhuy.myapplication.adapter.admin.userOrganations.UserAdapter;
import com.manhhuy.myapplication.databinding.ActivityUserManagementBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.UserResponse;
import com.manhhuy.myapplication.model.Organization;
import com.manhhuy.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminFragment extends Fragment implements OnUserActionListener, OnOrganizationActionListener {

    private ActivityUserManagementBinding binding;
    private UserAdapter userAdapter;
    private OrganizationAdapter organizationAdapter;
    private ApiEndpoints apiEndpoints;

    private final List<User> userList = new ArrayList<>();
    private final List<User> filteredUserList = new ArrayList<>();
    private final List<Organization> organizationList = new ArrayList<>();
    private final List<Organization> filteredOrgList = new ArrayList<>();

    private String currentUserFilter = "Tất cả";
    private String currentOrgFilter = "Tất cả";
    private boolean isUsersTab = true;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = ActivityUserManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize API client
        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);
        
        setupRecyclerView();
        loadUsersFromApi();
        loadOrganizationsFromApi();
        setupListeners();
        updateStatistics();
    }

    private void setupRecyclerView() {
        // Setup User RecyclerView
        userAdapter = new UserAdapter(filteredUserList, this);
        binding.rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUserList.setAdapter(userAdapter);

        // Setup Organization RecyclerView
        organizationAdapter = new OrganizationAdapter(filteredOrgList, this);
        binding.rvOrgList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvOrgList.setAdapter(organizationAdapter);
    }

    /**
     * Load Users từ API
     */
    private void loadUsersFromApi() {
        apiEndpoints.getAllUsers().enqueue(new Callback<RestResponse<List<UserResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<UserResponse>>> call,
                                 Response<RestResponse<List<UserResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    userList.clear();
                    for (UserResponse ur : response.body().getData()) {
                        User user = new User();
                        user.setId(ur.getId());
                        user.setFullName(ur.getFullName());
                        user.setEmail(ur.getEmail());
                        user.setAvatarUrl(ur.getAvatarUrl());
                        user.setStatus(mapStatus(ur.getStatus()));
                        user.setJoinDate(formatDate(ur.getCreatedAt()));
                        user.setActivityCount(ur.getActivityCount() != null ? ur.getActivityCount() : 0);
                        user.setPointsCount(ur.getTotalPoints() != null ? ur.getTotalPoints() : 0);
                        user.setViolationType(ur.getViolation() != null && ur.getViolation() ? "Vi phạm" : null);
                        userList.add(user);
                    }
                    filteredUserList.clear();
                    filteredUserList.addAll(userList);
                    userAdapter.updateList(filteredUserList);
                    updateStatistics();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<List<UserResponse>>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String mapStatus(String status) {
        if ("ACTIVE".equalsIgnoreCase(status)) return "Hoạt động";
        if ("LOCKED".equalsIgnoreCase(status)) return "Bị khóa";
        if ("PENDING".equalsIgnoreCase(status)) return "Chờ xác thực";
        return "Hoạt động";
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "N/A";
        try {
            // Parse ISO 8601: "2024-10-15T10:30:00Z"
            java.time.Instant instant = java.time.Instant.parse(isoDate);
            java.time.LocalDate date = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            return "N/A";
        }
    }

    /**
     * Load Organizations từ API (filter users by role ORGANIZATION)
     */
    private void loadOrganizationsFromApi() {
        apiEndpoints.getAllUsers().enqueue(new Callback<RestResponse<List<UserResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<UserResponse>>> call,
                                 Response<RestResponse<List<UserResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    organizationList.clear();
                    for (UserResponse ur : response.body().getData()) {
                        if ("ROLE_ORGANIZATION".equalsIgnoreCase(ur.getRole()) || 
                            "ORGANIZATION".equalsIgnoreCase(ur.getRole())) {
                            
                            Organization org = new Organization();
                            org.setId(String.valueOf(ur.getId()));
                            org.setName(ur.getFullName());
                            org.setEmail(ur.getEmail());
                            org.setStatus(mapStatus(ur.getStatus()));
                            org.setFoundedDate(formatDate(ur.getCreatedAt()));
                            org.setMemberCount(ur.getActivityCount() != null ? ur.getActivityCount() : 0);
                            org.setViolationType(ur.getViolation() != null && ur.getViolation() ? "Vi phạm" : null);
                            organizationList.add(org);
                        }
                    }
                    filteredOrgList.clear();
                    filteredOrgList.addAll(organizationList);
                    organizationAdapter.updateList(filteredOrgList);
                    updateStatistics();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<List<UserResponse>>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupListeners() {
        binding.btnBack.setVisibility(View.GONE); // Hide back button in fragment

        // Tab click listeners
        binding.tabUsers.setOnClickListener(v -> switchToUsersTab());
        binding.tabOrganizations.setOnClickListener(v -> switchToOrganizationsTab());

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
        toggleViews(View.VISIBLE, View.GONE);
        updateTabColors();
    }

    private void switchToOrganizationsTab() {
        isUsersTab = false;
        toggleViews(View.GONE, View.VISIBLE);
        updateTabColors();
    }

    private void toggleViews(int userVisibility, int orgVisibility) {
        binding.llUserStats.setVisibility(userVisibility);
        binding.cvUserSearch.setVisibility(View.GONE);
        binding.hvUserFilter.setVisibility(userVisibility);
        binding.rvUserList.setVisibility(userVisibility);

        binding.llOrgStats.setVisibility(orgVisibility);
        binding.cvOrgSearch.setVisibility(View.GONE);
        binding.hvOrgFilter.setVisibility(orgVisibility);
        binding.rvOrgList.setVisibility(orgVisibility);
    }

    private void updateTabColors() {
        ViewGroup activeTab = isUsersTab ? binding.tabUsers : binding.tabOrganizations;
        ViewGroup inactiveTab = isUsersTab ? binding.tabOrganizations : binding.tabUsers;

        setTabActive(activeTab);
        setTabInactive(inactiveTab);
    }

    private void setTabActive(ViewGroup tab) {
        tab.setBackgroundResource(R.drawable.bg_button_gray_solid);
        ((ImageView) tab.getChildAt(0)).setColorFilter(getResources().getColor(R.color.app_green_primary));
        ((ImageView) tab.getChildAt(0)).setAlpha(1.0f);
        ((TextView) tab.getChildAt(1)).setTextColor(getResources().getColor(R.color.app_green_primary));
        ((TextView) tab.getChildAt(1)).setAlpha(1.0f);
    }

    private void setTabInactive(ViewGroup tab) {
        tab.setBackgroundResource(android.R.color.transparent);
        ((ImageView) tab.getChildAt(0)).setColorFilter(getResources().getColor(R.color.white));
        ((ImageView) tab.getChildAt(0)).setAlpha(0.8f);
        ((TextView) tab.getChildAt(1)).setTextColor(getResources().getColor(R.color.white));
        ((TextView) tab.getChildAt(1)).setAlpha(0.8f);
    }

    private void setUserFilter(String filter, TextView selectedButton) {
        currentUserFilter = filter;
        resetAllFilterButtons(binding.btnFilterAll, binding.btnFilterActive, binding.btnFilterLocked, binding.btnFilterPending);
        setFilterButtonSelected(selectedButton);
        filterUsers("");
    }

    private void setOrgFilter(String filter, TextView selectedButton) {
        currentOrgFilter = filter;
        resetAllFilterButtons(binding.btnOrgFilterAll, binding.btnOrgFilterActive, binding.btnOrgFilterLocked, binding.btnOrgFilterPending);
        setFilterButtonSelected(selectedButton);
        filterOrganizations("");
    }

    private void resetAllFilterButtons(TextView... buttons) {
        for (TextView button : buttons) {
            button.setBackgroundResource(R.drawable.bg_filter_unselected);
            button.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    private void setFilterButtonSelected(TextView button) {
        button.setBackgroundResource(R.drawable.bg_filter_selected);
        button.setTextColor(getResources().getColor(R.color.white));
    }

    private void filterUsers(String searchText) {
        filteredUserList.clear();
        for (User user : userList) {
            if (matchesSearchAndFilter(user.getName(), user.getEmail(), user.getStatus(), searchText, currentUserFilter)) {
                filteredUserList.add(user);
            }
        }
        userAdapter.updateList(filteredUserList);
    }

    private void filterOrganizations(String searchText) {
        filteredOrgList.clear();
        for (Organization org : organizationList) {
            if (matchesSearchAndFilter(org.getName(), org.getEmail(), org.getStatus(), searchText, currentOrgFilter)) {
                filteredOrgList.add(org);
            }
        }
        organizationAdapter.updateList(filteredOrgList);
    }

    private boolean matchesSearchAndFilter(String name, String email, String status, String searchText, String filter) {
        boolean matchesSearch = searchText.isEmpty() || 
                name.toLowerCase().contains(searchText.toLowerCase()) || 
                email.toLowerCase().contains(searchText.toLowerCase());
        boolean matchesFilter = filter.equals("Tất cả") || status.equals(filter);
        return matchesSearch && matchesFilter;
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
        Toast.makeText(getContext(), "Xem chi tiết: " + user.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement view user details
    }

    @Override
    public void onLockUnlockClick(User user) {
        String action = user.getStatus().equals("Bị khóa") ? "mở khóa" : "khóa";

        new AlertDialog.Builder(requireContext())
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
                    Toast.makeText(getContext(), "Đã " + action + " tài khoản", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDeleteClick(User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa tài khoản " + user.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    apiEndpoints.deleteUser(user.getId()).enqueue(new Callback<RestResponse<Void>>() {
                        @Override
                        public void onResponse(Call<RestResponse<Void>> call, Response<RestResponse<Void>> response) {
                            if (response.isSuccessful()) {
                                userList.remove(user);
                                filteredUserList.remove(user);
                                userAdapter.notifyDataSetChanged();
                                updateStatistics();
                                Toast.makeText(getContext(), "Đã xóa tài khoản", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 400 && getContext() != null) {
                                Toast.makeText(getContext(), "Không thể xóa: User đang có event liên quan", Toast.LENGTH_LONG).show();
                            } else if (getContext() != null) {
                                Toast.makeText(getContext(), "Lỗi xóa tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RestResponse<Void>> call, Throwable t) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onViewClick(Organization organization) {
        Toast.makeText(getContext(), "Xem chi tiết: " + organization.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLockUnlockClick(Organization organization) {
        String action = organization.getStatus().equals("Bị khóa") ? "mở khóa" : "khóa";
        new AlertDialog.Builder(requireContext())
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
                    Toast.makeText(getContext(), "Đã " + action + " tổ chức", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDeleteClick(Organization organization) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa tổ chức " + organization.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    organizationList.remove(organization);
                    filteredOrgList.remove(organization);
                    organizationAdapter.notifyDataSetChanged();
                    updateStatistics();
                    Toast.makeText(getContext(), "Đã xóa tổ chức", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
