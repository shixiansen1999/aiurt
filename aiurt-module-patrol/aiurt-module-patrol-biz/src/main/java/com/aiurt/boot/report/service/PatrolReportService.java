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
import com.aiurt.boot.report.model.dto.MonthDTO;
import com.aiurt.boot.screen.service.PatrolScreenService;
import com.aiurt.boot.screen.utils.ScreenDateUtil;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
        //根据当前用户id
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(user.getId());
        List<String> orgList = userSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
         String thisWeek = getThisWeek(new Date());
        String mondayDate = thisWeek.split("~")[0];
        String sundayDate = thisWeek.split("~")[1];
        //默认本周
        PatrolReportModel omitModel = new PatrolReportModel();
        if(ObjectUtil.isEmpty(report.getStartDate()))
        {
            report.setStartDate(mondayDate);
            report.setEndDate(sundayDate);
            String date = ScreenDateUtil.getThisWeek(new Date());
            omitModel.setStartDate(date.split("~")[0]);
            omitModel.setEndDate(date.split("~")[1]);
        }
        else
        {
           ;
            String omitStartTime = screenService.getOmitDateScope( DateUtil.parse(report.getStartDate())).split("~")[0];
            String omitEndTime = screenService.getOmitDateScope(DateUtil.parse(report.getEndDate())).split("~")[1];
            omitModel.setStartDate(omitStartTime);
            omitModel.setEndDate(omitEndTime);
        }
        report.setOrgList(orgList);
        PatrolReportModel model = new PatrolReportModel();
        BeanUtils.copyProperties(report,model);
        model.setLineCode(null);
        model.setStationCode(null);
        model.setSubsystemCode(null);
        List<PatrolReport> list = patrolTaskMapper.getReportTaskList(pageList,model);
        List<PatrolReport> reportList =patrolTaskMapper.getReportTaskList(pageList,report);
        for (PatrolReport patrolReport : list) {
            if(CollUtil.isNotEmpty(reportList))
            {
                for (PatrolReport d : reportList) {
                    if(patrolReport.getOrgCode().equals(d.getOrgCode()))
                    {
                        BeanUtils.copyProperties(d,patrolReport);
                    }
                }
            }
          else
            {
                PatrolReport patrolReport1 = setZero(patrolReport);
                BeanUtils.copyProperties(patrolReport1,patrolReport);
            }
        }
        BeanUtils.copyProperties(report,omitModel);
        List<PatrolReport> omitList =patrolTaskMapper.getReportOmitList(omitModel);
        if(CollUtil.isNotEmpty(list)&&CollUtil.isNotEmpty(omitList))
        {
            for (PatrolReport patrolReport : list) {
                if(CollUtil.isNotEmpty(omitList))
                {

                    for (PatrolReport d : omitList) {
                        if(patrolReport.getOrgCode().equals(d.getOrgCode()))
                        {
                            patrolReport.setMissInspectedNumber(d.getMissInspectedNumber());
                        }
                    }
                }
                else
                {
                    PatrolReport patrolReport1 = setZero(patrolReport);
                    BeanUtils.copyProperties(patrolReport1,patrolReport);
                }
            }
        }
        Integer abnormalNumber = 0;
        Integer faultNumber = 0;
        String completionRate = String.format("%.2f", 0F);
        if(CollUtil.isNotEmpty(list))
        {
            for(PatrolReport patrolReport : list)
            {
                 List<String> taskIds = Arrays.asList(patrolReport.getTaskId().split(","));
                 if(CollUtil.isNotEmpty(taskIds))
                 {
                     for (String  taskId: taskIds) {
                         List<PatrolTaskDevice> faultList = patrolTaskDeviceMapper.getFaultList(taskId);
                         if(faultList.size()>0)
                         {
                             faultNumber=faultNumber+1;
                         }
                     }
                 }

                if (patrolReport.getTaskTotal() != 0 && patrolReport.getInspectedNumber() != 0) {
                    // 完成率=已完成数除以总数X100%
                    double rate = (1.0 * patrolReport.getInspectedNumber() / patrolReport.getTaskTotal()) * 100;
                    completionRate = String.format("%.2f", rate);
                    patrolReport.setCompletionRate(completionRate+"%");
                }
                else
                {
                    patrolReport.setCompletionRate("0.00%");
                }
                 patrolReport.setAbnormalNumber(faultNumber);
                 patrolReport.setAbnormalNumber(abnormalNumber);
            }
        }
        //时间为空，默认前四周
        // 平均每周漏巡视数
        if(ObjectUtil.isEmpty(report.getStartDate()))
        {
            Date start = DateUtil.beginOfWeek(new Date());
            DateTime end = DateUtil.endOfWeek(new Date());
            for (int i = 0; i <4 ; i++)
            {
                Date date  = DateUtil.offsetDay(start, -7);
                start=date;

            }
            DateTime lastSunday = DateUtil.offsetDay(end, -7);
            report.setStartDate( DateUtil.formatDate(start));
            report.setEndDate( DateUtil.formatDate(lastSunday));
            List<PatrolReport> avgWeekOmitList =patrolTaskMapper.getReportOmitList(omitModel);
            if(CollUtil.isNotEmpty(list))
            {
                for (PatrolReport patrolReport : list) {
                    if(CollUtil.isNotEmpty(avgWeekOmitList))
                    {

                        for (PatrolReport d : avgWeekOmitList) {
                            if(patrolReport.getOrgCode().equals(d.getOrgCode()))
                            {
                                if(patrolReport.getMissInspectedNumber()==0)
                                {
                                    patrolReport.setAwmPatrolNumber("0.00");
                                }
                                else
                                {
                                    double avg = patrolReport.getMissInspectedNumber()/ 4;
                                    String completionRated = String.format("%.2f", avg);
                                    patrolReport.setAwmPatrolNumber(completionRated);
                                }

                            }
                        }
                    }
                    else
                    {
                         PatrolReport patrolReport1 = setZero(patrolReport);
                         BeanUtils.copyProperties(patrolReport1,patrolReport);
                    }

                }
            }
        }
       return  pageList.setRecords(list);
    }
    public PatrolReport setZero(PatrolReport report)
    {
        PatrolReport patrolReport = new PatrolReport();
        patrolReport.setTaskId(report.getTaskId());
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

    public ModelAndView reportExport(HttpServletRequest request, PatrolReportModel reportReqVO) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //获取数据
        Page<PatrolReport> page = new Page<>(reportReqVO.getPageNo(), reportReqVO.getPageSize());
        IPage<PatrolReport> report = this.getTaskDate(page, reportReqVO);
        List<PatrolReport> reportData = report.getRecords();
        HSSFWorkbook workbook = null;
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
    public List<FailureReport> getFailureReport(String lineCode,String stationCode,String startTime,String endTime) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SimpleDateFormat mm= new SimpleDateFormat("yyyy-MM");
        if (ObjectUtil.isEmpty(startTime) && ObjectUtil.isEmpty(endTime)){
            startTime = mm.format(new Date())+"-01"; endTime = mm.format(new Date())+"-31";
        }
        List<FailureReport> failureReportIPage = patrolTaskMapper.getFailureReport(sysUser.getId(),lineCode,stationCode,startTime,endTime);
        String finalStartTime = startTime;
        String finalEndTime = endTime;
        failureReportIPage.forEach(f->{
            if (f.getLastMonthNum()!=0){
            double sub = NumberUtil.sub(f.getMonthNum(), f.getLastMonthNum());
            BigDecimal div = NumberUtil.div(sub,NumberUtil.round(f.getLastMonthNum(),2));
            f.setLastMonthStr(NumberUtil.round(NumberUtil.mul(div,100),2).toString()+"%");
            }else {
                f.setLastMonthStr(NumberUtil.mul(NumberUtil.round(f.getMonthNum(),2),100).toString()+"%");
            }
            if (f.getLastYearNum()!=0){
                double sub = NumberUtil.sub(f.getYearNum(), f.getLastYearNum());
                BigDecimal div = NumberUtil.div(sub,NumberUtil.round(f.getLastYearNum(),2));
                f.setLastMonthStr(NumberUtil.round(NumberUtil.mul(div,100),2).toString()+"%");
            }else {
                f.setLastYearStr(NumberUtil.mul(NumberUtil.round(f.getYearNum(),2),100).toString()+"%");
            }
            List<Integer> num = patrolTaskMapper.selectNum(f.getCode(),null,lineCode,stationCode, finalStartTime, finalEndTime);
            int s = num.stream().reduce(Integer::sum).orElse(0);
            f.setAverageResponse(f.getResolvedNum()==0?0:s/f.getResolvedNum());
            List<Integer> num1 = patrolTaskMapper.selectNum1(f.getCode(),null,lineCode,stationCode, finalStartTime, finalEndTime);
            int s1 = num1.stream().reduce(Integer::sum).orElse(0);
            f.setAverageResolution(f.getResolvedNum()==0?0:s1/f.getResolvedNum());
        });
        return failureReportIPage;
    }

    public List<MonthDTO> getMonthNum(String lineCode, String stationCode) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<MonthDTO> monthDTOS = patrolTaskMapper.selectMonth(sysUser.getId(),lineCode,stationCode);
        return monthDTOS;
    }
    public List<MonthDTO> getMonthOrgNum(String lineCode, String stationCode,String systemCode) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(user.getId());
        List<String> orgCodes =userSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        List<MonthDTO> monthDTOS = patrolTaskMapper.selectMonthOrg(orgCodes,lineCode,stationCode,systemCode);
        return monthDTOS;
    }

    public List<FailureOrgReport> getFailureOrgReport(String lineCode, String stationCode, String startTime, String endTime, String systemCode) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(user.getId());
        List<String> ids =userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());
        List<FailureOrgReport> orgReport = patrolTaskMapper.getOrgReport(ids,lineCode,stationCode,startTime,endTime,systemCode);
        String finalStartTime = startTime;
        String finalEndTime = endTime;
        orgReport.forEach(f->{
            if (f.getLastMonthNum()!=0){
                double sub = NumberUtil.sub(f.getMonthNum(), f.getLastMonthNum());
                BigDecimal div = NumberUtil.div(sub,NumberUtil.round(f.getLastMonthNum(),2));
                f.setLastMonthStr(NumberUtil.round(NumberUtil.mul(div,100),2).toString()+"%");
            }else {
                f.setLastMonthStr(NumberUtil.mul(NumberUtil.round(f.getMonthNum(),2),100).toString()+"%");
            }
            if (f.getLastYearNum()!=0){
                double sub = NumberUtil.sub(f.getYearNum(), f.getLastYearNum());
                BigDecimal div = NumberUtil.div(sub,NumberUtil.round(f.getLastYearNum(),2));
                f.setLastMonthStr(NumberUtil.round(NumberUtil.mul(div,100),2).toString()+"%");
            }else {
                f.setLastYearStr(NumberUtil.mul(NumberUtil.round(f.getYearNum(),2),100).toString()+"%");
            }
            List<Integer> num = patrolTaskMapper.selectNum(null,f.getOrgCode(),lineCode,stationCode, finalStartTime, finalEndTime);
            int s = num.stream().reduce(Integer::sum).orElse(0);
            f.setAverageResponse(f.getResolvedNum()==0?0:s/f.getResolvedNum());
            List<Integer> num1 = patrolTaskMapper.selectNum1(null,f.getOrgCode(),lineCode,stationCode, finalStartTime, finalEndTime);
            int s1 = num1.stream().reduce(Integer::sum).orElse(0);
            f.setAverageResolution(f.getResolvedNum()==0?0:s1/f.getResolvedNum());
        });
        return orgReport;
    }

    /**
     * 子系统故障列表报表导出
     *
     * @param request
     * @return
     */
    public ModelAndView reportSystemExport(HttpServletRequest request, String lineCode, String stationCode, String startTime, String endTime) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<FailureReport> failureReportList = this.getFailureReport(lineCode,stationCode,startTime,endTime);
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
    public ModelAndView reportOrgExport(HttpServletRequest request, String lineCode, String stationCode, String startTime, String endTime, String systemCode) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<FailureOrgReport> failureOrgReport = this.getFailureOrgReport(lineCode,stationCode,startTime,endTime,systemCode);
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
}
