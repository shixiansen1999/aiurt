package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.service.IStockIncomingMaterialsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * @Description: 提报物资
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "提报物资")
@RestController
@RequestMapping("/stock/stockIncomingMaterials")
public class StockIncomingMaterialsController {

    @Autowired
    private IStockIncomingMaterialsService iStockIncomingMaterialsService;
    @Autowired
    private IStockIncomingMaterialsService stockIncomingMaterialsService;

    /**
     * 分页列表查询
     *
     * @param stockIncomingMaterials
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "提报物资-分页列表查询")
    @ApiOperation(value = "提报物资-分页列表查询", notes = "提报物资-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockIncomingMaterials>> queryPageList(StockIncomingMaterials stockIncomingMaterials,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockIncomingMaterials>> result = new Result<IPage<StockIncomingMaterials>>();
        QueryWrapper<StockIncomingMaterials> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        String inOrderCode = stockIncomingMaterials==null?"":stockIncomingMaterials.getInOrderCode();
        if(inOrderCode != null && !"".equals(inOrderCode)){
            queryWrapper.eq("in_order_code",inOrderCode);
        }
        queryWrapper.orderByDesc("create_time");
        Page<StockIncomingMaterials> page = new Page<StockIncomingMaterials>(pageNo, pageSize);
        IPage<StockIncomingMaterials> pageList = iStockIncomingMaterialsService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
}
