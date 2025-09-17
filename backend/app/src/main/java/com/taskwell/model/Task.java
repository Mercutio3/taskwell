package com.taskwell.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "title" })
})
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @ManyToOne
    @jakarta.persistence.JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user; // The user to whom the task is assigned

    @NotNull(message = "Status is required")
    private TaskStatus status;

    @FutureOrPresent
    private LocalDateTime dueDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    @NotNull(message = "Priority is required")
    private TaskPriority priority; // LOW, MEDIUM, HIGH

    private TaskCategory category;

    // No-args constructor required by JPA
    public Task() {
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;

    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    public TaskCategory getCategory() {
        return category;
    }
}
