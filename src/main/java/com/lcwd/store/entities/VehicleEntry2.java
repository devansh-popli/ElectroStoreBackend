package com.lcwd.store.entities;

import com.lcwd.store.dtos.VehicleEntry2Dto;
import com.lcwd.store.dtos.VehicleEntryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle_entries")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleEntry2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String purpose;
    private String dated;
    private String documentType;
    private String documentNo;
    private String vendorName;
    private String vehicleNumber;
    @ElementCollection
    private List<String> vehicleDocument = new ArrayList<>();
    @ElementCollection
    private List<String> vehicleImages = new ArrayList<>();
    @ElementCollection
    private List<String> driverDocument = new ArrayList<>();
    private String vehicleType;
    private String tuuAbo;
    private String dateOfEntry;
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
    private String status;
    private String assignedToRole;
    private String assignedToUser;
    private String statusUpdatedBy;
    private String remarks;
    @OneToMany(mappedBy = "vehicleEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusHistory> statusHistories = new ArrayList<>();
    public VehicleEntry2Dto toDTO() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, VehicleEntry2Dto.class);
    }
}