package com.manhhuy.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.databinding.SearchResultItemBinding;
import com.manhhuy.myapplication.model.SearchResult;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private List<SearchResult> results;
    private OnResultClickListener listener;

    public interface OnResultClickListener {
        void onResultClick(SearchResult result);
    }

    public SearchResultAdapter(List<SearchResult> results) {
        this.results = results;
    }

    public void setListener(OnResultClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchResultItemBinding binding = SearchResultItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new SearchResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        SearchResult result = results.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return results != null ? results.size() : 0;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private final SearchResultItemBinding binding;

        public SearchResultViewHolder(SearchResultItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SearchResult result) {
            binding.searchResultTitle.setText(result.getTitle());
            binding.searchResultOrganization.setText(result.getOrganization());
            binding.searchResultLocation.setText(result.getLocation());
            
            // Load image from URL or drawable
            if (result.getImageUrl() != null && !result.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(result.getImageUrl())
                        .centerCrop()
                        .into(binding.searchResultImage);
            } else {
                binding.searchResultImage.setImageResource(result.getImageResId());
            }
            
            // Set description
            binding.searchResultDescription.setText(result.getDescription() != null ? 
                    result.getDescription() : "");
            
            // Set deadline
            binding.searchResultDeadline.setText(result.getDeadline() != null ? 
                    result.getDeadline() : "N/A");
            
            // Set slots
            String slotsText = result.getTotalSlots() > 0 ? 
                    result.getTotalSlots() + " slots" : "No slots";
            binding.searchResultSlots.setText(slotsText);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onResultClick(result);
                }
            });
        }
    }
}

