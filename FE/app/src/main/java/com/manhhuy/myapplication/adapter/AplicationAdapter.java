package com.manhhuy.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.model.Applicant;

import java.util.ArrayList;
import java.util.List;

public class AplicationAdapter extends RecyclerView.Adapter<AplicationAdapter.ApplicantViewHolder> {

    private Context context;
    private List<Applicant> applicantList;
    private List<Applicant> applicantListFull;
    private OnApplicantActionListener listener;

    public interface OnApplicantActionListener {
        void onAccept(Applicant applicant, int position);

        void onReject(Applicant applicant, int position);

        void onViewDetails(Applicant applicant);
    }

    public AplicationAdapter(Context context, List<Applicant> applicantList, OnApplicantActionListener listener) {
        this.context = context;
        this.applicantList = applicantList;
        this.applicantListFull = new ArrayList<>(applicantList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position) {
        Applicant applicant = applicantList.get(position);

        // Set data
        holder.tvName.setText(applicant.getName());
        holder.tvEmail.setText(applicant.getEmail());
        holder.tvActivityName.setText(applicant.getActivityName());
        holder.tvRegDate.setText(applicant.getRegistrationDate());
        holder.tvPhone.setText(applicant.getPhone());

        // Set note
        if (applicant.getNote() != null && !applicant.getNote().isEmpty()) {
            holder.noteSection.setVisibility(View.VISIBLE);
            holder.tvNote.setText(applicant.getNote());
        } else {
            holder.noteSection.setVisibility(View.GONE);
        }

        // Set status and buttons
        switch (applicant.getStatus()) {
            case 0: // Pending
                holder.tvStatus.setText("Chờ duyệt");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orange));
                holder.actionButtons.setVisibility(View.VISIBLE);
                holder.btnViewDetails.setVisibility(View.GONE);
                break;
            case 1: // Accepted
                holder.tvStatus.setText("Đã chấp nhận");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_accepted);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_primary));
                holder.actionButtons.setVisibility(View.GONE);
                holder.btnViewDetails.setVisibility(View.VISIBLE);
                break;
            case 2: // Rejected
                holder.tvStatus.setText("Đã từ chối");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
                holder.actionButtons.setVisibility(View.GONE);
                holder.btnViewDetails.setVisibility(View.VISIBLE);
                break;
        }

        // Button listeners
        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccept(applicant, position);
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject(applicant, position);
            }
        });

        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetails(applicant);
            }
        });

        // Item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetails(applicant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }

    // Filter by status
    public void filterByStatus(int status) {
        applicantList.clear();
        if (status == -1) {
            // Show all
            applicantList.addAll(applicantListFull);
        } else {
            for (Applicant applicant : applicantListFull) {
                if (applicant.getStatus() == status) {
                    applicantList.add(applicant);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Update applicant status
    public void updateApplicantStatus(int position, int newStatus) {
        if (position >= 0 && position < applicantList.size()) {
            applicantList.get(position).setStatus(newStatus);
            notifyItemChanged(position);
        }
    }

    // Get counts
    public int getPendingCount() {
        int count = 0;
        for (Applicant applicant : applicantListFull) {
            if (applicant.getStatus() == 0)
                count++;
        }
        return count;
    }

    public int getAcceptedCount() {
        int count = 0;
        for (Applicant applicant : applicantListFull) {
            if (applicant.getStatus() == 1)
                count++;
        }
        return count;
    }

    public int getRejectedCount() {
        int count = 0;
        for (Applicant applicant : applicantListFull) {
            if (applicant.getStatus() == 2)
                count++;
        }
        return count;
    }

    static class ApplicantViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvEmail, tvStatus;
        TextView tvActivityName, tvRegDate, tvPhone;
        LinearLayout noteSection, actionButtons;
        TextView tvNote;
        Button btnAccept, btnReject, btnViewDetails;

        public ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvActivityName = itemView.findViewById(R.id.tvActivityName);
            tvRegDate = itemView.findViewById(R.id.tvRegDate);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            noteSection = itemView.findViewById(R.id.noteSection);
            tvNote = itemView.findViewById(R.id.tvNote);
            actionButtons = itemView.findViewById(R.id.actionButtons);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
