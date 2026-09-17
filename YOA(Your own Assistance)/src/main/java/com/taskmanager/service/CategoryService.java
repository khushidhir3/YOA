package com.taskmanager.service;

import com.taskmanager.dto.request.CategoryRequest;
import com.taskmanager.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryRequest request, String username);
    Category updateCategory(Long id, CategoryRequest request, String username);
    void deleteCategory(Long id, String username);
    List<Category> getCategoriesForUser(String username);
}
