package com.bear.ms.OrderService.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "ORDER_DETAILS")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long orderId;

    @Column(name= "PRODUCT_ID")
    private long productId;

    @Column(name= "QUANTITY")
    private long quantity;

    @Column(name= "ORDER_DATE")
    private Instant orderDate;

    @Column(name= "STATUS")
    private String orderStatus;

    @Column(name= "TOTAL_AMOUNT")
    private long amount;


}
