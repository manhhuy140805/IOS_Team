package com.manhhuy.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemOrganizationBinding;
import com.manhhuy.myapplication.model.Organization;

import java.util.List;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.OrgViewHolder> {

    private List<Organization> organizationList;
    private OnOrganizationActionListener listener;

    public interface OnOrganizationActionListener {
        void onViewClick(Organization organization);
        void onLockUnlockClick(Organization organization);
        void onDeleteClick(Organization organization);
    }

    public OrganizationAdapter(List<Organization> organizationList, OnOrganizationActionListener listener) {
        this.organizationList = organizationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrganizationBinding binding = ItemOrganizationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        this.organizationList = newList;
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
            binding.tvFoundedDate.setText("Thành lập: " + organization.getFoundedDate());
            binding.tvMemberCount.setText("Thành viên: " + organization.getMemberCount());

            // Set status badge
            binding.tvOrgStatus.setText(organization.getStatus());
            switch (organization.getStatus()) {
                case "Hoạt động":
                    binding.tvOrgStatus.setBackgroundResource(R.drawable.bg_status_active);
                    binding.tvOrgStatus.setTextColor(itemView.getContext().getColor(R.color.app_green_primary));
                    binding.btnLockUnlock.setText("Khóa");
                    break;
                case "Bị khóa":
                    binding.tvOrgStatus.setBackgroundResource(R.drawable.bg_button_lock);
                    binding.tvOrgStatus.setTextColor(itemView.getContext().getColor(R.color.pink));
                    binding.btnLockUnlock.setText("Mở khóa");
                    break;
                case "Chờ xác thực":
                    binding.tvOrgStatus.setBackgroundResource(R.drawable.bg_status_pending);
                    binding.tvOrgStatus.setTextColor(itemView.getContext().getColor(R.color.orange));
                    binding.btnLockUnlock.setText("Khóa");
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
            binding.btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewClick(organization);
                }
            });

            binding.btnLockUnlock.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLockUnlockClick(organization);
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(organization);
                }
            });
        }
    }
}
