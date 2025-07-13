package com.lcwd.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusHistoryDto {
    private int id;
    private String status;
    private String statusUpdatedBy;
    private String description;
    private Date timestamp;
}
