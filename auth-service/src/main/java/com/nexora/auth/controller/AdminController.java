// Refactored: extracted 9 constants
package com.nexora.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import static com.nexora.auth.constants.ServiceConstants.*;

@RestController
@RequestMapping(ADMIN_BASE_PATH)
@RequiredArgsConstructor
public class AdminController {

    @GetMapping(ADMIN_DASHBOARD_PATH)
    @PreAuthorize(ADMIN_ROLE_EXPRESSION)
    public ResponseEntity<Map<String, String>> getDashboard() {
        return ResponseEntity.ok(Map.of(
                RESPONSE_MESSAGE_KEY, ADMIN_DASHBOARD_MESSAGE,
                RESPONSE_STATUS_KEY, RESPONSE_STATUS_ACTIVE
        ));
    }

    @GetMapping(ADMIN_USERS_PATH)
    @PreAuthorize(ADMIN_ROLE_EXPRESSION)
    public ResponseEntity<Map<String, String>> getUsers() {
        return ResponseEntity.ok(Map.of(
                RESPONSE_MESSAGE_KEY, ADMIN_USERS_MESSAGE,
                RESPONSE_STATUS_KEY, RESPONSE_STATUS_ACTIVE
        ));
    }
}
