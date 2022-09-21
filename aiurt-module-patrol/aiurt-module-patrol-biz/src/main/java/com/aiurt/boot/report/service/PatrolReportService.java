package com.aiurt.boot.report.service;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.report.model.PatrolReportModel;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        model.setStationCode(null);
        model.setLineCode(null);
        model.setSubsystemCode(null);
        List<PatrolReport> list = patrolTaskMapper.getReportTaskList(pageList,model);
        List<PatrolReport> reportList =patrolTaskMapper.getReportTaskList(pageList,report);

       return  pageList.setRecords(list);
    }
}
