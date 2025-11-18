package com.manhhuy.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.model.FilterCategory;
import java.util.List;

public class FilterCategoryAdapter extends RecyclerView.Adapter<FilterCategoryAdapter.FilterCategoryViewHolder> {

    private List<FilterCategory> categories;
    private OnCategorySelectionListener listener;

    public interface OnCategorySelectionListener {
        void onCategorySelected(FilterCategory category, boolean isSelected);
    }

    public FilterCategoryAdapter(List<FilterCategory> categories) {
        this.categories = categories;
    }

    public void setListener(OnCategorySelectionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Chip chip = (Chip) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_category_item, parent, false);
        return new FilterCategoryViewHolder(chip);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterCategoryViewHolder holder, int position) {
        FilterCategory category = categories.get(position);
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void setCategories(List<FilterCategory> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public static class FilterCategoryViewHolder extends RecyclerView.ViewHolder {
        private final Chip chip;

        public FilterCategoryViewHolder(Chip chip) {
            super(chip);
            this.chip = chip;
        }

        public void bind(FilterCategory category, OnCategorySelectionListener listener) {
            chip.setText(category.getName());
            chip.setChecked(category.isSelected());
            
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                category.setSelected(isChecked);
                if (listener != null) {
                    listener.onCategorySelected(category, isChecked);
                }
            });
        }
    }
}
