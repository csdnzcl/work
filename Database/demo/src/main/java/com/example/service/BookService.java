package com.example.service;

import com.example.mapper.BookMapper;
import com.example.pojo.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    public List<Book> getBooks(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return bookMapper.findByKeyword(keyword);
        }
        return bookMapper.findAll();
    }

    /**
     * 后台：新书录入 (业务逻辑封装在这里)
     */
    @Transactional
    public void addBook(Book book) {
        // 1. 插入图书基本信息
        bookMapper.insertBook(book);

        // 2. 插入供应商关联 (🔴 修复逻辑：获取 ID 并传给 Mapper)
        Integer supplierId = book.getSupplierId();
        if (supplierId == null) {
            supplierId = 1; // 默认关联 ID=1
        }
        // 这里传入了两个参数，完全符合 Mapper 接口定义！
        bookMapper.insertBookSupplier(book.getIsbn(), supplierId);

        // 3. 处理作者
        if (book.getAuthorName() != null && !book.getAuthorName().trim().isEmpty()) {
            String name = book.getAuthorName().trim();
            Integer authorId = bookMapper.findAuthorIdByName(name);

            if (authorId == null) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", name);
                bookMapper.insertAuthor(params);
                authorId = ((Number) params.get("id")).intValue();
            }
            bookMapper.insertBookAuthor(book.getIsbn(), authorId);
        }
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        Double total = bookMapper.getTotalRevenue();
        stats.put("dailySales", total == null ? 0.0 : total);
        stats.put("dailyProfit", total == null ? 0.0 : total * 0.4);
        return stats;
    }
}