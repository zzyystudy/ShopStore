package com.zxyy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxyy.pojo.entity.VirtualGoodItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VirtualGoodItemMapper extends BaseMapper<VirtualGoodItem> {


    @Select("""
        SELECT *
        FROM virtual_goods_item
        WHERE product_id = #{productId}
          AND status = 0
        ORDER BY id
        LIMIT #{quantity}
        FOR UPDATE SKIP LOCKED
        """)
    List<VirtualGoodItem> selectAvailableForUpdate(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity);
}
