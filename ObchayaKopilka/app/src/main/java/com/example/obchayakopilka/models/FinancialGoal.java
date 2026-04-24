package com.example.obchayakopilka.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "financial_goals")
public class FinancialGoal {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private Date deadline;
    private int userId;

    public FinancialGoal() {}

    public FinancialGoal(String name, double targetAmount, double currentAmount, Date deadline, int userId) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getProgress() {
        if (targetAmount <= 0) return 0;
        return (currentAmount / targetAmount) * 100;
    }
}
