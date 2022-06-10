package com.aiurt.boot.modules.schedule.controller;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.schedule.entity.ScheduleItem;
import com.swsc.copsms.modules.schedule.service.IScheduleItemService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: schedule_item
 * @Author: qian
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "schedule_item")
@RestController
@RequestMapping("/schedule/scheduleItem")
public class ScheduleItemController {
    @Autowired
    private IScheduleItemService scheduleItemService;

    /**
     * 分页列表查询
     *
     * @param scheduleItem
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "schedule_item-分页列表查询")
    @ApiOperation(value = "schedule_item-分页列表查询", notes = "schedule_item-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<ScheduleItem>> queryPageList(ScheduleItem scheduleItem,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     HttpServletRequest req) {
        Result<IPage<ScheduleItem>> result = new Result<IPage<ScheduleItem>>();
        QueryWrapper<ScheduleItem> queryWrapper = QueryGenerator.initQueryWrapper(scheduleItem, req.getParameterMap());
        Page<ScheduleItem> page = new Page<ScheduleItem>(pageNo, pageSize);
        IPage<ScheduleItem> pageList = scheduleItemService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param scheduleItem
     * @return
     */
    @AutoLog(value = "schedule_item-添加")
    @ApiOperation(value = "schedule_item-添加", notes = "schedule_item-添加")
    @PostMapping(value = "/add")
    public Result<ScheduleItem> add(@RequestBody ScheduleItem scheduleItem) {
        Result<ScheduleItem> result = new Result<ScheduleItem>();
        try {
            scheduleItemService.save(scheduleItem);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param scheduleItem
     * @return
     */
    @AutoLog(value = "schedule_item-编辑")
    @ApiOperation(value = "schedule_item-编辑", notes = "schedule_item-编辑")
    @PutMapping(value = "/edit")
    public Result<ScheduleItem> edit(@RequestBody ScheduleItem scheduleItem) {
        Result<ScheduleItem> result = new Result<ScheduleItem>();
        ScheduleItem scheduleItemEntity = scheduleItemService.getById(scheduleItem.getId());
        if (scheduleItemEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = scheduleItemService.updateById(scheduleItem);
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
    @AutoLog(value = "schedule_item-通过id删除")
    @ApiOperation(value = "schedule_item-通过id删除", notes = "schedule_item-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            scheduleItemService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "schedule_item-批量删除")
    @ApiOperation(value = "schedule_item-批量删除", notes = "schedule_item-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<ScheduleItem> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<ScheduleItem> result = new Result<ScheduleItem>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.scheduleItemService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "schedule_item-通过id查询")
    @ApiOperation(value = "schedule_item-通过id查询", notes = "schedule_item-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<ScheduleItem> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<ScheduleItem> result = new Result<ScheduleItem>();
        ScheduleItem scheduleItem = scheduleItemService.getById(id);
        if (scheduleItem == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(scheduleItem);
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
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<ScheduleItem> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                ScheduleItem scheduleItem = JSON.parseObject(deString, ScheduleItem.class);
                queryWrapper = QueryGenerator.initQueryWrapper(scheduleItem, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<ScheduleItem> pageList = scheduleItemService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "schedule_item列表");
        mv.addObject(NormalExcelConstants.CLASS, ScheduleItem.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("schedule_item列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<ScheduleItem> listScheduleItems = ExcelImportUtil.importExcel(file.getInputStream(), ScheduleItem.class, params);
                scheduleItemService.saveBatch(listScheduleItems);
                return Result.ok("文件导入成功！数据行数:" + listScheduleItems.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

    @GetMapping("getAllScheduleItem")
    public Result<List<ScheduleItem>> getAllScheduleItem() {
        Result<List<ScheduleItem>> result = new Result<List<ScheduleItem>>();
        List<ScheduleItem> itemList = scheduleItemService.list(new LambdaQueryWrapper<ScheduleItem>().eq(ScheduleItem::getDelFlag, 0));
        result.setResult(itemList);
        result.setSuccess(true);
        return result;
    }

}
