package com.taskmanager.service.impl;

import com.taskmanager.dao.UserDao;
import com.taskmanager.dto.request.CategoryRequest;
import com.taskmanager.entity.Category;
import com.taskmanager.entity.User;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.repository.CategoryRepository;
import com.taskmanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserDao userDao;

    @Override
    @Transactional
    public Category createCategory(CategoryRequest request, String username) {
        User user = getUser(username);
        Category category = Category.builder()
                .name(request.getName())
                .color(request.getColor())
                .user(user)
                .build();
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryRequest request, String username) {
        Category category = getWithAccess(id, username);
        category.setName(request.getName());
        category.setColor(request.getColor());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id, String username) {
        Category category = getWithAccess(id, username);
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesForUser(String username) {
        User user = getUser(username);
        return categoryRepository.findByUserOrGlobal(user);
    }

    private User getUser(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private Category getWithAccess(Long id, String username) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        User user = getUser(username);
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        if (!isAdmin && (cat.getUser() == null || !cat.getUser().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You don't own this category");
        }
        return cat;
    }
}
