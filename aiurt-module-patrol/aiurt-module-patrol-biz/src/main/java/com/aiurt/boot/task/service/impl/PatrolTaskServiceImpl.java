package com.aiurt.boot.task.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.plan.entity.PatrolPlan;
import com.aiurt.boot.plan.mapper.PatrolPlanMapper;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private PatrolTaskOrganizationMapper patrolTaskOrganizationMapper;

    @Autowired
    private PatrolTaskStationMapper patrolTaskStationMapper;
    @Autowired
    private PatrolTaskStandardMapper patrolTaskStandardMapper;

    @Autowired
    private PatrolPlanMapper patrolPlanMapper;

    @Override
    public IPage<PatrolTaskParam> getTaskList(Page<PatrolTaskParam> page, PatrolTaskParam patrolTaskParam) {
        IPage<PatrolTaskParam> taskIPage = patrolTaskMapper.getTaskList(page, patrolTaskParam);
        taskIPage.getRecords().stream().forEach(l -> {
            // 组织机构信息
            l.setDepartInfo(patrolTaskOrganizationMapper.selectOrgByTaskCode(l.getCode()));
            // 站点信息
            l.setStationInfo(patrolTaskStationMapper.selectStationByTaskCode(l.getCode()));
        });
        return taskIPage;
    }

    @Override
    public PatrolTaskParam selectBasicInfo(PatrolTaskParam patrolTaskParam) {
        if (StrUtil.isEmpty(patrolTaskParam.getId())) {
            throw new AiurtBootException("记录的ID不能为空！");
        }
        QueryWrapper<PatrolTaskParam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PatrolTaskParam::getId, patrolTaskParam.getId());
        PatrolTaskParam taskParam = Optional.ofNullable(patrolTaskMapper.selectBasicInfo(patrolTaskParam)).orElseGet(PatrolTaskParam::new);
        // 组织机构信息
        List<PatrolTaskOrganizationDTO> organizationInfo = Optional.ofNullable(patrolTaskOrganizationMapper.selectOrgByTaskCode(taskParam.getCode()))
                .orElseGet(Collections::emptyList)
                .stream().collect(Collectors.toList());
        // 站点信息
        List<PatrolTaskStationDTO> stationInfo = Optional.ofNullable(patrolTaskStationMapper.selectStationByTaskCode(taskParam.getCode()))
                .orElseGet(Collections::emptyList)
                .stream().collect(Collectors.toList());
        // 巡检用户
        QueryWrapper<PatrolTaskUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(PatrolTaskUser::getTaskCode, taskParam.getCode());
        List<PatrolTaskUser> userList = Optional.ofNullable(patrolTaskUserMapper.selectList(userQueryWrapper))
                .orElseGet(Collections::emptyList)
                .stream().collect(Collectors.toList());

        PatrolPlan patrolPlan = Optional.ofNullable(patrolPlanMapper.selectOne(new QueryWrapper<PatrolPlan>().lambda().eq(PatrolPlan::getCode, taskParam.getPlanCode())))
                .orElseGet(PatrolPlan::new);
        // 获取任务的专业信息
        List<String> majorInfo = patrolPlanMapper.getMajorInfoByPlanId(patrolPlan.getId());
        // 获取任务的子系统信息
        List<String> subsystemInfo = patrolPlanMapper.getSubsystemInfoByPlanId(patrolPlan.getId());
        taskParam.setDepartInfo(organizationInfo);
        taskParam.setStationInfo(stationInfo);
        taskParam.setUserInfo(userList);
        taskParam.setMajorInfo(majorInfo);
        taskParam.setSubsystemInfo(subsystemInfo);
        return taskParam;
    }

    @Override
    public int taskAppoint(Map<String, List<PatrolAppointUserDTO>> map) {
        AtomicInteger count = new AtomicInteger();
        for (Map.Entry<String, List<PatrolAppointUserDTO>> listEntry : map.entrySet()) {
            List<PatrolAppointUserDTO> list = listEntry.getValue();

            // 根据任务code查找未指派的任务
            QueryWrapper<PatrolTask> taskWrapper = new QueryWrapper<>();
            taskWrapper.lambda()
                    .eq(PatrolTask::getCode, listEntry.getKey())
                    .eq(PatrolTask::getStatus, PatrolConstant.TASK_INIT)
                    .eq(PatrolTask::getDiscardStatus, PatrolConstant.TASK_UNDISCARD);
            PatrolTask patrolTask = patrolTaskMapper.selectOne(taskWrapper);

            if (ObjectUtil.isNotEmpty(patrolTask)) {
                // 标记是否插入指派的用户信息
                AtomicInteger insert = new AtomicInteger();
                Optional.ofNullable(list).orElseGet(Collections::emptyList).stream().forEach(l -> {
                    if (ObjectUtil.isEmpty(l) || ObjectUtil.isEmpty(l.getUserId())) {
                        return;
                    }
                    if (ObjectUtil.isEmpty(l.getUserName())) {
                        l.setUserName(patrolTaskUserMapper.getUsername(l.getUserId()));
                    }
                    // 指派用户信息
                    PatrolTaskUser taskUser = new PatrolTaskUser();
                    taskUser.setTaskCode(listEntry.getKey());
                    taskUser.setUserId(l.getUserId());
                    taskUser.setUserName(l.getUserName());

                    // 添加指派用户
                    insert.addAndGet(patrolTaskUserMapper.insert(taskUser));
                });
                // 若插入指派的人员后则更新任务状态
                if (insert.get() > 0) {
                    // 任务状态
                    PatrolTask task = new PatrolTask();
                    task.setStatus(PatrolConstant.TASK_APPOINT);
                    // 更改任务状态为已指派
                    patrolTaskMapper.update(task, taskWrapper);
                    count.getAndIncrement();
                }
            }
        }
        return count.get();
    }

    @Override
    public int taskDiscard(List<PatrolTask> list) {
        AtomicInteger count = new AtomicInteger();
        Optional.ofNullable(list).orElseGet(Collections::emptyList).stream().forEach(l -> {
            if (ObjectUtil.isEmpty(l) || ObjectUtil.isEmpty(l.getId())) {
                return;
            }
            l.setDiscardStatus(PatrolConstant.TASK_DISCARD);
            patrolTaskMapper.updateById(l);
            count.getAndIncrement();
        });
        return count.get();
    }
    @Override
    public Page<PatrolTaskDTO> getPatrolTaskPoolList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO) {
        List<PatrolTaskDTO> taskList = patrolTaskMapper.getPatrolTaskPoolList(pageList, patrolTaskDTO);
        taskList.stream().forEach(e -> {
            String userName = patrolTaskMapper.getUserName(e.getBackId());
            List<String> organizationName = patrolTaskMapper.getOrganizationName(e.getPlanCode());
            List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(e.getId());
            String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).collect(Collectors.joining(","));
            String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).collect(Collectors.joining(","));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getPlanCode());
            List<String> stationName = patrolTaskMapper.getStationName(e.getPlanCode());
            List<String> patrolUserName = patrolTaskMapper.getPatrolUserName(e.getCode());
            String orgName = organizationName.stream().collect(Collectors.joining(","));
            String stName = stationName.stream().collect(Collectors.joining(","));
            String ptuName = patrolUserName.stream().collect(Collectors.joining(","));
            e.setSysName(sysName);
            e.setMajorName(majorName);
            e.setOrgCode(orgCodes);
            e.setOrganizationName(orgName);
            e.setStationName(stName);
            e.setPatrolUserName(ptuName);
            e.setPatrolReturnUserName(userName);
        });
        return pageList.setRecords(taskList);
    }

    @Override
    public Page<PatrolTaskDTO> getPatrolTaskList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO) {
        List<PatrolTaskDTO> taskList = patrolTaskMapper.getPatrolTaskList(pageList, patrolTaskDTO);
        taskList.stream().forEach(e -> {
            String userName = patrolTaskMapper.getUserName(e.getBackId());
            List<String> organizationName = patrolTaskMapper.getOrganizationName(e.getPlanCode());
            List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(e.getId());
            String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).collect(Collectors.joining(","));
            String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).collect(Collectors.joining(","));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getPlanCode());
            List<String> stationName = patrolTaskMapper.getStationName(e.getPlanCode());
            List<String> patrolUserName = patrolTaskMapper.getPatrolUserName(e.getCode());
            String orgName = organizationName.stream().collect(Collectors.joining(","));
            String stName = stationName.stream().collect(Collectors.joining(","));
            String ptuName = patrolUserName.stream().collect(Collectors.joining(","));
            e.setSysName(sysName);
            e.setMajorName(majorName);
            e.setOrgCode(orgCodes);
            e.setOrganizationName(orgName);
            e.setStationName(stName);
            e.setPatrolUserName(ptuName);
            e.setPatrolReturnUserName(userName);
        });
        return pageList.setRecords(taskList);
    }

    @Override
    public void getPatrolTaskReceive(PatrolTaskDTO patrolTaskDTO) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        //个人领取：将待指派或退回之后重新领取改为待执行，变为个人领取（传任务主键id,状态）
        if (patrolTaskDTO.getStatus() == 0 || patrolTaskDTO.getStatus() == 3) {
            updateWrapper.set(PatrolTask::getStatus, 2)
                    .set(PatrolTask::getSource, 1)
                    .eq(PatrolTask::getId, patrolTaskDTO.getId());
            update(updateWrapper);
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
        }
        //提交任务：将执行中，变为待审核、添加任务结束人id,传签名地址
        if (patrolTaskDTO.getStatus() == 4) {
            updateWrapper.set(PatrolTask::getStatus, 6)
                    .set(PatrolTask::getEndUserId, sysUser.getId())
                    .set(PatrolTask::getSignUrl,patrolTaskDTO.getSignUrl())
                    .eq(PatrolTask::getId, patrolTaskDTO.getId());
            update(updateWrapper);
        }
    }

    @Override
    public void getPatrolTaskReturn(PatrolTaskDTO patrolTaskDTO) {
        //更新巡检状态及添加退回理由、退回人Id（传任务主键id、退回理由）
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolTask::getStatus, 3)
                .set(PatrolTask::getBackId, sysUser.getId())
                .set(PatrolTask::getBackReason, patrolTaskDTO.getBackReason())
                .eq(PatrolTask::getId, patrolTaskDTO.getId());
        update(updateWrapper);
        //删除这个任务的巡检人
        LambdaQueryWrapper<PatrolTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PatrolTask::getId,patrolTaskDTO.getId());
        PatrolTask patrolTask = patrolTaskMapper.selectOne(queryWrapper);
        LambdaQueryWrapper<PatrolTaskUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatrolTaskUser::getTaskCode,patrolTask.getCode());
        List<PatrolTaskUser> patrolTaskUsers = patrolTaskUserMapper.selectList(wrapper);
        patrolTaskUserMapper.deleteBatchIds(patrolTaskUsers);
    }
    @Override
    public List<PatrolTaskUserDTO> getPatrolTaskAppointSelect(PatrolTaskDTO patrolTaskDTO) {
        //查询这个部门的信息人员,传组织机构ids
        List<String> codes = patrolTaskDTO.getOrgCode();
        List<PatrolTaskUserDTO> arrayList = new ArrayList<>();
        for (String code : codes) {
            PatrolTaskUserDTO userDTO = new PatrolTaskUserDTO();
            String organizationName = patrolTaskMapper.getOrgName(code);
            List<PatrolTaskUserContentDTO> user = patrolTaskMapper.getUser(code);
            userDTO.setOrganizationName(organizationName);
            userDTO.setUserList(user);
            arrayList.add(userDTO);
        }
        return arrayList;
    }

    @Override
    public void getPatrolTaskAppoint(List<PatrolTaskUserDTO> patrolTaskUserDTO) {
        //传整个实体。添加指派人员信息
        for (PatrolTaskUserDTO ptu : patrolTaskUserDTO) {
            PatrolTaskUser patrolTaskUser = new PatrolTaskUser();
            List<PatrolTaskUserContentDTO> userList = ptu.getUserList();
            String userId = userList.stream().map(PatrolTaskUserContentDTO::getId).collect(Collectors.joining());
            String realName = userList.stream().map(PatrolTaskUserContentDTO::getRealname).collect(Collectors.joining());
            patrolTaskUser.setUserId(userId);
            patrolTaskUser.setUserName(realName);
            patrolTaskUser.setTaskCode(ptu.getTaskCode());
            patrolTaskUser.setDelFlag(0);
            patrolTaskUserMapper.insert(patrolTaskUser);
        }
        //将任务来源改为常规指派,将任务状态改为待确认
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolTask::getSource, 2).set(PatrolTask::getStatus, 1).eq(PatrolTask::getCode, patrolTaskUserDTO.get(0).getTaskCode());
        update(updateWrapper);
    }
    @Override
    public PatrolTaskSubmitDTO getSubmitTaskCount(PatrolTaskSubmitDTO patrolTaskSubmitDTO) {
        LambdaQueryWrapper<PatrolTaskDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PatrolTaskDevice::getTaskId,patrolTaskSubmitDTO.getTaskId());
        List<PatrolTaskDevice> patrolTaskDevices = patrolTaskDeviceMapper.selectList(queryWrapper);
        List<PatrolTaskDevice> collect = patrolTaskDevices.stream().filter(e -> e.getStatus() == 0).collect(Collectors.toList());
        List<PatrolTaskDevice> list = patrolTaskDevices.stream().filter(e -> e.getStatus() == 1).collect(Collectors.toList());
        List<PatrolTaskDevice> patrolTaskDeviceList = patrolTaskDevices.stream().filter(e -> e.getStatus() == 2).collect(Collectors.toList());
        PatrolTaskSubmitDTO submitDTO = new PatrolTaskSubmitDTO();
        if(CollUtil.isNotEmpty(collect))
        {
            submitDTO.setNotInspectedNumber(collect.size());
        }
        if(CollUtil.isEmpty(collect))
        {
            submitDTO.setNotInspectedNumber(0);
        }
        if(CollUtil.isNotEmpty(list))
        {
            submitDTO.setInspectedNumber(list.size());
        }
        if(CollUtil.isEmpty(list))
        {
            submitDTO.setInspectedNumber(0);
        }
        if(CollUtil.isNotEmpty(patrolTaskDeviceList))
        {
            submitDTO.setTotalNumber(patrolTaskDeviceList.size());
        }
        if(CollUtil.isEmpty(patrolTaskDeviceList))
        {
            submitDTO.setTotalNumber(0);
        }
        return submitDTO;
    }
}
