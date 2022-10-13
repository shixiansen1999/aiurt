package com.aiurt.boot.personnelteam.service;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.OverhaulApi;
import com.aiurt.boot.personnelteam.mapper.PersonnelTeamMapper;
import com.aiurt.boot.task.dto.PersonnelTeamDTO;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zwl
 */
@Service
public class PersonnelTeamService implements OverhaulApi {

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private PersonnelTeamMapper personnelTeamMapper;

      @Override
      public Map<String, PersonnelTeamDTO> personnelInformation (Date startDate, Date endDate, List<String> teamId,String userId){
          LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
          //根据登录人的id查询所属班组
          List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(sysUser.getId());
          //所属班组id的list集合
          List<String> collect = userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());

          if (CollectionUtil.isEmpty(teamId) && CollectionUtil.isNotEmpty(collect)){
              return this.getUserList(startDate, endDate, collect, null);
          }

          if (CollectionUtil.isNotEmpty(teamId)){
              return this.getUserList(startDate, endDate, teamId, null);
          }

          if (CollectionUtil.isEmpty(teamId)){
              return this.getUserList(startDate, endDate, null, userId);
          }

          return new HashMap<>(16);
      }

    private Map<String, PersonnelTeamDTO> getUserList(Date startDate, Date endDate, List<String> teamId,String userId){
            Map<String,PersonnelTeamDTO> map = new HashMap<>(16);
            if (CollectionUtil.isNotEmpty(teamId)){
                List<LoginUser> useList = sysBaseAPI.getUseList(teamId);
                //获取人员id
                List<String> collect1 = useList.stream().map(LoginUser::getId).collect(Collectors.toList());

                //查询人员的计划任务数量
                List<PersonnelTeamDTO> scheduledTask1 = personnelTeamMapper.getScheduledTask(collect1, null, startDate, endDate,null);

                //查询人员的完成任务数量
                List<PersonnelTeamDTO> scheduledTask2 = personnelTeamMapper.getScheduledTask(collect1, 8L, startDate, endDate,null);
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

        for (Map.Entry<String, PersonnelTeamDTO> entry : collect2.entrySet()) {
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
                personnelTeamDTO.setOverhaulWorkingHours(l);
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
                if (personnelTeamDTO.getCompleteTaskNumber()!=null && personnelTeamDTO.getPlanTaskNumber()!=null) {
                    BigDecimal div = NumberUtil.div(personnelTeamDTO.getCompleteTaskNumber(), personnelTeamDTO.getPlanTaskNumber());
                    String string = NumberUtil.roundStr(String.valueOf(div), 2);
                    if (StrUtil.isNotEmpty(string)) {
                        personnelTeamDTO.setPlanCompletionRate(string);
                    } else {
                        personnelTeamDTO.setPlanCompletionRate("0");
                    }
                }else {
                    personnelTeamDTO.setPlanCompletionRate("0");
                }
            } else {
                personnelTeamDTO.setPlanCompletionRate("0");
            }
            map.put(key, personnelTeamDTO);
        }
    }


    @Override
    public Map<String, PersonnelTeamDTO> teamInformation (Date startDate, Date endDate, List<String> teamId){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //根据登录人的id查询所属班组
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(sysUser.getId());

        if(CollectionUtil.isNotEmpty(userSysDepart)){
            //所属班组id的list集合
            List<String> collect = userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());

            //所属班组code的list集合
            List<String> collect1 = userSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());


            if (CollectionUtil.isNotEmpty(teamId)){

                //根据班组id查询班组code
                List<String> codeList = personnelTeamMapper.getIdList(teamId);

                return getTeamList(startDate, endDate, teamId,codeList);

            }else {
                return getTeamList(startDate, endDate, collect,collect1);
            }
        }
        return new HashMap<>(16);
    }



    public Map<String, PersonnelTeamDTO> getTeamList(Date startDate, Date endDate, List<String> teamId , List<String> codeList){
        Map<String,PersonnelTeamDTO> map = new HashMap<>(16);
        //查询班组下的人员信息
        List<LoginUser> useList = sysBaseAPI.getUseList(teamId);

        //获取人员id
        List<String> collect1 = useList.stream().map(LoginUser::getId).collect(Collectors.toList());

        //查询班组所有的计划任务数
        List<PersonnelTeamDTO> teamTask = personnelTeamMapper.getTeamTask(codeList, null, startDate, endDate);

        //获取班组的codeMap
        Map<String, PersonnelTeamDTO> collect2 = teamTask.stream().collect(Collectors.toMap(PersonnelTeamDTO::getTeamCode, v -> v));

        if (CollectionUtil.isNotEmpty(collect1)){
            //查询班组所属人员的所有已完成的任务
            List<PersonnelTeamDTO> scheduledTask = personnelTeamMapper.getScheduledTask(collect1, 8L, startDate, endDate,null);

            //获取班组所属用户idMap
            Map<String, PersonnelTeamDTO> collect4 = scheduledTask.stream().collect(Collectors.toMap(PersonnelTeamDTO::getUserId, v -> v));

            //获取班组所属用户idList
            List<String> collect5 = scheduledTask.stream().map(PersonnelTeamDTO::getUserId).collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(collect2)) {

                for (Map.Entry<String, PersonnelTeamDTO> entry : collect2.entrySet()) {

                    PersonnelTeamDTO personnelTeamDTO = new PersonnelTeamDTO();
                    //班组计划任务数量
                    PersonnelTeamDTO value = entry.getValue();
                    Long counter1 = value.getCounter();
                    personnelTeamDTO.setPlanTaskNumber(counter1);

                    String id = personnelTeamMapper.getId(entry.getKey());

                    if (CollectionUtil.isNotEmpty(collect4)) {

                        for (Map.Entry<String, PersonnelTeamDTO> entry1 : collect4.entrySet()) {

                            //根据用户id查询班组编码
                            LoginUser userById = sysBaseAPI.getUserById(entry1.getKey());
                            String orgCode = userById.getOrgCode();
                            if (entry.getKey().equals(orgCode)) {

                                //班组完成任务数量
                                PersonnelTeamDTO value1 = entry1.getValue();
                                Long counter2 = value1.getCounter();
                                personnelTeamDTO.setCompleteTaskNumber(counter2);

                                //计划完成率
                                BigDecimal div = NumberUtil.div(counter2, counter1);
                                String string = NumberUtil.roundStr(String.valueOf(div), 2);
                                personnelTeamDTO.setPlanCompletionRate(string);

                                if (CollectionUtil.isNotEmpty(collect5)){
                                    //过滤掉不是同一班组的人员
                                    List<String> collect3 = collect5.stream().filter(q -> q.equals(entry1.getKey())).collect(Collectors.toList());

                                    if (CollectionUtil.isNotEmpty(collect3)){
                                        //获取所有检修任务人员总工时和所有同行人总工时
                                        List<PersonnelTeamDTO> teamTime = personnelTeamMapper.getTeamTime(collect3, startDate, endDate);
                                        List<PersonnelTeamDTO> teamPeerTime = personnelTeamMapper.getTeamPeerTime(collect3, startDate, endDate);
                                        List<String> collect = teamTime.stream().map(PersonnelTeamDTO::getTaskId).collect(Collectors.toList());
                                        //若同行人和指派人同属一个班组，则该班组只取一次工时，不能累加
                                        List<PersonnelTeamDTO> dtos = teamPeerTime.stream().filter(t -> !collect.contains(t.getTaskId())).collect(Collectors.toList());
                                        dtos.addAll(teamTime);
                                        BigDecimal sum = new BigDecimal("0.00");
                                        for (PersonnelTeamDTO dto : dtos) {
                                            sum = sum.add(dto.getInspecitonTotalTime());
                                        }
                                        //秒转时
                                        BigDecimal decimal = sum.divide(new BigDecimal("3600"),1, BigDecimal.ROUND_HALF_UP);
                                        personnelTeamDTO.setOverhaulWorkingHours(decimal.longValue());
                                    }else {
                                        personnelTeamDTO.setOverhaulWorkingHours(0L);
                                    }
                                }

                            }
                        }
                    }
                    personnelTeamDTO.setTeamId(id);
                    map.put(id, personnelTeamDTO);
                }
            }
        }
          return map;
    }
}
