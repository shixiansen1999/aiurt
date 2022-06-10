package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.util.TokenUtils;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailEditDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockLevel2CheckDetailVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2CheckDetailService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 二级库盘点列表记录
 * @Author: qian
 * @Date: 2021-09-18
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库盘点列表记录")
@RestController
@RequestMapping("/secondLevelWarehouse/stockLevel2CheckDetail")
public class StockLevel2CheckDetailController {
    @Autowired
    private IStockLevel2CheckDetailService stockLevel2CheckDetailService;

    @Resource
    private ISysBaseAPI iSysBaseAPI;

    /**
     * 分页列表查询
     *
     * @param stockLevel2CheckDetailDTO
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "填写盘点结果以及查看盘点结果的列表-分页列表查询")
    @ApiOperation(value = "填写盘点结果以及查看盘点结果的列表-分页列表查询", notes = "填写盘点结果以及查看盘点结果的列表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockLevel2CheckDetailVO>> queryPageList(
            StockLevel2CheckDetailDTO stockLevel2CheckDetailDTO,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {
        Result<IPage<StockLevel2CheckDetailVO>> result = new Result<>();
        if (StrUtil.isEmpty(stockLevel2CheckDetailDTO.getStockCheckCode())) {
            result.error500("盘点任务单号不能为空");
        } else {
            Page<StockLevel2CheckDetailVO> page = new Page<>(pageNo, pageSize);
            IPage<StockLevel2CheckDetailVO> pageList = stockLevel2CheckDetailService.queryPageList(page, stockLevel2CheckDetailDTO);
            result.setSuccess(true);
            result.setResult(pageList);
        }

        return result;
    }

    /**
     * 最新库存数据
     *
     * @param warehouseCode
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "最新库存数据-分页列表查询")
    @ApiOperation(value = "最新库存数据-分页列表查询", notes = "最新库存数据-分页列表查询")
    @GetMapping(value = "/queryNewestStockList")
    public Result<IPage<StockLevel2CheckDetailVO>> queryNewestStockList(
            @ApiParam(value = "仓库编号",required = true)@RequestParam("warehouseCode") String warehouseCode,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {
        Result<IPage<StockLevel2CheckDetailVO>> result = new Result<>();
        if (StrUtil.isEmpty(warehouseCode)) {
            result.error500("仓库编号不能为空");
        } else {
            Page<StockLevel2CheckDetailVO> page = new Page<>(pageNo, pageSize);
            IPage<StockLevel2CheckDetailVO> pageList = stockLevel2CheckDetailService.queryNewestStockList(page,warehouseCode);
            result.setSuccess(true);
            result.setResult(pageList);
        }
        return result;
    }

    @AutoLog("最新库存数据-导出")
    @ApiOperation("最新库存数据导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportNewestStockXls(
            @ApiParam("仓库编号") @RequestParam("warehouseCode") String warehouseCode,
            HttpServletRequest request, HttpServletResponse response) {
        String userName = TokenUtils.getUserName(request, iSysBaseAPI);
        // 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<StockLevel2CheckDetailExcel> list = stockLevel2CheckDetailService.exportNewestStockXls(warehouseCode);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSerialNumber(i + 1);
        }
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "最新库存数据列表");
        mv.addObject(NormalExcelConstants.CLASS, StockLevel2CheckDetailExcel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("最新库存数据列表数据", "导出人:"+userName, "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 填写盘点结果
     *
     * @param checkDetailList
     * @return
     */
    @AutoLog(value = "填写盘点结果-批量编辑")
    @ApiOperation(value = "填写盘点结果-批量编辑", notes = "填写盘点结果-批量编辑")
    @PostMapping(value = "/edit")
    public Result<?> edit(@RequestBody List<StockLevel2CheckDetailEditDTO> checkDetailList) {
        Result<?> result = new Result<>();
        if(CollUtil.isEmpty(checkDetailList)){
            result.error500("接收参数数据为空");
        }
        stockLevel2CheckDetailService.updateDetail(checkDetailList);
        result.success("修改成功!");
        return result;
    }
}
