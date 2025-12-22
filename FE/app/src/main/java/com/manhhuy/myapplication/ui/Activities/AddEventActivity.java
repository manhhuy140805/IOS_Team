package com.manhhuy.myapplication.ui.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityAddEventBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.request.EventRequest;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.EventTypeResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity {

    private ActivityAddEventBinding binding;
    
    private Uri selectedImageUri;
    private String uploadedImageUrl;
    private ImageView ivEventImage;
    
    private String selectedCategory = "";
    private Integer selectedEventTypeId = null;
    private String selectedStartDate = "";
    private String selectedEndDate = "";
    private boolean isLoading = false;
    
    // Edit mode
    private boolean isEditMode = false;
    private Integer eventId = null;
    private EventResponse currentEvent = null;
    
    private final ActivityResultLauncher<Intent> imagePickerLauncher = 
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    displaySelectedImage();
                    uploadImageToCloudinary();
                }
            });

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
        
        apiService.getEventTypes().enqueue(new Callback<RestResponse<List<EventTypeResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<EventTypeResponse>>> call,
                                 Response<RestResponse<List<EventTypeResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventTypeResponse> eventTypes = response.body().getData();
                    if (eventTypes != null && !eventTypes.isEmpty()) {
                        selectedEventTypeId = eventTypes.get(0).getId();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<List<EventTypeResponse>>> call, Throwable t) {
                // Ignore
            }
        });
    }
    
    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnCreateEvent.setOnClickListener(v -> createEvent());
        
        setupCategoryListeners();
        setupDatePickers();
        
        // Find ImageView inside uploadImageContainer
        ivEventImage = new ImageView(this);
        ivEventImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        binding.uploadImageContainer.setOnClickListener(v -> openImagePicker());
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
        resetAllCategoryButtons();
        highlightCategoryButton(category);
    }
    
    private void resetAllCategoryButtons() {
        int defaultBg = R.drawable.bg_custom_edit_text;
        int defaultColor = R.color.text_primary;
        
        binding.btnCategoryCommunity.setBackgroundResource(defaultBg);
        binding.btnCategoryEnvironment.setBackgroundResource(defaultBg);
        binding.btnCategoryEducation.setBackgroundResource(defaultBg);
        binding.btnCategoryHealth.setBackgroundResource(defaultBg);
        binding.btnCategoryAnimal.setBackgroundResource(defaultBg);
        binding.btnCategoryOther.setBackgroundResource(defaultBg);
        
        binding.btnCategoryCommunity.setCompoundDrawableTintList(getColorStateList(defaultColor));
        binding.btnCategoryEnvironment.setCompoundDrawableTintList(getColorStateList(defaultColor));
        binding.btnCategoryEducation.setCompoundDrawableTintList(getColorStateList(defaultColor));
        binding.btnCategoryHealth.setCompoundDrawableTintList(getColorStateList(defaultColor));
        binding.btnCategoryAnimal.setCompoundDrawableTintList(getColorStateList(defaultColor));
        binding.btnCategoryOther.setCompoundDrawableTintList(getColorStateList(defaultColor));
    }
    
    private void highlightCategoryButton(String category) {
        int selectedBg = R.drawable.bg_category_tab_selected_reward;
        int selectedColor = R.color.app_green_primary;
        
        switch (category) {
            case "community":
                binding.btnCategoryCommunity.setBackgroundResource(selectedBg);
                binding.btnCategoryCommunity.setCompoundDrawableTintList(getColorStateList(selectedColor));
                break;
            case "environment":
                binding.btnCategoryEnvironment.setBackgroundResource(selectedBg);
                binding.btnCategoryEnvironment.setCompoundDrawableTintList(getColorStateList(selectedColor));
                break;
            case "education":
                binding.btnCategoryEducation.setBackgroundResource(selectedBg);
                binding.btnCategoryEducation.setCompoundDrawableTintList(getColorStateList(selectedColor));
                break;
            case "health":
                binding.btnCategoryHealth.setBackgroundResource(selectedBg);
                binding.btnCategoryHealth.setCompoundDrawableTintList(getColorStateList(selectedColor));
                break;
            case "animal":
                binding.btnCategoryAnimal.setBackgroundResource(selectedBg);
                binding.btnCategoryAnimal.setCompoundDrawableTintList(getColorStateList(selectedColor));
                break;
            case "other":
                binding.btnCategoryOther.setBackgroundResource(selectedBg);
                binding.btnCategoryOther.setCompoundDrawableTintList(getColorStateList(selectedColor));
                break;
        }
    }
    
    private void createEvent() {
        if (isLoading) return;
        
        String title = binding.etEventTitle.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String location = binding.etAddress.getText().toString().trim();
        String volunteersStr = binding.etNumberNeeded.getText().toString().trim();
        
        if (!validateInputs(title, description, location, volunteersStr)) {
            return;
        }
        
        int numVolunteers = Integer.parseInt(volunteersStr);
        EventRequest request = buildEventRequest(title, description, location, numVolunteers);
        
        isLoading = true;
        showLoading();
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<EventResponse> call = isEditMode && eventId != null 
                ? apiService.updateEvent(eventId, request)
                : apiService.createEvent(request);
        
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                isLoading = false;
                hideLoading();
                
                if (response.isSuccessful() && response.body() != null) {
                    showSuccess(isEditMode ? "Cập nhật sự kiện thành công!" : "Tạo sự kiện thành công!");
                    finish();
                } else {
                    showError("Không thể " + (isEditMode ? "cập nhật" : "tạo") + " sự kiện");
                }
            }
            
            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                isLoading = false;
                hideLoading();
                showError("Lỗi kết nối");
            }
        });
    }
    
    private boolean validateInputs(String title, String description, String location, String volunteersStr) {
        if (title.isEmpty()) {
            showError("Vui lòng nhập tên sự kiện");
            return false;
        }
        if (description.isEmpty()) {
            showError("Vui lòng nhập mô tả");
            return false;
        }
        if (location.isEmpty()) {
            showError("Vui lòng nhập địa điểm");
            return false;
        }
        if (selectedStartDate.isEmpty()) {
            showError("Vui lòng chọn ngày bắt đầu");
            return false;
        }
        if (selectedEndDate.isEmpty()) {
            showError("Vui lòng chọn ngày kết thúc");
            return false;
        }
        if (volunteersStr.isEmpty()) {
            showError("Vui lòng nhập số lượng tình nguyện viên");
            return false;
        }
        if (selectedEventTypeId == null) {
            showError("Vui lòng chọn loại sự kiện");
            return false;
        }
        
        try {
            int volunteers = Integer.parseInt(volunteersStr);
            if (volunteers <= 0) {
                showError("Số lượng tình nguyện viên phải lớn hơn 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Số lượng tình nguyện viên không hợp lệ");
            return false;
        }
        
        return true;
    }
    
    private String buildLocation(String location) {
        String city = binding.etCity.getText().toString().trim();
        String district = binding.etDistrict.getText().toString().trim();
        
        if (!city.isEmpty()) location += ", " + city;
        if (!district.isEmpty()) location += ", " + district;
        
        return location;
    }
    
    private EventRequest buildEventRequest(String title, String description, String location, int numVolunteers) {
        EventRequest request = new EventRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setLocation(buildLocation(location));
        request.setEventStartTime(selectedStartDate);
        request.setEventEndTime(selectedEndDate);
        request.setNumOfVolunteers(numVolunteers);
        request.setRewardPoints(null);
        request.setEventTypeId(selectedEventTypeId);
        request.setCategory(selectedCategory);
        request.setStatus("PENDING");
        request.setImageUrl(uploadedImageUrl);
        return request;
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
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        
        apiService.getEventById(eventId).enqueue(new Callback<RestResponse<EventResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<EventResponse>> call, 
                                 Response<RestResponse<EventResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentEvent = response.body().getData();
                    populateEventData(currentEvent);
                } else {
                    showError("Không thể tải thông tin sự kiện");
                    finish();
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<EventResponse>> call, Throwable t) {
                showError("Lỗi kết nối");
                finish();
            }
        });
    }
    
    private void populateEventData(EventResponse event) {
        binding.etEventTitle.setText(event.getTitle());
        binding.etDescription.setText(event.getDescription());
        
        // Parse location
        if (event.getLocation() != null) {
            String[] parts = event.getLocation().split(",");
            if (parts.length > 0) binding.etAddress.setText(parts[0].trim());
            if (parts.length > 1) binding.etCity.setText(parts[1].trim());
            if (parts.length > 2) binding.etDistrict.setText(parts[2].trim());
        }
        
        if (event.getNumOfVolunteers() != null) {
            binding.etNumberNeeded.setText(String.valueOf(event.getNumOfVolunteers()));
        }
        
        if (event.getEventStartTime() != null) {
            selectedStartDate = event.getEventStartTime();
            binding.etEventDate.setText(selectedStartDate);
        }
        
        if (event.getEventEndTime() != null) {
            selectedEndDate = event.getEventEndTime();
            binding.etRegistrationDeadline.setText(selectedEndDate);
        }
        
        if (event.getEventTypeId() != null) {
            selectedEventTypeId = event.getEventTypeId();
        }
        
        if (event.getCategory() != null && !event.getCategory().isEmpty()) {
            selectedCategory = event.getCategory();
            selectCategoryByName(selectedCategory);
        }
        
        // Load existing image
        uploadedImageUrl = event.getImageUrl();
        if (uploadedImageUrl != null && !uploadedImageUrl.isEmpty() && !isFinishing()) {
            binding.uploadImageContainer.removeAllViews();
            binding.uploadImageContainer.addView(ivEventImage);
            
            Glide.with(this)
                    .load(uploadedImageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_download)
                    .error(R.drawable.ic_download)
                    .into(ivEventImage);
        }
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
    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    
    private void displaySelectedImage() {
        if (isFinishing() || binding == null) return;
        
        // Clear previous views and add image
        binding.uploadImageContainer.removeAllViews();
        binding.uploadImageContainer.addView(ivEventImage);
        
        Glide.with(this)
                .load(selectedImageUri)
                .centerCrop()
                .into(ivEventImage);
    }
    
    private void uploadImageToCloudinary() {
        if (selectedImageUri == null || binding == null) return;
        
        showLoading();

        try {
            String mimeType = getContentResolver().getType(selectedImageUri);
            if (mimeType == null || !mimeType.startsWith("image/")) {
                mimeType = "image/jpeg";
            }
            
            File file = createFileFromUri(selectedImageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
            apiService.uploadImage(body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    file.delete();
                    hideLoading();
                    
                    if (binding == null || isFinishing()) return;

                    if (response.isSuccessful() && response.body() != null) {
                        Object dataObj = response.body().get("data");
                        if (dataObj instanceof Map) {
                            Map<String, Object> data = (Map<String, Object>) dataObj;
                            uploadedImageUrl = (String) data.get("imageUrl");
                            showSuccess("Ảnh đã được tải lên!");
                        } else {
                            showError("Upload thất bại");
                        }
                    } else {
                        showError("Upload thất bại");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    file.delete();
                    hideLoading();
                    if (binding == null || isFinishing()) return;
                    showError("Lỗi kết nối");
                }
            });
        } catch (Exception e) {
            hideLoading();
            showError("Lỗi đọc file");
        }
    }
    
    private File createFileFromUri(Uri uri) throws Exception {
        File file = new File(getCacheDir(), "upload_" + System.currentTimeMillis() + ".jpg");
        
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            
            if (inputStream == null) {
                throw new Exception("Cannot open input stream");
            }
            
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        
        return file;
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