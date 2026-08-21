package com.zxyy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxyy.mapper.CategoryMapper;
import com.zxyy.pojo.entity.Category;
import com.zxyy.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class ICategoryService extends ServiceImpl<CategoryMapper,Category> implements CategoryService {
}
