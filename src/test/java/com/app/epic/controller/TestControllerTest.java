package com.app.epic.controller;

import com.app.epic.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {
    
    @InjectMocks
    private TestController testController;
    
    private MockMvc mockMvc;
    
    @Test
    void testHome() {
        // Given & When
        ResponseEntity<ApiResponse<Map<String, Object>>> response = testController.home();
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        ApiResponse<Map<String, Object>> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Epic Application está online", body.getMessage());
        
        Map<String, Object> data = body.getData();
        assertNotNull(data);
        assertEquals("Epic Application", data.get("application"));
        assertEquals("running", data.get("status"));
        assertEquals("Aplicação está funcionando perfeitamente!", data.get("message"));
        assertEquals("/h2-console", data.get("h2Console"));
    }
    
    @Test
    void testHomeEndpoint() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
        
        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Epic Application está online"))
                .andExpect(jsonPath("$.data.application").value("Epic Application"))
                .andExpect(jsonPath("$.data.status").value("running"));
    }
    
    @Test
    void testTestMethod() {
        // Given & When
        ResponseEntity<ApiResponse<Map<String, String>>> response = testController.test();
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        ApiResponse<Map<String, String>> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Endpoint de teste funcionando corretamente", body.getMessage());
        
        Map<String, String> data = body.getData();
        assertNotNull(data);
        assertEquals("test", data.get("endpoint"));
        assertEquals("working", data.get("status"));
        assertNotNull(data.get("timestamp"));
    }
    
    @Test
    void testTestEndpoint() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
        
        // When & Then
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Endpoint de teste funcionando corretamente"))
                .andExpect(jsonPath("$.data.endpoint").value("test"))
                .andExpect(jsonPath("$.data.status").value("working"));
    }
} 