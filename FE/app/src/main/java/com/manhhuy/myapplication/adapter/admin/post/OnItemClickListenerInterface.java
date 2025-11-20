package com.manhhuy.myapplication.adapter.admin.post;

import com.manhhuy.myapplication.model.EventPost;

public interface OnItemClickListenerInterface {
    void onApproveClick(EventPost post, int position);
    void onRejectClick(EventPost post, int position);
    void onStatisticsClick(EventPost post, int position);
    void onEditClick(EventPost post, int position);
    void onReviewClick(EventPost post, int position);
}
