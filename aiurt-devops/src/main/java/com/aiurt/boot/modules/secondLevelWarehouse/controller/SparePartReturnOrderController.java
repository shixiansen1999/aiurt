package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartOutOrder;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartReturnOrder;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartReturnQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartReturnVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartReturnOrderService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author WangHongTao
 * @Date 2021/11/15
 */
@Slf4j
@Api(tags = "备件退库表")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartReturnOrder")
public class SparePartReturnOrderController {

    @Resource
    ISparePartReturnOrderService sparePartReturnOrderService;

    /**
     * 分页列表查询
     *
     * @param sparePartReturnQuery
     * @param req
     * @return
     */
    @AutoLog(value = "备件退库表-分页列表查询")
    @ApiOperation(value = "备件退库表-分页列表查询", notes = "备件退库表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SparePartReturnVO>> queryPageList(
            SparePartReturnVO order,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Valid SparePartReturnQuery sparePartReturnQuery,
            HttpServletRequest req) {
        QueryWrapper<SparePartReturnVO> queryWrapper = QueryGenerator.initQueryWrapper(order, req.getParameterMap());
        Page<SparePartReturnVO> page = new Page<>(pageNo, pageSize);
        IPage<SparePartReturnVO> pageList = sparePartReturnOrderService.pageList(page, queryWrapper, sparePartReturnQuery);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param sparePartReturnOrder
     * @return
     */
    @AutoLog(value = "备件退库表-添加")
    @ApiOperation(value = "备件退库表-添加", notes = "备件退库表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@Valid @RequestBody SparePartReturnOrder sparePartReturnOrder,
                         HttpServletRequest req) {
        Result<?> result = new Result<SparePartOutOrder>();
        try {
            result= sparePartReturnOrderService.addReturnOrder(result, sparePartReturnOrder,req);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 导出excel
     * @param sparePartReturnQuery
     * @return
     */
    @AutoLog(value = "导出excel")
    @ApiOperation(value = "导出excel", notes = "导出excel")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(SparePartReturnQuery sparePartReturnQuery){
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SparePartReturnVO> pageList = sparePartReturnOrderService.exportXls(sparePartReturnQuery);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "备件退库列表");
        mv.addObject(NormalExcelConstants.CLASS, SparePartReturnVO.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件退库列表数据",  "导出信息", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "备件退库表-通过id删除")
    @ApiOperation(value = "备件退库表-通过id删除", notes = "备件退库表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            sparePartReturnOrderService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败{}", e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.ok("删除成功!");
    }
}
