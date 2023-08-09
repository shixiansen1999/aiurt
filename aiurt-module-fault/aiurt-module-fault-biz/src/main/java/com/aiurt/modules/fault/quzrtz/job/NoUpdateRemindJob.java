package com.aiurt.modules.fault.quzrtz.job;

import cn.hutool.core.date.DateTime;
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
import com.aiurt.modules.fault.dto.FaultForSendMessageDTO;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.service.IFaultService;
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
 * @Classname : ReceiveNoUpdateRemindJob
 * @Description : 故障领取后超时未更新状态定时任务，包括已领取、已接收、维修中
 * @Date : 2023/8/8 17:14
 */
@Component
@Slf4j
public class NoUpdateRemindJob implements Job {

    private String parameter;
    private static final int LEN = 3;

    @Autowired
    private FaultMapper faultMapper;
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
            FaultForSendMessageDTO faultForSendMessageDTO = faultMapper.queryForSendMessage(paramList.get(0), Integer.parseInt(paramList.get(1)), updateTime);
            // 判断配置开启否，故障状态更新否
            boolean b = ObjectUtil.isNotEmpty(remindParam) && FaultConstant.ENABLE.equals(remindParam.getValue()) && ObjectUtil.isNotEmpty(faultForSendMessageDTO);
            if (b) {
                // 发送消息给维修负责人
                Date now = new Date();
                String msg = "请及时更新维修状态";
                String content = "故障编号:"+ faultForSendMessageDTO.getCode() +"<br/>";
                sendReminderMessage(now, faultForSendMessageDTO.getAppointUserName(), msg, content);
                log.info(DateUtil.formatDateTime(now) + "," + msg + ",故障编号:" + faultForSendMessageDTO.getCode());
            } else {
                // 删除定时任务
                String jobId = context.getJobDetail().getKey().getName();
                QuartzJobDTO quartzJobDTO = sysBaseApi.getQuartzJobById(jobId);
                sysBaseApi.deleteAndStopJob(quartzJobDTO);
                log.info("定时任务已删除,任务id:" + jobId);
            }
        } catch (Exception e) {
            log.error("NoUpdateRemindJob Error found:", e);
            throw e;
        }
    }

    private void sendReminderMessage(Date date, String toUser, String msg, String content) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setToUser(toUser);
        messageDTO.setTitle(msg + DateUtil.format(date, "yyyy-MM-dd"));
        messageDTO.setContent(content);
        HashMap<String, Object> map = new HashMap<>(10);
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.RECEIVE_FAULT_NO_UPDATE.getType());
        messageDTO.setData(map);
        messageDTO.setMsgAbstract(msg);
        messageDTO.setPublishingContent(msg);
        messageDTO.setIsRingBell(true);
        SysParamModel durationParam = iSysParamApi.selectByCode(SysParamCodeConstant.FAULT_RECEIVE_NO_UPDATE_RING_DURATION);
        messageDTO.setRingDuration(ObjectUtil.isNotEmpty(durationParam) ? Integer.parseInt(StrUtil.trim(durationParam.getValue())) : 5);
        SysParamModel sysParamModel = iSysParamApi.selectByCode(SysParamCodeConstant.FAULT_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        messageDTO.setPriority("L");
        messageDTO.setStartTime(date);
        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_6);
        sysBaseApi.sendTemplateMessage(messageDTO);
    }
}
