package com.manhhuy.myapplication.adapter.admin.userOrganations;

import com.manhhuy.myapplication.model.User;

public interface OnUserActionListener {
    void onViewClick(User user);

    void onLockUnlockClick(User user);

    void onDeleteClick(User user);
}
