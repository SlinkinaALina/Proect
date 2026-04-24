package com.example.obchayakopilka.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.obchayakopilka.R;
import com.example.obchayakopilka.adapters.CategoriesAdapter;
import com.example.obchayakopilka.database.AppDatabase;
import com.example.obchayakopilka.models.Category;
import com.example.obchayakopilka.models.User;
import com.example.obchayakopilka.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private RecyclerView rvCategories;
    private Button btnAddCategory;
    private CategoriesAdapter adapter;
    private List<Category> categoryList;
    private AppDatabase database;
    private SessionManager sessionManager;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        rvCategories = view.findViewById(R.id.rv_categories);
        btnAddCategory = view.findViewById(R.id.btn_add_category);

        database = AppDatabase.getInstance(getContext());
        sessionManager = new SessionManager(getContext());
        currentUser = sessionManager.getCurrentUser();

        categoryList = new ArrayList<>();
        adapter = new CategoriesAdapter(categoryList);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setAdapter(adapter);

        loadCategories();

        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        return view;
    }

    private void loadCategories() {
        new Thread(() -> {
            List<Category> categories = database.categoryDao().getAllCategories(currentUser.getId());
            getActivity().runOnUiThread(() -> {
                categoryList.clear();
                categoryList.addAll(categories);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void showAddCategoryDialog() {
        // Простой диалог добавления категории
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);

        android.widget.EditText etName = dialogView.findViewById(R.id.et_category_name);
        android.widget.EditText etLimit = dialogView.findViewById(R.id.et_category_limit);
        android.widget.Spinner spType = dialogView.findViewById(R.id.sp_category_type);

        builder.setView(dialogView)
                .setTitle("Добавить категорию")
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String name = etName.getText().toString();
                    String type = spType.getSelectedItem().toString().equals("Доход") ? "income" : "expense";
                    double limit = etLimit.getText().toString().isEmpty() ? 0 : Double.parseDouble(etLimit.getText().toString());

                    Category category = new Category(name, type, "", limit, currentUser.getId());
                    new Thread(() -> {
                        database.categoryDao().insert(category);
                        getActivity().runOnUiThread(() -> {
                            loadCategories();
                            Toast.makeText(getContext(), "Категория добавлена", Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}