package com.lcwd.store.repositories;

// AttendanceRepository.java
import com.lcwd.store.entities.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Page<Attendance> findByEmpCode(Pageable pageable, String empCode);

    List<Attendance> findByEmpCodeAndAttendanceDate(String empCode, Date date);

    Page<Attendance> findByAttendanceDate(Pageable pageable, Date sqlDate);

    @Query("SELECT a FROM Attendance a WHERE MONTH(a.attendanceDate) = :month AND YEAR(a.attendanceDate) = :year")
    Page<Attendance> findByMonthAndYear(@Param("month") int month, @Param("year") int year, Pageable pageable);

    @Query("SELECT a FROM Attendance a WHERE a.empCode = :empCode AND MONTH(a.attendanceDate) = :month AND YEAR(a.attendanceDate) = :year")
    Page<Attendance> findByEmpCodeAndMonthAndYear(
            @Param("empCode") String empCode,
            @Param("month") int month,
            @Param("year") int year,
            Pageable pageable
    );
}
