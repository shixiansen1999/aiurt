package com.aiurt.boot.task.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.constant.PatrolMessageUrlConstant;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
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
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.boot.utils.PatrolCodeUtil;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.MessageTypeEnum;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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
    private IPatrolTaskDeviceService patrolTaskDeviceService;
    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;
    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;
    @Autowired
    private PatrolCheckResultMapper patrolCheckResultMapper;

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
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IBaseApi baseApi;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;
    @Autowired
    private PatrolAccompanyMapper accompanyMapper;
    @Autowired
    private ISysParamAPI iSysParamAPI;

    @Override
    public IPage<PatrolTaskParam> getTaskList(Page<PatrolTaskParam> page, PatrolTaskParam patrolTaskParam) {
        // 数据权限过滤
        List<String> taskCode = new ArrayList<>();
        try {
            taskCode = this.taskDataPermissionFilter();
        } catch (Exception e) {
            return page;
        }

        IPage<PatrolTaskParam> taskPage = patrolTaskMapper.getTaskList(page, patrolTaskParam, taskCode);
        // 禁用数据权限过滤-start
        boolean filter = GlobalThreadLocal.setDataFilter(false);
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
            // 巡检用户信息
            if (CollectionUtils.isEmpty(l.getUserInfo())) {
                QueryWrapper<PatrolTaskUser> userWrapper = new QueryWrapper<>();
                userWrapper.lambda().eq(PatrolTaskUser::getTaskCode, l.getCode());
                List<PatrolTaskUser> userInfo = Optional.ofNullable(patrolTaskUserMapper.selectList(userWrapper))
                        .orElseGet(Collections::emptyList).stream().collect(Collectors.toList());
                l.setUserInfo(userInfo);
            }
        });
        // 禁用数据权限过滤-end
        GlobalThreadLocal.setDataFilter(filter);
        return taskPage;
    }

    /**
     * 巡视任务数据权限过滤
     *
     * @return
     */
    private List<String> taskDataPermissionFilter() throws AiurtBootException {
        List<String> taskCodesByOrg = patrolTaskOrganizationMapper.getTaskCodeByUserOrg();
        List<String> taskCodesByMajorSystem = patrolTaskStandardMapper.getTaskCodeByUserMajorSystem();
        List<String> taskCodesByStation = patrolTaskStationMapper.getTaskCodeByUserStation();
        List<String> taskCodes = CollectionUtil.intersection(taskCodesByOrg, taskCodesByMajorSystem, taskCodesByStation).stream().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(taskCodes)) {
            throw new AiurtBootException("暂无任务！");
        }
        return taskCodes;
    }

    @Override
    public PatrolTaskParam selectBasicInfo(PatrolTaskParam patrolTaskParam) {
        if (StrUtil.isEmpty(patrolTaskParam.getId())) {
            throw new AiurtBootException("记录的ID不能为空！");
        }
        QueryWrapper<PatrolTaskParam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PatrolTaskParam::getId, patrolTaskParam.getId());
        PatrolTaskParam taskParam = patrolTaskMapper.selectBasicInfo(patrolTaskParam);
        Assert.notNull(taskParam, "未找到对应记录！");
        // 组织机构信息
        List<PatrolTaskOrganizationDTO> organizationInfo = patrolTaskOrganizationMapper.selectOrgByTaskCode(taskParam.getCode());
        // 站点信息
        List<PatrolTaskStationDTO> stationInfo = patrolTaskStationMapper.selectStationByTaskCode(taskParam.getCode());
        // 巡检用户
        QueryWrapper<PatrolTaskUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(PatrolTaskUser::getTaskCode, taskParam.getCode());
        List<PatrolTaskUser> userList = patrolTaskUserMapper.selectList(userQueryWrapper);

        PatrolPlan patrolPlan = Optional.ofNullable(patrolPlanMapper.selectOne(new QueryWrapper<PatrolPlan>().lambda().eq(PatrolPlan::getCode, taskParam.getPlanCode())))
                .orElseGet(PatrolPlan::new);
        // 获取任务的专业信息
        List<String> majorInfo = patrolPlanMapper.getMajorInfoByPlanId(taskParam.getId());
        // 获取任务的子系统信息
        List<String> subsystemInfo = patrolPlanMapper.getSubsystemInfoByPlanId(taskParam.getId());
        // 同行人
        String accompanyUserName = patrolTaskDeviceMapper.getAccompanyUserByTaskId(taskParam.getId());

        taskParam.setDepartInfo(organizationInfo);
        taskParam.setStationInfo(stationInfo);
        taskParam.setUserInfo(userList);
        taskParam.setMajorInfo(majorInfo);
        taskParam.setSubsystemInfo(subsystemInfo);
        taskParam.setAccompanyName(accompanyUserName);
        if (StrUtil.isNotEmpty(taskParam.getEndUserId())) {
            taskParam.setEndUsername(patrolTaskMapper.getUsername(taskParam.getEndUserId()));
        }
        taskParam.setDisposeUserName(patrolTaskMapper.getUsername(taskParam.getDisposeId()));

        return taskParam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int taskAppoint(PatrolAppointInfoDTO patrolAppointInfoDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        // 用户信息数据
        Map<String, List<PatrolAppointUserDTO>> map = Optional.ofNullable(patrolAppointInfoDTO.getMap()).orElseGet(ConcurrentHashMap::new);
        AtomicInteger count = new AtomicInteger();
        for (Map.Entry<String, List<PatrolAppointUserDTO>> listEntry : map.entrySet()) {
            List<PatrolAppointUserDTO> list = listEntry.getValue();
            if (CollUtil.isEmpty(list)) {
                throw new AiurtBootException("未指定用户，请指定用户！");
            }
            // 根据任务code查找未指派的任务
            QueryWrapper<PatrolTask> taskWrapper = new QueryWrapper<>();
            taskWrapper.lambda()
                    .eq(PatrolTask::getCode, listEntry.getKey())
                    .eq(PatrolTask::getDiscardStatus, PatrolConstant.TASK_UNDISCARD)
                    .and(status -> status.eq(PatrolTask::getStatus, PatrolConstant.TASK_INIT)
                            .or()
                            .eq(PatrolTask::getStatus, PatrolConstant.TASK_RETURNED));
            PatrolTask patrolTask = patrolTaskMapper.selectOne(taskWrapper);

            if (ObjectUtil.isNotEmpty(patrolTask)) {
                // 指派之前查询是否指派过巡检用户，存在则删除掉然后再添加(主要是重新生成接口)
                QueryWrapper<PatrolTaskUser> taskUserWrapper = new QueryWrapper<>();
                taskUserWrapper.lambda().eq(PatrolTaskUser::getTaskCode, listEntry.getKey());
                List<PatrolTaskUser> taskUserList = patrolTaskUserMapper.selectList(taskUserWrapper);
                if (CollectionUtil.isNotEmpty(taskUserList)) {
                    List<String> collect = taskUserList.stream().map(l -> l.getId()).collect(Collectors.toList());
                    patrolTaskUserMapper.deleteBatchIds(collect);
                }

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
                    // 作业类型
                    task.setType(patrolAppointInfoDTO.getType());
                    // 计划令编号和图片地址
                    task.setPlanOrderCode(patrolAppointInfoDTO.getPlanOrderCode());
                    task.setPlanOrderCodeUrl(patrolAppointInfoDTO.getPlanOrderCodeUrl());
                    if (ObjectUtil.isEmpty(patrolTask.getSource())) {
                        task.setSource(PatrolConstant.TASK_COMMON);
                    }
                    // 更新检查开始结束时间
                    task.setStartTime(patrolAppointInfoDTO.getStartTime());
                    task.setEndTime(patrolAppointInfoDTO.getEndTime());
                    // 任务状态
                    task.setStatus(PatrolConstant.TASK_CONFIRM);
                    // 记录指派人
                    task.setAssignId(loginUser.getId());
                    // 更改任务状态为待确认
                    patrolTaskMapper.update(task, taskWrapper);
                    count.getAndIncrement();
                }
            }
        }
        // 发送消息
        this.sendMessagePC(map);
        return count.get();
    }

    /**
     * PC巡视任务指派发送消息
     *
     * @param map
     */
    private void sendMessagePC(Map<String, List<PatrolAppointUserDTO>> map) {
        if (CollectionUtil.isNotEmpty(map)) {
            List<PatrolTask> list = this.lambdaQuery()
                    .eq(PatrolTask::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .in(PatrolTask::getCode, map.keySet())
                    .list();
            for (String code : map.keySet()) {
                PatrolTask patrolTask = list.stream().filter(l -> code.equals(l.getCode())).findFirst().get();
                List<PatrolAppointUserDTO> users = map.get(code);
                String[] userIds = users.stream().map(PatrolAppointUserDTO::getUserId).toArray(String[]::new);
                List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                String userNames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
                if (ObjectUtil.isEmpty(patrolTask) || CollectionUtil.isEmpty(loginUsers)) {
                    continue;
                }

                LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
                //发送通知
                MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(),userNames, "巡视任务-指派" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
                PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
                BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_ASSIGN.getType());
                patrolMessageDTO.setMessageType(MessageTypeEnum.XT.getType());
                patrolMessageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
                patrolMessageDTO.setMsgAbstract("新的巡视任务");
                patrolMessageDTO.setPublishingContent("接收到新的巡视任务，请尽快确认");
                sendMessage(messageDTO,userNames,null,patrolMessageDTO);
            }
        }
    }

    /**
     * APP指派发送消息
     *
     * @param patrolAccompanyList
     */
    @Override
    public void sendMessageApp(PatrolTaskAppointSaveDTO patrolAccompanyList) {
        List<PatrolAccompanyDTO> accompanys = patrolAccompanyList.getAccompanyDTOList();
        if (ObjectUtil.isEmpty(patrolAccompanyList)
                || StrUtil.isEmpty(patrolAccompanyList.getId())
                || CollectionUtil.isEmpty(accompanys)) {
            return;
        }
        PatrolTask patrolTask = this.getById(patrolAccompanyList.getId());
        if (ObjectUtil.isEmpty(patrolTask)) {
            return;
        }
        String[] userIds = accompanys.stream().map(PatrolAccompanyDTO::getUserId).toArray(String[]::new);
        List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
        String userNames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        //发送通知
        MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(),userNames, "巡视任务-指派" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
        PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
        BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
        //业务类型，消息类型，消息模板编码，摘要，发布内容
        patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_ASSIGN.getType());
        patrolMessageDTO.setMessageType(MessageTypeEnum.XT.getType());
        patrolMessageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
        patrolMessageDTO.setMsgAbstract("新的巡视任务");
        patrolMessageDTO.setPublishingContent("接收到新的巡视任务，请尽快确认");
        sendMessage(messageDTO,userNames,null,patrolMessageDTO);
    }


    /**
     * 巡视任务确认后发送待办消息
     *
     * @param patrolTask
     */
    private void sendWaitingMessage(PatrolTask patrolTask) {
        QueryWrapper<PatrolTaskUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PatrolTaskUser::getTaskCode, patrolTask.getCode()).eq(PatrolTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<PatrolTaskUser> taskUsers = patrolTaskUserMapper.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(taskUsers)) {
            return;
        }

        String[] userIds = taskUsers.stream().map(PatrolTaskUser::getUserId).toArray(String[]::new);
        List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
        String userNames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));

        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",patrolTask.getCode());
        map.put("patrolTaskName",patrolTask.getName());
        String station = patrolTaskStationMapper.getStationByTaskCode(patrolTask.getCode());
        map.put("patrolStation",station);
        map.put("patrolTaskTime",patrolTask.getStartTime().toString()+patrolTask.getEndTime().toString());
        map.put("patrolName", userNames);

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setData(map);
        todoDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
        todoDTO.setTitle("巡视任务-漏检");
        todoDTO.setMsgAbstract("巡视任务-漏检");
        todoDTO.setPublishingContent("巡视任务漏检，请尽快处置");

        todoDTO.setProcessDefinitionName("巡视管理");
        todoDTO.setTaskName(patrolTask.getName() + "(待执行)");
        todoDTO.setBusinessKey(patrolTask.getId());
        todoDTO.setBusinessType(TodoBusinessTypeEnum.PATROL_EXECUTE.getType());
        todoDTO.setCurrentUserName(loginUser.getUsername());
        todoDTO.setTaskType(TodoTaskTypeEnum.PATROL.getType());
        todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
        todoDTO.setUrl(PatrolMessageUrlConstant.AFFIRM_URL);
        todoDTO.setAppUrl(PatrolMessageUrlConstant.AFFIRM_APP_URL);
        isTodoBaseAPI.createTodoTask(todoDTO);
    }

    /**
     * 任务审核不通过发送消息给巡视人
     *
     * @param id
     */
    private void sendAuditNoPassMessage(String id, LoginUser loginUser) {
        PatrolTask patrolTask = this.getById(id);
        QueryWrapper<PatrolTaskUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PatrolTaskUser::getTaskCode, patrolTask.getCode()).eq(PatrolTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<PatrolTaskUser> taskUsers = patrolTaskUserMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(taskUsers)) {
            return;
        }
        String[] userIds = taskUsers.stream().map(PatrolTaskUser::getUserId).toArray(String[]::new);
        List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
        String userNames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));

        //发送通知
        MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(),userNames, "审核驳回" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
        PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
        BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        map.put("rejectReason",patrolTask.getRejectReason());
        messageDTO.setData(map);
        //业务类型，消息类型，消息模板编码，摘要，发布内容
        patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_AUDIT.getType());
        patrolMessageDTO.setMessageType(MessageTypeEnum.XT.getType());
        patrolMessageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE_REJECT);
        patrolMessageDTO.setMsgAbstract("巡视任务审核驳回");
        patrolMessageDTO.setPublishingContent("巡视任务审核驳回，请重新处理");
        sendMessage(messageDTO,userNames,null,patrolMessageDTO);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> patrolTaskAudit(String id, Integer status, String remark, String backReason) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作!");
        if (StrUtil.isNotBlank(loginUser.getRoleCodes())) {
            List<String> roleCodes = StrUtil.split(loginUser.getRoleCodes(), ',');
            if (!roleCodes.contains(RoleConstant.FOREMAN)) {
                throw new AiurtBootException("您没有审核操作权限！");
            }
        }
        LambdaUpdateWrapper<PatrolTask> queryWrapper = new LambdaUpdateWrapper<>();
        // 任务有一个人审核则更新待办消息
        isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.PATROL_AUDIT.getType(), id, loginUser.getUsername(), CommonTodoStatus.DONE_STATUS_1);
        //不通过传0
        if (PatrolConstant.AUDIT_NOPASS.equals(status)) {
            queryWrapper.set(PatrolTask::getStatus, PatrolConstant.TASK_BACK).set(PatrolTask::getRemark, backReason).eq(PatrolTask::getId, id);
            this.update(queryWrapper);
            // 审核不通过则给任务的巡视人发送消息
            this.sendAuditNoPassMessage(id, loginUser);
            return Result.OK("不通过");
        } else {
            queryWrapper.set(PatrolTask::getStatus, PatrolConstant.TASK_COMPLETE).set(PatrolTask::getAuditorRemark, remark).set(PatrolTask::getAuditorTime, new Date()).eq(PatrolTask::getId, id);
            this.update(queryWrapper);
            return Result.OK("通过成功");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
        if (ObjectUtil.isNotEmpty(patrolTaskDTO.getDateScope())) {
            String[] split = patrolTaskDTO.getDateScope().split(",");
            Date dateHead = DateUtil.parse(split[0], "yyyy-MM-dd");
            Date dateEnd = DateUtil.parse(split[1], "yyyy-MM-dd");
            patrolTaskDTO.setDateHead(dateHead);
            patrolTaskDTO.setDateEnd(dateEnd);

        }
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        List<CsUserDepartModel> userDepartModelList = sysBaseApi.getDepartByUserId(sysUser.getId());
//        List<String> orgCodeList = userDepartModelList.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
//        boolean admin = SecurityUtils.getSubject().hasRole("admin");
//        if (!admin) {
//            patrolTaskDTO.setUserHaveOrgCodeList(orgCodeList);
//        }
        // 数据权限过滤
        try {
            List<String> taskCodes = this.taskDataPermissionFilter();
            patrolTaskDTO.setTaskCodes(taskCodes);
        } catch (AiurtBootException e) {
            return pageList;
        }

        List<PatrolTaskDTO> taskList = patrolTaskMapper.getPatrolTaskPoolList(pageList, patrolTaskDTO);
        taskList.stream().forEach(e -> {
            String userName = patrolTaskMapper.getUserName(e.getBackId());
            List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(e.getId());
            String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).distinct().collect(Collectors.joining("；"));
            String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).distinct().collect(Collectors.joining("；"));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getCode());
            e.setOrganizationName(manager.translateOrg(orgCodes));
            List<StationDTO> stationName = patrolTaskMapper.getStationName(e.getCode());
            e.setStationName(manager.translateStation(stationName));
            e.setEndUserName(e.getEndUserName() == null ? "-" : e.getEndUserName());
            e.setSubmitTime(e.getSubmitTime() == null ? "-" : e.getSubmitTime());
            e.setPeriod(e.getPeriod() == null ? "-" : e.getPeriod());
            e.setSysName(sysName);
            e.setMajorName(majorName);
            e.setOrgCodeList(orgCodes);
            e.setPatrolUserName(manager.spliceUsername(e.getCode()));
            e.setPatrolReturnUserName(userName == null ? "-" : userName);
        });
        return pageList.setRecords(taskList);
    }

    @Override
    public Page<PatrolTaskDTO> getPatrolTaskList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO) {
        if (ObjectUtil.isNotEmpty(patrolTaskDTO.getDateScope())) {
            String[] split = patrolTaskDTO.getDateScope().split(",");
            Date dateHead = DateUtil.parse(split[0], "yyyy-MM-dd");
            Date dateEnd = DateUtil.parse(split[1], "yyyy-MM-dd");
            patrolTaskDTO.setDateHead(dateHead);
            patrolTaskDTO.setDateEnd(dateEnd);

        }
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        List<CsUserDepartModel> userDepartModelList = sysBaseApi.getDepartByUserId(sysUser.getId());
//        List<String> orgCodeList = userDepartModelList.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
//        boolean admin = SecurityUtils.getSubject().hasRole("admin");
//        if (!admin) {
//            patrolTaskDTO.setUserHaveOrgCodeList(orgCodeList);
//        }
        // 数据权限过滤
        try {
            List<String> taskCodes = this.taskDataPermissionFilter();
            patrolTaskDTO.setTaskCodes(taskCodes);
        } catch (AiurtBootException e) {
            return pageList;
        }
        List<PatrolTaskDTO> taskList = patrolTaskMapper.getPatrolTaskList(pageList, patrolTaskDTO);
        taskList.stream().forEach(e -> {
            String userName = patrolTaskMapper.getUserName(e.getBackId());
            List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(e.getId());
            String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).distinct().collect(Collectors.joining("；"));
            String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).distinct().collect(Collectors.joining("；"));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getCode());
            e.setOrganizationName(manager.translateOrg(orgCodes));
            List<StationDTO> stationName = patrolTaskMapper.getStationName(e.getCode());
            List<PatrolTaskDevice> taskDeviceList = patrolTaskDeviceMapper.selectList(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getTaskId, e.getId()));
            List<PatrolAccompany> accompanyList = new ArrayList<>();
            for (PatrolTaskDevice patrolTaskDevice : taskDeviceList) {
                List<PatrolAccompany> patrolAccompanies = accompanyMapper.selectList(new LambdaQueryWrapper<PatrolAccompany>().eq(PatrolAccompany::getTaskDeviceCode, patrolTaskDevice.getPatrolNumber()));
                if (CollUtil.isNotEmpty(patrolAccompanies)) {
                    accompanyList.addAll(patrolAccompanies);
                }
            }
            if (CollUtil.isNotEmpty(accompanyList)) {
                accompanyList = accompanyList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PatrolAccompany::getUserId))), ArrayList::new));
                String peerPeople = accompanyList.stream().map(PatrolAccompany::getUsername).collect(Collectors.joining(";"));
                e.setPeerPeople(peerPeople);
            }
            e.setStationName(manager.translateStation(stationName));
            e.setSysName(sysName);
            e.setMajorName(majorName);
            e.setOrgCodeList(orgCodes);
            e.setPatrolUserName(manager.spliceUsername(e.getCode()));
            e.setPatrolReturnUserName(userName == null ? "-" : userName);
            e.setPeriod(e.getPeriod() == null ? "-" : e.getPeriod());
            e.setPatrolReturnUserName(e.getPeriod() == null ? "-" : e.getPeriod());
            e.setSource(e.getSource() == null ? "-" : e.getSource());
        });
        return pageList.setRecords(taskList);
    }

    @Override
    public void getPatrolTaskReceive(PatrolTaskDTO patrolTaskDTO) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        PatrolTask patrolTask = patrolTaskMapper.selectById(patrolTaskDTO.getId());
        //个人领取：将待指派或退回之后重新领取改为待执行，变为个人领取（传任务主键id,状态）
        if (PatrolConstant.TASK_INIT.equals(patrolTaskDTO.getStatus()) || PatrolConstant.TASK_RETURNED.equals(patrolTaskDTO.getStatus())) {
            // 当前登录人所属部门是在检修任务的指派部门范围内才可以领取
            List<PatrolTaskOrganization> patrolTaskOrganizations = patrolTaskOrganizationMapper.selectList(new LambdaQueryWrapper<PatrolTaskOrganization>()
                    .eq(PatrolTaskOrganization::getTaskCode, patrolTask.getCode())
                    .eq(PatrolTaskOrganization::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (CollUtil.isNotEmpty(patrolTaskOrganizations)) {
                List<String> orgList = patrolTaskOrganizations.stream().map(PatrolTaskOrganization::getOrgCode).collect(Collectors.toList());
                if (!orgList.contains(manager.checkLogin().getOrgCode()) && !admin) {
                    throw new AiurtBootException("只有该任务的巡检人才可以领取");
                }
            }
            //删除之前的巡检人
            List<PatrolTaskUser> taskUserList = patrolTaskUserMapper.selectList(new LambdaQueryWrapper<PatrolTaskUser>().eq(PatrolTaskUser::getTaskCode, patrolTask.getCode()));
            if (CollUtil.isNotEmpty(taskUserList)) {
                patrolTaskUserMapper.deleteBatchIds(taskUserList);
            }
            updateWrapper.set(PatrolTask::getStatus, 2)
                    .set(PatrolTask::getSource, 1)
                    .eq(PatrolTask::getId, patrolTaskDTO.getId());
            update(updateWrapper);
            //添加巡检人
            PatrolTaskUser patrolTaskUser = new PatrolTaskUser();
            patrolTaskUser.setTaskCode(patrolTask.getCode());
            patrolTaskUser.setUserId(sysUser.getId());
            patrolTaskUser.setUserName(sysUser.getRealname());
            patrolTaskUser.setDelFlag(0);
            patrolTaskUserMapper.insert(patrolTaskUser);
            // 领取后发送待办消息
            this.sendWaitingMessage(patrolTask);
        }
        //确认：将待确认改为待执行
        if (PatrolConstant.TASK_CONFIRM.equals(patrolTaskDTO.getStatus())) {
            if (manager.checkTaskUser(patrolTask.getCode()) == false && !admin) {
                throw new AiurtBootException("只有该任务的巡检人才可以确认");
            }
            updateWrapper.set(PatrolTask::getStatus, 2).eq(PatrolTask::getId, patrolTaskDTO.getId());
            update(updateWrapper);
            // 确认后发送待办消息
            this.sendWaitingMessage(patrolTask);
        }
        //执行：将待执行改为执行中
        if (PatrolConstant.TASK_EXECUTE.equals(patrolTaskDTO.getStatus())) {
            if (manager.checkTaskUser(patrolTask.getCode()) == false && !admin) {
                throw new AiurtBootException("只有该任务的巡检人才可以执行");
            }
            updateWrapper.set(PatrolTask::getStatus, 4)
                    .set(PatrolTask::getBeginTime, new Date())
                    .eq(PatrolTask::getId, patrolTaskDTO.getId());
            update(updateWrapper);
            // 执行之后更新所有人的待办
            isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.PATROL_EXECUTE.getType(), patrolTask.getId(), sysUser.getId(), CommonTodoStatus.DONE_STATUS_1);
        }

    }


    @Override
    public void getPatrolTaskReturn(PatrolTaskDTO patrolTaskDTO) {
        LambdaQueryWrapper<PatrolTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PatrolTask::getId, patrolTaskDTO.getId());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        PatrolTask patrolTask = patrolTaskMapper.selectOne(queryWrapper);
        if (manager.checkTaskUser(patrolTask.getCode()) == false && !admin) {
            throw new AiurtBootException("只有该任务的巡检人才可以退回");
        }
        //更新巡检状态及添加退回理由、退回人Id（传任务主键id、退回理由）
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolTask::getStatus, 3)
                .set(PatrolTask::getBackId, sysUser.getId())
                .set(PatrolTask::getBackReason, patrolTaskDTO.getBackReason())
                .eq(PatrolTask::getId, patrolTaskDTO.getId());
        update(updateWrapper);
        // 发送消息给指派人，使得指派人重新指派任务
        if (PatrolConstant.TASK_COMMON.equals(patrolTask.getSource()) || PatrolConstant.TASK_MANUAL.equals(patrolTask.getSource())) {
            String assignId = patrolTask.getAssignId();
            if (StrUtil.isNotEmpty(assignId)) {
                LoginUser user = sysBaseApi.getUserById(assignId);
                //发送通知
                MessageDTO messageDTO = new MessageDTO(sysUser.getUsername(),user.getUsername(), "巡视任务退回后" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
                PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
                BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
                //构建消息模板
                HashMap<String, Object> map = new HashMap<>();
                map.put("backReason",patrolTask.getBackReason());
                messageDTO.setData(map);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_ASSIGN.getType());
                patrolMessageDTO.setMessageType(MessageTypeEnum.XT.getType());
                patrolMessageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE_RETURN);
                patrolMessageDTO.setMsgAbstract("巡视任务退回");
                patrolMessageDTO.setPublishingContent("巡视任务退回，请重新安排");
                sendMessage(messageDTO,null,user.getUsername(),patrolMessageDTO);

                // 同时需要更新待执行任务为已办
                isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.PATROL_EXECUTE.getType(), patrolTask.getId(), sysUser.getId(), CommonTodoStatus.DONE_STATUS_1);
            }
            return;
        }
        // 个人领取的任务退回后发送消息给工班长
        QueryWrapper<PatrolTaskOrganization> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PatrolTaskOrganization::getDelFlag, CommonConstant.DEL_FLAG_0).eq(PatrolTaskOrganization::getTaskCode, patrolTask.getCode());
        List<PatrolTaskOrganization> organizations = patrolTaskOrganizationMapper.selectList(wrapper);
        List<String> orgCodes = organizations.stream().map(PatrolTaskOrganization::getOrgCode).collect(Collectors.toList());
        String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(orgCodes, Arrays.asList(RoleConstant.FOREMAN));
        if (StrUtil.isEmpty(userName)) {
            return;
        }
        LoginUser user= sysBaseApi.getUserByName(userName);
        //发送通知
        MessageDTO messageDTO = new MessageDTO(sysUser.getUsername(),userName, "巡视任务退回后" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
        PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
        BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        map.put("backReason",patrolTask.getBackReason());
        messageDTO.setData(map);
        //业务类型，消息类型，消息模板编码，摘要，发布内容
        patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_ASSIGN.getType());
        patrolMessageDTO.setMessageType(MessageTypeEnum.XT.getType());
        patrolMessageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE_RETURN);
        patrolMessageDTO.setMsgAbstract("巡视任务退回");
        patrolMessageDTO.setPublishingContent("巡视任务退回，请重新安排");
        sendMessage(messageDTO,null,user.getUsername(),patrolMessageDTO);
    }

    @Override
    public List<PatrolTaskUserDTO> getPatrolTaskAppointSelect(PatrolOrgDTO orgCode) {
        //查询这个部门的信息人员,传组织机构ids
        List<PatrolTaskUserDTO> arrayList = new ArrayList<>();
        for (String code : orgCode.getOrg()) {
            PatrolTaskUserDTO userDTO = new PatrolTaskUserDTO();
            String organizationName = patrolTaskMapper.getOrgName(code);
            List<PatrolTaskUserContentDTO> user = patrolTaskMapper.getUser(code);
            userDTO.setOrgCode(code);
            userDTO.setOrganizationName(organizationName);
            userDTO.setUserList(user);
            arrayList.add(userDTO);
        }
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测为未登录状态，请登录系统后操作！");
        }
        // 获取当前登录人的部门权限
        List<CsUserDepartModel> departList = sysBaseApi.getDepartByUserId(loginUser.getId());
        List<String> orgCodes = departList.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
        // 当前登录人的部门权限和任务的组织机构交集
        List<String> intersectOrg = CollectionUtil.intersection(orgCodes, orgCode.getOrg()).stream().collect(Collectors.toList());
        // 根据当班人员过滤待指派人员信息
        if (ObjectUtil.isNotEmpty(orgCode.getIdentity())) {
            if (CollectionUtil.isEmpty(orgCodes) || CollectionUtil.isEmpty(intersectOrg)) {
                return new ArrayList<>();
            }
            // 根据配置决定是否关联排班
            SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_SCHEDULING);
            boolean value = "1".equals(paramModel.getValue()) ? true : false;
            if (value) {
                // 获取今日当班的人员
                List<SysUserTeamDTO> todayOndutyDetail = baseApi.getTodayOndutyDetailNoPage(intersectOrg, new Date());
                List<String> todayUserId = todayOndutyDetail.stream().map(SysUserTeamDTO::getUserId).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(todayUserId)) {
                    return new ArrayList<>();
                }
                arrayList.stream().forEach(l -> {
                    List<PatrolTaskUserContentDTO> userList = Optional.ofNullable(l.getUserList()).orElseGet(Collections::emptyList);
                    List<PatrolTaskUserContentDTO> newUserList = userList.stream()
                            .filter(u -> todayUserId.contains(u.getId()))
                            .collect(Collectors.toList());
                    l.setUserList(newUserList);
                });
            }
        }
        PatrolTask patrolTask = patrolTaskMapper.selectById(orgCode.getTaskId());
        if (PatrolConstant.TASK_RETURNED.equals(patrolTask.getStatus())) {
            return arrayList;
        }
        List<PatrolTaskUser> patrolTaskUsers = patrolTaskUserMapper.selectList(new LambdaQueryWrapper<PatrolTaskUser>().eq(PatrolTaskUser::getTaskCode, patrolTask.getCode()));
        if (PatrolConstant.TASK_COMMON.equals(patrolTask.getSource())) {
            arrayList.stream().forEach(a -> {
                List<String> collect = patrolTaskUsers.stream().map(PatrolTaskUser::getUserId).collect(Collectors.toList());
                List<PatrolTaskUserContentDTO> userList = a.getUserList();
                List<PatrolTaskUserContentDTO> list = userList.stream().filter(l -> collect.contains(l.getId())).collect(Collectors.toList());
                userList.removeAll(list);
                a.setUserList(userList);
            });
            return arrayList;
        } else {
            //同行人的判断
            if (ObjectUtil.isNull(orgCode.getIdentity())) {
                arrayList.stream().forEach(e -> {
                    List<String> userIdList = patrolTaskUsers.stream().map(PatrolTaskUser::getUserId).collect(Collectors.toList());
                    List<PatrolTaskUserContentDTO> userList = e.getUserList();
                    List<PatrolTaskUserContentDTO> list = userList.stream().filter(l -> userIdList.contains(l.getId())).collect(Collectors.toList());
                    userList.removeAll(list);
                    e.setUserList(userList);
                });
                return arrayList;
            } else {
                return arrayList;
            }
        }
    }

    @Override
    public Page<PatrolTaskDTO> getPatrolTaskManualList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO) {
        if (ObjectUtil.isNotEmpty(patrolTaskDTO.getDateScope())) {
            String[] split = patrolTaskDTO.getDateScope().split(",");
            Date dateHead = DateUtil.parse(split[0], "yyyy-MM-dd");
            Date dateEnd = DateUtil.parse(split[1], "yyyy-MM-dd");
            patrolTaskDTO.setDateHead(dateHead);
            patrolTaskDTO.setDateEnd(dateEnd);

        }
        // 数据权限过滤
        List<String> taskCode = new ArrayList<>();
        try {
            taskCode = this.taskDataPermissionFilter();
            patrolTaskDTO.setTaskCodes(taskCode);
        } catch (Exception e) {
            return pageList;
        }
        List<PatrolTaskDTO> taskDTOList = patrolTaskMapper.getPatrolTaskManualList(pageList, patrolTaskDTO);
        taskDTOList.stream().forEach(e -> {
            String userName = patrolTaskMapper.getUserName(e.getBackId());
            List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(e.getId());
            String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).distinct().collect(Collectors.joining("；"));
            String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).distinct().collect(Collectors.joining("；"));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getCode());
            e.setOrganizationName(manager.translateOrg(orgCodes));
            List<String> stationCodeList = patrolTaskMapper.getStationCode(e.getCode());
            List<StationDTO> stationName = patrolTaskMapper.getStationName(e.getCode());
            e.setStationName(manager.translateStation(stationName));
            e.setSysName(sysName);
            e.setStationCodeList(stationCodeList);
            e.setMajorName(majorName);
            e.setOrgCodeList(orgCodes);
            e.setPatrolUserName(manager.spliceUsername(e.getCode()));
            e.setPatrolReturnUserName(userName);
        });
        return pageList.setRecords(taskDTOList);
    }

    @Override
    public List<MajorDTO> getMajorSubsystemGanged(String id) {
        QueryWrapper<PatrolTaskStandard> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PatrolTaskStandard::getTaskId, id);
        List<PatrolTaskStandard> list = patrolTaskStandardMapper.selectList(wrapper);
        // 专业编码
        List<String> majorInfo = list.stream().map(PatrolTaskStandard::getProfessionCode).distinct().collect(Collectors.toList());
        // 子系统编码
        List<String> subSystemInfo = list.stream().map(PatrolTaskStandard::getSubsystemCode).distinct().collect(Collectors.toList());

        List<MajorDTO> major = new ArrayList<>();
        List<SubsystemDTO> subsystem = new ArrayList<>();

        Optional.ofNullable(majorInfo).orElseGet(Collections::emptyList).stream()
                .filter(l -> StrUtil.isNotEmpty(l))
                .forEach(l -> {
                    MajorDTO majorDTO = new MajorDTO();
                    majorDTO.setMajorCode(l);
                    // 专业名称
                    String majorName = patrolTaskMapper.getMajorNameByMajorCode(l);
                    majorDTO.setMajorName(majorName);
                    major.add(majorDTO);
                });
        Optional.ofNullable(subSystemInfo).orElseGet(Collections::emptyList).stream()
                .filter(l -> StrUtil.isNotEmpty(l))
                .forEach(l -> {
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
                List<SubsystemDTO> subsystemInfo = patrolTaskMapper.getMajorSubsystemGanged(l.getMajorCode(), subsystem);
                l.setSubsystemInfo(subsystemInfo);
            }
        });
        return major;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int taskAudit(String code, Integer auditStatus, String auditReason, String remark) {
        QueryWrapper<PatrolTask> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(PatrolTask::getCode, code);
        PatrolTask patrolTask = patrolTaskMapper.selectOne(wrapper);
        // 获取当前登录用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("未发现登录用户，请登录系统后操作！");
        }
        if (StrUtil.isNotBlank(loginUser.getRoleCodes())) {
            List<String> roleCodes = StrUtil.split(loginUser.getRoleCodes(), ',');
            if (!roleCodes.contains(RoleConstant.FOREMAN)) {
                throw new AiurtBootException("您没有审核操作权限！");
            }
        }
        patrolTask.setAuditorId(loginUser.getId());
        if (PatrolConstant.AUDIT_NOPASS.equals(auditStatus)) {
            if (StrUtil.isEmpty(auditReason)) {
                throw new AiurtBootException("审核不通过原因不能为空！");
            }
            patrolTask.setRejectReason(auditReason);
            patrolTask.setStatus(PatrolConstant.TASK_BACK);
            // 任务审核不通过发送消息给巡视人
            this.sendAuditNoPassMessage(patrolTask.getId(), loginUser);
        } else {
            patrolTask.setStatus(PatrolConstant.TASK_COMPLETE);
            patrolTask.setAuditorRemark(remark);
            patrolTask.setAuditorTime(new Date());
        }
        int updateById = patrolTaskMapper.updateById(patrolTask);
        // 任务有一个人审核后则更新待办消息为已办
        isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.PATROL_AUDIT.getType(), patrolTask.getId(), loginUser.getUsername(), CommonTodoStatus.DONE_STATUS_1);
        return updateById;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getPatrolTaskSubmit(PatrolTaskDTO patrolTaskDTO) {
        //提交任务：将待执行、执行中，变为待审核、添加任务结束人id,传签名地址、任务主键id、审核状态
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        PatrolTask patrolTask = patrolTaskMapper.selectById(patrolTaskDTO.getId());
        if (manager.checkTaskUser(patrolTask.getCode()) == false && !admin) {
            throw new AiurtBootException("只有该任务的巡检人才可以提交任务");
        } else {
            List<PatrolTaskDevice> taskDevices = patrolTaskDeviceMapper.selectList(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getTaskId, patrolTask.getId()));
            List<PatrolTaskDevice> errDeviceList = taskDevices.stream().filter(e -> PatrolConstant.RESULT_EXCEPTION.equals(e.getCheckResult())).collect(Collectors.toList());
            LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
            if (PatrolConstant.TASK_CHECK.equals(patrolTask.getAuditor())) {
                if (CollUtil.isNotEmpty(errDeviceList)) {
                    updateWrapper.set(PatrolTask::getStatus, 6)
                            .set(PatrolTask::getEndUserId, sysUser.getId())
                            .set(PatrolTask::getSignUrl, patrolTaskDTO.getSignUrl())
                            .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                            .set(PatrolTask::getAbnormalState, 0)
                            .eq(PatrolTask::getId, patrolTaskDTO.getId());
                } else {
                    updateWrapper.set(PatrolTask::getStatus, 6)
                            .set(PatrolTask::getEndUserId, sysUser.getId())
                            .set(PatrolTask::getSignUrl, patrolTaskDTO.getSignUrl())
                            .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                            .set(PatrolTask::getAbnormalState, 1)
                            .eq(PatrolTask::getId, patrolTaskDTO.getId());
                }
            } else {
                if (CollUtil.isNotEmpty(errDeviceList)) {
                    updateWrapper.set(PatrolTask::getStatus, 7)
                            .set(PatrolTask::getEndUserId, sysUser.getId())
                            .set(PatrolTask::getSignUrl, patrolTaskDTO.getSignUrl())
                            .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                            .set(PatrolTask::getAbnormalState, 0)
                            .eq(PatrolTask::getId, patrolTaskDTO.getId());
                } else {
                    updateWrapper.set(PatrolTask::getStatus, 7)
                            .set(PatrolTask::getEndUserId, sysUser.getId())
                            .set(PatrolTask::getSignUrl, patrolTaskDTO.getSignUrl())
                            .set(PatrolTask::getAbnormalState, 1)
                            .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                            .eq(PatrolTask::getId, patrolTaskDTO.getId());
                }

            }
            patrolTaskMapper.update(new PatrolTask(), updateWrapper);
            // 提交任务如果需要审核则发送一条审核待办消息
            if (PatrolConstant.TASK_CHECK.equals(patrolTask.getAuditor())) {
                QueryWrapper<PatrolTaskOrganization> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(PatrolTaskOrganization::getTaskCode, patrolTask.getCode())
                        .eq(PatrolTaskOrganization::getDelFlag, CommonConstant.DEL_FLAG_0);
                List<PatrolTaskOrganization> organizations = patrolTaskOrganizationMapper.selectList(wrapper);
                List<String> orgCodes = organizations.stream().map(PatrolTaskOrganization::getOrgCode).collect(Collectors.toList());
                String userName = sysBaseApi.getUserNameByOrgCodeAndRoleCode(orgCodes, Arrays.asList(RoleConstant.FOREMAN));

                QueryWrapper<PatrolTaskUser> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(PatrolTaskUser::getTaskCode, patrolTask.getCode()).eq(PatrolTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0);
                List<PatrolTaskUser> taskUsers = patrolTaskUserMapper.selectList(queryWrapper);
                if (CollectionUtil.isEmpty(taskUsers)) {
                    return;
                }

                String[] userIds = taskUsers.stream().map(PatrolTaskUser::getUserId).toArray(String[]::new);
                List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                String userNames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));

                //构建消息模板
                HashMap<String, Object> map = new HashMap<>();
                map.put("code",patrolTaskDTO.getCode());
                map.put("patrolTaskName",patrolTaskDTO.getName());
                String station = patrolTaskStationMapper.getStationByTaskCode(patrolTaskDTO.getCode());
                map.put("patrolStation",station);
                map.put("patrolTaskTime",patrolTaskDTO.getStartTime().toString()+patrolTaskDTO.getEndTime().toString());
                map.put("patrolName", userNames);

                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setData(map);
                todoDTO.setProcessDefinitionName("巡视管理");
                todoDTO.setTaskName(patrolTask.getName() + "(待审核)");
                todoDTO.setBusinessKey(patrolTask.getId());
                todoDTO.setBusinessType(TodoBusinessTypeEnum.PATROL_AUDIT.getType());
                todoDTO.setCurrentUserName(userName);
                todoDTO.setTaskType(TodoTaskTypeEnum.PATROL.getType());
                todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
                todoDTO.setUrl(PatrolMessageUrlConstant.AUDIT_URL);
                todoDTO.setAppUrl(PatrolMessageUrlConstant.AUDIT_APP_URL);
                isTodoBaseAPI.createTodoTask(todoDTO);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void getPatrolTaskManualListAdd(PatrolTaskManualDTO patrolTaskManualDTO) {
        //保存任务信息
        PatrolTask patrolTask = new PatrolTask();
        patrolTask.setName(patrolTaskManualDTO.getName());
        patrolTask.setCode(PatrolCodeUtil.getTaskCode());
        patrolTask.setPatrolDate(patrolTaskManualDTO.getPatrolDate());
        patrolTask.setStatus(0);
        patrolTask.setSource(3);
        patrolTask.setDelFlag(0);
        patrolTask.setDiscardStatus(0);
        patrolTask.setRebuild(0);
        patrolTask.setOmitStatus(0);
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
            if (ObjectUtil.isNotNull(patrolStandard) && 1 == patrolStandard.getDeviceType()) {
                List<DeviceDTO> deviceList = ns.getDeviceList();
                if (CollUtil.isEmpty(deviceList)) {
                    throw new AiurtBootException("要指定设备才可以保存");
                } else {
                    //遍历设备单号
                    deviceList.stream().forEach(dv -> {
                        PatrolTaskDevice patrolTaskDevice = new PatrolTaskDevice();
                        patrolTaskDevice.setTaskId(taskId);//巡检任务id
                        patrolTaskDevice.setDelFlag(0);
                        patrolTaskDevice.setStatus(0);//单号状态
                        patrolTaskDevice.setPatrolNumber(PatrolCodeUtil.getBillCode());//巡检单号
                        patrolTaskDevice.setTaskStandardId(taskStandardId);//巡检任务标准关联表ID
                        patrolTaskDevice.setDeviceCode(dv.getCode());//设备code
                        Device device = patrolTaskDeviceMapper.getDevice(dv.getCode());
                        patrolTaskDevice.setLineCode(device.getLineCode());//线路code
                        patrolTaskDevice.setStationCode(device.getStationCode());//站点code
                        patrolTaskDevice.setPositionCode(device.getPositionCode());//位置code
                        patrolTaskDeviceMapper.insert(patrolTaskDevice);
                        patrolTaskDeviceService.copyItems(patrolTaskDevice);
                    });
                }
            } else {
                List<String> stationCodeList1 = patrolTaskManualDTO.getStationCodeList();
                stationCodeList1.stream().forEach(sc -> {
                    PatrolTaskDevice patrolTaskDevice = new PatrolTaskDevice();
                    patrolTaskDevice.setTaskId(taskId);
                    patrolTaskDevice.setDelFlag(0);
                    patrolTaskDevice.setStatus(0);//单号状态
                    patrolTaskDevice.setPatrolNumber(PatrolCodeUtil.getBillCode());
                    patrolTaskDevice.setTaskStandardId(taskStandardId);//巡检任务标准关联表ID
                    String lineCode = patrolTaskStationMapper.getLineStaionCode(sc);
                    patrolTaskDevice.setLineCode(lineCode);//线路code
                    patrolTaskDevice.setStationCode(sc);//站点code
                    patrolTaskDeviceMapper.insert(patrolTaskDevice);
                    patrolTaskDeviceService.copyItems(patrolTaskDevice);
                });
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
        // 更新漏巡任务待办消息
        isTodoBaseAPI.updateTodoTaskState(
                TodoBusinessTypeEnum.PATROL_OMIT.getType(),
                task.getId(),
                loginUser.getUsername(),
                CommonTodoStatus.DONE_STATUS_1
        );
        return patrolTaskMapper.updateById(task);
    }

    @Override
    public Page<PatrolTaskStandardDTO> getPatrolTaskManualDetail(Page<PatrolTaskStandardDTO> pageList, String id) {
        List<PatrolTaskStandardDTO> standardList = patrolTaskStandardMapper.getStandard(id);
        standardList.stream().forEach(e -> {
            PatrolStandard patrolStandard = patrolStandardMapper.selectById(e.getStandardId());
            if (patrolStandard.getDeviceType() == 1) {
                e.setSpecifyDevice(1);
            } else {
                e.setSpecifyDevice(0);
            }
            LambdaQueryWrapper<PatrolTaskDevice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PatrolTaskDevice::getTaskId, e.getTaskId()).eq(PatrolTaskDevice::getTaskStandardId, e.getTaskStandardId());
            List<PatrolTaskDevice> taskDeviceList = patrolTaskDeviceMapper.selectList(queryWrapper);
            List<DeviceDTO> dtoList = new ArrayList<>();
            taskDeviceList.stream().forEach(td -> {
                if (ObjectUtil.isNotNull(td.getDeviceCode())) {
                    DeviceDTO deviceDTO = patrolTaskDeviceMapper.getTaskStandardDevice(td.getDeviceCode());
                    String statusName = patrolTaskDeviceMapper.getStatusName(deviceDTO.getStatus());
                    deviceDTO.setStatusName(statusName);
                    if (ObjectUtil.isNotEmpty(deviceDTO.getPositionCode())) {
                        String positionDevice = patrolTaskDeviceMapper.getDevicePosition(deviceDTO.getPositionCode());
                        String position = deviceDTO.getPositionCodeName() + "/" + positionDevice;
                        deviceDTO.setPositionCodeName(position);
                    }
                    String majorName = patrolTaskDeviceMapper.getMajorName(deviceDTO.getMajorCode());
                    String sysName = patrolTaskDeviceMapper.getSysName(deviceDTO.getSystemCode());
                    deviceDTO.setMajorCodeName(majorName);
                    deviceDTO.setSystemCodeName(sysName);
                    dtoList.add(deviceDTO);
                } else {
                    e.setDeviceList(new ArrayList<>());
                }
            });
            e.setDeviceList(dtoList);
        });
        return pageList.setRecords(standardList);
    }

    @Override
    public List<PatrolUserInfoDTO> getAssignee(List<String> list) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录系统，请登录后操作！");
        }
        if (CollectionUtil.isEmpty(list)) {
            throw new AiurtBootException("任务编号的集合对象为空！");
        }

        // 获取当前登录人的部门权限
        List<CsUserDepartModel> departList = sysBaseApi.getDepartByUserId(loginUser.getId());
        List<String> loginUserOrgCodes = departList.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(loginUserOrgCodes)) {
            return Collections.emptyList();
        }
        boolean orgClose = GlobalThreadLocal.setDataFilter(false);
        List<String> orgCode = patrolTaskOrganizationMapper.getOrgCode(list.get(0));

        // 获取批量指派时的用户 需要相同的组织机构
        int size = list.size();
        if (size > 1) {
            for (int i = 1; i < size; i++) {
                List<String> code = patrolTaskOrganizationMapper.getOrgCode(list.get(i));
                boolean contains1 = orgCode.containsAll(code);
                boolean contains2 = code.containsAll(orgCode);
                if (contains1 && contains2) {
                    continue;
                } else {
                    throw new AiurtBootException("请选择组织机构一致的任务进行批量指派！");
                }
            }
        }
        GlobalThreadLocal.setDataFilter(orgClose);
        List<PatrolUserInfoDTO> userInfo = patrolTaskOrganizationMapper.getUserListByTaskCode(list.get(0));
        // 根据当前登录人部门权限过滤指派人员
        userInfo = userInfo.stream().filter(l -> loginUserOrgCodes.contains(l.getOrgCode())).collect(Collectors.toList());
        // 当前登录人的部门权限和任务的组织机构交集
        List<String> intersectOrg = CollectionUtil.intersection(loginUserOrgCodes, orgCode).stream().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(intersectOrg)) {
            return Collections.emptyList();
        }

        // 根据配置决定是否关联排班
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_SCHEDULING);
        boolean value = "1".equals(paramModel.getValue()) ? true : false;
        if (value) {
            // 获取今日当班的人员
            List<SysUserTeamDTO> todayOndutyDetail = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(orgCode)) {
                todayOndutyDetail = baseApi.getTodayOndutyDetailNoPage(intersectOrg, new Date());
            }
            if (CollectionUtil.isEmpty(todayOndutyDetail)) {
                return Collections.emptyList();
            }
            // 今日当班的人员的用户id
            List<String> todayUserId = todayOndutyDetail.stream().map(SysUserTeamDTO::getUserId).collect(Collectors.toList());
            // 根据今日当班人员过滤指派人员
            for (PatrolUserInfoDTO user : userInfo) {
                if (ObjectUtil.isNotEmpty(user.getUserId())) {
                    String separator = ",";
                    String[] ids = StrUtil.split(user.getUserId(), separator);
                    String[] names = StrUtil.split(user.getUserName(), separator);
                    List<String> userId = new LinkedList<>();
                    List<String> userName = new LinkedList<>();
                    for (int i = 0; i < ids.length; i++) {
                        if (todayUserId.contains(ids[i])) {
                            userId.add(ids[i]);
                            userName.add(names[i]);
                        }
                    }
                    user.setUserId(userId.stream().collect(Collectors.joining(separator)));
                    user.setUserName(userName.stream().collect(Collectors.joining(separator)));
                }
            }
        }
        // 再过滤掉人员为空的记录
        userInfo = userInfo.stream().filter(l -> StrUtil.isNotBlank(l.getUserId())).collect(Collectors.toList());
        return userInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String rebuildTask(PatrolRebuildDTO patrolRebuildDTO) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        String taskId = patrolRebuildDTO.getTaskId();
        QueryWrapper<PatrolTask> wrapper = new QueryWrapper<>();
        // 获取未作废、未处置、已漏检、未重新生成的任务
        wrapper.lambda().eq(PatrolTask::getId, taskId)
                .eq(PatrolTask::getOmitStatus, PatrolConstant.OMIT_STATUS)
                .eq(PatrolTask::getDisposeStatus, PatrolConstant.TASK_UNDISPOSE)
                .eq(PatrolTask::getDiscardStatus, PatrolConstant.TASK_UNDISCARD)
                .eq(PatrolTask::getRebuild, PatrolConstant.TASK_UNREBUILD);
        PatrolTask patrolTask = patrolTaskMapper.selectOne(wrapper);
        if (ObjectUtil.isEmpty(patrolTask)) {
            throw new AiurtBootException("未找到满足重新生成条件的任务！");
        }
        PatrolTask task = new PatrolTask();
        // 任务编号
        String taskCode = PatrolCodeUtil.getTaskCode();
        task.setCode(taskCode);
        // 任务名称
        task.setName(patrolTask.getName());
        // 作业类型
        task.setType(patrolTask.getType());
        // 任务方式-手工下发
        task.setSource(PatrolConstant.TASK_MANUAL);
        // 任务状态-待指派
        task.setStatus(PatrolConstant.TASK_INIT);
        // 任务是否需要审核
        task.setAuditor(patrolTask.getAuditor());
        //计划令编码
        task.setPlanOrderCode(patrolTask.getPlanOrderCode());
        //计划令图片
        task.setPlanOrderCodeUrl(patrolTask.getPlanOrderCodeUrl());
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

        // 更新漏巡任务待办消息
        isTodoBaseAPI.updateTodoTaskState(
                TodoBusinessTypeEnum.PATROL_OMIT.getType(),
                patrolTask.getId(),
                loginUser.getUsername(),
                CommonTodoStatus.DONE_STATUS_1
        );

        // 组织机构信息
        if (ObjectUtil.isEmpty(patrolRebuildDTO.getDeptCode())) {
            List<PatrolTaskOrganizationDTO> patrolTaskOrgList = patrolTaskOrganizationMapper.selectOrgByTaskCode(patrolTask.getCode());
            patrolTaskOrgList.stream().forEach(l -> {
                PatrolTaskOrganization organization = new PatrolTaskOrganization();
                organization.setTaskCode(taskCode);
                organization.setOrgCode(l.getOrgCode());
                patrolTaskOrganizationMapper.insert(organization);
            });
        } else {
            List<String> list = Arrays.asList(patrolRebuildDTO.getDeptCode());
            list.stream().forEach(l -> {
                PatrolTaskOrganization organization = new PatrolTaskOrganization();
                organization.setTaskCode(taskCode);
                organization.setOrgCode(l);
                patrolTaskOrganizationMapper.insert(organization);
            });
        }

        // 站所信息
        if (ObjectUtil.isEmpty(patrolRebuildDTO.getStationCode())) {
            List<PatrolTaskStationDTO> taskStationList = patrolTaskStationMapper.selectStationByTaskCode(patrolTask.getCode());
            taskStationList.stream().forEach(l -> {
                PatrolTaskStation station = new PatrolTaskStation();
                station.setTaskCode(taskCode);
                station.setStationCode(l.getStationCode());
                patrolTaskStationMapper.insert(station);
            });
        } else {
            List<String> list = Arrays.asList(patrolRebuildDTO.getStationCode());
            list.stream().forEach(l -> {
                PatrolTaskStation station = new PatrolTaskStation();
                station.setTaskCode(taskCode);
                station.setStationCode(l);
                patrolTaskStationMapper.insert(station);
            });
        }

        // 根据原任务ID获取巡检任务标准关联表信息
        QueryWrapper<PatrolTaskStandard> taskStandardWrapper = new QueryWrapper<>();
        taskStandardWrapper.lambda().eq(PatrolTaskStandard::getTaskId, taskId);
        List<PatrolTaskStandard> taskStandardList = patrolTaskStandardMapper.selectList(taskStandardWrapper);
        taskStandardList.stream().forEach(l -> {
            PatrolTaskStandard taskStandard = new PatrolTaskStandard();
            taskStandard.setTaskId(newTaskId);
            taskStandard.setStandardId(l.getStandardId());
            taskStandard.setStandardCode(l.getStandardCode());
            taskStandard.setProfessionCode(l.getProfessionCode());
            taskStandard.setSubsystemCode(l.getSubsystemCode());
            taskStandard.setDeviceTypeCode(l.getDeviceTypeCode());
            patrolTaskStandardMapper.insert(taskStandard);
            // 新任务标准ID
            String taskStandardId = taskStandard.getId();

            // 根据原任务ID和原任务标准关联表ID 获取原巡检任务设备关联表信息
            QueryWrapper<PatrolTaskDevice> taskDeviceWrapper = new QueryWrapper<>();
            taskDeviceWrapper.lambda().eq(PatrolTaskDevice::getTaskId, taskId)
                    .eq(PatrolTaskDevice::getTaskStandardId, l.getId());
            List<PatrolTaskDevice> taskDeviceList = patrolTaskDeviceMapper.selectList(taskDeviceWrapper);

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
                        String billCode = PatrolCodeUtil.getBillCode();
                        taskDevice.setPatrolNumber(billCode);
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

                        // 根据原任务设备表主键ID获取原工单检查项目内容列表
                        QueryWrapper<PatrolCheckResult> resultWrapper = new QueryWrapper<>();
                        resultWrapper.lambda().eq(PatrolCheckResult::getTaskDeviceId, d.getId());
                        List<PatrolCheckResult> resultList = patrolCheckResultMapper.selectList(resultWrapper);

                        // 新增对应工单检查项目内容
                        List<PatrolCheckResult> newResultList = new ArrayList<>();
                        Optional.ofNullable(resultList).orElseGet(Collections::emptyList).stream().forEach(
                                // result: PatrolCheckResult对象
                                result -> {
                                    PatrolCheckResult checkResult = new PatrolCheckResult();
                                    // 新的任务标准表ID
                                    checkResult.setTaskStandardId(taskStandardId);
                                    // 新的任务设备ID
                                    checkResult.setTaskDeviceId(taskDevice.getId());
                                    // 检查结果、字典结果值、文本填写结果、检查用户和备注为空
                                    checkResult.setCode(result.getCode())
                                            .setContent(result.getContent())
                                            .setQualityStandard(result.getQualityStandard())
                                            .setHierarchyType(result.getHierarchyType())
                                            .setOldId(result.getOldId())
                                            .setParentId(result.getParentId())
                                            .setOrder(result.getOrder())
                                            .setCheck(result.getCheck())
                                            .setInputType(result.getInputType())
                                            .setDictCode(result.getDictCode())
                                            .setRegular(result.getRegular())
                                            .setRequired(result.getRequired());
                                    newResultList.add(checkResult);
                                }
                        );
                        patrolCheckResultMapper.addResultList(newResultList);
                    }
            );
        });
        return taskCode;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void getPatrolTaskManualEdit(PatrolTaskManualDTO patrolTaskManualDTO) {
        //更新任务信息
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PatrolTask::getRemark, patrolTaskManualDTO.getRemark()).set(PatrolTask::getAuditor, patrolTaskManualDTO.getAuditor())
                .set(PatrolTask::getStartTime, patrolTaskManualDTO.getStartTime()).set(PatrolTask::getEndTime, patrolTaskManualDTO.getEndTime())
                .set(PatrolTask::getType, patrolTaskManualDTO.getType())
                .set(PatrolTask::getName, patrolTaskManualDTO.getName()).set(PatrolTask::getPatrolDate, patrolTaskManualDTO.getPatrolDate()).eq(PatrolTask::getId, patrolTaskManualDTO.getId());
        patrolTaskMapper.update(new PatrolTask(), updateWrapper);
        //删除、保存站点、组织
        saveOrgStation(patrolTaskManualDTO);
        //删除巡检任务标准表的信息
        List<PatrolTaskStandard> taskStandardList = patrolTaskStandardMapper.selectList(new LambdaQueryWrapper<PatrolTaskStandard>().eq(PatrolTaskStandard::getTaskId, patrolTaskManualDTO.getId()));
        if (CollUtil.isNotEmpty(taskStandardList)) {
            patrolTaskStandardMapper.deleteBatchIds(taskStandardList);
        }
        //删除单号
        List<PatrolTaskDevice> devices = patrolTaskDeviceMapper.selectList(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getTaskId, patrolTaskManualDTO.getId()));
        //删除检查项
        if (CollUtil.isNotEmpty(devices)) {
            devices.stream().forEach(d -> {
                List<PatrolCheckResult> patrolCheckResult = patrolCheckResultMapper.selectList(new LambdaQueryWrapper<PatrolCheckResult>().eq(PatrolCheckResult::getTaskDeviceId, d.getId()));
                if (ObjectUtil.isNotEmpty(patrolCheckResult)) {
                    patrolCheckResultMapper.deleteBatchIds(patrolCheckResult);
                }
            });
            patrolTaskDeviceMapper.deleteBatchIds(devices);
        }
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
            if (ObjectUtil.isNotNull(patrolStandard) && 1 == patrolStandard.getDeviceType()) {
                List<DeviceDTO> deviceList = ns.getDeviceList();
                //遍历设备单号
                deviceList.stream().forEach(dv -> {
                    PatrolTaskDevice patrolTaskDevice = new PatrolTaskDevice();
                    patrolTaskDevice.setTaskId(taskId);//巡检任务id
                    patrolTaskDevice.setDelFlag(0);
                    patrolTaskDevice.setStatus(0);//单号状态
                    patrolTaskDevice.setPatrolNumber(PatrolCodeUtil.getBillCode());
                    patrolTaskDevice.setTaskStandardId(taskStandardId);//巡检任务标准关联表ID
                    patrolTaskDevice.setDeviceCode(dv.getCode());//设备code
                    Device device = patrolTaskDeviceMapper.getDevice(dv.getCode());
                    patrolTaskDevice.setLineCode(device.getLineCode());//线路code
                    patrolTaskDevice.setStationCode(device.getStationCode());//站点code
                    patrolTaskDevice.setPositionCode(device.getPositionCode());//位置code
                    patrolTaskDeviceMapper.insert(patrolTaskDevice);
                    patrolTaskDeviceService.copyItems(patrolTaskDevice);
                });
            } else {
                List<String> stationCodeList1 = patrolTaskManualDTO.getStationCodeList();
                stationCodeList1.stream().forEach(sc -> {
                    PatrolTaskDevice patrolTaskDevice = new PatrolTaskDevice();
                    patrolTaskDevice.setTaskId(taskId);
                    patrolTaskDevice.setDelFlag(0);
                    patrolTaskDevice.setStatus(0);//单号状态
                    patrolTaskDevice.setPatrolNumber(PatrolCodeUtil.getBillCode());
                    patrolTaskDevice.setTaskStandardId(taskStandardId);//巡检任务标准关联表ID
                    String lineCode = patrolTaskStationMapper.getLineStaionCode(sc);
                    patrolTaskDevice.setLineCode(lineCode);//线路code
                    patrolTaskDevice.setStationCode(sc);//站点code
                    patrolTaskDeviceMapper.insert(patrolTaskDevice);
                    patrolTaskDeviceService.copyItems(patrolTaskDevice);
                });
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrgStation(PatrolTaskManualDTO patrolTaskManualDTO) {
        //先删除
        List<PatrolTaskOrganization> list = patrolTaskOrganizationMapper.selectList(new LambdaQueryWrapper<PatrolTaskOrganization>().eq(PatrolTaskOrganization::getTaskCode, patrolTaskManualDTO.getCode()));
        if (CollUtil.isNotEmpty(list)) {
            patrolTaskOrganizationMapper.deleteBatchIds(list);
        }
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
        List<PatrolTaskStation> stationList = patrolTaskStationMapper.selectList(new LambdaQueryWrapper<PatrolTaskStation>().eq(PatrolTaskStation::getTaskCode, patrolTaskManualDTO.getCode()));
        if (CollUtil.isNotEmpty(stationList)) {
            patrolTaskStationMapper.deleteBatchIds(stationList);
        }
        //后保存站点信息
        List<String> stationCodeList = patrolTaskManualDTO.getStationCodeList();
        stationCodeList.stream().forEach(s -> {
            PatrolTaskStation station = new PatrolTaskStation();
            station.setDelFlag(0);
            station.setStationCode(s);
            station.setTaskCode(taskCode);
            patrolTaskStationMapper.insert(station);
        });
    }

    @Override
    public String getLineCode(String stationCode) {
        return patrolTaskMapper.getLineCode(stationCode);
    }

    @Override
    public PatrolTaskDTO getDetail(String id) {
        PatrolTaskDTO e = patrolTaskMapper.getDetail(id);
        String userName = patrolTaskMapper.getUserName(e.getBackId());
        List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(e.getId());
        String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).distinct().collect(Collectors.joining("；"));
        String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).distinct().collect(Collectors.joining("；"));
        List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getCode());
        e.setOrganizationName(manager.translateOrg(orgCodes));
        List<StationDTO> stationName = patrolTaskMapper.getStationName(e.getCode());
        e.setStationName(manager.translateStation(stationName));
        e.setEndUserName(e.getEndUserName() == null ? "-" : e.getEndUserName());
        e.setSubmitTime(e.getSubmitTime() == null ? "-" : e.getSubmitTime());
        e.setPeriod(e.getPeriod() == null ? "-" : e.getPeriod());
        e.setSysName(sysName);
        e.setMajorName(majorName);
        e.setOrgCodeList(orgCodes);
        e.setPatrolUserName(manager.spliceUsername(e.getCode()));
        e.setPatrolReturnUserName(userName == null ? "-" : userName);
        List<PatrolTaskDevice> taskDeviceList = patrolTaskDeviceMapper.selectList(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getTaskId, id));
        List<PatrolAccompany> accompanyList = new ArrayList<>();
        for (PatrolTaskDevice patrolTaskDevice : taskDeviceList) {
            List<PatrolAccompany> patrolAccompanies = accompanyMapper.selectList(new LambdaQueryWrapper<PatrolAccompany>().eq(PatrolAccompany::getTaskDeviceCode, patrolTaskDevice.getPatrolNumber()));
            if (CollUtil.isNotEmpty(patrolAccompanies)) {
                accompanyList.addAll(patrolAccompanies);
            }
        }
        if (CollUtil.isNotEmpty(accompanyList)) {
            accompanyList = accompanyList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PatrolAccompany::getUserId))), ArrayList::new));
            String peerPeople = accompanyList.stream().map(PatrolAccompany::getUsername).collect(Collectors.joining(";"));
            e.setPeerPeople(peerPeople);
        }
        return e;
    }

    /**
     * 巡视消息发送
     *
     * @param messageDTO
     * @param usernames
     * @param username
     * @param patrolMessageDTO
     */
    private void sendMessage(MessageDTO messageDTO, String usernames, String username, PatrolMessageDTO patrolMessageDTO) {
        //发送通知
        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(messageDTO.getData())) {
            map.putAll(messageDTO.getData());
        }
        map.put("code",patrolMessageDTO.getCode());
        map.put("patrolTaskName",patrolMessageDTO.getName());
        String station = patrolTaskStationMapper.getStationByTaskCode(patrolMessageDTO.getCode());
        map.put("patrolStation",station);
        map.put("patrolTaskTime",patrolMessageDTO.getStartTime().toString()+patrolMessageDTO.getEndTime().toString());
        if (StrUtil.isNotEmpty(usernames)) {
            map.put("patrolName", usernames);
        } else {
            map.put("patrolName",username);
        }
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, patrolMessageDTO.getId());
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, patrolMessageDTO.getBusType());
        messageDTO.setData(map);
        messageDTO.setType(patrolMessageDTO.getMessageType());
        messageDTO.setTemplateCode(patrolMessageDTO.getTemplateCode());
        messageDTO.setMsgAbstract(patrolMessageDTO.getMsgAbstract());
        messageDTO.setPublishingContent(patrolMessageDTO.getPublishingContent());
        sysBaseApi.sendTemplateMessage(messageDTO);
    }
}
