package com.zxyy.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.zxyy.pojo.dto.InvalidStockDTO;
import com.zxyy.pojo.dto.InvalidStocksDTO;
import com.zxyy.pojo.dto.VirtualGoodItemDTO;
import com.zxyy.pojo.dto.VirtualGoodItemPageQueryDTO;
import com.zxyy.pojo.entity.VirtualGoodItem;
import com.zxyy.result.PageResult;
import com.zxyy.result.Result;
import com.zxyy.service.impl.VirtualGoodItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/virtualGoodsItem")
@Slf4j
@Tag(name = "管理端虚拟商品接口")
public class VirtualGoodsItemController {
    @Autowired
    private VirtualGoodItemService virtualGoodItemService;

    //分页查询指定商品的库存信息
    @GetMapping("/page")
    @Operation(summary = "分页查询库存信息")
    public Result<PageResult> page(VirtualGoodItemPageQueryDTO virtualGoodItemPageQueryDTO){
        log.info("分页查询库存信息:{}",virtualGoodItemPageQueryDTO);
        PageResult pageResult = virtualGoodItemService.pageQuery(virtualGoodItemPageQueryDTO);
        return Result.success(pageResult);
    }


    //增加商品库存
    @PostMapping
    @Operation(summary = "新增商品库存")
    public Result<String> addVirtualGoodItems(@RequestBody VirtualGoodItemDTO virtualGoodsItemDTO){
        log.info("新增商品库存:{}",virtualGoodsItemDTO);
        virtualGoodItemService.addVirtualGoodItems(virtualGoodsItemDTO);
        return Result.success();
    }

    @PutMapping("/invalid")
    @Operation(summary = "库存禁用")
    public Result<String> setInvalidStock(@RequestBody InvalidStockDTO invalidStockDTO){
        log.info("禁用库存:{}",invalidStockDTO);
        VirtualGoodItem virtualGoodItem = BeanUtil.copyProperties(invalidStockDTO, VirtualGoodItem.class);
        virtualGoodItemService.updateByInventoryNo(virtualGoodItem);
        return Result.success();
    }

    @PutMapping("/invalidList")
    @Operation(summary = "批量禁用库存")
    public Result<String> setInvalidStocks(@RequestBody InvalidStocksDTO invalidStocksDTO){
        log.info("批量禁用库存:{}",invalidStocksDTO);
        virtualGoodItemService.setInvalidStocks(invalidStocksDTO);
        return Result.success();
    }
}
