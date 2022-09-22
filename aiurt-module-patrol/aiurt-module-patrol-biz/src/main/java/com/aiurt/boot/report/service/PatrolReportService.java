package com.aiurt.boot.report.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.report.model.PatrolReportModel;
import com.aiurt.boot.screen.service.PatrolScreenService;
import com.aiurt.boot.screen.utils.ScreenDateUtil;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Date mondayDate = DateUtil.parse(thisWeek.split("~")[0]);
        Date sundayDate = DateUtil.parse(thisWeek.split("~")[1]);
        //默认本周
        if(ObjectUtil.isEmpty(report.getStartDate()))
        {
            report.setStartDate(mondayDate);
            report.setEndDate(sundayDate);
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
            for (PatrolReport d : reportList) {
                if(patrolReport.getOrgCode().equals(d.getOrgCode()))
                {
                    BeanUtils.copyProperties(d,patrolReport);
                }
            }
        }
        //获取漏巡的时间范围
        PatrolReportModel omitModel = new PatrolReportModel();
        //默认本周
        if(ObjectUtil.isEmpty(report.getStartDate()))
        {
            String date = ScreenDateUtil.getThisWeek(new Date());
            Date startTime = DateUtil.parse(date.split("~")[0]);
            Date endTime = DateUtil.parse(date.split("~")[1]);
            omitModel.setStartDate(startTime);
            omitModel.setEndDate(endTime);
        }
        else
        {
            String omitStartTime = screenService.getOmitDateScope(report.getStartDate()).split("~")[0];
            String omitEndTime = screenService.getOmitDateScope(report.getEndDate()).split("~")[1];
            Date startTime = DateUtil.parse(omitEndTime, "yyyy-MM-dd");
            Date endTime = DateUtil.parse(omitStartTime, "yyyy-MM-dd");
            omitModel.setStartDate(startTime);
            omitModel.setEndDate(endTime);
        }
        BeanUtils.copyProperties(report,omitModel);
        List<PatrolReport> omitList =patrolTaskMapper.getReportOmitList(omitModel);
        if(CollUtil.isNotEmpty(list)&&CollUtil.isNotEmpty(omitList))
        {
            for (PatrolReport patrolReport : list) {
                for (PatrolReport d : omitList) {
                    if(patrolReport.getOrgCode().equals(d.getOrgCode()))
                    {
                        patrolReport.setMissInspectedNumber(d.getMissInspectedNumber());
                    }
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
                         List<PatrolTaskDevice> taskDeviceList = patrolTaskDeviceMapper.getTaskAbnormal(taskId);
                         List<PatrolTaskDevice> faultList = patrolTaskDeviceMapper.getFaultList(taskId);
                         if(taskDeviceList.size()>0)
                         {
                             abnormalNumber=abnormalNumber+1;
                         }
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
       return  pageList.setRecords(list);
    }
    public static String getThisWeek(Date date) {
        DateTime start = DateUtil.beginOfWeek(date);
        DateTime end = DateUtil.endOfWeek(date);
        String thisWeek = DateUtil.format(start, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(end, "yyyy-MM-dd 23:59:59");
        return thisWeek;
    }

}
