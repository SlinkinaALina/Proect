package com.example.obchayakopilka.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.obchayakopilka.R;
import com.example.obchayakopilka.adapters.TransactionsAdapter;
import com.example.obchayakopilka.database.AppDatabase;
import com.example.obchayakopilka.models.Category;
import com.example.obchayakopilka.models.Transaction;
import com.example.obchayakopilka.models.User;
import com.example.obchayakopilka.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionsFragment extends Fragment {

    private RecyclerView rvTransactions;
    private TransactionsAdapter adapter;
    private List<Transaction> transactionList;
    private Button btnAdd;  // ← Используем обычную кнопку вместо FAB
    private TextView tvTotalIncome, tvTotalExpense, tvBalance;
    private Spinner spFilterType;
    private Button btnFilterDate;

    private AppDatabase database;
    private SessionManager sessionManager;
    private User currentUser;
    private Date startDate, endDate;
    private SimpleDateFormat dateFormat;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        rvTransactions = view.findViewById(R.id.rv_transactions);
        btnAdd = view.findViewById(R.id.btn_add_transaction);  // ← ID кнопки
        tvTotalIncome = view.findViewById(R.id.tv_total_income);
        tvTotalExpense = view.findViewById(R.id.tv_total_expense);
        tvBalance = view.findViewById(R.id.tv_balance);
        spFilterType = view.findViewById(R.id.sp_filter_type);
        btnFilterDate = view.findViewById(R.id.btn_filter_date);

        database = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());
        currentUser = sessionManager.getCurrentUser();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        // Установка дат по умолчанию
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate = cal.getTime();
        String dateRange = dateFormat.format(startDate) + " - " + dateFormat.format(endDate);
        btnFilterDate.setText(dateRange);

        transactionList = new ArrayList<>();
        adapter = new TransactionsAdapter(transactionList, this::showEditTransactionDialog);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(adapter);

        loadTransactions();
        updateStatistics();

        btnAdd.setOnClickListener(v -> showAddTransactionDialog());
        btnFilterDate.setOnClickListener(v -> showDateRangePicker());
        spFilterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterTransactions();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void loadTransactions() {
        if (currentUser == null) return;

        new Thread(() -> {
            List<Transaction> transactions = database.transactionDao()
                    .getAllTransactions(currentUser.getId());
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    transactionList.clear();
                    transactionList.addAll(transactions);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private void updateStatistics() {
        if (currentUser == null) return;

        new Thread(() -> {
            Double totalIncome = database.transactionDao()
                    .getSumByTypeAndDate(currentUser.getId(), "income", startDate, endDate);
            Double totalExpense = database.transactionDao()
                    .getSumByTypeAndDate(currentUser.getId(), "expense", startDate, endDate);

            if (totalIncome == null) totalIncome = 0.0;
            if (totalExpense == null) totalExpense = 0.0;
            double balance = totalIncome - totalExpense;
            final double finalIncome = totalIncome;
            final double finalExpense = totalExpense;
            final double finalBalance = balance;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    String incomeText = String.format(Locale.getDefault(), "Доходы: %.2f ₽", finalIncome);
                    String expenseText = String.format(Locale.getDefault(), "Расходы: %.2f ₽", finalExpense);
                    String balanceText = String.format(Locale.getDefault(), "Баланс: %.2f ₽", finalBalance);
                    tvTotalIncome.setText(incomeText);
                    tvTotalExpense.setText(expenseText);
                    tvBalance.setText(balanceText);
                });
            }
        }).start();
    }

    private void filterTransactions() {
        if (currentUser == null) return;

        String selectedType = spFilterType.getSelectedItem().toString();

        new Thread(() -> {
            List<Transaction> filtered;
            if (selectedType.equals("Все")) {
                filtered = database.transactionDao().getAllTransactions(currentUser.getId());
            } else {
                String type = selectedType.equals("Доходы") ? "income" : "expense";
                filtered = database.transactionDao()
                        .getTransactionsByTypeAndDate(currentUser.getId(), type, startDate, endDate);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    transactionList.clear();
                    transactionList.addAll(filtered);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private void showDateRangePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog startDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar startCal = Calendar.getInstance();
                    startCal.set(year, month, dayOfMonth);
                    startDate = startCal.getTime();

                    DatePickerDialog endDialog = new DatePickerDialog(requireContext(),
                            (view2, year2, month2, dayOfMonth2) -> {
                                Calendar endCal = Calendar.getInstance();
                                endCal.set(year2, month2, dayOfMonth2);
                                endDate = endCal.getTime();
                                String dateRange = dateFormat.format(startDate) + " - " + dateFormat.format(endDate);
                                btnFilterDate.setText(dateRange);
                                updateStatistics();
                                filterTransactions();
                            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    endDialog.show();
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        startDialog.show();
    }

    private void showAddTransactionDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_transaction, null);

        EditText etAmount = dialogView.findViewById(R.id.et_amount);
        EditText etComment = dialogView.findViewById(R.id.et_comment);
        Spinner spType = dialogView.findViewById(R.id.sp_type);
        Spinner spCategory = dialogView.findViewById(R.id.sp_category);
        TextView tvDate = dialogView.findViewById(R.id.tv_date);

        Calendar cal = Calendar.getInstance();
        tvDate.setText(dateFormat.format(cal.getTime()));
        tvDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar newCal = Calendar.getInstance();
                        newCal.set(year, month, dayOfMonth);
                        tvDate.setText(dateFormat.format(newCal.getTime()));
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        // Загрузка категорий
        new Thread(() -> {
            List<Category> categories = database.categoryDao().getAllCategories(currentUser.getId());
            List<String> categoryNames = new ArrayList<>();
            Map<String, Integer> categoryIdMap = new HashMap<>();

            for (Category cat : categories) {
                categoryNames.add(cat.getName());
                categoryIdMap.put(cat.getName(), cat.getId());
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCategory.setAdapter(adapter);
                });
            }
        }).start();

        builder.setView(dialogView)
                .setTitle("Добавить операцию")
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    double amount = Double.parseDouble(etAmount.getText().toString());
                    String type = spType.getSelectedItem().toString().equals("Доход") ? "income" : "expense";
                    String comment = etComment.getText().toString();
                    String categoryName = spCategory.getSelectedItem().toString();

                    new Thread(() -> {
                        List<Category> categories = database.categoryDao().getAllCategories(currentUser.getId());
                        int categoryId = -1;
                        for (Category cat : categories) {
                            if (cat.getName().equals(categoryName)) {
                                categoryId = cat.getId();
                                break;
                            }
                        }

                        try {
                            Date selectedDate = dateFormat.parse(tvDate.getText().toString());
                            Transaction transaction = new Transaction(
                                    currentUser.getId(), categoryId, 1, amount, type, selectedDate, comment
                            );
                            database.transactionDao().insert(transaction);

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    loadTransactions();
                                    updateStatistics();
                                    Toast.makeText(getContext(), "Операция добавлена", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).start();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showEditTransactionDialog(Transaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_transaction, null);

        EditText etAmount = dialogView.findViewById(R.id.et_amount);
        EditText etComment = dialogView.findViewById(R.id.et_comment);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        etAmount.setText(String.valueOf(transaction.getAmount()));
        etComment.setText(transaction.getComment());

        builder.setView(dialogView)
                .setTitle("Редактировать операцию")
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    transaction.setAmount(Double.parseDouble(etAmount.getText().toString()));
                    transaction.setComment(etComment.getText().toString());

                    new Thread(() -> {
                        database.transactionDao().update(transaction);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                loadTransactions();
                                updateStatistics();
                                Toast.makeText(getContext(), "Операция обновлена", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Отмена", null)
                .show();

        btnDelete.setOnClickListener(v -> {
            new Thread(() -> {
                database.transactionDao().delete(transaction);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        loadTransactions();
                        updateStatistics();
                        Toast.makeText(getContext(), "Операция удалена", Toast.LENGTH_SHORT).show();

                    });
                }
            }).start();
        });
    }
}