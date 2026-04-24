package com.example.obchayakopilka.database;

import androidx.room.*;
import com.example.obchayakopilka.models.FinancialGoal;
import java.util.List;

@Dao
public interface FinancialGoalDao {
    @Insert
    void insert(FinancialGoal goal);

    @Update
    void update(FinancialGoal goal);

    @Delete
    void delete(FinancialGoal goal);

    @Query("SELECT * FROM financial_goals WHERE userId = :userId")
    List<FinancialGoal> getGoalsByUser(int userId);
}
