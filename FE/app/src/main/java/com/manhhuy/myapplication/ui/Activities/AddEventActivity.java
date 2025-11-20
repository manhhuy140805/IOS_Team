package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityAddEventBinding;

public class AddEventActivity extends AppCompatActivity {

    private ActivityAddEventBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityAddEventBinding.inflate(getLayoutInflater());
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
        
        // Cancel button
        binding.btnCancel.setOnClickListener(v -> finish());
        
        // Create Event button
        binding.btnCreateEvent.setOnClickListener(v -> createEvent());
        
        // Category button listeners
        setupCategoryListeners();
        
        // Image upload listener
        binding.uploadImageContainer.setOnClickListener(v -> uploadImage());
        
        // Map location listener
        binding.mapContainer.setOnClickListener(v -> selectMapLocation());
    }
    
    private void setupCategoryListeners() {
        // Community button listener
        binding.btnCategoryCommunity.setOnClickListener(v -> selectCategory("community"));
        
        // Environment button listener
        binding.btnCategoryEnvironment.setOnClickListener(v -> selectCategory("environment"));
        
        // Education button listener
        binding.btnCategoryEducation.setOnClickListener(v -> selectCategory("education"));
        
        // Health button listener
        binding.btnCategoryHealth.setOnClickListener(v -> selectCategory("health"));
        
        // Animal button listener
        binding.btnCategoryAnimal.setOnClickListener(v -> selectCategory("animal"));
        
        // Other button listener
        binding.btnCategoryOther.setOnClickListener(v -> selectCategory("other"));
    }
    
    private void selectCategory(String category) {
        // Reset all buttons to unselected style
        binding.btnCategoryCommunity.setBackgroundResource(R.drawable.bg_custom_edit_text);
        binding.btnCategoryEnvironment.setBackgroundResource(R.drawable.bg_custom_edit_text);
        binding.btnCategoryEducation.setBackgroundResource(R.drawable.bg_custom_edit_text);
        binding.btnCategoryHealth.setBackgroundResource(R.drawable.bg_custom_edit_text);
        binding.btnCategoryAnimal.setBackgroundResource(R.drawable.bg_custom_edit_text);
        binding.btnCategoryOther.setBackgroundResource(R.drawable.bg_custom_edit_text);
        
        // Set tint to text_primary for all
        binding.btnCategoryCommunity.setCompoundDrawableTintList(
            getColorStateList(R.color.text_primary));
        binding.btnCategoryEnvironment.setCompoundDrawableTintList(
            getColorStateList(R.color.text_primary));
        binding.btnCategoryEducation.setCompoundDrawableTintList(
            getColorStateList(R.color.text_primary));
        binding.btnCategoryHealth.setCompoundDrawableTintList(
            getColorStateList(R.color.text_primary));
        binding.btnCategoryAnimal.setCompoundDrawableTintList(
            getColorStateList(R.color.text_primary));
        binding.btnCategoryOther.setCompoundDrawableTintList(
            getColorStateList(R.color.text_primary));
        
        // Set selected category style
        switch (category) {
            case "community":
                binding.btnCategoryCommunity.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
                binding.btnCategoryCommunity.setCompoundDrawableTintList(
                    getColorStateList(R.color.app_green_primary));
                break;
            case "environment":
                binding.btnCategoryEnvironment.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
                binding.btnCategoryEnvironment.setCompoundDrawableTintList(
                    getColorStateList(R.color.app_green_primary));
                break;
            case "education":
                binding.btnCategoryEducation.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
                binding.btnCategoryEducation.setCompoundDrawableTintList(
                    getColorStateList(R.color.app_green_primary));
                break;
            case "health":
                binding.btnCategoryHealth.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
                binding.btnCategoryHealth.setCompoundDrawableTintList(
                    getColorStateList(R.color.app_green_primary));
                break;
            case "animal":
                binding.btnCategoryAnimal.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
                binding.btnCategoryAnimal.setCompoundDrawableTintList(
                    getColorStateList(R.color.app_green_primary));
                break;
            case "other":
                binding.btnCategoryOther.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
                binding.btnCategoryOther.setCompoundDrawableTintList(
                    getColorStateList(R.color.app_green_primary));
                break;
        }
    }
    
    private void createEvent() {
        // Get all input values
        String eventTitle = binding.etEventTitle.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String eventDate = binding.etEventDate.getText().toString().trim();
        String startTime = binding.etStartTime.getText().toString().trim();
        String duration = binding.etDuration.getText().toString().trim();
        String registrationDeadline = binding.etRegistrationDeadline.getText().toString().trim();
        String numberNeeded = binding.etNumberNeeded.getText().toString().trim();
        String ageRequirement = binding.etAgeRequirement.getText().toString().trim();
        String requirements = binding.etRequirements.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String district = binding.etDistrict.getText().toString().trim();
        
        // Validate inputs
        if (eventTitle.isEmpty() || description.isEmpty() || eventDate.isEmpty() || 
            startTime.isEmpty() || duration.isEmpty() || numberNeeded.isEmpty() || 
            address.isEmpty() || city.isEmpty()) {
            showError("Vui lòng điền đầy đủ thông tin bắt buộc");
            return;
        }
        
        // TODO: Send event creation request to server
        // showSuccess("Event created successfully");
    }
    
    private void uploadImage() {
        // TODO: Implement image picker functionality
    }
    
    private void selectMapLocation() {
        // TODO: Implement map location selection
    }
    
    private void showError(String message) {
        // TODO: Show error dialog or toast
    }
    
    private void showSuccess(String message) {
        // TODO: Show success dialog or toast
    }
}