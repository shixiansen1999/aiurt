package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartApply;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.*;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartApplyVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartApplyService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.enums.MaterialApplyCommitEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Description: 备件申领
 * @Author: qian
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "备件申领/二级库出库")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartApply")
public class SparePartApplyController {
    @Autowired
    private ISparePartApplyService sparePartApplyService;
    @Resource
    private ISysBaseAPI iSysBaseAPI;

    /**
     * 备件申领-分页列表查询
     *
     * @param sparePartQuery
     * @return
     */
    @AutoLog(value = "备件申领-分页列表查询")
    @ApiOperation(value = "备件申领-分页列表查询", notes = "备件申领-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SparePartApplyVO>> queryPageList(SparePartQuery sparePartQuery) {
        Page<SparePartApplyVO> page = new Page<>(sparePartQuery.getPageNo(), sparePartQuery.getPageSize());
        IPage<SparePartApplyVO> list = sparePartApplyService.queryPageList(page, sparePartQuery);
        return Result.ok(list);
    }

    /**
     * 二级库出库列表-分页列表查询
     *
     * @param sparePartQuery
     * @return
     */
    @AutoLog(value = "二级库出库列表-分页列表查询")
    @ApiOperation(value = "二级库出库列表-分页列表查询", notes = "二级库出库列表-分页列表查询")
    @GetMapping(value = "/listLevel2")
    public Result<IPage<SparePartApplyVO>> queryPageListLevel2(SparePartQuery sparePartQuery) {
        Page<SparePartApplyVO> page = new Page<>(sparePartQuery.getPageNo(), sparePartQuery.getPageSize());
        IPage<SparePartApplyVO> list = sparePartApplyService.queryPageListLevel2(page, sparePartQuery);
        return Result.ok(list);
    }

    /**
     * 添加申领单
     *
     * @param addApplyDTO
     * @return
     */
    @AutoLog(value = "添加申领单")
    @ApiOperation(value = "添加申领单", notes = "添加申领单")
    @PostMapping(value = "/add")
    public Result<SparePartApply> add(@Valid @RequestBody AddApplyDTO addApplyDTO,
                                      HttpServletRequest req) {
        try {
            sparePartApplyService.addApply(addApplyDTO, req);
            return Result.ok("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("操作失败" + e.getMessage());
        }
    }

    /**
     * 提交申领单
     *
     * @param ids
     * @param req
     * @return
     */
    @AutoLog(value = "提交申领单")
    @ApiOperation(value = "提交申领单", notes = "提交申领单")
    @GetMapping(value = "/submitForm")
    public Result<?> submitForm(@RequestParam(name = "ids") String ids,
                                HttpServletRequest req) {
        try {
            return sparePartApplyService.submitFormByIds(ids, req);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("提交失败" + e.getMessage());
        }
    }

    /**
     * 提交申领单
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "提交申领单-编辑")
    @ApiOperation(value = "提交申领单", notes = "提交申领单")
    @GetMapping(value = "/edit")
    public Result<SparePartApply> edit(@RequestParam("ids") List<Integer> ids,
                                       HttpServletRequest req) {
        Result<SparePartApply> result = new Result<SparePartApply>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();

        if (CollUtil.isEmpty(ids)) {
            result.error500("ids不能为空");
        } else {
            SparePartApply sparePartApply = new SparePartApply();
            sparePartApply.setUpdateBy(userId);
            sparePartApply.setCommitStatus(MaterialApplyCommitEnum.COMMITTED.getCode());
            boolean ok = sparePartApplyService.update(sparePartApply
                    , new QueryWrapper<SparePartApply>().in(SparePartApply.ID, ids));
            if (ok) {
                result.success("修改成功!");
            } else {
                result.error500("修改失败!");
            }
        }
        return result;
    }


    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "备件申领/二级库出库-通过id删除")
    @ApiOperation(value = "备件申领/二级库出库-通过id删除", notes = "备件申领/二级库出库-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            sparePartApplyService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败, {}", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog("出库确认-编辑")
    @ApiOperation("出库确认")
    @PostMapping(value = "/stockOutConfirm")
    public Result<?> stockOutConfirm(@RequestBody StockOutDTO stockOutDTOList,
                                     HttpServletRequest req) {
        Result<SparePartApply> result = new Result<SparePartApply>();
        try {
            sparePartApplyService.stockOutConfirm(stockOutDTOList, req);
        } catch (Exception e) {
            log.error("出库失败, {}", e.getMessage());
            return Result.error("出库失败!" + e.getMessage());
        }
        return result.success("出库成功!");
    }

    /**
     * 编辑申领单
     *
     * @param editApplyDTO
     * @return
     */
    @AutoLog(value = "备件申领-编辑")
    @ApiOperation(value = "编辑申领单", notes = "编辑申领单")
    @PostMapping(value = "/editApply")
    public Result<SparePartApply> editApply(@Valid @RequestBody EditApplyDTO editApplyDTO,
                                            HttpServletRequest req) {
        Result<SparePartApply> result = new Result<SparePartApply>();
        try {
            sparePartApplyService.editApply(editApplyDTO, req);
            result.success("编辑成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("编辑失败" + e.getMessage());
        }
        return result;
    }

    /**
     * 备件申领导出excel
     *
     * @param request
     * @param response
     */
    @AutoLog(value = "备件申领导出excel")
    @ApiOperation("备件申领导出excel")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(
            SparePartQuery sparePartQuery,
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<StockApplyExcel> list = sparePartApplyService.exportXls(sparePartQuery);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "备件申领列表");
        mv.addObject(NormalExcelConstants.CLASS, StockApplyExcel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件申领列表数据", "导出信息", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 二级库出库导出excel
     *
     * @param selections
     * @return
     */
    @ApiOperation("二级库出库导出excel")
    @GetMapping(value = "/exportStock2Xls")
    public ModelAndView exportStock2Xls(@NotEmpty @ApiParam(value = "行数据ids", required = true) @RequestParam("selections") List<Integer> selections) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<StockOutExcel> list = sparePartApplyService.exportStock2Xls(selections);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "二级库出库列表");
        mv.addObject(NormalExcelConstants.CLASS, StockOutExcel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("二级库出库列表数据", "导出信息", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

}
