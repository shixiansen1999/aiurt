package com.aiurt.boot.task.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.manager.PatrolManager;
import com.aiurt.boot.plan.entity.PatrolPlan;
import com.aiurt.boot.plan.mapper.PatrolPlanMapper;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    @Autowired
    private PatrolStandardMapper patrolStandardMapper;
    @Autowired
    private PatrolManager manager;

    @Override
    public IPage<PatrolTaskParam> getTaskList(Page<PatrolTaskParam> page, PatrolTaskParam patrolTaskParam) {
        if (ObjectUtil.isNotEmpty(patrolTaskParam) && ObjectUtil.isNotEmpty(patrolTaskParam.getDateScope())) {
            String[] split = patrolTaskParam.getDateScope().split(",");
            Date dateHead = DateUtil.parse(split[0], "yyyy-MM-dd");
            Date dateEnd = DateUtil.parse(split[1], "yyyy-MM-dd");
            patrolTaskParam.setDateHead(dateHead);
            patrolTaskParam.setDateEnd(dateEnd);
        }
        IPage<PatrolTaskParam> taskPage = patrolTaskMapper.getTaskList(page, patrolTaskParam);
        taskPage.getRecords().stream().forEach(l -> {
            // 组织机构信息
            l.setDepartInfo(patrolTaskOrganizationMapper.selectOrgByTaskCode(l.getCode()));
            // 站点信息
            l.setStationInfo(patrolTaskStationMapper.selectStationByTaskCode(l.getCode()));
            if (ObjectUtil.isNotEmpty(l.getEndUserId())) {
                // 任务结束用户名称
                l.setEndUsername(patrolTaskMapper.getUsername(l.getEndUserId()));
            }
            if (ObjectUtil.isNotEmpty(l.getAuditorId())) {
                // 审核用户名称
                l.setAuditUsername(patrolTaskMapper.getUsername(l.getAuditorId()));
            }
            if (ObjectUtil.isNotEmpty(l.getBackId())) {
                // 退回用户名称
                l.setBackUsername(patrolTaskMapper.getUsername(l.getBackId()));
            }
            if (ObjectUtil.isNotEmpty(l.getDisposeId())) {
                // 处置用户名称
                l.setDisposeUserName(patrolTaskMapper.getUsername(l.getDisposeId()));
            }
        });
        return taskPage;
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
    public int taskAppoint(PatrolAppointInfoDTO patrolAppointInfoDTO) {
        // 用户信息数据
        Map<String, List<PatrolAppointUserDTO>> map = Optional.ofNullable(patrolAppointInfoDTO.getMap()).orElseGet(ConcurrentHashMap::new);
        AtomicInteger count = new AtomicInteger();
        for (Map.Entry<String, List<PatrolAppointUserDTO>> listEntry : map.entrySet()) {
            List<PatrolAppointUserDTO> list = listEntry.getValue();
            // 根据任务code查找未指派的任务
            QueryWrapper<PatrolTask> taskWrapper = new QueryWrapper<>();
            taskWrapper.lambda()
                    .eq(PatrolTask::getCode, listEntry.getKey())
                    .eq(PatrolTask::getStatus, PatrolConstant.TASK_INIT).or().eq(PatrolTask::getStatus, PatrolConstant.TASK_RETURNED)
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
                    PatrolTask task = new PatrolTask();
                    // 计划令编号和图片地址
                    task.setPlanOrderCode(patrolAppointInfoDTO.getPlanOrderCode());
                    task.setPlanOrderCodeUrl(patrolAppointInfoDTO.getPlanOrderCodeUrl());
                    task.setSource(2);
                    // 更新检查开始结束时间
                    task.setStartTime(patrolAppointInfoDTO.getStartTime());
                    task.setEndTime(patrolAppointInfoDTO.getEndTime());
                    // 任务状态
                    task.setStatus(PatrolConstant.TASK_CONFIRM);
                    // 更改任务状态为待确认
                    patrolTaskMapper.update(task, taskWrapper);
                    count.getAndIncrement();
                }
            }
        }
        return count.get();
    }

    @Override
    @Transactional(rollbackFor = AiurtBootException.class)
    public int taskDiscard(List<PatrolTask> list) {
        AtomicInteger count = new AtomicInteger();
        Optional.ofNullable(list).orElseGet(Collections::emptyList).stream().forEach(l -> {
            if (ObjectUtil.isEmpty(l)) {
                throw new AiurtBootException("集合存在空对象，请传输相关数据！");
            }
            if (ObjectUtil.isEmpty(l.getId())) {
                throw new AiurtBootException("任务记录主键ID为空！");
            }
            if (ObjectUtil.isEmpty(l.getDiscardReason())) {
                throw new AiurtBootException("任务ID为[" + l.getId() + "]作废理由为空！");
            }
            l.setDiscardStatus(PatrolConstant.TASK_DISCARD);
            l.setDiscardReason(l.getDiscardReason());
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
            List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(e.getId());
            String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).collect(Collectors.joining(","));
            String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).collect(Collectors.joining(","));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getCode());
            e.setOrganizationName(manager.translateOrg(orgCodes));
            List<StationDTO> stationName = patrolTaskMapper.getStationName(e.getCode());
            e.setStationName( manager.translateStation(stationName));
            List<String> patrolUserName = patrolTaskMapper.getPatrolUserName(e.getCode());
            String ptuName = patrolUserName.stream().collect(Collectors.joining(","));
            e.setSysName(sysName);
            e.setMajorName(majorName);
            e.setOrgCodeList(orgCodes);
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
            List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(e.getId());
            String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).collect(Collectors.joining(","));
            String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).collect(Collectors.joining(","));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getCode());
            e.setOrganizationName(manager.translateOrg(orgCodes));
            List<StationDTO> stationName = patrolTaskMapper.getStationName(e.getCode());
            e.setStationName( manager.translateStation(stationName));
            List<String> patrolUserName = patrolTaskMapper.getPatrolUserName(e.getCode());
            String ptuName = patrolUserName.stream().collect(Collectors.joining(","));
            e.setSysName(sysName);
            e.setMajorName(majorName);
            e.setOrgCodeList(orgCodes);
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
        if (PatrolConstant.TASK_INIT.equals(patrolTaskDTO.getStatus())  || PatrolConstant.TASK_RETURNED.equals(patrolTaskDTO.getStatus())) {
            updateWrapper.set(PatrolTask::getStatus, 2)
                    .set(PatrolTask::getSource, 1)
                    .eq(PatrolTask::getId, patrolTaskDTO.getId());
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
        if (PatrolConstant.TASK_CONFIRM.equals(patrolTaskDTO.getStatus())) {
            updateWrapper.set(PatrolTask::getStatus, 2).eq(PatrolTask::getId, patrolTaskDTO.getId());
            update(updateWrapper);
        }
        //执行：将待执行改为执行中
        if (PatrolConstant.TASK_EXECUTE.equals(patrolTaskDTO.getStatus())) {
            updateWrapper.set(PatrolTask::getStatus, 4).eq(PatrolTask::getId, patrolTaskDTO.getId());
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
        queryWrapper.eq(PatrolTask::getId, patrolTaskDTO.getId());
        PatrolTask patrolTask = patrolTaskMapper.selectOne(queryWrapper);
        LambdaQueryWrapper<PatrolTaskUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatrolTaskUser::getTaskCode, patrolTask.getCode());
        List<PatrolTaskUser> patrolTaskUsers = patrolTaskUserMapper.selectList(wrapper);
        patrolTaskUserMapper.deleteBatchIds(patrolTaskUsers);
    }

    @Override
    public List<PatrolTaskUserDTO> getPatrolTaskAppointSelect(PatrolOrgDTO orgCoed) {
        //查询这个部门的信息人员,传组织机构ids
        List<PatrolTaskUserDTO> arrayList = new ArrayList<>();
        for (String code : orgCoed.getOrg()) {
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
        queryWrapper.eq(PatrolTaskDevice::getTaskId, patrolTaskSubmitDTO.getTaskId());
        List<PatrolTaskDevice> patrolTaskDevices = patrolTaskDeviceMapper.selectList(queryWrapper);
        List<PatrolTaskDevice> collect = patrolTaskDevices.stream().filter(e -> e.getStatus() == 0).collect(Collectors.toList());
        List<PatrolTaskDevice> list = patrolTaskDevices.stream().filter(e -> e.getStatus() == 1).collect(Collectors.toList());
        List<PatrolTaskDevice> patrolTaskDeviceList = patrolTaskDevices.stream().filter(e -> e.getStatus() == 2).collect(Collectors.toList());
        PatrolTaskSubmitDTO submitDTO = new PatrolTaskSubmitDTO();
        if (CollUtil.isNotEmpty(collect)) {
            submitDTO.setNotInspectedNumber(collect.size());
        }
        if (CollUtil.isEmpty(collect)) {
            submitDTO.setNotInspectedNumber(0);
        }
        if (CollUtil.isNotEmpty(list)) {
            submitDTO.setInspectedNumber(list.size());
        }
        if (CollUtil.isEmpty(list)) {
            submitDTO.setInspectedNumber(0);
        }
        if (CollUtil.isNotEmpty(patrolTaskDeviceList)) {
            submitDTO.setTotalNumber(patrolTaskDeviceList.size());
        }
        if (CollUtil.isEmpty(patrolTaskDeviceList)) {
            submitDTO.setTotalNumber(0);
        }
        return submitDTO;
    }

    @Override
    public Page<PatrolTaskDTO> getPatrolTaskManualList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO) {
        List<PatrolTaskDTO> taskDTOList = patrolTaskMapper.getPatrolTaskManualList(pageList, patrolTaskDTO);
        taskDTOList.stream().forEach(e -> {
            String userName = patrolTaskMapper.getUserName(e.getBackId());
            List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(e.getId());
            String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).collect(Collectors.joining(","));
            String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).collect(Collectors.joining(","));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getCode());
            e.setOrganizationName(manager.translateOrg(orgCodes));
            List<String> stationCodeList = patrolTaskMapper.getStationCode(e.getCode());
            List<StationDTO> stationName = patrolTaskMapper.getStationName(e.getCode());
            e.setStationName( manager.translateStation(stationName));
            List<String> patrolUserName = patrolTaskMapper.getPatrolUserName(e.getCode());
            String ptuName = patrolUserName.stream().collect(Collectors.joining(","));
            e.setSysName(sysName);
            e.setStationCodeList(stationCodeList);
            e.setMajorName(majorName);
            e.setOrgCodeList(orgCodes);
            e.setPatrolUserName(ptuName);
            e.setPatrolReturnUserName(userName);
        });
        return pageList.setRecords(taskDTOList);
    }

    @Override
    public List<MajorDTO> getMajorSubsystemGanged(String id) {
        QueryWrapper<PatrolTaskStandard> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PatrolTaskStandard::getTaskId, id);
        List<PatrolTaskStandard> list = Optional.ofNullable(patrolTaskStandardMapper.selectList(wrapper)).orElseGet(Collections::emptyList);
        // 专业编码
        List<String> majorInfo = list.stream().map(PatrolTaskStandard::getProfessionCode).distinct().collect(Collectors.toList());
        // 子系统编码
        List<String> subSystemInfo = list.stream().map(PatrolTaskStandard::getSubsystemCode).distinct().collect(Collectors.toList());

        List<MajorDTO> major = new ArrayList<>();
        List<SubsystemDTO> subsystem = new ArrayList<>();

        Optional.ofNullable(majorInfo).orElseGet(Collections::emptyList).stream().forEach(l -> {
            MajorDTO majorDTO = new MajorDTO();
            majorDTO.setMajorCode(l);
            // 专业名称
            String majorName = patrolTaskMapper.getMajorNameByMajorCode(l);
            majorDTO.setMajorName(majorName);
            major.add(majorDTO);
        });
        Optional.ofNullable(subSystemInfo).orElseGet(Collections::emptyList).stream().forEach(l -> {
            SubsystemDTO subsystemDTO = new SubsystemDTO();
            subsystemDTO.setSubsystemCode(l);
            // 子系统名称
            String majorName = patrolTaskMapper.getSubsystemNameBySystemCode(l);
            subsystemDTO.setSubsystemName(majorName);
            subsystem.add(subsystemDTO);
        });
        // 获取专业下的子系统信息
        Optional.ofNullable(major).orElseGet(Collections::emptyList).stream().forEach(l -> {
            if (ObjectUtil.isNotEmpty(l.getMajorCode()) && CollectionUtil.isNotEmpty(subsystem)) {
                List<SubsystemDTO> subsystemInfo = Optional.ofNullable(patrolTaskMapper.getMajorSubsystemGanged(l.getMajorCode(), subsystem))
                        .orElseGet(Collections::emptyList).stream().collect(Collectors.toList());
                l.setSubsystemInfo(subsystemInfo);
            }
        });
        return major;
    }

    @Override
    public int taskAudit(String code, Integer auditStatus, String auditReason, String remark) {
        QueryWrapper<PatrolTask> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PatrolTask::getCode, code);
        PatrolTask patrolTask = patrolTaskMapper.selectOne(wrapper);
        // 获取当前登录用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        patrolTask.setAuditorId(loginUser.getId());

        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("未发现登录用户，请登录系统后操作！");
        }
        if (PatrolConstant.AUDIT_NOPASS.equals(auditStatus)) {
            if (StrUtil.isEmpty(auditReason)) {
                throw new AiurtBootException("审核不通过原因不能为空！");
            }
            patrolTask.setRejectReason(auditReason);
            patrolTask.setStatus(PatrolConstant.TASK_BACK);
        } else {
            patrolTask.setStatus(PatrolConstant.TASK_COMPLETE);
            patrolTask.setAuditorRemark(remark);
        }
        int updateById = patrolTaskMapper.updateById(patrolTask);
        return updateById;
    }

    @Override
    public void getPatrolTaskSubmit(PatrolTaskDTO patrolTaskDTO) {
        //提交任务：将待执行、执行中，变为待审核、添加任务结束人id,传签名地址、任务主键id、审核状态
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        if (patrolTaskDTO.getAuditor() == 1) {
            updateWrapper.set(PatrolTask::getStatus, 6)
                    .set(PatrolTask::getEndUserId, sysUser.getId())
                    .set(PatrolTask::getSignUrl, patrolTaskDTO.getSignUrl())
                    .set(PatrolTask::getEndTime, LocalDateTime.now())
                    .eq(PatrolTask::getId, patrolTaskDTO.getId());
        } else {
            updateWrapper.set(PatrolTask::getStatus, 7)
                    .set(PatrolTask::getEndUserId, sysUser.getId())
                    .set(PatrolTask::getSignUrl, patrolTaskDTO.getSignUrl())
                    .set(PatrolTask::getEndTime, LocalDateTime.now())
                    .eq(PatrolTask::getId, patrolTaskDTO.getId());
        }
        update(updateWrapper);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void getPatrolTaskManualListAdd(PatrolTaskManualDTO patrolTaskManualDTO) {
        //保存任务信息
        PatrolTask patrolTask = new PatrolTask();
        patrolTask.setName(patrolTaskManualDTO.getName());
        String xjCode = "XR" + System.currentTimeMillis();
        patrolTask.setCode(xjCode);
        patrolTask.setPatrolDate(patrolTaskManualDTO.getPatrolDate());
        patrolTask.setStatus(0);
        patrolTask.setSource(3);
        patrolTask.setDelFlag(0);
        patrolTask.setDiscardStatus(0);
        patrolTask.setRebuild(0);
        patrolTask.setType(patrolTaskManualDTO.getType());
        patrolTask.setRemark(patrolTaskManualDTO.getRemark());
        patrolTask.setStartTime(patrolTaskManualDTO.getStartTime());
        patrolTask.setEndTime(patrolTaskManualDTO.getEndTime());
        patrolTask.setAuditor(patrolTaskManualDTO.getAuditor());
        patrolTaskMapper.insert(patrolTask);
        //保存组织信息
        String taskCode = patrolTask.getCode();
        List<String> orgCodeList = patrolTaskManualDTO.getOrgCodeList();
        orgCodeList.stream().forEach(o -> {
            PatrolTaskOrganization organization = new PatrolTaskOrganization();
            organization.setTaskCode(taskCode);
            organization.setDelFlag(0);
            organization.setOrgCode(o);
            patrolTaskOrganizationMapper.insert(organization);
        });
        //保存站点信息
        List<String> stationCodeList = patrolTaskManualDTO.getStationCodeList();
        stationCodeList.stream().forEach(s -> {
            PatrolTaskStation station = new PatrolTaskStation();
            station.setDelFlag(0);
            station.setStationCode(s);
            station.setTaskCode(taskCode);
            patrolTaskStationMapper.insert(station);
        });
        //保存巡检任务标准表的信息
        String taskId = patrolTask.getId();
        List<PatrolTaskStandardDTO> patrolStandardList = patrolTaskManualDTO.getPatrolStandardList();
        patrolStandardList.stream().forEach(ns -> {
            PatrolTaskStandard patrolTaskStandard = new PatrolTaskStandard();
            patrolTaskStandard.setTaskId(taskId);
            patrolTaskStandard.setStandardId(ns.getId());//继承了标准表，id即为标准表id
            patrolTaskStandard.setDelFlag(0);
            patrolTaskStandard.setDeviceTypeCode(ns.getDeviceTypeCode());
            patrolTaskStandard.setSubsystemCode(ns.getSubsystemCode());
            patrolTaskStandard.setProfessionCode(ns.getProfessionCode());
            patrolTaskStandard.setStandardCode(ns.getCode());
            patrolTaskStandardMapper.insert(patrolTaskStandard);
            String taskStandardId = patrolTaskStandard.getId();
            //生成单号
            //判断是否与设备相关
            PatrolStandard patrolStandard = patrolStandardMapper.selectById(ns.getId());
            if (ObjectUtil.isNotNull(patrolStandard) && 1==patrolStandard.getDeviceType()) {
                List<DeviceDTO> deviceList = ns.getDeviceList();
                //遍历设备单号
                deviceList.stream().forEach(dv -> {
                    PatrolTaskDevice patrolTaskDevice = new PatrolTaskDevice();
                    patrolTaskDevice.setTaskId(taskId);//巡检任务id
                    patrolTaskDevice.setDelFlag(0);
                    patrolTaskDevice.setStatus(0);//单号状态
                    String xdCode = "XD" + System.currentTimeMillis();
                    patrolTaskDevice.setPatrolNumber(xdCode);//巡检单号
                    patrolTaskDevice.setTaskStandardId(taskStandardId);//巡检任务标准关联表ID
                    patrolTaskDevice.setDeviceCode(dv.getCode());//设备code
                    Device device = patrolTaskDeviceMapper.getDevice(dv.getCode());
                    patrolTaskDevice.setLineCode(device.getLineCode());//线路code
                    patrolTaskDevice.setStationCode(device.getStationCode());//站点code
                    patrolTaskDevice.setPositionCode(device.getPositionCode());//位置code
                    patrolTaskDeviceMapper.insert(patrolTaskDevice);
                });
            } else {
                List<String> stationCodeList1 = patrolTaskManualDTO.getStationCodeList();
                stationCodeList1.stream().forEach(sc -> {
                    PatrolTaskDevice patrolTaskDevice = new PatrolTaskDevice();
                    patrolTaskDevice.setTaskId(taskId);
                    patrolTaskDevice.setDelFlag(0);
                    patrolTaskDevice.setStatus(0);//单号状态
                    String xdCode = "XD" + System.currentTimeMillis();
                    patrolTaskDevice.setPatrolNumber(xdCode);//巡检单号
                    patrolTaskDevice.setTaskStandardId(taskStandardId);//巡检任务标准关联表ID
                    String lineCode =patrolTaskStationMapper.getLineStaionCode(sc);
                    patrolTaskDevice.setLineCode(lineCode);//线路code
                    patrolTaskDevice.setStationCode(sc);//站点code
                    patrolTaskDeviceMapper.insert(patrolTaskDevice);
                });
            }
        });
    }

    @Override
    public int taskDispose(PatrolTask task, String omitExplain) {
        // 获取当前登录用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测为未登录状态，请登录系统后操作！");
        }
        task.setDisposeId(loginUser.getId());
        // 漏检说明
        task.setOmitExplain(omitExplain);
        task.setDisposeTime(new Date());
        // 更新为已处置状态
        task.setDisposeStatus(PatrolConstant.TASK_DISPOSE);
        return patrolTaskMapper.updateById(task);
    }

    @Override
    public Page<PatrolTaskStandardDTO> getPatrolTaskManualDetail(Page<PatrolTaskStandardDTO> pageList, String id) {
        List<PatrolTaskStandardDTO> standardList = patrolTaskStandardMapper.getStandard(id);
        standardList.stream().forEach(e -> {
            PatrolStandard patrolStandard = patrolStandardMapper.selectById(e.getStandardId());
            if(patrolStandard.getDeviceType()==1)
            { e.setSpecifyDevice(1); }
            else{ e.setSpecifyDevice(0); }
            LambdaQueryWrapper<PatrolTaskDevice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PatrolTaskDevice::getTaskId, e.getTaskId()).eq(PatrolTaskDevice::getTaskStandardId, e.getTaskStandardId());
            List<PatrolTaskDevice> taskDeviceList = patrolTaskDeviceMapper.selectList(queryWrapper);
            List<DeviceDTO> dtoList = new ArrayList<>();
            taskDeviceList.stream().forEach(td -> {
                if (ObjectUtil.isNotNull(td.getDeviceCode())) {
                    DeviceDTO deviceDTO = patrolTaskDeviceMapper.getTaskStandardDevice(td.getDeviceCode());
                    if (ObjectUtil.isNotNull(deviceDTO.getPositionCode())) {
                        String positionDevice = patrolTaskDeviceMapper.getDevicePosition(deviceDTO.getPositionCode());
                        String position = deviceDTO.getPositionCodeName() + "/" + positionDevice;
                        deviceDTO.setPositionCodeName(position);
                    }
                    dtoList.add(deviceDTO);
                }
                else { e.setDeviceList(new ArrayList<>()); }
            });
            e.setDeviceList(dtoList);
        });
        return pageList.setRecords(standardList);
    }

    @Override
    public List<PatrolUserInfoDTO> getAssignee(List<String> list) {
        if (CollectionUtil.isEmpty(list)) {
            throw new AiurtBootException("任务编号的集合对象为空！");
        }
        int size = list.size();
        List<PatrolUserInfoDTO> userInfo = Optional.ofNullable(patrolTaskOrganizationMapper.getUserListByTaskCode(list.get(0))).orElseGet(Collections::emptyList);

        // 获取批量指派时的用户 需要相同的组织机构
        if (size > 1) {
            List<String> orgCode = Optional.ofNullable(patrolTaskOrganizationMapper.getOrgCode(list.get(0))).orElseGet(Collections::emptyList);
            for (int i = 1; i < size; i++) {
                List<String> code = Optional.ofNullable(patrolTaskOrganizationMapper.getOrgCode(list.get(i))).orElseGet(Collections::emptyList);
                boolean contains1 = orgCode.containsAll(code);
                boolean contains2 = code.containsAll(orgCode);
                if (contains1 && contains2) {
                    continue;
                } else {
                    throw new AiurtBootException("请选择组织机构一致的任务进行批量指派！");
                }
            }

        }
        return userInfo;
    }

    @Override
    public String rebuildTask(PatrolRebuildDTO patrolRebuildDTO) {
        String taskId = patrolRebuildDTO.getTaskId();
        QueryWrapper<PatrolTask> wrapper = new QueryWrapper<>();
        // 获取未作废、未处置、已漏检、未重新生成的任务
        wrapper.lambda().eq(PatrolTask::getId, taskId)
                .eq(PatrolTask::getStatus, PatrolConstant.TASK_MISSED)
                .eq(PatrolTask::getDisposeStatus, PatrolConstant.TASK_UNDISPOSE)
                .eq(PatrolTask::getDiscardStatus, PatrolConstant.TASK_UNDISCARD)
                .eq(PatrolTask::getRebuild, PatrolConstant.TASK_UNREBUILD);
        PatrolTask patrolTask = patrolTaskMapper.selectOne(wrapper);
        if (ObjectUtil.isEmpty(patrolTask)) {
            throw new AiurtBootException("未找到满足重新生成条件的任务！");
        }
        PatrolTask task = new PatrolTask();
        // 任务编号
        String taskCode = "XR" + System.currentTimeMillis();
        task.setCode(taskCode);
        // 任务名称
        task.setName(patrolTask.getName());
        // 计划编号
        task.setPlanCode(patrolTask.getPlanCode());
        // 作业类型
        task.setType(patrolTask.getType());
        // 是否委外
        task.setOutsource(patrolTask.getOutsource());
        // 巡检周期
        task.setPeriod(patrolTask.getPeriod());
        // 任务方式-手工下发
        task.setSource(PatrolConstant.TASK_MANUAL);
        // 任务状态-待指派
        task.setStatus(PatrolConstant.TASK_INIT);
        // 任务是否需要审核
        task.setAuditor(patrolTask.getAuditor());
        // 处置状态-未处置
        task.setDisposeStatus(PatrolConstant.TASK_UNDISPOSE);
        // 作废状态-未作废
        task.setDiscardStatus(PatrolConstant.TASK_UNDISCARD);
        // 重新生成状态-未重新生成状态
        task.setRebuild(PatrolConstant.TASK_UNREBUILD);
        patrolTaskMapper.insert(task);
        String newTaskId = task.getId();

        // 将原漏检任务更新为已重新生成状态
        patrolTask.setRebuild(PatrolConstant.TASK_REBUILD);
        patrolTaskMapper.updateById(patrolTask);

        // 组织机构信息
        if (ObjectUtil.isEmpty(patrolRebuildDTO.getDeptCode())) {
            List<PatrolTaskOrganizationDTO> patrolTaskOrgList = Optional.ofNullable(patrolTaskOrganizationMapper.selectOrgByTaskCode(patrolTask.getCode()))
                    .orElseGet(Collections::emptyList);
            patrolTaskOrgList.stream().forEach(l -> {
                PatrolTaskOrganization organization = new PatrolTaskOrganization();
                organization.setTaskCode(taskCode);
                organization.setOrgCode(l.getOrgCode());
                patrolTaskOrganizationMapper.insert(organization);
            });
        }
        // 站所信息
        if (ObjectUtil.isEmpty(patrolRebuildDTO.getStationCode())) {
            List<PatrolTaskStationDTO> taskStationList = Optional.ofNullable(patrolTaskStationMapper.selectStationByTaskCode(patrolTask.getCode()))
                    .orElseGet(Collections::emptyList);
            taskStationList.stream().forEach(l -> {
                PatrolTaskStation station = new PatrolTaskStation();
                station.setTaskCode(taskCode);
                station.setStationCode(l.getStationCode());
                patrolTaskStationMapper.insert(station);
            });
        }

        // 根据原任务ID获取巡检任务标准关联表信息
        QueryWrapper<PatrolTaskStandard> taskStandardWrapper = new QueryWrapper<>();
        taskStandardWrapper.lambda().eq(PatrolTaskStandard::getTaskId, taskId);
        List<PatrolTaskStandard> taskStandardList = Optional.ofNullable(patrolTaskStandardMapper.selectList(taskStandardWrapper))
                .orElseGet(Collections::emptyList).stream().collect(Collectors.toList());
        taskStandardList.stream().forEach(l -> {
            PatrolTaskStandard taskStandard = new PatrolTaskStandard();
            taskStandard.setTaskId(newTaskId);
            taskStandard.setStandardId(l.getStandardId());
            taskStandard.setStandardCode(l.getStandardCode());
            taskStandard.setProfessionCode(l.getProfessionCode());
            taskStandard.setSubsystemCode(l.getSubsystemCode());
            taskStandard.setDeviceTypeCode(l.getDeviceTypeCode());
            patrolTaskStandardMapper.insert(taskStandard);

            // 根据原任务ID和原任务标准关联表ID 获取原巡检任务设备关联表信息
            QueryWrapper<PatrolTaskDevice> taskDeviceWrapper = new QueryWrapper<>();
            taskDeviceWrapper.lambda().eq(PatrolTaskDevice::getTaskId, taskId)
                    .eq(PatrolTaskDevice::getTaskStandardId, l.getStandardId());
            List<PatrolTaskDevice> taskDeviceList = Optional.ofNullable(patrolTaskDeviceMapper.selectList(taskDeviceWrapper))
                    .orElseGet(Collections::emptyList).stream().collect(Collectors.toList());

            // 新增对应的设备工单信息
            taskDeviceList.stream().forEach(
                    // d:PatrolTaskDevice对象
                    d -> {
                        PatrolTaskDevice taskDevice = new PatrolTaskDevice();
                        // 新任务表ID
                        taskDevice.setTaskId(newTaskId);
                        // 新任务标准ID
                        taskDevice.setTaskStandardId(taskStandard.getId());
                        // 巡检单号
                        String billCode = "XD" + System.currentTimeMillis();
                        taskDevice.setDeviceCode(billCode);
                        // 设备编号
                        taskDevice.setDeviceCode(d.getDeviceCode());
                        // 线路编号
                        taskDevice.setLineCode(d.getLineCode());
                        // 站所编号
                        taskDevice.setStationCode(d.getStationCode());
                        // 位置编号
                        taskDevice.setPositionCode(d.getPositionCode());
                        // 检查状态-初始为未开始
                        taskDevice.setStatus(PatrolConstant.BILL_INIT);
                        patrolTaskDeviceMapper.insert(taskDevice);
                    }
            );
        });
        return taskCode;
    }

    @Override
    public void getPatrolTaskManualEdit(PatrolTaskManualDTO patrolTaskManualDTO) {
        //更新任务信息
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolTask::getRemark, patrolTaskManualDTO.getRemark()).set(PatrolTask::getAuditor, patrolTaskManualDTO.getAuditor()).
                set(PatrolTask::getStartTime, patrolTaskManualDTO.getStartTime()).set(PatrolTask::getEndTime, patrolTaskManualDTO.getEndTime())
                .set(PatrolTask::getName, patrolTaskManualDTO.getName()).set(PatrolTask::getPatrolDate, patrolTaskManualDTO.getPatrolDate()).eq(PatrolTask::getId, patrolTaskManualDTO.getId());
        patrolTaskMapper.update(new PatrolTask(), updateWrapper);
        //先删除
        List<PatrolTaskOrganization> list = patrolTaskOrganizationMapper.selectList(new LambdaQueryWrapper<PatrolTaskOrganization>().eq(PatrolTaskOrganization::getTaskCode, patrolTaskManualDTO.getCode()));
        if(CollUtil.isNotEmpty(list))
        {patrolTaskOrganizationMapper.deleteBatchIds(list); }
        //后保存组织信息
        String taskCode = patrolTaskManualDTO.getCode();
        List<String> orgCodeList = patrolTaskManualDTO.getOrgCodeList();
        orgCodeList.stream().forEach(o -> {
            PatrolTaskOrganization organization = new PatrolTaskOrganization();
            organization.setTaskCode(taskCode);
            organization.setDelFlag(0);
            organization.setOrgCode(o);
            patrolTaskOrganizationMapper.insert(organization);
        });
        //先删除
        List<PatrolTaskStation> stationList = patrolTaskStationMapper.selectList( new LambdaQueryWrapper<PatrolTaskStation>().eq(PatrolTaskStation::getTaskCode, patrolTaskManualDTO.getCode()));
        if(CollUtil.isNotEmpty(stationList))
        { patrolTaskStationMapper.deleteBatchIds(stationList);}
        //后保存站点信息
        List<String> stationCodeList = patrolTaskManualDTO.getStationCodeList();
        stationCodeList.stream().forEach(s -> {
            PatrolTaskStation station = new PatrolTaskStation();
            station.setDelFlag(0);
            station.setStationCode(s);
            station.setTaskCode(taskCode);
            patrolTaskStationMapper.insert(station);
        });
        //删除巡检任务标准表的信息
        List<PatrolTaskStandard> taskStandardList = patrolTaskStandardMapper.selectList(new LambdaQueryWrapper<PatrolTaskStandard>().eq(PatrolTaskStandard::getTaskId,patrolTaskManualDTO.getId()));
        if(CollUtil.isNotEmpty(taskStandardList))
        {patrolTaskStandardMapper.deleteBatchIds(taskStandardList);}
        //删除单号
        List<PatrolTaskDevice> devices = patrolTaskDeviceMapper.selectList(new  LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getTaskId,patrolTaskManualDTO.getId()));
        if(CollUtil.isNotEmpty(devices))
        { patrolTaskDeviceMapper.deleteBatchIds(devices); }
        //保存巡检任务标准表的信息
        String taskId = patrolTaskManualDTO.getId();
        List<PatrolTaskStandardDTO> patrolStandardList = patrolTaskManualDTO.getPatrolStandardList();
        patrolStandardList.stream().forEach(ns -> {
            PatrolTaskStandard patrolTaskStandard = new PatrolTaskStandard();
            patrolTaskStandard.setTaskId(taskId);
            patrolTaskStandard.setStandardId(ns.getId());//继承了标准表，id即为标准表id
            patrolTaskStandard.setDelFlag(0);
            patrolTaskStandard.setDeviceTypeCode(ns.getDeviceTypeCode());
            patrolTaskStandard.setSubsystemCode(ns.getSubsystemCode());
            patrolTaskStandard.setProfessionCode(ns.getProfessionCode());
            patrolTaskStandard.setStandardCode(ns.getCode());
            patrolTaskStandardMapper.insert(patrolTaskStandard);
            String taskStandardId = patrolTaskStandard.getId();
            //生成单号
            //判断是否与设备相关
            PatrolStandard patrolStandard = patrolStandardMapper.selectById(ns.getId());
            if (ObjectUtil.isNotNull(patrolStandard) && 1==patrolStandard.getDeviceType()) {
                List<DeviceDTO> deviceList = ns.getDeviceList();
                //遍历设备单号
                deviceList.stream().forEach(dv -> {
                    PatrolTaskDevice patrolTaskDevice = new PatrolTaskDevice();
                    patrolTaskDevice.setTaskId(taskId);//巡检任务id
                    patrolTaskDevice.setDelFlag(0);
                    patrolTaskDevice.setStatus(0);//单号状态
                    String xdCode = "XD" + System.currentTimeMillis();
                    patrolTaskDevice.setPatrolNumber(xdCode);//巡检单号
                    patrolTaskDevice.setTaskStandardId(taskStandardId);//巡检任务标准关联表ID
                    patrolTaskDevice.setDeviceCode(dv.getCode());//设备code
                    Device device = patrolTaskDeviceMapper.getDevice(dv.getCode());
                    patrolTaskDevice.setLineCode(device.getLineCode());//线路code
                    patrolTaskDevice.setStationCode(device.getStationCode());//站点code
                    patrolTaskDevice.setPositionCode(device.getPositionCode());//位置code
                    patrolTaskDeviceMapper.insert(patrolTaskDevice);
                });
            } else {
                List<String> stationCodeList1 = patrolTaskManualDTO.getStationCodeList();
                stationCodeList1.stream().forEach(sc -> {
                    PatrolTaskDevice patrolTaskDevice = new PatrolTaskDevice();
                    patrolTaskDevice.setTaskId(taskId);
                    patrolTaskDevice.setDelFlag(0);
                    patrolTaskDevice.setStatus(0);//单号状态
                    String xdCode = "XD" + System.currentTimeMillis();
                    patrolTaskDevice.setPatrolNumber(xdCode);//巡检单号
                    patrolTaskDevice.setTaskStandardId(taskStandardId);//巡检任务标准关联表ID
                    String lineCode =patrolTaskStationMapper.getLineStaionCode(sc);
                    patrolTaskDevice.setLineCode(lineCode);//线路code
                    patrolTaskDevice.setStationCode(sc);//站点code
                    patrolTaskDeviceMapper.insert(patrolTaskDevice);
                });
            }
        });
    }
}
