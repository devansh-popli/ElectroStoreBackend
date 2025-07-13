package com.lcwd.store.dtos;

import com.lcwd.store.entities.OrderItem;
import com.lcwd.store.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String orderId;
    //pending delivered dispatched
    private String orderStatus="PENDING";
    //notpaid, paid
    private String paymentStatus="NOTPAID";
    private int orderAmount;
    private String billingAddress;
    private String billingPhone;
    private String billingName;
    private Date orderedDate=new Date();
    private Date deliveredDate;
    private UserDto user;
    private List<OrderItemDto> orderItems=new ArrayList<>();
    private String razorPayOrderId;
    private String razorPayPaymentId;
    @Setter(AccessLevel.NONE)
    private String referralCode;
    private Boolean isChildOrder;
    private UserDto childUser;
    private Double yourCommission;
}
