/*
package com.example.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    // 1. 创建订单主表
    @Insert("INSERT INTO T_ORDERS (CustomerID, Status, Total_Amount) VALUES (#{customerId}, 'Pending', 0)")
    @Options(useGeneratedKeys = true, keyProperty = "orderId", keyColumn = "OrderID")
    void createOrder(Map<String, Object> params); // 使用 Map 传递以便回填 ID

    // 2. 创建订单明细
    @Insert("INSERT INTO T_ORDER_DETAILS (OrderID, ISBN, Quantity) VALUES (#{orderId}, #{isbn}, #{quantity})")
    void createOrderDetail(Integer orderId, String isbn, Integer quantity);

    // 3. !核心! MyBatis 调用存储过程 SP_Process_Order_Payment
    // syntax: { CALL procedure_name(?, ?) }
    @Select("{CALL SP_Process_Order_Payment(" +
            "#{orderId, mode=IN, jdbcType=INTEGER}, " +
            "#{result, mode=OUT, jdbcType=VARCHAR}" +
            ")}")
    @Options(statementType = StatementType.CALLABLE)
    void callPaymentProcedure(Map<String, Object> params);

    // 4. 查历史订单
    @Select("SELECT * FROM V_CUSTOMER_ORDER_DETAILS WHERE CustomerID = #{customerId} ORDER BY Order_Date DESC")
    List<Map<String, Object>> findHistory(Integer customerId);

    // 5. 管理员发货
    @Update("UPDATE T_ORDERS SET Status = 'Shipped' WHERE OrderID = #{orderId}")
    void shipOrder(Integer orderId);

    // 6. 管理员查所有订单 (关联客户名)
    @Select("SELECT o.OrderID as id, c.Name as customerName, o.Order_Date as date, " +
            "o.Total_Amount as amount, o.Status as status " +
            "FROM T_ORDERS o " +
            "JOIN T_CUSTOMERS c ON o.CustomerID = c.CustomerID " +
            "ORDER BY o.Order_Date DESC")
    List<Map<String, Object>> findAllOrders();
}*/
package com.example.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    // 1. 创建订单主表 (参数是 Map，不需要 @Param)
    @Insert("INSERT INTO T_ORDERS (CustomerID, Status, Total_Amount) VALUES (#{customerId}, 'Pending', 0)")
    @Options(useGeneratedKeys = true, keyProperty = "orderId", keyColumn = "OrderID")
    void createOrder(Map<String, Object> params);

    // 2. 创建订单明细 (多参数，必须加 @Param ！！！)
    @Insert("INSERT INTO T_ORDER_DETAILS (OrderID, ISBN, Quantity) VALUES (#{orderId}, #{isbn}, #{quantity})")
    void createOrderDetail(@Param("orderId") Integer orderId,
                           @Param("isbn") String isbn,
                           @Param("quantity") Integer quantity);

    // 3. 调用存储过程 (参数是 Map，不需要 @Param)
    @Select("{CALL SP_Process_Order_Payment(" +
            "#{orderId, mode=IN, jdbcType=INTEGER}, " +
            "#{result, mode=OUT, jdbcType=VARCHAR}" +
            ")}")
    @Options(statementType = StatementType.CALLABLE)
    void callPaymentProcedure(Map<String, Object> params);

    // 4. 查历史订单
    @Select("SELECT * FROM V_CUSTOMER_ORDER_DETAILS WHERE CustomerID = #{customerId} ORDER BY Order_Date DESC")
    List<Map<String, Object>> findHistory(Integer customerId);

    // 5. 管理员发货
    @Update("UPDATE T_ORDERS SET Status = 'Shipped' WHERE OrderID = #{orderId}")
    void shipOrder(Integer orderId);

    // 6. 管理员查所有订单
    @Select("SELECT o.OrderID as id, c.Name as customerName, o.Order_Date as date, " +
            "o.Total_Amount as amount, o.Status as status " +
            "FROM T_ORDERS o " +
            "JOIN T_CUSTOMERS c ON o.CustomerID = c.CustomerID " +
            "ORDER BY o.Order_Date DESC")
    List<Map<String, Object>> findAllOrders();
}