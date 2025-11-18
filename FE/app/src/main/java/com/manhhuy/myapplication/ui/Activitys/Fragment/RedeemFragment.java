package com.manhhuy.myapplication.ui.Activitys.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manhhuy.myapplication.R;

/**
 * GIẢI THÍCH: RedeemFragment - Trang đổi thưởng bằng điểm tích lũy
 * Fragment này hiển thị danh sách các phần thưởng mà user có thể đổi
 * bằng điểm họ kiếm được từ các hoạt động tình nguyện
 */
public class RedeemFragment extends Fragment {

    public RedeemFragment() {
        // Required empty public constructor
    }

    public static RedeemFragment newInstance() {
        return new RedeemFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // GIẢI THÍCH: Inflate layout fragment_redeem.xml
        return inflater.inflate(R.layout.fragment_redeem, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // GIẢI THÍCH: Setup RecyclerView cho danh sách rewards

        // TODO: Hiển thị điểm hiện tại của user
        // TODO: Setup RecyclerView cho danh sách rewards có thể đổi
        // TODO: Handle click event khi user chọn đổi thưởng
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up resources
    }
}