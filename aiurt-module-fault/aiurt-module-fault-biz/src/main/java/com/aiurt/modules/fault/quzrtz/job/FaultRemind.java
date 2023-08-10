///*
//package com.aiurt.modules.fault.quzrtz.job;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import com.aiurt.boot.constant.RoleConstant;
//import com.aiurt.boot.constant.SysParamCodeConstant;
//import com.aiurt.common.api.dto.message.MessageDTO;
//import com.aiurt.common.constant.CommonConstant;
//import com.aiurt.common.util.SysAnnmentTypeEnum;
//import com.aiurt.modules.common.api.IBaseApi;
//import com.aiurt.modules.fault.constants.FaultConstant;
//import com.aiurt.modules.fault.dto.FaultForSendMessageDTO;
//import com.aiurt.modules.fault.entity.Fault;
//import com.aiurt.modules.fault.enums.FaultStatusEnum;
//import com.aiurt.modules.fault.mapper.FaultMapper;
//import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import lombok.extern.slf4j.Slf4j;
//import org.jeecg.common.system.api.ISysBaseAPI;
//import org.jeecg.common.system.api.ISysParamAPI;
//import org.jeecg.common.system.vo.SysParamModel;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.Collectors;
//
//*/
///**
// * @author : sbx
// * @Classname : FaultNotification
// * @Description : TODO
// * @Date : 2023/6/20 14:15
// *//*
//
//@Slf4j
//@Component
//public class FaultRemind {
//    @Autowired
//    private FaultMapper faultMapper;
//    @Autowired
//    private ISysBaseAPI sysBaseApi;
//    @Autowired
//    private ISysParamAPI iSysParamApi;
//    @Autowired
//    private IBaseApi baseApi;
//
//    */
///**
//     * 故障未领取时要给予当班人员提示音（每两分钟提醒20秒）
//     * @param code
//     *//*
//
//    public void processFaultAdd(String code, Date date) {
//        // 创建定时执行服务
//        ScheduledThreadPoolExecutor scheduler = createScheduler();
//        // 获取配置初始等待时间和间隔
//        SysParamModel delayParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_RECEIVE_DELAY);
//        SysParamModel periodParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_RECEIVE_PERIOD);
//        long delay = Long.parseLong(StrUtil.trim(delayParam.getValue()));
//        long period = Long.parseLong(StrUtil.trim(periodParam.getValue()));
//        // 判断是否有效
//        boolean b1 = ObjectUtil.isEmpty(date) && ObjectUtil.isEmpty(delay) && ObjectUtil.isEmpty(period);
//        if (b1) {
//            log.info("校验失败,故障编号：{}",code);
//            return;
//        }
//
//        // 提醒任务
//        Runnable reminderTask = () -> {
//            log.info("进入超时无人领取发送消息及提示音任务，故障编号：{}",code);
//            Fault fault = getFault(code);
//            // 计算当前时间与故障审核通过时间的时间间隔
//            //LocalDateTime currentTime = LocalDateTime.now();
//            //Duration duration = Duration.between(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), currentTime);
//            // 上报五分钟后，且没有人领取故障，发送提醒消息
//            SysParamModel remindParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_RECEIVE_FAULT_REMIND);
//            boolean b = ObjectUtil.isNotEmpty(remindParam) && FaultConstant.ENABLE.equals(remindParam.getValue()) && ObjectUtil.isNotEmpty(fault) && !checkIfSomeoneClaimedFault(fault);
//            log.info("{}",b);
//            if (b) {
//                log.info("超时无人领取发送消息及提示音，故障编号：{}", code);
//                Date now = new Date();
//                // 获取故障所在班组的今日当班人员,并发送消息给今日当班人员
//                List<SysUserTeamDTO> userList = baseApi.getTodayOndutyDetailNoPage(CollUtil.newArrayList(fault.getSysOrgCode()), now);
//                if (CollUtil.isNotEmpty(userList)) {
//                    List<String> collect = userList.stream().map(SysUserTeamDTO::getUsername).distinct().collect(Collectors.toList());
//                    String content = "故障编号：" + code + "<br/>";
//                    sendReminderMessage(now, CollUtil.join(collect, ","), "有新的故障发生，请及时查看", content, SysAnnmentTypeEnum.NO_RECEIVE_FAULT, SysParamCodeConstant.NO_RECEIVE_FAULT_RING_DURATION);
//                }
//            } else {
//                // 取消任务
//                log.info("取消超时未领取提醒任务故障编号：{}", code);
//                scheduler.shutdown();
//            }
//        };
//        // 安排提醒任务，每两分钟执行一次
//        scheduler.scheduleAtFixedRate(reminderTask, delay, period, TimeUnit.SECONDS);
//
//    }
//
//    */
///**
//     * 故障领取后两小时未更新任务状态需给予维修人提示音（每两小时提醒5秒）
//     * @param code
//     *//*
//
//    public void processFaultNoUpdate(String code, Integer status) {
//        // 创建定时执行服务
//        ScheduledThreadPoolExecutor scheduler = createScheduler();
//        // 获取当前故障
//        Fault fault = getFault(code);
//        // 获取故障当前的更新时间
//        Date updateTime = fault.getUpdateTime();
//        // 获取配置初始等待时间和间隔
//        SysParamModel delayParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_UPDATE_DELAY);
//        SysParamModel periodParam = iSysParamApi.selectByCode(SysParamCodeConstant.NO_UPDATE_PERIOD);
//        long delay = Long.parseLong(StrUtil.trim(delayParam.getValue()));
//        long period = Long.parseLong(StrUtil.trim(periodParam.getValue()));
//        // 判断是否有效
//        boolean b1 = ObjectUtil.isEmpty(fault) && ObjectUtil.isEmpty(status) && ObjectUtil.isEmpty(delay) && ObjectUtil.isEmpty(period);
//        if (b1) {
//            log.info("校验失败,故障编号：{}",code);
//            return;
//        }
//        // 提醒任务
//        Runnable reminderTask = () -> {
//            log.info("进入超时未更新状态发送消息及提示音任务，故障编号：{}",code);
//            // 计算当前时间与故障上报时间的时间间隔
//            //LocalDateTime currentTime = LocalDateTime.now();
//            //Duration duration = Duration.between(updateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), currentTime);
//            // 两小时后，没有更新故障状态（未填写维修单、未挂起、或填写维修单后未提交），发送提醒消息
//            SysParamModel remindParam = iSysParamApi.selectByCode(SysParamCodeConstant.RECEIVE_FAULT_NO_UPDATE);
//            FaultForSendMessageDTO faultForSendMessageDTO = faultMapper.queryForSendMessage(code, status, updateTime);
//            boolean b = ObjectUtil.isNotEmpty(remindParam) && FaultConstant.ENABLE.equals(remindParam.getValue()) && ObjectUtil.isNotEmpty(faultForSendMessageDTO);
//            log.info("{}",b);
//            if (b) {
//                log.info("超时未更新状态发送消息及提示音，故障编号：{}", code);
//                // 发送消息给维修负责人
//                String content = "故障编号："+code+"<br/>";
//                sendReminderMessage(new Date(), faultForSendMessageDTO.getAppointUserName(), "请及时更新维修状态", content, SysAnnmentTypeEnum.RECEIVE_FAULT_NO_UPDATE, SysParamCodeConstant.FAULT_RECEIVE_NO_UPDATE_RING_DURATION);
//            } else {
//                // 取消任务
//                log.info("取消超时未更新状态提醒任务故障编号：{}", code);
//                scheduler.shutdown();
//            }
//        };
//        // 安排提醒任务，每两小时执行一次
//        scheduler.scheduleAtFixedRate(reminderTask, delay, period, TimeUnit.SECONDS);
//    }
//
//    */
///**
//     * 故障挂起超时未处理提醒
//     *//*
//
//    public void processFaultHangUpTimeOut(Fault f1) {
//        ScheduledThreadPoolExecutor scheduler = createScheduler();
//        Fault f = getFault(f1.getCode());
//        if (ObjectUtil.isEmpty(f)) {
//            log.info("未找到改故障，故障编号{}", f1.getCode());
//            return;
//        }
//        // 获取初始延时和间隔时长配置
//        SysParamModel delayParam = iSysParamApi.selectByCode(SysParamCodeConstant.HUR_DELAY);
//        SysParamModel periodParam = iSysParamApi.selectByCode(SysParamCodeConstant.HUR_PERIOD);
//        boolean b1 = ObjectUtil.isEmpty(delayParam) && ObjectUtil.isEmpty(periodParam);
//        if (b1) {
//            log.info("故障挂起超时未处理提醒:没有添加初始延时或间隔时长配置");
//            return;
//        }
//        long delay = Long.parseLong(StrUtil.trim(delayParam.getValue()));
//        long period = Long.parseLong(StrUtil.trim(periodParam.getValue()));
//        boolean b2 = ObjectUtil.isEmpty(delay) && ObjectUtil.isEmpty(period);
//        if (b2) {
//            log.info("请检查故障挂起超时未处理提醒的初始延时和间隔时长配置,{},{}", delay, period);
//            return;
//        }
//        // 计数
//        AtomicInteger counter = new AtomicInteger(0);
//        // 提醒任务
//        Runnable reminderTask = () -> {
//            // 获取故障挂起超时未处理提醒配置
//            SysParamModel remindParam = iSysParamApi.selectByCode(SysParamCodeConstant.HANG_UP_REMIND);
//            // 判断是否未处理
//            Date updateTime = DateUtil.parseDateTime(DateUtil.format(f.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
//            Fault fault = faultMapper.selectOne(new LambdaQueryWrapper<Fault>().eq(Fault::getCode, f.getCode()).eq(Fault::getStatus, FaultStatusEnum.HANGUP.getStatus()).eq(Fault::getUpdateTime, updateTime));
//            boolean b = ObjectUtil.isNotEmpty(remindParam) && FaultConstant.ENABLE.equals(remindParam.getValue()) && ObjectUtil.isNotEmpty(fault);
//            if (b) {
//                // 获取今日当班人员
//                List<SysUserTeamDTO> userList = baseApi.getTodayOndutyDetailNoPage(CollUtil.newArrayList(fault.getSysOrgCode()), new Date());
//                List<String> collect = CollUtil.isNotEmpty(userList) ? userList.stream().map(SysUserTeamDTO::getUsername).distinct().collect(Collectors.toList()) : new ArrayList<>();
//                // 获取故障所属班组的班组长
//                String foreman = sysBaseApi.getUserNameByOrgCodeAndRoleCode(CollUtil.newArrayList(fault.getSysOrgCode()), CollUtil.newArrayList(RoleConstant.FOREMAN));
//                List<String> foremanList = StrUtil.splitTrim(foreman, ",");
//                for (String s : foremanList) {
//                    if (!collect.contains(s)) {
//                       collect.add(s);
//                    }
//                }
//                // 发送消息给今日当班人员和班组长
//                int i = counter.getAndIncrement();
//                int timeoutDuration = (int)(delay + (i * period));
//                if (CollUtil.isNotEmpty(collect)) {
//                    String msg = "故障挂起已超过" + secondToTime(timeoutDuration);
//                    String content = "故障编号：" + fault.getCode() + "<br/>";
//                    sendReminderMessage(new Date(), CollUtil.join(collect, ","), msg, content, SysAnnmentTypeEnum.HANG_UP_REMIND, SysParamCodeConstant.HUR_RING_DURATION);
//                }
//            } else {
//                // 取消任务
//                log.info("取消故障挂起超时未处理提醒任务，故障编号：{}", f.getCode());
//                scheduler.shutdown();
//            }
//        };
//        scheduler.scheduleAtFixedRate(reminderTask, delay, period, TimeUnit.SECONDS);
//    }
//
//    private static ScheduledThreadPoolExecutor createScheduler() {
//        // 创建 ThreadFactory，用于设置线程名称
//        ThreadFactory threadFactory = new CustomThreadFactory("FaultReminderThread");
//        // 创建 ScheduledThreadPoolExecutor，指定线程池大小和 ThreadFactory
//        return new ScheduledThreadPoolExecutor(5, threadFactory);
//    }
//
//    */
///**
//     * 获取故障信息
//     * @param code
//     * @return
//     *//*
//
//    private Fault getFault(String code) {
//        return faultMapper.selectByCode(code);
//    }
//
//    */
///**
//     * 检查是否有人领取了故障
//     * @param fault
//     * @return
//     *//*
//
//    private boolean checkIfSomeoneClaimedFault(Fault fault) {
//        // 判断故障是否被领取
//        boolean b = FaultStatusEnum.RECEIVE.getStatus() <= fault.getStatus();
//        if (b) {
//            return true;
//        }
//        return false;
//    }
//
//    */
///**
//     * 发送提醒消息
//     * @param date
//     * @param toUser
//     * @param msg
//     *//*
//
//    private void sendReminderMessage(Date date, String toUser, String msg, String content, SysAnnmentTypeEnum typeEnum, String ringDurationParam) {
//        // 发送消息提醒领取故障
//        // 发送通知
//        MessageDTO messageDTO = new MessageDTO(null, toUser, msg + DateUtil.format(date, "yyyy-MM-dd"), content);
//
//        // 业务类型，消息类型，消息模板编码，摘要，发布内容
//        HashMap<String, Object> map = new HashMap<>(10);
//        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, typeEnum.getType());
//        messageDTO.setData(map);
//        messageDTO.setMsgAbstract(msg);
//        messageDTO.setPublishingContent(msg);
//        messageDTO.setIsRingBell(true);
//        SysParamModel durationParam = iSysParamApi.selectByCode(ringDurationParam);
//        messageDTO.setRingDuration(ObjectUtil.isNotEmpty(durationParam) ? Integer.parseInt(StrUtil.trim(durationParam.getValue())) : 5);
//        SysParamModel sysParamModel = iSysParamApi.selectByCode(SysParamCodeConstant.FAULT_MESSAGE);
//        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
//        messageDTO.setPriority("L");
//        messageDTO.setStartTime(date);
//        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_6);
//        sysBaseApi.sendTemplateMessage(messageDTO);
//    }
//
//    */
///**
//     * 秒数转换为时间格式(h小时m分钟s秒)
//     * @param seconds 需要转换的秒数
//     * @return 转换后的字符串
//     *//*
//
//    public static String secondToTime(int seconds) {
//        if (seconds <= 0) {
//            return "0秒";
//        }
//        int hour = seconds / 3600;
//        int other = seconds % 3600;
//        int minute = other / 60;
//        int second = other % 60;
//        final StringBuilder sb = new StringBuilder();
//        if (hour > 0) {
//            sb.append(hour);
//            sb.append("小时");
//        }
//        if (minute > 0) {
//            sb.append(minute);
//            sb.append("分钟");
//        }
//        if (second > 0) {
//            sb.append(second);
//            sb.append("秒");
//        }
//        return sb.toString();
//    }
//
//    */
///**
//     * 自定义 ThreadFactory 实现类
//     *//*
//
//    static class CustomThreadFactory implements ThreadFactory {
//        private final AtomicInteger threadCount = new AtomicInteger(1);
//        private final String threadNamePrefix;
//
//        public CustomThreadFactory(String threadNamePrefix) {
//            this.threadNamePrefix = threadNamePrefix;
//        }
//
//        @Override
//        public Thread newThread(Runnable r) {
//            Thread thread = new Thread(r);
//            thread.setName(threadNamePrefix + "-" + threadCount.getAndIncrement());
//            return thread;
//        }
//    }
//}
//*/
