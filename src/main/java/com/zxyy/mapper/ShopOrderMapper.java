package com.zxyy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxyy.pojo.entity.ShopOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ShopOrderMapper extends BaseMapper<ShopOrder> {

    @Update("UPDATE shop_order SET order_status = 40, closed_time = NOW(),close_reason = '超时未支付' " +
            "WHERE id = #{id} AND order_status = 10")
    int closeById(@Param("id") Long orderId);

    @Update("UPDATE shop_order SET order_status = 30, paid_time = NOW() " +
            "WHERE order_no = #{orderNo} AND order_status = 10")
    int markPaid(@Param("orderNo") String orderNo);
}
