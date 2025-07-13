package com.lcwd.store.services;

import com.lcwd.store.dtos.AttendanceDto;
import com.lcwd.store.dtos.PageableResponse;

import java.sql.Date;

public interface AttendanceService {
    PageableResponse<AttendanceDto> getAllAttendances(int month, int year, String empCode, int pageNumber, int pageSize, String sortBy, String sortDir);

    AttendanceDto saveAttendance(AttendanceDto attendance);

    AttendanceDto getAttendancesByEmpCodeAndDate(String empCode, Date date);

    void deleteAttendance(Long attendanceId);

    PageableResponse<AttendanceDto> getAttendancesByDate(Date sqlDate, int pageNumber, int pageSize, String sortBy, String sortDir);

    PageableResponse<AttendanceDto> getAttendancesByMonthAndYear(int month,int year, int pageNumber, int pageSize, String sortBy, String sortDir);
}
