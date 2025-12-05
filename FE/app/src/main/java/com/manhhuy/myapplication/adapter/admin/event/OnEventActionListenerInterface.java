package com.manhhuy.myapplication.adapter.admin.event;

import com.manhhuy.myapplication.model.EventPost;

public interface OnEventActionListenerInterface {
    void onViewClick(EventPost event);

    void onEditClick(EventPost event);

    void onDeleteClick(EventPost event);
    
    void onNotificationClick(EventPost event);
}