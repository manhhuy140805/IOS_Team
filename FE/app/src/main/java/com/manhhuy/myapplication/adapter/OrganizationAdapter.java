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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organization, parent, false);
        return new OrgViewHolder(view);
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
        private ShapeableImageView ivOrgLogo;
        private TextView tvOrgName;
        private TextView tvOrgEmail;
        private TextView tvOrgStatus;
        private TextView tvFoundedDate;
        private TextView tvMemberCount;
        private LinearLayout llViolationWarning;
        private TextView tvViolationType;
        private TextView btnView;
        private TextView btnLockUnlock;
        private TextView btnDelete;

        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOrgLogo = itemView.findViewById(R.id.ivOrgLogo);
            tvOrgName = itemView.findViewById(R.id.tvOrgName);
            tvOrgEmail = itemView.findViewById(R.id.tvOrgEmail);
            tvOrgStatus = itemView.findViewById(R.id.tvOrgStatus);
            tvFoundedDate = itemView.findViewById(R.id.tvFoundedDate);
            tvMemberCount = itemView.findViewById(R.id.tvMemberCount);
            llViolationWarning = itemView.findViewById(R.id.llViolationWarning);
            tvViolationType = itemView.findViewById(R.id.tvViolationType);
            btnView = itemView.findViewById(R.id.btnView);
            btnLockUnlock = itemView.findViewById(R.id.btnLockUnlock);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Organization organization, OnOrganizationActionListener listener) {
            // Set basic info
            tvOrgName.setText(organization.getName());
            tvOrgEmail.setText(organization.getEmail());
            tvFoundedDate.setText("Thành lập: " + organization.getFoundedDate());
            tvMemberCount.setText("Thành viên: " + organization.getMemberCount());

            // Set status badge
            tvOrgStatus.setText(organization.getStatus());
            switch (organization.getStatus()) {
                case "Hoạt động":
                    tvOrgStatus.setBackgroundResource(R.drawable.bg_status_active);
                    tvOrgStatus.setTextColor(itemView.getContext().getColor(R.color.green_primary));
                    btnLockUnlock.setText("Khóa");
                    break;
                case "Bị khóa":
                    tvOrgStatus.setBackgroundResource(R.drawable.bg_status_locked);
                    tvOrgStatus.setTextColor(itemView.getContext().getColor(R.color.pink));
                    btnLockUnlock.setText("Mở khóa");
                    break;
                case "Chờ xác thực":
                    tvOrgStatus.setBackgroundResource(R.drawable.bg_status_pending);
                    tvOrgStatus.setTextColor(itemView.getContext().getColor(R.color.orange));
                    btnLockUnlock.setText("Khóa");
                    break;
            }

            // Show/hide violation warning
            if (organization.getViolationType() != null && !organization.getViolationType().isEmpty()) {
                llViolationWarning.setVisibility(View.VISIBLE);
                tvViolationType.setText("Vi phạm: " + organization.getViolationType());
            } else {
                llViolationWarning.setVisibility(View.GONE);
            }

            // Set click listeners
            btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewClick(organization);
                }
            });

            btnLockUnlock.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLockUnlockClick(organization);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(organization);
                }
            });
        }
    }
}
