package com.app.epic.service;

import com.app.epic.dto.*;
import com.app.epic.entity.Role;
import com.app.epic.entity.User;
import com.app.epic.exception.BusinessException;
import com.app.epic.exception.ResourceNotFoundException;
import com.app.epic.mapper.UserMapper;
import com.app.epic.repository.RoleRepository;
import com.app.epic.repository.UserRepository;
import com.app.epic.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to authenticate user: {}", request.getUsername());
        
        try {
            // Autenticar usando Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            // Carregar detalhes do usuário
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Buscar usuário completo no banco
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", "username", userDetails.getUsername()));
            
            // Atualizar último login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            // Gerar tokens
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            // Converter para DTO
            UserResponseDTO userResponse = userMapper.toResponseDTO(user);
            
            log.info("User authenticated successfully: {}", request.getUsername());
            
            return AuthResponse.of(
                accessToken,
                refreshToken,
                jwtUtil.getExpirationTime() / 1000, // converter para segundos
                userResponse
            );
            
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user: {}", request.getUsername());
            throw new BusinessException("Credenciais inválidas");
        }
    }
    
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register new user: {}", request.getUsername());
        
        // Validar se username já existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username já está em uso: " + request.getUsername());
        }
        
        // Validar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já está em uso: " + request.getEmail());
        }
        
        // Criar novo usuário
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .enabled(true)
                .build();
        
        // Adicionar role padrão USER
        Optional<Role> userRole = roleRepository.findByName("USER");
        if (userRole.isPresent()) {
            user.addRole(userRole.get());
        } else {
            // Criar role USER se não existir
            Role newUserRole = Role.builder()
                    .name("USER")
                    .description("Usuário comum do sistema")
                    .build();
            roleRepository.save(newUserRole);
            user.addRole(newUserRole);
        }
        
        // Salvar usuário
        User savedUser = userRepository.save(user);
        
        // Carregar UserDetails para gerar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        
        // Gerar tokens
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        // Converter para DTO
        UserResponseDTO userResponse = userMapper.toResponseDTO(savedUser);
        
        log.info("User registered successfully: {}", request.getUsername());
        
        return AuthResponse.of(
            accessToken,
            refreshToken,
            jwtUtil.getExpirationTime() / 1000,
            userResponse
        );
    }
    
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Attempting to refresh token");
        
        String refreshToken = request.getRefreshToken();
        
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException("Refresh token inválido ou expirado");
        }
        
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        if (!jwtUtil.isTokenValid(refreshToken, userDetails)) {
            throw new BusinessException("Refresh token inválido");
        }
        
        // Buscar usuário completo
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "username", username));
        
        // Gerar novos tokens
        String newAccessToken = jwtUtil.generateToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        // Converter para DTO
        UserResponseDTO userResponse = userMapper.toResponseDTO(user);
        
        log.info("Token refreshed successfully for user: {}", username);
        
        return AuthResponse.of(
            newAccessToken,
            newRefreshToken,
            jwtUtil.getExpirationTime() / 1000,
            userResponse
        );
    }
    
    public void logout(String token) {
        log.info("User logout requested");
        // Aqui você pode implementar uma blacklist de tokens se necessário
        // Por simplicidade, vamos apenas logar o logout
        // Em produção, você pode querer invalidar o token no cache/database
    }
    
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUser(String username) {
        log.debug("Getting current user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "username", username));
        
        return userMapper.toResponseDTO(user);
    }
    
    public AuthResponse registerWithRoles(RegisterRequest request, Set<String> roleNames) {
        log.info("Attempting to register new user with specific roles: {}", request.getUsername());
        
        // Validar se username já existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username já está em uso: " + request.getUsername());
        }
        
        // Validar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já está em uso: " + request.getEmail());
        }
        
        // Criar novo usuário
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .enabled(true)
                .build();
        
        // Adicionar roles especificadas
        for (String roleName : roleNames) {
            Optional<Role> role = roleRepository.findByName(roleName);
            if (role.isPresent()) {
                user.addRole(role.get());
            } else {
                log.warn("Role {} not found, skipping", roleName);
            }
        }
        
        // Se nenhuma role foi adicionada, adicionar USER como padrão
        if (user.getRoles().isEmpty()) {
            Optional<Role> userRole = roleRepository.findByName("USER");
            userRole.ifPresent(user::addRole);
        }
        
        // Salvar usuário
        User savedUser = userRepository.save(user);
        
        // Carregar UserDetails para gerar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        
        // Gerar tokens
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        // Converter para DTO
        UserResponseDTO userResponse = userMapper.toResponseDTO(savedUser);
        
        log.info("User registered successfully with roles {}: {}", roleNames, request.getUsername());
        
        return AuthResponse.of(
            accessToken,
            refreshToken,
            jwtUtil.getExpirationTime() / 1000,
            userResponse
        );
    }
} 