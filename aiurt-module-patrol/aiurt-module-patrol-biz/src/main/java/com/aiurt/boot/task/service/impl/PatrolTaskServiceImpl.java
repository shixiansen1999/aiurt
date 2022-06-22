package com.aiurt.boot.task.service.impl;


import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.mapper.PatrolTaskUserMapper;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolTaskServiceImpl extends ServiceImpl<PatrolTaskMapper, PatrolTask> implements IPatrolTaskService {

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;
    @Override
    public IPage<PatrolTask> getTaskList(Page<PatrolTask> page, PatrolTask patrolTask) {
        return patrolTaskMapper.getTaskList(page, patrolTask);
    }

    @Override
    public Page<PatrolTaskDTO> getPatrolTaskList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO) {
        List<PatrolTaskDTO> taskList = patrolTaskMapper.getPatrolTaskList(pageList,patrolTaskDTO);
        taskList.stream().forEach(e->{
            String userName = patrolTaskMapper.getUserName(e.getBackId());
            List<String> organizationName =patrolTaskMapper.getOrganizationName(patrolTaskDTO.getOrganizationId(),e.getPlanCode());
            List<String> stationName =patrolTaskMapper.getStationName(e.getPlanCode());
            List<String> patrolUserName =patrolTaskMapper.getPatrolUserName(e.getCode());
            String orgName =organizationName.stream().collect(Collectors.joining("、"));
            String stName =stationName.stream().collect(Collectors.joining("、"));
            String ptuName =patrolUserName.stream().collect(Collectors.joining("、"));
            e.setOrganizationName(orgName);
            e.setStationName(stName);
            e.setPatrolUserName(ptuName);
            e.setPatrolReturnUserName(userName);
        });
        return pageList.setRecords(taskList) ;
    }

    @Override
    public void getPatrolTaskReceive(PatrolTaskDTO patrolTaskDTO) {
        LambdaUpdateWrapper <PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        //领取：将待指派改为待执行
        if(patrolTaskDTO.getStatus()==0)
        {//更新巡检状态
            updateWrapper.set(PatrolTask::getStatus,2).eq(PatrolTask::getId,patrolTaskDTO.getId());
            update(updateWrapper);
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            updateWrapper.set(PatrolTask::getStatus,2).eq(PatrolTask::getId,patrolTaskDTO.getId());
            update(updateWrapper);
            //添加巡检人
            PatrolTask patrolTask = patrolTaskMapper.selectById(patrolTaskDTO.getId());
            PatrolTaskUser patrolTaskUser = new PatrolTaskUser();
            patrolTaskUser.setTaskCode(patrolTask.getCode());
            patrolTaskUser.setUserId(sysUser.getId());
            patrolTaskUser.setUserName(sysUser.getRealname());
            patrolTaskUser.setDelFlag(0);
            patrolTaskUserMapper.insert(patrolTaskUser);
        }
        //确认：将待确认改为待执行
        if(patrolTaskDTO.getStatus()==1)
        {
            updateWrapper.set(PatrolTask::getStatus,2).eq(PatrolTask::getId,patrolTaskDTO.getId());
            update(updateWrapper);
        }
        //执行：将待确认改为执行中
        if(patrolTaskDTO.getStatus()==2)
        {
            updateWrapper.set(PatrolTask::getStatus,4).eq(PatrolTask::getId,patrolTaskDTO.getId());
            update(updateWrapper);
        }
    }

    @Override
    public void getPatrolTaskReturn(PatrolTaskDTO patrolTaskDTO) {
        //更新巡检状态及添加退回人Id
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaUpdateWrapper <PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolTask::getStatus,3)
                     .set(PatrolTask::getBackId,sysUser.getId())
                     .eq(PatrolTask::getId,patrolTaskDTO.getId());
        update(updateWrapper);
        PatrolTask patrolTask = patrolTaskMapper.selectById(patrolTaskDTO.getId());


    }

}
