package com.aiurt.modules.common.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultRepairParticipants;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.mapper.FaultRepairParticipantsMapper;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.index.mapper.FaultCountMapper;
import com.aiurt.modules.largescream.mapper.FaultInformationMapper;
import com.aiurt.modules.largescream.model.FaultDurationTask;
import com.aiurt.modules.largescream.util.FaultLargeDateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日待办故障数
 *
 * @author: qkx
 * @date: 2022-09-09 15:11
 */
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
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private FaultInformationMapper faultInformationMapper;

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
    public String getFaultTask() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //获取当前用户作为被指派/领取人，负责过的故障报修单
        List<FaultRepairRecord> faultList = recordMapper.selectList(new LambdaQueryWrapper<FaultRepairRecord>().eq(FaultRepairRecord::getAppointUserName, sysUser.getUsername()));
       //获取已经填写的维修单
        List<FaultRepairRecord> recordList = faultList.stream().filter(f -> f.getArriveTime() != null).collect(Collectors.toList());
        //去重复
        List<FaultRepairRecord> list=recordList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(o->o.getFaultCode()+";"+o.getAppointUserName()))), ArrayList::new));
        //获取当前用户作为参与人，参与过的故障报修单
        List<FaultRepairParticipants> participantsList = participantsMapper.selectList(new LambdaQueryWrapper<FaultRepairParticipants>().eq(FaultRepairParticipants::getUserName, sysUser.getUsername()));
        //去重复
        Set <FaultRepairRecord> faultRepairRecords = new HashSet<>();
        participantsList.stream().forEach(p->{
             FaultRepairRecord record = recordMapper.selectById(p.getFaultRepairRecordId());
             faultRepairRecords.add(record);
        });
        list.addAll(faultRepairRecords);
        //查出当天用户是否进行维修
        List<String> faultNames = new ArrayList<>();
        for (FaultRepairRecord record : list) {
             FaultRepairRecord faultRepairRecord = faultMapper.getUserToday(record.getId(),new Date());
             if(ObjectUtil.isNotEmpty(faultRepairRecord))
             {
                 Fault fault = faultMapper.selectOne(new LambdaQueryWrapper<Fault>().eq(Fault::getCode, record.getFaultCode()));
                 String stationName = faultMapper.getStationName(fault.getStationCode());
                 LoginUser loginUser = sysBaseAPI.queryUser(fault.getAppointUserName());
                 String faultStatus = faultMapper.getStatusName(fault.getStatus());
                 String faultName = stationName+" "+fault.getFaultPhenomenon()+" "+loginUser.getRealname()+"-"+faultStatus;
                 faultNames.add(faultName);
             }
        }
        return   CollUtil.join(faultNames, "。");
    }

    /**
     * 班组画像获取维修工时
     * @param type
     * @param teamId
     * @return
     */
    @Override
    public Map<String, BigDecimal> getFaultUserHours(int type, String teamId) {
        Map<String, BigDecimal> userDurationMap = new HashMap<>();
        // 班组的人员
        List<LoginUser> userList = sysBaseApi.getUserPersonnel(teamId);
        String dateTime = FaultLargeDateUtil.getDateTime(type);
        Date startTime = DateUtil.parse(dateTime.split("~")[0]);
        Date endTime = DateUtil.parse(dateTime.split("~")[1]);

        // 获取维修人员在指定时间范围内的任务时长(单位秒)
        List<FaultDurationTask> faultUserDuration = faultInformationMapper.getFaultUserDuration(startTime, endTime);
        Map<String, Long> durationMap = faultUserDuration.stream().collect(Collectors.toMap(k -> k.getId(),
                v -> ObjectUtil.isEmpty(v.getDuration()) ? 0L : v.getDuration(), (a, b) -> a));
        userList.stream().forEach(l -> {
            String userId = l.getId();
            Long timeOne = durationMap.get(userId);
            if (ObjectUtil.isEmpty(timeOne)) {
                timeOne = 0L;
            }
            double time = 1.0 * (timeOne) / 3600;
            // 展示需要以小时数展示，并保留两位小数
            BigDecimal decimal = new BigDecimal(time).setScale(2, BigDecimal.ROUND_HALF_UP);
            userDurationMap.put(userId, decimal);
        });
        return userDurationMap;
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
        if (ObjectUtil.isNotEmpty(beginDate)) {
            for (int i = 0; i < dayNum; i++) {
                DateTime dateTime = DateUtil.offsetDay(beginDate, i);
                String currDateStr = DateUtil.format(dateTime, "yyyy/MM/dd");
                List<Fault> dailyFaultNumList = faultCountMapper.getDailyFaultNum(dateTime);
                map.put(currDateStr, CollUtil.isNotEmpty(dailyFaultNumList) ? dailyFaultNumList.size() : 0);
            }
        }
        return map;
    }

}
