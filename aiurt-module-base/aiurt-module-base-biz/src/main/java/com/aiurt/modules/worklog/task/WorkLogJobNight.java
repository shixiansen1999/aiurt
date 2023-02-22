package com.aiurt.modules.worklog.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.enums.MessageTypeEnum;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.mapper.ScheduleRecordMapper;
import com.aiurt.modules.worklog.dto.WorkLogJobDTO;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.service.IWorkLogRemindService;
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
import java.util.*;
import java.util.stream.Collectors;

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
    @Resource
    private ScheduleRecordMapper scheduleRecordMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        WorkLogJobDTO dto = (WorkLogJobDTO)jobExecutionContext.getMergedJobDataMap().get("orgId");
        String dateNow= DateUtil.today();
        //根据部门id,获取部门下的当天上班的人员
        List<ScheduleRecord> allUserList =workLogRemindService.getOrgUserTodayWork(dateNow,dto.getOrgId());
        //查询当天这个部门下的已提交工作日志的人员
        List<WorkLog> workLogList = workLogService.list(new LambdaQueryWrapper<WorkLog>().like(WorkLog::getSubmitTime, dateNow).eq(WorkLog::getOrgId,dto.getOrgId()));
        //去重（因为上报没有限制，人可以提交多次）
        List<WorkLog> distinctWorkLogList = workLogList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(WorkLog:: getSubmitId))), ArrayList::new));
        //拿到提交人的id集合
        List<String> distinctWorkLogUserIds = distinctWorkLogList.stream().map(WorkLog::getSubmitId).collect(Collectors.toList());
        //当天有人排班，并上报了
        if (CollUtil.isNotEmpty(distinctWorkLogUserIds))
        {
            //获取上报人所在的排班信息
            List<ScheduleRecord> workRecordList = scheduleRecordMapper.selectList(new LambdaQueryWrapper<ScheduleRecord>().in(ScheduleRecord::getUserId, distinctWorkLogUserIds).like(ScheduleRecord::getDate, dateNow).eq(ScheduleRecord::getDelFlag,0));
            List<Integer> workLogScheduleUserIds = workRecordList.stream().map(ScheduleRecord::getItemId).collect(Collectors.toList());
            //过滤数据，过滤不是当天上报同一组的人
            List<ScheduleRecord> notWorkLogUserList = allUserList.stream().filter(a -> !workLogScheduleUserIds.contains(a.getItemId()) ).collect(Collectors.toList());
            List<String> notWorkLogUserIds = notWorkLogUserList.stream().map(ScheduleRecord::getUserId).collect(Collectors.toList());
            String[] userIds = notWorkLogUserIds.toArray(new String[0]);
            List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(userIds);
            List<String> userName = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(userName)){
                //发消息提醒
                userName.forEach(
                        u->{
                            //发送通知
                            MessageDTO messageDTO = new MessageDTO(dto.getFromUser(), u, "工作日志上报" + DateUtil.today(), null, com.aiurt.common.constant.CommonConstant.MSG_CATEGORY_8);
                            //构建消息模板
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.WORKLOG.getType());

                            messageDTO.setType(MessageTypeEnum.XT.getType());
                            messageDTO.setMsgAbstract("工作日志上报");
                            messageDTO.setPublishingContent("今日工作日志未上报");
                            iSysBaseAPI.sendTemplateMessage(messageDTO);
                        }
                );
            }
        }
        //当天这个组织没有人提交工作日志
        if(CollUtil.isEmpty(workLogList))
        {
            //获取排班人的用户id
            List<String> notWorkLogUserIds = allUserList.stream().map(ScheduleRecord::getUserId).collect(Collectors.toList());
            String[] userIds = notWorkLogUserIds.toArray(new String[0]);
            List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(userIds);
            List<String> userName = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.toList());
            //当天有人排班，但没有提交工作日志，就发送消息
            if (CollectionUtils.isNotEmpty(userName)){
                //发消息提醒
                userName.forEach(
                        u->{
                            //发送通知
                            MessageDTO messageDTO = new MessageDTO(dto.getFromUser(), u, "工作日志上报" + DateUtil.today(), null, com.aiurt.common.constant.CommonConstant.MSG_CATEGORY_8);
                            //构建消息模板
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.WORKLOG.getType());

                            messageDTO.setType(MessageTypeEnum.XT.getType());
                            messageDTO.setMsgAbstract("工作日志上报");
                            messageDTO.setPublishingContent("今日工作日志未上报");
                            iSysBaseAPI.sendTemplateMessage(messageDTO);
                        }
                );
            }
        }

    }
}
