package com.lcwd.store.repositories;

import com.lcwd.store.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,String> {
    Optional<Role> findByRoleName(String roleName);
}
