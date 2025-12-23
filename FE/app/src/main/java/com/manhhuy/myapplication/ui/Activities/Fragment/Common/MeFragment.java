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

                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<UserResponse> restResponse = response.body();

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

        // Set user information
        binding.tvFullName.setText(user.getFullName() != null ? user.getFullName() : "Người dùng");
        binding.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        binding.tvPhone.setText(user.getPhone() != null ? user.getPhone() : "");

        // Set role badge text based on role
        String roleText = getRoleText(user.getRole());
        binding.tvRole.setText(roleText);

        // Set member since (format createdAt if available, otherwise use default)
        // Note: Backend doesn't return createdAt yet, so we'll use a default
        binding.tvMemberSince.setText("Thành viên từ: 2024");

        // Set statistics
        Integer activityCount = user.getActivityCount();
        binding.tvEventsCount.setText(String.format(Locale.getDefault(), "%d",
                activityCount != null ? activityCount : 0));

        // Set points
        Integer points = user.getTotalPoints();
        binding.tvPointsCount.setText(String.format(Locale.getDefault(), "%,d", points != null ? points : 0));
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
    }

    private String getRoleText(String role) {
        switch (role) {
            case "VOLUNTEER":
                return "Tình nguyện viên";
            case "ORGANIZER":
                return "Tổ chức";
            case "ADMIN":
                return "Quản trị viên";
            default:
                return "Thành viên";
        }
    }

    private void setupClickListeners() {

        binding.ivSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cài đặt", Toast.LENGTH_SHORT).show();
        });

        binding.cardMyEvents.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyEventsActivity.class);
            startActivity(intent);
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