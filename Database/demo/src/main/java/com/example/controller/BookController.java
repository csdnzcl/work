package com.example.controller;

import com.example.mapper.BookMapper;
import com.example.pojo.Book;
import com.example.pojo.OrderRequest;
import com.example.service.BookService;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookService bookService;
    @Autowired
    private OrderService orderService;

    // ================= 前台用户接口 =================

    // 1. 获取图书列表
    @GetMapping("/books")
    public List<Book> getAllBooks(@RequestParam(required = false) String keyword) {
        return bookService.getBooks(keyword);
    }

    // 2. 提交订单
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

    // 3. 历史订单 (🔴 修复点：调用 Service 方法，稍后我们在 Service 里补上)
    @GetMapping("/orders/history")
    public List<Map<String, Object>> getHistory(@RequestParam Integer customerId) {
        return orderService.getCustomerHistory(customerId);
    }

    // ================= 后台管理员接口 =================

    // 4. 管理员图书列表
    @GetMapping("/admin/books")
    public List<Map<String, Object>> getAdminBooks() {
        return bookMapper.findBooksForAdmin();
    }

    // 5. 新书录入 (🔴 核心修复：直接调 Service，解决了参数不匹配报错)
    @PostMapping("/admin/books")
    public Map<String, Object> addBook(@RequestBody Book book) {
        try {
            bookService.addBook(book);
            return Map.of("success", true, "message", "录入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "录入失败: " + e.getMessage());
        }
    }

    // 6. 获取缺货记录
    @GetMapping("/shortages")
    public List<Map<String, Object>> getShortages() {
        return bookMapper.findShortages();
    }

    // 7. 补货
    @PostMapping("/shortages/restock")
    public Map<String, Object> restock(@RequestBody Map<String, Object> payload) {
        String isbn = (String) payload.get("isbn");
        Integer qty = (Integer) payload.get("qty");
        bookMapper.restockBook(isbn, qty);
        return Map.of("success", true, "message", "补货成功");
    }

    // 8. 订单管理与发货
    @GetMapping("/admin/orders")
    public List<Map<String, Object>> getAdminOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping("/orders/ship")
    public Map<String, Object> shipOrder(@RequestBody Map<String, Object> payload) {
        Integer orderId = (Integer) payload.get("id");
        orderService.shipOrder(orderId);
        return Map.of("success", true, "message", "发货成功");
    }

    // 9. 财务仪表盘
    @GetMapping("/admin/stats")
    public Map<String, Object> getStats() {
        return bookService.getDashboardStats();
    }

    // 10. 清空库存接口
    @PostMapping("/admin/books/clear")
    public Map<String, Object> clearStock(@RequestBody Map<String, List<String>> payload) {
        List<String> isbns = payload.get("isbns"); // 接收 ISBN 列表
        try {
            for (String isbn : isbns) {
                // 直接调 Mapper (简单逻辑无需 Service)
                bookMapper.clearStock(isbn);
            }
            return Map.of("success", true, "message", "库存已清零");
        } catch (Exception e) {
            return Map.of("success", false, "message", "操作失败: " + e.getMessage());
        }
    }
}