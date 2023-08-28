package com.aiurt.boot.task.service.impl;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.*;
import com.aiurt.boot.manager.PatrolManager;
import com.aiurt.boot.plan.entity.PatrolPlan;
import com.aiurt.boot.plan.mapper.PatrolPlanMapper;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.statistics.dto.IndexStationDTO;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.param.CustomCellMergeHandler;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.aiurt.boot.task.service.*;
import com.aiurt.boot.utils.PatrolCodeUtil;
import com.aiurt.boot.utils.PdfUtil;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.*;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Slf4j
@Service
public class PatrolTaskServiceImpl extends ServiceImpl<PatrolTaskMapper, PatrolTask> implements IPatrolTaskService {

    @Value("${jeecg.path.upload:/opt/upFiles}")
    private String path;
    @Value("${jeecg.minio.bucketName}")
    private String bucketName;

    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private IPatrolTaskDeviceService patrolTaskDeviceService;
    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;
    @Autowired
    private IPatrolTaskUserService patrolTaskUserService;
    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;
    @Autowired
    private IPatrolCheckResultService patrolCheckResultService;
    @Autowired
    private PatrolCheckResultMapper patrolCheckResultMapper;
    @Autowired
    private IPatrolTaskOrganizationService patrolTaskOrganizationService;
    @Autowired
    private PatrolTaskOrganizationMapper patrolTaskOrganizationMapper;
    @Autowired
    private IPatrolTaskStationService patrolTaskStationService;
    @Autowired
    private PatrolTaskStationMapper patrolTaskStationMapper;
    @Autowired
    private IPatrolTaskStandardService patrolTaskStandardService;
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
    private PatrolSamplePersonMapper patrolSamplePersonMapper;
    @Autowired
    private ISysParamAPI iSysParamAPI;


