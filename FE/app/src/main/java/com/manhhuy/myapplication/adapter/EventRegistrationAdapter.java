package com.manhhuy.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemEventRegistrationBinding;
import com.manhhuy.myapplication.helper.response.EventRegistrationResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventRegistrationAdapter extends RecyclerView.Adapter<EventRegistrationAdapter.ViewHolder> {

    private List<EventRegistrationResponse> registrations;
    private OnRegistrationClickListener listener;

    public interface OnRegistrationClickListener {
        void onRegistrationClick(EventRegistrationResponse registration);
    }

    public EventRegistrationAdapter(List<EventRegistrationResponse> registrations) {
        this.registrations = registrations;
    }

    public void setListener(OnRegistrationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventRegistrationBinding binding = ItemEventRegistrationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventRegistrationResponse registration = registrations.get(position);
        holder.bind(registration);
    }

    @Override
    public int getItemCount() {
        return registrations != null ? registrations.size() : 0;
    }

    public void setRegistrations(List<EventRegistrationResponse> registrations) {
        this.registrations = registrations;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEventRegistrationBinding binding;

        public ViewHolder(ItemEventRegistrationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(EventRegistrationResponse registration) {
            // Set event title
            binding.tvEventTitle.setText(registration.getEventTitle() != null ? 
                    registration.getEventTitle() : "Sự kiện");

            // Set registration status
            String status = getStatusText(registration.getStatus());
            binding.tvStatus.setText(status);
            binding.tvStatus.setBackgroundResource(getStatusBackground(registration.getStatus()));

            // Set join date
            if (registration.getJoinDate() != null) {
                binding.tvJoinDate.setText("Đăng ký: " + formatDate(registration.getJoinDate()));
            } else {
                binding.tvJoinDate.setText("Đăng ký: N/A");
            }

            // Set check-in status
            if (registration.getCheckedIn() != null && registration.getCheckedIn()) {
                binding.tvCheckIn.setVisibility(View.VISIBLE);
                binding.tvCheckIn.setText("✓ Đã check-in");
            } else {
                binding.tvCheckIn.setVisibility(View.GONE);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRegistrationClick(registration);
                }
            });
        }

        private String getStatusText(String status) {
            if (status == null) return "Chưa xác nhận";
            switch (status) {
                case "PENDING":
                    return "Chờ duyệt";
                case "APPROVED":
                    return "Đã duyệt";
                case "REJECTED":
                    return "Bị từ chối";
                case "CANCELLED":
                    return "Đã hủy";
                default:
                    return status;
            }
        }

        private int getStatusBackground(String status) {
            if (status == null) return R.drawable.bg_status_pending;
            switch (status) {
                case "PENDING":
                    return R.drawable.bg_status_pending;
                case "APPROVED":
                    return R.drawable.bg_status_approved;
                case "REJECTED":
                    return R.drawable.bg_status_rejected;
                case "CANCELLED":
                    return R.drawable.bg_status_rejected;
                default:
                    return R.drawable.bg_status_pending;
            }
        }

        private String formatDate(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return date != null ? outputFormat.format(date) : dateString;
            } catch (Exception e) {
                return dateString;
            }
        }
    }
}
