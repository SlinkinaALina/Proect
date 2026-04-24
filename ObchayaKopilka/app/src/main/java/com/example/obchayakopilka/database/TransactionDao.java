package com.example.obchayakopilka.database;

import androidx.room.*;
import com.example.obchayakopilka.models.Transaction;
import java.util.Date;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    List<Transaction> getAllTransactions(int userId);

    // ДОБАВЬ ЭТИ МЕТОДЫ:

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = :type")
    Double getTotalByType(int userId, String type);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = :type AND date BETWEEN :startDate AND :endDate")
    Double getSumByTypeAndDate(int userId, String type, Date startDate, Date endDate);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type AND date BETWEEN :startDate AND :endDate")
    List<Transaction> getTransactionsByTypeAndDate(int userId, String type, Date startDate, Date endDate);
}