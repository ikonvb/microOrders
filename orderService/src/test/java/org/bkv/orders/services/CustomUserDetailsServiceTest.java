package org.bkv.orders.services;

import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.repo.UserRepository;
import org.bkv.orders.utils.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_existingAdmin_returnsUserDetailsWithAdminRole() {
        UserEntity admin = new UserEntity();
        admin.setUserName("adminUser");
        admin.setPassword("password");
        admin.setRole("admin");

        when(userRepository.findByUserName("adminUser")).thenReturn(Optional.of(admin));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("adminUser");

        assertEquals("adminUser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Roles.ROLE_ADMIN.name())));
    }

    @Test
    void loadUserByUsername_existingUser_returnsUserDetailsWithUserRole() {
        UserEntity user = new UserEntity();
        user.setUserName("normalUser");
        user.setPassword("password");
        user.setRole("user");

        when(userRepository.findByUserName("normalUser")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("normalUser");

        assertEquals("normalUser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Roles.ROLE_USER.name())));
    }

    @Test
    void loadUserByUsername_nonExistingUser_throwsUsernameNotFoundException() {
        when(userRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown"));
    }
}