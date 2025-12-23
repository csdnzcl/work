package com.example.mapper;

import com.example.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {
    // 管理员登录查询
    @Select("SELECT * FROM T_ADMINS WHERE Username = #{username} AND Password = #{password}")
    Admin login(@Param("username") String username, @Param("password") String password);
}