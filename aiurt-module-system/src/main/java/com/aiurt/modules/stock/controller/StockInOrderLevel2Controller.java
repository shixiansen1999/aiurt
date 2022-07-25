package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.entity.StockInOrderLevel2;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.entity.StockLevel2Info;
import com.aiurt.modules.stock.service.IStockIncomingMaterialsService;
import com.aiurt.modules.stock.service.IStockInOrderLevel2Service;
import com.aiurt.modules.stock.service.IStockLevel2Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 二级库入库管理
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库入库管理")
@RestController
@RequestMapping("/stock/stockInOrderLevel2")
public class StockInOrderLevel2Controller {

    @Autowired
    private IStockInOrderLevel2Service iStockInOrderLevel2Service;

    /**
     * 分页列表查询
     *
     * @param stockInOrderLevel2
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库入库管理-分页列表查询")
    @ApiOperation(value = "二级库入库管理-分页列表查询", notes = "二级库入库管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockInOrderLevel2>> queryPageList(StockInOrderLevel2 stockInOrderLevel2,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockInOrderLevel2>> result = new Result<IPage<StockInOrderLevel2>>();
        QueryWrapper<StockInOrderLevel2> queryWrapper = QueryGenerator.initQueryWrapper(stockInOrderLevel2, req.getParameterMap());
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        if(stockInOrderLevel2.getEntryTimeBegin() != null && stockInOrderLevel2.getEntryTimeEnd() != null ){
            queryWrapper.between("entry_time",stockInOrderLevel2.getEntryTimeBegin(),stockInOrderLevel2.getEntryTimeEnd());
        }
        queryWrapper.orderByDesc("create_time");
        Page<StockInOrderLevel2> page = new Page<StockInOrderLevel2>(pageNo, pageSize);
        IPage<StockInOrderLevel2> pageList = iStockInOrderLevel2Service.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "二级库入库管理-添加")
    @ApiOperation(value = "二级库入库管理-添加", notes = "二级库入库管理-添加")
    @PostMapping(value = "/add")
    public Result<StockInOrderLevel2> add(@RequestBody StockInOrderLevel2 stockInOrderLevel2) {
        Result<StockInOrderLevel2> result = new Result<StockInOrderLevel2>();
        try {
            iStockInOrderLevel2Service.add(stockInOrderLevel2);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 提交
     * @param
     * @return
     */
    @ApiOperation(value = "提交", notes = "提交")
    @GetMapping(value = "/submitPlanStatus")
    public Result<String> submitPlan(@RequestParam(name = "status", required = true) String status,
                                     @RequestParam(name = "code", required = true) String code) {
        boolean ok = iStockInOrderLevel2Service.submitPlan(status, code);
        if (ok) {
            return Result.ok("操作成功！");
        }else{
            return Result.ok("操作失败！");
        }
    }

    /**
     * 新增获取入库编号
     * @param
     * @return
     */
    @ApiOperation(value = "新增获取入库编号", notes = "新增获取入库编号")
    @GetMapping(value = "/getInOrderCode")
    public Result<StockInOrderLevel2> getInOrderCode() throws ParseException {
        return Result.ok(iStockInOrderLevel2Service.getInOrderCode());
    }

    /**
     * 二级库入库管理详情查询
     * @param id
     * @return
     */
    @ApiOperation(value = "二级库入库管理详情查询", notes = "二级库入库管理详情查询")
    @GetMapping(value = "/queryById")
    public Result<StockInOrderLevel2> queryById(@RequestParam(name = "id", required = true) String id) {
        StockInOrderLevel2 stockInOrderLevel2 = iStockInOrderLevel2Service.getById(id);
        return Result.ok(stockInOrderLevel2);
    }

    @AutoLog(value = "二级库入库管理-编辑")
    @ApiOperation(value = "二级库入库管理-编辑", notes = "二级库入库管理-编辑")
    @PostMapping(value = "/edit")
    public Result<StockInOrderLevel2> edit(@RequestBody StockInOrderLevel2 stockInOrderLevel2) {
        Result<StockInOrderLevel2> result = new Result<StockInOrderLevel2>();
        StockInOrderLevel2 stockInOrderLevel2Entity = iStockInOrderLevel2Service.getById(stockInOrderLevel2.getId());
        if (stockInOrderLevel2Entity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = iStockInOrderLevel2Service.edit(stockInOrderLevel2);
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

    @AutoLog(value = "二级库入库管理-通过id删除")
    @ApiOperation(value = "二级库入库管理-通过id删除", notes = "二级库入库管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            StockInOrderLevel2 stockInOrderLevel2 = iStockInOrderLevel2Service.getById(id);
            iStockInOrderLevel2Service.removeById(stockInOrderLevel2);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog(value = "二级库入库管理分类-批量删除")
    @ApiOperation(value = "二级库入库管理分类-批量删除", notes = "二级库入库管理分类-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            iStockInOrderLevel2Service.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    @AutoLog(value = "二级库入库管理-导出")
    @ApiOperation(value = "二级库入库管理-导出", notes = "二级库入库管理-导出")
    @GetMapping(value = "/export")
    public void eqExport(@RequestParam(name = "ids", defaultValue = "") String ids,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        iStockInOrderLevel2Service.eqExport(ids, request, response);
    }
}
