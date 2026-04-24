package com.example.obchayakopilka.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.obchayakopilka.R;
import com.example.obchayakopilka.database.AppDatabase;
import com.example.obchayakopilka.models.User;
import com.example.obchayakopilka.utils.SessionManager;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvTotalBalance, tvMonthIncome, tvMonthExpense, tvWelcome;
    private AppDatabase database;
    private SessionManager sessionManager;
    private User currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvTotalBalance = view.findViewById(R.id.tv_total_balance);
        tvMonthIncome = view.findViewById(R.id.tv_month_income);
        tvMonthExpense = view.findViewById(R.id.tv_month_expense);
        tvWelcome = view.findViewById(R.id.tv_welcome);

        database = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());
        currentUser = sessionManager.getCurrentUser();

        if (currentUser != null) {
            tvWelcome.setText("Добро пожаловать, " + currentUser.getUsername() + "!");
        }

        loadStatistics();

        return view;
    }

    private void loadStatistics() {
        if (currentUser == null) return;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        new Thread(() -> {
            Double monthIncome = database.transactionDao()
                    .getSumByTypeAndDate(currentUser.getId(), "income", startDate, endDate);
            Double monthExpense = database.transactionDao()
                    .getSumByTypeAndDate(currentUser.getId(), "expense", startDate, endDate);
            Double allIncome = database.transactionDao().getTotalByType(currentUser.getId(), "income");
            Double allExpense = database.transactionDao().getTotalByType(currentUser.getId(), "expense");

            if (monthIncome == null) monthIncome = 0.0;
            if (monthExpense == null) monthExpense = 0.0;
            if (allIncome == null) allIncome = 0.0;
            if (allExpense == null) allExpense = 0.0;

            double balance = allIncome - allExpense;
            final double finalMonthIncome = monthIncome;
            final double finalMonthExpense = monthExpense;
            final double finalBalance = balance;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvTotalBalance.setText(String.format(Locale.getDefault(), "Общий баланс: %.2f ₽", finalBalance));
                    tvMonthIncome.setText(String.format(Locale.getDefault(), "Доходы за месяц: %.2f ₽", finalMonthIncome));
                    tvMonthExpense.setText(String.format(Locale.getDefault(), "Расходы за месяц: %.2f ₽", finalMonthExpense));
                });
            }
        }).start();
    }
}