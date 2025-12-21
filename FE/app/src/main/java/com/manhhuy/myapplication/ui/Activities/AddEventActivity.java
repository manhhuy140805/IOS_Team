package com.manhhuy.myapplication.ui.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityAddEventBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.request.EventRequest;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.EventTypeResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "AddEventActivity";
    private ActivityAddEventBinding binding;
    
    private String selectedCategory = "";
    private Integer selectedEventTypeId = null;
    private String selectedStartDate = "";
    private String selectedEndDate = "";
    private boolean isLoading = false;
    
    // Edit mode
    private boolean isEditMode = false;
    private Integer eventId = null;
    private EventResponse currentEvent = null;

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
        
        // Check if edit mode
        isEditMode = getIntent().getBooleanExtra("IS_EDIT_MODE", false);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        
        // Update UI based on mode
        if (isEditMode) {
            binding.tvPageTitle.setText("Cập nhật Hoạt động tình nguyện");
            binding.tvPageSubtitle.setText("Chỉnh sửa thông tin hoạt động");
            binding.btnCreateEvent.setText("Cập nhật sự kiện");
        }
        
        if (isEditMode && eventId != -1) {
            loadEventDetails();
        }
        
        setupListeners();
        loadEventTypes();
    }
    
    private void loadEventTypes() {
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<RestResponse<List<EventTypeResponse>>> call = apiService.getEventTypes();
        
        call.enqueue(new Callback<RestResponse<List<EventTypeResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<EventTypeResponse>>> call,
                                 Response<RestResponse<List<EventTypeResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventTypeResponse> eventTypes = response.body().getData();
                    if (eventTypes != null && !eventTypes.isEmpty()) {
                        // Auto-select first event type
                        selectedEventTypeId = eventTypes.get(0).getId();
                        Log.d(TAG, "Loaded " + eventTypes.size() + " event types");
                    }
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<List<EventTypeResponse>>> call, Throwable t) {
                Log.e(TAG, "Failed to load event types: " + t.getMessage());
            }
        });
    }
    
    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnCreateEvent.setOnClickListener(v -> createEvent());
        
        setupCategoryListeners();
        setupDatePickers();
        
        binding.uploadImageContainer.setOnClickListener(v -> uploadImage());
        binding.mapContainer.setOnClickListener(v -> selectMapLocation());
    }
    
    private void setupDatePickers() {
        // Disable keyboard input, only allow date picker
        binding.etEventDate.setFocusable(false);
        binding.etEventDate.setClickable(true);
        binding.etEventDate.setOnClickListener(v -> showDatePicker(true));
        
        binding.etRegistrationDeadline.setFocusable(false);
        binding.etRegistrationDeadline.setClickable(true);
        binding.etRegistrationDeadline.setOnClickListener(v -> showDatePicker(false));
    }
    
    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateString = sdf.format(calendar.getTime());
                
                if (isStartDate) {
                    selectedStartDate = dateString;
                    binding.etEventDate.setText(dateString);
                } else {
                    selectedEndDate = dateString;
                    binding.etRegistrationDeadline.setText(dateString);
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
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
        selectedCategory = category;
        
        // Reset all category buttons
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
        if (isLoading) return;
        
        // Validate inputs - map to actual layout fields
        String title = binding.etEventTitle.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String location = binding.etAddress.getText().toString().trim(); // Use etAddress for location
        String volunteersStr = binding.etNumberNeeded.getText().toString().trim();
        String rewardPointsStr = ""; // No reward field in current layout
        
        if (title.isEmpty()) {
            showError("Vui lòng nhập tên sự kiện");
            return;
        }
        
        if (description.isEmpty()) {
            showError("Vui lòng nhập mô tả");
            return;
        }
        
        if (location.isEmpty()) {
            showError("Vui lòng nhập địa điểm");
            return;
        }
        
        if (selectedStartDate.isEmpty()) {
            showError("Vui lòng chọn ngày bắt đầu");
            return;
        }
        
        if (selectedEndDate.isEmpty()) {
            showError("Vui lòng chọn ngày kết thúc");
            return;
        }
        
        if (volunteersStr.isEmpty()) {
            showError("Vui lòng nhập số lượng tình nguyện viên");
            return;
        }
        
        if (selectedEventTypeId == null) {
            showError("Vui lòng chọn loại sự kiện");
            return;
        }
        
        int numVolunteers;
        try {
            numVolunteers = Integer.parseInt(volunteersStr);
            if (numVolunteers <= 0) {
                showError("Số lượng tình nguyện viên phải lớn hơn 0");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Số lượng tình nguyện viên không hợp lệ");
            return;
        }
        
        Integer rewardPoints = null;
        if (!rewardPointsStr.isEmpty()) {
            try {
                rewardPoints = Integer.parseInt(rewardPointsStr);
            } catch (NumberFormatException e) {
                showError("Điểm thưởng không hợp lệ");
                return;
            }
        }
        
        // Add city and district to location if available
        String city = binding.etCity.getText().toString().trim();
        String district = binding.etDistrict.getText().toString().trim();
        if (!city.isEmpty()) {
            location = location + ", " + city;
        }
        if (!district.isEmpty()) {
            location = location + ", " + district;
        }
        
        // Create event request
        EventRequest request = new EventRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setLocation(location);
        request.setEventStartTime(selectedStartDate);
        request.setEventEndTime(selectedEndDate);
        request.setNumOfVolunteers(numVolunteers);
        request.setRewardPoints(rewardPoints);
        request.setEventTypeId(selectedEventTypeId);
        request.setCategory(selectedCategory);
        request.setStatus("PENDING"); // Default status
        
        // Call API
        isLoading = true;
        showLoading();
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<EventResponse> call;
        
        if (isEditMode && eventId != null) {
            // Update existing event
            call = apiService.updateEvent(eventId, request);
        } else {
            // Create new event
            call = apiService.createEvent(request);
        }
        
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                isLoading = false;
                hideLoading();
                
                if (response.isSuccessful() && response.body() != null) {
                    showSuccess(isEditMode ? "Cập nhật sự kiện thành công!" : "Tạo sự kiện thành công!");
                    finish(); // Close activity and return to previous screen
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? 
                            response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Error: " + errorBody);
                        showError("Không thể " + (isEditMode ? "cập nhật" : "tạo") + " sự kiện: " + response.message());
                    } catch (Exception e) {
                        showError("Không thể " + (isEditMode ? "cập nhật" : "tạo") + " sự kiện");
                    }
                }
            }
            
            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                isLoading = false;
                hideLoading();
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    private void showLoading() {
        binding.btnCreateEvent.setEnabled(false);
        binding.btnCreateEvent.setText(isEditMode ? "Đang cập nhật..." : "Đang tạo...");
    }
    
    private void hideLoading() {
        binding.btnCreateEvent.setEnabled(true);
        binding.btnCreateEvent.setText(isEditMode ? "Cập nhật sự kiện" : "Tạo sự kiện");
    }
    
    private void loadEventDetails() {
        Log.d(TAG, "Loading event details for ID: " + eventId);
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<RestResponse<EventResponse>> call = apiService.getEventById(eventId);
        
        call.enqueue(new Callback<RestResponse<EventResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<EventResponse>> call, 
                                 Response<RestResponse<EventResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<EventResponse> restResponse = response.body();
                    currentEvent = restResponse.getData();
                    
                    if (currentEvent != null) {
                        Log.d(TAG, "Event loaded: " + currentEvent.getTitle());
                        populateEventData(currentEvent);
                    } else {
                        Log.e(TAG, "Event data is null in RestResponse");
                        Toast.makeText(AddEventActivity.this, 
                            "Không có dữ liệu sự kiện", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.e(TAG, "Failed to load event: " + response.code());
                    Toast.makeText(AddEventActivity.this, 
                        "Không thể tải thông tin sự kiện", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<EventResponse>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(AddEventActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    
    private void populateEventData(EventResponse event) {
        Log.d(TAG, "Populating event data: " + event.getTitle());
        
        // Fill form with event data
        binding.etEventTitle.setText(event.getTitle());
        binding.etDescription.setText(event.getDescription());
        
        // Parse location - might contain city and district
        String location = event.getLocation();
        if (location != null) {
            String[] parts = location.split(",");
            if (parts.length > 0) {
                binding.etAddress.setText(parts[0].trim());
            }
            if (parts.length > 1) {
                binding.etCity.setText(parts[1].trim());
            }
            if (parts.length > 2) {
                binding.etDistrict.setText(parts[2].trim());
            }
        }
        
        // Set number of volunteers
        if (event.getNumOfVolunteers() != null) {
            binding.etNumberNeeded.setText(String.valueOf(event.getNumOfVolunteers()));
        }
        
        // Set dates
        if (event.getEventStartTime() != null) {
            selectedStartDate = event.getEventStartTime();
            binding.etEventDate.setText(selectedStartDate);
            Log.d(TAG, "Start date: " + selectedStartDate);
        }
        
        if (event.getEventEndTime() != null) {
            selectedEndDate = event.getEventEndTime();
            binding.etRegistrationDeadline.setText(selectedEndDate);
            Log.d(TAG, "End date: " + selectedEndDate);
        }
        
        // Set event type
        if (event.getEventTypeId() != null) {
            selectedEventTypeId = event.getEventTypeId();
            Log.d(TAG, "Event type ID: " + selectedEventTypeId);
        }
        
        // Set category if available
        if (event.getCategory() != null && !event.getCategory().isEmpty()) {
            selectedCategory = event.getCategory();
            Log.d(TAG, "Category: " + selectedCategory);
            // Try to select the category button
            selectCategoryByName(selectedCategory);
        }
        
        // Update button text
        binding.btnCreateEvent.setText("Cập nhật sự kiện");
        
        Log.d(TAG, "Event data populated successfully");
    }
    
    private void selectCategoryByName(String categoryName) {
        // Map category name to button
        switch (categoryName.toLowerCase()) {
            case "cộng đồng":
            case "community":
                selectCategory("community");
                break;
            case "môi trường":
            case "environment":
                selectCategory("environment");
                break;
            case "giáo dục":
            case "education":
                selectCategory("education");
                break;
            case "y tế":
            case "health":
                selectCategory("health");
                break;
            case "động vật":
            case "animal":
                selectCategory("animal");
                break;
            default:
                selectCategory("other");
                break;
        }
    }
    
    private void uploadImage() {
        // TODO: Implement image upload
        Toast.makeText(this, "Chức năng upload ảnh đang phát triển", Toast.LENGTH_SHORT).show();
    }
    
    private void selectMapLocation() {
        // TODO: Implement map location picker
        Toast.makeText(this, "Chức năng chọn vị trí đang phát triển", Toast.LENGTH_SHORT).show();
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}