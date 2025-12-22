package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.MyRewardAdapter;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiService;
import com.manhhuy.myapplication.helper.response.MyRewardResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRewardActivity extends AppCompatActivity {

    private static final String TAG = "MyRewardActivity";

    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private LinearLayout errorState;
    private TextView tvErrorMessage;
    private Button btnRetry;
    private RecyclerView recyclerViewRewards;

    private MyRewardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_reward);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadMyRewards();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        errorState = findViewById(R.id.errorState);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnRetry = findViewById(R.id.btnRetry);
        recyclerViewRewards = findViewById(R.id.recyclerViewRewards);

        btnRetry.setOnClickListener(v -> loadMyRewards());
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new MyRewardAdapter(this);
        recyclerViewRewards.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRewards.setAdapter(adapter);
    }

    private void loadMyRewards() {
        // Check if logged in
        String token = ApiConfig.getToken();
        if (token == null || token.isEmpty()) {
            showError("Vui lòng đăng nhập để xem phần thưởng");
            return;
        }

        showLoading();

        ApiService.api().getMyRewards(0, 100).enqueue(new Callback<RestResponse<PageResponse<MyRewardResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<PageResponse<MyRewardResponse>>> call,
                    Response<RestResponse<PageResponse<MyRewardResponse>>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<PageResponse<MyRewardResponse>> restResponse = response.body();

                    if (restResponse.getData() != null && restResponse.getData().getContent() != null) {
                        List<MyRewardResponse> rewards = restResponse.getData().getContent();
                        Log.d(TAG, "Loaded " + rewards.size() + " rewards");

                        if (rewards.isEmpty()) {
                            showEmpty();
                        } else {
                            adapter.setRewards(rewards);
                            showContent();
                        }
                    } else {
                        showEmpty();
                    }
                } else {
                    Log.e(TAG, "Failed to load rewards: " + response.code());
                    showError("Không thể tải phần thưởng");
                }
            }

            @Override
            public void onFailure(Call<RestResponse<PageResponse<MyRewardResponse>>> call, Throwable t) {
                Log.e(TAG, "Error loading rewards", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewRewards.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        recyclerViewRewards.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
    }

    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        recyclerViewRewards.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
        errorState.setVisibility(View.GONE);
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerViewRewards.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
    }
}