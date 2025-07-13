package com.lcwd.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductSearchDto implements Serializable {
    private String productId;
    private String title;
    private Date addedDate;
}
