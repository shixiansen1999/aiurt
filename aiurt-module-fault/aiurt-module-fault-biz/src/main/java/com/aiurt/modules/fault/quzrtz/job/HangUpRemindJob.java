package com.aiurt.modules.fault.quzrtz.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : sbx
 * @Classname : HangUpRemindJob
 * @Description : 故障挂起超时未处理提醒定时任务
 * @Date : 2023/8/1 18:08
 */
@Component
@Slf4j
public class HangUpRemindJob implements Job {

    private String parameter;
    private static final int LEN = 3;

    @Autowired
    private IFaultService faultService;
    @Autowired
    private ISysParamAPI iSysParamApi;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IBaseApi baseApi;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            SysParamModel remindParam = iSysParamApi.selectByCode(SysParamCodeConstant.HANG_UP_REMIND);
            List<String> paramList = StrUtil.splitTrim(parameter, StrUtil.COMMA);
            DateTime updateTime = null;
            if (paramList.size() == LEN) {
                updateTime = DateUtil.parse(paramList.get(2), "yyyy-MM-dd HH:mm:ss");
            }
            Fault fault = faultService.getOne(new LambdaQueryWrapper<Fault>().eq(Fault::getCode, paramList.get(0)).eq(Fault::getStatus, paramList.get(1)).eq(ObjectUtil.isNotEmpty(updateTime), Fault::getUpdateTime, updateTime), false);
            // 判断配置开启否，故障还是挂起否
            boolean b = ObjectUtil.isNotEmpty(remindParam) && FaultConstant.ENABLE.equals(remindParam.getValue()) && ObjectUtil.isNotEmpty(fault);
            if (b) {
                // 任务执行逻辑
                // 获取今日当班人员
                Date now = new Date();
                List<SysUserTeamDTO> userList = baseApi.getTodayOndutyDetailNoPage(CollUtil.newArrayList(fault.getSysOrgCode()), now);
                List<String> collect = CollUtil.isNotEmpty(userList) ? userList.stream().map(SysUserTeamDTO::getUsername).distinct().collect(Collectors.toList()) : new ArrayList<>();
                // 获取故障所属班组的班组长
                String foreman = sysBaseApi.getUserNameByOrgCodeAndRoleCode(CollUtil.newArrayList(fault.getSysOrgCode()), CollUtil.newArrayList(RoleConstant.FOREMAN));
                List<String> foremanList = StrUtil.splitTrim(foreman, StrUtil.COMMA);
                for (String s : foremanList) {
                    if (!collect.contains(s)) {
                        collect.add(s);
                    }
                }
                // 发送消息给今日当班人员和班组长
                long between = DateUtil.between(updateTime, now, DateUnit.SECOND);
                String msg = "故障挂起已超过" + secondToTime(between);
                if (CollUtil.isNotEmpty(collect)) {
                    String content = "故障编号:" + fault.getCode() + "<br/>";
                    sendReminderMessage(now, CollUtil.join(collect, StrUtil.COMMA), msg, content);
                }
                log.info(DateUtil.formatDateTime(now) + "," + msg + ",故障编号:" + fault.getCode());
            } else {
                // 删除定时任务
                String jobId = context.getJobDetail().getKey().getName();
                QuartzJobDTO quartzJobDTO = sysBaseApi.getQuartzJobById(jobId);
                sysBaseApi.deleteAndStopJob(quartzJobDTO);
                log.info("定时任务已删除,任务id:" + jobId);
            }
        } catch (Exception e) {
            log.error("HangUpRemindJob Error found:", e);
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
     * @param date
     * @param toUser
     * @param msg
     * @param content
     */
    private void sendReminderMessage(Date date, String toUser, String msg, String content) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setToUser(toUser);
        messageDTO.setTitle(msg + DateUtil.format(date, "yyyy-MM-dd"));
        messageDTO.setContent(content);
        HashMap<String, Object> map = new HashMap<>(10);
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.HANG_UP_REMIND.getType());
        messageDTO.setData(map);
        messageDTO.setMsgAbstract(msg);
        messageDTO.setPublishingContent(msg);
        messageDTO.setIsRingBell(true);
        SysParamModel durationParam = iSysParamApi.selectByCode(SysParamCodeConstant.HUR_RING_DURATION);
        messageDTO.setRingDuration(ObjectUtil.isNotEmpty(durationParam) ? Integer.parseInt(StrUtil.trim(durationParam.getValue())) : 5);
        SysParamModel sysParamModel = iSysParamApi.selectByCode(SysParamCodeConstant.FAULT_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        messageDTO.setPriority("L");
        messageDTO.setStartTime(date);
        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_6);
        sysBaseApi.sendTemplateMessage(messageDTO);
    }
}
