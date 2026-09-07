package com.zxyy.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxyy.pojo.dto.CategoryDTO;
import com.zxyy.pojo.dto.CategoryPageQueryDTO;
import com.zxyy.pojo.entity.Category;
import com.zxyy.result.PageResult;
import com.zxyy.result.Result;
import com.zxyy.service.impl.CategoryService;
import com.zxyy.service.impl.CategoryService;
import com.zxyy.util.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
@Tag(name = "管理端分类接口")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //查看所有商品 分页查询 根据条件
    @GetMapping("/page")
    @Operation(summary = "分页查询接口")
    public Result<PageResult> page(CategoryPageQueryDTO CategoryPageQueryDTO){
        log.info("分页查询:{}",CategoryPageQueryDTO);
        Page<Category> page = categoryService.lambdaQuery()
                .like(StringUtils.hasText(CategoryPageQueryDTO.getName()), Category::getName, CategoryPageQueryDTO.getName())
                //区间查询
                .ge(CategoryPageQueryDTO.getStartTime() != null, Category::getCreateTime, CategoryPageQueryDTO.getStartTime())
                .le(CategoryPageQueryDTO.getEndTime() != null, Category::getCreateTime, CategoryPageQueryDTO.getEndTime())
                .eq(CategoryPageQueryDTO.getStatus() != null, Category::getStatus, CategoryPageQueryDTO.getStatus())
                //排序字段
                .orderBy(true, CategoryPageQueryDTO.isAsc(), getSortField(CategoryPageQueryDTO.getSortField()))
                .page(new Page<>(CategoryPageQueryDTO.getPage(), CategoryPageQueryDTO.getPageSize()));
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getRecords());
        return Result.success(pageResult);
    }

    private SFunction<Category, ?> getSortField(String sortField) {
        if ("createTime".equals(sortField)) {
            return Category::getCreateTime;  // 按创建时间排序
        } else {
            return Category::getId;          // 默认按 ID 排序
        }
    }
    //新增商品
    @PostMapping
    @Operation(summary = "新增分类接口")
    public Result<String> save(@RequestBody CategoryDTO CategoryDTO){
        log.info("新增商品:{}",CategoryDTO);
        categoryService.saveCategory(CategoryDTO);
        return Result.success();
    }
    //修改商品 编号 图片等等  逻辑删除！
    @PutMapping
    @Operation(summary = "修改分类接口")
    public Result<String> update(@RequestBody CategoryDTO CategoryDTO){
        log.info("修改商品:{}",CategoryDTO);
        Category Category = BeanUtil.copyProperties(CategoryDTO, Category.class);
        Category.setUpdateUser(BaseContext.getCurrentId());
        //管理端应该可以获取到商品的id 用户端我们使用
        UpdateWrapper<Category> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",CategoryDTO.getId());
        categoryService.update(Category,updateWrapper);
        return Result.success();
    }
    //彻底删除接口
    @DeleteMapping
    @Operation(summary = "删除分类")
    public Result<String> delete(Long id){
        log.info("删除商品编号:{}",id);
        categoryService.delete(id);
        return Result.success();
    }
}
