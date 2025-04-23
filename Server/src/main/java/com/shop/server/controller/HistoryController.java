package com.shop.server.controller;

import com.shop.server.dto.HistoryRequest;
import com.shop.server.model.History;
import com.shop.server.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/history")
public class HistoryController {

    private final HistoryRepository historyRepository;

    @Autowired
    public HistoryController(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<List<Long>> getOrderHistory(@PathVariable String phoneNumber) {
        return historyRepository.findById(phoneNumber)
                .map(history -> ResponseEntity.ok(history.getOrderIds()))
                .orElse(ResponseEntity.ok(Collections.emptyList()));
    }

    @PostMapping
    public ResponseEntity<?> updateOrderHistory(@RequestBody HistoryRequest historyRequest) {

        History history = historyRepository.findById(historyRequest.getPhoneNumber())
                .orElseGet(() -> {
                    History newHistory = new History();
                    newHistory.setPhoneNumber(historyRequest.getPhoneNumber());
                    newHistory.setOrderIds(new ArrayList<>());
                    return newHistory;
                });

        history.getOrderIds().add(historyRequest.getOrderId());
        historyRepository.save(history);

        return ResponseEntity.ok().build();
    }
}