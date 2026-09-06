package com.zxyy.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxyy.pojo.dto.ProductDTO;
import com.zxyy.pojo.dto.ProductPageQueryDTO;
import com.zxyy.pojo.entity.Product;
import com.zxyy.result.PageResult;
import com.zxyy.result.Result;
import com.zxyy.service.impl.ProductService;
import com.zxyy.util.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
@Tag(name = "管理端商品接口")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    //查看所有商品 分页查询 根据条件
    @GetMapping("/page")
    @Operation(summary = "分页查询接口")
    public Result<PageResult> page(ProductPageQueryDTO productPageQueryDTO){
        log.info("分页查询:{}",productPageQueryDTO);
        Page<Product> page = productService.lambdaQuery()
                .like(StringUtils.hasText(productPageQueryDTO.getName()), Product::getName, productPageQueryDTO.getName())
                //区间查询
                .ge(productPageQueryDTO.getStartTime() != null, Product::getCreateTime, productPageQueryDTO.getStartTime())
                .le(productPageQueryDTO.getEndTime() != null, Product::getCreateTime, productPageQueryDTO.getEndTime())
                .ge(productPageQueryDTO.getStartPrice() != null, Product::getPrice, productPageQueryDTO.getStartPrice())
                .le(productPageQueryDTO.getEndPrice() != null, Product::getPrice, productPageQueryDTO.getEndPrice())
                .eq(productPageQueryDTO.getStatus() != null, Product::getStatus, productPageQueryDTO.getStatus())
                //排序字段
                .orderBy(true, productPageQueryDTO.isAsc(), getSortField(productPageQueryDTO.getSortField()))
                .page(new Page<>(productPageQueryDTO.getPage(), productPageQueryDTO.getPageSize()));
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getRecords());
        return Result.success(pageResult);
    }

    private SFunction<Product, ?> getSortField(String sortField) {
        if ("price".equals(sortField)) {
            return Product::getPrice;      // 按价格排序
        } else if ("createTime".equals(sortField)) {
            return Product::getCreateTime;  // 按创建时间排序
        } else if ("soldCount".equals(sortField)) {
            return Product::getSoldCount;       // 按销量排序
        } else {
            return Product::getId;          // 默认按 ID 排序
        }
    }
    //新增商品
    @PostMapping
    @Operation(summary = "新增商品接口")
    public Result<String> save(@RequestBody  ProductDTO productDTO){
        log.info("新增商品:{}",productDTO);
        Product product = BeanUtil.copyProperties(productDTO, Product.class);
        product.setCreateUser(BaseContext.getCurrentId());
        product.setUpdateUser(BaseContext.getCurrentId());
        productService.save(product);
        return Result.success();
    }
    //修改商品 编号 图片等等  逻辑删除！
    @PutMapping
    @Operation(summary = "修改商品接口")
    public Result<String> update(@RequestBody ProductDTO productDTO){
        log.info("修改商品:{}",productDTO);
        Product product = BeanUtil.copyProperties(productDTO, Product.class);
        product.setUpdateUser(BaseContext.getCurrentId());
        //管理端应该可以获取到商品的id 用户端我们使用
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("product_no",productDTO.getProductNo());
        productService.update(product,updateWrapper);
        return Result.success();
    }
    //彻底删除接口


}
