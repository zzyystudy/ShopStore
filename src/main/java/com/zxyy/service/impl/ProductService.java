package com.zxyy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxyy.constant.MessageConstant;
import com.zxyy.exception.CategoryException;
import com.zxyy.exception.ProductLogicStillAlive;
import com.zxyy.mapper.CategoryMapper;
import com.zxyy.mapper.ProductMapper;
import com.zxyy.pojo.dto.ProductDTO;
import com.zxyy.pojo.dto.ProductPageQueryDTO;
import com.zxyy.pojo.entity.Category;
import com.zxyy.pojo.entity.Product;
import com.zxyy.result.PageResult;
import com.zxyy.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product>{
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据商品编号彻底删除商品
     * @param productNo
     */
    public void delete(String productNo) {
        Product product = query().eq("product_no", productNo).one();
        //判断是否已经逻辑删除
        if(product.getIsDeleted().equals(1)){
            throw new ProductLogicStillAlive(MessageConstant.PRODUCT_LOGIC_ALIVE);
        }
        removeById(product.getId());
    }

    public void saveProduct(ProductDTO productDTO) {
        //判断category是否存在 这个前端校验一下按理说就可以了 TODO后端有没有校验的必要
        Category category = categoryMapper.selectById(productDTO.getCategoryId());
        if(category == null){
            throw new CategoryException(MessageConstant.CATEGORY_NOT_EXIT);
        }
        Product product = BeanUtil.copyProperties(productDTO, Product.class);
        product.setCreateUser(BaseContext.getCurrentId());
        product.setUpdateUser(BaseContext.getCurrentId());
        save(product);
    }
}
