package com.aiurt.modules.worklog.task;

import com.aiurt.common.api.dto.message.BusMessageDTO;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.worklog.dto.WorkLogJobDTO;
import com.aiurt.modules.worklog.service.IWorkLogService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
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
       boolean task = true;
//        if (!TaskStatusUtil.getTaskStatus()) {
//          return;
//       }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++定时任务开始");
        WorkLogJobDTO dto = (WorkLogJobDTO)jobExecutionContext.getMergedJobDataMap().get("orgId");
        //根据部门id,获取部门下的全部人员
        List<LoginUser> personnel = iSysBaseAPI.getUserPersonnel(dto.getOrgId());
        List<String> userIds = personnel.stream().map(LoginUser::getUsername).collect(Collectors.toList());
        // todo 后期修改
		//List<LoginUser> userList = userMapper.selectUserByTimeAndItemAndOrgId(DateUtils.getDate("yyyy-MM-dd"),"白",dto.getOrgId());
//        if (ObjectUtil.isEmpty(userList)){
//          return;
//        }
        //发消息提醒
//        WorkLog workLogList = workLogService.getOne(new LambdaQueryWrapper<WorkLog>().eq(WorkLog::getOrgId,dto.getOrgId()));
       // LoginUser userByName = iSysBaseAPI.getUserByName(workLogList.getCreateBy());
        //过滤工班长
      //  userIds.remove(userByName.getId());
        if (CollectionUtils.isNotEmpty(userIds)){
            //todo 待处理
                userIds.forEach(
                        u->{
                            BusMessageDTO messageDTO = new BusMessageDTO();
                           // messageDTO.setFromUser(workLogList.getCreateBy());
                            messageDTO.setFromUser(u);
                            messageDTO.setToUser(u);
                            messageDTO.setToAll(false);
                            messageDTO.setContent(dto.getContent().toString());
                            messageDTO.setCategory("2");
                            messageDTO.setTitle("测试定时任务（工作日志）");
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

