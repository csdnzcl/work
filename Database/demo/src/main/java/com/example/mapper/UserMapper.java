package com.example.mapper;

import com.example.pojo.Customer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    // 1. 登录查询 (保持不变)
    @Select("SELECT * FROM T_CUSTOMERS WHERE Name = #{username} AND Password = #{password}")
    Customer login(@Param("username") String username, @Param("password") String password);

    // 2. 根据ID查用户 (保持不变，用于刷新余额)
    @Select("SELECT * FROM T_CUSTOMERS WHERE CustomerID = #{id}")
    Customer findById(Integer id);

    // 3. 【新增】注册新用户
    // 默认余额 0，默认等级 1，默认累计消费 0
    @Insert("INSERT INTO T_CUSTOMERS (Name, Password, Address, Balance, Credit_Level, Total_Spent) " +
            "VALUES (#{name}, #{password}, #{address}, 0.00, 1, 0.00)")
    void register(Customer customer);

    // 4. 【新增】检查用户名是否已存在 (防止重复注册)
    @Select("SELECT COUNT(*) FROM T_CUSTOMERS WHERE Name = #{username}")
    int countByUsername(String username);
}