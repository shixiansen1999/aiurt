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
        PatrolReportModel orgCodeName = new PatrolReportModel();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(user.getId());
        List<String> orgList = userSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        List<String> orgIdList = userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());
        //当前人无配置班组时，返回空列表
        if(CollUtil.isEmpty(orgList))
        {
            return  pageList.setRecords(new ArrayList<>());
        }
        else
        {
            report.setOrgCodeList(orgList);
            orgCodeName.setOrgIdList(orgIdList);
        }
        if(ObjectUtil.isNotEmpty(report.getLineCode()))
        {
            //查询该线路下，用户所拥有的站点code
             List<LineOrStationDTO> stationList = selectStation(report.getLineCode());
             List<String> stationCodeList = stationList.stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
             report.setStationCodeList(stationCodeList);
        }
        orgCodeName.setOrgCode(report.getOrgCode());
        PatrolReportModel omitModel = new PatrolReportModel();
        BeanUtils.copyProperties(report, omitModel);
        //是否默认查本周
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
        } else {

            boolean isNowWeek = isNowWeekDate(report.getStartDate(), report.getEndDate());
            isNullDate = isNowWeek;
            String start = screenService.getOmitDateScope(DateUtil.parse(report.getStartDate()));
            String end = screenService.getOmitDateScope(DateUtil.parse(report.getEndDate()));
            omitModel.setStartDate(start.split("~")[0]);
            omitModel.setEndDate(end.split("~")[1]);
        }
        //只查组织机构，做主数据返回，为了条件查询不影响组织机构显示
        List<PatrolReport> list = patrolTaskMapper.getReportTaskList(pageList, orgCodeName);
        //计算完成率、巡检总数、未完成、完成数、异常任务数
        List<PatrolReport> reportList = patrolTaskMapper.getTasks(report);
        //计算漏巡视数
        List<PatrolReport> omitList = patrolTaskMapper.getReportOmitList(omitModel);
        for (PatrolReport patrolReport : list) {
            //计算完成率、巡检总数、未完成、完成数、异常任务数
            if (CollUtil.isNotEmpty(reportList)) {
                for (PatrolReport d : reportList) {
                    Integer faultNumber = 0;
                    List<String> taskIds = Arrays.asList(d.getTaskId().split(","));
                    if (CollUtil.isNotEmpty(taskIds)) {
                        //计算故障数量
                        for (String taskId : taskIds) {
                            List<PatrolTaskDevice> faultList = patrolTaskDeviceMapper.getFaultList(taskId);
                            if (faultList.size() > 0) {
                                faultNumber = faultNumber + 1;
                            }
                        }
                    }
                   d.setFaultNumber(faultNumber);
                    if (patrolReport.getOrgCode().equals(d.getOrgCode())) {
                        BeanUtils.copyProperties(d, patrolReport);
                    }
                }
            } else {
                PatrolReport patrolReport1 = setZero(patrolReport);
                BeanUtils.copyProperties(patrolReport1, patrolReport);
            }
            //计算漏巡视数、计算平均周漏巡视数和平均每月漏数巡视数
            if (CollUtil.isNotEmpty(omitList)) {
                for (PatrolReport d : omitList) {
                    if (patrolReport.getOrgCode().equals(d.getOrgCode())) {
                        if (ObjectUtil.isNull(d.getMissInspectedNumber())||d.getMissInspectedNumber() == 0) {
                            patrolReport.setMissInspectedNumber(0);
                            patrolReport.setAwmPatrolNumber("-");
                            patrolReport.setAmmPatrolNumber("-");
                        } else {
                            //是否是默认，是，本周不算
                            if (isNullDate) {
                                patrolReport.setTaskId(d.getTaskId());
                                patrolReport.setMissInspectedNumber(d.getMissInspectedNumber());
                                patrolReport.setAwmPatrolNumber("-");
                                patrolReport.setAmmPatrolNumber("-");
                            } else {
                                //计算平均每周漏检数
                                long weekNumber = getWeekNumber(report.getStartDate(), report.getEndDate());
                                if (weekNumber == 0) {
                                    patrolReport.setTaskId(d.getTaskId());
                                    patrolReport.setMissInspectedNumber(d.getMissInspectedNumber());
                                    patrolReport.setAwmPatrolNumber("-");
                                } else {
                                    double avg = NumberUtil.div(d.getMissInspectedNumber() , weekNumber);
                                    BigDecimal b = new BigDecimal(avg);
                                    double fave = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    String completionRated = String.format("%.2f", fave);
                                    patrolReport.setTaskId(d.getTaskId());
                                    patrolReport.setMissInspectedNumber(d.getMissInspectedNumber());
                                    patrolReport.setAwmPatrolNumber(completionRated);
                                }
                                //计算平均每每月漏检数
                                long monthNumber = getMonthNumber(report.getStartDate(), report.getEndDate());
                                if (monthNumber == 0) {
                                    patrolReport.setAmmPatrolNumber("-");
                                } else {
                                    double avg = NumberUtil.div(d.getMissInspectedNumber() , monthNumber);
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
                patrolReport.setMissInspectedNumber(0);
                patrolReport.setAwmPatrolNumber("-");
                patrolReport.setAmmPatrolNumber("-");
            }
            if(ObjectUtil.isNull(patrolReport.getTaskId()))
            {
                patrolReport.setOrgCode(patrolReport.getOrgCode());
                patrolReport.setOrgName(patrolReport.getOrgName());
                patrolReport.setTaskTotal(0);
                patrolReport.setAbnormalNumber(0);
                patrolReport.setMissInspectedNumber(0);
                patrolReport.setCompletionRate("0.00");
                patrolReport.setAwmPatrolNumber("-");
                patrolReport.setAmmPatrolNumber("-");
                patrolReport.setFaultNumber(0);
                patrolReport.setInspectedNumber(0);
                patrolReport.setNotInspectedNumber(0);
            }
            if(ObjectUtil.isNull(patrolReport.getAwmPatrolNumber()))
            {  patrolReport.setAwmPatrolNumber("-");

            }
            if(ObjectUtil.isNull(patrolReport.getTaskTotal()))
            {
                patrolReport.setTaskTotal(0);
                patrolReport.setNotInspectedNumber(0);
                patrolReport.setInspectedNumber(0);
                patrolReport.setCompletionRate("0.00");
            }
            if(ObjectUtil.isNull(patrolReport.getAmmPatrolNumber()))
            {
                patrolReport.setAmmPatrolNumber("-");
            }
            if(ObjectUtil.isNull(patrolReport.getFaultNumber()))
            {
                patrolReport.setFaultNumber(0);
            }
            if(ObjectUtil.isNull(patrolReport.getAbnormalNumber()))
            {
                patrolReport.setAbnormalNumber(0);
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
        patrolReport.setCompletionRate("0.00");
        patrolReport.setAbnormalNumber(0);
        patrolReport.setMissInspectedNumber(0);
        patrolReport.setAwmPatrolNumber("-");
        patrolReport.setAmmPatrolNumber("-");
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

    public static boolean isNowWeekDate(String startDate,String endDate) {
        DateTime webStart = DateUtil.beginOfWeek(DateUtil.parse(startDate));
        DateTime webEnd = DateUtil.endOfWeek(DateUtil.parse(endDate));
        DateTime start = DateUtil.beginOfWeek(new Date());
        DateTime end = DateUtil.endOfWeek(new Date());
        boolean startIsSame = DateUtil.isSameDay(webStart, start);
        boolean endIsSame = DateUtil.isSameDay(webEnd, end);
        if(startIsSame==true&&endIsSame==true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public long getMonthNumber(String startDate, String endDate) {
        String today= DateUtil.today();
        Date s = DateUtil.parse(startDate, "yyyy-MM");
        Date e = DateUtil.parse(endDate, "yyyy-MM");
        Date n = DateUtil.parse(today, "yyyy-MM");
        Date start = DateUtil.parse(startDate, "yyyy-MM-dd");
        Date end = DateUtil.parse(endDate, "yyyy-MM-dd");
        int startMonth = DateUtil.month(start)+1;
        int endMonth = DateUtil.month(end)+1;
        //开始时间大于等于当前时间
        if(s.after(n)||s.equals(n))
        {
            return 0;
           // System.out.println("开始时间大于当前时间");
        }
        //开始时间小于当前时间
        else
        {
            //结束时间小于当前时间
            if(e.before(n))
            {
                int startYear = DateUtil.year(start);
                int endYear = DateUtil.year(end);
                //结束月份大于开始月份
                if(endYear>startYear)
                {

                    int monthNumber = 12-startMonth+1+endMonth;
                    return monthNumber;
                }
                //结束月份大于等于开始月份
                else
                {
                    int monthNumber = endMonth-startMonth+1;
                    return monthNumber;
                }

            }
            //结束时间大于等于当前时间
            if(e.equals(n)||e.after(n))
            {
                int startYear = DateUtil.year(start);
                int endYear = DateUtil.year(end);
                DateTime dateTime = DateUtil.lastMonth();
                int lastMonth = DateUtil.month(dateTime)+1;
                //结束年份大于开始年份
                if(endYear>startYear) {
                    int monthNumber = lastMonth+12-startMonth+1;
                    return monthNumber;
                }
                //结束年份小于等于开始年份
                else
                {
                    int monthNumber = lastMonth-startMonth+1;
                    return monthNumber;
                }
            }
        }
        return 0;
    }

    public long getWeekNumber(String start, String end) {
         long weekNumber = PatrolDateUtils.countTwoDayWeek(start, end);
         boolean sameWeek = isNowWeekDate(start,end);
        if(sameWeek)
         {
             weekNumber=weekNumber-1;
         }
        return weekNumber;
    }

    public ModelAndView reportExport(HttpServletRequest request, PatrolReportModel reportReqVO) {

        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        Page<PatrolReport> page = new Page<PatrolReport>(1, 9999);
        IPage<PatrolReport> report = this.getTaskDate(page,reportReqVO);
        List<PatrolReport> failureReports = report.getRecords();
        if (CollectionUtil.isNotEmpty(failureReports)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "巡视报表");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, PatrolReport.class);
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-巡视报表", "巡视报表"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, failureReports);
        }
        return mv;
    }
     public IPage<FailureReport> getFailureReport(Page<FailureReport>page,String lineCode, List<String> stationCode, String startTime, String endTime) {
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
        IPage<FailureReport> failureReportIPage = patrolTaskMapper.getFailureReport(page,sysUser.getId(), lineCode, stationCode, startTime, endTime);
        String finalStartTime = startTime;
        String finalEndTime = endTime;
        List<String> finalStationCode = stationCode;
        failureReportIPage.getRecords().forEach(f -> {
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
        return failureReportIPage;
    }

    public List<MonthDTO> getMonthNum(String lineCode, List<String> stationCode) {
        if (ObjectUtil.isNotEmpty(lineCode)&& CollectionUtil.isEmpty(stationCode)){
            stationCode= this.selectStation(lineCode).stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
        }else if (ObjectUtil.isEmpty(lineCode)&& CollectionUtil.isEmpty(stationCode)){
            stationCode = this.selectStation(null).stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<MonthDTO> monthDTOS = patrolTaskMapper.selectMonth(sysUser.getId(), lineCode, stationCode);
        return monthDTOS;
    }
    public List<MonthDTO> getMonthOrgNum(String lineCode, List<String> stationCode, List<String> systemCode) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(user.getId());
        List<String> orgCodes = userSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(lineCode)&& CollectionUtil.isEmpty(stationCode)){
            stationCode= this.selectStation(lineCode).stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
        }else if (ObjectUtil.isEmpty(lineCode)&& CollectionUtil.isEmpty(stationCode)){
            stationCode = this.selectStation(null).stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
        }
        if ( CollectionUtil.isEmpty(systemCode)){
            systemCode = this.selectSystem().stream().map(LineOrStationDTO::getCode).collect(Collectors.toList());
        }
        List<MonthDTO> monthDTOS = patrolTaskMapper.selectMonthOrg(orgCodes, lineCode, stationCode, systemCode);
        return monthDTOS;
    }

    public IPage<FailureOrgReport> getFailureOrgReport(Page<FailureOrgReport> page,String lineCode, List<String> stationCode, String startTime, String endTime, List<String> systemCode) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(user.getId());
        List<String> ids =userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)){
            return null;
        }
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
        IPage<FailureOrgReport> orgReport = patrolTaskMapper.getOrgReport(page,ids,lineCode,stationCode,startTime,endTime,systemCode);
        String finalStartTime = startTime;
        String finalEndTime = endTime;
        List<String> finalStationCode = stationCode;
        orgReport.getRecords().forEach(f -> {
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
            Page<FailureReport> page = new Page<FailureReport>(1, 9999);
            IPage<FailureReport> failureReportList = this.getFailureReport(page,lineCode, stationCode, startTime, endTime);
            List<FailureReport> failureReports = failureReportList.getRecords();
            if (CollectionUtil.isNotEmpty(failureReports)) {
                //导出文件名称
                mv.addObject(NormalExcelConstants.FILE_NAME, "子系统故障报表");
                //excel注解对象Class
                mv.addObject(NormalExcelConstants.CLASS, FailureReport.class);
                //自定义表格参数
                mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-子系统故障报表", "子系统故障报表"));
                //导出数据列表
                mv.addObject(NormalExcelConstants.DATA_LIST, failureReports);
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
            Page<FailureOrgReport> page = new Page<FailureOrgReport>(1, 9999);
            IPage<FailureOrgReport> failureOrgReport = this.getFailureOrgReport(page,lineCode, stationCode, startTime, endTime, systemCode);
            List<FailureOrgReport> failureOrgReports = failureOrgReport.getRecords();
            if (CollectionUtil.isNotEmpty(failureOrgReports)) {
                //导出文件名称
                mv.addObject(NormalExcelConstants.FILE_NAME, "班组故障报表");
                //excel注解对象Class
                mv.addObject(NormalExcelConstants.CLASS, FailureOrgReport.class);
                //自定义表格参数
                mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-班组故障报表", "班组故障报表"));
                //导出数据列表
                mv.addObject(NormalExcelConstants.DATA_LIST, failureOrgReports);
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
        //获取自己及管辖的下的班组
        if (CollUtil.isEmpty(lineOrStationDTOS)) {
            return CollUtil.newArrayList();
        } else {
            List<LineOrStationDTO> list = new ArrayList<>();
            for (LineOrStationDTO model : lineOrStationDTOS) {
                if (model.getOrgCategory().equals("3") || model.getOrgCategory().equals("4") || model.getOrgCategory().equals("5")) {
                    list.add(model);
                    List<LineOrStationDTO> models = patrolTaskMapper.getUserOrgCategory(model.getCode());
                    if (CollUtil.isNotEmpty(models)) {
                        list.addAll(models);
                    }
                } else {
                    List<LineOrStationDTO> models = patrolTaskMapper.getUserOrgCategory(model.getCode());
                    if (CollUtil.isNotEmpty(models)) {
                        list.addAll(models);
                    }
                }
            }
            if (CollUtil.isEmpty(list)) {
                return CollUtil.newArrayList();
            } else {
                return list;
            }
        }
    }
}
