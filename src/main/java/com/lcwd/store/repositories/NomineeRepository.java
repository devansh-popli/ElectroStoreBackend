package com.lcwd.store.repositories;

import com.lcwd.store.entities.Nominee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NomineeRepository extends JpaRepository<Nominee,Integer> {
}
