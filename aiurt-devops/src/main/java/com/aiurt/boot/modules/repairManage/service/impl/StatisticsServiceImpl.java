package com.aiurt.boot.modules.repairManage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.repairManage.entity.RepairPool;
import com.aiurt.boot.modules.repairManage.entity.RepairTask;
import com.aiurt.boot.modules.repairManage.mapper.RepairPoolMapper;
import com.aiurt.boot.modules.repairManage.mapper.RepairTaskMapper;
import com.aiurt.boot.modules.repairManage.service.IStatisticsService;
import com.aiurt.boot.modules.repairManage.vo.RepairItemVO;
import com.aiurt.boot.modules.repairManage.vo.StatisticsQueryVO;
import com.aiurt.boot.modules.repairManage.vo.TimeVO;
import com.aiurt.boot.modules.repairManage.vo.WorkLoadVO;
import com.aiurt.common.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/27 17:06
 */
@Service
public class StatisticsServiceImpl implements IStatisticsService {

    @Autowired
    private RepairTaskServiceImpl repairTaskService;

    @Resource
    private RepairTaskMapper repairTaskMapper;

    @Resource
    private RepairPoolMapper repairPoolMapper;

    @Resource
    private IStationService stationService;

    @Override
    public Result workload(StatisticsQueryVO statisticsQueryVO) {
        LambdaQueryWrapper<RepairTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairTask::getDelFlag, 0)
                .ge(RepairTask::getSubmitTime, statisticsQueryVO.getStartTime().concat(" 00:00:00"))
                .le(RepairTask::getSubmitTime, statisticsQueryVO.getEndTime().concat(" 23:59:59"));
        if (statisticsQueryVO.getTeamId() != null) {
            List<Station> stationList = stationService.list(new QueryWrapper<Station>().eq("team_id", statisticsQueryVO.getTeamId()).eq("del_flag", 0));
            final List<String> teamIdList = stationList.stream().map(Station::getTeamId).collect(Collectors.toList());
            wrapper.in(RepairTask::getOrganizationId,teamIdList);
        }
        if (StrUtil.isNotBlank(statisticsQueryVO.getUserName())) {
            wrapper.like(RepairTask::getSumitUserName, statisticsQueryVO.getUserName());
        }
        //根据筛选条件获取检修记录
        List<RepairTask> taskList = repairTaskMapper.selectList(wrapper);
        //按姓名分组
        Map<String, List<RepairTask>> map = taskList.stream().collect(Collectors.groupingBy(RepairTask::getSumitUserName));

