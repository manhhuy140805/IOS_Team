package com.manhhuy.myapplication.ui.Activities.Fragment.Organization;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.AplicationAdapter;
import com.manhhuy.myapplication.databinding.FragmentAcceptApplicantBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.response.EventRegistrationResponse;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.model.Applicant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceptApplicantFragment extends Fragment implements AplicationAdapter.OnApplicantActionListener {

    private static final String TAG = "AcceptApplicantFragment";
    private FragmentAcceptApplicantBinding binding;

    // Data
    private List<Applicant> applicantList;
    private List<EventRegistrationResponse> registrationList;
    private AplicationAdapter adapter;
    private int currentFilter = -1; // -1=all, 0=pending, 1=accepted, 2=rejected
    
    private boolean isLoading = false;
    private List<EventResponse> myEvents;
    
    // Flag to track if data has been loaded
    private boolean isDataLoaded = false;

    public AcceptApplicantFragment() {
        // Required empty public constructor
    }

    public static AcceptApplicantFragment newInstance() {
        return new AcceptApplicantFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentAcceptApplicantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applicantList = new ArrayList<>();
        registrationList = new ArrayList<>();
        
        setupRecyclerView();
        setupListeners();
        setupSwipeRefresh();
        
        // Load data only on first time
        if (!isDataLoaded) {
            loadAllEventRegistrations(null);
            isDataLoaded = true;
        }
    }
    
    /**
     * Setup SwipeRefreshLayout
     */
    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.app_green_primary,
            R.color.app_green_light
        );
        
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Reload data based on current filter
            String status = null;
            if (currentFilter == 0) {
                status = "PENDING";
            } else if (currentFilter == 1) {
                status = "APPROVED";
            } else if (currentFilter == 2) {
                status = "REJECTED";
            }
            
            loadAllEventRegistrations(status);
            
            // Stop refreshing after a delay
            binding.swipeRefreshLayout.postDelayed(() -> {
                if (binding != null) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);
        });
    }
    

    private void loadAllEventRegistrations(String status) {
        if (isLoading) return;
        
        isLoading = true;
        showLoading();
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<RestResponse<PageResponse<EventRegistrationResponse>>> call = apiService.getMyEventsRegistrations(
                0, 100, status
        );
        
        call.enqueue(new Callback<RestResponse<PageResponse<EventRegistrationResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<PageResponse<EventRegistrationResponse>>> call,
                                 Response<RestResponse<PageResponse<EventRegistrationResponse>>> response) {
                isLoading = false;
                hideLoading();
                
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response successful: " + response.isSuccessful());
                Log.d(TAG, "Response body null: " + (response.body() == null));
                
                if (!response.isSuccessful()) {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error response: " + errorBody);
                        Toast.makeText(getContext(), "Lỗi: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    updateEmptyState();
                    return;
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<PageResponse<EventRegistrationResponse>> restResponse = response.body();
                    Log.d(TAG, "RestResponse data null: " + (restResponse.getData() == null));
                    
                    PageResponse<EventRegistrationResponse> pageResponse = restResponse.getData();
                    
                    if (pageResponse != null) {
                        Log.d(TAG, "PageResponse content null: " + (pageResponse.getContent() == null));
                        if (pageResponse.getContent() != null) {
                            Log.d(TAG, "Content size: " + pageResponse.getContent().size());
                        }
                    }
                    
                    if (pageResponse != null && pageResponse.getContent() != null) {
                        registrationList = pageResponse.getContent();
                        
                        applicantList.clear();
                        for (EventRegistrationResponse reg : registrationList) {
                            Applicant applicant = convertToApplicant(reg);
                            applicantList.add(applicant);
                            Log.d(TAG, "Added applicant: " + applicant.getName());
                        }
                        
                        Log.d(TAG, "Total applicants before updateData: " + applicantList.size());
                        adapter.updateData(new ArrayList<>(applicantList));
                        updateCounts();
                        updateEmptyState();
                        
                        Log.d(TAG, "Loaded " + applicantList.size() + " registrations");
                    } else {
                        Log.e(TAG, "PageResponse or content is null");
                        updateEmptyState();
                    }
                } else {
                    Log.e(TAG, "Response not successful or body is null");
                    Toast.makeText(getContext(), "Không thể tải danh sách đăng ký", Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<PageResponse<EventRegistrationResponse>>> call, Throwable t) {
                isLoading = false;
                hideLoading();
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }
    
    private Applicant convertToApplicant(EventRegistrationResponse reg) {
        int status;
        if ("APPROVED".equals(reg.getStatus())) {
            status = 1;
        } else if ("REJECTED".equals(reg.getStatus())) {
            status = 2;
        } else {
            status = 0; // PENDING
        }
        
        return new Applicant(
                reg.getUserName() != null ? reg.getUserName() : "N/A",
                reg.getUserEmail() != null ? reg.getUserEmail() : "",
                reg.getEventTitle() != null ? reg.getEventTitle() : "",
                reg.getJoinDate() != null ? reg.getJoinDate() : "",
                reg.getUserPhone() != null ? reg.getUserPhone() : "",
                reg.getNotes() != null ? reg.getNotes() : "",
                status,
                reg.getUserAvatarUrl() != null ? reg.getUserAvatarUrl() : "",
                String.valueOf(reg.getId())
        );
    }
    
    private void showLoading() {
        if (binding != null) {
            binding.rvApplicants.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.GONE);
        }
    }
    
    private void hideLoading() {
        if (binding != null) {
            binding.rvApplicants.setVisibility(View.VISIBLE);
        }
    }
    
    private void showEmptyState() {
        if (binding != null) {
            binding.rvApplicants.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView() {
        adapter = new AplicationAdapter(requireContext(), applicantList, this);
        binding.rvApplicants.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvApplicants.setAdapter(adapter);

        updateEmptyState();
    }

    private void setupListeners() {
        // Back button - Hide in fragment when used as tab
        binding.btnBack.setVisibility(View.GONE);

        binding.tabAll.setOnClickListener(v -> selectTab(binding.tabAll, -1));
        binding.tabPending.setOnClickListener(v -> selectTab(binding.tabPending, 0));
        binding.tabAccepted.setOnClickListener(v -> selectTab(binding.tabAccepted, 1));
        binding.tabRejected.setOnClickListener(v -> selectTab(binding.tabRejected, 2));
    }

    private void selectTab(TextView selectedTab, int filter) {
        currentFilter = filter;

        // Reset all tabs
        resetTab(binding.tabAll);
        resetTab(binding.tabPending);
        resetTab(binding.tabAccepted);
        resetTab(binding.tabRejected);

        // Highlight selected
        selectedTab.setBackgroundResource(R.drawable.bg_filter_selected);
        selectedTab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        // Reload from API with status filter
        String status = null;
        if (filter == 0) {
            status = "PENDING";
        } else if (filter == 1) {
            status = "APPROVED";
        } else if (filter == 2) {
            status = "REJECTED";
        }
        
        loadAllEventRegistrations(status);
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_filter_unselected);
        tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
    }

    private void updateCounts() {
        binding.tvPendingCount.setText(String.valueOf(adapter.getPendingCount()));
        binding.tvAcceptedCount.setText(String.valueOf(adapter.getAcceptedCount()));
        binding.tvRejectedCount.setText(String.valueOf(adapter.getRejectedCount()));
    }

    private void updateEmptyState() {
        if (binding != null) {
            if (adapter.getItemCount() == 0) {
                binding.rvApplicants.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.rvApplicants.setVisibility(View.VISIBLE);
                binding.emptyState.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAccept(Applicant applicant, int position) {
        updateRegistrationStatus(applicant, position, "APPROVED");
    }

    @Override
    public void onReject(Applicant applicant, int position) {
        updateRegistrationStatus(applicant, position, "REJECTED");
    }
    
    private void updateRegistrationStatus(Applicant applicant, int position, String status) {
        Integer registrationId = Integer.parseInt(applicant.getRegistrationId());
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<RestResponse<EventRegistrationResponse>> call = apiService.updateRegistrationStatus(registrationId, status);
        
        call.enqueue(new Callback<RestResponse<EventRegistrationResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<EventRegistrationResponse>> call,
                                 Response<RestResponse<EventRegistrationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int newStatus = "APPROVED".equals(status) ? 1 : 2;
                    applicant.setStatus(newStatus);
                    adapter.updateApplicantStatus(position, newStatus);
                    updateCounts();
                    
                    String message = "APPROVED".equals(status) ? 
                        "Đã chấp nhận: " + applicant.getName() :
                        "Đã từ chối: " + applicant.getName();
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), 
                        "Không thể cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<EventRegistrationResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewDetails(Applicant applicant) {
        Toast.makeText(requireContext(),
                "Xem chi tiết: " + applicant.getName(),
                Toast.LENGTH_SHORT).show();
        // TODO: Open detail screen
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}