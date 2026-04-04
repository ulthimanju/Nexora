package com.nexora.auth.service;

import com.nexora.auth.constants.LogMessages;
import com.nexora.auth.dto.request.PromoteUserRequest;
import com.nexora.auth.dto.response.AdminDashboardDTO;
import com.nexora.auth.dto.response.UserSummaryDTO;
import com.nexora.auth.exception.AuthException;
import com.nexora.auth.model.Role;
import com.nexora.auth.model.User;
import com.nexora.auth.repository.RoleRepository;
import com.nexora.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nexora.auth.constants.ErrorMessages.ROLE_NOT_FOUND_WITH_NAME;
import static com.nexora.auth.constants.ErrorMessages.USER_NOT_FOUND;
import static com.nexora.auth.constants.ServiceConstants.ADMIN_DASHBOARD_MESSAGE;
import static com.nexora.auth.constants.ServiceConstants.RESPONSE_STATUS_ACTIVE;
import static com.nexora.auth.constants.ServiceConstants.ROLE_ADMIN;
import static com.nexora.auth.constants.ServiceConstants.ROLE_INSTRUCTOR;
import static com.nexora.auth.constants.ServiceConstants.ROLE_STUDENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public AdminDashboardDTO getDashboard() {
        List<User> users = userRepository.findAll();
        long totalUsers = users.size();
        long adminCount = users.stream().filter(user -> hasRole(user, ROLE_ADMIN)).count();
        long instructorCount = users.stream().filter(user -> hasRole(user, ROLE_INSTRUCTOR)).count();
        long studentCount = users.stream().filter(user -> hasRole(user, ROLE_STUDENT)).count();

        return AdminDashboardDTO.builder()
                .status(RESPONSE_STATUS_ACTIVE)
                .message(ADMIN_DASHBOARD_MESSAGE)
                .totalUsers(totalUsers)
                .adminCount(adminCount)
                .instructorCount(instructorCount)
                .studentCount(studentCount)
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSummaryDTO promoteUser(UUID userId, PromoteUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (request.getEmail() != null && !user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new AuthException("Provided email does not match the target user", HttpStatus.BAD_REQUEST);
        }

        String normalizedRole = normalizeRole(request.getRole());
        Role role = roleRepository.findByName(normalizedRole)
                .orElseThrow(() -> new AuthException(String.format(ROLE_NOT_FOUND_WITH_NAME, normalizedRole), HttpStatus.BAD_REQUEST));

        Set<Role> roles = user.getRoles();
        if (roles.stream().noneMatch(r -> r.getName().equals(normalizedRole))) {
            roles.add(role);
            user.setRoles(roles);
            userRepository.save(user);
            log.info(LogMessages.USER_PROMOTED, user.getUsername(), normalizedRole);
        }

        return mapToSummary(user);
    }

    private UserSummaryDTO mapToSummary(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return UserSummaryDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roleNames)
                .enabled(user.isEnabled())
                .build();
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals(roleName));
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            throw new AuthException(String.format(ROLE_NOT_FOUND_WITH_NAME, role), HttpStatus.BAD_REQUEST);
        }

        String normalized = role.toUpperCase(Locale.ROOT);
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        return normalized;
    }
}
