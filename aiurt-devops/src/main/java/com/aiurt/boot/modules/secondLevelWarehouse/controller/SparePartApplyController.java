package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import java.text.ParseException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.enums.MaterialApplyCommitEnum;
import com.swsc.copsms.common.util.PageLimitUtil;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartApply;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.AddApplyDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockApplyExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockOutDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.service.ISparePartApplyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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

    /**
     * 分页列表查询
     *
     * @param sparePartApply
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "备件申领-分页列表查询")
    @ApiOperation(value = "备件申领-分页列表查询", notes = "备件申领-分页列表查询")
    @GetMapping(value = "/list")
    public Result<PageLimitUtil<SparePartApply>> queryPageList(SparePartApply sparePartApply,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       @RequestParam(name = "startTime", required = false) String startTime,
                                                       @RequestParam(name = "endTime", required = false) String endTime,
                                                       HttpServletRequest req) throws ParseException {
        Result<PageLimitUtil<SparePartApply>> result = new Result<PageLimitUtil<SparePartApply>>();
		PageLimitUtil<SparePartApply> pageLimitUtil = sparePartApplyService.
				queryPageList(sparePartApply, pageNo, pageSize, startTime, endTime,req);
		result.setSuccess(true);
        result.setResult(pageLimitUtil);
        return result;
    }

    /**
     * 添加申领单
     *
     * @param addApplyDTO
     * @return
     */
    @AutoLog(value = "备件申领-添加")
    @ApiOperation(value = "添加申领单", notes = "添加申领单")
    @PostMapping(value = "/add")
    public Result<SparePartApply> add(@RequestBody AddApplyDTO addApplyDTO) {
        Result<SparePartApply> result = new Result<SparePartApply>();
        try {
            sparePartApplyService.addApply(addApplyDTO);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
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
    public Result<SparePartApply> edit(@RequestParam("ids") List<Integer> ids) {
        Result<SparePartApply> result = new Result<SparePartApply>();
        if (CollUtil.isEmpty(ids)) {
            result.error500("ids不能为空");
        } else {
            SparePartApply sparePartApply = new SparePartApply();
            boolean ok = sparePartApplyService.update(
                    sparePartApply.setCommitStatus(MaterialApplyCommitEnum.COMMITTED.getCode())
                    , new QueryWrapper<SparePartApply>().in("id", ids));
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
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
    @AutoLog(value = "备件申领-通过id删除")
    @ApiOperation(value = "备件申领-通过id删除", notes = "备件申领-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            sparePartApplyService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog("出库确认-编辑")
    @ApiOperation("出库确认")
    @PostMapping(value = "/stockOutConfirm")
    public Result<SparePartApply> stockOutConfirm(@RequestBody StockOutDTO stockOutDTOList) {
        Result<SparePartApply> result = new Result<SparePartApply>();
        if (CollUtil.isEmpty(stockOutDTOList.getMaterialVOList())) {
            result.error500("出库列表不能为空");
        } else {
            Boolean aBoolean = sparePartApplyService.stockOutConfirm(stockOutDTOList);
            //TODO 返回false说明什么？
            if (aBoolean) {
                result.success("修改成功!");
            }
        }

        return result;
    }
//    /**
//     * 批量删除
//     *
//     * @param ids
//     * @return
//     */
//    @AutoLog(value = "备件申领-批量删除")
//    @ApiOperation(value = "备件申领-批量删除", notes = "备件申领-批量删除")
//    @DeleteMapping(value = "/deleteBatch")
//    public Result<SparePartApply> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
//        Result<SparePartApply> result = new Result<SparePartApply>();
//        if (ids == null || "".equals(ids.trim())) {
//            result.error500("参数不识别！");
//        } else {
//            this.sparePartApplyService.removeByIds(Arrays.asList(ids.split(",")));
//            result.success("删除成功!");
//        }
//        return result;
//    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "备件申领-通过id查询")
    @ApiOperation(value = "备件申领-通过id查询", notes = "备件申领-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SparePartApply> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SparePartApply> result = new Result<SparePartApply>();
        SparePartApply sparePartApply = sparePartApplyService.getById(id);
        if (sparePartApply == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sparePartApply);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @ApiOperation("导出excel")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(
            @ApiParam(value = "行数据ids" ,required = true) @RequestParam("ids") List<Integer> ids,
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<StockApplyExcel> list = sparePartApplyService.exportXls(ids);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "备件申领列表");
        mv.addObject(NormalExcelConstants.CLASS, StockApplyExcel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件申领列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

}
