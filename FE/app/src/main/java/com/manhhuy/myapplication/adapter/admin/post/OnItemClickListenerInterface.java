package com.manhhuy.myapplication.adapter.admin.post;

import com.manhhuy.myapplication.helper.response.EventResponse;

public interface OnItemClickListenerInterface {
    void onViewClick(EventResponse event);
    void onApproveClick(EventResponse event);
    void onRejectClick(EventResponse event);
}
