package com.zxyy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxyy.mapper.ProductMapper;
import com.zxyy.pojo.dto.ProductPageQueryDTO;
import com.zxyy.pojo.entity.Product;
import com.zxyy.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product>{

}
