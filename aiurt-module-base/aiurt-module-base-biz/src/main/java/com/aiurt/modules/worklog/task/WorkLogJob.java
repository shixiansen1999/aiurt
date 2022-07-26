package com.aiurt.modules.worklog.task;

import com.aiurt.common.api.dto.message.BusMessageDTO;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.common.util.TaskStatusUtil;
import com.aiurt.modules.worklog.dto.WorkLogJobDTO;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.service.IWorkLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
public class WorkLogJob implements Job {


//	@Resource
//	private SysUserMapper userMapper;

    @Resource
    private IWorkLogService workLogService;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    //todo 待处理
//    @Resource
//    private IMessageService messageService;
//
//    @Resource
//    private IMessageReadService messageReadService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (!TaskStatusUtil.getTaskStatus()) {
            return;
        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++定时任务开始");
        WorkLogJobDTO dto = (WorkLogJobDTO)jobExecutionContext.getMergedJobDataMap().get("orgId");
        LocalDate now = LocalDate.now();
        LocalDateTime startTime = now.atTime(0, 0, 0);
        LocalDateTime endTime = now.atTime(23, 59, 59);
        //根据部门id,获取部门下的全部人员
        List<LoginUser> personnel = iSysBaseAPI.getUserPersonnel(dto.getOrgId());
		List<String> userIds = personnel.stream().map(LoginUser::getId).collect(Collectors.toList());
        // todo 后期修改
		//List<LoginUser> userList = userMapper.selectUserByTimeAndItemAndOrgId(DateUtils.getDate("yyyy-MM-dd"),"白",dto.getOrgId());
       // if (ObjectUtil.isEmpty(userList)){
       //     return;
       // }
        //发消息提醒去掉创建人
        List<WorkLog> workLogList = workLogService.list(new LambdaQueryWrapper<WorkLog>()
                .eq(WorkLog::getDelFlag, CommonConstant.DEL_FLAG_0)
                .in(WorkLog::getCreateBy, userIds)
                .between(WorkLog::getCreateTime, startTime, endTime)
                .select(WorkLog::getCreateBy)
        );
        List<String> successIds = workLogList.stream().map(WorkLog::getCreateBy).collect(Collectors.toList());
        userIds.removeAll(successIds);
        if (CollectionUtils.isNotEmpty(userIds)){
            //todo 待处理
                userIds.forEach(
                        u->{
                            BusMessageDTO messageDTO = new BusMessageDTO();
                            messageDTO.setFromUser(u);
                            messageDTO.setToUser(u);
                            messageDTO.setToAll(false);
                            messageDTO.setContent(dto.getContent().toString());
                            messageDTO.setCategory("2");
                            messageDTO.setTitle("工作日志提醒");
                            messageDTO.setBusType(SysAnnmentTypeEnum.WORKLOG.getType());
                            iSysBaseAPI.sendBusAnnouncement(messageDTO);
//                            List<MessageRead> list = new ArrayList<>();
//                            MessageRead read = new MessageRead();
//                            read.setMessageId(message.getId()).setReadFlag(CommonConstant.DEL_FLAG_0).setStaffId(u).setDelFlag(CommonConstant.DEL_FLAG_0);
//                            list.add(read);
                        }
                );
               // messageReadService.saveBatch(list);

        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++定时任务结束");
    }
}

