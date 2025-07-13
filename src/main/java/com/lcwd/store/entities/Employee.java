package com.lcwd.store.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "employee")
    private List<Nominee> nominees=new ArrayList<>();
    private String empCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String houseNo;
    private String street;
    private String landmark;
    private String cityTehsil;
    private String postcode;
    private String additionalPhoneNumber;
    private String sameAsResidential;
    private String state;
    private String department;
    private String currentHouseNo;
    private String currentStreet;
    private String currentLandmark;
    private String currentCityTehsil;
    private String currentState;
    private String currentPostcode;
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
    private String nameOfBank;
    private String branch;
    private String accountHolderName;
    private String accountNo;
    private String ifscNo;
    private String disabilityStatus;
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
    private String profileImageId;
    private String signatureImageId;
    private String aadharImageId;
    private String panCardImageId;
    private String drivingImageId;
    private String passportImageId;
    private String bankDocumentImageId;
}
