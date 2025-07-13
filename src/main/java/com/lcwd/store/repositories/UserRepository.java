package com.lcwd.store.repositories;

import com.lcwd.store.entities.Role;
import com.lcwd.store.entities.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
 Optional<User> findByEmail(String email);
 List<User> findByNameContaining(String keyword);
    @Query(value = "SELECT u FROM User u JOIN FETCH u.roles r ",countQuery = "SELECT count(u) FROM User u ")
    Page<User> findAllWithDetails(Pageable pageable);

//    @EntityGraph(attributePaths = { "roles" ,"cart","referral","ordersByReferralUser"})
//    public Page<User> findAll(Pageable pageable);

    List<User> findByParentReferralCode(String referralId);

    Page<User> findByRoles(Pageable pageable, Role role);
}
