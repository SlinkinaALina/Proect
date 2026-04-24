package com.example.obchayakopilka.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.obchayakopilka.R;
import com.example.obchayakopilka.models.Category;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Category> categories;

    public CategoriesAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvName.setText(category.getName());
        String typeText = category.getType().equals("income") ? "Доход" : "Расход";
        holder.tvType.setText(typeText);
        if (category.getLimitAmount() > 0) {
            holder.tvLimit.setText("Лимит: " + category.getLimitAmount() + " ₽");
        } else {
            holder.tvLimit.setText("Лимит не установлен");
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvLimit;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_category_name);
            tvType = itemView.findViewById(R.id.tv_category_type);
            tvLimit = itemView.findViewById(R.id.tv_category_limit);
        }
    }
}

