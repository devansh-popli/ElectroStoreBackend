package com.lcwd.store.dtos;

import com.lcwd.store.entities.Employee;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NomineeDto {
    private int id;
    private String name;
    private String relationship;
    private String mobile;
    private String aadharCard;
}
