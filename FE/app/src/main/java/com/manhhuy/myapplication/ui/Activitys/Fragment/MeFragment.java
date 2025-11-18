package com.manhhuy.myapplication.ui.Activitys.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.manhhuy.myapplication.databinding.FragmentMeBinding;

import java.util.Locale;

public class MeFragment extends Fragment {

    private FragmentMeBinding binding;

    // Fake user data matching the User entity
    private String fullName = "Hiếu Võ Lập Trình";
    private String email = "vndhieuak@gmail.com";
    private String phone = "+84 987 654 321";
// Using default icon
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

        // If we have avatar URL, we would load it with Glide or Picasso here
        // For now, using default drawable
        // TODO: Load avatar with Glide.with(this).load(avatarUrl).into(binding.ivAvatar);
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
        // Settings button
        binding.ivSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cài đặt", Toast.LENGTH_SHORT).show();
            // Navigate to settings screen
        });

        // My Events
        binding.cardMyEvents.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Sự kiện của tôi", Toast.LENGTH_SHORT).show();
            // Navigate to my events screen
        });

        // My Certificates
        binding.cardMyCertificates.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chứng chỉ của tôi", Toast.LENGTH_SHORT).show();
            // Navigate to certificates screen
        });

        // My Rewards
        binding.cardMyRewards.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Phần thưởng của tôi", Toast.LENGTH_SHORT).show();
            // Navigate to rewards screen
        });

        // Edit Profile
        binding.cardEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chỉnh sửa hồ sơ", Toast.LENGTH_SHORT).show();
            // Navigate to edit profile screen
        });

        // Logout
        binding.cardLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đăng xuất", Toast.LENGTH_SHORT).show();
            // Show confirmation dialog and logout
            handleLogout();
        });
    }

    private void handleLogout() {
        // TODO: Clear session, navigate to login screen
        // For now, just show a message
        if (getActivity() != null) {
            // Clear any stored tokens/session
            // Intent intent = new Intent(getActivity(), LoginActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}