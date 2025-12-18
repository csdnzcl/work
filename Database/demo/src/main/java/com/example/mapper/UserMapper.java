package com.example.mapper;

import com.example.pojo.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 别忘了导包
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    // 多参数，必须加 @Param
    @Select("SELECT * FROM T_CUSTOMERS WHERE Name = #{username} AND Password = #{password}")
    Customer login(@Param("username") String username, @Param("password") String password);
}