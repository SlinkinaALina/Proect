package com.example.obchayakopilka.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int categoryId;
    private int accountId;
    private double amount;
    private String type;
    private Date date;
    private String comment;

    public Transaction() {}

    public Transaction(int userId, int categoryId, int accountId, double amount, String type, Date date, String comment) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.comment = comment;
    }

    // Геттеры и сеттеры (нажми Ctrl+Insert внутри класса, выбери Getter and Setter)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
