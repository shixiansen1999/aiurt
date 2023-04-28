package com.aiurt.modules.schedule.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.util.DateUtils;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.schedule.dto.*;
import com.aiurt.modules.schedule.entity.ScheduleHolidays;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.mapper.ScheduleRecordMapper;
import com.aiurt.modules.schedule.model.DayScheduleModel;
import com.aiurt.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.modules.schedule.service.IScheduleHolidaysService;
import com.aiurt.modules.schedule.service.IScheduleRecordService;
import com.aiurt.modules.schedule.vo.ScheduleCalendarVo;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
 * @Author: HQY
 * @Date: 2022-07-20
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
    private ScheduleRecordMapper scheduleRecordMapper;
    @Autowired
    private ISysBaseAPI iSysBaseApi;
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
    @AutoLog(value = "排班记录-通过id删除",operateType = 4,permissionUrl = "/schedule/scheduleCalendar")
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
    @AutoLog(value = "排班记录-批量删除",operateType = 4,permissionUrl = "/schedule/scheduleCalendar")
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

    @AutoLog(value = "排班记录-查询工班日历1")
    @ApiOperation(value = "排班记录-查询工班日历1", notes = "排班记录-查询工班日历1")
    @GetMapping(value = "getUserSchedule")
    @PermissionData(pageComponent = "schedule/ScheduleCalendar")
    public Result<List<DayScheduleModel>> getUserSchedule(@RequestParam(name = "date", required = false) String date,
                                                          @RequestParam(name = "orgId", required = false) String orgId,
                                                          @RequestParam(name = "text", required = false) String text) {
        Result<List<DayScheduleModel>> result = new Result<List<DayScheduleModel>>();

        //根据数据规则查出所属权限的人员，这个只有根据部门权限查部门的人
        List<LoginUser> allUsers = iSysBaseApi.getAllUsers();
        List<String> userIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(allUsers)) {
            List<String> collect = allUsers.stream().map(LoginUser::getId).collect(Collectors.toList());
            userIds.addAll(collect);
        }else {
            result.setResult(new ArrayList<>());
            result.setSuccess(true);
            return result;
        }

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
        List<ScheduleRecordModel> allRecordList = scheduleRecordService.getAllScheduleRecordsByMonth(date,orgId,text,userIds);
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
                scheduleCalendarVo.setStartTime(recordModel.getStartTime());
                scheduleCalendarVo.setEndTime(recordModel.getEndTime());
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

    @AutoLog(value = "排班记录-我的排班")
    @ApiOperation(value = "排班记录-我的排班", notes = "排班记录-我的排班")
    @GetMapping(value = "getMySchedule")
    public Result<List<DayScheduleModel>> getMySchedule(@RequestParam(name = "date", required = false) String date,
                                                          @RequestParam(name = "userId", required = false) String userId) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //List<String> roleCodeList = scheduleRecordMapper.getRoleCodeById(loginUser.getId());
        if (StringUtils.isBlank(userId)
                //&& !roleCodeList.contains(RoleConstant.DIRECTOR) && !roleCodeList.contains(RoleConstant.ADMIN)
        ) {
            userId = loginUser.getId();
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
        List<ScheduleRecordModel> allRecordList = scheduleRecordService.getMySchedule(date, userId);
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
                scheduleCalendarVo.setStartTime(recordModel.getStartTime());
                scheduleCalendarVo.setEndTime(recordModel.getEndTime());
                scheduleCalendarVo.setTimeId(recordModel.getTimeId());
                scheduleCalendarVo.setOrgName(loginUser.getOrgName());
                scheduleCalendarVo.setDate(recordModel.getDate());
                scheduleCalendarVo.setContent(recordModel.getItemName() + "-" + recordModel.getUserName());
                list.get(index).getVoList().add(scheduleCalendarVo);
            }
        }else {
            int index = calendar.get(Calendar.DAY_OF_MONTH) - 1;
            ScheduleCalendarVo scheduleCalendarVo = new ScheduleCalendarVo();
            scheduleCalendarVo.setOrgName(loginUser.getOrgName());
            list.get(index).getVoList().add(scheduleCalendarVo);
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
        List<LoginUser> userList = scheduleRecordMapper.userList(loginUser.getOrgId());
        if (StringUtils.isNotEmpty(date) && ObjectUtil.isNotEmpty(userList)) {
            List<String> ids = userList.stream().map(LoginUser::getId).collect(Collectors.toList());
            List<ScheduleRecordModel> recordModelList = scheduleRecordService.getRecordListByDayAndUserIds(date, ids);
            result.setResult(recordModelList);
        }
        result.setSuccess(true);
        return result;
    }

    //editRecord
    @AutoLog(value = "排班记录-编辑")
    @ApiOperation(value = "排班记录-编辑", notes = "排班记录-编辑")
    @PutMapping(value = "/editRecord")
    public Result<ScheduleRecord> editRecord(@RequestBody List<ScheduleRecordREditDTO> scheduleRecordREditDTOList) {
        return scheduleRecordService.editRecord(scheduleRecordREditDTOList);
    }


    /**
     * 首页-根据日期查询班次情况
     *
     * @param scheduleRecordDTO
     * @return
     */
    @AutoLog(value = "首页-根据日期查询班次情况", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-根据日期查询班次情况", notes = "首页-根据日期查询班次情况")
    @PermissionData(pageComponent = "dashboard/Analysis")
    @RequestMapping(value = "/getStaffOnDuty", method = RequestMethod.GET)
    public Result<IPage<SysUserScheduleDTO>> getStaffOnDuty(@Validated ScheduleRecordDTO scheduleRecordDTO
    ) {
        Page<SysUserScheduleDTO> page = new Page<>(scheduleRecordDTO.getPageNo(), scheduleRecordDTO.getPageSize());
        IPage<SysUserScheduleDTO> maintenanceSituation = scheduleRecordService.getStaffOnDuty(page, scheduleRecordDTO);
        return Result.OK(maintenanceSituation);
    }

    /**
     * 获取大屏的班组信息
     *
     * @param lineCode 线路code
     * @return
     */
    @AutoLog(value = "获取大屏的班组信息", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "获取大屏的班组信息", notes = "获取大屏的班组信息")
    @RequestMapping(value = "/overviewInfo", method = RequestMethod.GET)
    public Result<ScheduleBigScreenDTO> getOverviewInfo(@ApiParam(name = "lineCode", value = "线路code,多个用,隔开") @RequestParam(value = "lineCode", required = false) String lineCode) {
        ScheduleBigScreenDTO result = scheduleRecordService.getTeamData(lineCode);
        return Result.OK(result);
    }

    /**
     * 获取大屏的班组信息-点击今日当班人数
     *
     * @param lineCode 线路code
     * @return
     */
    @AutoLog(value = "获取大屏的班组信息-点击今日当班人数", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "获取大屏的班组信息-点击今日当班人数", notes = "获取大屏的班组信息-点击今日当班人数")
    @RequestMapping(value = "/getTodayOndutyDetail", method = RequestMethod.GET)
    public Result<IPage<SysUserTeamDTO>> getTodayOndutyDetail(@ApiParam(name = "lineCode", value = "线路code,多个用,隔开") @RequestParam(value = "lineCode", required = false) String lineCode,
                                                              @ApiParam(name = "orgCode", value = "班组code") @RequestParam(value = "orgCode", required = false) String orgCode,
                                                              @ApiParam(name = "name", value = "名称") @RequestParam(value = "name", required = false) String name,
                                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<SysUserTeamDTO> page = new Page<>(pageNo, pageSize);
        IPage<SysUserTeamDTO> result = scheduleRecordService.getTodayOndutyDetail(lineCode, orgCode, page,name);
        return Result.OK(result);
    }

    /**
     * 获取大屏的班组信息-点击总人员数
     *
     * @param lineCode 线路code
     * @return
     */
    @AutoLog(value = "获取大屏的班组信息-点击总人员数", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "获取大屏的班组信息-点击总人员数", notes = "获取大屏的班组信息-点击总人员数")
    @RequestMapping(value = "/getTotalPepoleDetail", method = RequestMethod.GET)
    public Result<IPage<SysUserTeamDTO>> getTotalPepoleDetail(@ApiParam(name = "lineCode", value = "线路code,多个用,隔开") @RequestParam(value = "lineCode", required = false) String lineCode,
                                                              @ApiParam(name = "orgCode", value = "班组code") @RequestParam(value = "orgCode", required = false) String orgCode,
                                                              @ApiParam(name = "name", value = "名称") @RequestParam(value = "name", required = false) String name,
                                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<SysUserTeamDTO> page = new Page<>(pageNo, pageSize);
        IPage<SysUserTeamDTO> result = scheduleRecordService.getTotalPepoleDetail(lineCode, orgCode, page,name);
        return Result.OK(result);
    }

    /**
     * 大屏的班组信息-点击总班组数
     *
     * @param lineCode 线路code
     * @return
     */
    @AutoLog(value = "获取大屏的班组信息-点击总班组数", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "获取大屏的班组信息-点击总班组数", notes = "获取大屏的班组信息-点击总班组数")
    @RequestMapping(value = "/getTotalTeamDetail", method = RequestMethod.GET)
    public Result<IPage<SysTotalTeamDTO>> getTotalTeamDetail(@ApiParam(name = "lineCode", value = "线路code,多个用,隔开")
                                                             @RequestParam(value = "lineCode", required = false) String lineCode,
                                                             @ApiParam(name = "orgCode", value = "班组code") @RequestParam(value = "orgCode", required = false) String orgCode,
                                                             @ApiParam(name = "name", value = "名称") @RequestParam(value = "name", required = false) String name,
                                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<SysTotalTeamDTO> page = new Page<>(pageNo, pageSize);
        IPage<SysTotalTeamDTO> result = scheduleRecordService.getTotalTeamDetail(page, lineCode,orgCode,name);
        return Result.OK(result);
    }


    /**
     * 班组下拉框
     * @return
     */
    @AutoLog(value = "班组下拉框", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "班组下拉框", notes = "班组下拉框")
    @GetMapping(value = "/selectDepart")
    @PermissionData(pageComponent = "schedule/ScheduleCalendar")
    public Result<List<SysDepartModel>> selectDepart() {
        //通过权限查部门
        List<SysDepartModel> userSysDepart = iSysBaseApi.getAllSysDepart();
        Result<List<SysDepartModel>> result = new Result<>();
        result.setSuccess(true);
        if (CollUtil.isEmpty(userSysDepart)) {
            result.setResult(new ArrayList<>());
        }
        result.setResult(userSysDepart);
        return result;
    }
}
