package com.app.epic.repository;

import com.app.epic.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUserId(Long userId);
    
    Optional<Address> findByUserIdAndIsPrimary(Long userId, Boolean isPrimary);
    
    List<Address> findByCity(String city);
    
    List<Address> findByState(String state);
    
    List<Address> findByCountry(String country);
    
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isPrimary = true")
    Optional<Address> findPrimaryAddressByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Address a WHERE a.city LIKE %:city% OR a.state LIKE %:state%")
    List<Address> findByLocationContaining(@Param("city") String city, @Param("state") String state);
    
    @Query("SELECT COUNT(a) FROM Address a WHERE a.user.id = :userId")
    Long countAddressesByUserId(@Param("userId") Long userId);
} 