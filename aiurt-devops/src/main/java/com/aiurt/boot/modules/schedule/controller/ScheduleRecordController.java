package com.aiurt.boot.modules.schedule.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.modules.schedule.entity.ScheduleHolidays;
import com.aiurt.boot.modules.schedule.entity.ScheduleItem;
import com.aiurt.boot.modules.schedule.entity.ScheduleLog;
import com.aiurt.boot.modules.schedule.entity.ScheduleRecord;
import com.aiurt.boot.modules.schedule.model.DayScheduleModel;
import com.aiurt.boot.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.boot.modules.schedule.service.IScheduleHolidaysService;
import com.aiurt.boot.modules.schedule.service.IScheduleItemService;
import com.aiurt.boot.modules.schedule.service.IScheduleLogService;
import com.aiurt.boot.modules.schedule.service.IScheduleRecordService;
import com.aiurt.boot.modules.schedule.vo.ScheduleCalendarVo;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.RoleConstant;
import com.aiurt.common.util.DateUtils;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: schedule_record
 * @Author: qian
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "排班记录")
@RestController
@RequestMapping("/schedule/scheduleRecord")
public class ScheduleRecordController {
    @Autowired
    private IScheduleRecordService scheduleRecordService;
    @Autowired
    private IScheduleHolidaysService holidaysService;
    @Autowired
    private IScheduleItemService itemService;
//    @Autowired
//    private ISysUserService userService;
    @Autowired
    private IScheduleLogService logService;

