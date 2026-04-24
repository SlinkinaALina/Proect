package com.example.obchayakopilka.database;

import androidx.room.*;
import com.example.obchayakopilka.models.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE userId = :userId")
    List<Category> getAllCategories(int userId);

    @Query("SELECT * FROM categories WHERE userId = :userId AND type = :type")
    List<Category> getCategoriesByType(int userId, String type);

    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(int id);
}
