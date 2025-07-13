package com.lcwd.store.dtos;


import com.lcwd.store.entities.Nominee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private int id;
    private List<NomineeDto> nominees;
    private String empCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
//    private MultipartFile profileImage;
    private String profileImageId;
    private String signatureImageId;
    private String aadharImageId;
    private String panCardImageId;
    private String drivingImageId;
    private String passportImageId;
    private String bankDocumentImageId;
//    private MultipartFile signatureImage;
    private String houseNo;
    private String street;
    private String landmark;
    private String cityTehsil;
    private String postcode;
    private String designation;
    private String jobExperience;
    private String jobExperienceLocation;
    private String aadharCard;
    private String panCard;
    private String drivingLicenseNo;
    private String passportNo;
    private String policeVerificationStation;
    private String policeVerificationCertificateNo;
    private Date dateOfIssue;
    private Date dateOfExpiry;
    private String issuedBy;
//    private List<MultipartFile> panDocumentFiles;
//    private List<MultipartFile> drivingDocumentFiles;
//    private List<MultipartFile> passportDocumentFiles;
//    private List<MultipartFile> aadharDocumentFiles;
    private String nameOfBank;
    private String branch;
    private String accountHolderName;
    private String accountNo;
    private String ifscNo;
//    private MultipartFile bankDocumentImage;
    private String disabilityStatus;
    private String additionalPhoneNumber;
    private String sameAsResidential;
    private String state;
    private String currentHouseNo;
    private String currentStreet;
    private String currentLandmark;
    private String currentCityTehsil;
    private String currentState;
    private String currentPostcode;
    private String department;
    private String height;
    private String weight;
    private String bloodGroup;
    private String disease;
    private String covidVaccination;
    private Date dateOfJoining;
    private String employmentHours;
    private String employmentStatus;
    private String monthlySalary;
    private String hourlyRate;
    private String weeklyOffDay;
}
