package com.example.mapper;

import com.example.pojo.Book;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper {

    // 1. 查所有书
    @Select("SELECT * FROM V_BOOK_CATALOG")
    List<Book> findAll();

    // 2. 模糊搜索
    @Select("SELECT * FROM V_BOOK_CATALOG WHERE Title LIKE CONCAT('%',#{keyword},'%') OR Authors LIKE CONCAT('%',#{keyword},'%')")
    List<Book> findByKeyword(String keyword);

    // 3. 【插入书】(只插基础字段，不涉及进价)
    @Insert("INSERT INTO T_BOOKS (ISBN, Title, Price, Stock_Qty, Min_Stock, Publisher) " +
            "VALUES (#{isbn}, #{title}, #{price}, #{stockQty}, #{minStock}, #{publisher})")
    void insertBook(Book book);

    // 4. 【插入供应商关联】
    @Insert("INSERT INTO T_BOOK_SUPPLIERS (ISBN, SupplierID) VALUES (#{isbn}, #{supplierId})")
    void insertBookSupplier(@Param("isbn") String isbn, @Param("supplierId") Integer supplierId);

    // 5. 【管理员查书】(进价由 0.6 * Price 自动计算，不查物理字段)
    @Select("SELECT " +
            "ISBN as isbn, " +
            "Title as title, " +
            "Publisher as publisher, " +
            "Price as price, " +
            "Stock_Qty as stockQty, " +
            "Min_Stock as minStock, " +
            "0.6 * Price as costPrice, " + // 虚拟进价
            "Authors as authors " +
            "FROM V_BOOK_CATALOG")
    List<Map<String, Object>> findBooksForAdmin();

    // 6. 补货
    @Update("UPDATE T_BOOKS SET Stock_Qty = Stock_Qty + #{qty} WHERE ISBN = #{isbn}")
    void restockBook(@Param("isbn") String isbn, @Param("qty") Integer qty);

    // 7. 查缺货
    @Select("SELECT * FROM V_PURCHASE_GUIDE")
    List<Map<String, Object>> findShortages();

    // 8. 财务统计
    @Select("SELECT SUM(Total_Revenue) FROM V_SALES_REPORT")
    Double getTotalRevenue();

    // --- 【新增】作者相关逻辑 ---

    // 查作者是否存在
    @Select("SELECT AuthorID FROM T_AUTHORS WHERE Name = #{name} LIMIT 1")
    Integer findAuthorIdByName(String name);

    // 插入新作者 (并回填ID)
    @Insert("INSERT INTO T_AUTHORS (Name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "AuthorID")
    void insertAuthor(Map<String, Object> params);

    // 关联书和作者
    @Insert("INSERT INTO T_BOOK_AUTHORS (ISBN, AuthorID, Author_Rank) VALUES (#{isbn}, #{authorId}, 1)")
    void insertBookAuthor(@Param("isbn") String isbn, @Param("authorId") Integer authorId);
}