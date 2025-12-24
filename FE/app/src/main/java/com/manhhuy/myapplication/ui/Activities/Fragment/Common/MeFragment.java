package com.manhhuy.myapplication.ui.Activities.Fragment.Common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.FragmentMeBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.JwtUtil;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.UserResponse;
import com.manhhuy.myapplication.ui.Activities.Fragment.User.MyEventsActivity;
import com.manhhuy.myapplication.ui.Activities.ChangePasswordActivity;
import com.manhhuy.myapplication.ui.Activities.EditProfileActivity;
import com.manhhuy.myapplication.ui.Activities.MainActivity;
import com.manhhuy.myapplication.ui.Activities.MyRewardActivity;
import com.manhhuy.myapplication.ui.Activities.UserActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeFragment extends Fragment {

    private static final String TAG = "MeFragment";
    private FragmentMeBinding binding;
    private ApiEndpoints apiEndpoints;

    public MeFragment() {
        // Required empty public constructor
    }

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize API client
        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);

        loadUserData();
        setupClickListeners();
        hideVolunteerOnlySections();
    }

    private void hideVolunteerOnlySections() {
        if (ApiConfig.isAdmin() || ApiConfig.isOrganizer()) {
            binding.cardMyCertificates.setVisibility(View.GONE);
            binding.cardMyRewards.setVisibility(View.GONE);
        }
    }

    private void loadUserData() {
        // Get token from SharedPreferences
        String token = ApiConfig.getToken();

        Log.d(TAG, "=== LOAD USER DATA ===");
        Log.d(TAG, "Token: " + (token != null ? "exists" : "null"));

        if (token == null || token.isEmpty()) {
            Log.w(TAG, "No token found, user not logged in");
            setDefaultUserInfo();
            return;
        }

        // Check if token is expired
        if (JwtUtil.isExpired(token)) {
            Log.w(TAG, "Token expired");
            setDefaultUserInfo();
            return;
        }

        // Get userId from token
        Integer userId = JwtUtil.getUserId(token);
        if (userId == null) {
            Log.e(TAG, "Cannot get userId from token");
            setDefaultUserInfo();
            return;
        }

        Log.d(TAG, "Loading user info for userId: " + userId);

        // Call API to get user info
        Call<RestResponse<UserResponse>> call = apiEndpoints.getUserById(userId);

        call.enqueue(new Callback<RestResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<UserResponse>> call,
                    Response<RestResponse<UserResponse>> response) {
                if (binding == null)
                    return; // Fragment destroyed

                Log.d(TAG, "API Response received");
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response successful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<UserResponse> restResponse = response.body();
                    Log.d(TAG, "RestResponse status: " + restResponse.getStatusCode());

                    if (restResponse.getStatusCode() == 200 && restResponse.getData() != null) {
                        UserResponse user = restResponse.getData();
                        updateUserInfo(user);
                        Log.d(TAG, "User info loaded successfully: " + user.getFullName());
                    } else {
                        Log.e(TAG, "API error: " + restResponse.getMessage());
                        setDefaultUserInfo();
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string()
                                : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    setDefaultUserInfo();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<UserResponse>> call, Throwable t) {
                if (binding == null)
                    return; // Fragment destroyed
                Log.e(TAG, "Failed to load user info: " + t.getMessage(), t);
                setDefaultUserInfo();
            }
        });
    }

    /**
     * Update UI with user information from API
     */
    private void updateUserInfo(UserResponse user) {
        if (binding == null)
            return;

        Log.d(TAG, "=== UPDATE USER INFO ===");
        Log.d(TAG, "Full name: " + user.getFullName());
        Log.d(TAG, "Avatar URL: " + user.getAvatarUrl());

        // Set user information
        binding.tvFullName.setText(user.getFullName() != null ? user.getFullName() : "Người dùng");
        binding.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        binding.tvPhone.setText(user.getPhone() != null ? user.getPhone() : "");

        // Load avatar with improved handling
        loadAvatar(user.getAvatarUrl());

        // Set role badge text based on role
        String roleText = getRoleText(user.getRole());
        binding.tvRole.setText(roleText);

        // Set member since
        binding.tvMemberSince
                .setText("Thành viên từ: " + (user.getCreatedAt() != null ? formatDate(user.getCreatedAt()) : "2024"));

        // Set statistics
        Integer activityCount = user.getActivityCount();
        binding.tvEventsCount.setText(String.format(Locale.getDefault(), "%d",
                activityCount != null ? activityCount : 0));

        // Set points
        Integer points = user.getTotalPoints();
        binding.tvPointsCount.setText(String.format(Locale.getDefault(), "%,d", points != null ? points : 0));
    }

    /**
     * Load avatar image with proper URL handling
     */
    private void loadAvatar(String avatarUrl) {
        if (binding == null || !isAdded()) {
            return;
        }

        try {
            // Check if avatar URL is valid
            if (avatarUrl != null && !avatarUrl.isEmpty() &&
                    !avatarUrl.equals("null") && !avatarUrl.equals("undefined")) {

                // Build full URL if needed
                String fullAvatarUrl = avatarUrl;
                if (!avatarUrl.startsWith("http://") && !avatarUrl.startsWith("https://")) {
                    // If relative URL, prepend base URL
                    fullAvatarUrl = "http://10.0.2.2:8081" + (avatarUrl.startsWith("/") ? "" : "/") + avatarUrl;
                }

                Log.d(TAG, "Loading avatar from: " + fullAvatarUrl);

                // Remove background and tint when loading real avatar
                binding.ivAvatar.setBackgroundResource(0);

                Glide.with(this)
                        .load(fullAvatarUrl)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .circleCrop()
                        .into(binding.ivAvatar);
            } else {
                Log.d(TAG, "No valid avatar URL, using default icon");
                // Set background for default icon
                binding.ivAvatar.setBackgroundResource(R.drawable.bg_avatar_circle);

                Glide.with(this)
                        .load(R.drawable.ic_user)
                        .circleCrop()
                        .into(binding.ivAvatar);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading avatar: " + e.getMessage(), e);
            // Fallback to default icon
            binding.ivAvatar.setBackgroundResource(R.drawable.bg_avatar_circle);

            Glide.with(this)
                    .load(R.drawable.ic_user)
                    .circleCrop()
                    .into(binding.ivAvatar);
        }
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty())
            return "2024";
        try {
            // Parse ISO 8601: "2024-10-15T10:30:00Z"
            java.time.Instant instant = java.time.Instant.parse(isoDate);
            java.time.LocalDate date = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            return "2024";
        }
    }

    /**
     * Set default user info when not logged in or error
     */
    private void setDefaultUserInfo() {
        if (binding == null)
            return;
        binding.tvFullName.setText("Người dùng");
        binding.tvEmail.setText("");
        binding.tvPhone.setText("");
        binding.tvRole.setText("Thành viên");
        binding.tvMemberSince.setText("Thành viên từ: 2024");
        binding.tvEventsCount.setText("0");
        binding.tvPointsCount.setText("0");

        // Set default avatar with background
        binding.ivAvatar.setBackgroundResource(R.drawable.bg_avatar_circle);
        Glide.with(this)
                .load(R.drawable.ic_user)
                .circleCrop()
                .into(binding.ivAvatar);
    }

    private String getRoleText(String role) {
        if (role == null)
            return "Thành viên";
        switch (role.toUpperCase()) {
            case "VOLUNTEER":
            case "ROLE_VOLUNTEER":
                return "Tình nguyện viên";
            case "ORGANIZATION":
            case "ROLE_ORGANIZATION":
                return "Tổ chức";
            case "ADMIN":
            case "ROLE_ADMIN":
                return "Quản trị viên";
            default:
                return "Thành viên";
        }
    }

    private void setupClickListeners() {

        binding.cardMyEvents.setOnClickListener(v -> {
            if (ApiConfig.isOrganizer()) {
                // If organizer, switch to Events tab in OrganizationActivity
                if (getActivity() instanceof com.manhhuy.myapplication.ui.Activities.OrganizationActivity) {
                    ((com.manhhuy.myapplication.ui.Activities.OrganizationActivity) getActivity()).switchToEventTab();
                }
            } else {
                // If volunteer, open MyEventsActivity
                Intent intent = new Intent(getActivity(), MyEventsActivity.class);
                startActivity(intent);
            }
        });

        binding.layoutMyCertificates.setOnClickListener(v -> {
            try {
                if (getActivity() instanceof UserActivity) {
                    ((UserActivity) getActivity()).switchToNofiticationTab();
                } else {
                    Toast.makeText(getContext(), "Xem thông báo", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error switching to certificate tab", e);
                Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        binding.layoutMyRewards.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyRewardActivity.class);
            startActivity(intent);
        });

        binding.layoutEditProfile.setOnClickListener(v -> {
            Log.d("MeFragment", "Edit profile clicked");
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivityForResult(intent, 200); // Request code 200 for profile edit
        });

        binding.layoutChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        binding.cardLogout.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });
    }

    private void showLogoutConfirmationDialog() {
        if (getActivity() == null)
            return;

        new AlertDialog.Builder(requireActivity())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    handleLogout();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void handleLogout() {
        if (getActivity() != null) {
            // Clear token from SharedPreferences
            ApiConfig.clearToken();
            Log.d(TAG, "Token cleared, logging out");

            // Redirect to login screen
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Reload user info after editing profile
        if (requestCode == 200 && resultCode == getActivity().RESULT_OK) {
            loadUserData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}