package com.lcwd.store.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GatePass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String edin;
    private String dateIn;
    private String dateOut;
    private String timeIn;
    private String timeOut;
    private String reason;
    private String approvedBy;
    private String nameOfApplicant;
    private String eidNumber;
    // Getters and Setters
}
