package com.example.controller;

import com.example.mapper.CustomerMapper;
import com.example.mapper.SupplierMapper;
import com.example.mapper.UserMapper;
import com.example.pojo.Customer;
import com.example.pojo.Supplier;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    // 1. 用户登录 (修改：增加 HttpServletResponse 参数用于设置 Cookie)
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> payload, HttpServletResponse response) {
        String username = payload.get("username");
        String password = payload.get("password");

        Customer customer = customerMapper.login(username, password);

        if (customer != null) {
            // --- 核心修改：登录成功，创建 Cookie ---
            Cookie cookie = new Cookie("loginUserId", String.valueOf(customer.getCustomerId()));
            cookie.setPath("/");           // 允许所有路径访问
            cookie.setMaxAge(7 * 24 * 3600); // 设置有效期为 7 天
            cookie.setHttpOnly(true);      // 禁止 JS 读取，提高安全性
            response.addCookie(cookie);    // 把 Cookie 加入响应头
            // -------------------------------------

            return Map.of("success", true, "data", customer);
        } else {
            return Map.of("success", false, "message", "用户名或密码错误");
        }
    }

    // 2. 检查登录状态 (新增：用于刷新页面时自动登录)
    @GetMapping("/check-login")
    public Map<String, Object> checkLogin(@CookieValue(value = "loginUserId", required = false) String loginUserId) {
        if (loginUserId != null) {
            // 如果 Cookie 存在，直接去数据库查用户信息
            Customer customer = customerMapper.findById(Integer.parseInt(loginUserId));
            if (customer != null) {
                return Map.of("success", true, "data", customer);
            }
        }
        return Map.of("success", false, "message", "未登录");
    }

    // 3. 退出登录 (新增：清除 Cookie)
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("loginUserId", null); //以此名建立一个空值cookie
        cookie.setPath("/");
        cookie.setMaxAge(0); // 设置生命周期为0，即立即删除
        response.addCookie(cookie);
        return Map.of("success", true, "message", "退出成功");
    }

    // 2. 刷新用户信息 (重点！用于购买后刷新余额)
    @GetMapping("/user/{id}")
    public Customer getUserInfo(@PathVariable Integer id) {
        return customerMapper.findById(id);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Customer customer) {
        // 1. 简单校验
        if (customer.getName() == null || customer.getPassword() == null) {
            return Map.of("success", false, "message", "用户名或密码不能为空");
        }

        // 2. 检查用户名是否重复
        int count = userMapper.countByUsername(customer.getName());
        if (count > 0) {
            return Map.of("success", false, "message", "该用户名已被注册！");
        }

        // 3. 执行插入
        try {
            userMapper.register(customer);
            return Map.of("success", true, "message", "注册成功，请登录！");
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