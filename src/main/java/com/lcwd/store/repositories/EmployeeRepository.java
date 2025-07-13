package com.lcwd.store.repositories;

import com.lcwd.store.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    @Query("SELECT MAX(f.empCode) FROM Employee f")
    String findLastEmployeeCode();

}
