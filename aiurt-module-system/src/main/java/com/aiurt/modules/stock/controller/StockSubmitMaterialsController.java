package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
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
@Api(tags = "提报物资")
@RestController
@RequestMapping("/stock/stockSubmitMaterials")
public class StockSubmitMaterialsController {

    @Autowired
    private IStockSubmitMaterialsService iStockSubmitMaterialsService;
    @Autowired
    private IStockSubmitMaterialsService stockSubmitMaterialsService;

    /**
     * 分页列表查询
     *
     * @param stockSubmitMaterials
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "提报物资-分页列表查询")
    @ApiOperation(value = "提报物资-分页列表查询", notes = "提报物资-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockSubmitMaterials>> queryPageList(StockSubmitMaterials stockSubmitMaterials,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockSubmitMaterials>> result = new Result<IPage<StockSubmitMaterials>>();
        QueryWrapper<StockSubmitMaterials> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        String submitPlanCode = stockSubmitMaterials==null?"":stockSubmitMaterials.getSubmitPlanCode();
        if(submitPlanCode != null && !"".equals(submitPlanCode)){
            queryWrapper.eq("submit_plan_code",submitPlanCode);
        }
        queryWrapper.orderByDesc("create_time");
        Page<StockSubmitMaterials> page = new Page<StockSubmitMaterials>(pageNo, pageSize);
        IPage<StockSubmitMaterials> pageList = iStockSubmitMaterialsService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
}
