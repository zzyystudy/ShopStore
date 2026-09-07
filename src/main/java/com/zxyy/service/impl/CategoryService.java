package com.zxyy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxyy.constant.MessageConstant;
import com.zxyy.exception.ProductLogicStillAlive;
import com.zxyy.mapper.CategoryMapper;
import com.zxyy.pojo.dto.CategoryDTO;
import com.zxyy.pojo.entity.Category;
import com.zxyy.pojo.entity.Product;
import com.zxyy.util.BaseContext;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper,Category>{
    public void saveCategory(CategoryDTO categoryDTO) {
        Category category = BeanUtil.copyProperties(categoryDTO, Category.class);
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        save(category);
    }

    public void delete(Long id) {
        Category category= query().eq("id", id).one();
        //判断是否已经逻辑删除
        if(category.getIsDeleted().equals(1)){
            throw new ProductLogicStillAlive(MessageConstant.CATEGORY_LOGIC_ALIVE);
        }
        removeById(category.getId());

    }
}
