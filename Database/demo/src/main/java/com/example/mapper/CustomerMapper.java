package com.example.mapper;

import com.example.pojo.Customer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface CustomerMapper {

    // 登录查询
    @Select("SELECT * FROM T_CUSTOMERS WHERE Name = #{username} AND Password = #{password}")
    Customer login(String username, String password);

    // 根据ID刷新用户信息 (查余额、等级)
    @Select("SELECT * FROM T_CUSTOMERS WHERE CustomerID = #{id}")
    Customer findById(Integer id);

    // 注册新用户
    @Insert("INSERT INTO T_CUSTOMERS (Name, Password, Address, Balance, Credit_Level) VALUES (#{name}, #{password}, #{address}, 1000.00, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "customerId", keyColumn = "CustomerID")
    void register(Customer customer);
}