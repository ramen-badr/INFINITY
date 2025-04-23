package com.shop.server.repository;

import com.shop.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByPhoneNumber(String phoneNumber);
}