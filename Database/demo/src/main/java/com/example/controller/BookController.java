package com.example.controller;

import com.example.mapper.BookMapper;
import com.example.pojo.Book;
import com.example.pojo.OrderRequest;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // 允许前端跨域
public class BookController {

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private OrderService orderService;

    // ================= 前台用户接口 (对应 index0.html) =================

    // 1. 获取图书列表
    @GetMapping("/books")
    public List<Book> getAllBooks(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return bookMapper.findByKeyword(keyword);
        }
        return bookMapper.findAll();
    }

    // 2. 提交订单 (调用存储过程)
    @PostMapping("/order/submit")
    public Map<String, Object> submitOrder(@RequestBody OrderRequest request) {
        String result = orderService.processOrder(
                request.getCustomerId(),
                request.getIsbn(),
                request.getQuantity()
        );
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.startsWith("Success"));
        response.put("message", result);
        return response;
    }

    // 3. 历史订单
    @GetMapping("/orders/history")
    public List<Map<String, Object>> getHistory(@RequestParam Integer customerId) {
        // 返回的数据字段名会自动映射为小写 (例如 orderid, total_amount)
        // 前端 Vue 需要根据实际返回的 JSON 调整大小写
        return orderService.getHistory(customerId);
    }

    // ================= 后台管理员接口 (对应 admin_enhance0.html) =================

    // 4. 管理员图书列表 (带进价)
    @GetMapping("/admin/books")
    public List<Map<String, Object>> getAdminBooks() {
        return bookMapper.findBooksForAdmin();
    }

    // 5. 新书录入
    @PostMapping("/admin/books")
    public Map<String, Object> addBook(@RequestBody Book book) {
        try {
            bookMapper.insertBook(book);
            return Map.of("success", true, "message", "录入成功");
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    // 6. 获取缺货记录
    @GetMapping("/shortages")
    public List<Map<String, Object>> getShortages() {
        return bookMapper.findShortages();
    }

    // 7. 补货 (Restock)
    @PostMapping("/shortages/restock")
    public Map<String, Object> restock(@RequestBody Map<String, Object> payload) {
        String isbn = (String) payload.get("isbn");
        Integer qty = (Integer) payload.get("qty");
        bookMapper.restockBook(isbn, qty);
        return Map.of("success", true, "message", "补货成功，触发器已自动处理缺货记录");
    }

    // 8. 订单管理与发货
    @GetMapping("/admin/orders")
    public List<Map<String, Object>> getAdminOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping("/orders/ship")
    public Map<String, Object> shipOrder(@RequestBody Map<String, Object> payload) {
        Integer orderId = (Integer) payload.get("id"); // 注意前端传的是 id 还是 orderId
        orderService.shipOrder(orderId);
        return Map.of("success", true, "message", "发货成功");
    }

    // 9. 财务仪表盘统计
    @GetMapping("/admin/stats")
    public Map<String, Object> getStats() {
        Double totalRev = bookMapper.getTotalRevenue();
        if (totalRev == null) totalRev = 0.0;

        // 简单模拟利润 = 营收 * 0.4
        return Map.of(
                "dailySales", totalRev,
                "dailyProfit", totalRev * 0.4
        );
    }
}