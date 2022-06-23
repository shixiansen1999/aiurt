package com.aiurt.boot.task.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.plan.mapper.PatrolPlanOrganizationMapper;
import com.aiurt.boot.plan.mapper.PatrolPlanStationMapper;
import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.dto.PatrolTaskUserContentDTO;
import com.aiurt.boot.task.dto.PatrolTaskUserDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.mapper.PatrolTaskUserMapper;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;

    @Autowired
    private PatrolPlanOrganizationMapper patrolPlanOrganizationMapper;

    @Autowired
    private PatrolPlanStationMapper patrolPlanStationMapper;

    @Override
    public IPage<PatrolTaskParam> getTaskList(Page<PatrolTaskParam> page, PatrolTaskParam patrolTaskParam) {
        IPage<PatrolTaskParam> taskIPage = patrolTaskMapper.getTaskList(page, patrolTaskParam);
        taskIPage.getRecords().stream().forEach(l -> {
            // 组织机构信息
            l.setDepartInfo(patrolPlanOrganizationMapper.selectOrgByPlanCode(l.getPlanCode()));
            // 站点信息
            l.setStationInfo(patrolPlanStationMapper.selectStationByPlanCode(l.getPlanCode()));
        });
        return taskIPage;
    }
    @Override
    public Page<PatrolTaskDTO> getPatrolTaskList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO) {
        List<PatrolTaskDTO> taskList = patrolTaskMapper.getPatrolTaskList(pageList,patrolTaskDTO);
        if(ObjectUtil.isNotEmpty(patrolTaskDTO))
        {

        }
        taskList.stream().forEach(e -> {
            String userName = patrolTaskMapper.getUserName(e.getBackId());
            List<String> organizationName = patrolTaskMapper.getOrganizationName(e.getPlanCode());
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getPlanCode());
            List<String> stationName = patrolTaskMapper.getStationName(e.getPlanCode());
            List<String> patrolUserName = patrolTaskMapper.getPatrolUserName(e.getCode());
            String orgName = organizationName.stream().collect(Collectors.joining("、"));
            String stName = stationName.stream().collect(Collectors.joining("、"));
            String ptuName = patrolUserName.stream().collect(Collectors.joining("、"));
            e.setOrgCode(orgCodes);
            e.setOrganizationName(orgName);
            e.setStationName(stName);
            e.setPatrolUserName(ptuName);
            e.setPatrolReturnUserName(userName);
        });
        return pageList.setRecords(taskList);
    }
    @Override
    public void getPatrolTaskReceive(PatrolTaskDTO patrolTaskDTO){
            LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
            //领取：将待指派改为待执行（传任务id,状态）
            if (patrolTaskDTO.getStatus() == 0) {//更新巡检状态
                updateWrapper.set(PatrolTask::getStatus, 2).eq(PatrolTask::getId, patrolTaskDTO.getId());
                update(updateWrapper);
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                updateWrapper.set(PatrolTask::getStatus, 2).eq(PatrolTask::getId, patrolTaskDTO.getId());
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
            if (patrolTaskDTO.getStatus() == 1) {
                updateWrapper.set(PatrolTask::getStatus, 2).eq(PatrolTask::getId, patrolTaskDTO.getId());
                update(updateWrapper);
            }
            //执行：将待确认改为执行中
            if (patrolTaskDTO.getStatus() == 2) {
                updateWrapper.set(PatrolTask::getStatus, 4).eq(PatrolTask::getId, patrolTaskDTO.getId());
                if (patrolTaskDTO.getStatus() == 2) {
                    updateWrapper.set(PatrolTask::getStatus, 4).eq(PatrolTask::getId, patrolTaskDTO.getId());
                    update(updateWrapper);
                }
                //提交任务：将执行中，变为待审核
                if (patrolTaskDTO.getStatus() == 4) {
                    updateWrapper.set(PatrolTask::getStatus, 4).eq(PatrolTask::getId, patrolTaskDTO.getId());
                    update(updateWrapper);
                }
            }
        }

    @Override
    public void getPatrolTaskReturn(PatrolTaskDTO patrolTaskDTO) {
        //更新巡检状态及添加退回理由、退回人Id（传任务id、退回理由）
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolTask::getStatus, 3)
                .set(PatrolTask::getBackId, sysUser.getId())
                .set(PatrolTask::getRemark, patrolTaskDTO.getRemark())
                .eq(PatrolTask::getId, patrolTaskDTO.getId());
        update(updateWrapper);
    }

    @Override
    public void getPatrolTaskCheck(PatrolTaskDTO patrolTaskDTO) {
        //更新任务状态、添加检查人id
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        PatrolTaskDevice patrolPDevice = new PatrolTaskDevice();
        LambdaUpdateWrapper <PatrolTaskDevice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolTaskDevice::getStatus,1)
                     .set(PatrolTaskDevice::getUserId,sysUser.getId())
                     .eq(PatrolTaskDevice::getTaskId,patrolTaskDTO.getId());
        patrolTaskDeviceMapper.update(patrolPDevice,updateWrapper);
    }

    @Override
    public  List<PatrolTaskUserDTO> getPatrolTaskAppointSelect(PatrolTaskDTO patrolTaskDTO) {
        //查询这个部门的信息人员
        List<String> codes = patrolTaskDTO.getOrgCode();
        List<PatrolTaskUserDTO> arrayList= new ArrayList<>();
        List<String> arrayLists= new ArrayList<>();
        PatrolTaskUserDTO userDTO =new PatrolTaskUserDTO();
        for (String code :codes)
        {
             String organizationName = patrolTaskMapper.getOrgName(code);
             List<PatrolTaskUserContentDTO> user = patrolTaskMapper.getUser(code);
             userDTO.setOrganizationName(organizationName);
             userDTO.setUserList(user);
            System.out.println(userDTO);
        }

        System.out.println(arrayLists);
       return arrayList;
    }
}