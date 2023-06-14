package com.aiurt.boot.personnelteam.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.api.OverhaulApi;
import com.aiurt.boot.bigscreen.mapper.BigScreenPlanMapper;
import com.aiurt.boot.index.dto.TaskUserDTO;
import com.aiurt.boot.index.dto.TeamUserDTO;
import com.aiurt.boot.personnelteam.mapper.PersonnelTeamMapper;
import com.aiurt.boot.task.dto.PersonnelTeamDTO;
import io.prometheus.client.Collector;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zwl
 */
@Slf4j
@Service
public class PersonnelTeamService implements OverhaulApi {

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private PersonnelTeamMapper personnelTeamMapper;

    @Autowired
    private BigScreenPlanMapper bigScreenPlanMapper;

      @Override
      public Map<String, PersonnelTeamDTO> personnelInformation (Date startDate, Date endDate, List<String> teamId,String userId,List<String> userIds){

          if (CollectionUtil.isNotEmpty(userIds)){
              return this.getUserList(startDate, endDate, userIds, null);
          }

          if (CollectionUtil.isEmpty(userIds)){
              return this.getUserList(startDate, endDate, null, userId);
          }

          return new HashMap<>(16);
      }

    private Map<String, PersonnelTeamDTO> getUserList(Date startDate, Date endDate, List<String> userIds,String userId){
            Map<String,PersonnelTeamDTO> map = new HashMap<>(16);
            if (CollectionUtil.isNotEmpty(userIds)){
                //查询人员的计划任务数量
                List<PersonnelTeamDTO> scheduledTask1 = personnelTeamMapper.getScheduledTask(userIds, null, startDate, endDate,null);

                //查询人员的完成任务数量
                List<PersonnelTeamDTO> scheduledTask2 = personnelTeamMapper.getScheduledTask(userIds, 8L, startDate, endDate,null);
                if (CollectionUtil.isNotEmpty(scheduledTask1) && CollectionUtil.isNotEmpty(scheduledTask2)){
                    Map<String, PersonnelTeamDTO> collect2 = scheduledTask1.stream().collect(Collectors.toMap(PersonnelTeamDTO::getUserId, v -> v));
                    getNumber(startDate, endDate, map, collect2, scheduledTask2);
                }

            }else {
                //查询人员的计划任务数量
                List<PersonnelTeamDTO> scheduledTask1 = personnelTeamMapper.getScheduledTask(null, null, startDate, endDate, userId);
                //查询人员的完成任务数量
                List<PersonnelTeamDTO> scheduledTask2 = personnelTeamMapper.getScheduledTask(null, 8L, startDate, endDate, userId);
                if (CollectionUtil.isNotEmpty(scheduledTask1) && CollectionUtil.isNotEmpty(scheduledTask2)){
                    Map<String, PersonnelTeamDTO> collect2 = scheduledTask1.stream().collect(Collectors.toMap(PersonnelTeamDTO::getUserId, v -> v));
                    getNumber(startDate, endDate, map, collect2, scheduledTask2);
                }
            }

          return map;
    }

    private void getNumber(Date startDate, Date endDate, Map<String, PersonnelTeamDTO> map, Map<String, PersonnelTeamDTO> collect2, List<PersonnelTeamDTO> scheduledTask2) {
        Map<String, PersonnelTeamDTO> collect3 = scheduledTask2.stream().collect(Collectors.toMap(PersonnelTeamDTO::getUserId, v -> v));
        //线程处理
        ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(3, 5);
        if (CollectionUtil.isNotEmpty(collect2)){
            for (Map.Entry<String, PersonnelTeamDTO> entry : collect2.entrySet()) {
                threadPoolExecutor.execute(() -> {
                    PersonnelTeamDTO personnelTeamDTO = new PersonnelTeamDTO();
                    String key = entry.getKey();

                    personnelTeamDTO.setUserId(key);

                    //检修人任务的工时
                    PersonnelTeamDTO userTime = personnelTeamMapper.getUserTime(key, startDate, endDate);
                    //同行人任务的工时
                    PersonnelTeamDTO peerTime = personnelTeamMapper.getUserPeerTime(key, startDate, endDate);
                    if(userTime.getCounter()!=null && peerTime.getCounter()!=null){
                        long l = userTime.getCounter() + peerTime.getCounter();
                        //检修人任务的总工时
                        //秒转时
                        BigDecimal decimal = new BigDecimal(l).divide(new BigDecimal("3600"),2, BigDecimal.ROUND_HALF_UP);
                        personnelTeamDTO.setOverhaulWorkingHours(decimal.floatValue());
                    }else {
                        personnelTeamDTO.setOverhaulWorkingHours(0L);
                    }

                    PersonnelTeamDTO q = collect3.get(key);
                    PersonnelTeamDTO e = entry.getValue();

                    //查询人员的计划任务数量
                    Long counter1 = e.getCounter();
                    if(counter1!=null){
                        personnelTeamDTO.setPlanTaskNumber(counter1);
                    }else {
                        personnelTeamDTO.setPlanTaskNumber(0L);
                    }

                    if (ObjectUtil.isNotEmpty(e) && ObjectUtil.isNotEmpty(q)) {

                        //查询人员的完成任务数量
                        Long counter2 = q.getCounter();
                        if (counter2!=null){
                            personnelTeamDTO.setCompleteTaskNumber(counter2);
                        }else {
                            personnelTeamDTO.setCompleteTaskNumber(0L);
                        }
                        //计划完成率
                        if (personnelTeamDTO.getCompleteTaskNumber() != null && personnelTeamDTO.getPlanTaskNumber() != null && personnelTeamDTO.getPlanTaskNumber() != 0) {
                            BigDecimal div = NumberUtil.div(personnelTeamDTO.getCompleteTaskNumber(), personnelTeamDTO.getPlanTaskNumber());
                            BigDecimal multiply = div.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                            personnelTeamDTO.setPlanCompletionRate(Convert.toStr(multiply));

                        } else {
                            personnelTeamDTO.setPlanCompletionRate("0");
                        }
                    } else {
                        personnelTeamDTO.setPlanCompletionRate("0");
                        personnelTeamDTO.setCompleteTaskNumber(0L);
                    }
                    map.put(key, personnelTeamDTO);
                });
            }
        }
        threadPoolExecutor.shutdown();
        try {
            // 等待线程池中的任务全部完成
            threadPoolExecutor.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 处理中断异常
            log.info("循环方法的线程中断异常", e.getMessage());
        }
    }