    /**
     * 分页列表查询
     *
     * @param scheduleRecord
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "排班记录-分页列表查询")
    @ApiOperation(value = "排班记录-分页列表查询", notes = "排班记录-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<ScheduleRecord>> queryPageList(ScheduleRecord scheduleRecord,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        Result<IPage<ScheduleRecord>> result = new Result<IPage<ScheduleRecord>>();
        QueryWrapper<ScheduleRecord> queryWrapper = QueryGenerator.initQueryWrapper(scheduleRecord, req.getParameterMap());
        Page<ScheduleRecord> page = new Page<ScheduleRecord>(pageNo, pageSize);
        IPage<ScheduleRecord> pageList = scheduleRecordService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param scheduleRecord
     * @return
     */
    @AutoLog(value = "排班记录-添加")
    @ApiOperation(value = "排班记录-添加", notes = "排班记录-添加")
    @PostMapping(value = "/add")
    public Result<ScheduleRecord> add(@RequestBody ScheduleRecord scheduleRecord) {
        Result<ScheduleRecord> result = new Result<ScheduleRecord>();
        try {
            scheduleRecordService.save(scheduleRecord);
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
     * @param scheduleRecord
     * @return
     */
    @AutoLog(value = "排班记录-编辑")
    @ApiOperation(value = "排班记录-编辑", notes = "排班记录-编辑")
    @PutMapping(value = "/edit")
    public Result<ScheduleRecord> edit(@RequestBody ScheduleRecord scheduleRecord) {
        Result<ScheduleRecord> result = new Result<ScheduleRecord>();
        ScheduleRecord scheduleRecordEntity = scheduleRecordService.getById(scheduleRecord.getId());
        if (scheduleRecordEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = scheduleRecordService.updateById(scheduleRecord);

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
    @AutoLog(value = "排班记录-通过id删除")
    @ApiOperation(value = "排班记录-通过id删除", notes = "排班记录-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            scheduleRecordService.removeById(id);
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
    @AutoLog(value = "排班记录-批量删除")
    @ApiOperation(value = "排班记录-批量删除", notes = "排班记录-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<ScheduleRecord> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<ScheduleRecord> result = new Result<ScheduleRecord>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.scheduleRecordService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "排班记录-通过id查询")
    @ApiOperation(value = "排班记录-通过id查询", notes = "排班记录-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<ScheduleRecord> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<ScheduleRecord> result = new Result<ScheduleRecord>();
        ScheduleRecord scheduleRecord = scheduleRecordService.getById(id);
        if (scheduleRecord == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(scheduleRecord);
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
        QueryWrapper<ScheduleRecord> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                ScheduleRecord scheduleRecord = JSON.parseObject(deString, ScheduleRecord.class);
                queryWrapper = QueryGenerator.initQueryWrapper(scheduleRecord, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<ScheduleRecord> pageList = scheduleRecordService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "schedule_record列表");
        mv.addObject(NormalExcelConstants.CLASS, ScheduleRecord.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("schedule_record列表数据", "导出人:Jeecg", "导出信息"));
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
                List<ScheduleRecord> listScheduleRecords = ExcelImportUtil.importExcel(file.getInputStream(), ScheduleRecord.class, params);
                scheduleRecordService.saveBatch(listScheduleRecords);
                return Result.ok("文件导入成功！数据行数:" + listScheduleRecords.size());
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

    @ApiOperation(value = "查询工班日历", notes = "查询工班日历")
    @RequestMapping(value = "/getDayUserSchedule", method = RequestMethod.GET)
    public Result<List<ScheduleCalendarVo>> getDayUserSchedule(@RequestParam(name = "date", required = true) String date) {
        Result<List<ScheduleCalendarVo>> result = new Result<List<ScheduleCalendarVo>>();
        List<ScheduleCalendarVo> list = new ArrayList<>();
        Date d = DateUtils.str2Date(date, new SimpleDateFormat("yyyy-MM-dd"));
        List<ScheduleRecord> scheduleRecords = scheduleRecordService.list(new QueryWrapper<ScheduleRecord>().eq("date", d).orderByAsc("id", "item_id"));
        for (ScheduleRecord s : scheduleRecords) {
            ScheduleCalendarVo scheduleCalendarVo = new ScheduleCalendarVo();
            if ("白班".equals(s.getItemName())) {
                scheduleCalendarVo.setType("success");
            } else if ("夜班".equals(s.getItemName())) {
                scheduleCalendarVo.setType("warning");
            } else {
                scheduleCalendarVo.setType("error");
            }
            scheduleCalendarVo.setContent(s.getItemName() + "-" + s.getUserId());
            list.add(scheduleCalendarVo);
        }

        result.setResult(list);
        result.setSuccess(true);
        return result;
    }


    @GetMapping(value = "getUserSchedule")
    public Result<List<DayScheduleModel>> getUserSchedule(@RequestParam(name = "date", required = false) String date,
                                                          @RequestParam(name = "orgId", required = false) String orgId) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // todo 后期修改
        List<String> roleCodeList = new ArrayList<>();
//        List<String> roleCodeList = userService.getRoleCodeById(loginUser.getId());
        if (StringUtils.isBlank(orgId)&&!roleCodeList.contains(RoleConstant.DIRECTOR)&&!roleCodeList.contains(RoleConstant.ADMIN)){
            orgId = loginUser.getOrgId();
        }
        Result<List<DayScheduleModel>> result = new Result<List<DayScheduleModel>>();
        if (StringUtils.isEmpty(date)) {
            date = DateUtil.format(new Date(), "yyyy-MM");
        }
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(DateUtils.parseDate(date, "yyyy-MM"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int maximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<DayScheduleModel> list = new ArrayList<>(maximum);
        for (int index = 0; index < maximum; index++) {
            DayScheduleModel model = new DayScheduleModel();
            model.setHolidays(new ArrayList<String>());
            model.setVoList(new ArrayList<ScheduleCalendarVo>());
            list.add(model);
        }
        List<ScheduleRecordModel> allRecordList = scheduleRecordService.getAllScheduleRecordsByMonth(date,orgId);
        if (allRecordList != null && allRecordList.size() > 0) {
            for (ScheduleRecordModel recordModel : allRecordList) {
                calendar.setTime(recordModel.getDate());
                int index = calendar.get(Calendar.DAY_OF_MONTH) - 1;
                ScheduleCalendarVo scheduleCalendarVo = new ScheduleCalendarVo();
                if ("白班".equals(recordModel.getItemName())) {
                    scheduleCalendarVo.setType("success");
                } else if ("夜班".equals(recordModel.getItemName())) {
                    scheduleCalendarVo.setType("warning");
                } else {
                    scheduleCalendarVo.setType("error");
                }
                scheduleCalendarVo.setColor(recordModel.getColor());
                scheduleCalendarVo.setContent(recordModel.getItemName() + "-" + recordModel.getUserName());
                list.get(index).getVoList().add(scheduleCalendarVo);
            }
        }
        List<ScheduleHolidays> holidaysList = holidaysService.getListByMonth(date);
        if (holidaysList != null && holidaysList.size() > 0) {
            for (ScheduleHolidays holidays : holidaysList) {
                calendar.setTime(holidays.getDate());
                int index = calendar.get(Calendar.DAY_OF_MONTH) - 1;
                list.get(index).getHolidays().add(holidays.getName());
            }
        }
        result.setResult(list);
        result.setSuccess(true);
        return result;
    }

    @GetMapping("dayRecordList")
    public Result<List<ScheduleRecordModel>> dayRecordList(@RequestParam(name = "date", required = false) String date) {
        Result<List<ScheduleRecordModel>> result = new Result<List<ScheduleRecordModel>>();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaQueryWrapper<LoginUser> queryWrapper = new LambdaQueryWrapper<>();
        // todo 后期修改
        List<LoginUser> userList = new ArrayList<>();
//        List<LoginUser> userList = userService.list(queryWrapper.like(LoginUser::getOrgCode, loginUser.getOrgCode()));
        if (StringUtils.isNotEmpty(date)&& ObjectUtil.isNotEmpty(userList)) {
            List<String>ids = userList.stream().map(LoginUser::getId).collect(Collectors.toList());
            List<ScheduleRecordModel> recordModelList = scheduleRecordService.getRecordListByDayAndUserIds(date,ids);
            result.setResult(recordModelList);
        }
        result.setSuccess(true);
        return result;
    }

    //editRecord
    @AutoLog(value = "排班记录-编辑")
    @ApiOperation(value = "排班记录-编辑", notes = "排班记录-编辑")
    @PutMapping(value = "/editRecord")
    public Result<ScheduleRecord> editRecord(@RequestBody ScheduleRecordModel scheduleRecord) {
        Result<ScheduleRecord> result = new Result<ScheduleRecord>();
        ScheduleRecord scheduleRecordEntity = scheduleRecordService.getById(scheduleRecord.getId());
        if (scheduleRecordEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            ScheduleItem oldItem = itemService.getById(scheduleRecordEntity.getItemId());
            ScheduleItem newItem = itemService.getById(scheduleRecord.getItemId());
            scheduleRecordEntity.setItemId(newItem.getId());
            scheduleRecordEntity.setColor(newItem.getColor());
            scheduleRecordEntity.setItemName(newItem.getName());
            scheduleRecordEntity.setStartTime(newItem.getStartTime());
            scheduleRecordEntity.setEndTime(newItem.getEndTime());

            boolean ok = scheduleRecordService.updateById(scheduleRecordEntity);

            ScheduleLog log=new ScheduleLog();
            log.setDate(scheduleRecordEntity.getDate());
            log.setRecordId(scheduleRecordEntity.getId());
            log.setDelFlag(0);
            log.setSourceItemId(oldItem.getId());
            log.setSourceItemName(oldItem.getName());
            log.setTargetItemId(newItem.getId());
            log.setTargetItemName(newItem.getName());
            log.setUserId(scheduleRecordEntity.getUserId());
            log.setRemark(scheduleRecord.getRemark());
            // todo 后期修改
            LoginUser user = new LoginUser();
//            LoginUser user = userService.getById(log.getUserId());
            log.setUserName(user.getRealname());
            logService.save(log);


            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }
}
