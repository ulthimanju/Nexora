// Refactored: extracted 6 constants
package com.nexora.auth.service;

import com.nexora.auth.constants.LogMessages;
import com.nexora.auth.model.Role;
import com.nexora.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import static com.nexora.auth.constants.ErrorMessages.ROLE_NOT_FOUND_WITH_NAME;
import static com.nexora.auth.constants.ServiceConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        List<String> defaultRoles = Arrays.asList(
                ROLE_ADMIN,
                ROLE_INSTRUCTOR,
                ROLE_STUDENT,
                ROLE_GUEST
        );

        for (String roleName : defaultRoles) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role(roleName);
                roleRepository.save(role);
                log.info(LogMessages.CREATED_ROLE, roleName);
            }
        }
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException(String.format(ROLE_NOT_FOUND_WITH_NAME, name)));
    }
}
