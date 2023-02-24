package com.aiurt.modules.worklog.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
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
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
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
public class WorkLogJob implements Job {
    @Resource
    private IWorkLogRemindService workLogRemindService;
    @Resource
    private IWorkLogService workLogService;
    @Resource
    private ScheduleRecordMapper scheduleRecordMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        WorkLogJobDTO dto = (WorkLogJobDTO)jobExecutionContext.getMergedJobDataMap().get("orgId");
        String dateNow= DateUtil.today();
        //根据部门id,获取部门下的当天上班的人员
        List<ScheduleRecord> allUserList =workLogRemindService.getOrgUserTodayWork(dateNow,dto.getOrgId());
        //查询当天这个部门下的上报人员信息
        List<WorkLog> workLogList = workLogService.list(new LambdaQueryWrapper<WorkLog>().like(WorkLog::getSubmitTime, dateNow).eq(WorkLog::getOrgId,dto.getOrgId()).eq(WorkLog::getDelFlag,0));
        //去重
        List<WorkLog> distinctWorkLogList = workLogList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(WorkLog:: getSubmitId))), ArrayList::new));
        List<String> distinctWorkLogUserIds = distinctWorkLogList.stream().map(WorkLog::getSubmitId).collect(Collectors.toList());
        //上报人所在的排班信息
        if (CollUtil.isNotEmpty(distinctWorkLogUserIds))
        {
            List<ScheduleRecord> workRecordList = scheduleRecordMapper.selectList(new LambdaQueryWrapper<ScheduleRecord>().in(ScheduleRecord::getUserId, distinctWorkLogUserIds).like(ScheduleRecord::getDate, dateNow).eq(ScheduleRecord::getDelFlag,0));
            List<Integer> workLogScheduleUserIds = workRecordList.stream().map(ScheduleRecord::getItemId).collect(Collectors.toList());
            //过滤数据，过滤不是当天上报同一组班次的人
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
                            try {
                                MessageDTO messageDTO = new MessageDTO(dto.getFromUser(), u, "工作日志上报" + DateUtil.today(), null, com.aiurt.common.constant.CommonConstant.MSG_CATEGORY_8);
                                //构建消息模板
                                HashMap<String, Object> map = new HashMap<>();
                                map.put(CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.WORKLOG.getType());
                                map.put("msgContent", dto.getContent());
                                messageDTO.setData(map);
                                messageDTO.setTemplateCode(com.aiurt.common.constant.CommonConstant.WORK_LOG_SERVICE_NOTICE);
                                SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.WORK_LOG_MESSAGE);
                                messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                                messageDTO.setMsgAbstract("工作日志上报");
                                messageDTO.setPublishingContent("今日工作日志未上报");
                                iSysBaseAPI.sendTemplateMessage(messageDTO);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
            }
        }
        if(CollUtil.isEmpty(workLogList))
        {
            //获取排班人的用户id
            List<String> notWorkLogUserIds = allUserList.stream().map(ScheduleRecord::getUserId).collect(Collectors.toList());
            String[] userIds = notWorkLogUserIds.toArray(new String[0]);
            List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(userIds);
            List<String> userName = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(userName)){
                //发消息提醒
                userName.forEach(
                        u->{
                            //发送通知
                            try {
                                MessageDTO messageDTO = new MessageDTO(dto.getFromUser(), u, "工作日志上报" + DateUtil.today(), null, com.aiurt.common.constant.CommonConstant.MSG_CATEGORY_8);
                                //构建消息模板
                                HashMap<String, Object> map = new HashMap<>();
                                map.put(CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.WORKLOG.getType());
                                map.put("msgContent", dto.getContent());
                                messageDTO.setData(map);
                                messageDTO.setTemplateCode(com.aiurt.common.constant.CommonConstant.WORK_LOG_SERVICE_NOTICE);
                                SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.WORK_LOG_MESSAGE);
                                messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                                messageDTO.setMsgAbstract("工作日志上报");
                                messageDTO.setPublishingContent("今日工作日志未上报");
                                iSysBaseAPI.sendTemplateMessage(messageDTO);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
            }
        }

    }
}

