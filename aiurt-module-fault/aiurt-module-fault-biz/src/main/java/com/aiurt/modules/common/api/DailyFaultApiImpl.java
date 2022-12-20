package com.aiurt.modules.common.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.fault.dto.FaultReportDTO;
import com.aiurt.modules.fault.dto.UserTimeDTO;
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
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public HashMap<String, String> getFaultTask(DateTime startTime, DateTime endTime) {
        HashMap<String, String> map = new HashMap<>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<LoginUser> sysUsers = sysBaseApi.getUserPersonnel(sysUser.getOrgId());
        List<String> userNames = Optional.ofNullable(sysUsers).orElse(Collections.emptyList()).stream().map(LoginUser::getUsername).collect(Collectors.toList());
        //获取当前用户部门的人作为被指派/领取人，负责过的故障报修单
        List<FaultRepairRecord> faultList = recordMapper.selectList(new LambdaQueryWrapper<FaultRepairRecord>().in(FaultRepairRecord::getAppointUserName, userNames).eq(FaultRepairRecord::getDelFlag, 0));
       //获取已经填写的维修单
        List<FaultRepairRecord> recordList = Optional.ofNullable(faultList).orElse(Collections.emptyList()).stream().filter(f -> f.getArriveTime() != null).collect(Collectors.toList());
        //去重复
        List<FaultRepairRecord> list=Optional.ofNullable(recordList).orElse(Collections.emptyList()).stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(o->o.getFaultCode()+";"+o.getAppointUserName()))), ArrayList::new));
        //获取当前用户作为参与人，参与过的故障报修单
        List<FaultRepairParticipants> participantsList = participantsMapper.selectList(new LambdaQueryWrapper<FaultRepairParticipants>().in(FaultRepairParticipants::getUserName, userNames));
        //去重复
        Set <FaultRepairRecord> faultRepairRecords = new HashSet<>();
        if (CollUtil.isNotEmpty(participantsList)) {
            participantsList.stream().forEach(p->{
                FaultRepairRecord record = recordMapper.selectById(p.getFaultRepairRecordId());
                faultRepairRecords.add(record);
            });
            faultRepairRecords.addAll(list);
        }

        StringBuilder content = new StringBuilder();
        StringBuilder code = new StringBuilder();

        //获取时间范围内的维修单
        if (CollUtil.isNotEmpty(faultRepairRecords)) {
            for (FaultRepairRecord record : faultRepairRecords) {
                if (record.getCreateTime().after(startTime) && record.getCreateTime().before(endTime)) {
                    Fault fault = faultMapper.selectOne(new LambdaQueryWrapper<Fault>().eq(Fault::getCode, record.getFaultCode()));
                    String stationName = sysBaseApi.getPosition(fault.getStationCode());
                    String lineName = sysBaseApi.getPosition(fault.getLineCode());
                    LoginUser userByName = sysBaseApi.getUserByName(record.getAppointUserName());
                    content.append(lineName).append("-").append(stationName).append(" ").append(record.getFaultPhenomenon()).append(" 维修人:").append(userByName.getRealname()).append("-");
                    if (record.getSolveStatus() == 1) {
                        content.append("维修完成。");
                    } else {
                        content.append("维修中。");
                    }
                    content.append("\n");
                    code.append(fault.getCode()).append(",");
                }
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
        return map;
    }

    /**
     * 班组画像获取维修工时
     * @param type
     * @param teamId
     * @return
     */
    @Override
    public Map<String, BigDecimal> getFaultUserHours(int type, String teamId) {
        Map<String, BigDecimal> userDurationMap = new HashMap<>(32);
        // 班组的人员
        List<LoginUser> userList = sysBaseApi.getUserPersonnel(teamId);
        String dateTime = FaultLargeDateUtil.getDateTime(type);
        Date startTime = DateUtil.parse(dateTime.split("~")[0]);
        Date endTime = DateUtil.parse(dateTime.split("~")[1]);

        // 获取维修人员在指定时间范围内的任务时长(单位秒)
        List<FaultDurationTask> faultUserDuration = faultInformationMapper.getFaultUserDuration(startTime, endTime);
        // 获取参与人员在指定时间范围内的任务时长(单位秒)
        List<FaultDurationTask> participantsDuration = faultInformationMapper.getFaultParticipantsDuration(startTime, endTime);

        Map<String, Long> durationMap = faultUserDuration.stream().collect(Collectors.toMap(k -> k.getUserId(),
                v -> ObjectUtil.isEmpty(v.getDuration()) ? 0L : v.getDuration(), (a, b) -> a));

        Map<String, Long> participantsMap = participantsDuration.stream().collect(Collectors.toMap(k -> k.getUserId(),
                v -> ObjectUtil.isEmpty(v.getDuration()) ? 0L : v.getDuration(), (a, b) -> a));

        userList.stream().forEach(l -> {
            String userId = l.getId();
            Long timeOne = durationMap.get(userId);
            Long timeTwo = participantsMap.get(userId);
            if (ObjectUtil.isEmpty(timeOne)) {
                timeOne = 0L;
            }
            if (ObjectUtil.isEmpty(timeTwo)) {
                timeTwo = 0L;
            }
            double time = 1.0 * (timeOne+timeTwo) / 3600;
            // 展示需要以小时数展示，并保留两位小数
            BigDecimal decimal = new BigDecimal(time).setScale(2, BigDecimal.ROUND_HALF_UP);
            userDurationMap.put(userId, decimal);
        });
        return userDurationMap;
    }

    /**
     * 班组画像获取维修工时（参与人和执行人不是同一个）
     * @param type
     * @param teamId
     * @return
     */
    @Override
    public BigDecimal getFaultHours(int type, String teamId) {
        // 班组的人员
        List<LoginUser> userList = sysBaseApi.getUserPersonnel(teamId);
        if (CollUtil.isNotEmpty(userList)) {
            String dateTime = FaultLargeDateUtil.getDateTime(type);
            Date startTime = DateUtil.parse(dateTime.split("~")[0]);
            Date endTime = DateUtil.parse(dateTime.split("~")[1]);

            // 获取指派人员在指定时间范围内的每一个任务的时长(单位秒)
            List<FaultDurationTask> faultByIdDuration = faultInformationMapper.getFaultByIdDuration(startTime, endTime, userList);
            // 获取参与人在指定时间范围内的每一个任务的任务时长(单位秒)
            List<FaultDurationTask> participantsByIdDuration = faultInformationMapper.getParticipantsDuration(startTime, endTime, userList);

            List<String> collect = faultByIdDuration.stream().map(FaultDurationTask::getTaskId).collect(Collectors.toList());
            //若参与人和指派人同属一个班组，则该班组只取一次工时，不能累加
            List<FaultDurationTask> dtos = participantsByIdDuration.stream().filter(t -> !collect.contains(t.getTaskId())).collect(Collectors.toList());
            dtos.addAll(faultByIdDuration);
            BigDecimal sum = new BigDecimal("0.00");
            for (FaultDurationTask dto : dtos) {
                sum = sum.add(new BigDecimal(dto.getDuration()));
            }
            //秒转时
            BigDecimal decimal = sum.divide(new BigDecimal("3600"), 1, BigDecimal.ROUND_HALF_UP);
            return decimal;
        }
    return new BigDecimal("0.00");
    }

    @Override
    public Map<String, FaultReportDTO> getFaultOrgReport(List<String> teamId, String startTime, String endTime) {
        Map<String, FaultReportDTO> map = new HashMap<>(32);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> sysDepartModels = sysBaseApi.getUserSysDepart(sysUser.getId());
        List<String> orgIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(teamId)){
            orgIds = teamId;
        }else {
            orgIds= sysDepartModels.stream().map(SysDepartModel::getId).collect(Collectors.toList());
        }
        orgIds.forEach(orgId->{
            FaultReportDTO f = faultInformationMapper.getFaultOrgReport(startTime,endTime,orgId);
            f.setConstructorsNum(faultInformationMapper.getConstructorsNum(startTime,endTime,orgId));
            //查询指派人任务时长
            List<UserTimeDTO> dtos = faultInformationMapper.getUserTime(f.getOrgId(),startTime,endTime);
            //查询参与人任务时长
            List<UserTimeDTO> userTimeDtos =faultInformationMapper.getAccompanyTime(f.getOrgId(),startTime,endTime);
            userTimeDtos = userTimeDtos.stream().parallel().filter(a -> dtos.stream().map(UserTimeDTO::getFrrId).collect(Collectors.toList()).contains(a.getFrrId()))
                    .collect(Collectors.toList());
            Long sum = userTimeDtos
                    .stream().filter(w-> w.getDuration() !=null)
                    .mapToLong(w -> w.getDuration())
                    .sum();
            f.setNum(f.getNum()+sum);
            List<String> str = faultInformationMapper.getConstructionHours(f.getOrgId(),startTime,endTime);
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
            if (f.getNum1()==0){
                f.setRepairTime("0");
            }else {
                Long s = (f.getNum()/f.getNum1())/60;
                f.setRepairTime(s.toString());
            }
            f.setFailureTime(new BigDecimal((1.0 * (f.getNum()) / 3600)).setScale(2, BigDecimal.ROUND_HALF_UP));
            BigDecimal totalPrice = doubles.stream().map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add);
            f.setConstructionHours(totalPrice.setScale(2,BigDecimal.ROUND_HALF_UP));
            map.put(f.getOrgId(),f);
        });
        return map;
    }

    @Override
    public Map<String, FaultReportDTO> getFaultUserReport(List<String> teamId, String startTime, String endTime,String userId) {
        Map<String, FaultReportDTO> map = new HashMap<>(32);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> sysDepartModels = sysBaseApi.getUserSysDepart(sysUser.getId());
        List<String> orgCodes = sysDepartModels.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
        List<LoginUser> loginUsers = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(teamId)){
           loginUsers= sysBaseApi.getUseList(teamId);
        }else {
          loginUsers = sysBaseApi.getUserByDepIds(orgCodes);
        }
        loginUsers.forEach(f->{
            FaultReportDTO faultReportDTO = faultInformationMapper.getFaultUserReport(teamId,startTime,endTime,orgCodes,f.getId());
            Long sum = faultInformationMapper.getUserTimes(f.getId(),startTime,endTime);
            faultReportDTO.setNum(faultReportDTO.getNum()+sum);
            List<String> str = faultInformationMapper.getUserConstructionHours(f.getId(),startTime,endTime);
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
            FaultReportDTO  fau = faultInformationMapper.getUserConstructorsNum(f.getId(),startTime,endTime);
            if (fau.getNum1()==0){
                faultReportDTO.setRepairTime("0");
            }else {
                Long s = (faultReportDTO.getNum()/fau.getNum1())/60;
                faultReportDTO.setRepairTime(s.toString());
            }
            faultReportDTO.setConstructorsNum(fau.getConstructorsNum());
            faultReportDTO.setFailureTime(new BigDecimal((1.0 * (faultReportDTO.getNum()) / 3600)).setScale(2, BigDecimal.ROUND_HALF_UP));
            BigDecimal totalPrice = doubles.stream().map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add);
            faultReportDTO.setConstructionHours(totalPrice.setScale(2,BigDecimal.ROUND_HALF_UP));
            map.put(f.getId(),faultReportDTO);
        });
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
