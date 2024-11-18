package com.example.brokeragefirmapp.controller;

import com.example.brokeragefirmapp.dto.ApiResponse;
import com.example.brokeragefirmapp.dto.AssetDTO;
import com.example.brokeragefirmapp.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */


@RestController
@RequestMapping("/api/admin")
public class AdminController {


    private final AdminService adminService;

    public AdminController( AdminService adminService ) {
        this.adminService = adminService;
    }

    @PostMapping("/match-orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> matchPendingOrders() {
        adminService.matchPendingOrders();
        ApiResponse<Void> response = new ApiResponse<>(true, "Pending orders matched successfully", null);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/create-asset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssetDTO>> createAsset( @RequestBody AssetDTO assetDTO) {
        AssetDTO createdAsset = adminService.createAsset(assetDTO);
        ApiResponse<AssetDTO> response = new ApiResponse<>(true, "Asset created successfully", createdAsset);
        return ResponseEntity.ok(response);
    }
}
