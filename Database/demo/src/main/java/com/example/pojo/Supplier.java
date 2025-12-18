package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private Integer supplierId;
    private String name;
    private String contactInfo;

    // 省略 Getter/Setter
}