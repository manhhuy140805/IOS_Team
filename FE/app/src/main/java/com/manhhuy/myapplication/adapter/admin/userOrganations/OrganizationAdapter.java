package com.manhhuy.myapplication.adapter.admin.userOrganations;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemOrganizationBinding;
import com.manhhuy.myapplication.model.Organization;

import java.util.List;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.OrgViewHolder> {

    private final List<Organization> organizationList;
    private final OnOrganizationActionListener listener;

    public OrganizationAdapter(List<Organization> organizationList, OnOrganizationActionListener listener) {
        this.organizationList = organizationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrganizationBinding binding = ItemOrganizationBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new OrgViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgViewHolder holder, int position) {
        Organization organization = organizationList.get(position);
        holder.bind(organization, listener);
    }

    @Override
    public int getItemCount() {
        return organizationList != null ? organizationList.size() : 0;
    }

    public void updateList(List<Organization> newList) {
//        this.organizationList = newList;
        notifyDataSetChanged();
    }

    static class OrgViewHolder extends RecyclerView.ViewHolder {
        private ItemOrganizationBinding binding;

        public OrgViewHolder(@NonNull ItemOrganizationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Organization organization, OnOrganizationActionListener listener) {
            // Set basic info
            binding.tvOrgName.setText(organization.getName());
            binding.tvOrgEmail.setText(organization.getEmail());
            binding.tvFoundedDate.setText(organization.getFoundedDate());
            binding.tvMemberCount.setText(String.valueOf(organization.getMemberCount()));

            // Set status badge
            binding.tvOrgStatus.setText(organization.getStatus());
            switch (organization.getStatus()) {
                case "Hoạt động":
                    binding.tvOrgStatus.setBackgroundResource(R.drawable.bg_status_active_event);
                    binding.tvOrgStatus.setTextColor(itemView.getContext().getColor(R.color.app_green_primary));
                    binding.btnLockUnlock.setImageResource(R.drawable.ic_lock);
                    binding.btnLockUnlock.setColorFilter(Color.parseColor("#FF9800"));
                    break;
                case "Bị khóa":
                    binding.tvOrgStatus.setBackgroundResource(R.drawable.bg_status_rejected_light);
                    binding.tvOrgStatus.setTextColor(Color.parseColor("#C62828"));
                    binding.btnLockUnlock.setImageResource(R.drawable.ic_lock_open);
                    binding.btnLockUnlock.setColorFilter(Color.parseColor("#4CAF50"));
                    break;
                case "Chờ xác thực":
                    binding.tvOrgStatus.setBackgroundResource(R.drawable.bg_status_pending_light);
                    binding.tvOrgStatus.setTextColor(Color.parseColor("#EF6C00"));
                    binding.btnLockUnlock.setImageResource(R.drawable.ic_lock);
                    binding.btnLockUnlock.setColorFilter(Color.parseColor("#FF9800"));
                    break;
            }

            // Show/hide violation warning
            if (organization.getViolationType() != null && !organization.getViolationType().isEmpty()) {
                binding.llViolationWarning.setVisibility(View.VISIBLE);
                binding.tvViolationType.setText("Vi phạm: " + organization.getViolationType());
            } else {
                binding.llViolationWarning.setVisibility(View.GONE);
            }

            // Set click listeners
            binding.btnView.setOnClickListener(v -> listener.onViewClick(organization));
            binding.btnLockUnlock.setOnClickListener(v -> listener.onLockUnlockClick(organization));
            binding.btnDelete.setOnClickListener(v -> listener.onDeleteClick(organization));
        }
    }
}
