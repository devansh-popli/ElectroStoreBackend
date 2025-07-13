package com.lcwd.store.entities;


import com.lcwd.store.dtos.VehicleEntryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String status;
    private String assignedToRole;
    private String assignedToUser;
    private String statusUpdatedBy;
    private String remarks;
    @OneToMany(mappedBy = "vehicleEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusHistoryJcb> statusHistories = new ArrayList<>();
    public VehicleEntryDto toDTO() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, VehicleEntryDto.class);
    }

}
