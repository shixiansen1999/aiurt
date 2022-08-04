package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.stock.entity.StockOutboundMaterials;
import com.aiurt.modules.stock.service.IStockOutboundMaterialsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 二级库管理-二级库出库管理-出库物资
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库管理-二级库出库管理-出库物资")
@RestController
@RequestMapping("/stock/stockOutboundMaterials")
public class StockOutboundMaterialsController {

    @Autowired
    private IStockOutboundMaterialsService iStockOutboundMaterialsService;

    /**
     * 详情
     * @param id
     * @return
     */
    @AutoLog(value = "二级库管理-二级库出库管理-出库物资-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/StockLevel2SecondaryList")
    @ApiOperation(value = "详情查询", notes = "详情查询")
    @GetMapping(value = "/queryById")
    public Result<StockOutboundMaterials> queryById(@RequestParam(name = "id", required = true) String id) {
        StockOutboundMaterials stockOutboundMaterials = iStockOutboundMaterialsService.getById(id);
        stockOutboundMaterials = iStockOutboundMaterialsService.translate(stockOutboundMaterials);
        return Result.ok(stockOutboundMaterials);
    }
}
