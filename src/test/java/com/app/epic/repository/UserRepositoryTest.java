package com.app.epic.repository;

import com.app.epic.entity.Role;
import com.app.epic.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    private Role testRole;
    
    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .name("USER")
                .description("User role")
                .build();
        entityManager.persistAndFlush(testRole);
        
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .firstName("Test")
                .lastName("User")
                .phone("11999999999")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
        testUser.addRole(testRole);
        entityManager.persistAndFlush(testUser);
        entityManager.clear();
    }
    
    @Test
    void testFindByUsername() {
        // When
        Optional<User> found = userRepository.findByUsername("testuser");
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("test@example.com", found.get().getEmail());
    }
    
    @Test
    void testFindByUsernameNotFound() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");
        
        // Then
        assertFalse(found.isPresent());
    }
    
    @Test
    void testFindByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }
    
    @Test
    void testExistsByUsername() {
        // When
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");
        
        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }
    
    @Test
    void testExistsByEmail() {
        // When
        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@test.com");
        
        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }
    
    @Test
    void testFindByEnabled() {
        // Given
        User disabledUser = User.builder()
                .username("disabled")
                .email("disabled@example.com")
                .password("password")
                .enabled(false)
                .build();
        entityManager.persistAndFlush(disabledUser);
        
        // When
        List<User> enabledUsers = userRepository.findByEnabled(true);
        List<User> disabledUsers = userRepository.findByEnabled(false);
        
        // Then
        assertEquals(1, enabledUsers.size());
        assertEquals("testuser", enabledUsers.get(0).getUsername());
        assertEquals(1, disabledUsers.size());
        assertEquals("disabled", disabledUsers.get(0).getUsername());
    }
    
    @Test
    void testFindByNameContaining() {
        // When
        List<User> foundByFirstName = userRepository.findByNameContaining("Test");
        List<User> foundByLastName = userRepository.findByNameContaining("User");
        List<User> notFound = userRepository.findByNameContaining("NotFound");
        
        // Then
        assertEquals(1, foundByFirstName.size());
        assertEquals("testuser", foundByFirstName.get(0).getUsername());
        assertEquals(1, foundByLastName.size());
        assertEquals("testuser", foundByLastName.get(0).getUsername());
        assertTrue(notFound.isEmpty());
    }
    
    @Test
    void testFindByRoleName() {
        // When
        List<User> usersWithRole = userRepository.findByRoleName("USER");
        List<User> usersWithoutRole = userRepository.findByRoleName("ADMIN");
        
        // Then
        assertEquals(1, usersWithRole.size());
        assertEquals("testuser", usersWithRole.get(0).getUsername());
        assertTrue(usersWithoutRole.isEmpty());
    }
    
    @Test
    void testCountActiveUsers() {
        // Given
        User disabledUser = User.builder()
                .username("disabled")
                .email("disabled@example.com")
                .password("password")
                .enabled(false)
                .build();
        entityManager.persistAndFlush(disabledUser);
        
        // When
        Long activeCount = userRepository.countActiveUsers();
        
        // Then
        assertEquals(1L, activeCount);
    }
    
    @Test
    void testSaveAndFindUser() {
        // Given
        User newUser = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password")
                .enabled(true)
                .firstName("New")
                .lastName("User")
                .build();
        
        // When
        User saved = userRepository.save(newUser);
        Optional<User> found = userRepository.findById(saved.getId());
        
        // Then
        assertNotNull(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("newuser", found.get().getUsername());
        assertEquals("new@example.com", found.get().getEmail());
    }
    
    @Test
    void testDeleteUser() {
        // Given
        Long userId = testUser.getId();
        
        // When
        userRepository.deleteById(userId);
        Optional<User> found = userRepository.findById(userId);
        
        // Then
        assertFalse(found.isPresent());
    }
} 