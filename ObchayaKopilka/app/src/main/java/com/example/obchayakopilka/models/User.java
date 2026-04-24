package com.example.obchayakopilka.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String email;
    private String username;
    private String password;
    private String role;
    private int familyBudgetId;

    public User() {}

    public User(String email, String username, String password, String role, int familyBudgetId) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.familyBudgetId = familyBudgetId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getFamilyBudgetId() { return familyBudgetId; }
    public void setFamilyBudgetId(int familyBudgetId) { this.familyBudgetId = familyBudgetId; }
}
