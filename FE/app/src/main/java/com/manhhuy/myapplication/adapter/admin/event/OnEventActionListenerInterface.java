package com.manhhuy.myapplication.adapter.admin.event;

import com.manhhuy.myapplication.helper.response.EventResponse;

public interface OnEventActionListenerInterface {
    void onViewClick(EventResponse event);

    void onEditClick(EventResponse event);

    void onDeleteClick(EventResponse event);
    
    void onNotificationClick(EventResponse event);
}