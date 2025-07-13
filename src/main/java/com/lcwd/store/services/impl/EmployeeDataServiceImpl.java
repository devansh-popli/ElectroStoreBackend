package com.lcwd.store.services.impl;

import com.lcwd.store.dtos.*;
import com.lcwd.store.entities.Category;
import com.lcwd.store.entities.Employee;
import com.lcwd.store.entities.Nominee;
import com.lcwd.store.entities.Product;
import com.lcwd.store.exceptions.ResourceNotFoundException;
import com.lcwd.store.helper.HelperUtils;
import com.lcwd.store.repositories.EmployeeRepository;
import com.lcwd.store.repositories.NomineeRepository;
import com.lcwd.store.services.FileService;
import jakarta.persistence.LockModeType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeDataServiceImpl implements com.lcwd.store.services.EmployeeDataService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NomineeRepository nomineeRepository;
    @Autowired
    private FileService fileService;

    public EmployeeDataServiceImpl() {
    }
    @Autowired
    ModelMapper modelMapper;

    @Value("${document.image.path}")
    private String imageUploadPath;
    @Override
    public EmployeeDto saveEmployeeWithNominees(EmployeeDto employee) {
        Employee employeeData=modelMapper.map(employee, Employee.class);
        // Save the employee along with associated nomine
       List<Nominee> nominees= employeeData.getNominees().stream().map(nominee -> {
            nominee.setEmployee(employeeData);
            return nominee;
        }).collect(Collectors.toList());
        employeeData.setNominees(nominees);

        return modelMapper.map(employeeRepository.save(employeeData), EmployeeDto.class);
    }

    @Override
    public PageableResponse<EmployeeDto> getAllEmployees(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sort);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);
        PageableResponse<EmployeeDto> response = HelperUtils.getPageableResponse(employeePage, EmployeeDto.class);
        return response;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public EmployeeDto updateEmployee( int employeeId,String type,String imageName) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        EmployeeDto employeeDto= modelMapper.map(employee, EmployeeDto.class);
        if(type.equalsIgnoreCase("aadhar"))
            employeeDto.setAadharImageId(imageName);
        else if(type.equalsIgnoreCase("driving"))
            employeeDto.setDrivingImageId(imageName);
        else if(type.equalsIgnoreCase("pan"))
            employeeDto.setPanCardImageId(imageName);
        else if(type.equalsIgnoreCase("passport"))
            employeeDto.setPassportImageId(imageName);
        else if(type.equalsIgnoreCase("signature"))
            employeeDto.setSignatureImageId(imageName);
        else if(type.equalsIgnoreCase("profileImage"))
            employeeDto.setProfileImageId(imageName);
        else if(type.equalsIgnoreCase("bankDocument"))
            employeeDto.setBankDocumentImageId(imageName);
        Employee updatedEmployee = employeeRepository.save(modelMapper.map(employeeDto,Employee.class));
        return modelMapper.map(updatedEmployee, EmployeeDto.class);
    }

    @Override
    public ImageResponse uploadDocumentImage(MultipartFile image, int employeeId, String type) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);
        ImageResponse response = ImageResponse.builder().success(true).status(HttpStatus.OK).imageName(imageName).message("Image Saved Successfully").build();
        EmployeeDto employeeDto1 = updateEmployee( employeeId,type,imageName);
        return response;
    }

    public EmployeeDto getEmployee(int employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        return modelMapper.map(employee, EmployeeDto.class);
    }
    @Override
    public String getLastEmployeeCode() {
        String lastAccountCode = employeeRepository.findLastEmployeeCode();
        return lastAccountCode != null ? lastAccountCode : "EMP-000";
    }

    @Override
    public void deleteProduct(int employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("product not found"));
        String imageFullPath = imageUploadPath + employee.getAadharImageId();
        Path path = Paths.get(imageFullPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }   imageFullPath = imageUploadPath + employee.getBankDocumentImageId();
         path = Paths.get(imageFullPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }   imageFullPath = imageUploadPath + employee.getDrivingImageId();
         path = Paths.get(imageFullPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }   imageFullPath = imageUploadPath + employee.getPassportImageId();
         path = Paths.get(imageFullPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }   imageFullPath = imageUploadPath + employee.getPanCardImageId();
         path = Paths.get(imageFullPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }   imageFullPath = imageUploadPath + employee.getProfileImageId();
         path = Paths.get(imageFullPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }   imageFullPath = imageUploadPath + employee.getSignatureImageId();
         path = Paths.get(imageFullPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(employee.getNominees()!=null && !CollectionUtils.isEmpty(employee.getNominees()))
        {
            for(Nominee nominee: employee.getNominees())
            {
                nominee.setEmployee(null);
            }
            employee.getNominees().clear();
            employeeRepository.save(employee);
        }
        employeeRepository.delete(employee);
    }
}
