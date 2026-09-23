package com.zxyy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxyy.constant.MessageConstant;
import com.zxyy.exception.BizException;
import com.zxyy.exception.ProductNotExit;
import com.zxyy.exception.VirtualGoodItemRecur;
import com.zxyy.mapper.ProductMapper;
import com.zxyy.mapper.VirtualGoodItemMapper;
import com.zxyy.pojo.dto.InvalidStocksDTO;
import com.zxyy.pojo.dto.VirtualGoodItemDTO;
import com.zxyy.pojo.dto.VirtualGoodItemPageQueryDTO;
import com.zxyy.pojo.entity.Product;
import com.zxyy.pojo.entity.VirtualGoodItem;
import com.zxyy.result.PageResult;
import com.zxyy.util.AESGCMUtil;
import com.zxyy.util.BaseContext;
import com.zxyy.util.HmacSha256Util;
import com.zxyy.util.ULIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class VirtualGoodItemService extends ServiceImpl<VirtualGoodItemMapper, VirtualGoodItem> {

    @Autowired
    private AESGCMUtil aesgcmUtil;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 新增库存 设计多表操作乐观锁增加库存
     * 需要加上事务注解
     *
     * @param virtualGoodsItemDTO
     */
    @Transactional
    public void addVirtualGoodItems(VirtualGoodItemDTO virtualGoodsItemDTO) {
        Long productId = virtualGoodsItemDTO.getProductId();
        List<String> goods = virtualGoodsItemDTO.getGoods();
        List<VirtualGoodItem> virtualGoodItems = new ArrayList<>();
        for(String good : goods){
            VirtualGoodItem virtualGoodItem = new VirtualGoodItem();
            //设置商品id
            virtualGoodItem.setProductId(productId);
            //生成并设置ulid
            String ulid = ULIDUtil.generateULID();
            String inventoryNo = "V-" + ulid;
            virtualGoodItem.setInventoryNo(inventoryNo);
            //生成nonce aes加密商品数据
            AESGCMUtil.EncryptedPayload encryptedPayload = aesgcmUtil.encrypt(good, inventoryNo);
            virtualGoodItem.setContentCiphertext(encryptedPayload.ciphertext());
            virtualGoodItem.setEncryptionNonce(encryptedPayload.nonce());
            //aes密钥版本  TODO 这里应该是一个配置 不应该硬编码
            virtualGoodItem.setEncryptionKeyVersion("2026");
            //生成加密数据的fingerprint
            String fingerprint = HmacSha256Util.sign(good);
            virtualGoodItem.setContentFingerprint(fingerprint);
            //审计字段赋值
            virtualGoodItem.setCreateUser(BaseContext.getCurrentId());
            virtualGoodItem.setUpdateUser(BaseContext.getCurrentId());
            //加入列表
            virtualGoodItems.add(virtualGoodItem);
        }
        //根据指纹查询判重
        List<String> fingerprints = virtualGoodItems.stream().map(virtualGoodItem -> {
            return virtualGoodItem.getContentFingerprint();
        }).toList();
        List<VirtualGoodItem> contentFingerprint = query().in("content_fingerprint", fingerprints).list();
        if(!contentFingerprint.isEmpty()){
            //不为空说说明重复了
            throw new VirtualGoodItemRecur(MessageConstant.ITEM_RECUR);
        }
        //不为空添加到数据库中
        saveBatch(virtualGoodItems);
        //增加库存这里不需要cas
        productMapper.addStock(productId,virtualGoodItems.size());
    }

    //根据商品编号修改商品信息
    public void updateByInventoryNo(VirtualGoodItem virtualGoodItem) {
        LambdaQueryWrapper<VirtualGoodItem> wrapper = new LambdaQueryWrapper<VirtualGoodItem>().eq(VirtualGoodItem::getInventoryNo, virtualGoodItem.getInventoryNo());
        update(virtualGoodItem,wrapper);
    }

    @Transactional
    public void setInvalidStocks(InvalidStocksDTO invalidStocksDTO) {
        //没有选择的情况前端处理
        LambdaUpdateWrapper<VirtualGoodItem> wrapper = new LambdaUpdateWrapper<VirtualGoodItem>()
                .in(VirtualGoodItem::getInventoryNo, invalidStocksDTO.getProductNos())
                .set(VirtualGoodItem::getStatus, invalidStocksDTO.getStatus())
                .set(VirtualGoodItem::getInvalidReason, invalidStocksDTO.getInvalidReason());
        update(wrapper);
        //扣减库存
        VirtualGoodItem virtualGoodItem = query()
                .eq("inventory_no", invalidStocksDTO.getProductNos().get(0))
                .one();
        int rows = productMapper.deductStock(virtualGoodItem.getProductId(),invalidStocksDTO.getProductNos().size());
        if(rows == 0){
            //如果更新失败 并发问题直接抛出异常
            throw new BizException(MessageConstant.OUT_OF_STOCK);
        }
    }

    public PageResult pageQuery(VirtualGoodItemPageQueryDTO virtualGoodItemPageQueryDTO) {
        //必须传递一个productNo
        String productNo = virtualGoodItemPageQueryDTO.getProductNo();
        if("".equals(productNo) || productNo == null){
            //如果没有编号 直接返回空 TODO 这里是抛出异常还是返回空 规范问题我很迷茫 不知道怎么参考
            return null;
        }
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getProductNo, productNo);
        Product product = productMapper.selectOne(wrapper);
        //TODO 这里再加一个检查到底必要不必要
        if(product == null){
            //没有找到商品抛异常 商品不存在
            throw new ProductNotExit(MessageConstant.PRODUCT_NOT_EXIT);
        }
        Page<VirtualGoodItem> page = lambdaQuery()
                .eq(product.getId() != null, VirtualGoodItem::getProductId, product.getId())
                .eq(virtualGoodItemPageQueryDTO.getStatus() != null,VirtualGoodItem::getStatus,virtualGoodItemPageQueryDTO.getStatus())
                .page(new Page<>(virtualGoodItemPageQueryDTO.getPage(), virtualGoodItemPageQueryDTO.getPageSize()));
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getRecords());
        //TODO 这里也是将所有的信息返回了
        return pageResult;
    }

    //禁用虚拟商品 扣减库存 乐观锁
    @Transactional
    public void setInvalidStock(VirtualGoodItem virtualGoodItem) {
        //先禁用商品 再扣减库存 TODO 扣减库存
        updateByInventoryNo(virtualGoodItem);
        //先查然后直接扣减库存 这里使用不连表查询
        VirtualGoodItem virtualGoodItem1 = query().eq("inventory_no", virtualGoodItem.getInventoryNo()).one();
        int rows = productMapper.deductStock(virtualGoodItem1.getProductId(),1);
        if(rows == 0){
            //如果更新失败 并发问题直接抛出异常
            throw new BizException(MessageConstant.OUT_OF_STOCK);
        }
    }
}
