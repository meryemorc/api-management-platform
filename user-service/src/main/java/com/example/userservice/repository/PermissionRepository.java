package com.example.userservice.repository;

import com.example.userservice.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByName(String Name);
    boolean existsByName(String Name);
    List<Permission> findByNameIn(List<String> names);

}