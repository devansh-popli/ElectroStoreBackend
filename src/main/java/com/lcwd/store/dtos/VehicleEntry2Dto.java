package com.lcwd.store.dtos;

import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleEntry2Dto {
    private Long id;
    private String purpose;
    private String dated;
    private String documentType;
    private String documentNo;
    private String vendorName;
    private String vehicleNumber;
    private String vehicleType;
    private String tuuAbo;
    private String dateOfEntry;
    private List<String> vehicleDocument = new ArrayList<>();
    private List<String> vehicleImages = new ArrayList<>();
    private List<String> driverDocument = new ArrayList<>();
    private String dayOfEntry;
    private String timeOfEntry;
    private String dateOfExit;
    private String dayOfExit;
    private String timeOfExit;
    private String phoneNo;
    private String panNo;
    private String bankAccountNo;
    private String briefDescription;
    private String paymentType;
    private String paymentTerms;
    private String commercialCost;
    private String assignedToRole;
    private String status;
    private String statusUpdatedBy;
    private String remarks;
    private String assignedToUser;
    private List<StatusHistoryDto> statusHistories;
}

