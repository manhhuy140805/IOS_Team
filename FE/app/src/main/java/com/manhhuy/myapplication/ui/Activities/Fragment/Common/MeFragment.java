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
import com.manhhuy.myapplication.ui.Activities.Fragment.User.MyEventsActivity;
import com.manhhuy.myapplication.ui.Activities.MainActivity;
import com.manhhuy.myapplication.ui.Activities.UserActivity;

import java.util.Locale;

public class MeFragment extends Fragment {

    private static final String TAG = "MeFragment";
    private FragmentMeBinding binding;

    // Fake user data matching the User entity
    private String fullName = "Hiếu Võ Lập Trình";
    private String email = "vndhieuak@gmail.com";
    private String phone = "+84 987 654 321";
    private String role = "VOLUNTEER"; // VOLUNTEER, ORGANIZER, ADMIN
    private String status = "ACTIVE"; // ACTIVE, LOCKED, PENDING
    private int eventsParticipated = 12;
    private int pointsEarned = 1250;
    private String memberSince = "15/03/2024";

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

        loadUserData();
        setupClickListeners();
    }

    private void loadUserData() {
        // Set user information
        binding.tvFullName.setText(fullName);
        binding.tvEmail.setText(email);
        binding.tvPhone.setText(phone);

        // Set role badge text based on role
        String roleText = getRoleText(role);
        binding.tvRole.setText(roleText);

        // Set member since
        binding.tvMemberSince.setText("Thành viên từ: " + memberSince);

        // Set statistics
        binding.tvEventsCount.setText(String.valueOf(eventsParticipated));
        binding.tvPointsCount.setText(String.format(Locale.getDefault(), "%,d", pointsEarned));
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

        binding.cardMyRewards.setOnClickListener(v -> {
            try {
                if (getActivity() instanceof UserActivity) {
                    ((UserActivity) getActivity()).switchToRedeemTab();
                } else {
                    Toast.makeText(getContext(), "Phần thưởng của tôi", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Phần thưởng của tôi", Toast.LENGTH_SHORT).show();
            }
        });

        binding.cardEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chỉnh sửa hồ sơ", Toast.LENGTH_SHORT).show();
        });

        binding.cardLogout.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });
    }

    private void showLogoutConfirmationDialog() {
        if (getActivity() == null) return;

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
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}