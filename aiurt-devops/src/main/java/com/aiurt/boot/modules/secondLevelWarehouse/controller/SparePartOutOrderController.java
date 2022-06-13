package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartOutOrder;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartOutExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartOutVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartOutOrderService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.result.FaultSparePartResult;
import com.aiurt.common.result.SparePartResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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
     * 分页列表查询
     *
     * @param sparePartLendQuery
     * @param req
     * @return
     */
    @AutoLog(value = "备件出库表-分页列表查询")
    @ApiOperation(value = "备件出库表-分页列表查询", notes = "备件出库表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SparePartOutVO>> queryPageList(
            SparePartLendQuery sparePartLendQuery,
            HttpServletRequest req) {
        Result<IPage<SparePartOutVO>> result = new Result<IPage<SparePartOutVO>>();
        Page<SparePartOutVO> page = new Page<SparePartOutVO>(sparePartLendQuery.getPageNo(), sparePartLendQuery.getPageSize());
        IPage<SparePartOutVO> pageList = sparePartOutOrderService.queryPageList(page, sparePartLendQuery);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param sparePartOutOrder
     * @return
     */
    @AutoLog(value = "备件出库表-添加")
    @ApiOperation(value = "备件出库表-添加", notes = "备件出库表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@Valid  @RequestBody SparePartOutOrder sparePartOutOrder,
                         HttpServletRequest req) {
        Result<?> result = new Result<SparePartOutOrder>();
        try {
            result= sparePartOutOrderService.addOutOrder(result, sparePartOutOrder,req);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 导出excel
     * @param sparePartLendQuery
     * @return
     */
    @AutoLog("备件出库信息-导出")
    @ApiOperation("备件出库信息导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(SparePartLendQuery sparePartLendQuery) {
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SparePartOutExcel> sparePartOutExcelIPage = sparePartOutOrderService.exportXls(sparePartLendQuery);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "备件出库信息列表");
        mv.addObject(NormalExcelConstants.CLASS, SparePartOutExcel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件出库信息列表数据","导出信息", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, sparePartOutExcelIPage);
        return mv;
    }

    /**
     * 履历-查询故障更换备件信息
     * @param id
     * @return
     */
    @AutoLog("履历-查询故障更换备件信息")
    @ApiOperation("履历-查询故障更换备件信息")
    @GetMapping(value = "/selectFaultChangePart")
    public Result<List<SparePartResult>> selectFaultChangePart(@RequestParam("id") Long id) {
        Result<List<SparePartResult>> sparePartResultResult = sparePartOutOrderService.selectFaultChangePart(id);
        return sparePartResultResult;
    }

    /**
     * 导出 履历-查询故障更换备件信息
     * @param id
     * @return
     */
    @AutoLog("履历-查询故障更换备件信息")
    @ApiOperation("履历-查询故障更换备件信息")
    @GetMapping(value = "/exportXlsFaultChangePart")
    public ModelAndView exportXlsFaultChangePart(@RequestParam("id") Long id) {
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        Result<List<SparePartResult>> sparePartResultResult = sparePartOutOrderService.selectFaultChangePart(id);
        List<SparePartResult> result = sparePartResultResult.getResult();
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "履历-更换备件信息");
        mv.addObject(NormalExcelConstants.CLASS, SparePartResult.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("履历-更换备件信息",  "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, result);
        return mv;
    }

    /**
     * 履历-查询故障信息
     * @param id
     * @return
     */
    @AutoLog("履历-查询故障信息")
    @ApiOperation("履历-查询故障件信息")
    @GetMapping(value = "/selectFaultDetail")
    public Result<List<FaultSparePartResult>> selectFaultDetail(@RequestParam("id") Long id) {
        Result<List<FaultSparePartResult>> listResult = sparePartOutOrderService.selectFaultDetail(id);
        return listResult;
    }

    /**
     * 导出 履历-查询故障信息
     * @param id
     * @return
     */
    @AutoLog("履历-查询故障信息")
    @ApiOperation("履历-查询故障信息")
    @GetMapping(value = "/exportXlsFaultDetail")
    public ModelAndView exportXlsFaultDetail(@RequestParam("id") Long id) {
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        Result<List<FaultSparePartResult>> listResult = sparePartOutOrderService.selectFaultDetail(id);
        List<FaultSparePartResult> result = listResult.getResult();
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "履历-故障信息");
        mv.addObject(NormalExcelConstants.CLASS, FaultSparePartResult.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("履历-故障信息",  "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, result);
        return mv;
    }


}
