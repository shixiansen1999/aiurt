package com.aiurt.modules.common.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.index.dto.RepairTaskNum;
import com.aiurt.modules.fault.dto.FaultReportDTO;
import com.aiurt.modules.fault.dto.UserTimeDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.mapper.FaultRepairParticipantsMapper;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.index.mapper.FaultCountMapper;
import com.aiurt.modules.largescream.mapper.FaultInformationMapper;
import com.aiurt.modules.largescream.model.FaultDurationTask;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 日待办故障数
 *
 * @author: qkx
 * @date: 2022-09-09 15:11
 */
@Slf4j
@Service
public class DailyFaultApiImpl implements DailyFaultApi {
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private FaultCountMapper faultCountMapper;
    @Autowired
    private FaultMapper faultMapper;
    @Autowired
    private FaultRepairParticipantsMapper participantsMapper;
    @Autowired
    private FaultRepairRecordMapper recordMapper;
    @Autowired
    private FaultInformationMapper faultInformationMapper;
    @Autowired
    private ISysParamAPI sysParamApi;

    @Override
    public Map<String, Integer> getDailyFaultNum(Integer year, Integer month) {
        // 获取某年某月的开始时间
        Date beginDate = beginOfMonth(year, month);

        // 计算某年某月一共有多少天
        int dayNum = getMonthDays(year, month);

        Map<String, Integer> dailyFaultNum = getDailyFaultNum(beginDate, dayNum);

        return dailyFaultNum;
    }

