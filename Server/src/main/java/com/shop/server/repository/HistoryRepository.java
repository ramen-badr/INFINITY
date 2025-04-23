package com.shop.server.repository;

import com.shop.server.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, String> {
}