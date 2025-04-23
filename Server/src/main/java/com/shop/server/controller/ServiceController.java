package com.shop.server.controller;

import com.shop.server.dto.ServiceRequest;
import com.shop.server.model.Service;
import com.shop.server.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public List<Long> getAllServiceIds() {
        return serviceRepository.findAll()
                .stream()
                .map(Service::getId)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable Long id) {
        return serviceRepository.findById(id)
                .map(service -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("name", service.getName());
                    response.put("price", service.getPrice());
                    response.put("imageUrl", "services/images/" + service.getImageUrl());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource image = new ClassPathResource("images/" + filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @PostMapping
    public ResponseEntity<?> createService(@RequestBody ServiceRequest serviceRequest) {
        Service newService = new Service();
        newService.setName(serviceRequest.getName());
        newService.setPrice(serviceRequest.getPrice());
        newService.setImageUrl(serviceRequest.getImageUrl());

        Service savedService = serviceRepository.save(newService);

        return ResponseEntity.ok(savedService.getId());
    }
}