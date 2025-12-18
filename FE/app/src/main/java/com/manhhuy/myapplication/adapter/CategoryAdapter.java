package com.manhhuy.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemCategoryBinding;
import com.manhhuy.myapplication.helper.response.EventTypeResponse;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<EventTypeResponse> categories;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(EventTypeResponse category);
    }

    public CategoryAdapter(List<EventTypeResponse> categories) {
        this.categories = categories;
    }

    public void setListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        EventTypeResponse category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void setCategories(List<EventTypeResponse> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(EventTypeResponse category) {
            binding.tvCategoryName.setText(category.getName());
            
            // Set icon based on category name
            int iconRes = getCategoryIcon(category.getName());
            binding.ivCategoryIcon.setImageResource(iconRes);


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }

        private int getCategoryIcon(String categoryName) {
            if (categoryName == null) return R.drawable.ic_event;
            
            switch (categoryName) {
                case "Tất cả":
                    return R.drawable.ic_organization;
                case "Cộng đồng":
                    return R.drawable.ic_group; 
                case "Môi trường":
                    return R.drawable.ic_plant;
                case "Giáo dục":
                    return R.drawable.ic_book;
                case "Sức khỏe":
                    return R.drawable.ic_heart; 
                case "Động vật":
                    return R.drawable.ic_favorite_border;
                default:
                    return R.drawable.ic_event;
            }
        }
    }
}
