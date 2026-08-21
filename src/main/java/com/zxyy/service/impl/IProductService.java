package com.zxyy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxyy.mapper.ProductMapper;
import com.zxyy.pojo.entity.Product;
import com.zxyy.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class IProductService extends ServiceImpl<ProductMapper, Product> implements ProductService {
}
