package com.manhhuy.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.databinding.ItemRedeemRequestBinding;
import com.manhhuy.myapplication.helper.response.UserRewardResponse;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminRedeemRequestAdapter extends RecyclerView.Adapter<AdminRedeemRequestAdapter.ViewHolder> {

    private Context context;
    private List<UserRewardResponse> requestList;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onApprove(UserRewardResponse request);
        void onReject(UserRewardResponse request);
    }

    public AdminRedeemRequestAdapter(Context context, List<UserRewardResponse> requestList, OnRequestActionListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRedeemRequestBinding binding = ItemRedeemRequestBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserRewardResponse request = requestList.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemRedeemRequestBinding binding;

        public ViewHolder(@NonNull ItemRedeemRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(UserRewardResponse request) {
            if (request.getUserName() != null) {
                binding.tvUserName.setText(request.getUserName());
            } else {
                binding.tvUserName.setText("Unknown User");
            }

            if (request.getRewardName() != null) {
                binding.tvRewardName.setText(request.getRewardName());
            } else {
                binding.tvRewardName.setText("Unknown Reward");
            }

            binding.tvPoints.setText("-" + request.getPointsSpent() + " điểm");

            if (request.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                binding.tvDate.setText(sdf.format(request.getCreatedAt()));
            }

            binding.btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(request);
            });

            binding.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(request);
            });
        }
    }
}
