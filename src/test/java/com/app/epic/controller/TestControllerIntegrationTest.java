package com.app.epic.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TestControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testHomeEndpointIntegration() throws Exception {
        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Epic Application está online"))
                .andExpect(jsonPath("$.data.application").value("Epic Application"))
                .andExpect(jsonPath("$.data.status").value("running"))
                .andExpect(jsonPath("$.data.message").value("Aplicação está funcionando perfeitamente!"))
                .andExpect(jsonPath("$.data.h2Console").value("/h2-console"))
                .andExpect(jsonPath("$.data.apiDocs").exists());
    }
    
    @Test
    void testTestEndpointIntegration() throws Exception {
        // When & Then
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Endpoint de teste funcionando corretamente"))
                .andExpect(jsonPath("$.data.endpoint").value("test"))
                .andExpect(jsonPath("$.data.status").value("working"))
                .andExpect(jsonPath("$.data.timestamp").exists());
    }
    
    @Test
    void testNonExistentEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/nonexistent"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testH2ConsoleAccess() throws Exception {
        // When & Then
        // No perfil de teste, o console H2 está desabilitado, então esperamos 404
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().isNotFound()); // Console H2 desabilitado em testes
    }
} 