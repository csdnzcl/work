package com.example.mapper;

import com.example.pojo.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SupplierMapper {
    @Select("SELECT * FROM T_SUPPLIERS")
    List<Supplier> findAll();
}