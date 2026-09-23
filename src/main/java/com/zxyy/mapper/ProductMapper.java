package com.zxyy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxyy.pojo.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE product SET available_stock = available_stock - #{num} " +
            "WHERE id = #{id} AND available_stock >= #{num}")
    int deductStock(@Param("id") Long productId,@Param("num") int num);

    @Update("UPDATE product SET available_stock = available_stock + #{num} " +
            "WHERE id = #{id}")
    int addStock(@Param("id") Long productId, @Param("num") int size);

    @Update("UPDATE product SET available_stock = available_stock - #{num} " +
            "WHERE product_no = #{no} AND available_stock >= #{num}")
    int deductStockByNo(@Param("no") String productNo, @Param("num")Integer quantity);
}
