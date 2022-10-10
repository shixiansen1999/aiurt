package com.aiurt.boot.personnelteam.service;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
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
              return this.getList(startDate, endDate, collect, null);
          }

          if (CollectionUtil.isNotEmpty(teamId)){
              return this.getList(startDate, endDate, teamId, null);
          }

          if (CollectionUtil.isEmpty(teamId)){
              return this.getList(startDate, endDate, null, userId);
          }

          return new HashMap<>(16);
      }

    private Map<String, PersonnelTeamDTO> getList(Date startDate, Date endDate, List<String> teamId,String userId){
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

            //总工时
            PersonnelTeamDTO time = personnelTeamMapper.getUserTime(key, startDate, endDate);
            personnelTeamDTO.setOverhaulWorkingHours(time.getCounter());

            PersonnelTeamDTO q = collect3.get(key);
            PersonnelTeamDTO e = entry.getValue();
            //查询人员的计划任务数量
            Long counter1 = e.getCounter();
            personnelTeamDTO.setPlanTaskNumber(counter1);

            //查询人员的完成任务数量
            Long counter2 = q.getCounter();
            personnelTeamDTO.setCompleteTaskNumber(counter2);

            //计划完成率
            BigDecimal div = NumberUtil.div(counter2, counter1);
            String string = NumberUtil.roundStr(String.valueOf(div), 2);
            personnelTeamDTO.setPlanCompletionRate(string);

            map.put(key, personnelTeamDTO);
        }
    }


    @Override
    public Map<String, PersonnelTeamDTO> teamInformation (Date startDate, Date endDate, List<String> teamId){

        Map<String,PersonnelTeamDTO> map = new HashMap<>(16);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //根据登录人的id查询所属班组
        List<SysDepartModel> userSysDepart = sysBaseAPI.getUserSysDepart(sysUser.getId());
        //所属班组id的list集合
        List<String> collect = userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());



        return map;
    }
}
