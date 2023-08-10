package com.aiurt.modules.fault.quzrtz.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : sbx
 * @Classname : NoReceiveRemindJob
 * @Description : 故障超时未领取提醒定时任务
 * @Date : 2023/8/8 15:44
 */
@Component
@Slf4j
public class NoReceiveRemindJob implements Job {

    private String parameter;

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
            SysParamModel remindParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_RECEIVE_FAULT_REMIND);
            Fault fault = faultService.getOne(new LambdaQueryWrapper<Fault>().eq(Fault::getCode, parameter), false);
            // 判断配置开启否，故障领取或接收否
            boolean b = ObjectUtil.isNotEmpty(remindParam) && FaultConstant.ENABLE.equals(remindParam.getValue()) && ObjectUtil.isNotEmpty(fault) && !checkIfSomeoneClaimedFault(fault);
            if (b) {
                // 任务执行逻辑
                Date now = new Date();
                // 获取故障所在班组的今日当班人员,并发送消息给今日当班人员
                List<SysUserTeamDTO> userList = baseApi.getTodayOndutyDetailNoPage(CollUtil.newArrayList(fault.getSysOrgCode()), now);
                String msg = "有新的故障发生,请及时查看";
                if (CollUtil.isNotEmpty(userList)) {
                    List<String> collect = userList.stream().map(SysUserTeamDTO::getUsername).distinct().collect(Collectors.toList());
                    String content = "故障编号:" + fault.getCode() + "<br/>";
                    sendReminderMessage(now, CollUtil.join(collect, StrUtil.COMMA), msg, content);
                }
                log.info(DateUtil.formatDateTime(now) + "," + msg + ",故障编号:" + fault.getCode());
            } else {
                // 删除定时任务
                String jobId = context.getJobDetail().getKey().getName();
                QuartzJobDTO quartzJobDTO = sysBaseApi.getQuartzJobById(jobId);
                sysBaseApi.deleteAndStopJob(quartzJobDTO);
                log.info("定时任务已删除,任务id:{}" + jobId);
            }
        } catch (Exception e) {
            log.error("NoReceiveRemindJob Error found:", e);
            throw e;
        }
    }

    /**
     * 检查是否有人领取了故障
     * @param fault
     * @return
     */
    private boolean checkIfSomeoneClaimedFault(Fault fault) {
        // 判断故障是否被领取
        boolean b = FaultStatusEnum.RECEIVE.getStatus() <= fault.getStatus();
        if (b) {
            return true;
        }
        return false;
    }

    private void sendReminderMessage(Date date, String toUser, String msg, String content) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setToUser(toUser);
        messageDTO.setTitle(msg + DateUtil.format(date, "yyyy-MM-dd"));
        messageDTO.setContent(content);
        HashMap<String, Object> map = new HashMap<>(10);
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.NO_RECEIVE_FAULT.getType());
        messageDTO.setData(map);
        messageDTO.setMsgAbstract(msg);
        messageDTO.setPublishingContent(msg);
        messageDTO.setIsRingBell(true);
        SysParamModel durationParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_RECEIVE_FAULT_RING_DURATION);
        messageDTO.setRingDuration(ObjectUtil.isNotEmpty(durationParam) ? Integer.parseInt(StrUtil.trim(durationParam.getValue())) : 5);
        SysParamModel sysParamModel = iSysParamApi.selectByCode(SysParamCodeConstant.FAULT_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        messageDTO.setPriority("L");
        messageDTO.setStartTime(date);
        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_6);
        sysBaseApi.sendTemplateMessage(messageDTO);
    }
}
