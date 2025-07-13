package com.lcwd.store.dtos;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VisitorDto {
        private Long id;

        private String name;
        private String fatherName;
        private String phone;
        private String address;
        private String photo; // Assuming you store the photo file path or URL
        private String purpose;
        private String concernPerson;
        private String timeIn;
        private String timeOut;
        private String aadharNumber;

    // Getters and setters

    }

