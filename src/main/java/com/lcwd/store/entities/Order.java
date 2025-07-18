package com.lcwd.store.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.modelmapper.internal.bytebuddy.matcher.FilterableList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "orders")
public class Order {

    @Id
    private String orderId;
    //pending delivered dispatched
    private String orderStatus;
    //not-paid, paid
    private String paymentStatus;
    private Long orderAmount;
    @Column(length = 1000)
    private String billingAddress;
    private String billingPhone;
    private String billingName;
    private Date orderedDate;
    private Date deliveredDate;
    private String razorPayPaymentId;
    private String razorPayOrderId;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private User referralUser;
    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<OrderItem> orderItems=new ArrayList<>();

}
