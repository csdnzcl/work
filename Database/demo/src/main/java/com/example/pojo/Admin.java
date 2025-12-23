package com.example.pojo;

import lombok.Data;

@Data
public class Admin {
    private Integer adminId;
    private String username;
    private String password;
    private String role; // SuperAdmin, StockMgr

}