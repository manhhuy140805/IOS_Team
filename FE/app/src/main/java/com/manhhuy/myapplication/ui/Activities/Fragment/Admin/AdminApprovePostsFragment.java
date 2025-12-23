package com.manhhuy.myapplication.ui.Activities.Fragment.Admin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.admin.post.EventPostAdapter;
import com.manhhuy.myapplication.adapter.admin.post.OnItemClickListenerInterface;
import com.manhhuy.myapplication.databinding.FragmentAdminApprovePostsBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.request.SendNotificationRequest;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Admin approve events: Quản lý duyệt bài đăng sự kiện
 */
public class AdminApprovePostsFragment extends Fragment implements OnItemClickListenerInterface {

    private FragmentAdminApprovePostsBinding binding;
    private EventPostAdapter adapter;
    private final List<EventResponse> eventList = new ArrayList<>();
    
    private ApiEndpoints apiEndpoints;
    private String currentFilter = "PENDING";
    
    // Pagination
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private static final int PAGE_SIZE = 20;

    public AdminApprovePostsFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentAdminApprovePostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);
        
        setupRecyclerView();
        setupListeners();
        loadEvents();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new EventPostAdapter(getContext(), eventList, this);
        binding.recyclerViewPosts.setLayoutManager(layoutManager);
        binding.recyclerViewPosts.setAdapter(adapter);
        
        // Pagination scroll listener
        binding.recyclerViewPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                if (!isLoading && hasMorePages && dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                        loadMoreEvents();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        binding.btnBack.setVisibility(View.GONE);
        binding.btnMenu.setVisibility(View.GONE);
        binding.btnLoadMore.setVisibility(View.GONE);

        // Ẩn tab "Tất cả" và "Đã duyệt"
        binding.tabAll.setVisibility(View.GONE);
        binding.tabApproved.setVisibility(View.GONE);

        binding.tabPending.setOnClickListener(v -> filterEvents("PENDING"));
        binding.tabRejected.setOnClickListener(v -> filterEvents("REJECTED"));
    }

    private void loadEvents() {
        currentPage = 0;
        hasMorePages = true;
        eventList.clear();
        loadEventsPage(currentPage);
    }
    
    private void loadMoreEvents() {
        if (!isLoading && hasMorePages) {
            currentPage++;
            loadEventsPage(currentPage);
        }
    }
    
    private void loadEventsPage(int page) {
        isLoading = true;
        
        // Chỉ load events với status PENDING hoặc REJECTED
        apiEndpoints.getAllEvents(page, PAGE_SIZE, "createdAt", "DESC",
            null, null, currentFilter, null, null, null, null, null)
            .enqueue(new Callback<RestResponse<PageResponse<EventResponse>>>() {
                @Override
                public void onResponse(Call<RestResponse<PageResponse<EventResponse>>> call,
                                     Response<RestResponse<PageResponse<EventResponse>>> response) {
                    isLoading = false;
                    
                    if (isResponseValid(response)) {
                        PageResponse<EventResponse> pageData = response.body().getData();
                        
                        if (page == 0) {
                            eventList.clear();
                        }
                        eventList.addAll(pageData.getContent());
                        
                        hasMorePages = !pageData.isLast();
                        adapter.notifyDataSetChanged();
                    } else {
                        showToast("Đã xảy ra lỗi khi tải sự kiện");
                    }
                }

                @Override
                public void onFailure(Call<RestResponse<PageResponse<EventResponse>>> call, Throwable t) {
                    isLoading = false;
                    showToast("Lỗi kết nối");
                }
            });
    }

    private void filterEvents(String filter) {
        currentFilter = filter;
        updateTabUI();
        loadEvents(); // Reload từ API với status mới
    }

    private void updateTabUI() {
        // Style mặc định: tab không được chọn
        styleTabUnselected(binding.tabPending);
        styleTabUnselected(binding.tabRejected);

        // Gắn style cho tab đang được chọn
        switch (currentFilter) {
            case "PENDING":
                styleTabSelected(binding.tabPending);
                break;
            case "REJECTED":
                styleTabSelected(binding.tabRejected);
                break;
        }
    }

    private void styleTabSelected(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_button_gray_solid);
        tab.setTextColor(getResources().getColor(R.color.app_green_primary));
        tab.setAlpha(1f);
        tab.setTypeface(tab.getTypeface(), Typeface.BOLD);
    }

    private void styleTabUnselected(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_category_tab_unselected_reward);
        tab.setTextColor(getResources().getColor(R.color.app_green_primary));
        tab.setAlpha(0.8f);
        tab.setTypeface(tab.getTypeface(), Typeface.NORMAL);
    }

    @Override
    public void onViewClick(EventResponse event) {
        Intent intent = new Intent(getContext(), DetailEventActivity.class);
        intent.putExtra("eventData", event);
        startActivity(intent);
    }

    @Override
    public void onApproveClick(EventResponse event) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Duyệt sự kiện")
            .setMessage("Bạn có chắc muốn duyệt sự kiện \"" + event.getTitle() + "\"?")
            .setPositiveButton("Duyệt", (dialog, which) -> {
                apiEndpoints.updateEventStatus(event.getId(), "ACTIVE")
                    .enqueue(new Callback<RestResponse<EventResponse>>() {
                        @Override
                        public void onResponse(Call<RestResponse<EventResponse>> call,
                                             Response<RestResponse<EventResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                // Xóa event khỏi danh sách vì đã duyệt (không còn PENDING)
                                eventList.remove(event);
                                adapter.notifyDataSetChanged();
                                showToast("Đã duyệt sự kiện");
                                sendNotificationToCreator(event, true);
                            } else {
                                showToast("Không thể duyệt sự kiện");
                            }
                        }

                        @Override
                        public void onFailure(Call<RestResponse<EventResponse>> call, Throwable t) {
                            showToast("Lỗi kết nối");
                        }
                    });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    @Override
    public void onRejectClick(EventResponse event) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Từ chối sự kiện")
            .setMessage("Bạn có chắc muốn từ chối sự kiện \"" + event.getTitle() + "\"?")
            .setPositiveButton("Từ chối", (dialog, which) -> {
                apiEndpoints.updateEventStatus(event.getId(), "REJECTED")
                    .enqueue(new Callback<RestResponse<EventResponse>>() {
                        @Override
                        public void onResponse(Call<RestResponse<EventResponse>> call,
                                             Response<RestResponse<EventResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if ("PENDING".equals(currentFilter)) {
                                    // Nếu đang ở tab Chờ duyệt, xóa khỏi list
                                    eventList.remove(event);
                                } else {
                                    // Nếu đang ở tab Đã từ chối, update status
                                    event.setStatus("REJECTED");
                                }
                                adapter.notifyDataSetChanged();
                                showToast("Đã từ chối sự kiện");
                                sendNotificationToCreator(event, false);
                            } else {
                                showToast("Không thể từ chối sự kiện");
                            }
                        }

                        @Override
                        public void onFailure(Call<RestResponse<EventResponse>> call, Throwable t) {
                            showToast("Lỗi kết nối");
                        }
                    });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void sendNotificationToCreator(EventResponse event, boolean isApproved) {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setEventId(event.getId());
        request.setRecipientType("CREATOR");
        
        if (isApproved) {
            request.setTitle("Sự kiện đã được duyệt");
            request.setContent("Sự kiện \"" + event.getTitle() + "\" của bạn đã được duyệt và đang hoạt động.");
        } else {
            request.setTitle("Sự kiện bị từ chối");
            request.setContent("Sự kiện \"" + event.getTitle() + "\" của bạn đã bị từ chối. Vui lòng kiểm tra lại thông tin.");
        }

        apiEndpoints.sendNotification(request).enqueue(new Callback<RestResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<RestResponse<Map<String, Object>>> call, Response<RestResponse<Map<String, Object>>> response) {
                // Notification sent (or failed silently)
            }

            @Override
            public void onFailure(Call<RestResponse<Map<String, Object>>> call, Throwable t) {
                // Ignore failure
            }
        });
    }
    
    private <T> boolean isResponseValid(Response<RestResponse<T>> response) {
        return response.isSuccessful() &&
               response.body() != null &&
               response.body().getData() != null;
    }
    
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