        final ArrayList<WorkLoadVO> list = new ArrayList<>();
        for (String userName : map.keySet()) {
            final WorkLoadVO vo = new WorkLoadVO();
            vo.setUserName(userName);
            Integer repairDuration = 0;
            Integer confirmAmount = 0;
            Integer acceptAmount = 0;
            Integer needReceiptAmount = 0;
            for (RepairTask repairTask : map.get(userName)) {
                final Date submitTime = repairTask.getSubmitTime();
                final Date createTime = repairTask.getCreateTime();
                //计算检修时长
                long minutes = ChronoUnit.MINUTES.between(Instant.ofEpochMilli(createTime.getTime()), Instant.ofEpochMilli(submitTime.getTime()));
                repairDuration = repairDuration + Integer.valueOf(String.valueOf(minutes));
                if (repairTask.getStatus() >= 2) {
                    confirmAmount++;
                    //统计需要验收的数量
                    if (repairTask.getIsReceipt() == 1) {
                        needReceiptAmount++;
                    }
                }
                if (repairTask.getStatus() >= 4) {
                    acceptAmount++;
                }
            }
            final int size = map.get(userName).size();
            vo.setRepairDuration(repairDuration);
            vo.setRepaireAmount(size);
            vo.setConfirmAmount(confirmAmount);
            vo.setUnconfirmAmout(size - confirmAmount);
            vo.setAcceptAmount(acceptAmount);
            vo.setUnacceptAmount(needReceiptAmount - acceptAmount);
            list.add(vo);
        }
        return Result.ok(list);
    }

    @Override
    public Result repairItem() {

        final RepairItemVO repairItemVO = new RepairItemVO();

        //已完成
        List<RepairTask> completeList = new ArrayList<>();
        LambdaQueryWrapper<RepairTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairTask::getDelFlag, 0);
        wrapper.likeRight(RepairTask::getCreateTime, DateUtils.getYear());
        wrapper.isNotNull(RepairTask::getSubmitTime);
        completeList = repairTaskMapper.selectList(wrapper);
        //未完成
        List<RepairTask> uncompleteList = new ArrayList<>();
        LambdaQueryWrapper<RepairTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RepairTask::getDelFlag, 0);
        queryWrapper.likeRight(RepairTask::getCreateTime, DateUtils.getYear());
        queryWrapper.isNull(RepairTask::getSubmitTime);
        uncompleteList = repairTaskMapper.selectList(queryWrapper);

        List<String> list = completeList.stream().map(RepairTask::getRepairPoolIds).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)){
            return Result.ok(repairItemVO);
        }
        //获取检修池记录
        final List<RepairPool> poolList = repairPoolMapper.selectList(new LambdaQueryWrapper<RepairPool>().in(RepairPool::getId, list));
        //按检修类型分组
        final Map<Integer, List<RepairPool>> listMap = poolList.stream().collect(Collectors.groupingBy(RepairPool::getType));
        //计算 已完成的数量
        Integer weekComplete = 0;
        Integer monthComplete = 0;
        Integer DmonthComplete = 0;
        Integer quarterComplete = 0;
        Integer semiAnnualComplete = 0;
        Integer annualComplete = 0;
        final Optional<List<RepairPool>> weekCompleteOpt = Optional.ofNullable(listMap.get(1));
        if (weekCompleteOpt != null && weekCompleteOpt.isPresent()) {
            weekComplete = weekCompleteOpt.get().size();
        }
        final Optional<List<RepairPool>> monthCompleteOpt = Optional.ofNullable(listMap.get(2));
        if (monthCompleteOpt != null && monthCompleteOpt.isPresent()) {
            monthComplete = monthCompleteOpt.get().size();
        }
        final Optional<List<RepairPool>> DmonthCompleteOpt = Optional.ofNullable(listMap.get(3));
        if (DmonthCompleteOpt != null && DmonthCompleteOpt.isPresent()) {
            DmonthComplete = DmonthCompleteOpt.get().size();
        }
        final Optional<List<RepairPool>> quarterCompleteOpt = Optional.ofNullable(listMap.get(4));
        if (quarterCompleteOpt != null && quarterCompleteOpt.isPresent()) {
            quarterComplete = quarterCompleteOpt.get().size();
        }
        final Optional<List<RepairPool>> semiAnnualCompleteOpt = Optional.ofNullable(listMap.get(5));
        if (semiAnnualCompleteOpt != null && semiAnnualCompleteOpt.isPresent()) {
            semiAnnualComplete = semiAnnualCompleteOpt.get().size();
        }
        final Optional<List<RepairPool>> annualCompleteOpt = Optional.ofNullable(listMap.get(6));
        if (annualCompleteOpt != null && annualCompleteOpt.isPresent()) {
            annualComplete = annualCompleteOpt.get().size();
        }

        //计算未完成的数量
        final List<String> collect = uncompleteList.stream().map(RepairTask::getRepairPoolIds).collect(Collectors.toList());
        //获取检修池记录
        final List<RepairPool> repairPoolList = repairPoolMapper.selectList(new LambdaQueryWrapper<RepairPool>().in(RepairPool::getId, collect));
        //按检修类型分组
        final Map<Integer, List<RepairPool>> map = repairPoolList.stream().collect(Collectors.groupingBy(RepairPool::getType));

        Integer weekUnComplete = 0;
        Integer monthUnComplete = 0;
        Integer DmonthUnComplete = 0;
        Integer quarterUnComplete = 0;
        Integer semiAnnualUnComplete = 0;
        Integer annualUnComplete = 0;

        final Optional<List<RepairPool>> weekUnCompleteOpt = Optional.ofNullable(map.get(1));
        if (weekUnCompleteOpt != null && weekUnCompleteOpt.isPresent()) {
            weekUnComplete = weekUnCompleteOpt.get().size();
        }
        final Optional<List<RepairPool>> monthUnCompleteOpt = Optional.ofNullable(map.get(2));
        if (monthUnCompleteOpt != null && monthUnCompleteOpt.isPresent()) {
            monthUnComplete = monthUnCompleteOpt.get().size();
        }
        final Optional<List<RepairPool>> DmonthUnCompleteOpt = Optional.ofNullable(map.get(3));
        if (DmonthUnCompleteOpt != null && DmonthUnCompleteOpt.isPresent()) {
            DmonthUnComplete = DmonthUnCompleteOpt.get().size();
        }
        final Optional<List<RepairPool>> quarterUnCompleteOpt = Optional.ofNullable(map.get(4));
        if (quarterUnCompleteOpt != null && quarterUnCompleteOpt.isPresent()) {
            quarterUnComplete = quarterUnCompleteOpt.get().size();
        }
        final Optional<List<RepairPool>> semiAnnualUnCompleteOpt = Optional.ofNullable(map.get(5));
        if (semiAnnualUnCompleteOpt != null && semiAnnualUnCompleteOpt.isPresent()) {
            semiAnnualUnComplete = semiAnnualUnCompleteOpt.get().size();
        }
        final Optional<List<RepairPool>> annualUnCompleteOpt = Optional.ofNullable(map.get(6));
        if (annualUnCompleteOpt != null && annualUnCompleteOpt.isPresent()) {
            annualUnComplete = annualUnCompleteOpt.get().size();
        }

        repairItemVO.setWeekComplete(weekComplete);
        repairItemVO.setMonthComplete(monthComplete);
        repairItemVO.setDmonthComplete(DmonthComplete);
        repairItemVO.setQuarterComplete(quarterComplete);
        repairItemVO.setSemiAnnualComplete(semiAnnualComplete);
        repairItemVO.setAnnualComplete(annualComplete);
        repairItemVO.setWeekUnComplete(weekUnComplete);
        repairItemVO.setMonthUnComplete(monthUnComplete);
        repairItemVO.setDmonthUnComplete(DmonthUnComplete);
        repairItemVO.setQuarterUnComplete(quarterUnComplete);
        repairItemVO.setSemiAnnualUnComplete(semiAnnualUnComplete);
        repairItemVO.setAnnualUnComplete(annualUnComplete);
        return Result.ok(repairItemVO);
    }

    @Override
    public Result compareToTeam(TimeVO timeVO) {
        LambdaQueryWrapper<RepairTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairTask::getDelFlag, 0)
                .ge(RepairTask::getCreateTime, timeVO.getStartTime().concat(" 00:00:00"))
                .le(RepairTask::getCreateTime, timeVO.getEndTime().concat(" 23:59:59"));
        List<RepairTask> taskList = repairTaskMapper.selectList(wrapper);
        //按姓名分组
        taskList.forEach(x -> x.setTeamName(stationService.getById(x.getOrganizationId()).getTeamName()));
        //按班组分类
        final Map<String, List<RepairTask>> map = taskList.stream().collect(Collectors.groupingBy(RepairTask::getTeamName));
        //获取每个班组 已完成和未完成的数量
        final ArrayList<Object> result = new ArrayList<>();
        for (String teamName : map.keySet()) {
            final HashMap<String, Object> stringIntegerHashMap = new HashMap<>();
            final List<RepairTask> list = map.get(teamName);
            Integer complete = 0;
            Integer uncomplete = 0;
            for (RepairTask repairTask : list) {
                if (repairTask.getSubmitTime() != null) {
                    complete++;
                } else {
                    uncomplete++;
                }
            }
            stringIntegerHashMap.put("teamName",teamName);
            stringIntegerHashMap.put("complete", complete);
            stringIntegerHashMap.put("uncomplete", uncomplete);
            result.add(stringIntegerHashMap);
        }
        return Result.ok(result);
    }

}
