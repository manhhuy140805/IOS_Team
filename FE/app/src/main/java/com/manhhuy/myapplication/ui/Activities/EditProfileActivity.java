package com.manhhuy.myapplication.ui.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private ActivityEditProfileBinding binding;
    private UserResponse currentUser;
    private ProgressDialog progressDialog;
    private boolean isUpdating = false;
    private String newAvatarUrl = null;
    
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
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        uploadImageToCloudinary(imageUri);
                    }
                }
            }
        );
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        binding.btnChangeAvatar.setOnClickListener(v -> {
            openImagePicker();
        });

        binding.btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                updateProfile();
            }
        });
    }
    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    
    private void uploadImageToCloudinary(Uri imageUri) {
        showProgressDialog("Đang tải ảnh lên...");
        
        try {
            // Convert URI to File
            File file = getFileFromUri(imageUri);
            if (file == null) {
                dismissProgressDialog();
                Toast.makeText(this, "Không thể đọc file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create multipart body
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            
            // Upload to Cloudinary
            ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
            Call<Map<String, Object>> call = apiService.uploadImage(body);
            
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, 
                                     Response<Map<String, Object>> response) {
                    dismissProgressDialog();
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> result = response.body();
                        newAvatarUrl = (String) result.get("url");
                        
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
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, 
                            "Không thể tải ảnh lên", Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    dismissProgressDialog();
                    Log.e(TAG, "Upload error: " + t.getMessage(), t);
                    Toast.makeText(EditProfileActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            dismissProgressDialog();
            Log.e(TAG, "Error preparing file: " + e.getMessage(), e);
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

        isUpdating = true;
        showProgressDialog("Đang cập nhật...");

        // Use new avatar URL if uploaded, otherwise keep current
        String avatarUrl = newAvatarUrl != null ? newAvatarUrl : currentUser.getAvatarUrl();
        
        UpdateUserRequest request = new UpdateUserRequest(fullName, phone, address, avatarUrl);
        
        Log.d(TAG, "Updating current user");
        Log.d(TAG, "Full name: " + fullName);
        Log.d(TAG, "Phone: " + phone);
        Log.d(TAG, "Address: " + address);
        Log.d(TAG, "Avatar URL: " + avatarUrl);

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
