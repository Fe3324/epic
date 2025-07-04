package com.app.epic.repository;

import com.app.epic.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    boolean existsByName(String name);
    
    List<Role> findByNameContaining(String name);
    
    @Query("SELECT r FROM Role r ORDER BY r.name")
    List<Role> findAllOrderByName();
    
    @Query("SELECT COUNT(r) FROM Role r")
    Long countRoles();
} 