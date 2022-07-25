package com.aiurt.modules.sparepart.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.dto.SparePartLendQuery;
import com.aiurt.modules.sparepart.entity.dto.SparePartOutExcel;
import com.aiurt.modules.sparepart.entity.vo.SparePartOutVO;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 备件出库表
 * @Author: qian
 * @Date: 2021-09-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "备件出库表")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartOutOrder")
public class SparePartOutOrderController {
    @Autowired
    private ISparePartOutOrderService sparePartOutOrderService;



    /**
     * 添加
     *
     * @param sparePartOutOrder
     * @return
     */
    @AutoLog(value = "备件出库表-添加")
    @ApiOperation(value = "备件出库表-添加", notes = "备件出库表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SparePartOutOrder sparePartOutOrder) {
        Result<?> result = new Result<SparePartOutOrder>();
        try {
            sparePartOutOrderService.addOutOrder(result, sparePartOutOrder);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }


    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @AutoLog("备件出库信息-导出")
    @ApiOperation("备件出库信息导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(
            SparePartLendQuery sparePartLendQuery,
            HttpServletRequest request, HttpServletResponse response) {
        // 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SparePartOutExcel> list = sparePartOutOrderService.exportXls(sparePartLendQuery);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "备件出库信息列表");
        mv.addObject(NormalExcelConstants.CLASS, SparePartOutExcel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件出库信息列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }



}
