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

    /**
     * 前台：获取图书列表
     * 逻辑：如果有搜索关键词，走模糊查询；否则查所有。
     */
    public List<Book> getBooks(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return bookMapper.findByKeyword(keyword);
        }
        return bookMapper.findAll();
    }

    /**
     * 后台：获取管理员视角的图书列表
     * 逻辑：包含进价 (Cost Price) 和供应商信息
     */
    public List<Map<String, Object>> getAdminBooks() {
        return bookMapper.findBooksForAdmin();
    }

    /**
     * 后台：新书录入
     * 逻辑：调用 Mapper 插入 T_BOOKS 表
     */
    @Transactional
    public void addBook(Book book) {
        // 这里可以加一些业务校验，比如 ISBN 是否已存在
        // 简单起见直接调用 DAO
        bookMapper.insertBook(book);
    }

    /**
     * 后台：获取缺货记录
     * 逻辑：查询视图 V_PURCHASE_GUIDE
     */
    public List<Map<String, Object>> getShortages() {
        return bookMapper.findShortages();
    }

    /**
     * 后台：补货 (进货)
     * 逻辑：更新 T_BOOKS 的库存。
     * 注意：不需要手动去改 T_SHORTAGE 表的状态，
     * 因为你已经在数据库写了触发器 TR_Restock_Clear_Shortage，
     * 当库存 Stock_Qty >= Min_Stock 时，数据库会自动把缺货记录标记为 'Done'。
     */
    @Transactional
    public void restockBook(String isbn, Integer qty) {
        bookMapper.restockBook(isbn, qty);
    }

    /**
     * 后台：获取仪表盘统计数据
     * 逻辑：计算总营收和估算利润
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 从视图 V_SALES_REPORT 获取总销售额
        Double totalRevenue = bookMapper.getTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }

        stats.put("dailySales", totalRevenue); // 模拟今日销售额

        // 模拟利润计算：假设利润是销售额的 40%
        // 在真实系统中，这应该通过 SUM(Quantity * (Unit_Price - Cost_Price)) 来精确计算
        stats.put("dailyProfit", totalRevenue * 0.4);

        return stats;
    }
}