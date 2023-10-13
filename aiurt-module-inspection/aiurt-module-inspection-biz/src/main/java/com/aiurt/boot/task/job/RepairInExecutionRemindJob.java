package com.aiurt.boot.task.job;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.mapper.RepairTaskUserMapper;
import com.aiurt.boot.task.service.IRepairTaskService;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.SysParamModel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author : sbx
 * @description : 维保任务执行中延时提醒任务
 * @date : 2023/9/26 14:20
 */
@Component
@Slf4j
public class RepairInExecutionRemindJob implements Job {

    private String parameter;
    private static final int LEN = 3;

    @Autowired
    private IRepairTaskService repairTaskService;
    @Autowired
    private RepairTaskUserMapper repairTaskUserMapper;
    @Autowired
    private ISysParamAPI iSysParamApi;
    @Autowired
    private ISysBaseAPI sysBaseApi;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            SysParamModel remindParam = iSysParamApi.selectByCode(SysParamCodeConstant.HANG_UP_REMIND);
            List<String> paramList = StrUtil.splitTrim(parameter, StrUtil.COMMA);
            Date updateTime = null;
            if (paramList.size() == LEN) {
                updateTime = DateUtil.parse(paramList.get(2), "yyyy-MM-dd HH:mm:ss");
            }
            RepairTask repairTask = repairTaskService.getOne(new LambdaQueryWrapper<RepairTask>()
                    .eq(RepairTask::getCode, paramList.get(0))
                    .eq(RepairTask::getStatus, paramList.get(1))
                    .eq(RepairTask::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(ObjectUtil.isNotNull(updateTime), RepairTask::getUpdateTime, updateTime), false);
            //判断配置是否开启，检修单状态是否还是执行中
            boolean b = ObjectUtil.isNotEmpty(remindParam) && "1".equals(remindParam.getValue()) && ObjectUtil.isNotEmpty(repairTask);
            if (b) {
                Date now = new Date();
                //获取检修单检修人
                String toUser = repairTaskUserMapper.selectUserNameByRepairTaskCode(repairTask.getCode());
                //如果updateTime为null则取createTime来计算
                if (ObjectUtil.isNull(updateTime)) {
                    updateTime = repairTask.getCreateTime();
                }
                long between = DateUtil.between(updateTime, now, DateUnit.SECOND);
                String msg = repairTask.getCode() + "此维保任务已进行" + secondToTime(between);
                if (StrUtil.isNotBlank(toUser)) {
                    sendReminderMessage(now, toUser, msg, null);
                }
                log.info(DateUtil.formatDateTime(now) + "," + msg);
            } else {
                // 删除定时任务
                String jobId = context.getJobDetail().getKey().getName();
                QuartzJobDTO quartzJobDTO = sysBaseApi.getQuartzJobById(jobId);
                sysBaseApi.deleteAndStopJob(quartzJobDTO);
                log.info("定时任务已删除,任务id:" + jobId);
            }
        } catch (Exception e) {
            log.error("RepairInExecutionRemindJob Error found:", e);
            throw e;
        }
    }

    /**
     * 秒数转换为时间格式(h小时m分钟s秒)
     * @param seconds 需要转换的秒数
     * @return 转换后的字符串
     */
    public static String secondToTime(long seconds) {
        if (seconds <= 0) {
            return "0秒";
        }
        long hour = seconds / 3600L;
        long other = seconds % 3600L;
        long minute = other / 60L;
        long second = other % 60L;
        final StringBuilder sb = new StringBuilder();
        if (hour > 0) {
            sb.append(hour);
            sb.append("小时");
        }
        if (minute > 0) {
            sb.append(minute);
            sb.append("分钟");
        }
        if (second > 0) {
            sb.append(second);
            sb.append("秒");
        }
        return sb.toString();
    }

    /**
     * 发送消息
     * @param date 消息发送时间
     * @param toUser 接收消息用户
     * @param msg 消息标题
     * @param content 消息内容
     */
    private void sendReminderMessage(Date date, String toUser, String msg, String content) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setToUser(toUser);
        messageDTO.setTitle(msg + DateUtil.format(date, "yyyy-MM-dd"));
        messageDTO.setContent(content);
        HashMap<String, Object> map = new HashMap<>(10);
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.REPAIR_IN_EXECUTION_REMIND.getType());
        messageDTO.setData(map);
        messageDTO.setMsgAbstract(msg);
        messageDTO.setPublishingContent(msg);
        messageDTO.setIsRingBell(true);
        SysParamModel durationParam = iSysParamApi.selectByCode(SysParamCodeConstant.HUR_RING_DURATION);
        messageDTO.setRingDuration(ObjectUtil.isNotEmpty(durationParam) ? Integer.parseInt(StrUtil.trim(durationParam.getValue())) : 5);
        SysParamModel sysParamModel = iSysParamApi.selectByCode(SysParamCodeConstant.REPAIR_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        messageDTO.setPriority("L");
        messageDTO.setStartTime(date);
        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_5);
        sysBaseApi.sendTemplateMessage(messageDTO);
    }
}
