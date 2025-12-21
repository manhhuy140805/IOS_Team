package com.manhhuy.myapplication.ui.Activities.Admin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityAddRewardBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.request.RewardRequest;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.RewardResponse;

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

public class AddRewardActivity extends AppCompatActivity {

    public static final String EXTRA_REWARD_ID = "reward_id";
    public static final String EXTRA_REWARD_DATA = "reward_data";
    
    private ActivityAddRewardBinding binding;
    private ApiEndpoints apiEndpoints;
    
    private Uri selectedImageUri;
    private String uploadedImageUrl;
    private Calendar selectedDate;
    
    private boolean isEditMode = false;
    private Integer rewardId;
    private RewardResponse editingReward;
    
    private final String[] rewardTypes = {"VOUCHER", "GIFT", "EXPERIENCE"};
    
    private final ActivityResultLauncher<Intent> imagePickerLauncher = 
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    displaySelectedImage();
                    uploadImage();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRewardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);
        
        checkEditMode();
        setupViews();
        setupListeners();
    }
    
    private void checkEditMode() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_REWARD_ID)) {
            isEditMode = true;
            rewardId = intent.getIntExtra(EXTRA_REWARD_ID, 0);
            
            if (rewardId <= 0) {
                showToast("ID không hợp lệ");
                finish();
                return;
            }
            
            String rewardJson = intent.getStringExtra(EXTRA_REWARD_DATA);
            if (rewardJson != null) {
                try {
                    editingReward = new Gson().fromJson(rewardJson, RewardResponse.class);
                } catch (Exception e) {
                    showToast("Dữ liệu không hợp lệ");
                    finish();
                }
            }
        }
    }

    private void setupViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, rewardTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRewardType.setAdapter(adapter);
        
        if (isEditMode && editingReward != null) {
            binding.tvTitle.setText("Chỉnh sửa quà tặng");
            binding.btnCreate.setText("Cập nhật");
            prefillData();
        }
    }
    
    private void prefillData() {
        binding.etRewardName.setText(editingReward.getName());
        binding.etDescription.setText(editingReward.getDescription());
        binding.etPointsRequired.setText(String.valueOf(editingReward.getPointsRequired()));
        binding.etQuantity.setText(String.valueOf(editingReward.getQuantity()));
        
        // Set type spinner
        for (int i = 0; i < rewardTypes.length; i++) {
            if (rewardTypes[i].equals(editingReward.getType())) {
                binding.spinnerRewardType.setSelection(i);
                break;
            }
        }
        
        // Set status
        if ("ACTIVE".equals(editingReward.getStatus())) {
            binding.rbActive.setChecked(true);
        } else {
            binding.rbInactive.setChecked(true);
        }
        
        // Set expiry date
        if (editingReward.getExpiryDate() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = Calendar.getInstance();
                selectedDate.setTime(sdf.parse(editingReward.getExpiryDate()));
                updateDateDisplay();
            } catch (Exception e) {
                // Ignore
            }
        }
        
        // Set image
        uploadedImageUrl = editingReward.getImageUrl();
        if (uploadedImageUrl != null && !uploadedImageUrl.isEmpty() && !isFinishing()) {
            binding.ivRewardImage.setVisibility(View.VISIBLE);
            binding.layoutImagePlaceholder.setVisibility(View.GONE);
            Glide.with(this)
                    .load(uploadedImageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_gift)
                    .error(R.drawable.ic_gift)
                    .into(binding.ivRewardImage);
        }
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnSelectImage.setOnClickListener(v -> openImagePicker());
        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());
        binding.btnCreate.setOnClickListener(v -> validateAndCreateReward());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void displaySelectedImage() {
        if (isFinishing()) return;
        
        binding.ivRewardImage.setVisibility(View.VISIBLE);
        binding.layoutImagePlaceholder.setVisibility(View.GONE);
        
        Glide.with(this)
                .load(selectedImageUri)
                .centerCrop()
                .into(binding.ivRewardImage);
    }

    private void uploadImage() {
        if (selectedImageUri == null || binding == null) return;
        
        setUploadProgress(true);

        try {
            String mimeType = getContentResolver().getType(selectedImageUri);
            if (mimeType == null || !mimeType.startsWith("image/")) {
                mimeType = "image/jpeg";
            }
            
            File file = createFileFromUri(selectedImageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            apiEndpoints.uploadImage(body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    file.delete();
                    if (binding == null) return;
                    setUploadProgress(false);

                    if (response.isSuccessful() && response.body() != null) {
                        Object dataObj = response.body().get("data");
                        if (dataObj instanceof Map) {
                            Map<String, Object> data = (Map<String, Object>) dataObj;
                            uploadedImageUrl = (String) data.get("imageUrl");
                            showToast("Upload ảnh thành công");
                        } else {
                            showToast("Upload thất bại");
                        }
                    } else {
                        showToast("Upload thất bại");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    file.delete();
                    if (binding == null) return;
                    setUploadProgress(false);
                    showToast("Lỗi kết nối");
                }
            });
        } catch (Exception e) {
            setUploadProgress(false);
            showToast("Lỗi đọc file");
        }
    }
    
    private void setUploadProgress(boolean show) {
        if (binding == null) return;
        binding.progressUpload.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnSelectImage.setEnabled(!show);
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

    private void showDatePicker() {
        Calendar calendar = selectedDate != null ? selectedDate : Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateDisplay();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        if (selectedDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.tvSelectedDate.setText(sdf.format(selectedDate.getTime()));
            binding.tvSelectedDate.setTextColor(getColor(R.color.text_primary));
        }
    }

    private void validateAndCreateReward() {
        String name = binding.etRewardName.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String pointsStr = binding.etPointsRequired.getText().toString().trim();
        String quantityStr = binding.etQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.etRewardName.setError("Vui lòng nhập tên");
            binding.etRewardName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pointsStr)) {
            binding.etPointsRequired.setError("Vui lòng nhập điểm");
            binding.etPointsRequired.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityStr)) {
            binding.etQuantity.setError("Vui lòng nhập số lượng");
            binding.etQuantity.requestFocus();
            return;
        }

        try {
            int points = Integer.parseInt(pointsStr);
            int quantity = Integer.parseInt(quantityStr);
            
            if (points < 1) {
                binding.etPointsRequired.setError("Điểm phải lớn hơn 0");
                return;
            }
            
            if (quantity < 0) {
                binding.etQuantity.setError("Số lượng không hợp lệ");
                return;
            }
            
            RewardRequest request = buildRequest(name, description, points, quantity);
            if (isEditMode) {
                updateReward(request);
            } else {
                createReward(request);
            }
        } catch (NumberFormatException e) {
            showToast("Vui lòng nhập số hợp lệ");
        }
    }
    
    private RewardRequest buildRequest(String name, String description, int points, int quantity) {
        RewardRequest request = new RewardRequest();
        request.setName(name);
        request.setType(rewardTypes[binding.spinnerRewardType.getSelectedItemPosition()]);
        request.setDescription(description.isEmpty() ? null : description);
        request.setImageUrl(uploadedImageUrl);
        request.setPointsRequired(points);
        request.setQuantity(quantity);
        request.setStatus(binding.rbActive.isChecked() ? "ACTIVE" : "INACTIVE");
        
        if (selectedDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            request.setExpiryDate(sdf.format(selectedDate.getTime()));
        }
        
        return request;
    }

    private void createReward(RewardRequest request) {
        setCreateButtonLoading(true);

        apiEndpoints.createReward(request).enqueue(new Callback<RestResponse<RewardResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<RewardResponse>> call, 
                                  Response<RestResponse<RewardResponse>> response) {
                if (isFinishing()) return;
                setCreateButtonLoading(false);

                if (response.isSuccessful() && response.body() != null 
                    && response.body().getStatusCode() == 201) {
                    showToast("Tạo thành công!");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Lỗi";
                    showToast("Tạo thất bại: " + message);
                }
            }

            @Override
            public void onFailure(Call<RestResponse<RewardResponse>> call, Throwable t) {
                if (isFinishing()) return;
                setCreateButtonLoading(false);
                showToast("Lỗi kết nối");
            }
        });
    }
    
    private void updateReward(RewardRequest request) {
        setCreateButtonLoading(true);

        apiEndpoints.updateReward(rewardId, request).enqueue(new Callback<RestResponse<RewardResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<RewardResponse>> call,
                                  Response<RestResponse<RewardResponse>> response) {
                if (isFinishing()) return;
                setCreateButtonLoading(false);

                if (response.isSuccessful() && response.body() != null
                    && response.body().getStatusCode() == 200) {
                    showToast("Cập nhật thành công!");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Lỗi";
                    showToast("Cập nhật thất bại: " + message);
                }
            }

            @Override
            public void onFailure(Call<RestResponse<RewardResponse>> call, Throwable t) {
                if (isFinishing()) return;
                setCreateButtonLoading(false);
                showToast("Lỗi kết nối");
            }
        });
    }
    
    private void setCreateButtonLoading(boolean loading) {
        if (binding == null) return;
        binding.btnCreate.setEnabled(!loading);
        if (isEditMode) {
            binding.btnCreate.setText(loading ? "Đang cập nhật..." : "Cập nhật");
        } else {
            binding.btnCreate.setText(loading ? "Đang tạo..." : "Tạo quà tặng");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
