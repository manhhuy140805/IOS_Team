package com.manhhuy.myapplication.ui.Activities.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manhhuy.myapplication.R;

/**
 * GIẢI THÍCH: HomeFragment - Trang chủ hiển thị danh sách sự kiện tình nguyện
 * Hiện tại đang để placeholder, bạn có thể thêm RecyclerView và Adapter sau
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  onCreateView - Method này tạo View cho Fragment

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//  chạy SAU KHI view đã được tạo
        // Đây là nơi để setup RecyclerView, load data, set listeners, etc.

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}