package com.example.service;

import com.example.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public List<Map<String, Object>> getCustomerHistory(Integer customerId) {
        return orderMapper.findHistory(customerId);
    }

    /**
     * 核心下单逻辑 (用户端)
     * 1. 插入主表
     * 2. 插入明细
     * 3. 调用存储过程结算
     */
    @Transactional
    public String processOrder(Integer customerId, String isbn, Integer quantity) {
        // 1. 插入订单主表
        Map<String, Object> orderParams = new HashMap<>();
        orderParams.put("customerId", customerId);
        orderMapper.createOrder(orderParams);

        // =======================================================
        // 🔴 修复点在这里：安全获取 MyBatis 回填的主键 ID
        // =======================================================
        // 数据库驱动返回的可能是 BigInteger, Long 或 Integer，不能直接强转
        Object idObj = orderParams.get("orderId");
        Integer newOrderId;

        if (idObj instanceof Number) {
            // Number 是 Integer, Long, BigInteger 的父类，可以直接转 int
            newOrderId = ((Number) idObj).intValue();
        } else {
            // 防御性代码，防止获取失败
            throw new RuntimeException("获取订单ID失败，返回值类型异常: " + (idObj == null ? "null" : idObj.getClass().getName()));
        }

        // 2. 插入订单明细
        orderMapper.createOrderDetail(newOrderId, isbn, quantity);

        // 3. 调用存储过程 SP_Process_Order_Payment
        Map<String, Object> procParams = new HashMap<>();
        procParams.put("orderId", newOrderId);
        procParams.put("result", null); // OUT 参数占位符

        orderMapper.callPaymentProcedure(procParams);

        // 4. 获取并返回存储过程的输出结果
        return (String) procParams.get("result");
    }

    /**
     * 查询用户历史订单
     */
    public List<Map<String, Object>> getHistory(Integer customerId) {
        return orderMapper.findHistory(customerId);
    }

    /**
     * 管理员发货
     */
    public void shipOrder(Integer orderId) {
        orderMapper.shipOrder(orderId);
    }

    /**
     * 管理员获取所有订单
     */
    public List<Map<String, Object>> getAllOrders() {
        return orderMapper.findAllOrders();
    }

    /**
     * 【新增】重新支付 (Retry Payment)
     * 逻辑：直接再次调用 SP_Process_Order_Payment 存储过程
     * 因为存储过程内部会再次检查余额、扣库存。如果这次余额够了，就会变 Success。
     */
    @Transactional
    public String retryOrder(Integer orderId) {
        try {
            Map<String, Object> procParams = new HashMap<>();
            procParams.put("orderId", orderId);
            procParams.put("result", null);

            // 复用 Mapper 里已有的调用存储过程方法
            orderMapper.callPaymentProcedure(procParams);

            return (String) procParams.get("result");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("重试支付异常: " + e.getMessage());
        }
    }

    /**
     * 【新增】删除/取消订单
     * 逻辑：先删明细，再删主表
     */
    @Transactional
    public void deleteOrder(Integer orderId) {
        orderMapper.deleteOrderDetails(orderId);
        orderMapper.deleteOrder(orderId);
    }
}