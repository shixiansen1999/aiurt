package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 二级库库存管理
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库库存管理")
@RestController
@RequestMapping("/stock/stockLevel2")
public class StockLevel2Controller {

    @Autowired
    private IStockLevel2Service iStockLevel2Service;

    /**
     * 分页列表查询
     *
     * @param stockLevel2
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库库存管理-分页列表查询")
    @ApiOperation(value = "二级库库存管理-分页列表查询", notes = "二级库库存管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockLevel2>> queryPageList(StockLevel2 stockLevel2,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Page<StockLevel2> page = new Page<StockLevel2>(pageNo, pageSize);
        IPage<StockLevel2> pageList = iStockLevel2Service.pageList(page,stockLevel2);
        return Result.OK(pageList);
    }

    /**
     * 二级库库存管理详情查询
     * @param id
     * @return
     */
    @ApiOperation(value = "二级库库存管理详情查询", notes = "二级库库存管理详情查询")
    @GetMapping(value = "/queryById")
    public Result<StockLevel2> queryById(@RequestParam(name = "id", required = true) String id) {
        StockLevel2 stockLevel2 = iStockLevel2Service.getDetailById(id);
        return Result.ok(stockLevel2);
    }

}