    @Override
    public HashMap<String, String> getFaultTask(DateTime startTime, DateTime endTime) {
        HashMap<String, String> map = new HashMap<>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<LoginUser> sysUsers = sysBaseApi.getUserPersonnel(sysUser.getOrgId());
        List<String> userNames = Optional.ofNullable(sysUsers).orElse(Collections.emptyList()).stream().map(LoginUser::getUsername).collect(Collectors.toList());
        if (CollUtil.isEmpty(userNames)) {
            return map;
        }
        //根据配置实现是否需要查询全部故障作为工作内容
        SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.WORKLOG_UNFINISH_FAULT);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            //查出当天维修单已完成的故障，和所有未完成维修单的故障
            List<Fault> todayFault = recordMapper.getTodayFault(startTime, endTime, userNames);
            if (CollUtil.isNotEmpty(todayFault)) {
                StringBuilder content = new StringBuilder();
                StringBuilder code = new StringBuilder();

                for (Fault fault : todayFault) {
                    String stationName = sysBaseApi.getPosition(fault.getStationCode());
                    String lineName = sysBaseApi.getPosition(fault.getLineCode());
                    content.append(lineName).append("-").append(stationName).append(" ");
                    if (StrUtil.isNotBlank(fault.getSymptoms())) {
                        content.append(fault.getSymptoms());
                    }else {
                        content.append(" 未填写故障原因");
                    }

                    if (StrUtil.isNotBlank(fault.getAppointUserName())) {
                        String realname = sysBaseApi.getUserByName(fault.getAppointUserName()).getRealname();
                        content.append(" 维修人:").append(realname);
                    } else {
                        content.append(" 未指派维修人:");
                    }
                    content.append("-");

                    String faultStatus = sysBaseApi.translateDict("fault_status", Convert.toStr(fault.getStatus()));
                    content.append(faultStatus);
                    content.append("\n");
                    code.append(fault.getCode()).append(",");
                }
                if (content.length() > 1) {
                    // 截取字符
                    content = content.deleteCharAt(content.length() - 1);
                    map.put("content", content.toString());
                }
                if (code.length() > 1) {
                    // 截取字符
                    code = code.deleteCharAt(code.length() - 1);
                    map.put("code", code.toString());
                }
            }

        } else {
            //查出指定时间范围内已完成的故障
            List<FaultRepairRecord> recordList =  recordMapper.getTodayRecord(startTime, endTime,userNames);

            StringBuilder content = new StringBuilder();
            StringBuilder code = new StringBuilder();

            //获取时间范围内的维修单
            if (CollUtil.isNotEmpty(recordList)) {
                for (FaultRepairRecord record : recordList) {
                    Fault fault = faultMapper.selectOne(new LambdaQueryWrapper<Fault>().eq(Fault::getCode, record.getFaultCode()));
                    String stationName = sysBaseApi.getPosition(fault.getStationCode());
                    String lineName = sysBaseApi.getPosition(fault.getLineCode());
                    LoginUser userByName = sysBaseApi.getUserByName(record.getAppointUserName());
                    content.append(lineName).append("-").append(stationName).append(" ").append(record.getSymptoms()).append(" 维修人:").append(userByName.getRealname()).append("-");
                    if (record.getSolveStatus() == 1) {
                        content.append("维修完成。");
                    } else {
                        content.append("维修中。");
                    }
                    content.append("\n");
                    code.append(fault.getCode()).append(",");
                }
                if (content.length() > 1) {
                    // 截取字符
                    content = content.deleteCharAt(content.length() - 1);
                    map.put("content", content.toString());
                }
                if (code.length() > 1) {
                    // 截取字符
                    code = code.deleteCharAt(code.length() - 1);
                    map.put("code", code.toString());
                }
            }
        }
        return map;
    }

    /**
     * 班组画像获取维修工时
     * @param startTime
     * @param endTime
     * @param teamId
     * @return
     */
    @Override
    public Map<String, BigDecimal> getFaultUserHours(Date startTime, Date endTime, String teamId) {
        Map<String, BigDecimal> userDurationMap = new HashMap<>(32);
        // 班组的人员
        List<LoginUser> userList = sysBaseApi.getUserPersonnel(teamId);
        SysParamModel filterParamModel = sysParamApi.selectByCode(SysParamCodeConstant.FAULT_FILTER);
        boolean filterValue = "1".equals(filterParamModel.getValue());
        List<FaultDurationTask> faultUserDuration  = new ArrayList<>();
        List<FaultDurationTask> participantsDuration  = new ArrayList<>();
        if(filterValue){
            // 获取维修人员在指定时间范围内的任务时长(单位秒)
            faultUserDuration = faultInformationMapper.getFilterFaultUserDuration(startTime, endTime);
            // 获取参与人员在指定时间范围内的任务时长(单位秒)
            participantsDuration = faultInformationMapper.getFilterFaultParticipantsDuration(startTime, endTime);
        }else {
        // 获取维修人员在指定时间范围内的任务时长(单位秒)
         faultUserDuration = faultInformationMapper.getFaultUserDuration(startTime, endTime);
        // 获取参与人员在指定时间范围内的任务时长(单位秒)
         participantsDuration = faultInformationMapper.getFaultParticipantsDuration(startTime, endTime);
        }

        Map<String, Integer> durationMap = faultUserDuration.stream().collect(Collectors.toMap(k -> k.getUserId(),
                v -> ObjectUtil.isEmpty(v.getDuration()) ? 0 : v.getDuration(), (a, b) -> a));

        Map<String, Integer> participantsMap = participantsDuration.stream().collect(Collectors.toMap(k -> k.getUserId(),
                v -> ObjectUtil.isEmpty(v.getDuration()) ? 0 : v.getDuration(), (a, b) -> a));

        userList.stream().forEach(l -> {
            String userId = l.getId();
            Integer timeOne = durationMap.get(userId) != null ? durationMap.get(userId) : 0;
            Integer timeTwo = participantsMap.get(userId)!= null ? participantsMap.get(userId) : 0;
            int time = timeOne + timeTwo;
            userDurationMap.put(userId, new BigDecimal(time));
        });
        return userDurationMap;
    }

    /**
     * 班组画像获取维修工时（参与人和执行人不是同一个）
     * @param startTime
     * @param endTime
     * @param teamId
     * @return
     */
    @Override
    public BigDecimal getFaultHours(Date startTime, Date endTime, String teamId) {
        // 班组的人员
        List<LoginUser> userList = sysBaseApi.getUserPersonnel(teamId);
        if (CollUtil.isNotEmpty(userList)) {
            /*String dateTime = FaultLargeDateUtil.getDateTime(type);
            Date startTime = DateUtil.parse(dateTime.split("~")[0]);
            Date endTime = DateUtil.parse(dateTime.split("~")[1]);*/
            SysParamModel filterParamModel = sysParamApi.selectByCode(SysParamCodeConstant.FAULT_FILTER);
            boolean filterValue = "1".equals(filterParamModel.getValue());
            List<FaultDurationTask> faultByIdDuration = new ArrayList<>();
            List<FaultDurationTask> participantsByIdDuration = new ArrayList<>();
            if(filterValue){
                // 获取指派人员在指定时间范围内的每一个任务的时长(单位秒)
                faultByIdDuration = faultInformationMapper.getFilterFaultUserDuration(startTime, endTime);
                // 获取参与人员在指定时间范围内的任务时长(单位秒)
                participantsByIdDuration = faultInformationMapper.getFilterFaultParticipantsDuration(startTime, endTime);
            }else {
                // 获取指派人员在指定时间范围内的每一个任务的时长(单位秒)
                 faultByIdDuration = faultInformationMapper.getFaultByIdDuration(startTime, endTime, userList);
                // 获取参与人在指定时间范围内的每一个任务的任务时长(单位秒)
                 participantsByIdDuration = faultInformationMapper.getParticipantsDuration(startTime, endTime, userList);
            }
            Map<String, Integer> durationMap = faultByIdDuration.stream().collect(Collectors.toMap(k -> k.getUserId(),
                    v -> ObjectUtil.isEmpty(v.getDuration()) ? 0 : v.getDuration(), (a, b) -> a));

            Map<String, Integer> participantsMap = participantsByIdDuration.stream().collect(Collectors.toMap(k -> k.getUserId(),
                    v -> ObjectUtil.isEmpty(v.getDuration()) ? 0 : v.getDuration(), (a, b) -> a));
            BigDecimal sum = new BigDecimal("0.00");
            for (LoginUser user : userList) {
                String userId = user.getId();
                Integer timeOne = durationMap.get(userId) != null ? durationMap.get(userId) : 0;
                Integer timeTwo = participantsMap.get(userId)!= null ? participantsMap.get(userId) : 0;

                int time =timeOne+timeTwo;
                sum = sum.add(new BigDecimal(time));
            }
            return sum;
        }
    return new BigDecimal("0.00");
    }

    @Override
    public Map<String, FaultReportDTO> getFaultOrgReport(List<String> teamId, String startTime, String endTime) {
        Map<String, FaultReportDTO> map = new HashMap<>(32);

        if (CollectionUtil.isEmpty(teamId)){
            return map;
        }
        SysParamModel filterParamModel = sysParamApi.selectByCode(SysParamCodeConstant.FAULT_FILTER);
        boolean filterValue = "1".equals(filterParamModel.getValue());
        //线程处理
        ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(3, 5);
        if (CollectionUtil.isNotEmpty(teamId)){
            teamId.forEach(orgId->{
                threadPoolExecutor.execute(() -> {
                    getMoreDetail(orgId, map, startTime, endTime,filterValue);
                });
            });
        }
        threadPoolExecutor.shutdown();
        try {
            // 等待线程池中的任务全部完成
            threadPoolExecutor.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 处理中断异常
            log.info("循环方法的线程中断异常", e.getMessage());
        }
        return map;
    }

    private void getMoreDetail(String orgId, Map<String, FaultReportDTO> map, String startTime, String endTime,boolean filterValue) {
        FaultReportDTO f = new FaultReportDTO();
        List<UserTimeDTO> userFaultList = new ArrayList<>();
        List<UserTimeDTO> accompanyFaultList = new ArrayList<>();

        if(filterValue){
            f = faultInformationMapper.getFilterFaultOrgReport(startTime,endTime,orgId);
            //查询参与人任务时长
            accompanyFaultList =faultInformationMapper.getFilterAccompanyTime(orgId,startTime,endTime);
        }else {
            f = faultInformationMapper.getFaultOrgReport(startTime,endTime,orgId);
            //查询参与人任务时长
            accompanyFaultList =faultInformationMapper.getAccompanyTime(orgId,startTime,endTime);
        }
        f.setOrgId(orgId);
        f.setConstructorsNum(faultInformationMapper.getConstructorsNum(startTime,endTime,orgId));
        f.setFaultCompletedTasks(faultInformationMapper.getFaultCompletedTasks(startTime,endTime,orgId));

        List<String> str = faultInformationMapper.getConstructionHours(f.getOrgId(),startTime,endTime);
        List<BigDecimal> doubles = new ArrayList<>();
        str.forEach(s -> {
            List<String> str1 = Arrays.asList(s.split(","));
            str1.forEach(ss->{
                List<String> strings = Arrays.asList(ss.split("至"));
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                try {
                    if(CollUtil.isNotEmpty(strings)){
                        Date start = format.parse(strings.get(0));
                        Date end = format.parse(strings.get(1));
                        Long time = end.getTime() - start.getTime();
                        BigDecimal decimal = new BigDecimal(time);
                        doubles.add(NumberUtil.div(decimal,NumberUtil.round((1000*60*60),2)));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        });
        if (f.getNum1()==0){
            f.setRepairTime(0);
        }else {
            // 平均维修时长不需要统计同行人
            BigDecimal bigDecimal = new BigDecimal(f.getNum()).divide(new BigDecimal(f.getNum1()),0, BigDecimal.ROUND_HALF_UP);
            f.setRepairTime(bigDecimal.intValue());
        }

        // 总工时需要统计同行人
        int sum = accompanyFaultList
                .stream().filter(w-> w.getDuration() !=null)
                .mapToInt(UserTimeDTO::getDuration)
                .sum();
        f.setFailureTime(f.getNum()+sum);
        BigDecimal totalPrice = doubles.stream().map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add);
        f.setConstructionHours(totalPrice.setScale(2,BigDecimal.ROUND_HALF_UP));
        map.put(f.getOrgId(),f);
    }

    @Override
    public Map<String, FaultReportDTO> getFaultUserReport(List<String> teamId, String startTime, String endTime,String userId,List<String> userIds) {
        Map<String, FaultReportDTO> map = new HashMap<>(32);
        List<String> users = new ArrayList<>();
        if (CollUtil.isEmpty(userIds)&&ObjectUtil.isEmpty(userId)) {
            return map;
        }
        if (ObjectUtil.isNotEmpty(userId)) {
            users.add(userId);
        }
        if (CollUtil.isNotEmpty(userIds)) {
            users.addAll(userIds);
        }
        SysParamModel filterParamModel = sysParamApi.selectByCode(SysParamCodeConstant.FAULT_FILTER);
        boolean filterValue = "1".equals(filterParamModel.getValue());
        //线程处理
        ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(3, 5);
        if (CollectionUtil.isNotEmpty(users)){
            users.forEach(id->{
                threadPoolExecutor.execute(() -> {
                    FaultReportDTO faultReportDTO = new FaultReportDTO();
                    int sum = 0;
                    if(filterValue){
                        faultReportDTO = faultInformationMapper.getFilterFaultUserReport(null,startTime,endTime,null,id);
                        sum = faultInformationMapper.getFilterUserTimes(id,startTime,endTime);
                    }else {
                        faultReportDTO = faultInformationMapper.getFaultUserReport(null,startTime,endTime,null,id);
                        sum = faultInformationMapper.getUserTimes(id,startTime,endTime);
                    }
                    List<String> str = faultInformationMapper.getUserConstructionHours(id,startTime,endTime);
                    List<BigDecimal> doubles = new ArrayList<>();
                    str.forEach(s -> {
                        List<String> str1 = Arrays.asList(s.split(","));
                        str1.forEach(ss->{
                            List<String> strings = Arrays.asList(ss.split("至"));
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                            try {
                                Date start = format.parse(strings.get(0));
                                Date end = format.parse(strings.get(1));
                                Long time = end.getTime() - start.getTime();
                                BigDecimal decimal = new BigDecimal(time);
                                doubles.add(NumberUtil.div(decimal,NumberUtil.round((1000*60*60),2)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                    FaultReportDTO  fau = faultInformationMapper.getUserConstructorsNum(id,startTime,endTime);
                    Integer finishNumfrom = faultInformationMapper.getFaultUserCompletedTasks(id, startTime, endTime);
                    faultReportDTO.setFaultCompletedTasks(finishNumfrom);

                    if (fau.getNum1()==0){
                        faultReportDTO.setRepairTime(0);
                    }else {
                        //平均维修时间不需要加上同行人工时
                        BigDecimal bigDecimal = new BigDecimal(faultReportDTO.getNum()).divide(new BigDecimal(fau.getNum1()),0, BigDecimal.ROUND_HALF_UP);
                        faultReportDTO.setRepairTime(bigDecimal.intValue());
                    }
                    faultReportDTO.setConstructorsNum(fau.getConstructorsNum());
                    //总工时需要加上同行人
                    faultReportDTO.setNum(faultReportDTO.getNum()+sum);
                    faultReportDTO.setFailureTime(faultReportDTO.getNum());
                    BigDecimal totalPrice = doubles.stream().map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add);
                    faultReportDTO.setConstructionHours(totalPrice.setScale(2,BigDecimal.ROUND_HALF_UP));
                    map.put(id,faultReportDTO);
                });
            });
        }
        threadPoolExecutor.shutdown();
        try {
            // 等待线程池中的任务全部完成
            threadPoolExecutor.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 处理中断异常
            log.info("循环方法的线程中断异常", e.getMessage());
        }
        return map;
    }

    /**
     * 计算某年某月一共有多少天
     *
     * @param year  年份
     * @param month 月份
     * @return
     */
    private int getMonthDays(Integer year, Integer month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 获取某年某月的开始时间
     *
     * @param year  年份
     * @param month 月份
     * @return
     */
    private Date beginOfMonth(Integer year, Integer month) {
        // 获取当前分区的日历信息(这里可以使用参数指定时区)
        Calendar calendar = Calendar.getInstance();
        // 设置年
        calendar.set(Calendar.YEAR, year);
        // 设置月，月份从0开始
        calendar.set(Calendar.MONTH, month - 1);
        // 设置为指定月的第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // 获取指定月第一天的时间
        Date start = calendar.getTime();
        return start;
    }

    /**
     * 获取首页日待办事项故障完成数量
     *
     * @param beginDate
     * @param dayNum
     * @return
     */
    public Map<String, Integer> getDailyFaultNum(Date beginDate, int dayNum) {
        Map<String, Integer> map = new HashMap<>(32);
        List<RepairTaskNum> repairTaskNums = faultCountMapper.getDailyFaultNum(DateUtil.offsetDay(beginDate, 0), DateUtil.offsetDay(beginDate, dayNum - 1));
        if(CollUtil.isNotEmpty(repairTaskNums)){
            map = repairTaskNums.stream().collect(Collectors.toMap(RepairTaskNum::getCurrDateStr, RepairTaskNum::getNum, (v1, v2) -> v1));
        }
        return map;
    }

}
