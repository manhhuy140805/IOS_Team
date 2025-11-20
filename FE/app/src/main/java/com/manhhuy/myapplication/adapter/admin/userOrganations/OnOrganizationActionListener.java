package com.manhhuy.myapplication.adapter.admin.userOrganations;

import com.manhhuy.myapplication.model.Organization;

public interface OnOrganizationActionListener {
    void onViewClick(Organization organization);
    void onLockUnlockClick(Organization organization);
    void onDeleteClick(Organization organization);
}
