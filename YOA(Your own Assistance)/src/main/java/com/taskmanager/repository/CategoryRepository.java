package com.taskmanager.repository;

import com.taskmanager.entity.Category;
import com.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.user = :user OR c.user IS NULL ORDER BY c.name")
    List<Category> findByUserOrGlobal(@Param("user") User user);

    List<Category> findByUser(User user);
    boolean existsByNameAndUser(String name, User user);
}