    @Override
    public IPage<PatrolTaskParam> getTaskList(Page<PatrolTaskParam> page, PatrolTaskParam patrolTaskParam) {
        // 数据权限过滤
//        List<String> taskCode = new ArrayList<>();
//        try {
//            taskCode = this.taskDataPermissionFilter();
//        } catch (Exception e) {
//            return page;
//        }

        IPage<PatrolTaskParam> taskPage = page;
        if (CollUtil.isNotEmpty(patrolTaskParam.getSelections())){
            // 只根据id查询
            List<PatrolTaskParam> taskList = patrolTaskMapper.getTaskListByIds(patrolTaskParam.getSelections());
            // 因为根据id查询只用于导出，就不设置页数之类的了
            taskPage.setRecords(taskList);
        }else{
            taskPage = patrolTaskMapper.getTaskList(page, patrolTaskParam);
        }

        // 转化巡视时长
        taskPage.getRecords().forEach(task->{
            task.setDurationString(TimeUtil.translateTime(task.getDuration()));
            // 实际巡视时长，只有wifi连接时间和提交时间都不为空时才有
            if (ArrayUtil.isAllNotEmpty(task.getSubmitTime(), task.getWifiConnectTime())){
                int actualDuration = (int) DateUtil.between(task.getWifiConnectTime(), task.getSubmitTime(), DateUnit.SECOND);
                task.setActualDurationString(TimeUtil.translateTime(actualDuration));
            }

            if (task.getMacStatus() != null) {
                String statusName = sysBaseApi.translateDict("mac_status", task.getMacStatus().toString());
                task.setMacStatusName(statusName);
            }
        });

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("patrol_task-%d").build();
        ExecutorService patrolTask = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
        List<Future<PatrolTaskParam>> futureList = new ArrayList<>();

        // 禁用数据权限过滤-start
        boolean filter = GlobalThreadLocal.setDataFilter(false);
        taskPage.getRecords().stream().forEach(l -> {
            //判断是否需要打印
            l.setHavePrint(patrolTaskParam.getHavePrint());

            Future<PatrolTaskParam> submit = patrolTask.submit(new PatrolTaskThreadService(l, patrolTaskOrganizationMapper, patrolTaskStationMapper,
                    patrolCheckResultMapper, patrolTaskMapper, patrolTaskDeviceMapper, patrolTaskUserMapper, patrolTaskDeviceService, sysBaseApi));
            futureList.add(submit);

        });
        // 确认每个线程都执行完成
        for (Future<PatrolTaskParam> fut : futureList) {
            try {
                fut.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        try {
            patrolTask.shutdown();
            // (所有的任务都结束的时候，返回TRUE)
            if (!patrolTask.awaitTermination(5 * 1000, TimeUnit.MILLISECONDS)) {
                // 5s超时的时候向线程池中所有的线程发出中断(interrupted)。
                patrolTask.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
            log.error("awaitTermination interrupted:{}", e);
            patrolTask.shutdownNow();
        }
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
        // 抽检人
        String samplePersonName = patrolTaskDeviceMapper.getSamplePersonNameByTaskId(taskParam.getId());

        taskParam.setDepartInfo(organizationInfo);
        taskParam.setStationInfo(stationInfo);
        taskParam.setUserInfo(userList);
        taskParam.setMajorInfo(majorInfo);
        taskParam.setSubsystemInfo(subsystemInfo);
        taskParam.setAccompanyName(accompanyUserName);
        taskParam.setSamplePersonName(samplePersonName);
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
        // userId:username
        Map<String, String> userInfoMap = new HashMap<>(16);
        // 批量指派用户信息
        List<PatrolTaskUser> taskUsers = new ArrayList<>();

        for (Map.Entry<String, List<PatrolAppointUserDTO>> entry : map.entrySet()) {
            List<PatrolAppointUserDTO> list = entry.getValue();
            if (CollUtil.isEmpty(list)) {
                throw new AiurtBootException("编号为：" + entry.getKey() + "的任务未指定用户！");
            }
            list.forEach(user -> {
                if (ObjectUtil.isEmpty(user) || ObjectUtil.isEmpty(user.getUserId())) {
                    return;
                }
                if (ObjectUtil.isEmpty(user.getUserName())) {
                    String username = userInfoMap.get(user.getUserId());
                    if (StrUtil.isEmpty(username)) {
                        username = patrolTaskUserMapper.getUsername(user.getUserId());
                        userInfoMap.put(user.getUserId(), username);
                    }
                    user.setUserName(username);
                }
                // 指派用户信息
                PatrolTaskUser taskUser = new PatrolTaskUser();
                taskUser.setTaskCode(entry.getKey());
                taskUser.setUserId(user.getUserId());
                taskUser.setUserName(user.getUserName());
                taskUsers.add(taskUser);
            });
        }

        Set<String> codeSet = map.keySet();
        codeSet.remove(null);
        if (CollUtil.isNotEmpty(codeSet)) {
            List<PatrolTask> tasks = this.lambdaQuery()
                    .eq(PatrolTask::getDelFlag, 0)
                    .eq(PatrolTask::getDiscardStatus, PatrolConstant.TASK_UNDISCARD)
                    .and(status -> status.eq(PatrolTask::getStatus, PatrolConstant.TASK_INIT)
                            .or()
                            .eq(PatrolTask::getStatus, PatrolConstant.TASK_RETURNED))
                    .in(PatrolTask::getCode, codeSet)
                    .list();
            // 指派之前查询是否指派过巡检用户，存在则删除掉然后再添加(主要是重新生成接口)
            QueryWrapper<PatrolTaskUser> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.lambda().in(PatrolTaskUser::getTaskCode, codeSet);
            patrolTaskUserMapper.delete(userQueryWrapper);
            // 保存指派用户
            patrolTaskUserService.saveBatch(taskUsers);

            // 更新巡视任务状态
            tasks.forEach(task -> {
                // 作业类型
                task.setType(patrolAppointInfoDTO.getType());
                // 计划令编号和图片地址
                task.setPlanOrderCode(patrolAppointInfoDTO.getPlanOrderCode());
                task.setPlanOrderCodeUrl(patrolAppointInfoDTO.getPlanOrderCodeUrl());
                if (ObjectUtil.isEmpty(task.getSource())) {
                    task.setSource(PatrolConstant.TASK_COMMON);
                }
                // 更新检查开始结束时间
                task.setStartTime(patrolAppointInfoDTO.getStartTime());
                task.setEndTime(patrolAppointInfoDTO.getEndTime());
                // 任务状态
                task.setStatus(PatrolConstant.TASK_CONFIRM);
                // 记录指派人
                task.setAssignId(loginUser.getId());
            });
            this.updateBatchById(tasks);
        }

//        for (Map.Entry<String, List<PatrolAppointUserDTO>> listEntry : map.entrySet()) {
//            List<PatrolAppointUserDTO> list = listEntry.getValue();
//            if (CollUtil.isEmpty(list)) {
//                throw new AiurtBootException("未指定用户，请指定用户！");
//            }
//            // 根据任务code查找未指派的任务
//            QueryWrapper<PatrolTask> taskWrapper = new QueryWrapper<>();
//            taskWrapper.lambda()
//                    .eq(PatrolTask::getCode, listEntry.getKey())
//                    .eq(PatrolTask::getDiscardStatus, PatrolConstant.TASK_UNDISCARD)
//                    .and(status -> status.eq(PatrolTask::getStatus, PatrolConstant.TASK_INIT)
//                            .or()
//                            .eq(PatrolTask::getStatus, PatrolConstant.TASK_RETURNED));
//            PatrolTask patrolTask = patrolTaskMapper.selectOne(taskWrapper);
//
//            if (ObjectUtil.isNotEmpty(patrolTask)) {
//                // 指派之前查询是否指派过巡检用户，存在则删除掉然后再添加(主要是重新生成接口)
//                QueryWrapper<PatrolTaskUser> taskUserWrapper = new QueryWrapper<>();
//                taskUserWrapper.lambda().eq(PatrolTaskUser::getTaskCode, listEntry.getKey());
//                List<PatrolTaskUser> taskUserList = patrolTaskUserMapper.selectList(taskUserWrapper);
//                if (CollectionUtil.isNotEmpty(taskUserList)) {
//                    List<String> collect = taskUserList.stream().map(l -> l.getId()).collect(Collectors.toList());
//                    patrolTaskUserMapper.deleteBatchIds(collect);
//                }
//
//                // 标记是否插入指派的用户信息
//                AtomicInteger insert = new AtomicInteger();
//                Optional.ofNullable(list).orElseGet(Collections::emptyList).stream().forEach(l -> {
//                    if (ObjectUtil.isEmpty(l) || ObjectUtil.isEmpty(l.getUserId())) {
//                        return;
//                    }
//                    if (ObjectUtil.isEmpty(l.getUserName())) {
//                        l.setUserName(patrolTaskUserMapper.getUsername(l.getUserId()));
//                    }
//                    // 指派用户信息
//                    PatrolTaskUser taskUser = new PatrolTaskUser();
//                    taskUser.setTaskCode(listEntry.getKey());
//                    taskUser.setUserId(l.getUserId());
//                    taskUser.setUserName(l.getUserName());
//
//                    // 添加指派用户
//                    insert.addAndGet(patrolTaskUserMapper.insert(taskUser));
//                });
//                // 若插入指派的人员后则更新任务状态
//                if (insert.get() > 0) {
//                    PatrolTask task = new PatrolTask();
//                    // 作业类型
//                    task.setType(patrolAppointInfoDTO.getType());
//                    // 计划令编号和图片地址
//                    task.setPlanOrderCode(patrolAppointInfoDTO.getPlanOrderCode());
//                    task.setPlanOrderCodeUrl(patrolAppointInfoDTO.getPlanOrderCodeUrl());
//                    if (ObjectUtil.isEmpty(patrolTask.getSource())) {
//                        task.setSource(PatrolConstant.TASK_COMMON);
//                    }
//                    // 更新检查开始结束时间
//                    task.setStartTime(patrolAppointInfoDTO.getStartTime());
//                    task.setEndTime(patrolAppointInfoDTO.getEndTime());
//                    // 任务状态
//                    task.setStatus(PatrolConstant.TASK_CONFIRM);
//                    // 记录指派人
//                    task.setAssignId(loginUser.getId());
//                    // 更改任务状态为待确认
//                    patrolTaskMapper.update(task, taskWrapper);
//                    count.getAndIncrement();
//                }
//            }
//        }
        // 发送消息
//        this.sendMessagePC(map);
        // 用一个线程发消息
        AsyncThreadPoolExecutorUtil executor = AsyncThreadPoolExecutorUtil.getExecutor();
        executor.submitTask(() -> {
            this.sendMessagePC(map);
            return "Send Message PC!";
        });
        return codeSet.size();
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
                PatrolTask patrolTask = list.stream().filter(l -> code.equals(l.getCode())).findFirst().orElse(null);
                List<PatrolAppointUserDTO> users = map.get(code);
                String[] userIds = users.stream().map(PatrolAppointUserDTO::getUserId).toArray(String[]::new);
                List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                String userNames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
                String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                if (ObjectUtil.isEmpty(patrolTask) || CollectionUtil.isEmpty(loginUsers)) {
                    continue;
                }

                LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
                //发送通知
                try {
                    MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(),userNames, "巡视任务-确认接收" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
                    PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
                    BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_ASSIGN.getType());
                    messageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("新的巡视任务");
                    messageDTO.setPublishingContent("接收到新的巡视任务，请尽快确认");
                    //响铃
                    messageDTO.setIsRingBell(true);
                    sendMessage(messageDTO,realNames,null,patrolMessageDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        try {
            MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(),userNames, "巡视任务-确认接收" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
            PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
            BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_ASSIGN.getType());
            messageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
            messageDTO.setMsgAbstract("新的巡视任务");
            messageDTO.setPublishingContent("接收到新的巡视任务，请尽快确认");
            String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
            //响铃
            messageDTO.setIsRingBell(true);
            sendMessage(messageDTO,realNames,null,patrolMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送待办消息
     *
     * @param patrolTask
     */
    private void sendWaitingMessage(PatrolTask patrolTask, TodoDTO todoDTO) {
        QueryWrapper<PatrolTaskUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PatrolTaskUser::getTaskCode, patrolTask.getCode()).eq(PatrolTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<PatrolTaskUser> taskUsers = patrolTaskUserMapper.selectList(queryWrapper);

        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(todoDTO.getData())) {
            map.putAll(todoDTO.getData());
        }
        map.put("code",patrolTask.getCode());
        map.put("patrolTaskName",patrolTask.getName());
        List<String> station = patrolTaskStationMapper.getStationByTaskCode(patrolTask.getCode());
        map.put("patrolStation",CollUtil.join(station,","));
        if (patrolTask.getPatrolDate() != null) {
            String patrolDate = DateUtil.format(patrolTask.getPatrolDate(), "yyyy-MM-dd");
            map.put("patrolTaskTime",patrolDate);
        }else {
            String s1 = DateUtil.format(patrolTask.getStartDate(), "yyyy-MM-dd");
            String s2 = DateUtil.format(patrolTask.getEndDate(), "yyyy-MM-dd");
            map.put("patrolTaskTime",s1+"-"+s2);
        }
        if (CollectionUtil.isNotEmpty(taskUsers)) {
            String[] userIds = taskUsers.stream().map(PatrolTaskUser::getUserId).toArray(String[]::new);
            List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
            String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
            String userNames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
            map.put("patrolName", realNames);
            todoDTO.setCurrentUserName(userNames);
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");

        todoDTO.setData(map);
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_MESSAGE_PROCESS);
        todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        todoDTO.setProcessDefinitionName("巡视管理");
        todoDTO.setTaskName(todoDTO.getTitle());
        todoDTO.setBusinessKey(patrolTask.getId());
        todoDTO.setBusinessType(TodoBusinessTypeEnum.PATROL_EXECUTE.getType());
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
        try {
            MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(),userNames, "巡视任务-审核驳回" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
            PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
            BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put("rejectReason",patrolTask.getRejectReason());
            messageDTO.setData(map);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            /*patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_AUDIT.getType());
            messageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE_REJECT);
            messageDTO.setMsgAbstract("巡视任务审核驳回");
            messageDTO.setPublishingContent("巡视任务审核驳回，请重新处理");
            String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
            sendMessage(messageDTO,realNames,null,patrolMessageDTO);*/

            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setTitle("巡视任务-审核驳回"+DateUtil.today());
            todoDTO.setMsgAbstract("巡视任务审核驳回");
            todoDTO.setPublishingContent("巡视任务审核驳回，请重新处理");
            todoDTO.setData(map);
            todoDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE_REJECT);
            this.sendWaitingMessage(patrolTask,todoDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 任务审核通过发送消息给巡视人
     *
     * @param id
     */
    private void sendAuditPassMessage(String id, LoginUser loginUser) {
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
        try {
            MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(),userNames, "巡视任务-审核通过" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
            PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
            BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_AUDIT.getType());
            messageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
            messageDTO.setMsgAbstract("巡视任务审核");
            messageDTO.setPublishingContent("巡视任务审核通过");
            String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
            sendMessage(messageDTO,realNames,null,patrolMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            queryWrapper.set(PatrolTask::getStatus, PatrolConstant.TASK_BACK)
                    .set(PatrolTask::getRejectReason, backReason)
                    .set(PatrolTask::getRemark, remark).eq(PatrolTask::getId, id);
            this.update(queryWrapper);
            // 审核不通过则给任务的巡视人发送消息
            this.sendAuditNoPassMessage(id, loginUser);
            return Result.OK("不通过");
        } else {
            queryWrapper.set(PatrolTask::getStatus, PatrolConstant.TASK_COMPLETE).set(PatrolTask::getAuditorRemark, remark).set(PatrolTask::getAuditorTime, new Date()).eq(PatrolTask::getId, id);
            this.update(queryWrapper);
            this.sendAuditPassMessage(id, loginUser);
            return Result.OK("通过成功");
        }
    }
    @Value("${support.path.exportPatrolPath}")
    private String exportPath;
    @Autowired
    private ArchiveUtils archiveUtils;
    @Override
    public void archPatrol(PatrolTaskParam patrolTask, String token, String finalArchiveUserId, String refileFolderId, String username, String sectId) {
        try {
            SXSSFWorkbook archiveFault = new SXSSFWorkbook();
            // TODO 后期修改 导出Excel表 ExcelUtils.createArchivePatrol(patrolTask);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Date date = new Date();
            Date submitTime = patrolTask.getSubmitTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = patrolTask.getName() + sdf.format(submitTime);
            String path = exportPath + fileName + ".xlsx";
            FileOutputStream fos = new FileOutputStream(path);
            archiveFault.write(os);
            fos.write(os.toByteArray());
            os.close();
            fos.close();
            PdfUtil.excel2pdf(path);

            //传入档案系统
            //创建文件夹
            String foldername = fileName + "_" + date.getTime();
            String refileFolderIdNew = archiveUtils.createFolder(token, refileFolderId, foldername);
            //上传文件
            String fileType = "pdf";
            File file = new File(exportPath + fileName + "." + fileType);
            Long size = file.length();
            InputStream in = new FileInputStream(file);
            JSONObject res = archiveUtils.upload(token, refileFolderIdNew, fileName + "." + fileType, size, fileType, in);
            String fileId = res.getString("fileId");
            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("fileId", fileId);
            fileInfo.put("operateType", "upload");
            ArrayList<Object> fileList = new ArrayList<>();
            fileList.add(fileInfo);
            Map values = new HashMap();
            // TODO 后期修改
//            values.put("archiver", archiveUserId);
//            values.put("username", realname);
//            values.put("duration", patrolTask.getSecertduration());
//            values.put("secert", patrolTask.getSecert());
//            values.put("secertduration", patrolTask.getSecertduration());
            values.put("name", fileName);
            values.put("fileList", fileList);
            values.put("number", values.get("number"));
            values.put("refileFolderId", refileFolderIdNew);
            values.put("sectid", sectId);
            Map result = archiveUtils.arch(values, token);
            Map<String, String> obj = JSON.parseObject((String) result.get("obj"), new TypeReference<HashMap<String, String>>() {
            });
            if (result.get("result").toString() == "true" && "新增".equals(obj.get("rs"))) {
                UpdateWrapper<PatrolTask> uwrapper = new UpdateWrapper<>();
                uwrapper.eq("id", patrolTask.getId()).set("ecm_status", 1);
                this.update(uwrapper);
            }


        } catch (Exception e) {
            e.printStackTrace();
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
//        try {
//            List<String> taskCodes = this.taskDataPermissionFilter();
//            patrolTaskDTO.setTaskCodes(taskCodes);
//        } catch (AiurtBootException e) {
//            return pageList;
//        }

        // 根据配置进行排序
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.APP_PATROL_TASK_POOL_SORT);
        boolean b = ObjectUtil.isNotEmpty(sysParamModel) && "1".equals(sysParamModel.getValue());
        List<PatrolTaskDTO> taskList = patrolTaskMapper.getPatrolTaskPoolList(pageList, patrolTaskDTO, b);
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("app_patrolTaskPool-%d").build();
        ExecutorService patrolTask = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
        List<Future<PatrolTaskDTO>> futureList = new ArrayList<>();
        taskList.stream().forEach(e -> {
            Future<PatrolTaskDTO> submit = patrolTask.submit(new AppPatrolTaskPoolThreadService(e,patrolTaskMapper,patrolTaskStandardMapper,manager,sysBaseApi));
            futureList.add(submit);
        });
            // 确认每个线程都执行完成
        for (Future<PatrolTaskDTO> fut : futureList) {
            try {
                fut.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        try {
            patrolTask.shutdown();
            // (所有的任务都结束的时候，返回TRUE)
            if (!patrolTask.awaitTermination(5 * 1000, TimeUnit.MILLISECONDS)) {
                // 5s超时的时候向线程池中所有的线程发出中断(interrupted)。
                patrolTask.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
            log.error("awaitTermination interrupted:{}", e);
            patrolTask.shutdownNow();
        }
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
//        try {
//            List<String> taskCodes = this.taskDataPermissionFilter();
//            patrolTaskDTO.setTaskCodes(taskCodes);
//        } catch (AiurtBootException e) {
//            return pageList;
//        }
        List<PatrolTaskDTO> taskList = patrolTaskMapper.getPatrolTaskList(pageList, patrolTaskDTO);
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("app_patrolTask-%d").build();
        ExecutorService patrolTask = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
        List<Future<PatrolTaskDTO>> futureList = new ArrayList<>();
        taskList.stream().forEach(e -> {
            Future<PatrolTaskDTO> submit = patrolTask.submit(new AppPatrolTaskThreadService(e,patrolTaskMapper,patrolTaskStandardMapper,manager,patrolTaskDeviceMapper,accompanyMapper,patrolSamplePersonMapper,sysBaseApi));
            futureList.add(submit);
        });
        // 确认每个线程都执行完成
        for (Future<PatrolTaskDTO> fut : futureList) {
            try {
                fut.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        try {
            patrolTask.shutdown();
            // (所有的任务都结束的时候，返回TRUE)
            if (!patrolTask.awaitTermination(5 * 1000, TimeUnit.MILLISECONDS)) {
                // 5s超时的时候向线程池中所有的线程发出中断(interrupted)。
                patrolTask.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
            log.error("awaitTermination interrupted:{}", e);
            patrolTask.shutdownNow();
        }
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
            updateWrapper.set(PatrolTask::getStatus, 4)
                    .set(PatrolTask::getBeginTime, new Date())
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
            try {
                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setTitle("巡视任务-确认接收"+DateUtil.today());
                todoDTO.setMsgAbstract("巡视任务接收");
                todoDTO.setPublishingContent("接收巡视任务指派，请在巡视任务计划执行日期开展巡视工作");
                todoDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
                this.sendWaitingMessage(patrolTask,todoDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //确认：将待确认改为执行中
        if (PatrolConstant.TASK_CONFIRM.equals(patrolTaskDTO.getStatus())) {
            if (manager.checkTaskUser(patrolTask.getCode()) == false && !admin) {
                throw new AiurtBootException("只有该任务的巡检人才可以确认");
            }
            updateWrapper.set(PatrolTask::getStatus, 4).set(PatrolTask::getBeginTime, new Date()).eq(PatrolTask::getId, patrolTaskDTO.getId());
            update(updateWrapper);
            // 确认后发送待办消息
            try {
                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setTitle("巡视任务接收"+DateUtil.today());
                todoDTO.setMsgAbstract("巡视任务接收");
                todoDTO.setPublishingContent("接收巡视任务指派，请在巡视任务计划执行日期开展巡视工作");
                todoDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
                this.sendWaitingMessage(patrolTask,todoDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                try {
                    MessageDTO messageDTO = new MessageDTO(sysUser.getUsername(),user.getUsername(), "巡视任务退回" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
                    PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
                    BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
                    //构建消息模板
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("backReason",patrolTaskDTO.getBackReason());
                    messageDTO.setData(map);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_ASSIGN.getType());
                    messageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE_RETURN);
                    messageDTO.setMsgAbstract("巡视任务退回");
                    messageDTO.setPublishingContent("巡视任务退回，请重新安排");
                    // 巡检用户
                    QueryWrapper<PatrolTaskUser> userQueryWrapper = new QueryWrapper<>();
                    userQueryWrapper.lambda().eq(PatrolTaskUser::getTaskCode, patrolTask.getCode());
                    List<PatrolTaskUser> userList = patrolTaskUserMapper.selectList(userQueryWrapper);
                    List<String> list = userList.stream().map(PatrolTaskUser::getUserName).collect(Collectors.toList());
                    sendMessage(messageDTO,CollUtil.join(list,","),null,patrolMessageDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
        try {
            MessageDTO messageDTO = new MessageDTO(sysUser.getUsername(),userName, "巡视任务退回后" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
            PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
            BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put("backReason",patrolTask.getBackReason());
            messageDTO.setData(map);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_ASSIGN.getType());
            messageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE_RETURN);
            messageDTO.setMsgAbstract("巡视任务退回");
            messageDTO.setPublishingContent("巡视任务退回，请重新安排");
            sendMessage(messageDTO,null,sysUser.getRealname(),patrolMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//        // 数据权限过滤
//        List<String> taskCode = new ArrayList<>();
//        try {
//            taskCode = this.taskDataPermissionFilter();
//            patrolTaskDTO.setTaskCodes(taskCode);
//        } catch (Exception e) {
//            return pageList;
//        }
        List<PatrolTaskDTO> taskDTOList = patrolTaskMapper.getPatrolTaskManualList(pageList, patrolTaskDTO);
        taskDTOList.stream().forEach(e -> {
            String userName = patrolTaskMapper.getUserName(e.getBackId());
            List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(e.getId());
            String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).distinct().collect(Collectors.joining("；"));
            String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).distinct().collect(Collectors.joining("；"));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getCode());
            List<GeneralReturn> orgCodeName = patrolTaskMapper.getOrgCodeName(orgCodes);
            e.setOrganizationName(manager.translateOrg(orgCodes));
            List<String> stationCodeList = patrolTaskMapper.getStationCode(e.getCode());
            List<StationDTO> stationName = patrolTaskMapper.getStationName(e.getCode());
            e.setStationName(manager.translateStation(stationName));
            e.setSysName(sysName);
            e.setOrgCodes(orgCodeName);
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
        int updateById = 0;
        if (PatrolConstant.AUDIT_NOPASS.equals(auditStatus)) {
            if (StrUtil.isEmpty(auditReason)) {
                throw new AiurtBootException("审核不通过原因不能为空！");
            }
            patrolTask.setRejectReason(auditReason);
            patrolTask.setStatus(PatrolConstant.TASK_BACK);
            updateById = patrolTaskMapper.updateById(patrolTask);
            // 任务审核不通过发送消息给巡视人
            this.sendAuditNoPassMessage(patrolTask.getId(), loginUser);
        } else {
            patrolTask.setStatus(PatrolConstant.TASK_COMPLETE);
            patrolTask.setAuditorRemark(remark);
            patrolTask.setAuditorTime(new Date());
            updateById = patrolTaskMapper.updateById(patrolTask);
            this.sendAuditPassMessage(patrolTask.getId(), loginUser);
        }

        // 任务有一个人审核后则更新待办消息为已办
        isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.PATROL_AUDIT.getType(), patrolTask.getId(), loginUser.getUsername(), CommonTodoStatus.DONE_STATUS_1);
        return updateById;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getPatrolTaskSubmit(PatrolTaskDTO patrolTaskDTO) {
        //提交任务：将待执行、执行中，变为待审核、添加任务结束人id,传签名地址、任务主键id、审核状态
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LoginUser user = sysBaseApi.getUserById(sysUser.getId());
        boolean admin = SecurityUtils.getSubject().hasRole("admin");
        PatrolTask patrolTask = patrolTaskMapper.selectById(patrolTaskDTO.getId());
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_SUBMIT_SIGNATURE);
        boolean value = "1".equals(paramModel.getValue());
        if (!manager.checkTaskUser(patrolTask.getCode()) && !admin) {
            throw new AiurtBootException("只有该任务的巡检人才可以提交任务");
        }
        List<PatrolTaskDevice> taskDevices = patrolTaskDeviceMapper.selectList(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getTaskId, patrolTask.getId()));
        List<PatrolTaskDevice> errDeviceList = taskDevices.stream().filter(e -> PatrolConstant.RESULT_EXCEPTION.equals(e.getCheckResult())).collect(Collectors.toList());
        LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
        if (PatrolConstant.TASK_CHECK.equals(patrolTask.getAuditor())) {
            if (CollUtil.isNotEmpty(errDeviceList)) {
                updateWrapper.set(PatrolTask::getStatus, 6)
                        .set(PatrolTask::getEndUserId, sysUser.getId())
                        .set(PatrolTask::getSignUrl, value?user.getSignatureUrl():patrolTaskDTO.getSignUrl())
                        .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                        .set(PatrolTask::getAbnormalState, 0)
                        .eq(PatrolTask::getId, patrolTaskDTO.getId());
            } else {
                updateWrapper.set(PatrolTask::getStatus, 6)
                        .set(PatrolTask::getEndUserId, sysUser.getId())
                        .set(PatrolTask::getSignUrl, value?user.getSignatureUrl():patrolTaskDTO.getSignUrl())
                        .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                        .set(PatrolTask::getAbnormalState, 1)
                        .eq(PatrolTask::getId, patrolTaskDTO.getId());
            }
        } else {
            if (CollUtil.isNotEmpty(errDeviceList)) {
                updateWrapper.set(PatrolTask::getStatus, 7)
                        .set(PatrolTask::getEndUserId, sysUser.getId())
                        .set(PatrolTask::getSignUrl, value?user.getSignatureUrl():patrolTaskDTO.getSignUrl())
                        .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                        .set(PatrolTask::getAbnormalState, 0)
                        .eq(PatrolTask::getId, patrolTaskDTO.getId());
            } else {
                updateWrapper.set(PatrolTask::getStatus, 7)
                        .set(PatrolTask::getEndUserId, sysUser.getId())
                        .set(PatrolTask::getSignUrl, value?user.getSignatureUrl():patrolTaskDTO.getSignUrl())
                        .set(PatrolTask::getAbnormalState, 1)
                        .set(PatrolTask::getSubmitTime, LocalDateTime.now())
                        .eq(PatrolTask::getId, patrolTaskDTO.getId());
            }

        }
        // 无论任务是否要审核，都要更新巡视工时
        // 根据配置决定巡视工时是使用mac计算，还是巡视工单时长之和
        SysParamModel model = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_DURATION_USE_MAC);
        if ("1".equals(model.getValue())) {
            // 使用mac计算工时
            // 根据id获取task
            PatrolTask task = this.getById(patrolTaskDTO.getId());
            // 查询出任务所在的站点，需求说一个任务不可能有多个站点，所以就取第一个
            List<String> stationCodeList =  patrolTaskStationService.getStationCodeByTaskCode(task.getCode());
            String stationCode = stationCodeList.get(0);
            // 查看站点是否是工区->提交人所在的班组的工区站点，是否就是任务的站点
            List<String> WorkAreaStationCodeList = sysBaseApi.getWorkAreaStationCodeByUserId(sysUser.getId());
            boolean isWorkArea = WorkAreaStationCodeList.contains(stationCode);
            // 巡视标准时长，单位：秒
            Integer standardDuration = task.getStandardDuration();
            // wifi最近连接巡视站点时间
            Date recentConnectTime = sysBaseApi.getRecentConnectTimeByStationCode(sysUser.getUsername(),stationCode);
            // 把提交任务时最近一次wifi连接记录存入patrol_task表
            updateWrapper.set(PatrolTask::getWifiConnectTime, recentConnectTime);
            if (isWorkArea) {
                // 工区，巡视时长等于上限时长。
                updateWrapper.set(PatrolTask::getDuration, standardDuration);
            } else {
                // 非工区，当巡视时长大于大于上限时长时，巡视时长等于上限时长。不然就是wifi最近连接巡视站点时间减提交时间
                if (ObjectUtil.isNull(recentConnectTime)) {
                    updateWrapper.set(PatrolTask::getDuration, standardDuration);
                }else {
                    int duration = (int) DateUtil.between(recentConnectTime, new Date(), DateUnit.SECOND);
                    // 上限时长(标准工时)判空
                    Integer realDuration = Optional.ofNullable(standardDuration).filter(s -> duration >= s).orElseGet(() -> duration);
                    updateWrapper.set(PatrolTask::getDuration, realDuration);
                }

            }
        }else{
            // 巡视时长使用各工单时长之和
            Integer duration = patrolTaskMapper.getTaskDurationBySumDevice(patrolTaskDTO.getId());
            updateWrapper.set(PatrolTask::getDuration, duration);
        }

        //获取mac地址
        List<PatrolTaskDeviceDTO> mac = patrolTaskDeviceMapper.getMac(patrolTaskDTO.getId());

        if (CollUtil.isNotEmpty(mac)) {
            long l = mac.stream().filter(m -> m.getMacStatus().equals(PatrolConstant.MAC_STATUS_EXCEPTION) || ObjectUtil.isEmpty(m.getMacStatus())).count();
            if (l == 0L) {
                updateWrapper.set(PatrolTask::getMacStatus, 1);
            }else {
                updateWrapper.set(PatrolTask::getMacStatus, 0);
            }
        } else {
            updateWrapper.set(PatrolTask::getMacStatus, 0);
        }

        patrolTaskMapper.update(new PatrolTask(), updateWrapper);
        // 提交任务如果需要审核则发送一条审核待办消息
        try {
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
                String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));

                //构建消息模板
                HashMap<String, Object> map = new HashMap<>();
                map.put("code",patrolTask.getCode());
                map.put("patrolTaskName",patrolTask.getName());
                List<String>  station = patrolTaskStationMapper.getStationByTaskCode(patrolTask.getCode());
                map.put("patrolStation",CollUtil.join(station,","));
                if (patrolTask.getPatrolDate() != null) {
                    String patrolDate = DateUtil.format(patrolTask.getPatrolDate(), "yyyy-MM-dd");
                    map.put("patrolTaskTime",patrolDate);
                }else {
                    String s1 = DateUtil.format(patrolTask.getStartDate(), "yyyy-MM-dd");
                    String s2 = DateUtil.format(patrolTask.getEndDate(), "yyyy-MM-dd");
                    map.put("patrolTaskTime",s1+"-"+s2);
                }
                map.put("patrolName", realNames);

                //发送通知
                MessageDTO messageDTO = new MessageDTO(sysUser.getUsername(),userName, "巡视任务-审核通过" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_4);
                PatrolMessageDTO patrolMessageDTO = new PatrolMessageDTO();
                BeanUtil.copyProperties(patrolTask,patrolMessageDTO);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                /*patrolMessageDTO.setBusType(SysAnnmentTypeEnum.PATROL_ASSIGN.getType());
                messageDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
                messageDTO.setMsgAbstract("巡视任务完成");
                messageDTO.setPublishingContent("巡视任务已完成，请确认");
                sendMessage(messageDTO,realNames,null,patrolMessageDTO);*/
                //发送代办
                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setData(map);
                SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_MESSAGE_PROCESS);
                todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");

                todoDTO.setTemplateCode(CommonConstant.PATROL_SERVICE_NOTICE);
                todoDTO.setTitle("巡视任务-审核"+DateUtil.today());
                todoDTO.setMsgAbstract("巡视任务完成");
                todoDTO.setPublishingContent("巡视任务已完成，请确认");

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

                // 更新待办
                isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.PATROL_EXECUTE.getType(), patrolTaskDTO.getId(), sysUser.getUsername(), "1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void getPatrolTaskManualListAdd(PatrolTaskManualDTO patrolTaskManualDTO) {
        //保存任务信息
        PatrolTask patrolTask = new PatrolTask();
        patrolTask.setName(patrolTaskManualDTO.getName());
        patrolTask.setCode(PatrolCodeUtil.getTaskCode());
        patrolTask.setStartDate(patrolTaskManualDTO.getStartDate());
        patrolTask.setEndDate(patrolTaskManualDTO.getEndDate());
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
        patrolTask.setStandardDuration(patrolTaskManualDTO.getStandardDuration());
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
    public int taskDispose(List<PatrolTask> patrolTasks, String omitExplain) {
        // 获取当前登录用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测为未登录状态，请登录系统后操作！");
        }
        for (PatrolTask task : patrolTasks) {
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

            patrolTaskMapper.updateById(task);
        }
        return patrolTasks.size();
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
                                            .setSpecialCharacters(result.getSpecialCharacters())
                                            .setRequired(result.getRequired())
                                            .setDelFlag(0);
                                    newResultList.add(checkResult);
                                }
                        );
                        if (CollUtil.isEmpty(newResultList)) {
                            throw new AiurtBootException("未找到对应工单检查项目内容！");
                        }
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
                .set(PatrolTask::getStandardDuration, patrolTaskManualDTO.getStandardDuration())
                .set(PatrolTask::getName, patrolTaskManualDTO.getName())
                .set(PatrolTask::getStartDate, patrolTaskManualDTO.getStartDate())
                .set(PatrolTask::getEndDate, patrolTaskManualDTO.getEndDate())
                .eq(PatrolTask::getId, patrolTaskManualDTO.getId());
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
                if(CollUtil.isEmpty(deviceList)){
                     throw new AiurtBootException("要指定设备才可以保存");
                }
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
        if (ObjectUtil.isNotEmpty(patrolTaskStandard)) {
            String majorName = patrolTaskStandard.stream().filter(t -> StrUtil.isNotEmpty(t.getMajorName())).map(PatrolTaskStandardDTO::getMajorName).distinct().collect(Collectors.joining(";"));
            String sysName = patrolTaskStandard.stream().filter(t -> StrUtil.isNotEmpty(t.getSysName())).map(PatrolTaskStandardDTO::getSysName).distinct().collect(Collectors.joining(";"));
            e.setMajorName(majorName);
            e.setSysName(sysName);
        }
        List<String> orgCodes = patrolTaskMapper.getOrgCode(e.getCode());
        e.setOrganizationName(manager.translateOrg(orgCodes));
        List<StationDTO> stationName = patrolTaskMapper.getStationName(e.getCode());
        e.setStationName(manager.translateStation(stationName));
        e.setEndUserName(e.getEndUserName() == null ? "-" : e.getEndUserName());
        e.setSubmitTime(e.getSubmitTime() == null ? "-" : e.getSubmitTime());
        e.setPeriod(e.getPeriod() == null ? "-" : e.getPeriod());
        e.setOrgCodeList(orgCodes);
        e.setPatrolUserName(manager.spliceUsername(e.getCode()));
        e.setPatrolReturnUserName(userName == null ? "-" : userName);
        List<PatrolTaskDevice> taskDeviceList = patrolTaskDeviceMapper.selectList(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getTaskId, id));
        List<PatrolAccompany> accompanyList = new ArrayList<>();
        List<PatrolSamplePerson> samplePersonList = new ArrayList<>();
        for (PatrolTaskDevice patrolTaskDevice : taskDeviceList) {
            List<PatrolAccompany> patrolAccompanies = accompanyMapper.selectList(new LambdaQueryWrapper<PatrolAccompany>().eq(PatrolAccompany::getTaskDeviceCode, patrolTaskDevice.getPatrolNumber()));
            if (CollUtil.isNotEmpty(patrolAccompanies)) {
                accompanyList.addAll(patrolAccompanies);
            }
            List<PatrolSamplePerson> patrolSamplePeoples = patrolSamplePersonMapper.selectList(new LambdaQueryWrapper<PatrolSamplePerson>().eq(PatrolSamplePerson::getTaskDeviceCode, patrolTaskDevice.getPatrolNumber()));
            if (CollUtil.isNotEmpty(patrolSamplePeoples)) {
                samplePersonList.addAll(patrolSamplePeoples);
            }
        }
        if (CollUtil.isNotEmpty(accompanyList)) {
            accompanyList = accompanyList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PatrolAccompany::getUserId))), ArrayList::new));
            String peerPeople = accompanyList.stream().map(PatrolAccompany::getUsername).collect(Collectors.joining(";"));
            e.setPeerPeople(peerPeople);
        }
        if (CollUtil.isNotEmpty(samplePersonList)) {
            samplePersonList = samplePersonList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PatrolSamplePerson::getUserId))), ArrayList::new));
            String samplePersonName = samplePersonList.stream().map(PatrolSamplePerson::getUsername).collect(Collectors.joining(";"));
            e.setSamplePersonName(samplePersonName);
        }
        return e;
    }

    /**
     * 巡视消息发送
     *
     * @param messageDTO
     * @param realNames
     * @param realName
     * @param patrolMessageDTO
     */
    private void sendMessage(MessageDTO messageDTO, String realNames, String realName, PatrolMessageDTO patrolMessageDTO) {
        //发送通知
        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(messageDTO.getData())) {
            map.putAll(messageDTO.getData());
        }
        map.put("code",patrolMessageDTO.getCode());
        map.put("patrolTaskName",patrolMessageDTO.getName());
        List<String> station = patrolTaskStationMapper.getStationByTaskCode(patrolMessageDTO.getCode());
        map.put("patrolStation",CollUtil.join(station,","));
        if (patrolMessageDTO.getPatrolDate() != null) {
            String patrolDate = DateUtil.format(patrolMessageDTO.getPatrolDate(), "yyyy-MM-dd");
            map.put("patrolTaskTime",patrolDate);
        }else {
            String s1 = DateUtil.format(patrolMessageDTO.getStartDate(), "yyyy-MM-dd");
            String s2 = DateUtil.format(patrolMessageDTO.getEndDate(), "yyyy-MM-dd");
            map.put("patrolTaskTime",s1+"-"+s2);
        }
        if (StrUtil.isNotEmpty(realNames)) {
            map.put("patrolName", realNames);
        } else {
            map.put("patrolName",realName);
        }
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, patrolMessageDTO.getId());
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, patrolMessageDTO.getBusType());
        messageDTO.setData(map);
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        sysBaseApi.sendTemplateMessage(messageDTO);
    }


    @Override
    public List<PrintPatrolTaskDTO> printPatrolTaskById(String ids) {
        List<PrintPatrolTaskDTO> arrayList = new ArrayList<>();
        List<String> idList = StrUtil.splitTrim(ids, ",");
        for (String id : idList) {
            PrintPatrolTaskDTO taskDTO = new PrintPatrolTaskDTO();
            PatrolTask patrolTask = patrolTaskMapper.selectById(id);
            Assert.notNull(patrolTask, "未找到对应记录！");
            taskDTO.setId(patrolTask.getId());
            taskDTO.setTitle(patrolTask.getName());
            // 站点信息
            List<PatrolTaskStationDTO> stationInfo = patrolTaskStationMapper.selectStationByTaskCode(patrolTask.getCode());
            taskDTO.setStationNames(stationInfo.stream().map(PatrolTaskStationDTO::getStationName).collect(Collectors.joining()));
            if (StrUtil.isNotEmpty(patrolTask.getEndUserId())) {
                taskDTO.setUserName(patrolTaskMapper.getUsername(patrolTask.getEndUserId()));
            }
            taskDTO.setSubmitTime(DateUtil.format(patrolTask.getSubmitTime(),"yyyy-MM-dd HH:mm:ss"));
            taskDTO.setSignUrl(patrolTask.getSignUrl());

            //巡视单内容

            List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(id);
            List<PrintStationDTO> stationDTOS = new ArrayList<>();

            for (PatrolStationDTO dto : billGangedInfo) {
                PrintStationDTO printStationDTO = new PrintStationDTO();
                printStationDTO.setStationName(dto.getStationName());
                List<PrintSystemDTO> printSystemDTOS = new ArrayList<>();

                //获取检修项
                List<PatrolBillDTO> billInfo = dto.getBillInfo();
                if (CollUtil.isNotEmpty(billInfo)) {
                    for (PatrolBillDTO patrolBillDTO : billInfo) {
                        //根据检修单号查询检修项
                        String billCode = patrolBillDTO.getBillCode();
                        PrintSystemDTO printSystemDTO = new PrintSystemDTO();
                        if (StrUtil.isNotEmpty(billCode)) {
                            PatrolTaskDeviceParam taskDeviceParam = patrolTaskDeviceMapper.getIdAndSystemName(billCode);
                            printSystemDTO.setSystemName(taskDeviceParam.getSubsystemName());
                            List<PrintDetailDTO> printDetailList = new ArrayList<>();

                            List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getCheckByTaskDeviceId(taskDeviceParam.getId());
                            for (PatrolCheckResultDTO c : checkResultList) {
                                String userName = patrolTaskMapper.getUserName(c.getUserId());
                                c.setCheckUserName(userName);

                                PrintDetailDTO printDetailDTO = new PrintDetailDTO();
                                printDetailDTO.setContent(
                                        Optional.ofNullable(c.getQualityStandard())
                                                .map(qs -> c.getContent() + ":" + qs)
                                                .orElse(c.getContent())
                                );
                                printDetailDTO.setResult(Convert.toStr(c.getCheckResult()));
                                printDetailDTO.setRemark(c.getRemark());
                                printDetailList.add(printDetailDTO);
                            }

                            printSystemDTO.setPrintDetailDTOS(printDetailList);
                            printSystemDTOS.add(printSystemDTO);
                        }
                    }
                    printStationDTO.setPrintSystemDTOS(printSystemDTOS);
                    stationDTOS.add(printStationDTO);
                }
            }
            taskDTO.setPrintStationDTOList(stationDTOS);
            List<String> collect = stationInfo.stream().map(PatrolTaskStationDTO::getStationName).collect(Collectors.toList());
            taskDTO.setTitle(CollUtil.join(collect, ",") + patrolTask.getName() + "巡视表");
            arrayList.add(taskDTO);
        }
        return arrayList;
    }

    @Override
    public List<PrintPatrolTaskStandardDTO> printPatrolTaskAndStandardById(String ids,String standardId) {
        List<PrintPatrolTaskStandardDTO> arrayList = new ArrayList<>();
        List<String> idList = StrUtil.splitTrim(ids, ",");
        for (String id : idList) {
            PrintPatrolTaskStandardDTO printPatrolTaskStandardDTO = new PrintPatrolTaskStandardDTO();
            PatrolTask patrolTask = patrolTaskMapper.selectById(id);
            Assert.notNull(patrolTask, "未找到对应记录！");
            printPatrolTaskStandardDTO.setId(patrolTask.getId());
            printPatrolTaskStandardDTO.setTitle(patrolTask.getName());
            // 站点信息
            List<PatrolTaskStationDTO> stationInfo = patrolTaskStationMapper.selectStationByTaskCode(patrolTask.getCode());
            printPatrolTaskStandardDTO.setStationNames(stationInfo.stream().map(PatrolTaskStationDTO::getStationName).collect(Collectors.joining()));
            if (StrUtil.isNotEmpty(patrolTask.getEndUserId())) {
                printPatrolTaskStandardDTO.setUserName(patrolTaskMapper.getUsername(patrolTask.getEndUserId()));
            }
            printPatrolTaskStandardDTO.setSubmitTime(DateUtil.format(patrolTask.getSubmitTime(),"yyyy-MM-dd HH:mm:ss"));
            printPatrolTaskStandardDTO.setSignUrl(patrolTask.getSignUrl());
            // 组织机构信息
            List<PatrolTaskOrganizationDTO> organizationInfo = patrolTaskOrganizationMapper.selectOrgByTaskCode(patrolTask.getCode());
            //巡视单内容
            List<PrintTaskStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfoToPrint(id);
            //巡视任务单
            List<PrintStandardDTO> standardDTOS = new ArrayList<>();
            PrintStandardDTO printStandardDTO = new PrintStandardDTO();
            List<PrintTaskStationDTO> printTaskStationDTOS = new ArrayList<>();
            for (PrintTaskStationDTO dto : billGangedInfo) {
                //获取检修项
                List<PrintStandardDetailDTO> billInfo = dto.getBillInfo();
                List<PrintStandardDetailDTO> printStandardDetailDTOS = new ArrayList<>();
                if (CollUtil.isNotEmpty(billInfo)) {
                    for (PrintStandardDetailDTO printStandardDetailDTO : billInfo) {
                        String billCode = printStandardDetailDTO.getBillCode();
                        //构建树
                        PatrolTaskDeviceParam taskDeviceParam = Optional.ofNullable(patrolTaskDeviceMapper.selectBillInfoByNumberToPrint(billCode))
                                .orElseGet(PatrolTaskDeviceParam::new);
                        List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getListByTaskDeviceId(taskDeviceParam.getId());
                        List<PatrolCheckResultDTO> tree = getTree(checkResultList, "0");
                        //设备位置翻译
                        StationDTO stationDTO = new StationDTO();
                        stationDTO.setLineCode(taskDeviceParam.getLineCode());
                        stationDTO.setStationCode(taskDeviceParam.getStationCode());
                        stationDTO.setPositionCode(taskDeviceParam.getPositionCode());
                        List<StationDTO> stationDTOList = new ArrayList<>();
                        stationDTOList.add(stationDTO);
                        String s = manager.translateStation(stationDTOList);
                        //设备的位置
                        if (ObjectUtil.isNotEmpty(taskDeviceParam.getDeviceCode())) {
                            taskDeviceParam.setDevicePositionName(s);
                        } else {
                            taskDeviceParam.setInspectionPositionName(s);
                            taskDeviceParam.setDevicePositionName(null);
                        }
                        //巡检工单详情
                        printStandardDetailDTO.setUserName(patrolTaskMapper.getUsername(patrolTask.getEndUserId()));
                        printStandardDetailDTO.setSubmitTime(DateUtil.format(patrolTask.getSubmitTime(),"yyyy-MM-dd HH:mm:ss"));
                        printStandardDetailDTO.setSignUrl(patrolTask.getSignUrl());
                        printStandardDetailDTO.setPeriod(patrolTask.getPeriod());
                        printStandardDetailDTO.setSpotCheckTime(taskDeviceParam.getCheckTime());
                        printStandardDetailDTO.setChildren(tree);
                        printStandardDetailDTO.setSpotCheckUserName(taskDeviceParam.getSamplePersonName());
                        printStandardDetailDTO.setDeviceName(taskDeviceParam.getDeviceName());
                        printStandardDetailDTO.setDeviceLocation(taskDeviceParam.getDevicePositionName());
                        printStandardDetailDTO.setDepartInfo(organizationInfo);
                        printStandardDetailDTOS.add(printStandardDetailDTO);
                    }
                }
                dto.setBillInfo(printStandardDetailDTOS);
                printTaskStationDTOS.add(dto);
            }
            printStandardDTO.setPatrolStationDTOS(printTaskStationDTOS);
            standardDTOS.add(printStandardDTO);

            printPatrolTaskStandardDTO.setPrintStandardDTOList(standardDTOS);
            List<String> collect = stationInfo.stream().map(PatrolTaskStationDTO::getStationName).collect(Collectors.toList());
            printPatrolTaskStandardDTO.setTitle(CollUtil.join(collect, ",") + patrolTask.getName() + "巡视表");
            arrayList.add(printPatrolTaskStandardDTO);
        }
        return arrayList;
    }

    /**
     * 构建巡检项目树
     *
     * @param list
     * @param parentId
     * @return
     */
    public List<PatrolCheckResultDTO> getTree(List<PatrolCheckResultDTO> list, String parentId) {
        // 树的根节点
        List<PatrolCheckResultDTO> tree = Optional.ofNullable(list).orElseGet(Collections::emptyList)
                .stream().filter(l -> ObjectUtil.isNotEmpty(l.getParentId()) && parentId.equals(l.getParentId()))
                .collect(Collectors.toList());
        // 非根节点数据
        List<PatrolCheckResultDTO> subList = Optional.ofNullable(list).orElseGet(Collections::emptyList)
                .stream().filter(l -> !parentId.equals(l.getParentId()))
                .collect(Collectors.toList());
        // 构建根节点下的子树
        tree.stream().forEach(l -> {
            List<PatrolCheckResultDTO> subTree = buildTree(subList, l.getOldId());
            l.setChildren(subTree);
        });
        return tree;
    }
    /**
     * 递归获取子树
     *
     * @param list
     * @param parentId
     * @return
     */
    public List<PatrolCheckResultDTO> buildTree(List<PatrolCheckResultDTO> list, String parentId) {
        List<PatrolCheckResultDTO> tree = new ArrayList<>();
        for (PatrolCheckResultDTO dept : list) {
            if (dept.getParentId() != null) {
                if (dept.getParentId().equals(parentId)) {
                    List<PatrolCheckResultDTO> subList = buildTree(list, dept.getId());
                    dept.setChildren(subList);
                    tree.add(dept);
                }
            }
        }
        return tree;
    }

    @Override
    public void patrolTaskManualDelete(String id) {
        if (StrUtil.isEmpty(id)) {
            throw new AiurtBootException("操作失败");
        }
        PatrolTask patrolTask = patrolTaskMapper.selectById(id);
        if (ObjectUtil.isNotEmpty(patrolTask)) {
            patrolTask.setDelFlag(CommonConstant.DEL_FLAG_1);
            patrolTaskMapper.updateById(patrolTask);
            if (StrUtil.isNotEmpty(patrolTask.getCode())) {
                patrolTaskOrganizationService.update(new LambdaUpdateWrapper<PatrolTaskOrganization>().set(PatrolTaskOrganization::getDelFlag, CommonConstant.DEL_FLAG_1).eq(PatrolTaskOrganization::getTaskCode, patrolTask.getCode()));
                patrolTaskStationService.update(new LambdaUpdateWrapper<PatrolTaskStation>().set(PatrolTaskStation::getDelFlag, CommonConstant.DEL_FLAG_1).eq(PatrolTaskStation::getTaskCode, patrolTask.getCode()));
                patrolTaskStandardService.update(new LambdaUpdateWrapper<PatrolTaskStandard>().set(PatrolTaskStandard::getDelFlag, CommonConstant.DEL_FLAG_1).eq(PatrolTaskStandard::getTaskId, patrolTask.getId()));
                List<PatrolTaskDevice> patrolTaskDeviceList = patrolTaskDeviceMapper.selectList(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getTaskId, patrolTask.getId()));
                if (ObjectUtil.isNotEmpty(patrolTaskDeviceList)) {
                    patrolTaskDeviceList.forEach((e) -> {
                        e.setDelFlag(CommonConstant.DEL_FLAG_1);
                        patrolTaskDeviceMapper.updateById(e);
                        List<PatrolCheckResult> patrolCheckResultList = patrolCheckResultMapper.selectList(new LambdaQueryWrapper<PatrolCheckResult>()
                                .eq(PatrolCheckResult::getTaskStandardId, e.getTaskStandardId())
                                .eq(PatrolCheckResult::getTaskDeviceId, e.getId()));
                        if (ObjectUtil.isNotEmpty(patrolCheckResultList)) {
                            patrolCheckResultList.forEach((t) -> {
                                t.setDelFlag(CommonConstant.DEL_FLAG_1);
                                patrolCheckResultMapper.updateById(t);
                            });
                        }
                    });
                }
            }
        } else {
            throw new AiurtBootException("操作失败");
        }
    }

    @Override
    public MacDto getMac(String id) {
        List<PatrolTaskDeviceDTO> mac = new ArrayList<>();
        //获取巡视单mac地址
        //根据配置决定是否需要把工单数量作为任务数量
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_TASK_DEVICE_NUM);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            mac = patrolTaskDeviceMapper.getMacByDeviceId(id);
        }else {
            mac = patrolTaskDeviceMapper.getMac(id);
        }

        PatrolTask byId = this.getById(mac.get(0).getTaskId());

        List<IndexStationDTO> stationInfo = patrolTaskStationMapper.getStationInfo(byId.getCode());
        List<String> list = Optional.ofNullable(stationInfo)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(IndexStationDTO::getStationCode)
                .collect(Collectors.toList());
        //获取WiFi地址管理mac地址
        List<StationAndMacModel> wifiMac = sysBaseApi.getStationAndMacByCode(list);

        MacDto macDto = new MacDto();
        if (CollUtil.isNotEmpty(mac)) {
            List<StationAndMacModel> arrayList = new ArrayList<>();
            for (PatrolTaskDeviceDTO dto : mac) {
                if (StrUtil.isNotEmpty(dto.getMac())) {
                    StationAndMacModel stationAndMacModel = new StationAndMacModel();
                    stationAndMacModel.setMac(dto.getMac());
                    stationAndMacModel.setStationName(StrUtil.isNotEmpty(dto.getStationName())?dto.getStationName():"不存在该mac地址");
                    arrayList.add(stationAndMacModel);
                }

            }
            List<StationAndMacModel> collect = arrayList.stream().distinct().collect(Collectors.toList());
            macDto.setLocalMac(collect);
            macDto.setStationMac(wifiMac);
        }
        return macDto;
    }

    @Override
    public void spotCheck(PatrolTaskDTO patrolTaskDTO) {
        PatrolTask patrolTask = this.getById(patrolTaskDTO.getId());
        if (ObjectUtil.isEmpty(patrolTask)) {
            throw new AiurtBootException("未找到此条数据");
        }
        if (ObjectUtil.isEmpty(patrolTaskDTO.getSpotCheckStatus())) {
            throw new AiurtBootException("抽查状态不能为空");
        }
        // 设置抽查信息
        patrolTask.setSpotCheckStatus(patrolTaskDTO.getSpotCheckStatus())
                .setSpotCheckTime(patrolTaskDTO.getSpotCheckTime())
                .setSpotCheckUserId(patrolTaskDTO.getSpotCheckUserId())
                .setSpotCheckRemark(patrolTaskDTO.getSpotCheckRemark());
        this.updateById(patrolTask);
    }

    @Override
    public String printPatrolTask(String id) {
        PatrolTask patrolTask = patrolTaskMapper.selectById(id);
        List<PatrolTaskStandard> patrolTaskStandard = patrolTaskStandardMapper.selectList(new LambdaQueryWrapper<PatrolTaskStandard>()
                .eq(PatrolTaskStandard::getDelFlag,0).eq(PatrolTaskStandard::getTaskId,patrolTask.getId()));
        PatrolStandard patrolStandard = patrolStandardMapper.selectOne(new LambdaQueryWrapper<PatrolStandard>()
                .eq(PatrolStandard::getDelFlag,0)
                .in(PatrolStandard::getCode,patrolTaskStandard.stream().map(PatrolTaskStandard::getStandardCode).collect(Collectors.toList()))
                .orderByDesc(PatrolStandard::getPrintTemplate).last("LIMIT 1"));
        String excelName = null;
        if (StrUtil.isNotEmpty(patrolStandard.getPrintTemplate())){
            excelName = sysBaseApi.dictById(patrolStandard.getPrintTemplate()).getValue();
        }else {
            excelName = "telephone_system.xlsx";
        }

        // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
        // 填充list 的时候还要注意 模板中{.} 多了个点 表示list
        // 如果填充list的对象是map,必须包涵所有list的key,哪怕数据为null，必须使用map.put(key,null)
        String templateFileName = "patrol" +"/" + "template" + "/" + excelName;
        log.info("templateFileName:"+templateFileName);
        InputStream minioFile = MinioUtil.getMinioFile("platform",templateFileName);
        Workbook workbookTpl = null;
        CellRangeAddress mergeRegion = null;
        Integer firstColumn = null;
        Integer lastColumn = null;
        try {
//            inputStreamTemplate = new FileInputStream(templateFileName);
            workbookTpl = WorkbookFactory.create(minioFile);
            Sheet sheet = workbookTpl.getSheetAt(0);
            mergeRegion = FilePrintUtils.findMergeRegions(sheet, 1,3,"巡检标准");
            firstColumn = mergeRegion.getFirstColumn();
            lastColumn = mergeRegion.getLastColumn();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 全部放到内存里面 并填充
        String fileName = patrolTask.getName() + System.currentTimeMillis() + ".xlsx";
        String relatiePath = "/" + "patrol" + "/" + "print" + "/" + fileName;
        String filePath = path +"/" +  fileName;
        // 这里 会填充到第一个sheet， 然后文件流会自动关闭
        // 查询头部数据
        PrintPatrolTaskDTO taskDTO = new PrintPatrolTaskDTO();

        Assert.notNull(patrolTask, "未找到对应记录！");
        taskDTO.setId(patrolTask.getId());
        taskDTO.setTitle(patrolTask.getName());
        // 站点信息
        List<PatrolTaskStationDTO> stationInfo = patrolTaskStationMapper.selectStationByTaskCode(patrolTask.getCode());
        taskDTO.setStationNames(stationInfo.stream().map(PatrolTaskStationDTO::getStationName).collect(Collectors.joining()));
        if (StrUtil.isNotEmpty(patrolTask.getEndUserId())) {
            taskDTO.setUserName(patrolTaskMapper.getUsername(patrolTask.getEndUserId()));
        }
        if (StrUtil.isNotEmpty(patrolTask.getSpotCheckUserId())) {
            taskDTO.setSpotCheckUserName(patrolTaskMapper.getUsername(patrolTask.getSpotCheckUserId()));
        }
        taskDTO.setSignUrl(patrolTask.getSignUrl());
        Map<String, Object> map = MapUtils.newHashMap();
        map.put("title",patrolTask.getName());
        map.put("patrolStation", taskDTO.getStationNames());
        map.put("patrolPerson", taskDTO.getUserName());
        map.put("checkUserName",taskDTO.getSpotCheckUserName());
        map.put("patrolDate", DateUtil.format(patrolTask.getSubmitTime(),"yyyy-MM-dd"));
        map.put("patrolTime", DateUtil.format(patrolTask.getSubmitTime(),"HH:mm"));
        Map<String, Object> imageMap = MapUtils.newHashMap();
        if(StrUtil.isNotEmpty(taskDTO.getSignUrl())){
            int index =  taskDTO.getSignUrl().indexOf("?");
            SysAttachment sysAttachment = sysBaseApi.getFilePath(taskDTO.getSignUrl().substring(0, index));
            InputStream inputStream = MinioUtil.getMinioFile("platform",sysAttachment.getFilePath());
            if(ObjectUtil.isEmpty(inputStream)){
                imageMap.put("signImage",null);
            } else {
                try {
                    byte[] convert = FilePrintUtils.convert(inputStream);
                //    WriteCellData writeImageData = FilePrintUtils.writeCellImageData(convert, excelDictModel);
              //      imageMap.put("signImage",writeImageData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            imageMap.put("signImage",null);
        }

//        String imagePath = "C:\\Users\\14719\\Desktop\\1685182072119.jpg";
//        // 设置图片数据
//        File[] files = {new File(imagePath)};


        //查询巡视标准详情
        List<PrintDTO> patrolData = getPrint(id,map);
        InputStream minioFile2 = MinioUtil.getMinioFile("platform",templateFileName);
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).withTemplate(minioFile2).build()) {
            int[] mergeColumnIndex = {0,1,2};
            CustomCellMergeHandler customCellMergeStrategy = new CustomCellMergeHandler(3,mergeColumnIndex);
            WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(customCellMergeStrategy).build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.FALSE).build();
            //填充列表数据
            excelWriter.fill(new FillWrapper("list",patrolData),fillConfig, writeSheet);
            //填充表头
            excelWriter.fill(map, writeSheet);
            //填充图片
            excelWriter.fill(imageMap, writeSheet);
            excelWriter.finish();
            int startRow = 3;
            int endRow = startRow;
            if (CollUtil.isNotEmpty(patrolData)){
                endRow = startRow+patrolData.size()-1;
            }

            try (InputStream inputStream = new FileInputStream(filePath);
                Workbook workbook = WorkbookFactory.create(inputStream)) {
                Sheet sheet = workbook.getSheetAt(0);
//                sheet.setMargin(Sheet.TopMargin, 0.5); // 上边距
//                sheet.setMargin(Sheet.BottomMargin, 0.5); // 下边距
//                sheet.setMargin(Sheet.LeftMargin, 1); // 左边距
//                sheet.setMargin(Sheet.RightMargin, 1); // 右边距
                FilePrintUtils.printSet(sheet);
                // 设置边距（单位为英寸）
                // 设置打印边距
                //自动换行
               // setWrapText(workbook,1,startRow,endRow,0,0);
                FilePrintUtils.addReturn(workbook,startRow,endRow,0,0);
                FilePrintUtils.setWrapText(workbook,7,1,1,1,1,true);
                FilePrintUtils.setWrapText(workbook,7,startRow,endRow,1,firstColumn>3?3:2,false);
                //合并指定范围行的单元格
                FilePrintUtils.mergeCellsInColumnRange(workbook,true,startRow,endRow,firstColumn,lastColumn);

                //设置第一列列宽
                FilePrintUtils.setColumnWidth(sheet,0,10);
                // 保存修改后的Excel文件
                try (OutputStream outputStream = new FileOutputStream(filePath)) {
                    workbook.write(outputStream);
                }
            }
            MinioUtil.upload(new FileInputStream(filePath),relatiePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SysAttachment sysAttachment = new SysAttachment();
        sysAttachment.setFileName(fileName);
        sysAttachment.setFilePath(relatiePath);
        sysAttachment.setType("minio");
        sysBaseApi.saveSysAttachment(sysAttachment);
        return sysAttachment.getId()+"?fileName="+sysAttachment.getFileName();
    }

    private List<PrintDTO> getPrint(String id, Map<String, Object> map) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(id);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getCheckByTaskDeviceIdAndParent(collect);
            for (PatrolCheckResultDTO c : checkResultList) {
                List<PatrolCheckResultDTO> list = patrolCheckResultMapper.getQualityStandard(collect,c.getOldId());
                for (PatrolCheckResultDTO t :list){
                    PrintDTO printDTO = new PrintDTO();
                    printDTO.setStandard(t.getQualityStandard());
                    printDTO.setEquipment(c.getContent());
                    printDTO.setContent(t.getContent());
                    if(ObjectUtil.isEmpty(t.getCheckResult())){
                        printDTO.setResultTrue("☐正常");
                        printDTO.setResultFalse("☐异常");
                    }else {
                        printDTO.setResultTrue(t.getCheckResult()==0?"☐正常":"☑正常");
                        printDTO.setResultFalse(t.getCheckResult()==0?"☑异常":"☐异常");
                    }
                    printDTO.setRemark(t.getRemark());
                    printDTO.setLocation(dto.getStationName());
                    printDTO.setSubSystem(t.getSubsystemName());
                    if (ObjectUtil.isNotEmpty(printDTO.getStandard())){
                        getPrint.add(printDTO);
                    }
                }
            }
        }
        return getPrint;
    }

    private List<PrintDTO> getRemark(String id) {
        List<PrintDTO> getRemark = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(id);
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll = patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            //父级
            for (PatrolCheckResultDTO parentDTO : checkResultAll.stream().filter(c -> c.getHierarchyType() == 0).collect(Collectors.toList())) {
                PrintDTO printDTO = new PrintDTO();
                String oldId = parentDTO.getOldId();
                StringBuffer stringBuffer = new StringBuffer();
                AtomicBoolean flag = new AtomicBoolean(false);
                //子级
                List<PatrolCheckResultDTO> childDTOs =  checkDTOs.stream()
                        .filter(c -> c.getHierarchyType() == 1)
                        .filter(c -> c.getParentId().equals(oldId))
                        .collect(Collectors.toList());
                childDTOs.forEach(c->{
                    if(c.getCheckResult().equals(0)){
                        flag.set(true);
                        stringBuffer.append(c.getQualityStandard()).append(":异常");
                        stringBuffer.append(",");
                    }
                });
                if(flag.get()){
                    printDTO.setResultTrue("☐正常");
                    printDTO.setResultFalse("☑异常");
                    stringBuffer.deleteCharAt(stringBuffer.length()-1);
                    printDTO.setRemark(stringBuffer.toString());
                }else{
                    printDTO.setResultTrue("☑正常");
                    printDTO.setResultFalse("☐异常");
                }
                getRemark.add(printDTO);
            }
        }
        return getRemark;
    }

    @Override
    public void exportExcel(Page<PatrolTaskParam> page, PatrolTaskParam patrolTaskParam, HttpServletRequest request, HttpServletResponse response) {
        // 如果有selections，根据id查询，就不走分页查询
        List<String> patrolTaskIdList;
        if (StrUtil.isNotEmpty(request.getParameter("selections"))){
            patrolTaskIdList = Arrays.asList(request.getParameter("selections").split(","));
        }else{
            IPage<PatrolTaskParam> taskPage = this.getTaskList(page, patrolTaskParam);
            patrolTaskIdList = taskPage.getRecords().stream().map(PatrolTaskParam::getId).collect(Collectors.toList());
        }
        // 根据任务id，查询出需要导出的数据
        List<PatrolTaskExportExcelDTO> list = patrolTaskMapper.queryPatrolTaskExportExcelDTOByIds(patrolTaskIdList);
        // 任务状态字典
        List<DictModel> statusDictItems = sysBaseApi.getDictItems(PatrolDictCode.TASK_STATUS);
        Map<String, String> statusDictMap = statusDictItems.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        // 任务领取方式字典
        List<DictModel> sourceDictItems = sysBaseApi.getDictItems(PatrolDictCode.PATROL_TASK_ACCESS);
        Map<String, String> sourceDictMap = sourceDictItems.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));
        // 将作废、漏检、状态翻译，时间转化成字符串
        List<Map<String, Object>> mapList = list.stream().map(dto -> {
            // 是否作废
            if (PatrolConstant.TASK_DISCARD.equals(dto.getDiscardStatus())) {
                dto.setDiscardStatusString("是");
            } else {
                dto.setDiscardStatusString("否");
            }
            // 是否漏检
            if (PatrolConstant.OMIT_STATUS.equals(dto.getOmitStatus())) {
                dto.setOmitStatusString("是");
            } else {
                dto.setOmitStatusString("否");
            }
            // 状态
            dto.setStatusString(statusDictMap.get(dto.getStatus().toString()));
            // 任务来源
            dto.setSourceString(sourceDictMap.get(dto.getSource().toString()));
            // 时间转化
            dto.setDurationString(TimeUtil.translateTime(dto.getDuration()));
            dto.setActualDurationString(TimeUtil.translateTime(dto.getActualDuration()));
            return BeanUtil.beanToMap(dto);
        }).collect(Collectors.toList());
        // 导出excel
        // 1.从minio获取模板文件
        InputStream inputStream = MinioUtil.getMinioFile(bucketName, "excel/template/巡视列表导出模板.xlsx");
        // 2.创建临时文件
        File fileTemp= new File("/templates/patrolTaskExport.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(inputStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        TemplateExportParams exportParams = new TemplateExportParams(fileTemp.getAbsolutePath());
        Map<String, Object> exportMap = new HashMap<>(Collections.singletonMap("maplist", mapList));
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, exportMap);
        try {// 设置返回的是附件，设置附件名称
            String attachName = new String("巡视任务导出.xls".getBytes(), StandardCharsets.UTF_8);
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + attachName);
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
        }catch (Exception e){
            e.printStackTrace();
        }
        // 关闭临时文件
        fileTemp.delete();
    }

    @Override
    public List<PrintPatrolTaskDTO> printPatrolTaskDeviceById(String ids) {
        List<PrintPatrolTaskDTO> arrayList = new ArrayList<>();
        List<String> idList = StrUtil.splitTrim(ids, ",");
        for (String id : idList) {
            PatrolTaskDevice byId = patrolTaskDeviceService.getById(id);
            PrintPatrolTaskDTO taskDTO = new PrintPatrolTaskDTO();
            PatrolTask patrolTask = patrolTaskMapper.selectById(byId.getTaskId());
            Assert.notNull(patrolTask, "未找到对应记录！");
            taskDTO.setId(patrolTask.getId());
            taskDTO.setTitle(patrolTask.getName());
            // 站点信息
            List<IndexStationDTO>  stationInfo = patrolTaskStationMapper.getStationInfoByDeviceId(id);
            taskDTO.setStationNames(stationInfo.stream().map(IndexStationDTO::getStationName).collect(Collectors.joining()));
            if (StrUtil.isNotEmpty(patrolTask.getEndUserId())) {
                taskDTO.setUserName(patrolTaskMapper.getUsername(patrolTask.getEndUserId()));
            }
            taskDTO.setSubmitTime(DateUtil.format(byId.getCheckTime(),"yyyy-MM-dd HH:mm:ss"));
            taskDTO.setSignUrl(patrolTask.getSignUrl());

            //巡视单内容
            List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfoByDeviceID(id);

            List<PrintStationDTO> stationDTOS = new ArrayList<>();

            for (PatrolStationDTO dto : billGangedInfo) {
                PrintStationDTO printStationDTO = new PrintStationDTO();
                printStationDTO.setStationName(dto.getStationName());
                List<PrintSystemDTO> printSystemDTOS = new ArrayList<>();

                //获取检修项
                List<PatrolBillDTO> billInfo = dto.getBillInfo();
                if (CollUtil.isNotEmpty(billInfo)) {
                    for (PatrolBillDTO patrolBillDTO : billInfo) {
                        //根据检修单号查询检修项
                        String billCode = patrolBillDTO.getBillCode();
                        PrintSystemDTO printSystemDTO = new PrintSystemDTO();
                        if (StrUtil.isNotEmpty(billCode)) {
                            PatrolTaskDeviceParam taskDeviceParam = patrolTaskDeviceMapper.getIdAndSystemName(billCode);
                            printSystemDTO.setSystemName(taskDeviceParam.getSubsystemName());
                            List<PrintDetailDTO> printDetailList = new ArrayList<>();

                            List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getCheckByTaskDeviceId(taskDeviceParam.getId());
                            for (PatrolCheckResultDTO c : checkResultList) {
                                String userName = patrolTaskMapper.getUserName(c.getUserId());
                                c.setCheckUserName(userName);

                                PrintDetailDTO printDetailDTO = new PrintDetailDTO();
                                printDetailDTO.setContent(
                                        Optional.ofNullable(c.getQualityStandard())
                                                .map(qs -> c.getContent() + ":" + qs)
                                                .orElse(c.getContent())
                                );
                                printDetailDTO.setResult(Convert.toStr(c.getCheckResult()));
                                printDetailDTO.setRemark(c.getRemark());
                                printDetailList.add(printDetailDTO);
                            }

                            printSystemDTO.setPrintDetailDTOS(printDetailList);
                            printSystemDTOS.add(printSystemDTO);
                        }
                    }
                    printStationDTO.setPrintSystemDTOS(printSystemDTOS);
                    stationDTOS.add(printStationDTO);
                }
            }
            taskDTO.setPrintStationDTOList(stationDTOS);
            taskDTO.setTitle(patrolTask.getName());
            arrayList.add(taskDTO);
        }
        return arrayList;
    }
    private List<PrintDTO> getWirelessSystem(String id,Map<String, Object> map) {
        List<PrintDTO> getPrint = new ArrayList<>();
        List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(id);
        Set<String> set = new LinkedHashSet<>() ;
        StringBuilder text  = new StringBuilder();
        for (PatrolStationDTO dto : billGangedInfo) {
            //获取检修项
            String str = new String();
            List<String> collect = dto.getBillInfo().stream().filter(d -> StrUtil.isNotEmpty(d.getBillCode())).map(t -> t.getBillCode()).collect(Collectors.toList());
            List<PatrolCheckResultDTO> checkResultAll =  patrolCheckResultMapper.getCheckResultAllByTaskId(collect);
            List<PatrolCheckResultDTO> checkDTOs = checkResultAll.stream().filter(c -> c.getCheck() != 0).collect(Collectors.toList());
            List<String> wirelessSystem = sysBaseApi.getDictItems("wireless_system").stream().map(w-> w.getText()).collect(Collectors.toList());
            List<String> result = wirelessSystem.stream().filter(w -> !checkDTOs.stream().anyMatch(c -> c.getContent().equals(w))).collect(Collectors.toList());
            if (!result.isEmpty()){
                str =  result.stream().filter(s -> s.contains("：") || s.contains(":") )
                        .map(s -> s.split("[：:]")[0])
                        .collect(Collectors.joining(",")) + "( 无 )";
                set.add(str);
            }
            checkDTOs.forEach(c-> {
                if(c.getCheckResult()==0){
                    text.append("\n").append(c.getContent()).append(":异常");
             }
            });
        }
            map.put("remark","本站 : \n"+set.toString()+text);
            return getPrint;
    }
}
