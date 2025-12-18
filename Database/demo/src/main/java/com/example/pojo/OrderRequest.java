package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {//接收前端JSON参数
    private Integer customerId;
    private String isbn;
    private Integer quantity;
}