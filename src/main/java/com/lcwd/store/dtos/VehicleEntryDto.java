package com.lcwd.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleEntryDto {

    private Long id;
    private String gatePassNo;
    private String site;
    private String vehicleNumber;
    private String inTime;
    private String outTime;
    private String inDate;
    private String outDate;
    private String selectedOption;
    private String hydraCapacity;
    private String ownerBankAccount;
    private String ownerPhone;
    private String photoUrl;
    private String justification;
    private String enteredBy;
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

