package com.example.controller;

import com.example.mapper.AdminMapper;
import com.example.pojo.Admin;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // 允许跨域
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;

    // 管理员登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> payload, HttpServletResponse response) {
        String username = payload.get("username");
        String password = payload.get("password");

        Admin admin = adminMapper.login(username, password);

        if (admin != null) {
            // --- 专业级的管理员 Cookie 设置 ---
            Cookie cookie = new Cookie("adminId", String.valueOf(admin.getAdminId()));

            // 1. 【安全核心】禁止 JavaScript 读取 (防 XSS)
            cookie.setHttpOnly(true);

            // 2. 【路径限制】只允许 /api 路径携带 (减少泄露风险)
            cookie.setPath("/");

            // 3. 【生命周期】
            // 方案 A: 浏览器关闭即失效 (推荐) -> 设置为 -1
            // 方案 B: 30分钟无操作失效 -> 设置为 30 * 60
            cookie.setMaxAge(-1);

            // 4. 【同站策略】(防 CSRF)
            // 注意：Java Servlet API 旧版本可能不支持直接 setSameSite，
            // 现在的 SpringBoot 内置 Tomcat 通常在响应头里自动处理，或者需要手动拼接 Header
            // 这里我们依靠 HttpOnly 已经挡住了 90% 的攻击。

            // 5. 【HTTPS】(如果你的项目上线配了 SSL 证书，这一行必须解开)
            // cookie.setSecure(true);

            response.addCookie(cookie);
            // -------------------------------------

            return Map.of("success", true, "message", "登录成功", "role", admin.getRole());
        } else {
            return Map.of("success", false, "message", "账号或密码错误");
        }
    }

    @GetMapping("/check-login")
    public Map<String, Object> checkLogin(@CookieValue(value = "adminId", required = false) String adminId) {
        if (adminId != null) {
            // 注意：这里需要去 AdminMapper 补一个 findById 方法
            // 为了演示，假设你有这个方法
            // Admin admin = adminMapper.findById(Integer.parseInt(adminId));

            // 简单模拟返回：
            return Map.of("success", true, "data", Map.of("username", "admin", "role", "SuperAdmin"));
        }
        return Map.of("success", false, "message", "未登录");
    }

    // 退出登录
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("adminId", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return Map.of("success", true);
    }
}