package com.taskwell.model;

public enum TaskStatus {
    PENDING, // Created but not yet started
    IN_PROGRESS, // Started and actively being worked on
    COMPLETE, // Finished
    CANCELLED, // Cancelled before completion
    ON_HOLD, // Temporarily paused
    OVERDUE, // Past due date and uncompleted
    ARCHIVED // No longer active but kept for records
}