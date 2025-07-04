package com.app.epic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {
    
    private Address address;
    private User user;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();
                
        address = Address.builder()
                .street("Av. Paulista, 1000")
                .city("São Paulo")
                .state("SP")
                .zipCode("01310-100")
                .country("Brasil")
                .isPrimary(true)
                .user(user)
                .build();
    }
    
    @Test
    void testAddressCreation() {
        // Then
        assertNotNull(address);
        assertEquals("Av. Paulista, 1000", address.getStreet());
        assertEquals("São Paulo", address.getCity());
        assertEquals("SP", address.getState());
        assertEquals("01310-100", address.getZipCode());
        assertEquals("Brasil", address.getCountry());
        assertTrue(address.getIsPrimary());
        assertEquals(user, address.getUser());
    }
    
    @Test
    void testAddressBuilder() {
        // Given & When
        Address newAddress = Address.builder()
                .street("Rua Augusta, 500")
                .city("São Paulo")
                .state("SP")
                .zipCode("01305-000")
                .build();
        
        // Then
        assertNotNull(newAddress);
        assertEquals("Rua Augusta, 500", newAddress.getStreet());
        assertEquals("São Paulo", newAddress.getCity());
        assertEquals("SP", newAddress.getState());
        assertEquals("01305-000", newAddress.getZipCode());
        assertEquals("Brasil", newAddress.getCountry()); // Default value
        assertFalse(newAddress.getIsPrimary()); // Default value
    }
    
    @Test
    void testGetFullAddress() {
        // When
        String fullAddress = address.getFullAddress();
        
        // Then
        String expected = "Av. Paulista, 1000, São Paulo, SP - 01310-100, Brasil";
        assertEquals(expected, fullAddress);
    }
    
    @Test
    void testGetFullAddressWithDifferentCountry() {
        // Given
        address.setCountry("Estados Unidos");
        
        // When
        String fullAddress = address.getFullAddress();
        
        // Then
        String expected = "Av. Paulista, 1000, São Paulo, SP - 01310-100, Estados Unidos";
        assertEquals(expected, fullAddress);
    }
    
    @Test
    void testDefaultValues() {
        // Given
        Address defaultAddress = new Address();
        
        // When & Then
        assertEquals("Brasil", defaultAddress.getCountry());
        assertFalse(defaultAddress.getIsPrimary());
    }
    
    @Test
    void testSetUser() {
        // Given
        User newUser = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password")
                .build();
        
        // When
        address.setUser(newUser);
        
        // Then
        assertEquals(newUser, address.getUser());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Given
        Address address1 = Address.builder()
                .id(1L)
                .street("Rua A")
                .city("São Paulo")
                .state("SP")
                .zipCode("12345-678")
                .build();
                
        Address address2 = Address.builder()
                .id(1L)
                .street("Rua A")
                .city("São Paulo")
                .state("SP")
                .zipCode("12345-678")
                .build();
        
        // When & Then
        assertEquals(address1, address2);
        assertEquals(address1.hashCode(), address2.hashCode());
    }
    
    @Test
    void testPrimaryAddressFlag() {
        // Given
        Address primaryAddress = Address.builder()
                .street("Rua Principal")
                .city("São Paulo")
                .state("SP")
                .zipCode("12345-678")
                .isPrimary(true)
                .build();
                
        Address secondaryAddress = Address.builder()
                .street("Rua Secundária")
                .city("São Paulo")
                .state("SP")
                .zipCode("87654-321")
                .isPrimary(false)
                .build();
        
        // When & Then
        assertTrue(primaryAddress.getIsPrimary());
        assertFalse(secondaryAddress.getIsPrimary());
    }
} 