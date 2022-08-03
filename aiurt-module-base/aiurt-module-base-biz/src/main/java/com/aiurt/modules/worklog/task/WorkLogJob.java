package com.aiurt.modules.worklog.task;

import com.aiurt.common.api.dto.message.BusMessageDTO;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.worklog.dto.WorkLogJobDTO;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
public class WorkLogJob implements Job {
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++定时任务开始");
        WorkLogJobDTO dto = (WorkLogJobDTO)jobExecutionContext.getMergedJobDataMap().get("orgId");
        //根据部门id,获取部门下的全部人员
        List<LoginUser> personnel = iSysBaseAPI.getUserPersonnel(dto.getOrgId());
        List<String> userIds = personnel.stream().map(LoginUser::getUsername).collect(Collectors.toList());
        LoginUser user = iSysBaseAPI.queryUser(dto.getFromUser());
        // todo 后期修改
        //过滤工班长
        List<String> list = userIds.stream().filter(e -> !e.equals((user.getId()))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(userIds)){
            //todo 待处理
            //发消息提醒
            list.forEach(
                        u->{
                            BusMessageDTO messageDTO = new BusMessageDTO();
                            messageDTO.setFromUser(dto.getFromUser());
                            messageDTO.setToUser(u);
                            messageDTO.setToAll(false);
                            messageDTO.setContent(dto.getContent());
                            messageDTO.setCategory("2");
                            messageDTO.setTitle("测试定时任务（工作日志）");
                            messageDTO.setBusType(SysAnnmentTypeEnum.WORKLOG.getType());
                            iSysBaseAPI.sendBusAnnouncement(messageDTO);
                        }
                );
        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++定时任务结束");
    }
}

