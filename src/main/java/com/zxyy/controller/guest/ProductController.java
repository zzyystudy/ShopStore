package com.zxyy.controller.guest;

import cn.hutool.core.bean.BeanUtil;
import com.zxyy.pojo.entity.Product;
import com.zxyy.pojo.vo.ProductVO;
import com.zxyy.result.Result;
import com.zxyy.service.impl.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController("guestProductController")
@RequestMapping("/guest/product")
@Tag(name = "客户端商品接口")
@Slf4j
public class ProductController {
    @Autowired
    private ProductService productService;
    //查看指定分类的商品
    @GetMapping
    @Operation(summary = "获取指定分类的商品信息")
    public Result<List<ProductVO>> getProductByCategoryId(Long id){
        log.info("获取分类id为:{}的商品信息",id);
        List<Product> products = productService.query().eq("category_id", id)
                .eq("is_deleted",1).list();
        List<ProductVO> productVOs = products.stream()
                .map(product -> {
                    ProductVO productVO = new ProductVO();
                    BeanUtil.copyProperties(product, productVO);
                    return productVO;
                }).collect(Collectors.toList());
        return Result.success(productVOs);
    }
}
