package com.lcwd.store.services;

import com.lcwd.store.dtos.EmployeeDto;
import com.lcwd.store.dtos.ImageResponse;
import com.lcwd.store.dtos.PageableResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EmployeeDataService {
    EmployeeDto saveEmployeeWithNominees(EmployeeDto employee);

    PageableResponse<EmployeeDto> getAllEmployees(int pageNumber, int pageSize, String sortBy, String sortDir);

    EmployeeDto getEmployee(int employeeId);

    EmployeeDto updateEmployee( int employeeId,String type,String imageName);

    ImageResponse uploadDocumentImage(MultipartFile image, int employeeId, String type) throws IOException;

    String getLastEmployeeCode();

    void deleteProduct(int employeeId);
}
