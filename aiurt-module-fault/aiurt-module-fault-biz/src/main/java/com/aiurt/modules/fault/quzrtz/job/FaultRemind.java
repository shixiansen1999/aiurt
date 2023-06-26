package com.aiurt.modules.fault.quzrtz.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.fault.dto.FaultForSendMessageDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author : sbx
 * @Classname : FaultNotification
 * @Description : TODO
 * @Date : 2023/6/20 14:15
 */
@Slf4j
@Component
public class FaultRemind {
    @Autowired
    private FaultMapper faultMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ISysParamAPI iSysParamApi;
    @Autowired
    private IBaseApi baseApi;

    /**
     * 故障未领取时要给予当班人员提示音（每两分钟提醒20秒）
     * @param code
     */
    public void processFaultAdd(String code, Date date) {
        // 创建定时执行服务
        ScheduledThreadPoolExecutor scheduler = createScheduler();
        // 获取配置初始等待时间和间隔
        SysParamModel delayParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_RECEIVE_DELAY);
        SysParamModel periodParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_RECEIVE_PERIOD);
        long delay = Long.parseLong(StrUtil.trim(delayParam.getValue()));
        long period = Long.parseLong(StrUtil.trim(periodParam.getValue()));
        // 判断是否有效
        boolean b1 = ObjectUtil.isEmpty(date) && ObjectUtil.isEmpty(delay) && ObjectUtil.isEmpty(period);
        if (b1) {
            log.info("校验失败,故障编号：{}",code);
            return;
        }

        // 提醒任务
        Runnable reminderTask = () -> {
            log.info("进入超时无人领取发送消息及提示音任务，故障编号：{}",code);
            Fault fault = getFault(code);
            // 计算当前时间与故障审核通过时间的时间间隔
            LocalDateTime currentTime = LocalDateTime.now();
            Duration duration = Duration.between(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), currentTime);
            // 上报五分钟后，且没有人领取故障，发送提醒消息
            boolean b = ObjectUtil.isNotEmpty(fault) && (duration.compareTo(Duration.ofSeconds(delay)) >= 0) && !checkIfSomeoneClaimedFault(fault);
            if (b) {
                log.info("超时无人领取发送消息及提示音，故障编号：{}", code);
                // 获取故障所在班组的今日当班人员,并发送消息给今日当班人员
                List<SysUserTeamDTO> userList = baseApi.getTodayOndutyDetailNoPage(CollUtil.newArrayList(fault.getSysOrgCode()), date);
                if (CollUtil.isNotEmpty(userList)) {
                    List<String> collect = userList.stream().map(SysUserTeamDTO::getUsername).distinct().collect(Collectors.toList());
                    String content = "故障编号：" + code + "<br/>";
                    sendReminderMessage(date, CollUtil.join(collect, ","), "有新的故障发生，请及时查看", content, SysParamCodeConstant.NO_RECEIVE_FAULT_RING_DURATION);
                }
            } else {
                // 取消任务
                scheduler.shutdown();
            }
        };
        // 安排提醒任务，每两分钟执行一次
        scheduler.scheduleAtFixedRate(reminderTask, delay, period, TimeUnit.SECONDS);

    }

    /**
     * 故障领取后两小时未更新任务状态需给予维修人提示音（每两小时提醒5秒）
     * @param code
     */
    public void processFaultNoUpdate(String code, Integer status) {
        // 创建定时执行服务
        ScheduledThreadPoolExecutor scheduler = createScheduler();
        // 获取当前故障
        Fault fault = getFault(code);
        // 获取故障当前的更新时间
        Date updateTime = fault.getUpdateTime();
        // 获取配置初始等待时间和间隔
        SysParamModel delayParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_UPDATE_DELAY);
        SysParamModel periodParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_UPDATE_PERIOD);
        long delay = Long.parseLong(StrUtil.trim(delayParam.getValue()));
        long period = Long.parseLong(StrUtil.trim(periodParam.getValue()));
        // 判断是否有效
        boolean b1 = ObjectUtil.isEmpty(fault) && ObjectUtil.isEmpty(status) && ObjectUtil.isEmpty(updateTime) && ObjectUtil.isEmpty(delay) && ObjectUtil.isEmpty(period);
        if (b1) {
            log.info("校验失败,故障编号：{}",code);
            return;
        }
        // 提醒任务
        Runnable reminderTask = () -> {
            log.info("进入超时未更新状态发送消息及提示音任务，故障编号：{}",code);
            // 计算当前时间与故障上报时间的时间间隔
            LocalDateTime currentTime = LocalDateTime.now();
            Duration duration = Duration.between(updateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), currentTime);
            // 两小时后，没有更新故障状态（未填写维修单、未挂起、或填写维修单后未提交），发送提醒消息
            FaultForSendMessageDTO faultForSendMessageDTO = faultMapper.queryForSendMessage(code, status, updateTime);
            boolean b = (duration.compareTo(Duration.ofSeconds(delay)) >= 0) && ObjectUtil.isNotEmpty(faultForSendMessageDTO);
            if (b) {
                log.info("超时未更新状态发送消息及提示音，故障编号：{}", code);
                // 发送消息给维修负责人
                String content = "故障编号："+code+"<br/>";
                sendReminderMessage(updateTime, faultForSendMessageDTO.getAppointUserName(), "请及时更新维修状态", content, SysParamCodeConstant.FAULT_RECEIVE_NO_UPDATE_RING_DURATION);
            } else {
                // 取消任务
                scheduler.shutdown();
            }
        };
        // 安排提醒任务，每两小时执行一次
        scheduler.scheduleAtFixedRate(reminderTask, delay, period, TimeUnit.SECONDS);
    }

    private static ScheduledThreadPoolExecutor createScheduler() {
        // 创建 ThreadFactory，用于设置线程名称
        ThreadFactory threadFactory = new CustomThreadFactory("FaultReminderThread");
        // 创建 ScheduledThreadPoolExecutor，指定线程池大小和 ThreadFactory
        return new ScheduledThreadPoolExecutor(5, threadFactory);
    }

    /**
     * 获取故障信息
     * @param code
     * @return
     */
    private Fault getFault(String code) {
        return faultMapper.selectByCode(code);
    }

    /**
     * 检查是否有人领取了故障
     * @param fault
     * @return
     */
    private boolean checkIfSomeoneClaimedFault(Fault fault) {
        // 判断故障是否被领取
        boolean b =fault.getStatus() > FaultStatusEnum.APPROVAL_PASS.getStatus() && fault.getStatus() < FaultStatusEnum.REPAIR.getStatus()
                && (ObjectUtil.equal(FaultStatusEnum.RECEIVE.getStatus(), fault.getStatus()) || ObjectUtil.equal(FaultStatusEnum.RECEIVE_ASSIGN.getStatus(), fault.getStatus()));
        if (b) {
            return true;
        }
        return false;
    }

    /**
     * 发送提醒消息
     * @param date
     * @param toUser
     * @param msg
     */
    private void sendReminderMessage(Date date, String toUser, String msg, String content, String ringDurationParam) {
        // 发送消息提醒领取故障
        // 发送通知
        MessageDTO messageDTO = new MessageDTO(null, toUser, msg, content);

        // 业务类型，消息类型，消息模板编码，摘要，发布内容
        HashMap<String, Object> map = new HashMap<>(10);
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.NO_RECEIVE_FAULT.getType());
        messageDTO.setData(map);
        messageDTO.setMsgAbstract(msg);
        messageDTO.setPublishingContent(msg);
        messageDTO.setIsRingBell(true);
        SysParamModel durationParam = iSysParamApi.selectByCode(ringDurationParam);
        messageDTO.setRingDuration(ObjectUtil.isNotEmpty(durationParam) ? Integer.parseInt(StrUtil.trim(durationParam.getValue())) : 5);
        SysParamModel sysParamModel = iSysParamApi.selectByCode(SysParamCodeConstant.FAULT_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        messageDTO.setPriority("L");
        messageDTO.setStartTime(date);
        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_6);
        sysBaseApi.sendTemplateMessage(messageDTO);
    }

    /**
     * 自定义 ThreadFactory 实现类
     */
    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadCount = new AtomicInteger(1);
        private final String threadNamePrefix;

        public CustomThreadFactory(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(threadNamePrefix + "-" + threadCount.getAndIncrement());
            return thread;
        }
    }
}
