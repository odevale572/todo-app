package com.todo.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todo.app.model.TodoItem;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    int countAllByCompleted(boolean completed);

    List<TodoItem> findAllByCompleted(boolean completed);
}