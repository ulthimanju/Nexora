package com.nexora.auth.controller;

import com.nexora.auth.dto.request.PromoteUserRequest;
import com.nexora.auth.dto.response.AdminDashboardDTO;
import com.nexora.auth.dto.response.UserSummaryDTO;
import com.nexora.auth.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.nexora.auth.constants.ServiceConstants.ADMIN_BASE_PATH;
import static com.nexora.auth.constants.ServiceConstants.ADMIN_DASHBOARD_PATH;
import static com.nexora.auth.constants.ServiceConstants.ADMIN_ROLE_EXPRESSION;
import static com.nexora.auth.constants.ServiceConstants.ADMIN_USERS_PATH;

@RestController
@RequestMapping(ADMIN_BASE_PATH)
@RequiredArgsConstructor
public class AdminManagementController {

    private final AdminService adminService;

    @GetMapping(ADMIN_DASHBOARD_PATH)
    @PreAuthorize(ADMIN_ROLE_EXPRESSION)
    public ResponseEntity<AdminDashboardDTO> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    @GetMapping(ADMIN_USERS_PATH)
    @PreAuthorize(ADMIN_ROLE_EXPRESSION)
    public ResponseEntity<List<UserSummaryDTO>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping(ADMIN_USERS_PATH + "/{userId}/promote")
    @PreAuthorize(ADMIN_ROLE_EXPRESSION)
    public ResponseEntity<UserSummaryDTO> promoteUser(
            @PathVariable UUID userId,
            @Valid @RequestBody PromoteUserRequest request
    ) {
        return ResponseEntity.ok(adminService.promoteUser(userId, request));
    }
}
