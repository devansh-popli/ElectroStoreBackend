package com.lcwd.store.services.impl;

// AttendanceService.java
import com.lcwd.store.dtos.AttendanceDto;
import com.lcwd.store.dtos.PageableResponse;
import com.lcwd.store.entities.Attendance;
import com.lcwd.store.entities.Category;
import com.lcwd.store.entities.Product;
import com.lcwd.store.exceptions.ResourceNotFoundException;
import com.lcwd.store.helper.HelperUtils;
import com.lcwd.store.repositories.AttendanceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AttendanceServiceImpl implements com.lcwd.store.services.AttendanceService {
@Autowired
private ModelMapper modelMapper;
    @Autowired
    private AttendanceRepository attendanceRepository;
    private int getMonthFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1; // Calendar months are zero-based
    }
    @Override
    public PageableResponse<AttendanceDto> getAllAttendances(int month,int year, String empCode, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);
        Page<Attendance> employeePage = attendanceRepository.findByEmpCodeAndMonthAndYear(empCode,month,year,pageable);
        PageableResponse<AttendanceDto> response = HelperUtils.getPageableResponse(employeePage, AttendanceDto.class);
//        response.setContent(response.getContent().stream()
//                .filter(attendance -> getMonthFromDate((Date) attendance.getAttendanceDate())==month)
//                .collect(Collectors.toList()));
        return response;
    }

    @Override
    public AttendanceDto saveAttendance(AttendanceDto attendance) {
        return modelMapper.map(attendanceRepository.save(modelMapper.map(attendance,Attendance.class)),AttendanceDto.class);
    }

    public AttendanceDto getAttendancesByEmpCodeAndDate(String empCode, Date date) {
        List<Attendance> attendance=attendanceRepository.findByEmpCodeAndAttendanceDate(empCode, date);
        if(attendance.isEmpty())
        {
            return new AttendanceDto();
        }
        return modelMapper.map(attendance.get(attendance.size()-1),AttendanceDto.class);
    }

    @Override
    public void deleteAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow(() -> new ResourceNotFoundException("product not found"));
        attendanceRepository.delete(attendance);
    }


    @Override
    public PageableResponse<AttendanceDto> getAttendancesByDate(Date sqlDate, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);

        Page<Attendance> employeePage = attendanceRepository.findByAttendanceDate(pageable,sqlDate);
        return HelperUtils.getPageableResponse(employeePage, AttendanceDto.class);
    }

    @Override
    public PageableResponse<AttendanceDto> getAttendancesByMonthAndYear(int month,int year, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);

        Page<Attendance> employeePage = attendanceRepository.findByMonthAndYear(month, year, pageable);
        //        response.setContent(response.getContent().stream()
//                .filter(attendance -> getMonthFromDate((Date) attendance.getAttendanceDate())==month)
//                .collect(Collectors.toList()));
        return HelperUtils.getPageableResponse(employeePage, AttendanceDto.class);
    }
}
