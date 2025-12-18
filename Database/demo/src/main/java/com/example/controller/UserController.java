package com.example.controller;

import com.example.mapper.CustomerMapper;
import com.example.mapper.SupplierMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.Customer;
import com.example.pojo.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private UserMapper userMapper;

    // 1. 用户登录 (简单版)
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> form) {
        Customer customer = userMapper.login(form.get("username"), form.get("password"));
        if (customer != null) {
            return Map.of("success", true, "data", customer);
        }
        return Map.of("success", false, "message", "用户名或密码错误");
    }

    // 2. 刷新用户信息 (重点！用于购买后刷新余额)
    @GetMapping("/user/{id}")
    public Customer getUserInfo(@PathVariable Integer id) {
        return customerMapper.findById(id);
    }

    // 3. 用户注册
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Customer customer) {
        try {
            customerMapper.register(customer);
            return Map.of("success", true, "message", "注册成功");
        } catch (Exception e) {
            return Map.of("success", false, "message", "注册失败: " + e.getMessage());
        }
    }

    // 4. 获取所有供应商 (给管理员下拉框用)
    @GetMapping("/suppliers")
    public List<Supplier> getSuppliers() {
        return supplierMapper.findAll();
    }
}