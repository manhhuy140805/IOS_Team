package com.manhhuy.myapplication.ui.Activities.Fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.AplicationAdapter;
import com.manhhuy.myapplication.model.Applicant;

import java.util.ArrayList;
import java.util.List;

public class AcceptApplicantFragment extends Fragment implements AplicationAdapter.OnApplicantActionListener {

    // UI Components
    private ImageView btnBack;
    private TextView tvPendingCount, tvAcceptedCount, tvRejectedCount;
    private TextView tabAll, tabPending, tabAccepted, tabRejected;
    private RecyclerView rvApplicants;
    private LinearLayout emptyState;

    // Data
    private List<Applicant> applicantList;
    private AplicationAdapter adapter;
    private int currentFilter = -1; // -1=all, 0=pending, 1=accepted, 2=rejected

    public AcceptApplicantFragment() {
        // Required empty public constructor
    }

    public static AcceptApplicantFragment newInstance() {
        return new AcceptApplicantFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accept_applicant, container, false);

        initViews(view);
        loadMockData();
        setupRecyclerView();
        setupListeners();
        updateCounts();

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvAcceptedCount = view.findViewById(R.id.tvAcceptedCount);
        tvRejectedCount = view.findViewById(R.id.tvRejectedCount);
        tabAll = view.findViewById(R.id.tabAll);
        tabPending = view.findViewById(R.id.tabPending);
        tabAccepted = view.findViewById(R.id.tabAccepted);
        tabRejected = view.findViewById(R.id.tabRejected);
        rvApplicants = view.findViewById(R.id.rvApplicants);
        emptyState = view.findViewById(R.id.emptyState);
    }

    private void loadMockData() {
        applicantList = new ArrayList<>();

        // Mock data - 10 applicants
        applicantList.add(new Applicant(
                "Võ Nguyễn Đại Hiếu",
                "abc.nguyen@email.com",
                "Beach Cleanup",
                "24/10/2025",
                "0901234567",
                "Tôi rất quan tâm đến môi trường và muốn đóng góp cho cộng đồng. Tôi có kinh nghiệm tham gia các hoạt động tương tự.",
                0, // pending
                ""));

        applicantList.add(new Applicant(
                "Trần Thị Bình",
                "binh.tran@gmail.com",
                "Teach English to Kids",
                "23/10/2025",
                "0912345678",
                "Tôi là sinh viên năm 3 chuyên ngành tiếng Anh. Tôi yêu trẻ em và muốn giúp các em có cơ hội học tập tốt hơn.",
                0, // pending
                ""));

        applicantList.add(new Applicant(
                "Lê Minh Cường",
                "cuong.le@email.com",
                "Tree Planting Drive",
                "22/10/2025",
                "0923456789",
                "",
                1, // accepted
                ""));

        applicantList.add(new Applicant(
                "Phạm Thị Dung",
                "dung.pham@email.com",
                "Food Bank Volunteer",
                "24/10/2025",
                "0923456789",
                "Tôi muốn giúp đỡ những người gặp khó khăn. Tôi có thể góp nhận vào cuối tuần.",
                0, // pending
                ""));

        applicantList.add(new Applicant(
                "Hoàng Văn Em",
                "em.hoang@email.com",
                "Animal Shelter Helper",
                "20/10/2025",
                "0934567890",
                "",
                2, // rejected
                ""));

        applicantList.add(new Applicant(
                "Nguyễn Văn An",
                "an.nguyen@email.com",
                "Community Garden",
                "19/10/2025",
                "0945678901",
                "Tôi thích trồng cây và làm vườn. Rất mong được đóng góp.",
                1, // accepted
                ""));

        applicantList.add(new Applicant(
                "Đỗ Thị Hoa",
                "hoa.do@email.com",
                "Elder Care Program",
                "18/10/2025",
                "0956789012",
                "Tôi có kinh nghiệm chăm sóc người cao tuổi và muốn mang lại niềm vui cho các cụ.",
                1, // accepted
                ""));

        applicantList.add(new Applicant(
                "Vũ Minh Khoa",
                "khoa.vu@email.com",
                "Youth Mentorship",
                "17/10/2025",
                "0967890123",
                "",
                0, // pending
                ""));

        applicantList.add(new Applicant(
                "Lý Thị Mai",
                "mai.ly@email.com",
                "Charity Run",
                "16/10/2025",
                "0978901234",
                "Tôi yêu thích chạy bộ và muốn kết hợp sở thích với hoạt động thiện nguyện.",
                1, // accepted
                ""));

        applicantList.add(new Applicant(
                "Bùi Văn Nam",
                "nam.bui@email.com",
                "Hospital Support",
                "15/10/2025",
                "0989012345",
                "",
                2, // rejected
                ""));
    }

    private void setupRecyclerView() {
        adapter = new AplicationAdapter(requireContext(), applicantList, this);
        rvApplicants.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvApplicants.setAdapter(adapter);

        updateEmptyState();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        tabAll.setOnClickListener(v -> selectTab(tabAll, -1));
        tabPending.setOnClickListener(v -> selectTab(tabPending, 0));
        tabAccepted.setOnClickListener(v -> selectTab(tabAccepted, 1));
        tabRejected.setOnClickListener(v -> selectTab(tabRejected, 2));
    }

    private void selectTab(TextView selectedTab, int filter) {
        currentFilter = filter;

        // Reset all tabs
        resetTab(tabAll);
        resetTab(tabPending);
        resetTab(tabAccepted);
        resetTab(tabRejected);

        // Highlight selected
        selectedTab.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
        selectedTab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        // Filter
        adapter.filterByStatus(filter);
        updateEmptyState();
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_tab_filter);
        tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.cyan));
    }

    private void updateCounts() {
        tvPendingCount.setText(String.valueOf(adapter.getPendingCount()));
        tvAcceptedCount.setText(String.valueOf(adapter.getAcceptedCount()));
        tvRejectedCount.setText(String.valueOf(adapter.getRejectedCount()));
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            rvApplicants.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvApplicants.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAccept(Applicant applicant, int position) {
        applicant.setStatus(1); // accepted
        adapter.updateApplicantStatus(position, 1);
        updateCounts();
        Toast.makeText(requireContext(),
                "Đã chấp nhận: " + applicant.getName(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReject(Applicant applicant, int position) {
        applicant.setStatus(2); // rejected
        adapter.updateApplicantStatus(position, 2);
        updateCounts();
        Toast.makeText(requireContext(),
                "Đã từ chối: " + applicant.getName(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewDetails(Applicant applicant) {
        Toast.makeText(requireContext(),
                "Xem chi tiết: " + applicant.getName(),
                Toast.LENGTH_SHORT).show();
        // TODO: Open detail screen
    }
}