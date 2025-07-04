package com.app.epic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    
    private User user;
    private Role role;
    private Address address;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword123")
                .enabled(true)
                .firstName("Test")
                .lastName("User")
                .phone("11999999999")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
                
        role = Role.builder()
                .name("USER")
                .description("User role")
                .build();
                
        address = Address.builder()
                .street("Rua Teste, 123")
                .city("São Paulo")
                .state("SP")
                .zipCode("01234-567")
                .country("Brasil")
                .isPrimary(true)
                .build();
    }
    
    @Test
    void testUserCreation() {
        // Then
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("encodedPassword123", user.getPassword());
        assertTrue(user.getEnabled());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("11999999999", user.getPhone());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());
    }
    
    @Test
    void testUserBuilder() {
        // Given & When
        User newUser = User.builder()
                .username("builder")
                .email("builder@test.com")
                .password("password")
                .build();
        
        // Then
        assertNotNull(newUser);
        assertEquals("builder", newUser.getUsername());
        assertEquals("builder@test.com", newUser.getEmail());
        assertEquals("password", newUser.getPassword());
        assertTrue(newUser.getEnabled()); // Default value
        assertNotNull(newUser.getRoles()); // Default empty set
        assertNotNull(newUser.getAddresses()); // Default empty set
    }
    
    @Test
    void testAddRole() {
        // When
        user.addRole(role);
        
        // Then
        assertTrue(user.getRoles().contains(role));
        assertTrue(role.getUsers().contains(user));
        assertEquals(1, user.getRoles().size());
    }
    
    @Test
    void testRemoveRole() {
        // Given
        user.addRole(role);
        
        // When
        user.removeRole(role);
        
        // Then
        assertFalse(user.getRoles().contains(role));
        assertFalse(role.getUsers().contains(user));
        assertEquals(0, user.getRoles().size());
    }
    
    @Test
    void testAddAddress() {
        // When
        user.addAddress(address);
        
        // Then
        assertTrue(user.getAddresses().contains(address));
        assertEquals(user, address.getUser());
        assertEquals(1, user.getAddresses().size());
    }
    
    @Test
    void testRemoveAddress() {
        // Given
        user.addAddress(address);
        
        // When
        user.removeAddress(address);
        
        // Then
        assertFalse(user.getAddresses().contains(address));
        assertNull(address.getUser());
        assertEquals(0, user.getAddresses().size());
    }
    
    @Test
    void testGetFullNameWithBothNames() {
        // When
        String fullName = user.getFullName();
        
        // Then
        assertEquals("Test User", fullName);
    }
    
    @Test
    void testGetFullNameWithoutNames() {
        // Given
        user.setFirstName(null);
        user.setLastName(null);
        
        // When
        String fullName = user.getFullName();
        
        // Then
        assertEquals("testuser", fullName);
    }
    
    @Test
    void testGetFullNameWithOnlyFirstName() {
        // Given
        user.setLastName(null);
        
        // When
        String fullName = user.getFullName();
        
        // Then
        assertEquals("testuser", fullName);
    }
    
    @Test
    void testDefaultValues() {
        // Given
        User defaultUser = new User();
        
        // When & Then
        assertNotNull(defaultUser.getRoles());
        assertNotNull(defaultUser.getAddresses());
        assertTrue(defaultUser.getRoles().isEmpty());
        assertTrue(defaultUser.getAddresses().isEmpty());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Given
        User user1 = User.builder()
                .id(1L)
                .username("test")
                .email("test@test.com")
                .password("pass")
                .build();
                
        User user2 = User.builder()
                .id(1L)
                .username("test")
                .email("test@test.com")
                .password("pass")
                .build();
        
        // When & Then
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
} 