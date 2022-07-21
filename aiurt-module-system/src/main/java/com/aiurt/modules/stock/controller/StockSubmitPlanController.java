package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.stock.entity.StockSubmitMaterials;
import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.aiurt.modules.stock.service.IStockSubmitMaterialsService;
import com.aiurt.modules.stock.service.IStockSubmitPlanService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.Date;
import java.util.List;

/**
 * @Description: 物资提报计划
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "物资提报计划")
@RestController
@RequestMapping("/stock/stockSubmitPlan")
public class StockSubmitPlanController {

    @Autowired
    private IStockSubmitPlanService iStockSubmitPlanService;
    @Autowired
    private IStockSubmitMaterialsService stockSubmitMaterialsService;

    /**
     * 分页列表查询
     *
     * @param stockSubmitPlan
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "物资提报计划-分页列表查询")
    @ApiOperation(value = "物资提报计划-分页列表查询", notes = "物资提报计划-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockSubmitPlan>> queryPageList(StockSubmitPlan stockSubmitPlan,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockSubmitPlan>> result = new Result<IPage<StockSubmitPlan>>();
        QueryWrapper<StockSubmitPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        String code = stockSubmitPlan.getCode();
        queryWrapper.orderByDesc("create_time");
        Page<StockSubmitPlan> page = new Page<StockSubmitPlan>(pageNo, pageSize);
        IPage<StockSubmitPlan> pageList = iStockSubmitPlanService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "物资提报计划-添加")
    @ApiOperation(value = "物资提报计划-添加", notes = "物资提报计划-添加")
    @PostMapping(value = "/add")
    public Result<StockSubmitPlan> add(@RequestBody StockSubmitPlan stockSubmitPlan) {
        Result<StockSubmitPlan> result = new Result<StockSubmitPlan>();
        try {
            iStockSubmitPlanService.add(stockSubmitPlan);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 修改提报计划状态
     * @param
     * @return
     */
    @ApiOperation(value = "修改提报计划状态", notes = "修改提报计划状态")
    @GetMapping(value = "/submitPlanStatus")
    public Result<String> submitPlan(@RequestParam(name = "status", required = true) Integer status,
                                     @RequestParam(name = "code", required = true) String code) {
        StockSubmitPlan stockSubmitPlan = iStockSubmitPlanService.getOne(new QueryWrapper<StockSubmitPlan>().eq("code",code));
        stockSubmitPlan.setStatus(status);
        boolean ok = iStockSubmitPlanService.updateById(stockSubmitPlan);
        if (ok) {
            return Result.ok("操作成功！");
        }else{
            return Result.ok("操作失败！");
        }
    }

    /**
     * 新增获取提报计划编号
     * @param
     * @return
     */
    @ApiOperation(value = "新增获取提报计划编号", notes = "新增获取提报计划编号")
    @GetMapping(value = "/getSubmitPlanCode")
    public Result<StockSubmitPlan> getSubmitPlanCode() {
        return Result.ok(iStockSubmitPlanService.getSubmitPlanCode());
    }

    /**
     * 物资提报计划详情查询
     * @param id
     * @return
     */
    @ApiOperation(value = "物资提报计划详情查询", notes = "物资提报计划详情查询")
    @GetMapping(value = "/queryById")
    public Result<StockSubmitPlan> queryById(@RequestParam(name = "id", required = true) String id) {
        StockSubmitPlan stockSubmitPlan = iStockSubmitPlanService.getById(id);
//        String code = stockSubmitPlan.getCode();
//        QueryWrapper<StockSubmitMaterials> queryWrapper = new QueryWrapper<StockSubmitMaterials>();
//        queryWrapper.eq("submit_plan_code",code).orderByDesc("create_time");
//        List<StockSubmitMaterials> stockSubmitMaterials = stockSubmitMaterialsService.list(queryWrapper);
//        stockSubmitPlan.setStockSubmitMaterialsList(stockSubmitMaterials);
        return Result.ok(stockSubmitPlan);
    }

    @AutoLog(value = "物资提报计划-编辑")
    @ApiOperation(value = "物资提报计划-编辑", notes = "物资提报计划-编辑")
    @PostMapping(value = "/edit")
    public Result<StockSubmitPlan> edit(@RequestBody StockSubmitPlan stockSubmitPlan) {
        Result<StockSubmitPlan> result = new Result<StockSubmitPlan>();
        StockSubmitPlan stockSubmitPlanEntity = iStockSubmitPlanService.getById(stockSubmitPlan.getId());
        if (stockSubmitPlanEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = iStockSubmitPlanService.edit(stockSubmitPlan);
            try{
            }catch (Exception e){
                throw new AiurtBootException(e.getMessage());
            }
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    @AutoLog(value = "物资提报计划-通过id删除")
    @ApiOperation(value = "物资提报计划-通过id删除", notes = "物资提报计划-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            StockSubmitPlan stockSubmitPlan = iStockSubmitPlanService.getById(id);
            iStockSubmitPlanService.removeById(stockSubmitPlan);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog(value = "物资提报计划分类-批量删除")
    @ApiOperation(value = "物资提报计划分类-批量删除", notes = "物资提报计划分类-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            iStockSubmitPlanService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }
}
