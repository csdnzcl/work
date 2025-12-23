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
    /**
     * 后台：新书录入 (升级版：支持多作者)
     * 支持格式： "作者A, 作者B, 作者C" (中英文逗号均可)
     */
    @Transactional
    public void addBook(Book book) {
        // 1. 插入图书基本信息
        bookMapper.insertBook(book);

        // 2. 插入供应商关联
        Integer supplierId = book.getSupplierId();
        if (supplierId == null) supplierId = 1;
        bookMapper.insertBookSupplier(book.getIsbn(), supplierId);

        // 3. 【核心升级】处理多位作者
        if (book.getAuthorName() != null && !book.getAuthorName().trim().isEmpty()) {
            // Step A: 统一分隔符 (把中文逗号替换成英文逗号)，然后分割
            String[] authorNames = book.getAuthorName()
                    .replace("，", ",") // 兼容中文逗号
                    .split(",");

            // Step B: 循环处理每一位作者
            // 注意：你的数据库触发器 TR_Limit_Authors 限制最多 4 位作者
            // 这里我们在代码层也做个截断，只取前 4 个，防止报错
            int maxAuthors = Math.min(authorNames.length, 4);

            for (int i = 0; i < maxAuthors; i++) {
                String name = authorNames[i].trim();
                if (name.isEmpty()) continue; // 跳过空名字

                // 查重：作者是否存在？
                Integer authorId = bookMapper.findAuthorIdByName(name);

                if (authorId == null) {
                    // 不存在 -> 新建作者
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", name);
                    bookMapper.insertAuthor(params);
                    authorId = ((Number) params.get("id")).intValue();
                }

                // 插入关联表 (Rank 从 1 开始，依次递增)
                // 参数：(ISBN, AuthorID, Rank) -> Rank = i + 1
                // 注意：你需要去 BookMapper 确认 insertBookAuthor 是否支持 Rank 参数
                // 如果之前的 Mapper 没写 Rank 参数，需要去改一下 Mapper (见下文)
                bookMapper.insertBookAuthor(book.getIsbn(), authorId, i + 1);
            }
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