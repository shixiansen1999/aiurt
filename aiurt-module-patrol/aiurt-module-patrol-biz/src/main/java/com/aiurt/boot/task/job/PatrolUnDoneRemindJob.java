package com.aiurt.boot.task.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
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
 * @description : 以班组为维度，当日15点整时有未完成巡视任务给予当班人员提醒
 * @date : 2023/9/26 21:55
 */
@Component
@Slf4j
public class PatrolUnDoneRemindJob implements Job {

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private ISysParamAPI iSysParamApi;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IBaseApi baseApi;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Date now = new Date();
        //查询今日未完成任务
        List<PatrolTaskDTO> unDone = patrolTaskMapper.queryUnDone(now);
        unDone.forEach(t -> {
            List<SysUserTeamDTO> userList = baseApi.getTodayOndutyDetailNoPage(CollUtil.newArrayList(t.getOrgCode()), now);
            List<String> collect = userList.stream().map(SysUserTeamDTO::getUsername).distinct().collect(Collectors.toList());
            String msg = "今日有未完成巡视任务";
            String codeStr = String.join("<br/>", StrUtil.splitTrim(t.getCode(), ","));
            String content = "<p>" + "任务编号:" + "<br/>" + codeStr + "<br/>" + "</p>";
            if (CollUtil.isNotEmpty(collect)) {
                sendReminderMessage(now, CollUtil.join(collect, StrUtil.COMMA), msg, content);
            }
            log.info(DateUtil.formatDateTime(now) + "," + t.getOrgCode() + msg + ":" + content);
        });
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
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.PATROL_UN_DONE_REMIND.getType());
        messageDTO.setData(map);
        messageDTO.setMsgAbstract(msg);
        messageDTO.setPublishingContent(msg);
        messageDTO.setIsRingBell(true);
        SysParamModel durationParam = iSysParamApi.selectByCode(SysParamCodeConstant.PUDR_RING_DURATION);
        messageDTO.setRingDuration(ObjectUtil.isNotEmpty(durationParam) ? Integer.parseInt(StrUtil.trim(durationParam.getValue())) : 5);
        SysParamModel sysParamModel = iSysParamApi.selectByCode(SysParamCodeConstant.PATROL_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        messageDTO.setPriority("L");
        messageDTO.setStartTime(date);
        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_6);
        sysBaseApi.sendTemplateMessage(messageDTO);
    }
}
