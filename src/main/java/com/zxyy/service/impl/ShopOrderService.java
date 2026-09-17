package com.zxyy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxyy.mapper.ShopOrderMapper;
import com.zxyy.pojo.dto.OrderSubmitDTO;
import com.zxyy.pojo.entity.ShopOrder;
import com.zxyy.pojo.vo.OrderSubmitVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopOrderService extends ServiceImpl<ShopOrderMapper, ShopOrder> {
    /**
     * 创建订单
     *
     * @param orderSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO) {
        String clientRequestNo = orderSubmitDTO.getClientRequestNo();
        //校验全局唯一的下单幂等号 防止重复下单
        ShopOrder shopOrder = query().eq("client_request_no", clientRequestNo).one();
        if(shopOrder != null){
            //如果重复下单 返回订单
            OrderSubmitVO orderSubmitVO = BeanUtil.copyProperties(shopOrder, OrderSubmitVO.class);

        }
        //规范化邮箱计算邮箱的HMAC指纹并加密 有相同的直接返回已存在的订单

        //查询商品是否上架 是否逻辑删除 以及购买数量是否满足限购要求

        //使用数据库中的商品价格计算订单金额

        //2.创建订单主表  TODO 什么时候开启定时任务

        //3.创建订单详细表 写入商品名称 编号 图片发货类型和快照

        //对每张订单明细表使用 for update skip locked 选出足量的可用库存

        //将库存实际改为reserved 只写入orderitemid 预占过期和订单统一处理

        //原子减少商品库存 不增加soldCount

        return null;
    }
}
