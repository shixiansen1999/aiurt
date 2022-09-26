package com.aiurt.boot.report.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.report.model.FailureOrgReport;
import com.aiurt.boot.report.model.FailureReport;
import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.report.model.PatrolReportModel;
import com.aiurt.boot.report.model.dto.LineOrStationDTO;
import com.aiurt.boot.report.model.dto.MonthDTO;
import com.aiurt.boot.report.utils.PatrolDateUtils;
import com.aiurt.boot.screen.service.PatrolScreenService;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/9/19
 * @desc
 */
@Service
public class PatrolReportService {
    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private PatrolScreenService screenService;

    public Page<PatrolReport> getTaskDate(Page<PatrolReport> pageList, PatrolReportModel report) {
        String startDate = report.getStartDate();
        String endDate = report.getEndDate();
        PatrolReportModel orgCodeName = new PatrolReportModel();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(user.getId());
        List<String> orgList = userSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        if(CollUtil.isEmpty(orgList))
        {
            report.setOrgCode("null");
        }
        else
        {
            report.setOrgList(orgList);
            orgCodeName.setOrgList(orgList);
        }
        orgCodeName.setOrgCode(report.getOrgCode());
        PatrolReportModel omitModel = new PatrolReportModel();
        PatrolReportModel avgWeekOmit = new PatrolReportModel();
        PatrolReportModel avgMonthOmit = new PatrolReportModel();
        BeanUtils.copyProperties(report, omitModel);
        BeanUtils.copyProperties(report, avgWeekOmit);
        BeanUtils.copyProperties(report, avgMonthOmit);
        boolean isNullDate = false;
        if (ObjectUtil.isEmpty(report.getStartDate())) {
            isNullDate = true;
            //本周的周一和周日
            String thisWeek = getThisWeek(new Date());
            report.setStartDate(thisWeek.split("~")[0]);
            report.setEndDate(thisWeek.split("~")[1]);
            //推算漏检日期范围
            String date = screenService.getOmitDateScope(new Date());
            omitModel.setStartDate(date.split("~")[0]);
            omitModel.setEndDate(date.split("~")[1]);
            //推算前四周的日期范围
            String thisOmitDate = getThisOmitWeekDate();
            avgWeekOmit.setStartDate(thisOmitDate.split("~")[0]);
            avgWeekOmit.setEndDate(thisOmitDate.split("~")[0]);
            //推算12个月的日期范围
            String yearDate = getThisOmitYearDate();
            avgMonthOmit.setStartDate(yearDate.split("~")[0]);
            avgMonthOmit.setEndDate(yearDate.split("~")[1]);
        } else {
            //时间不为空，推算漏检日期范围
            String omitStartTime = screenService.getOmitDateScope(DateUtil.parse(report.getStartDate())).split("~")[0];
            String omitEndTime = screenService.getOmitDateScope(DateUtil.parse(report.getEndDate())).split("~")[1];
            omitModel.setStartDate(omitStartTime);
            omitModel.setEndDate(omitEndTime);
        }
        List<PatrolReport> list = patrolTaskMapper.getReportTaskList(pageList, orgCodeName);
        List<PatrolReport> reportList = patrolTaskMapper.getTasks(report);
        List<PatrolReport> omitList = patrolTaskMapper.getReportOmitList(omitModel);
        List<PatrolReport> avgWeekOmitList = patrolTaskMapper.getReportOmitList(avgWeekOmit);
        List<PatrolReport> avgMonthOmitList = patrolTaskMapper.getReportOmitList(avgMonthOmit);
        Integer faultNumber = 0;
        for (PatrolReport patrolReport : list) {
            //计算完成率、巡检总数、未完成、完成数、异常任务数
            if (CollUtil.isNotEmpty(reportList)) {
                for (PatrolReport d : reportList) {
                    List<String> taskIds = Arrays.asList(d.getTaskId().split(","));
                    if (CollUtil.isNotEmpty(taskIds)) {
                        //计算异常数量
                        for (String taskId : taskIds) {
                            List<PatrolTaskDevice> faultList = patrolTaskDeviceMapper.getFaultList(taskId);
                            if (faultList.size() > 0) {
                                faultNumber = faultNumber + 1;
                            }
                        }
                    }
                    patrolReport.setFaultNumber(faultNumber);
                    if (patrolReport.getOrgCode().equals(d.getOrgCode())) {
                        BeanUtils.copyProperties(d, patrolReport);
                    }
                }
            } else {
                PatrolReport patrolReport1 = setZero(patrolReport);
                BeanUtils.copyProperties(patrolReport1, patrolReport);
            }
            //计算漏巡视数
            if (CollUtil.isNotEmpty(omitList)) {

                for (PatrolReport d : omitList) {
                    if (patrolReport.getOrgCode().equals(d.getOrgCode())) {
                        patrolReport.setMissInspectedNumber(d.getMissInspectedNumber());
                    }
                }
            } else {
                patrolReport.setMissInspectedNumber(0);
            }
            //计算平均周漏巡视数
            if (CollUtil.isNotEmpty(avgWeekOmitList)) {
                for (PatrolReport d : avgWeekOmitList) {
                    if (patrolReport.getOrgCode().equals(d.getOrgCode())) {
                        if (ObjectUtil.isNull(patrolReport.getMissInspectedNumber())||patrolReport.getMissInspectedNumber() == 0) {
                            patrolReport.setAwmPatrolNumber("-");
                        } else {
                            //是否是默认
                            if (isNullDate == true) {
                                double avg = patrolReport.getMissInspectedNumber() / 4;
                                BigDecimal b = new BigDecimal(avg);
                                double fave = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                String completionRated = String.format("%.2f", fave);
                                patrolReport.setAwmPatrolNumber(completionRated);
                            } else {
                                Integer weekNumber = getWeekNumber(startDate, endDate);
                                if (weekNumber == 0) {
                                    patrolReport.setAwmPatrolNumber("-");
                                } else {
                                    double avg = patrolReport.getMissInspectedNumber() / weekNumber;
                                    BigDecimal b = new BigDecimal(avg);
                                    double fave = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    String completionRated = String.format("%.2f", fave);
                                    patrolReport.setAwmPatrolNumber(completionRated);
                                }
                            }
                        }

                    }
                }
            } else {
                patrolReport.setAwmPatrolNumber("-");
            }
            //计算平均每月漏巡视数
            if (CollUtil.isNotEmpty(avgMonthOmitList)) {
                for (PatrolReport d : avgWeekOmitList) {
                    if (patrolReport.getOrgCode().equals(d.getOrgCode())) {
                        if (ObjectUtil.isNull(patrolReport.getMissInspectedNumber())||patrolReport.getMissInspectedNumber() == 0) {
                            patrolReport.setAmmPatrolNumber("-");
                        } else {
                            //是否是默认
                            if (isNullDate == true) {
                                double avg = patrolReport.getMissInspectedNumber() / 12;
                                BigDecimal b = new BigDecimal(avg);
                                double fave = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                String completionRated = String.format("%.2f", fave);
                                patrolReport.setAmmPatrolNumber(completionRated);
                            } else {
                                Integer weekNumber = getWeekNumber(startDate, endDate);
                                if (weekNumber == 0) {
                                    patrolReport.setAmmPatrolNumber("-");
                                } else {
                                    double avg = patrolReport.getMissInspectedNumber() / weekNumber;
                                    BigDecimal b = new BigDecimal(avg);
                                    double fave = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    String completionRated = String.format("%.2f", fave);
                                    patrolReport.setAmmPatrolNumber(completionRated);
                                }
                            }
                        }

                    }
                }
            } else {
                patrolReport.setAmmPatrolNumber("-");
            }
        }
        return pageList.setRecords(list);
    }

