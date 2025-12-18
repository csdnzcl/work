package com.example.mapper;

import com.example.pojo.Book;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper {

    // 1. 查所有书 (前台用)
    @Select("SELECT * FROM V_BOOK_CATALOG")
    List<Book> findAll();

    // 2. 模糊搜索 (前台用)
    @Select("SELECT * FROM V_BOOK_CATALOG WHERE Title LIKE CONCAT('%',#{keyword},'%') OR Authors LIKE CONCAT('%',#{keyword},'%')")
    List<Book> findByKeyword(String keyword);

    // 3. 插入新书 (管理员录入)
    @Insert("INSERT INTO T_BOOKS (ISBN, Title, Price, Stock_Qty, Min_Stock, Publisher) VALUES (#{isbn}, #{title}, #{price}, #{stockQty}, #{minStock}, #{publisher})")
    void insertBook(Book book);

    // 4. 建立书与供应商的关联 (如果不写这个，新书就没有供应商信息)
    @Insert("INSERT INTO T_BOOK_SUPPLIERS (ISBN, SupplierID) VALUES (#{isbn}, 1)")
    void insertBookSupplier(String isbn);

    // 5. 管理员查书 (带进价)
    @Select("SELECT " +
            "ISBN as isbn, " +
            "Title as title, " +
            "Publisher as publisher, " +
            "Price as price, " +
            "Stock_Qty as stockQty, " +
            "Min_Stock as minStock, " +
            "0.6 * Price as costPrice, " +
            "Authors as authors " +
            "FROM V_BOOK_CATALOG")
    List<Map<String, Object>> findBooksForAdmin();

    // 6. 补货 (多参数，必须加 @Param ！！！)
    @Update("UPDATE T_BOOKS SET Stock_Qty = Stock_Qty + #{qty} WHERE ISBN = #{isbn}")
    void restockBook(@Param("isbn") String isbn, @Param("qty") Integer qty);

    // 7. 查缺货记录
    // 对应数据库视图 V_PURCHASE_GUIDE
    @Select("SELECT * FROM V_PURCHASE_GUIDE")
    List<Map<String, Object>> findShortages();

    // 8. 财务统计 (Service 的 getDashboardStats 方法会用到)
    // 对应数据库视图 V_SALES_REPORT
    @Select("SELECT SUM(Total_Revenue) FROM V_SALES_REPORT")
    Double getTotalRevenue();
}