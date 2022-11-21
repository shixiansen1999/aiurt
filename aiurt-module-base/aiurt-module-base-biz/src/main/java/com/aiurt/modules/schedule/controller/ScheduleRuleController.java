package com.aiurt.modules.schedule.controller;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.schedule.service.IScheduleRuleItemService;
import com.aiurt.modules.schedule.service.IScheduleRuleService;
import com.aiurt.modules.schedule.entity.ScheduleRule;
import com.aiurt.modules.schedule.entity.ScheduleRuleItem;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: schedule_rule
 * @Author: HQY
 * @Date: 2022-07-20
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "排班规则")
@RestController
@RequestMapping("/schedule/scheduleRule")
public class ScheduleRuleController {
    @Autowired
    private IScheduleRuleService scheduleRuleService;
    @Autowired
    private IScheduleRuleItemService scheduleRuleItemService;

    /**
     * 分页列表查询
     *
     * @param scheduleRule
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "排班规则-分页列表查询")
    @ApiOperation(value = "排班规则-分页列表查询", notes = "排班规则-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<ScheduleRule>> queryPageList(ScheduleRule scheduleRule,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     HttpServletRequest req) {
        Result<IPage<ScheduleRule>> result = new Result<IPage<ScheduleRule>>();
        QueryWrapper<ScheduleRule> queryWrapper = QueryGenerator.initQueryWrapper(scheduleRule, req.getParameterMap());
        Page<ScheduleRule> page = new Page<ScheduleRule>(pageNo, pageSize);
        IPage<ScheduleRule> pageList = scheduleRuleService.page(page, queryWrapper);
        pageList.getRecords().forEach(temp -> {
            List<ScheduleRuleItem> scheduleRuleItems = scheduleRuleItemService.getByRuleId(temp.getId());
            temp.setNames(scheduleRuleItems.stream().map(ScheduleRuleItem::getItemId).toArray(Integer[]::new));
            Integer[] keys = new Integer[scheduleRuleItems.size()];
            String way = "";
            for (int i = 0; i < scheduleRuleItems.size(); i++) {
                if (i<scheduleRuleItems.size()-1){
                    keys[i] = i;
                    way = way + scheduleRuleItems.get(i).getItemName() + "| ";
                }else {
                    keys[i] = i;
                    way = way + scheduleRuleItems.get(i).getItemName();
                }
            }
            temp.setKeys(keys);
            temp.setWay(way);

        });
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param scheduleRule
     * @return
     */
    @AutoLog(value = "排班规则-添加")
    @ApiOperation(value = "排班规则-添加", notes = "排班规则-添加")
    @PostMapping(value = "/add")
    public Result<ScheduleRule> add(@RequestBody ScheduleRule scheduleRule) {
        Result<ScheduleRule> result = new Result<ScheduleRule>();
        try {
            if ( null == scheduleRule.getKeys() || 0 >= scheduleRule.getKeys().length
                    ||null == scheduleRule.getNames()|| 0 >= scheduleRule.getNames().length ||StringUtils.isBlank(scheduleRule.getName())) {
                result.error500("操作失败");
                return result;
            }
            scheduleRuleService.save(scheduleRule);
            for (int i = 0; i < scheduleRule.getNames().length; i++) {
                ScheduleRuleItem scheduleRuleItem = new ScheduleRuleItem();
                scheduleRuleItem.setRuleId(scheduleRule.getId());
                scheduleRuleItem.setItemId(scheduleRule.getNames()[i]);
                scheduleRuleItem.setSort(i + 1);
                scheduleRuleItem.setCreateTime(new Date());
                scheduleRuleItemService.save(scheduleRuleItem);
            }
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
     * @param scheduleRule
     * @return
     */
    @AutoLog(value = "排班规则-编辑")
    @ApiOperation(value = "排班规则-编辑", notes = "排班规则-编辑")
    @PutMapping(value = "/edit")
    public Result<ScheduleRule> edit(@RequestBody ScheduleRule scheduleRule) {
        Result<ScheduleRule> result = new Result<ScheduleRule>();
        ScheduleRule scheduleRuleEntity = scheduleRuleService.getById(scheduleRule.getId());
        if (scheduleRuleEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = scheduleRuleService.updateById(scheduleRule);
            scheduleRuleItemService.remove(new QueryWrapper<ScheduleRuleItem>().eq("rule_id", scheduleRule.getId()));
            for (int i = 0; i < scheduleRule.getNames().length; i++) {
                ScheduleRuleItem scheduleRuleItem = new ScheduleRuleItem();
                scheduleRuleItem.setRuleId(scheduleRule.getId());
                scheduleRuleItem.setItemId(scheduleRule.getNames()[i]);
                scheduleRuleItem.setSort(i + 1);
                scheduleRuleItem.setCreateTime(new Date());
                scheduleRuleItemService.save(scheduleRuleItem);
            }

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
    @AutoLog(value = "排班规则-通过id删除")
    @ApiOperation(value = "排班规则-通过id删除", notes = "排班规则-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            scheduleRuleService.removeById(id);
            scheduleRuleItemService.remove(new QueryWrapper<ScheduleRuleItem>().eq("rule_id", id));
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
    @AutoLog(value = "排班规则-批量删除")
    @ApiOperation(value = "排班规则-批量删除", notes = "排班规则-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<ScheduleRule> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<ScheduleRule> result = new Result<ScheduleRule>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.scheduleRuleService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "排班规则-通过id查询")
    @ApiOperation(value = "排班规则-通过id查询", notes = "排班规则-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<ScheduleRule> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<ScheduleRule> result = new Result<ScheduleRule>();
        ScheduleRule scheduleRule = scheduleRuleService.getById(id);
        if (scheduleRule == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(scheduleRule);
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
        QueryWrapper<ScheduleRule> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                ScheduleRule scheduleRule = JSON.parseObject(deString, ScheduleRule.class);
                queryWrapper = QueryGenerator.initQueryWrapper(scheduleRule, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<ScheduleRule> pageList = scheduleRuleService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "schedule_rule列表");
        mv.addObject(NormalExcelConstants.CLASS, ScheduleRule.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("schedule_rule列表数据", "导出人:Jeecg", "导出信息"));
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
                List<ScheduleRule> listScheduleRules = ExcelImportUtil.importExcel(file.getInputStream(), ScheduleRule.class, params);
                scheduleRuleService.saveBatch(listScheduleRules);
                return Result.ok("文件导入成功！数据行数:" + listScheduleRules.size());
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

}
