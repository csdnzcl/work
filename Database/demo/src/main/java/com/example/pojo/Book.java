package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data; // 如果没装Lombok，请手动生成Getter/Setter
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String isbn;
    private String title;
    private String authors;
    private String publisher;
    private Double price;
    private Integer stockQty; // 对应数据库 Stock_Qty
    private Integer minStock; // 对应数据库 Min_Stock
}