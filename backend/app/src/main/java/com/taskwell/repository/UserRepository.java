package com.taskwell.repository;

import com.taskwell.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods
    User findByUsername(String username);
}
