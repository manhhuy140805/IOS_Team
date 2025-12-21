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
import com.manhhuy.myapplication.databinding.ItemApplicantBinding;
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
        ItemApplicantBinding binding = ItemApplicantBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ApplicantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position) {
        Applicant applicant = applicantList.get(position);

        // Set data
        holder.binding.tvName.setText(applicant.getName());
        holder.binding.tvEmail.setText(applicant.getEmail());
        holder.binding.tvActivityName.setText(applicant.getActivityName());
        holder.binding.tvRegDate.setText(applicant.getRegistrationDate());
        holder.binding.tvPhone.setText(applicant.getPhone());

        // Set note
        if (applicant.getNote() != null && !applicant.getNote().isEmpty()) {
            holder.binding.noteSection.setVisibility(View.VISIBLE);
            holder.binding.tvNote.setText(applicant.getNote());
        } else {
            holder.binding.noteSection.setVisibility(View.GONE);
        }

        // Set status and buttons
        switch (applicant.getStatus()) {
            case 0: // Pending
                holder.binding.tvStatus.setText("Chờ duyệt");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orange));
                holder.binding.actionButtons.setVisibility(View.VISIBLE);
                break;
            case 1: // Accepted
                holder.binding.tvStatus.setText("Đã chấp nhận");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_accepted);
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.app_green_primary));
                holder.binding.actionButtons.setVisibility(View.GONE);
                break;
            case 2: // Rejected
                holder.binding.tvStatus.setText("Đã từ chối");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_reject_button);
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
                holder.binding.actionButtons.setVisibility(View.GONE);
                break;
        }

        // Button listeners
        holder.binding.btnAccept.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccept(applicant, position);
            }
        });

        holder.binding.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject(applicant, position);
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

    // Update data
    public void updateData(List<Applicant> newData) {
        applicantList.clear();
        applicantList.addAll(newData);
        applicantListFull.clear();
        applicantListFull.addAll(newData);
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
        ItemApplicantBinding binding;

        public ApplicantViewHolder(@NonNull ItemApplicantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
