package com.example.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private Integer customerId;
    private String name;
    private String password; // 简单演示，明文存储
    private String address;
    private BigDecimal balance;
    private Integer creditLevel;
    private BigDecimal totalSpent;

    // 省略 Getter/Setter
}