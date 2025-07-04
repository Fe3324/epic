package com.app.epic.controller;

import com.app.epic.dto.*;
import com.app.epic.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    
    private final AddressService addressService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AddressResponseDTO>>> findAll(
            @PageableDefault(size = 20, sort = "city", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.info("GET /api/addresses - Finding all addresses with pagination");
        
        PageResponse<AddressResponseDTO> addresses = addressService.findAll(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(addresses, "Endereços listados com sucesso"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponseDTO>> findById(@PathVariable Long id) {
        log.info("GET /api/addresses/{} - Finding address by id", id);
        
        AddressResponseDTO address = addressService.findById(id);
        
        return ResponseEntity.ok(ApiResponse.success(address, "Endereço encontrado com sucesso"));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AddressResponseDTO>>> findByUserId(@PathVariable Long userId) {
        log.info("GET /api/addresses/user/{} - Finding addresses by user id", userId);
        
        List<AddressResponseDTO> addresses = addressService.findByUserId(userId);
        
        return ResponseEntity.ok(ApiResponse.success(addresses, "Endereços do usuário listados com sucesso"));
    }
    
    @GetMapping("/user/{userId}/primary")
    public ResponseEntity<ApiResponse<AddressResponseDTO>> findPrimaryAddressByUserId(@PathVariable Long userId) {
        log.info("GET /api/addresses/user/{}/primary - Finding primary address by user id", userId);
        
        Optional<AddressResponseDTO> primaryAddress = addressService.findPrimaryAddressByUserId(userId);
        
        if (primaryAddress.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(primaryAddress.get(), "Endereço principal encontrado"));
        } else {
            return ResponseEntity.ok(ApiResponse.success(null, "Usuário não possui endereço principal"));
        }
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<AddressResponseDTO>>> findByCity(@PathVariable String city) {
        log.info("GET /api/addresses/city/{} - Finding addresses by city", city);
        
        List<AddressResponseDTO> addresses = addressService.findByCity(city);
        
        return ResponseEntity.ok(ApiResponse.success(addresses, "Endereços da cidade '" + city + "' listados com sucesso"));
    }
    
    @GetMapping("/state/{state}")
    public ResponseEntity<ApiResponse<List<AddressResponseDTO>>> findByState(@PathVariable String state) {
        log.info("GET /api/addresses/state/{} - Finding addresses by state", state);
        
        List<AddressResponseDTO> addresses = addressService.findByState(state);
        
        return ResponseEntity.ok(ApiResponse.success(addresses, "Endereços do estado '" + state + "' listados com sucesso"));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AddressResponseDTO>>> searchByLocation(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state) {
        
        log.info("GET /api/addresses/search?city={}&state={} - Searching addresses by location", city, state);
        
        List<AddressResponseDTO> addresses = addressService.searchByLocation(
            city != null ? city : "",
            state != null ? state : ""
        );
        
        return ResponseEntity.ok(ApiResponse.success(addresses, "Busca por localização realizada com sucesso"));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponseDTO>> create(@Valid @RequestBody AddressCreateDTO createDTO) {
        log.info("POST /api/addresses - Creating new address for user id: {}", createDTO.getUserId());
        
        AddressResponseDTO createdAddress = addressService.create(createDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdAddress));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponseDTO>> update(
            @PathVariable Long id, 
            @Valid @RequestBody AddressUpdateDTO updateDTO) {
        
        log.info("PUT /api/addresses/{} - Updating address", id);
        
        AddressResponseDTO updatedAddress = addressService.update(id, updateDTO);
        
        return ResponseEntity.ok(ApiResponse.success(updatedAddress, "Endereço atualizado com sucesso"));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.info("DELETE /api/addresses/{} - Deleting address", id);
        
        addressService.delete(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Endereço deletado com sucesso"));
    }
    
    @PatchMapping("/user/{userId}/primary/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponseDTO>> setPrimaryAddress(
            @PathVariable Long userId, 
            @PathVariable Long addressId) {
        
        log.info("PATCH /api/addresses/user/{}/primary/{} - Setting primary address", userId, addressId);
        
        AddressResponseDTO primaryAddress = addressService.setPrimaryAddress(userId, addressId);
        
        return ResponseEntity.ok(ApiResponse.success(primaryAddress, "Endereço principal definido com sucesso"));
    }
} 