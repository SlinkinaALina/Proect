package com.example.obchayakopilka.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.obchayakopilka.R;
import com.example.obchayakopilka.database.AppDatabase;
import com.example.obchayakopilka.models.User;
import com.example.obchayakopilka.utils.SessionManager;
import java.util.Calendar;
import java.util.Date;

public class AnalyticsFragment extends Fragment {

    private TextView tvTotalIncome, tvTotalExpense, tvBalance, tvAvgExpense;
    private AppDatabase database;
    private SessionManager sessionManager;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        tvTotalIncome = view.findViewById(R.id.tv_total_income);
        tvTotalExpense = view.findViewById(R.id.tv_total_expense);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvAvgExpense = view.findViewById(R.id.tv_avg_expense);

        database = AppDatabase.getInstance(getContext());
        sessionManager = new SessionManager(getContext());
        currentUser = sessionManager.getCurrentUser();

        loadAnalytics();

        return view;
    }

    private void loadAnalytics() {
        new Thread(() -> {
            Double totalIncome = database.transactionDao().getTotalByType(currentUser.getId(), "income");
            Double totalExpense = database.transactionDao().getTotalByType(currentUser.getId(), "expense");

            if (totalIncome == null) totalIncome = 0.0;
            if (totalExpense == null) totalExpense = 0.0;
            double balance = totalIncome - totalExpense;

            // Средние расходы в день (за последние 30 дней)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            Date startDate = cal.getTime();
            Double last30DaysExpense = database.transactionDao()
                    .getSumByTypeAndDate(currentUser.getId(), "expense", startDate, new Date());
            if (last30DaysExpense == null) last30DaysExpense = 0.0;
            double avgExpense = last30DaysExpense / 30;

            final double finalTotalIncome = totalIncome;
            final double finalTotalExpense = totalExpense;
            final double finalBalance = balance;
            final double finalAvgExpense = avgExpense;

            getActivity().runOnUiThread(() -> {
                tvTotalIncome.setText(String.format("Всего доходов: %.2f ₽", finalTotalIncome));
                tvTotalExpense.setText(String.format("Всего расходов: %.2f ₽", finalTotalExpense));
                tvBalance.setText(String.format("Баланс: %.2f ₽", finalBalance));
                tvAvgExpense.setText(String.format("Средние расходы в день: %.2f ₽", finalAvgExpense));
            });
        }).start();
    }
}
