package com.app.epic.controller;

import com.app.epic.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Map<String, Object>>> home() {
        Map<String, Object> data = Map.of(
            "application", "Epic Application",
            "status", "running",
            "message", "Aplicação está funcionando perfeitamente!",
            "h2Console", "/h2-console",
            "apiDocs", Map.of(
                "users", "/api/users",
                "roles", "/api/roles", 
                "addresses", "/api/addresses",
                "auth", Map.of(
                    "login", "/auth/login",
                    "register", "/auth/register",
                    "refresh", "/auth/refresh",
                    "me", "/auth/me",
                    "validate", "/auth/validate",
                    "logout", "/auth/logout"
                )
            )
        );
        
        return ResponseEntity.ok(ApiResponse.success(data, "Epic Application está online"));
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, String>>> test() {
        Map<String, String> data = Map.of(
            "endpoint", "test",
            "status", "working",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        return ResponseEntity.ok(ApiResponse.success(data, "Endpoint de teste funcionando corretamente"));
    }
} 