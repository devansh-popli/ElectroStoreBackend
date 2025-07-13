package com.lcwd.store.controller;

import com.lcwd.store.dtos.*;
import com.lcwd.store.services.EmployeeDataService;
import com.lcwd.store.services.FileService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.MessageUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeDataService employeeDataService;
    @Autowired
    FileService fileService;
    @Value("${document.image.path}")
    private String imageUploadPath;

    @PostMapping("/save")
    public ResponseEntity<EmployeeDto> saveEmployee(@RequestBody EmployeeDto employee) throws IOException {
        EmployeeDto savedEmployee = employeeDataService.saveEmployeeWithNominees(employee);
//        List<CompletableFuture<ImageResponse>> documentUploadFutures = new ArrayList<>();
//        if(!employee.getAadharDocumentFiles().isEmpty())
//        {
//            for (MultipartFile aadharDocumentFile : employee.getAadharDocumentFiles()) {
//                documentUploadFutures.add(uploadDocumentImage(aadharDocumentFile,employee.getId(),"aadhar"));
//            }
//        }if(!employee.getPanDocumentFiles().isEmpty())
//        {
//            for (MultipartFile panDocumentFile : employee.getPanDocumentFiles()) {
//                documentUploadFutures.add(uploadDocumentImage(panDocumentFile,employee.getId(),"pan"));
//            }
//        }if(!employee.getPassportDocumentFiles().isEmpty())
//        {
//            for (MultipartFile passportDocumentFile : employee.getPassportDocumentFiles()) {
//                documentUploadFutures.add(uploadDocumentImage(passportDocumentFile,employee.getId(),"passport"));
//            }
//        }if(!employee.getDrivingDocumentFiles().isEmpty())
//        {
//            for (MultipartFile drivingDocumentFile : employee.getDrivingDocumentFiles()) {
//                documentUploadFutures.add(uploadDocumentImage(drivingDocumentFile,employee.getId(),"driving"));
//            }
//        }if(!employee.getProfileImage().isEmpty())
//        {
//            documentUploadFutures.add(uploadDocumentImage(employee.getProfileImage(),employee.getId(),"profileImage"));
//        }if(!employee.getSignatureImage().isEmpty())
//        {
//            documentUploadFutures.add(uploadDocumentImage(employee.getSignatureImage(),employee.getId(),"signature"));
//        }if(!employee.getBankDocumentImage().isEmpty())
//        {
//            documentUploadFutures.add(uploadDocumentImage(employee.getBankDocumentImage(),employee.getId(),"bankDocument"));
//        }
//
//        CompletableFuture<ImageResponse>[] futuresArray = documentUploadFutures.toArray(new CompletableFuture[0]);
//        CompletableFuture<Void> allOf = CompletableFuture.allOf(futuresArray);

//        try {
//            allOf.get(); // Wait for all document uploads to complete
//        } catch (InterruptedException | ExecutionException e) {
//            // Handle exceptions if necessary
//        }
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<PageableResponse<EmployeeDto>> getAllEmployees(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                         @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,

                                                                         @RequestParam(value = "sortBy", defaultValue = "firstName", required = false)
                                                                             String sortBy,
                                                                         @RequestParam(value = "sortDir", defaultValue = "asc", required = false)
                                                                             String sortDir) {
        PageableResponse<EmployeeDto> employees = employeeDataService.getAllEmployees(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
    @PostMapping("/upload/image/{employeeId}/{type}")
    public ResponseEntity<ImageResponse> uploadDocumentImage(@RequestParam("employeeDocumentImage") MultipartFile image,@PathVariable int employeeId,@PathVariable String type) throws IOException {

 ImageResponse response = employeeDataService.uploadDocumentImage(image,employeeId,type);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/image/{employeeId}/{type}")
    public void getProductImage(@PathVariable int employeeId, @PathVariable String type,HttpServletResponse response) throws IOException {
        EmployeeDto employeeDto = employeeDataService.getEmployee(employeeId);
        log.info("Product Image Name {}", type);
        String imageName="";
        if(type.equalsIgnoreCase("aadhar"))
            imageName=employeeDto.getAadharImageId();
        else if(type.equalsIgnoreCase("driving"))
            imageName=employeeDto.getDrivingImageId();
        else if(type.equalsIgnoreCase("pan"))
            imageName=employeeDto.getPanCardImageId();
        else if(type.equalsIgnoreCase("passport"))
            imageName=employeeDto.getPassportImageId();
        else if(type.equalsIgnoreCase("signature"))
           imageName= employeeDto.getSignatureImageId();
        else if(type.equalsIgnoreCase("profileImage"))
            imageName=employeeDto.getProfileImageId();
        else if(type.equalsIgnoreCase("bankDocument"))
           imageName= employeeDto.getBankDocumentImageId();
        InputStream resource = fileService.getResouce(imageUploadPath, imageName);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        //to remember
        StreamUtils.copy(resource, response.getOutputStream());

    }
    @GetMapping("/lastEmployeeCode")
    public String getLastAccountCode() {
        String lastAccountCode = employeeDataService.getLastEmployeeCode();
        return lastAccountCode;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable("employeeId") int employeeId) {
        employeeDataService.deleteProduct(employeeId);
        ApiResponseMessage apiResponseMessage = ApiResponseMessage.builder().message("Product Deleted Successfully")
                .success(true)
                .status(HttpStatus.OK).build();
        return new ResponseEntity<>(apiResponseMessage, HttpStatus.OK);
    }

}
