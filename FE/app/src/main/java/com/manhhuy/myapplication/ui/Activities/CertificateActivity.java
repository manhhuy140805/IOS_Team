package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityCertificateBinding;

public class CertificateActivity extends AppCompatActivity {

    private ActivityCertificateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityCertificateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        setupListeners();
    }
    
    private void setupListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());
        
        // Share button in toolbar
        binding.btnLink.setOnClickListener(v -> {
            // TODO: Implement share functionality
        });
        
        // Download PDF button
        binding.btnDownloadPdf.setOnClickListener(v -> {
            // TODO: Implement PDF download
        });
        
        // Share button
        binding.btnShare.setOnClickListener(v -> {
            // TODO: Implement share functionality
        });
    }
}