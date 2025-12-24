package com.manhhuy.myapplication.ui.Activities.Fragment.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.adapter.AdminRedeemRequestAdapter;
import com.manhhuy.myapplication.databinding.FragmentAdminRedeemRequestBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.UserRewardResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRedeemRequestFragment extends Fragment implements AdminRedeemRequestAdapter.OnRequestActionListener {

    private static final String TAG = "AdminRedeemRequest";
    private FragmentAdminRedeemRequestBinding binding;
    private AdminRedeemRequestAdapter adapter;
    private List<UserRewardResponse> requestList = new ArrayList<>();
    private ApiEndpoints apiEndpoints;

    public AdminRedeemRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentAdminRedeemRequestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);

        setupRecyclerView();
        loadPendingRequests();
    }

    private void setupRecyclerView() {
        adapter = new AdminRedeemRequestAdapter(getContext(), requestList, this);
        binding.recyclerViewRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewRequests.setAdapter(adapter);
    }

    private void loadPendingRequests() {
        binding.progressBar.setVisibility(View.VISIBLE);

        apiEndpoints.getPendingRewards(0, 100).enqueue(new Callback<RestResponse<PageResponse<UserRewardResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<PageResponse<UserRewardResponse>>> call,
                    Response<RestResponse<PageResponse<UserRewardResponse>>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<PageResponse<UserRewardResponse>> restResponse = response.body();
                    if (restResponse.getData() != null && restResponse.getData().getContent() != null) {
                        requestList.clear();
                        requestList.addAll(restResponse.getData().getContent());
                        adapter.notifyDataSetChanged();

                        updateEmptyState();
                    }
                } else {
                    Log.e(TAG, "Failed to load requests: " + response.code());
                    Toast.makeText(getContext(), "Không thể tải danh sách yêu cầu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<PageResponse<UserRewardResponse>>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error loading requests", t);
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (requestList.isEmpty()) {
            binding.recyclerViewRequests.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewRequests.setVisibility(View.VISIBLE);
            binding.emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onApprove(UserRewardResponse request) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận duyệt")
                .setMessage("Bạn có chắc chắn muốn duyệt yêu cầu đổi quà này?")
                .setPositiveButton("Duyệt", (dialog, which) -> updateRequestStatus(request.getId(), "ACCEPTED"))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onReject(UserRewardResponse request) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận từ chối")
                .setMessage("Bạn có chắc chắn muốn từ chối yêu cầu này? Điểm sẽ được hoàn lại cho người dùng.")
                .setPositiveButton("Từ chối", (dialog, which) -> updateRequestStatus(request.getId(), "REJECTED"))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateRequestStatus(Integer id, String status) {
        binding.progressBar.setVisibility(View.VISIBLE);

        apiEndpoints.updateUserRewardStatus(id, status).enqueue(new Callback<RestResponse<UserRewardResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<UserRewardResponse>> call,
                    Response<RestResponse<UserRewardResponse>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    String message = "ACCEPTED".equals(status) ? "Đã duyệt yêu cầu" : "Đã từ chối yêu cầu";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    loadPendingRequests(); // Reload list
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<UserRewardResponse>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
