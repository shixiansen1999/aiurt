package com.aiurt.modules.worklog.task;

import cn.hutool.core.date.DateUtil;
import com.aiurt.common.api.dto.message.BusMessageDTO;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.worklog.dto.WorkLogJobDTO;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.service.IWorkLogRemindService;
import com.aiurt.modules.worklog.service.IWorkLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
public class WorkLogJobNight implements Job {
    @Resource
    private IWorkLogRemindService workLogRemindService;
    @Resource
    private IWorkLogService workLogService;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        WorkLogJobDTO dto = (WorkLogJobDTO)jobExecutionContext.getMergedJobDataMap().get("orgId");
        //根据部门id,获取部门下的当天上班的人员
        String dateNow= DateUtil.today();
         List<String> userName =workLogRemindService.getOrgUserTodayWork(dateNow,dto.getOrgId());
        if (CollectionUtils.isNotEmpty(userName)){
            //发消息提醒
            userName.forEach(
                    u->{
                        LoginUser loginUser = iSysBaseAPI.queryUser(u);
                        List<WorkLog> workLogList = workLogService.list(new LambdaQueryWrapper<WorkLog>().like(WorkLog::getSubmitTime, dateNow).eq(WorkLog::getSubmitId,loginUser.getId()));
                        if(workLogList.size()==0)
                        {
                            BusMessageDTO messageDTO = new BusMessageDTO();
                            messageDTO.setFromUser(dto.getFromUser());
                            messageDTO.setToUser(u);
                            messageDTO.setToAll(false);
                            messageDTO.setContent(dto.getContent());
                            messageDTO.setCategory("2");
                            messageDTO.setTitle("工作日志上报提醒");
                            messageDTO.setBusType(SysAnnmentTypeEnum.WORKLOG.getType());
                            iSysBaseAPI.sendBusAnnouncement(messageDTO);
                        }
                    }
            );
        }
    }
}