    @Override
    public Map<String, PersonnelTeamDTO> teamInformation (Date startDate, Date endDate, List<String> teamId){

        if (CollectionUtil.isNotEmpty(teamId)){
            //根据班组id查询班组code
            List<String> codeList = personnelTeamMapper.getIdList(teamId);
            return getTeamList(startDate, endDate, teamId,codeList);
        }
        return new HashMap<>(16);
    }

    @Override
    public Map<String, Integer> getTeamInspecitonTotalTime(Date startTime, Date endTime, List<String> teamIdList) {
        Map<String,Integer> teamInspecitonTotalTime = new ConcurrentHashMap<>();
        // 获取班组下的成员列表，根据班组id为key组成一个map
        Map<String, List<LoginUser>> userMap = sysBaseAPI.getUseList(teamIdList)
                .stream()
                .collect(Collectors.groupingBy(LoginUser::getOrgId));
        //线程处理
        ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(3, 5);
        teamIdList.forEach(teamId->{
            threadPoolExecutor.execute(()->{
                List<LoginUser> userList = userMap.get(teamId);
                Map<String, Integer> userInspecitonTotalTimeMap = getUserInspecitonTotalTime(startTime, endTime, userList);
                int sum = userInspecitonTotalTimeMap.values().stream().mapToInt(Integer::intValue).sum();
                teamInspecitonTotalTime.put(teamId, sum);
            });
        });
        threadPoolExecutor.shutdown();
        try {
            // 等待线程池中的任务全部完成
            threadPoolExecutor.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 处理中断异常
            log.info("循环方法的线程中断异常, {}", e.getMessage());
        }

        return teamInspecitonTotalTime;
    }

    @Override
    public Map<String, Integer> getUserInspecitonTotalTime(Date startTime, Date endTime, List<LoginUser> userList) {
        Map<String, Integer> userInspecitonTotalTimeMap = new HashMap<>();
        //获取检修任务人员个人总工时和同行人个人总工时
        Map<String, Long> collect1;
        Map<String, Long> collect2;

        // 使用大屏的统计工时的方法，需要转成TeamUserDTO类的user列表
        List<TeamUserDTO> users = userList.stream().map(u -> {
            TeamUserDTO teamUserDTO = new TeamUserDTO();
            teamUserDTO.setUserId(u.getId());
            return teamUserDTO;
        }).collect(Collectors.toList());

        List<TeamUserDTO> reconditionTime = bigScreenPlanMapper.getReconditionTime(users, startTime, endTime);
        List<TeamUserDTO> reconditionTimeByPeer = bigScreenPlanMapper.getReconditionTimeByPeer(users, startTime, endTime);
        collect1 = reconditionTime.stream().collect(Collectors.toMap(TeamUserDTO::getUserId,
                v -> ObjectUtil.isEmpty(v.getTime()) ? 0L : v.getTime(), (a, b) -> a));
        collect2 = reconditionTimeByPeer.stream().collect(Collectors.toMap(TeamUserDTO::getUserId,
                v -> ObjectUtil.isEmpty(v.getTime()) ? 0L : v.getTime(), (a, b) -> a));
        for (TeamUserDTO teamUserDTO : users){
            //获取检修个人总总工时
            Long hours = collect1.get(teamUserDTO.getUserId());
            Long peerHours = collect2.get(teamUserDTO.getUserId());
            long time = 0L;
            if (hours != null) {
                time = time + hours;
            }
            if (peerHours != null) {
                time = time + peerHours;
            }
            userInspecitonTotalTimeMap.put(teamUserDTO.getUserId(), (int)time);
        }
        return userInspecitonTotalTimeMap;
    }


