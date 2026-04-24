package com.example.obchayakopilka.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String type;
    private String iconPath;
    private double limitAmount;
    private int userId;

    public Category() {}

    public Category(String name, String type, String iconPath, double limitAmount, int userId) {
        this.name = name;
        this.type = type;
        this.iconPath = iconPath;
        this.limitAmount = limitAmount;
        this.userId = userId;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getIconPath() { return iconPath; }
    public void setIconPath(String iconPath) { this.iconPath = iconPath; }
    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
