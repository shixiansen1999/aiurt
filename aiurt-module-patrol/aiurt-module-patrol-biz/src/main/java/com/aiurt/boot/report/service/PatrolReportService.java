package com.aiurt.boot.report.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.report.model.PatrolReportModel;
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
import java.util.Calendar;
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
    private ISysBaseAPI iSysBaseAPI;
    public Page<PatrolReport> getTaskDate(Page<PatrolReport> pageList, PatrolReportModel report) {
        //根据当前用户id
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = iSysBaseAPI.getUserSysDepart(user.getId());
        List<String> orgList = userSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        Calendar cal = Calendar.getInstance();
        // 设置一个星期的第一天
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if(dayWeek==1){
            dayWeek = 8;
        }
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);
        Date mondayDate = cal.getTime();
        cal.add(Calendar.DATE, 4 +cal.getFirstDayOfWeek());
        Date sundayDate = cal.getTime();
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
                }
                 patrolReport.setCompletionRate(completionRate);
                 patrolReport.setAbnormalNumber(faultNumber);
                 patrolReport.setAbnormalNumber(abnormalNumber);
            }
        }
       return  pageList.setRecords(list);
    }
}
