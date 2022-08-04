package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
import com.aiurt.modules.stock.service.IStockSubmitMaterialsService;
import com.aiurt.modules.stock.service.IStockSubmitMaterialsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * @Description: 提报物资
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库管理-提报计划-提报物资")
@RestController
@RequestMapping("/stock/stockSubmitMaterials")
public class StockSubmitMaterialsController {

    @Autowired
    private IStockSubmitMaterialsService iStockSubmitMaterialsService;

    /**
     * 分页列表查询
     *
     * @param stockSubmitMaterials
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库管理-提报计划-提报物资-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/secondLevelWarehouse/EscalationPlanList")
    @ApiOperation(value = "二级库管理-提报计划-提报物资-分页列表查询", notes = "二级库管理-提报计划-提报物资-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockSubmitMaterials>> queryPageList(StockSubmitMaterials stockSubmitMaterials,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Page<StockSubmitMaterials> page = new Page<StockSubmitMaterials>(pageNo, pageSize);
        IPage<StockSubmitMaterials> pageList = iStockSubmitMaterialsService.pageList(page,stockSubmitMaterials);
        return Result.OK(pageList);
    }
}
