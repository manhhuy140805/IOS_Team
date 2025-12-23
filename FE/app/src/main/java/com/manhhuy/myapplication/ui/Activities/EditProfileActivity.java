package com.manhhuy.myapplication.ui.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityEditProfileBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.request.UpdateUserRequest;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.UserResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ActivityEditProfileBinding binding;
    private UserResponse currentUser;
    private ProgressDialog progressDialog;
    private boolean isUpdating = false;
    private String newAvatarUrl = null;
    private String selectedDateOfBirth = null;
    private Calendar calendar = Calendar.getInstance();
    
    // Image picker launcher
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupImagePicker();
        setupToolbar();
        loadCurrentUserInfo();
        setupClickListeners();
    }
    
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG, "Image picker result received");
                Log.d(TAG, "Result code: " + result.getResultCode());
                Log.d(TAG, "RESULT_OK: " + RESULT_OK);
                
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    Log.d(TAG, "Image URI: " + imageUri);
                    
                    if (imageUri != null) {
                        Log.d(TAG, "Starting upload to Cloudinary");
                        uploadImageToCloudinary(imageUri);
                    } else {
                        Log.e(TAG, "Image URI is null");
                        Toast.makeText(this, "Không thể lấy ảnh", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Image picker cancelled or failed");
                }
            }
        );
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        binding.btnChangeAvatar.setOnClickListener(v -> {
            Log.d(TAG, "Change avatar button clicked");
            checkPermissionAndOpenPicker();
        });

        binding.etDateOfBirth.setOnClickListener(v -> {
            showDatePicker();
        });

        binding.btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                updateProfile();
            }
        });
    }
    
    private void checkPermissionAndOpenPicker() {
        // For Android 13+ (API 33+), use READ_MEDIA_IMAGES
        // For Android 10-12 (API 29-32), use READ_EXTERNAL_STORAGE
        // For Android 9 and below, use READ_EXTERNAL_STORAGE
        
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        
        Log.d(TAG, "Checking permission: " + permission);
        Log.d(TAG, "Android version: " + Build.VERSION.SDK_INT);
        
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission granted, opening image picker");
            openImagePicker();
        } else {
            Log.d(TAG, "Permission not granted, requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted by user");
                openImagePicker();
            } else {
                Log.d(TAG, "Permission denied by user");
                Toast.makeText(this, "Cần cấp quyền truy cập ảnh để thay đổi avatar", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void openImagePicker() {
        Log.d(TAG, "Opening image picker");
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            Log.d(TAG, "Launching image picker intent");
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening image picker", e);
            Toast.makeText(this, "Không thể mở thư viện ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                // Format for display (dd/MM/yyyy)
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                binding.etDateOfBirth.setText(displayFormat.format(calendar.getTime()));
                
                // Format for API (yyyy-MM-dd)
                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDateOfBirth = apiFormat.format(calendar.getTime());
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set max date to today (can't select future date)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    
    private void uploadImageToCloudinary(Uri imageUri) {
        Log.d(TAG, "=== UPLOAD IMAGE TO CLOUDINARY ===");
        Log.d(TAG, "Image URI: " + imageUri);
        
        showProgressDialog("Đang tải ảnh lên...");
        
        try {
            // Get MIME type from URI
            String mimeType = getContentResolver().getType(imageUri);
            Log.d(TAG, "MIME type from URI: " + mimeType);
            
            // If MIME type is null, try to detect from file extension
            if (mimeType == null) {
                mimeType = "image/jpeg"; // default
                Log.d(TAG, "MIME type is null, using default: " + mimeType);
            }
            
            // Convert URI to File
            File file = getFileFromUri(imageUri);
            if (file == null) {
                dismissProgressDialog();
                Log.e(TAG, "Failed to convert URI to File");
                Toast.makeText(this, "Không thể đọc file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d(TAG, "File created: " + file.getAbsolutePath());
            Log.d(TAG, "File size: " + file.length() + " bytes");
            
            // Create multipart body with correct MIME type
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            
            Log.d(TAG, "Multipart body created with MIME type: " + mimeType);
            Log.d(TAG, "Calling upload API...");
            
            // Upload to Cloudinary
            ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
            Call<Map<String, Object>> call = apiService.uploadImage(body);
            
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, 
                                     Response<Map<String, Object>> response) {
                    dismissProgressDialog();
                    
                    Log.d(TAG, "Upload response received");
                    Log.d(TAG, "Response code: " + response.code());
                    Log.d(TAG, "Response successful: " + response.isSuccessful());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> result = response.body();
                        Log.d(TAG, "Response body: " + result.toString());
                        
                        // Backend wraps response in RestResponse with "data" field
                        Object dataObj = result.get("data");
                        if (dataObj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> data = (Map<String, Object>) dataObj;
                            newAvatarUrl = (String) data.get("imageUrl");
                        } else {
                            // Fallback: try to get directly from result
                            newAvatarUrl = (String) result.get("imageUrl");
                            if (newAvatarUrl == null) {
                                newAvatarUrl = (String) result.get("url");
                            }
                        }
                        
                        Log.d(TAG, "New avatar URL: " + newAvatarUrl);
                        
                        if (newAvatarUrl != null) {
                            // Display new avatar
                            Glide.with(EditProfileActivity.this)
                                .load(newAvatarUrl)
                                .placeholder(R.drawable.ic_user)
                                .error(R.drawable.ic_user)
                                .circleCrop()
                                .into(binding.ivAvatar);
                            
                            Toast.makeText(EditProfileActivity.this, 
                                "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "URL is null in response");
                            Toast.makeText(EditProfileActivity.this, 
                                "Không nhận được URL ảnh", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Upload failed with code: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        Toast.makeText(EditProfileActivity.this, 
                            "Không thể tải ảnh lên: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    dismissProgressDialog();
                    Log.e(TAG, "Upload failed", t);
                    Log.e(TAG, "Error message: " + t.getMessage());
                    Toast.makeText(EditProfileActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            dismissProgressDialog();
            Log.e(TAG, "Error preparing file", e);
            Log.e(TAG, "Error message: " + e.getMessage());
            Toast.makeText(this, "Lỗi xử lý file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            
            File file = new File(getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            outputStream.close();
            inputStream.close();
            
            return file;
        } catch (Exception e) {
            Log.e(TAG, "Error converting URI to File: " + e.getMessage(), e);
            return null;
        }
    }

    private void loadCurrentUserInfo() {
        showLoading();

        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<RestResponse<UserResponse>> call = apiService.getCurrentUser();

        call.enqueue(new Callback<RestResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<UserResponse>> call,
                                 Response<RestResponse<UserResponse>> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<UserResponse> restResponse = response.body();
                    currentUser = restResponse.getData();

                    if (currentUser != null) {
                        displayUserInfo();
                    } else {
                        Toast.makeText(EditProfileActivity.this,
                            "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this,
                        "Lỗi tải thông tin: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<UserResponse>> call, Throwable t) {
                hideLoading();
                Toast.makeText(EditProfileActivity.this,
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayUserInfo() {
        if (currentUser == null) return;

        // Set text fields
        binding.etFullName.setText(currentUser.getFullName());
        binding.etPhone.setText(currentUser.getPhone());
        binding.etEmail.setText(currentUser.getEmail());
        binding.etAddress.setText(currentUser.getAddress());

        // Set date of birth
        if (currentUser.getDateOfBirth() != null && !currentUser.getDateOfBirth().isEmpty()) {
            try {
                // Parse yyyy-MM-dd from API
                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                java.util.Date date = apiFormat.parse(currentUser.getDateOfBirth());
                
                if (date != null) {
                    calendar.setTime(date);
                    selectedDateOfBirth = currentUser.getDateOfBirth();
                    
                    // Display as dd/MM/yyyy
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    binding.etDateOfBirth.setText(displayFormat.format(date));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + e.getMessage());
            }
        }

        // Set gender
        if (currentUser.getGender() != null) {
            switch (currentUser.getGender()) {
                case "MALE":
                    binding.rbMale.setChecked(true);
                    break;
                case "FEMALE":
                    binding.rbFemale.setChecked(true);
                    break;
                case "OTHER":
                    binding.rbOther.setChecked(true);
                    break;
                default:
                    binding.rbMale.setChecked(true);
                    break;
            }
        } else {
            binding.rbMale.setChecked(true);
        }

        // Load avatar
        if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                .load(currentUser.getAvatarUrl())
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .circleCrop()
                .into(binding.ivAvatar);
        }
    }

    private boolean validateInput() {
        String fullName = binding.etFullName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        if (fullName.isEmpty()) {
            binding.etFullName.setError("Vui lòng nhập họ tên");
            binding.etFullName.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            binding.etPhone.setError("Vui lòng nhập số điện thoại");
            binding.etPhone.requestFocus();
            return false;
        }

        if (!phone.matches("^0\\d{9}$")) {
            binding.etPhone.setError("Số điện thoại không hợp lệ");
            binding.etPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void updateProfile() {
        if (isUpdating || currentUser == null) return;

        String fullName = binding.etFullName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        
        // Get gender
        String gender = "MALE"; // default
        if (binding.rbFemale.isChecked()) {
            gender = "FEMALE";
        } else if (binding.rbOther.isChecked()) {
            gender = "OTHER";
        }

        isUpdating = true;
        showProgressDialog("Đang cập nhật...");

        // Use new avatar URL if uploaded, otherwise keep current
        String avatarUrl = newAvatarUrl != null ? newAvatarUrl : currentUser.getAvatarUrl();
        
        UpdateUserRequest request = new UpdateUserRequest(
            fullName, 
            phone, 
            address, 
            avatarUrl,
            selectedDateOfBirth,  // Can be null if not changed
            gender
        );
        
        Log.d(TAG, "Updating current user");
        Log.d(TAG, "Full name: " + fullName);
        Log.d(TAG, "Phone: " + phone);
        Log.d(TAG, "Address: " + address);
        Log.d(TAG, "Avatar URL: " + avatarUrl);
        Log.d(TAG, "Date of Birth: " + selectedDateOfBirth);
        Log.d(TAG, "Gender: " + gender);

        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<RestResponse<UserResponse>> call = apiService.updateCurrentUser(request);

        call.enqueue(new Callback<RestResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<UserResponse>> call,
                                 Response<RestResponse<UserResponse>> response) {
                isUpdating = false;
                dismissProgressDialog();
                
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response successful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Update successful");
                    Toast.makeText(EditProfileActivity.this,
                        "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    
                    // Set result to notify previous activity to reload
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = "Không thể cập nhật thông tin";
                    if (response.code() == 400) {
                        errorMsg = "Thông tin không hợp lệ";
                    } else if (response.code() == 403) {
                        errorMsg = "Bạn không có quyền cập nhật";
                    }
                    
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    
                    Toast.makeText(EditProfileActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<UserResponse>> call, Throwable t) {
                isUpdating = false;
                dismissProgressDialog();
                Log.e(TAG, "Update failed: " + t.getMessage(), t);
                Toast.makeText(EditProfileActivity.this,
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSave.setEnabled(false);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnSave.setEnabled(true);
    }

    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        binding = null;
    }
}
