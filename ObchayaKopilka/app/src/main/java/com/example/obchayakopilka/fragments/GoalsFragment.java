package com.example.obchayakopilka.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.obchayakopilka.R;
import com.example.obchayakopilka.database.AppDatabase;
import com.example.obchayakopilka.models.FinancialGoal;
import com.example.obchayakopilka.models.User;
import com.example.obchayakopilka.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalsFragment extends Fragment {

    private RecyclerView rvGoals;
    private TextView tvNoGoals;
    private Button btnAddGoal;
    private List<FinancialGoal> goalList;
    private AppDatabase database;
    private SessionManager sessionManager;
    private User currentUser;
    private SimpleDateFormat dateFormat;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        rvGoals = view.findViewById(R.id.rv_goals);
        tvNoGoals = view.findViewById(R.id.tv_no_goals);
        btnAddGoal = view.findViewById(R.id.btn_add_goal);

        database = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());
        currentUser = sessionManager.getCurrentUser();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        goalList = new ArrayList<>();

        btnAddGoal.setOnClickListener(v -> showAddGoalDialog());
        loadGoals();

        return view;
    }

    private void loadGoals() {
        if (currentUser == null) return;

        new Thread(() -> {
            List<FinancialGoal> goals = database.financialGoalDao().getGoalsByUser(currentUser.getId());
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    goalList.clear();
                    goalList.addAll(goals);
                    updateUI();
                });
            }
        }).start();
    }

    private void updateUI() {
        if (getContext() == null) return;

        if (goalList.isEmpty()) {
            rvGoals.setVisibility(View.GONE);
            tvNoGoals.setVisibility(View.VISIBLE);
        } else {
            rvGoals.setVisibility(View.VISIBLE);
            tvNoGoals.setVisibility(View.GONE);

            // Создаём кастомный адаптер для RecyclerView
            GoalsAdapter adapter = new GoalsAdapter(goalList);
            rvGoals.setLayoutManager(new LinearLayoutManager(getContext()));
            rvGoals.setAdapter(adapter);
        }
    }

    private void showAddGoalDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_goal, null);

        EditText etName = dialogView.findViewById(R.id.et_goal_name);
        EditText etTarget = dialogView.findViewById(R.id.et_target_amount);
        EditText etCurrent = dialogView.findViewById(R.id.et_current_amount);
        TextView tvDeadline = dialogView.findViewById(R.id.tv_deadline);

        Calendar cal = Calendar.getInstance();
        tvDeadline.setText(dateFormat.format(cal.getTime()));
        tvDeadline.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar newCal = Calendar.getInstance();
                        newCal.set(year, month, dayOfMonth);
                        tvDeadline.setText(dateFormat.format(newCal.getTime()));
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        builder.setView(dialogView)
                .setTitle("Добавить финансовую цель")
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String name = etName.getText().toString();
                    double target = Double.parseDouble(etTarget.getText().toString());
                    double current = etCurrent.getText().toString().isEmpty() ? 0 : Double.parseDouble(etCurrent.getText().toString());

                    try {
                        Date deadline = dateFormat.parse(tvDeadline.getText().toString());
                        FinancialGoal goal = new FinancialGoal(name, target, current, deadline, currentUser.getId());
                        new Thread(() -> {
                            database.financialGoalDao().insert(goal);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    loadGoals();
                                    Toast.makeText(getContext(), "Цель добавлена", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }).start();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    // Внутренний класс адаптера для RecyclerView
    private class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

        private List<FinancialGoal> goals;

        GoalsAdapter(List<FinancialGoal> goals) {
            this.goals = goals;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FinancialGoal goal = goals.get(position);
            String title = goal.getName();
            String subtitle = String.format(Locale.getDefault(), "%.2f / %.2f ₽ (%.0f%%)",
                    goal.getCurrentAmount(), goal.getTargetAmount(), goal.getProgress());

            holder.text1.setText(title);
            holder.text2.setText(subtitle);
        }

        @Override
        public int getItemCount() {
            return goals.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;

            ViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