    public Map<String, PersonnelTeamDTO> getTeamList(Date startDate, Date endDate, List<String> teamId , List<String> codeList){
        Map<String,PersonnelTeamDTO> map = new HashMap<>(16);
        //查询班组下的人员信息
        List<LoginUser> useList = sysBaseAPI.getUseList(teamId);

        //查询班组所有的计划任务数
        List<PersonnelTeamDTO> teamTask = personnelTeamMapper.getTeamTask(codeList, null, startDate, endDate);
        //获取班组的codeMap
        Map<String, PersonnelTeamDTO> collect2 = teamTask.stream().collect(Collectors.toMap(PersonnelTeamDTO::getTeamCode, v -> v));

        //线程处理
        ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(3, 5);
        if (CollectionUtil.isNotEmpty(collect2)){
            for (Map.Entry<String, PersonnelTeamDTO> entry : collect2.entrySet()) {
                threadPoolExecutor.execute(() -> {
                    countTeamList(entry,useList, startDate, endDate, map);
                });
            }
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

    private void countTeamList(Map.Entry<String, PersonnelTeamDTO> entry,List<LoginUser> useList,Date startDate, Date endDate,Map<String,PersonnelTeamDTO> map) {
        //获取人员id
        List<String> collect1 = useList.stream().filter(l->l.getOrgCode().equals(entry.getKey())).map(LoginUser::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(collect1)) {
            //查询班组所属人员的所有已完成的任务
            List<PersonnelTeamDTO> scheduledTask = personnelTeamMapper.getScheduledTask(collect1, 8L, startDate, endDate,null);
            //获取班组所属用户idMap
            //Map<String, PersonnelTeamDTO> collect4 = scheduledTask.stream().collect(Collectors.toMap(PersonnelTeamDTO::getUserId, v -> v));
            //获取班组所属用户idList
            List<String> collect5 = scheduledTask.stream().map(PersonnelTeamDTO::getUserId).collect(Collectors.toList());

            PersonnelTeamDTO personnelTeamDTO = new PersonnelTeamDTO();
            //班组计划任务数量
            PersonnelTeamDTO value = entry.getValue();
            Long counter1 = value.getCounter();
            personnelTeamDTO.setPlanTaskNumber(counter1);

            String id = personnelTeamMapper.getId(entry.getKey());

            personnelTeamDTO.setCompleteTaskNumber(0L);
            personnelTeamDTO.setPlanCompletionRate("0");
            personnelTeamDTO.setOverhaulWorkingHours(0L);

            //班组完成任务数量
            personnelTeamDTO.setCompleteTaskNumber(value.getCompleteTaskNumber());
            //计划完成率
            if (counter1 != 0L) {
                BigDecimal div = NumberUtil.div(value.getCompleteTaskNumber(), counter1);
                BigDecimal multiply = div.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                personnelTeamDTO.setPlanCompletionRate(Convert.toStr(multiply));
            }

            if (CollectionUtil.isNotEmpty(collect5)){
                //获取所有检修任务人员总工时和所有同行人总工时
                List<PersonnelTeamDTO> teamTime = personnelTeamMapper.getTeamTime(collect5, startDate, endDate);
                List<PersonnelTeamDTO> teamPeerTime = personnelTeamMapper.getTeamPeerTime(collect5, startDate, endDate);
                List<String> collect = teamTime.stream().map(PersonnelTeamDTO::getTaskId).collect(Collectors.toList());
                //若同行人和指派人同属一个班组，则该班组只取一次工时，不能累加
                List<PersonnelTeamDTO> dtos = teamPeerTime.stream().filter(t -> !collect.contains(t.getTaskId())).collect(Collectors.toList());
                dtos.addAll(teamTime);
                BigDecimal sum = new BigDecimal("0.00");
                for (PersonnelTeamDTO dto : dtos) {
                    if (ObjectUtil.isNotEmpty(dto.getInspecitonTotalTime())) {
                        sum = sum.add(dto.getInspecitonTotalTime());
                    }
                }
                //秒转时
                BigDecimal decimal = sum.divide(new BigDecimal("3600"),2, BigDecimal.ROUND_HALF_UP);
                personnelTeamDTO.setOverhaulWorkingHours(decimal.floatValue());
            }else {
                personnelTeamDTO.setOverhaulWorkingHours(0L);
            }
            personnelTeamDTO.setTeamId(id);
            map.put(id, personnelTeamDTO);
        }
    }
}
