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

        binding.btnBack.setOnClickListener(v -> finish());
        

        binding.btnCancel.setOnClickListener(v -> finish());
        

        binding.btnCreateEvent.setOnClickListener(v -> createEvent());

        setupCategoryListeners();
        

        binding.uploadImageContainer.setOnClickListener(v -> uploadImage());

        binding.mapContainer.setOnClickListener(v -> selectMapLocation());
    }
    
    private void setupCategoryListeners() {

        binding.btnCategoryCommunity.setOnClickListener(v -> selectCategory("community"));
        

        binding.btnCategoryEnvironment.setOnClickListener(v -> selectCategory("environment"));

        binding.btnCategoryEducation.setOnClickListener(v -> selectCategory("education"));
        

        binding.btnCategoryHealth.setOnClickListener(v -> selectCategory("health"));
        

        binding.btnCategoryAnimal.setOnClickListener(v -> selectCategory("animal"));

        binding.btnCategoryOther.setOnClickListener(v -> selectCategory("other"));
    }
    
    private void selectCategory(String category) {

        binding.btnCategoryCommunity.setBackgroundResource(R.drawable.bg_custom_edit_text);
        binding.btnCategoryEnvironment.setBackgroundResource(R.drawable.bg_custom_edit_text);
        binding.btnCategoryEducation.setBackgroundResource(R.drawable.bg_custom_edit_text);
        binding.btnCategoryHealth.setBackgroundResource(R.drawable.bg_custom_edit_text);
        binding.btnCategoryAnimal.setBackgroundResource(R.drawable.bg_custom_edit_text);
        binding.btnCategoryOther.setBackgroundResource(R.drawable.bg_custom_edit_text);
        

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


    }
    
    private void uploadImage() {

    }
    
    private void selectMapLocation() {

    }
    
    private void showError(String message) {

    }
    
    private void showSuccess(String message) {

    }
}