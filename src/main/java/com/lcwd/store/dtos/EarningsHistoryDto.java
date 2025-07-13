package com.lcwd.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EarningsHistoryDto {
    private Date month;
    private Long totalOrderAmount;
    private Long totalCommission;
}