    public PatrolReport setZero(PatrolReport report) {
        PatrolReport patrolReport = new PatrolReport();
        patrolReport.setTaskId(report.getTaskId());
        patrolReport.setOrgCode(report.getOrgCode());
        patrolReport.setOrgName(report.getOrgName());
        patrolReport.setTaskTotal(0);
        patrolReport.setCompletionRate("-");
        patrolReport.setAbnormalNumber(0);
        patrolReport.setMissInspectedNumber(0);
        patrolReport.setAwmPatrolNumber("-");
        patrolReport.setFaultNumber(0);
        patrolReport.setInspectedNumber(0);
        patrolReport.setNotInspectedNumber(0);
        return patrolReport;
    }

    public static String getThisWeek(Date date) {
        DateTime start = DateUtil.beginOfWeek(date);
        DateTime end = DateUtil.endOfWeek(date);
        String thisWeek = DateUtil.format(start, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(end, "yyyy-MM-dd 23:59:59");
        return thisWeek;
    }

    public String getThisOmitWeekDate() {
        Date start = DateUtil.beginOfWeek(new Date());
        DateTime end = DateUtil.endOfWeek(new Date());
        for (int i = 0; i < 4; i++) {
            Date date = DateUtil.offsetDay(start, -7);
            start = date;

        }
        DateTime lastSunday = DateUtil.offsetDay(end, -7);
        String thisOmitDate = DateUtil.format(start, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(lastSunday, "yyyy-MM-dd 23:59:59");
        return thisOmitDate;
    }

    public String getThisOmitYearDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, -1);
        Date date2 = DateUtil.date(c.getTime());
        Date lastYearFirstDay = DateUtil.beginOfMonth(date2);
        Date lastDay = DateUtil.lastMonth();
        Date lastMonday = DateUtil.endOfMonth(lastDay);
        String thisOmitDate = DateUtil.format(lastYearFirstDay, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(lastMonday, "yyyy-MM-dd 23:59:59");
        return thisOmitDate;
    }

    public Integer getWeekNumber(String start, String end) {
        boolean sameTime = DateUtil.isSameDay(new Date(), DateUtil.parseDate(start));
        boolean endTime = DateUtil.isSameDay(new Date(), DateUtil.parseDate(end));
        if (sameTime == true) {
            return 0;
        }
        if (endTime == true) {
             boolean sameDate = PatrolDateUtils.isSameDate(start, end);
             Integer weekNumber = PatrolDateUtils.countTwoDayWeek(start, end, sameDate);
             return  weekNumber;
        }
        return 1;
    }

    public ModelAndView reportExport(HttpServletRequest request, PatrolReportModel reportReqVO) {

        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //获取数据
        String[] strings = new String[]{"orgName", "taskTotal"};
        Page<PatrolReport> page = new Page<>(reportReqVO.getPageNo(), reportReqVO.getPageSize());
        IPage<PatrolReport> report = this.getTaskDate(page, reportReqVO);
        List<PatrolReport> reportData = report.getRecords();
        if (CollUtil.isNotEmpty(reportData)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "巡视报表");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, PatrolReport.class);
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS);
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, reportData);
        }
        return mv;
    }
     public List<FailureReport> getFailureReport(String lineCode, List<String> stationCode, String startTime, String endTime) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SimpleDateFormat mm = new SimpleDateFormat("yyyy-MM");
        if (ObjectUtil.isEmpty(startTime) && ObjectUtil.isEmpty(endTime)) {
            startTime = mm.format(new Date()) + "-01"; endTime = mm.format(new Date()) + "-31";
        }
        if (ObjectUtil.isNotEmpty(lineCode)&& CollectionUtil.isEmpty(stationCode)){
            stationCode= this.selectStation(lineCode).stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
        }else if (ObjectUtil.isEmpty(lineCode)&& CollectionUtil.isEmpty(stationCode)){
            stationCode = this.selectStation(null).stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
        }
        List<FailureReport> failureReportIPage = patrolTaskMapper.getFailureReport(sysUser.getId(), lineCode, stationCode, startTime, endTime);
        String finalStartTime = startTime;
        String finalEndTime = endTime;
           for (FailureReport failureReport : failureReportIPage) {
            if (failureReport.getLastMonthNum() != 0) {
        double sub = NumberUtil.sub(failureReport.getMonthNum(), failureReport.getLastMonthNum());
        BigDecimal div = NumberUtil.div(sub, NumberUtil.round(failureReport.getLastMonthNum(), 2));
        failureReport.setLastMonthStr(NumberUtil.round(NumberUtil.mul(div, 100), 2).toString() + "%");
             } else {
        failureReport.setLastMonthStr(NumberUtil.mul(NumberUtil.round(failureReport.getMonthNum(), 2), 100).toString() + "%");
        List<String> finalStationCode = stationCode;
        failureReportIPage.forEach(f -> {
            if (f.getLastMonthNum() != 0) {
                double sub = NumberUtil.sub(f.getMonthNum(), f.getLastMonthNum());
                BigDecimal div = NumberUtil.div(sub, NumberUtil.round(f.getLastMonthNum(), 2));
                f.setLastMonthStr(NumberUtil.round(NumberUtil.mul(div, 100), 2).toString() + "%");
            } else {
                f.setLastMonthStr(NumberUtil.mul(NumberUtil.round(f.getMonthNum(), 2), 100).toString() + "%");
            }
            if (f.getLastYearNum() != 0) {
                double sub = NumberUtil.sub(f.getYearNum(), f.getLastYearNum());
                BigDecimal div = NumberUtil.div(sub, NumberUtil.round(f.getLastYearNum(), 2));
                f.setLastMonthStr(NumberUtil.round(NumberUtil.mul(div, 100), 2).toString() + "%");
            } else {
                f.setLastYearStr(NumberUtil.mul(NumberUtil.round(f.getYearNum(), 2), 100).toString() + "%");
            }
            List<Integer> num = patrolTaskMapper.selectNum(f.getCode(), null, lineCode, finalStationCode, finalStartTime, finalEndTime);
            int s = num.stream().reduce(Integer::sum).orElse(0);
            f.setAverageResponse(f.getResolvedNum() == 0 ? 0 : s / f.getResolvedNum());
            f.setAverageResponse(f.getResolvedNum() == 0 ? 0 : s / f.getResolvedNum());
            List<Integer> num1 = patrolTaskMapper.selectNum1(f.getCode(), null, lineCode, finalStationCode, finalStartTime, finalEndTime);
            int s1 = num1.stream().reduce(Integer::sum).orElse(0);
            f.setAverageResolution(f.getResolvedNum() == 0 ? 0 : s1 / f.getResolvedNum());
        });
    }
}
        return failureReportIPage;
    }

    public List<MonthDTO> getMonthNum(String lineCode, String stationCode) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<MonthDTO> monthDTOS = patrolTaskMapper.selectMonth(sysUser.getId(), lineCode, stationCode);
        return monthDTOS;
    }
    public List<MonthDTO> getMonthOrgNum(String lineCode, String stationCode, String systemCode) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(user.getId());
        List<String> orgCodes = userSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        List<MonthDTO> monthDTOS = patrolTaskMapper.selectMonthOrg(orgCodes, lineCode, stationCode, systemCode);
        return monthDTOS;
    }

    public List<FailureOrgReport> getFailureOrgReport(String lineCode, List<String> stationCode, String startTime, String endTime, List<String> systemCode) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(user.getId());
        List<String> ids =userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());
        SimpleDateFormat mm= new SimpleDateFormat("yyyy-MM");
        if (ObjectUtil.isEmpty(startTime) && ObjectUtil.isEmpty(endTime)){
            startTime = mm.format(new Date())+"-01"; endTime = mm.format(new Date())+"-31";
        }
        if (ObjectUtil.isNotEmpty(lineCode)&& CollectionUtil.isEmpty(stationCode)){
            stationCode= this.selectStation(lineCode).stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
        }else if (ObjectUtil.isEmpty(lineCode)&& CollectionUtil.isEmpty(stationCode)){
            stationCode = this.selectStation(null).stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
        }
        if ( CollectionUtil.isEmpty(systemCode)){
            systemCode = this.selectSystem().stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
        }
        List<FailureOrgReport> orgReport = patrolTaskMapper.getOrgReport(ids,lineCode,stationCode,startTime,endTime,systemCode);
        String finalStartTime = startTime;
        String finalEndTime = endTime;
        List<String> finalStationCode = stationCode;
        orgReport.forEach(f -> {
            if (f.getLastMonthNum() != 0) {
                double sub = NumberUtil.sub(f.getMonthNum(), f.getLastMonthNum());
                BigDecimal div = NumberUtil.div(sub, NumberUtil.round(f.getLastMonthNum(), 2));
                f.setLastMonthStr(NumberUtil.round(NumberUtil.mul(div, 100), 2).toString() + "%");
            } else {
                f.setLastMonthStr(NumberUtil.mul(NumberUtil.round(f.getMonthNum(), 2), 100).toString() + "%");
            }
            if (f.getLastYearNum() != 0) {
                double sub = NumberUtil.sub(f.getYearNum(), f.getLastYearNum());
                BigDecimal div = NumberUtil.div(sub, NumberUtil.round(f.getLastYearNum(), 2));
                f.setLastMonthStr(NumberUtil.round(NumberUtil.mul(div, 100), 2).toString() + "%");
            } else {
                f.setLastYearStr(NumberUtil.mul(NumberUtil.round(f.getYearNum(), 2), 100).toString() + "%");
            }
            List<Integer> num = patrolTaskMapper.selectNum(null, f.getOrgCode(), lineCode, finalStationCode, finalStartTime, finalEndTime);
            int s = num.stream().reduce(Integer::sum).orElse(0);
            f.setAverageResponse(f.getResolvedNum() == 0 ? 0 : s / f.getResolvedNum());
            f.setAverageResponse(f.getResolvedNum() == 0 ? 0 : s / f.getResolvedNum());
            List<Integer> num1 = patrolTaskMapper.selectNum1(null, f.getOrgCode(), lineCode, finalStationCode, finalStartTime, finalEndTime);
            int s1 = num1.stream().reduce(Integer::sum).orElse(0);
            f.setAverageResolution(f.getResolvedNum() == 0 ? 0 : s1 / f.getResolvedNum());
        });
                  return orgReport;
            }

            /**
             * 子系统故障列表报表导出
             *
             * @param request
                 * @return
                 */
        public ModelAndView reportSystemExport (HttpServletRequest request, String lineCode, List < String > stationCode, String startTime, String endTime){
            ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
            List<FailureReport> failureReportList = this.getFailureReport(lineCode, stationCode, startTime, endTime);
            if (CollectionUtil.isNotEmpty(failureReportList)) {
                //导出文件名称
                mv.addObject(NormalExcelConstants.FILE_NAME, "子系统故障报表");
                //excel注解对象Class
                mv.addObject(NormalExcelConstants.CLASS, FailureReport.class);
                //自定义表格参数
                mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-子系统故障报表", "子系统故障报表"));
                //导出数据列表
                mv.addObject(NormalExcelConstants.DATA_LIST, failureReportList);
            }
            return mv;
        }
        /**
         * 班组故障列表报表导出
         *
         * @param request
         * @return
         */
        public ModelAndView reportOrgExport (HttpServletRequest request, String lineCode, List <String> stationCode, String startTime, String endTime, List < String > systemCode){
            ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
            List<FailureOrgReport> failureOrgReport = this.getFailureOrgReport(lineCode, stationCode, startTime, endTime, systemCode);
            if (CollectionUtil.isNotEmpty(failureOrgReport)) {
                //导出文件名称
                mv.addObject(NormalExcelConstants.FILE_NAME, "班组故障报表");
                //excel注解对象Class
                mv.addObject(NormalExcelConstants.CLASS, FailureOrgReport.class);
                //自定义表格参数
                mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-班组故障报表", "班组故障报表"));
                //导出数据列表
                mv.addObject(NormalExcelConstants.DATA_LIST, failureOrgReport);
            }
            return mv;
     }

        public List<LineOrStationDTO> selectStation (String lineCode){
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            List<LineOrStationDTO> station = patrolTaskMapper.selectStation(sysUser.getId(), lineCode);
            return station;
       }

        public List<LineOrStationDTO> selectLine () {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            List<LineOrStationDTO> line = patrolTaskMapper.selectLine(sysUser.getId());
            List<LineOrStationDTO> list = line.stream()
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(LineOrStationDTO::getCode))), ArrayList::new));
            return list;
       }
        public List<LineOrStationDTO> selectSystem () {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            List<LineOrStationDTO> system = patrolTaskMapper.selectSystem(sysUser.getId());
            return system;
       }

    public List<LineOrStationDTO> selectDepart () {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<LineOrStationDTO> lineOrStationDTOS = patrolTaskMapper.selectDepart(sysUser.getId());
        return lineOrStationDTOS;
    }
        }
