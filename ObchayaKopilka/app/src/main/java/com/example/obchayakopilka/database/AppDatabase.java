package com.example.obchayakopilka.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.obchayakopilka.models.*;

@Database(entities = {User.class, Transaction.class, Category.class, FinancialGoal.class}, version = 1)
@TypeConverters({Converters.class})  // ← ДОБАВЬ ЭТУ СТРОКУ
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract FinancialGoalDao financialGoalDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "family_budget.db")
                    .createFromAsset("family_budget.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
