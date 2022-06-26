package com.aiurt.boot.task.service.impl;


import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.plan.entity.PatrolPlan;
import com.aiurt.boot.plan.mapper.PatrolPlanMapper;
import com.aiurt.boot.plan.mapper.PatrolPlanOrganizationMapper;
import com.aiurt.boot.plan.mapper.PatrolPlanStationMapper;
import com.aiurt.boot.plan.param.PatrolPlanOrganizationParam;
import com.aiurt.boot.plan.param.PatrolPlanStationParam;
import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.dto.PatrolTaskUserContentDTO;
import com.aiurt.boot.task.dto.PatrolTaskUserDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.mapper.PatrolTaskStandardMapper;
import com.aiurt.boot.task.mapper.PatrolTaskUserMapper;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private PatrolTaskStandardMapper patrolTaskStandardMapper;

    @Autowired
    private PatrolPlanMapper patrolPlanMapper;

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
    public PatrolTaskParam selectBasicInfo(PatrolTaskParam patrolTaskParam) {
        if (StrUtil.isEmpty(patrolTaskParam.getId())) {
            throw new AiurtBootException("记录的ID不能为空！");
        }
        QueryWrapper<PatrolTaskParam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PatrolTaskParam::getId, patrolTaskParam.getId());
        PatrolTaskParam taskParam = Optional.ofNullable(patrolTaskMapper.selectBasicInfo(patrolTaskParam)).orElseGet(PatrolTaskParam::new);
        // 组织机构信息
        List<PatrolPlanOrganizationParam> organizationInfo = Optional.ofNullable(patrolPlanOrganizationMapper.selectOrgByPlanCode(taskParam.getPlanCode()))
                .orElseGet(Collections::emptyList)
                .stream().collect(Collectors.toList());
        // 站点信息
        List<PatrolPlanStationParam> stationInfo = Optional.ofNullable(patrolPlanStationMapper.selectStationByPlanCode(taskParam.getPlanCode()))
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
    public Page<PatrolTaskDTO> getPatrolTaskList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO) {
        List<PatrolTaskDTO> taskList = patrolTaskMapper.getPatrolTaskList(pageList, patrolTaskDTO);
        taskList.stream().forEach(e -> {
            String userName = patrolTaskMapper.getUserName(e.getBackId());
            List<String> organizationName = patrolTaskMapper.getOrganizationName(e.getPlanCode());
            LambdaQueryWrapper<PatrolTaskStandard> queryWrapper = new LambdaQueryWrapper<>();
            PatrolTaskStandard patrolTaskStandards = patrolTaskStandardMapper.selectOne(queryWrapper.eq(PatrolTaskStandard::getTaskId, e.getId()));
            String majorName=patrolTaskStandardMapper.getMajorName(patrolTaskStandards.getProfessionCode());
            String sysName=patrolTaskStandardMapper.getSysName(patrolTaskStandards.getProfessionCode());
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getPlanCode());
            List<String> stationName = patrolTaskMapper.getStationName(e.getPlanCode());
            List<String> patrolUserName = patrolTaskMapper.getPatrolUserName(e.getCode());
            String orgName = organizationName.stream().collect(Collectors.joining("、"));
            String stName = stationName.stream().collect(Collectors.joining("、"));
            String ptuName = patrolUserName.stream().collect(Collectors.joining("、"));
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
        //领取：将待指派改为待执行，变为个人领取（传任务主键id,状态）
        if (patrolTaskDTO.getStatus() == 0) {//更新巡检状态
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
        if (patrolTaskDTO.getStatus() == 2) {
            updateWrapper.set(PatrolTask::getStatus, 4).eq(PatrolTask::getId, patrolTaskDTO.getId());
            update(updateWrapper);
        }
        //提交任务：将执行中，变为待审核、添加任务结束人id
        if (patrolTaskDTO.getStatus() == 4) {
            updateWrapper.set(PatrolTask::getStatus, 4)
                    .set(PatrolTask::getEndUserId, sysUser.getId())
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
    }

    @Override
    public void getPatrolTaskCheck(PatrolTaskDTO patrolTaskDTO) {
        //更新任务状态、添加检查人id，传任务主键id
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        PatrolTaskDevice patrolPDevice = new PatrolTaskDevice();
        LambdaUpdateWrapper<PatrolTaskDevice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolTaskDevice::getStatus, 1)
                .set(PatrolTaskDevice::getUserId, sysUser.getId())
                .eq(PatrolTaskDevice::getTaskId, patrolTaskDTO.getId());
        patrolTaskDeviceMapper.update(patrolPDevice, updateWrapper);
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
        //将任务来源改为常规指派
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(PatrolTask::getSource, 2).eq(PatrolTask::getCode, patrolTaskUserDTO.get(0).getTaskCode());
            update(updateWrapper);
    }
}
