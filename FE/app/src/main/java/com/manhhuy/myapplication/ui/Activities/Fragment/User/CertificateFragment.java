package com.manhhuy.myapplication.ui.Activities.Fragment.User;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.manhhuy.myapplication.databinding.FragmentCertificateBinding;

public class CertificateFragment extends Fragment {

    private FragmentCertificateBinding binding;

    // Sample certificate data
    private String userName = "Nguyễn Văn An";
    private String eventName = "Beach Cleanup - Vung Tau";
    private String eventDate = "15/11/2025";
    private String duration = "4 giờ";
    private String location = "Bãi Sau, Vũng Tàu";
    private String organization = "Green Vietnam";
    private String certificateCode = "VC-2025-001234";
    private String issueDate = "16/11/2025";
    private int pointsEarned = 200;
    private int totalPoints = 4285;

    public CertificateFragment() {
        // Required empty public constructor
    }

    public static CertificateFragment newInstance() {
        return new CertificateFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCertificateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        loadCertificateData();
        setupListeners();
    }

    private void loadCertificateData() {
        // Set certificate data
        binding.tvName.setText(userName);
        binding.tvEventName.setText("\"" + eventName + "\"");
        binding.tvDate.setText(eventDate);
        binding.tvDuration.setText(duration);
        binding.tvLocation.setText(location);
        binding.tvOrganization.setText(organization);
        binding.tvCertificateCode.setText(certificateCode);
        binding.tvIssueDate.setText(issueDate);
        binding.tvPoints.setText("+" + pointsEarned);
        binding.tvTotalPoints.setText(String.format("%,d", totalPoints));
    }
    
    private void setupListeners() {
        // Back button - Hide in fragment when used as tab
        binding.btnBack.setVisibility(View.GONE);
        
        // Share button in toolbar
        binding.btnLink.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chia sẻ chứng nhận", Toast.LENGTH_SHORT).show();
            // TODO: Implement share functionality
        });
        
        // Download PDF button
        binding.btnDownloadPdf.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tải xuống PDF", Toast.LENGTH_SHORT).show();
            // TODO: Implement PDF download
        });
        
        // Share button
        binding.btnShare.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chia sẻ chứng nhận", Toast.LENGTH_SHORT).show();
            // TODO: Implement share functionality
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
