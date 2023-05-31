package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.manager.dto.OrgDTO;
import com.aiurt.boot.manager.utils.CodeGenerateUtils;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.plan.req.*;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.aiurt.boot.strategy.mapper.InspectionStrategyMapper;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.AsyncThreadPoolExecutorUtil;
import com.aiurt.common.util.DateUtils;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.common.util.UpdateHelperUtils;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Service
public class RepairPoolServiceImpl extends ServiceImpl<RepairPoolMapper, RepairPool> implements IRepairPoolService {

    @Resource
    private ISysBaseAPI sysBaseApi;
    @Resource
    private IBaseApi baseApi;
    @Resource
    private InspectionManager manager;
    @Resource
    private RepairPoolRelMapper relMapper;
    @Resource
    private RepairPoolCodeMapper repairPoolCodeMapper;
    @Resource
    private RepairPoolStationRelMapper repairPoolStationRelMapper;
    @Resource
    private RepairPoolOrgRelMapper orgRelMapper;
    @Resource
    private InspectionStrategyMapper inspectionStrategyMapper;
    @Resource
    private RepairPoolDeviceRelMapper repairPoolDeviceRel;
    @Resource
    private RepairPoolCodeContentMapper repairPoolCodeContentMapper;
    @Resource
    private RepairTaskMapper repairTaskMapper;
    @Resource
    private RepairTaskOrgRelMapper repairTaskOrgRelMapper;
    @Resource
    private RepairTaskStationRelMapper repairTaskStationRelMapper;
    @Resource
    private RepairTaskUserMapper repairTaskUserMapper;
    @Resource
    private RepairTaskDeviceRelMapper repairTaskDeviceRelMapper;
    @Resource
    private RepairTaskStandardRelMapper repairTaskStandardRelMapper;
    @Resource
    private RepairTaskResultMapper repairTaskResultMapper;
    @Resource
    private InspectionCodeContentMapper inspectionCodeContentMapper;
    @Resource
    private InspectionCodeMapper inspectionCodeMapper;
    @Resource
    private ISysParamAPI iSysParamAPI;

    /**
     * 查询检修计划池中的检修任务列表。
     *
     * @param selectPlanReq 查询条件对象，封装了查询所需的筛选参数，如起始时间、结束时间、状态等
     * @return 返回一个检修计划池任务分页对象，包含符合查询条件的检修任务列表
     */
    @Override
    public IPage<RepairPool> queryList(SelectPlanReq selectPlanReq) {
        // 构造分页查询条件
        Page<RepairPool> page = new Page<>(selectPlanReq.getPageNo(), selectPlanReq.getPageSize());
        selectPlanReq.setIsManual(InspectionConstant.IS_MANUAL_0);

        // 解析逗号分隔的状态列表字符串，并设置到查询条件对象中
        if (StrUtil.isNotEmpty(selectPlanReq.getStatuList())) {
            List<String> split = StrUtil.split(selectPlanReq.getStatuList(), ',');
            if (CollUtil.isNotEmpty(split)) {
                selectPlanReq.setStatusList(split);
            }
        }

        // 执行查询
        List<RepairPool> result = baseMapper.selectRepairPool(page, selectPlanReq);

        if (CollUtil.isEmpty(result)) {
            return page;
        }

        boolean filter = GlobalThreadLocal.setDataFilter(false);

        // 获取所有 repairPool 的 code 列表
        List<String> repairPoolCodes = result.stream().map(RepairPool::getCode).collect(Collectors.toList());

        // 获取检查周期类型字典映射
        Map<String, String> cycleTypeMap = getCycleTypeMap();

        // 获取检查任务状态字典映射
        Map<String, String> inspectionTaskStateMap = getInspectionTaskStateMap();

        // 获取工作类型映射
        Map<String, String> workTypeMap = getWorkTypeMap();

        // 查询所有相关的组织机构
        List<RepairPoolOrgRel> allRepairPoolOrgRels = getAllRepairPoolOrgRels(repairPoolCodes);

        // 查询所有相关的站点
        Map<String, List<StationDTO>> allRepairPoolStationRels = getAllRepairPoolStationRels(repairPoolCodes);

        if (CollUtil.isNotEmpty(result)) {
            processResultWithThreadPool(result, cycleTypeMap, inspectionTaskStateMap, workTypeMap, allRepairPoolOrgRels, allRepairPoolStationRels);
        }
        GlobalThreadLocal.setDataFilter(filter);
        // 返回带有查询结果的分页对象
        return page.setRecords(result);
    }

    /**
     * 使用线程池处理检修任务列表的结果数据，包括字典项映射等
     *
     * @param result                 需要处理的检修任务列表
     * @param cycleTypeMap           检查周期类型字典映射，用于将检查周期类型的值映射为对应的文本
     * @param inspectionTaskStateMap 检查任务状态字典映射，用于将检查任务状态的值映射为对应的文本
     * @param workTypeMap            工作类型字典映射，用于将工作类型的值映射为对应的文本
     */
    private void processResultWithThreadPool(List<RepairPool> result,
                                             Map<String, String> cycleTypeMap,
                                             Map<String, String> inspectionTaskStateMap,
                                             Map<String, String> workTypeMap,
                                             List<RepairPoolOrgRel> allRepairPoolOrgRels,
                                             Map<String, List<StationDTO>> allRepairPoolStationRels) {
        // 使用线程池并发处理查询结果
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("repair-pool-%d").build();
        ExecutorService repairPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
        List<Future<RepairPool>> futureList = new ArrayList<>();
        if (CollUtil.isNotEmpty(result)) {
            result.forEach(repair -> {
                // 提交线程任务
                Future<RepairPool> submit = repairPool.submit(new PoolThreadService(repair, sysBaseApi, manager, repairPoolStationRelMapper, baseMapper, cycleTypeMap, inspectionTaskStateMap, workTypeMap, allRepairPoolOrgRels, allRepairPoolStationRels));
                futureList.add(submit);
            });

            // 确认每个线程都执行完成
            for (Future<RepairPool> fut : futureList) {
                try {
                    fut.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            // 关闭线程池
            try {
                repairPool.shutdown();
                // 等待所有任务结束后返回（超时时间为5秒）
                if (!repairPool.awaitTermination(5 * 1000, TimeUnit.MILLISECONDS)) {
                    // 超时后中止线程池中全部的线程的执行
                    repairPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
                log.error("awaitTermination interrupted:{}", e);
                repairPool.shutdownNow();
            }
        }
    }

    /**
     * 获取时间范围和周数
     *
     * @param year 年份
     * @return
     */
    @Override
    @SneakyThrows
    public Result getTimeInfo(Integer year) {
        LocalDateTime yearFirst = DateUtils.getYearFirst(year);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = yearFirst.atZone(zoneId);
        Date date = Date.from(zonedDateTime.toInstant());
        ArrayList<Object> list = DateUtils.getWeekAndTime(date);
        return Result.ok(list);
    }

    /**
     * 通过检修计划id查看检修标准详情
     *
     * @param req
     * @return
     */
    @Override
    public RepairStrategyDTO queryStandardById(RepairStrategyReq req) {
        RepairStrategyDTO result = new RepairStrategyDTO();
        if (StrUtil.isNotEmpty(req.getStandardId())) {

            LambdaQueryWrapper<RepairPoolCode> poolCodeQueryWrapper = new LambdaQueryWrapper<>();
            poolCodeQueryWrapper.eq(RepairPoolCode::getId, req.getStandardId());
            poolCodeQueryWrapper.eq(RepairPoolCode::getDelFlag, CommonConstant.DEL_FLAG_0);

            RepairPoolCode repairPoolCode = repairPoolCodeMapper.selectOne(poolCodeQueryWrapper);
            if (ObjectUtil.isNotEmpty(repairPoolCode)) {

                // 设备清单
                List<RepairDeviceDTO> repairDeviceDTOList = new ArrayList<>();
                RepairPoolRel repairPoolRel = relMapper.selectOne(new LambdaQueryWrapper<RepairPoolRel>()
                        .eq(RepairPoolRel::getRepairPoolCode, req.getCode())
                        .eq(RepairPoolRel::getRepairPoolStaId, req.getStandardId())
                        .eq(RepairPoolRel::getDelFlag, CommonConstant.DEL_FLAG_0));
                if (ObjectUtil.isNotEmpty(repairPoolRel)) {
                    List<RepairPoolDeviceRel> repairPoolDeviceRels = repairPoolDeviceRel.selectList(
                            new LambdaQueryWrapper<RepairPoolDeviceRel>()
                                    .eq(RepairPoolDeviceRel::getRepairPoolRelId, repairPoolRel.getId()));
                    if (CollUtil.isNotEmpty(repairPoolDeviceRels)) {
                        List<String> deviceCodes = repairPoolDeviceRels.stream().map(RepairPoolDeviceRel::getDeviceCode).collect(Collectors.toList());
                        repairDeviceDTOList = manager.queryDeviceByCodes(deviceCodes);
                    }
                }
                result.setRepairDeviceDTOList(repairDeviceDTOList);

                // 基本信息
                result.setCode(repairPoolCode.getCode());
                result.setTitle(repairPoolCode.getTitle());
                result.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairPoolCode.getType())));
                result.setMajorName(manager.translateMajor(Arrays.asList(repairPoolCode.getMajorCode()), InspectionConstant.MAJOR));
                result.setSubsystemName(manager.translateMajor(Arrays.asList(repairPoolCode.getSubsystemCode()), InspectionConstant.SUBSYSTEM));
                result.setDeviceTypeName(manager.queryNameByCode(repairPoolCode.getDeviceTypeCode()));
                result.setIsAppointDevice(CollUtil.isNotEmpty(repairDeviceDTOList) ? "是" : "否");
                result.setIsAppointDeviceType(sysBaseApi.translateDict(DictConstant.IS_APPOINT_DEVICE, String.valueOf(repairPoolCode.getIsAppointDevice())));

                // 检修项清单
                result.setRepairPoolCodeContentList(selectCodeContentList(repairPoolCode.getId()));
            }
        }
        return result;
    }

    /**
     * 检修计划详情查询检修项清单
     *
     * @param id 检修标准id
     * @return 构造树形
     */
    @Override
    public List<RepairPoolCodeContent> selectCodeContentList(String id) {
        if (StrUtil.isEmpty(id)) {
            return new ArrayList<>();
        }
        List<RepairPoolCodeContent> result = repairPoolCodeContentMapper.selectList(
                new LambdaQueryWrapper<RepairPoolCodeContent>()
                        .eq(RepairPoolCodeContent::getRepairPoolCodeId, id)
                        .eq(RepairPoolCodeContent::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .orderByAsc(RepairPoolCodeContent::getSortNo));

        if (CollUtil.isNotEmpty(result)) {
            result.forEach(r -> {
                r.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_PROJECT, String.valueOf(r.getType())));
                r.setStatusItemName(sysBaseApi.translateDict(DictConstant.INSPECTION_STATUS_ITEM, String.valueOf(r.getStatusItem())));
                r.setInspectionTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_VALUE, String.valueOf(r.getInspectionType())));
            });
        }

        // 构造树形结构
        return treeFirst(result);
    }

    /**
     * 构造树，不固定根节点
     *
     * @param list 全部数据
     * @return 构造好以后的树形
     */
    public static List<RepairPoolCodeContent> treeFirst(List<RepairPoolCodeContent> list) {
        Map<String, RepairPoolCodeContent> map = new HashMap<>(50);
        for (RepairPoolCodeContent treeNode : list) {
            map.put(treeNode.getId(), treeNode);
        }
        return addChildren(list, map);
    }

    /**
     * 递归子节点
     *
     * @param list
     * @param map
     * @return
     */
    private static List<RepairPoolCodeContent> addChildren
    (List<RepairPoolCodeContent> list, Map<String, RepairPoolCodeContent> map) {
        List<RepairPoolCodeContent> rootNodes = new ArrayList<>();
        for (RepairPoolCodeContent treeNode : list) {
            RepairPoolCodeContent parentHave = map.get(treeNode.getPid());
            if (ObjectUtil.isEmpty(parentHave)) {
                rootNodes.add(treeNode);
            } else {
                // 当前位置显示实体类中的List元素定义的参数为null，出现空指针异常错误
                if (ObjectUtil.isEmpty(parentHave.getChildren())) {
                    parentHave.setChildren(new ArrayList<RepairPoolCodeContent>());
                    parentHave.getChildren().add(treeNode);
                } else {
                    parentHave.getChildren().add(treeNode);
                }
            }
        }
        return rootNodes;
    }

    /**
     * 通过检修计划id查看详情
     *
     * @param id
     * @return
     */
    @Override
    public RepairPoolDetailsDTO queryById(String id) {
        RepairPool repairPool = baseMapper.selectById(id);
        RepairPoolDetailsDTO re = new RepairPoolDetailsDTO();
        if (ObjectUtil.isNotEmpty(repairPool)) {
            String code = repairPool.getCode();

            // 根据检修计划单号查询对应的检修标准
            List<RepairPoolCode> repairPoolCodes = queryStandardByCode(code);

            if (CollUtil.isNotEmpty(repairPoolCodes)) {
                // 专业
                re.setMajorName(manager.translateMajor(repairPoolCodes.stream().map(RepairPoolCode::getMajorCode).collect(Collectors.toList()), InspectionConstant.MAJOR));
                // 子系统
                re.setSubsystemName(manager.translateMajor(repairPoolCodes.stream().map(RepairPoolCode::getSubsystemCode).collect(Collectors.toList()), InspectionConstant.SUBSYSTEM));
            }

            // 开始时间
            re.setStartTime(repairPool.getStartTime());
            // 结束时间
            re.setEndTime(repairPool.getEndTime());
            // 计划名称
            re.setName(repairPool.getName());
            // 计划编码
            re.setCode(repairPool.getCode());
            // 所属周（相对年）
            re.setWeeks(String.valueOf(repairPool.getWeeks()));
            if (repairPool.getStartTime() != null && repairPool.getWeeks() != null) {
                Date[] dateByWeek = DateUtils.getDateByWeek(DateUtil.year(repairPool.getStartTime()), repairPool.getWeeks());
                if (dateByWeek.length != 0) {
                    String weekName = String.format("第%d周(%s~%s)", repairPool.getWeeks(), DateUtil.format(dateByWeek[0], "yyyy/MM/dd"), DateUtil.format(dateByWeek[1], "yyyy/MM/dd"));
                    // 计划所属周
                    re.setWeeks(weekName);
                }
            }
            // 退回理由
            re.setRemark(repairPool.getRemark());
            // 状态
            re.setStatus(repairPool.getStatus());
            // 作业类型
            re.setWorkType(repairPool.getWorkType());
            // 站点
            List<StationDTO> repairPoolStationRels = repairPoolStationRelMapper.selectStationList(code);
            re.setStationName(manager.translateStation(repairPoolStationRels));
            // 周期类型
            re.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairPool.getType())));
            // 状态
            re.setStatusName(sysBaseApi.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repairPool.getStatus())));

            // 组织机构
            List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(new LambdaQueryWrapper<RepairPoolOrgRel>()
                    .eq(RepairPoolOrgRel::getRepairPoolCode, code)
                    .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
            List<String> orgList = new ArrayList<>();
            if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
                orgList = repairPoolOrgRels.stream().map(r -> r.getOrgCode()).collect(Collectors.toList());
                re.setOrgName(manager.translateOrg(orgList));
            }

            // 年份
            re.setYear(ObjectUtil.isNotEmpty(repairPool.getStartTime()) ? DateUtil.year(repairPool.getStartTime()) : null);
            // 所属策略
            InspectionStrategy inspectionStrategy = inspectionStrategyMapper.selectOne(new QueryWrapper<InspectionStrategy>()
                    .eq("code", repairPool.getInspectionStrCode())
                    .eq("del_flag", CommonConstant.DEL_FLAG_0));
            re.setStrategy(ObjectUtil.isNotEmpty(inspectionStrategy) ? inspectionStrategy.getName() : "");

            // 是否审核
            re.setIsConfirm(sysBaseApi.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(repairPool.getIsConfirm())));

            // 是否审核
            re.setIsReceipt(sysBaseApi.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(repairPool.getIsReceipt())));

            // 作业类型
            re.setWorkTypeName(sysBaseApi.translateDict(DictConstant.WORK_TYPE, String.valueOf(repairPool.getWorkType())));

            // 是否委外
            re.setIsOutsource(sysBaseApi.translateDict(DictConstant.INSPECTION_IS_MANUAL, String.valueOf(repairPool.getIsOutsource())));

            // 查询该计划对应的任务
            RepairTask repairTask = repairTaskMapper.selectOne(new LambdaQueryWrapper<RepairTask>()
                    .eq(RepairTask::getRepairPoolId, repairPool.getId())
                    .eq(RepairTask::getDelFlag, CommonConstant.DEL_FLAG_0));
            // 任务提交人
            if (ObjectUtil.isNotEmpty(repairTask)) {
                re.setSubmitUserName(repairTask.getSumitUserName());
                re.setConfirmUrl(repairTask.getConfirmUrl());
            }

        }
        return re;
    }

    /**
     * 检修计划池-调整时间
     *
     * @param ids
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Result updateTime(String ids, String startTime, String endTime) {
        String[] split = ids.split(",");
        for (String id : split) {
            int week = DateUtils.getWeekOfYear(DateUtil.parse(startTime));
            RepairPool repairPool = this.baseMapper.selectById(id);

            if (InspectionConstant.WEEKLY_INSPECTION.equals(repairPool.getType())) {
                throw new AiurtBootException("周检无法修改时间");
            }
            repairPool.setWeeks(week);
            repairPool.setStartTime(DateUtil.parse(startTime.concat(" 00:00:00")));
            repairPool.setEndTime(DateUtil.parse(endTime.concat(" 23:59:59")));
            this.baseMapper.updateById(repairPool);
        }
        return Result.ok();
    }

    /**
     * 检修详情里的适用专业和专业子系统级联下拉列表
     *
     * @param code
     * @return
     */
    @Override
    public List<MajorDTO> queryMajorList(String code) {
        List<MajorDTO> result = new ArrayList<>();
        // 根据检修任务code查询关联的标准
        List<RepairPoolRel> repairPoolRels = relMapper.selectList(
                new LambdaQueryWrapper<RepairPoolRel>()
                        .eq(RepairPoolRel::getRepairPoolCode, code)
                        .eq(RepairPoolRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> rid = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            List<RepairPoolCode> repairPoolCodes = repairPoolCodeMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolCode>()
                            .eq(RepairPoolCode::getDelFlag, CommonConstant.DEL_FLAG_0)
                            .in(RepairPoolCode::getId, rid));

            // 查询并处理标准对应的专业、专业子系统
            if (CollUtil.isNotEmpty(repairPoolCodes)) {
                Set<String> majorCode = repairPoolCodes.stream().map(RepairPoolCode::getMajorCode).collect(Collectors.toSet());
                Set<String> subSystemCode = repairPoolCodes.stream().map(RepairPoolCode::getSubsystemCode).collect(Collectors.toSet());
                if (CollUtil.isNotEmpty(majorCode) && CollUtil.isNotEmpty(subSystemCode)) {
                    result = baseMapper.queryMajorList(majorCode, subSystemCode);
                }
            }
        }
        return result;
    }


    /**
     * 指派检修任务
     *
     * @param assignDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result assigned(AssignDTO assignDTO) {
        // 校验
        check(assignDTO);

        List<String> ids = assignDTO.getIds();

        for (String id : ids) {
            // 修改检修计划状态
            RepairPool repairPool = baseMapper.selectById(id);
            if (ObjectUtil.isEmpty(repairPool)) {
                throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
            }

            // 是手动任务需要更新开始时间和结束时间
            if (assignDTO.getIsManual().equals(InspectionConstant.IS_MANUAL_1)) {
                repairPool.setStartTime(assignDTO.getStartTime());
                repairPool.setEndTime(assignDTO.getEndTime());
            }

            repairPool.setStatus(InspectionConstant.TO_BE_CONFIRMED);
            baseMapper.updateById(repairPool);

            // 添加检修任务
            RepairTask repairTask = createRepairTask(assignDTO, repairPool);
            repairTaskMapper.insert(repairTask);

            // 保存站点关联信息
            saveRepairTaskStationRel(repairPool);

            // 保存检修人信息
            saveRepairTaskUser(assignDTO, repairPool);

            // 保存组织机构信息
            saveRepairTaskOrgRel(repairPool);

            // 生成检修标准关联、检修设备清单、检修结果信息
            this.generate(repairPool, repairTask.getId(), repairPool.getCode());

            // 发送消息给对用的检修人
            // 提交任务到线程池
            AsyncThreadPoolExecutorUtil.getExecutor().submitTask(() -> {
                // 调用发送消息方法
                this.sendMessage(assignDTO.getUserIds(), repairPool.getCode());
                return null;
            });

        }
        return Result.ok();
    }

    /**
     * 检修消息发送
     *
     * @param userIds
     */
    private void sendMessage(List<String> userIds, String code) {
        if (CollUtil.isNotEmpty(userIds)) {
            // 查找用户id对应的用户username
            String[] strings = userIds.toArray(new String[userIds.size()]);
            List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(strings);
            if (CollUtil.isNotEmpty(loginUsers)) {
                LambdaQueryWrapper<RepairTask> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(RepairTask::getCode, code);
                RepairTask repairTask = repairTaskMapper.selectOne(wrapper);

                String usernames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
                //发送通知
                try {
                    MessageDTO messageDTO = new MessageDTO(manager.checkLogin().getUsername(), usernames, "检修任务-接收" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_5);

                    //构建消息模板
                    HashMap<String, Object> map = new HashMap<>();
                    if (CollUtil.isNotEmpty(messageDTO.getData())) {
                        map.putAll(messageDTO.getData());
                    }
                    map.put("code", repairTask.getCode());
                    String typeName = sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairTask.getType()));
                    map.put("repairTaskName", typeName + repairTask.getCode());
                    List<String> codes = repairTaskMapper.getRepairTaskStation(repairTask.getId());
                    if (CollUtil.isNotEmpty(codes)) {
                        Map<String, String> stationNameByCode = sysBaseApi.getStationNameByCode(codes);
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Map.Entry<String, String> entry : stationNameByCode.entrySet()) {
                            stringBuilder.append(entry.getValue());
                            stringBuilder.append(",");
                        }
                        if (stringBuilder.length() > 0) {
                            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        }
                        map.put("repairStation", stringBuilder.toString());
                    }
                    map.put("repairTaskTime", DateUtil.format(repairTask.getStartTime(), "yyyy-MM-dd HH:mm") + "-" + DateUtil.format(repairTask.getEndTime(), "yyyy-MM-dd HH:mm"));
                    String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                    if (StrUtil.isNotEmpty(realNames)) {
                        map.put("repairName", realNames);
                    }
                    map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, repairTask.getId());
                    map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.INSPECTION_ASSIGN.getType());
                    messageDTO.setData(map);
                    SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.REPAIR_MESSAGE);
                    messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                    messageDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("新的检修任务");
                    messageDTO.setPublishingContent("接收到新的检修任务，请尽快确认");
                    //响铃
                    messageDTO.setIsRingBell(true);
                    sysBaseApi.sendTemplateMessage(messageDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }

    /**
     * 生成检修标准关联、检修设备清单、检修结果信息
     *
     * @param repairPool
     * @param taskId
     */
    @Override
    public void generate(RepairPool repairPool, String taskId, String taskCode) {
        // 检修计划关联检修标准
        List<RepairPoolRel> repairPoolRels = relMapper.selectList(
                new LambdaQueryWrapper<RepairPoolRel>()
                        .eq(RepairPoolRel::getRepairPoolCode, repairPool.getCode())
                        .eq(RepairPoolRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> staIds = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            List<RepairPoolCode> repairPoolCodes = repairPoolCodeMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolCode>()
                            .in(RepairPoolCode::getId, staIds)
                            .eq(RepairPoolCode::getDelFlag, CommonConstant.DEL_FLAG_0));

            // 插入检修标准
            if (CollUtil.isNotEmpty(repairPoolCodes)) {
                repairPoolCodes.forEach(re -> {
                    RepairTaskStandardRel repairTaskStandardRel = new RepairTaskStandardRel();
                    repairTaskStandardRel.setRepairTaskId(taskId);
                    repairTaskStandardRel.setCode(re.getCode());
                    repairTaskStandardRel.setMajorCode(re.getMajorCode());
                    repairTaskStandardRel.setSubsystemCode(re.getSubsystemCode());
                    repairTaskStandardRel.setTitle(re.getTitle());
                    repairTaskStandardRel.setDeviceTypeCode(re.getDeviceTypeCode());
                    repairTaskStandardRel.setIsAppointDevice(re.getIsAppointDevice());
                    repairTaskStandardRelMapper.insert(repairTaskStandardRel);
                    // 生成检修设备清单
                    this.generateInventory(re.getId(), repairTaskStandardRel.getId(), taskId, taskCode, repairTaskStandardRel.getIsAppointDevice());
                });

            }
        }
    }

    /**
     * app指派任务下拉接口
     *
     * @param id 检修计划id
     */
    @Override
    public List<OrgDTO> queryUserDownList(String id) {
        List<OrgDTO> result = new ArrayList<>();

        if (StrUtil.isEmpty(id)) {
            return result;
        }

        // 查询对应的检修计划
        RepairPool repairPool = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(repairPool)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 查询检修计划关联的组织结构
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .eq(RepairPoolOrgRel::getRepairPoolCode, repairPool.getCode())
                        .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            List<String> orgList = repairPoolOrgRels.stream().map(RepairPoolOrgRel::getOrgCode).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(orgList)) {
                // 当前登录人与计划的部门作交集处理
                List<String> list = manager.handleMixedOrgCode(orgList);
                if (CollUtil.isNotEmpty(list)) {
                    String orgStrs = StrUtil.join(",", list);
                    result = manager.queryUserByOrdCode(orgStrs);

                    // 根据配置决定是否关联排班
                    SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.INSPECTION_SCHEDULING);
                    boolean value = "1".equals(paramModel.getValue()) ? true : false;
                    if (value) {
                        // 过滤不是今日当班的人员
                        result = filterNoShiftUser(result, list);
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param orgDTOS
     * @param orgCodes
     * @return
     */
    private List<OrgDTO> filterNoShiftUser(List<OrgDTO> orgDTOS, List<String> orgCodes) {
        // 获取今日当班人员信息
        List<SysUserTeamDTO> todayOndutyDetail = baseApi.getTodayOndutyDetailNoPage(orgCodes, new Date());
        if (CollectionUtil.isEmpty(orgDTOS) || CollectionUtil.isEmpty(todayOndutyDetail)) {
            return orgDTOS;
        }
        List<String> userIds = todayOndutyDetail.stream().map(SysUserTeamDTO::getUserId).collect(Collectors.toList());
        // 获取仅在今日值班的人员
        for (OrgDTO dto : orgDTOS) {
            List<LoginUser> list = Optional.ofNullable(dto.getUsers())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .filter(l -> userIds.contains(l.getId()))
                    .collect(Collectors.toList());
            dto.setUsers(list);
        }
        return orgDTOS;
    }

    /**
     * 生成检修设备清单
     *
     * @param oldStaId        计划中的关联的检修标准id
     * @param newStaId        任务中新的检修标准id
     * @param taskId          任务id
     * @param taskCode        是否与设备类型相关
     * @param isAppointDevice
     */
    private void generateInventory(String oldStaId, String newStaId, String taskId, String taskCode, Integer
            isAppointDevice) {

        // 与设备不相关
        if (InspectionConstant.NO_ISAPPOINT_DEVICE.equals(isAppointDevice)) {
            List<RepairTaskStationRel> repairTaskStationRels = repairTaskStationRelMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskStationRel>()
                            .eq(RepairTaskStationRel::getRepairTaskCode, taskCode)
                            .eq(RepairTaskStationRel::getDelFlag, CommonConstant.DEL_FLAG_0));

            // 每个站点都生成一个清单
            if (CollUtil.isNotEmpty(repairTaskStationRels)) {
                repairTaskStationRels.forEach(re -> {
                    String jxdCode = CodeGenerateUtils.generateCode("JXD");
                    RepairTaskDeviceRel repairTaskDeviceRel = new RepairTaskDeviceRel();
                    repairTaskDeviceRel.setCode(jxdCode);
                    repairTaskDeviceRel.setRepairTaskId(taskId);
                    repairTaskDeviceRel.setTaskStandardRelId(newStaId);
                    repairTaskDeviceRel.setLineCode(re.getLineCode());
                    repairTaskDeviceRel.setStationCode(re.getStationCode());
                    repairTaskDeviceRel.setPositionCode(re.getPositionCode());
                    repairTaskDeviceRelMapper.insert(repairTaskDeviceRel);

                    // 生成检修结果表
                    this.generateItemResult(oldStaId, repairTaskDeviceRel.getId());
                });
            }
        }

        // 与设备相关
        if (InspectionConstant.IS_APPOINT_DEVICE.equals(isAppointDevice)) {
            RepairPoolRel repairPoolRels = relMapper.selectOne(
                    new LambdaQueryWrapper<RepairPoolRel>()
                            .eq(RepairPoolRel::getRepairPoolCode, taskCode)
                            .eq(RepairPoolRel::getRepairPoolStaId, oldStaId)
                            .eq(RepairPoolRel::getDelFlag, CommonConstant.DEL_FLAG_0));

            if (ObjectUtil.isNotEmpty(repairPoolRels)) {
                List<RepairPoolDeviceRel> repairPoolDeviceRels = repairPoolDeviceRel.selectList(
                        new LambdaQueryWrapper<RepairPoolDeviceRel>()
                                .eq(RepairPoolDeviceRel::getRepairPoolRelId, repairPoolRels.getId()));

                if (CollUtil.isNotEmpty(repairPoolDeviceRels)) {
                    // 与设备相关并且已经指定了设备
                    List<String> deviceCodeList = repairPoolDeviceRels.stream().map(RepairPoolDeviceRel::getDeviceCode).collect(Collectors.toList());

                    // 插入设备清单
                    for (String deviceCode : deviceCodeList) {
                        RepairTaskDeviceRel repairTaskDeviceRel = new RepairTaskDeviceRel();
                        String jxdCode = CodeGenerateUtils.generateCode("JXD");
                        repairTaskDeviceRel.setCode(jxdCode);
                        repairTaskDeviceRel.setDeviceCode(deviceCode);
                        repairTaskDeviceRel.setRepairTaskId(taskId);
                        repairTaskDeviceRel.setTaskStandardRelId(newStaId);
                        repairTaskDeviceRelMapper.insert(repairTaskDeviceRel);
                        // 生成检修结果表
                        this.generateItemResult(oldStaId, repairTaskDeviceRel.getId());
                    }
                }
            }
        }
    }

    /**
     * 生成检修结果表
     *
     * @param staId 检修标准id
     * @param id    检修设备清单id
     */
    private void generateItemResult(String staId, String id) {
        List<RepairPoolCodeContent> repairPoolCodeContents = repairPoolCodeContentMapper.selectList(
                new LambdaQueryWrapper<RepairPoolCodeContent>()
                        .eq(RepairPoolCodeContent::getRepairPoolCodeId, staId)
                        .eq(RepairPoolCodeContent::getDelFlag, CommonConstant.DEL_FLAG_0));

        // <K,V> K是旧的id,V新生成的id
        HashMap<String, String> map = new HashMap<>(32);

        if (CollUtil.isNotEmpty(repairPoolCodeContents)) {
            for (RepairPoolCodeContent repairPoolCodeContent : repairPoolCodeContents) {
                RepairTaskResult repairTaskResult = new RepairTaskResult();
                repairTaskResult.setTaskDeviceRelId(id);
                repairTaskResult.setDataCheck(repairPoolCodeContent.getDataCheck());
                repairTaskResult.setHasChild(repairPoolCodeContent.getHasChild());
                repairTaskResult.setDictCode(repairPoolCodeContent.getDictCode());
                repairTaskResult.setQualityStandard(repairPoolCodeContent.getQualityStandard());
                repairTaskResult.setName(repairPoolCodeContent.getName());
                repairTaskResult.setStatusItem(repairPoolCodeContent.getStatusItem());
                repairTaskResult.setSortNo(repairPoolCodeContent.getSortNo());
                repairTaskResult.setType(repairPoolCodeContent.getType());
                repairTaskResult.setCode(repairPoolCodeContent.getCode());
                repairTaskResult.setPid(repairPoolCodeContent.getPid());
                repairTaskResult.setInspectionType(repairPoolCodeContent.getInspectionType());

                // 插入检修结果表
                repairTaskResultMapper.insert(repairTaskResult);
                map.put(repairPoolCodeContent.getId(), repairTaskResult.getId());
            }

            // 更新pid
            List<RepairTaskResult> repairTaskResults = repairTaskResultMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskResult>()
                            .eq(RepairTaskResult::getTaskDeviceRelId, id)
                            .ne(RepairTaskResult::getPid, 0)
                            .eq(RepairTaskResult::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (CollUtil.isNotEmpty(repairTaskResults)) {
                for (RepairTaskResult repairTaskResult : repairTaskResults) {
                    repairTaskResult.setPid(map.get(repairTaskResult.getPid()));
                    repairTaskResultMapper.updateById(repairTaskResult);
                }
            }
        }
    }

    /**
     * 校验指派任务时的实体信息
     *
     * @param assignDTO
     */
    private void check(AssignDTO assignDTO) {
        if (ObjectUtil.isEmpty(assignDTO)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        List<String> ids = assignDTO.getIds();
        if (CollUtil.isEmpty(ids)
                || CollUtil.isEmpty(assignDTO.getUserIds())
                || ObjectUtil.isEmpty(assignDTO.getStartTime())
                || ObjectUtil.isEmpty(assignDTO.getEndTime())
                || assignDTO.getIsManual() == null) {
            throw new AiurtBootException(InspectionConstant.INCOMPLETE_PARAMETERS);
        }

        // 是否已经被指派过
        ids.forEach(id -> {
            RepairPool repairPool = baseMapper.selectById(id);
            if (ObjectUtil.isNotEmpty(repairPool)
                    && (!InspectionConstant.TO_BE_ASSIGNED.equals(repairPool.getStatus()) && !InspectionConstant.GIVE_BACK.equals(repairPool.getStatus()))) {
                throw new AiurtBootException(String.format("检修计划名称为%s已被指派任务，请勿重复指派", repairPool.getName()));
            }
        });

        // 多条同时指派需要保证计划关联的组织机构都是一样的
        // todo 校验计划令编码必填
    }

    /**
     * 指派检修任务人员下拉列表
     *
     * @return
     */
    @Override
    public List<LoginUser> queryUserList(String code) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录系统，请登录后操作！");
        }
        List<LoginUser> result = new ArrayList<>();
        if (StrUtil.isEmpty(code)) {
            return result;
        }
        // 获取当前登录人的部门权限
        List<CsUserDepartModel> departList = sysBaseApi.getDepartByUserId(loginUser.getId());
        List<String> userOrgCodes = departList.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(userOrgCodes)) {
            return result;
        }
        // 当前计划关联的组织机构里的人员
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .eq(RepairPoolOrgRel::getRepairPoolCode, code)
                        .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            List<String> orgCodes = repairPoolOrgRels.stream().map(RepairPoolOrgRel::getOrgCode).collect(Collectors.toList());
            // 当前登录人的部门权限和任务的组织机构交集
            List<String> intersectOrg = CollectionUtil.intersection(userOrgCodes, orgCodes).stream().collect(Collectors.toList());
            if (CollectionUtil.isEmpty(intersectOrg)) {
                return Collections.emptyList();
            }
            result = sysBaseApi.getUserByDepIds(manager.handleMixedOrgCode(orgCodes));
            // 根据配置决定是否关联排班
            SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.INSPECTION_SCHEDULING);
            boolean value = "1".equals(paramModel.getValue()) ? true : false;
            if (value) {
                // 获取今日当班人员信息
                List<SysUserTeamDTO> todayOndutyDetail = baseApi.getTodayOndutyDetailNoPage(intersectOrg, new Date());
                if (CollectionUtil.isEmpty(todayOndutyDetail)) {
                    return Collections.emptyList();
                }
                List<String> userIds = todayOndutyDetail.stream().map(SysUserTeamDTO::getUserId).collect(Collectors.toList());
                // 过滤仅在今日当班的待指派人员
                result = result.stream().filter(l -> userIds.contains(l.getId())).collect(Collectors.toList());
            }
        }
        return result;
    }

    /**
     * 检修详情里的检修标准下拉列表
     *
     * @param code       检修计划单号
     * @param majorCode  专业编码
     * @param systemCode 专业子系统编码
     * @return
     */
    @Override
    public List<StandardNewDTO> queryStandardList(String code, String majorCode, String systemCode) {
        List<StandardNewDTO> result = new ArrayList<>();
        if (StrUtil.isEmpty(code)) {
            return result;
        }

        LambdaQueryWrapper<RepairPoolRel> lambdaQueryWrapper = new LambdaQueryWrapper<RepairPoolRel>();
        lambdaQueryWrapper.eq(RepairPoolRel::getRepairPoolCode, code);
        lambdaQueryWrapper.eq(RepairPoolRel::getDelFlag, CommonConstant.DEL_FLAG_0);

        List<RepairPoolRel> repairPoolRels = relMapper.selectList(lambdaQueryWrapper);

        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> codeList = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(codeList)) {

                LambdaQueryWrapper<RepairPoolCode> poolCodeLambdaQueryWrapper = new LambdaQueryWrapper<RepairPoolCode>();
                poolCodeLambdaQueryWrapper.in(RepairPoolCode::getId, codeList);
                poolCodeLambdaQueryWrapper.eq(RepairPoolCode::getDelFlag, CommonConstant.DEL_FLAG_0);
                if (StrUtil.isNotEmpty(majorCode)) {
                    poolCodeLambdaQueryWrapper.eq(RepairPoolCode::getMajorCode, majorCode);
                }
                if (StrUtil.isNotEmpty(systemCode)) {
                    poolCodeLambdaQueryWrapper.eq(RepairPoolCode::getSubsystemCode, systemCode);
                }
                List<RepairPoolCode> repairPoolCodes = repairPoolCodeMapper.selectList(poolCodeLambdaQueryWrapper);

                if (CollUtil.isNotEmpty(repairPoolCodes)) {
                    for (RepairPoolCode repairPoolCode : repairPoolCodes) {
                        StandardNewDTO standardDTO = new StandardNewDTO();
                        standardDTO.setId(repairPoolCode.getId());
                        standardDTO.setName(repairPoolCode.getTitle());
                        result.add(standardDTO);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 分页查询手工下发的检修任务列表
     *
     * @param page          分页对象，包含当前页码和每页显示的记录数
     * @param manualTaskReq 查询条件对象，包含检修任务单号、检修类型、状态等筛选条件
     * @return IPage<RepairPool> 结果对象，包含分页后的检修任务列表及分页信息
     */
    @Override
    public IPage<RepairPool> listPage(Page<RepairPool> page, ManualTaskReq manualTaskReq) {
        // 构造查询条件
        SelectPlanReq selectPlanReq = new SelectPlanReq();
        selectPlanReq.setIsManual(InspectionConstant.IS_MANUAL_1);
        selectPlanReq.setIsManualSign(true);
        selectPlanReq.setStationCodeList(StrUtil.split(manualTaskReq.getStationList(), ','));
        selectPlanReq.setOrgCodeList(StrUtil.split(manualTaskReq.getOrgList(), ','));
        selectPlanReq.setCode(manualTaskReq.getCode());
        selectPlanReq.setType(manualTaskReq.getType());
        selectPlanReq.setStartTime(manualTaskReq.getStartTime());
        selectPlanReq.setEndTime(manualTaskReq.getEndTime());
        if (manualTaskReq.getStatus() != null) {
            selectPlanReq.setStatusList(Arrays.asList(String.valueOf(manualTaskReq.getStatus())));
        }

        // 执行查询
        List<RepairPool> result = baseMapper.selectRepairPool(page, selectPlanReq);

        if (CollUtil.isEmpty(result)) {
            return page;
        }

        boolean filterFlag = GlobalThreadLocal.setDataFilter(false);

        // 获取所有 repairPool 的 code 列表
        List<String> repairPoolCodes = result.stream().map(RepairPool::getCode).collect(Collectors.toList());

        // 查询所有相关的组织机构
        List<RepairPoolOrgRel> allRepairPoolOrgRels = getAllRepairPoolOrgRels(repairPoolCodes);

        // 查询所有相关的站点
        Map<String, List<StationDTO>> allRepairPoolStationRels = getAllRepairPoolStationRels(repairPoolCodes);

        // 检修任务状态
        Map<String, String> inspectionTaskStateMap = getInspectionTaskStateMap();

        result.forEach(re -> {
            // 组织机构
            List<String> orgList = allRepairPoolOrgRels.stream()
                    .filter(orgRel -> orgRel.getRepairPoolCode().equals(re.getCode()))
                    .map(RepairPoolOrgRel::getOrgCode)
                    .collect(Collectors.toList());
            re.setOrgName(manager.translateOrg(orgList));

            // 站点
            List<StationDTO> repairPoolStationRels = allRepairPoolStationRels.getOrDefault(re.getCode(), Collections.emptyList());
            re.setStationName(manager.translateStation(repairPoolStationRels));

            //检修任务状态
            re.setStatusName(inspectionTaskStateMap.get(String.valueOf(re.getStatus())));
        });
        GlobalThreadLocal.setDataFilter(filterFlag);
        return page.setRecords(result);
    }

    /**
     * 添加手工下发检修任务
     *
     * @param repairPoolDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addManualTask(RepairPoolReq repairPoolDTO) {
        // 校验必填信息
        chekcRepairPoolDTO(repairPoolDTO);

        RepairPool repairPool = new RepairPool();
        String jxCode = CodeGenerateUtils.generateCode("JX");
        repairPool.setCode(jxCode);
        UpdateHelperUtils.copyNullProperties(repairPoolDTO, repairPool);

        // 保存任务信息
        baseMapper.insert(repairPool);

        // 保存站点
        List<StationDTO> addStationCode = repairPoolDTO.getAddStationCode();
        if (CollUtil.isNotEmpty(addStationCode)) {
            addStationCode.forEach(re -> {
                RepairPoolStationRel repairPoolStationRel = new RepairPoolStationRel();
                repairPoolStationRel.setRepairPoolCode(repairPool.getCode());
                UpdateHelperUtils.copyNullProperties(re, repairPoolStationRel);
                repairPoolStationRelMapper.insert(repairPoolStationRel);
            });
        }

        // 保存组织机构
        List<String> orgCodes = repairPoolDTO.getOrgCodes();
        if (CollUtil.isNotEmpty(orgCodes)) {
            orgCodes.forEach(org -> {
                RepairPoolOrgRel repairPoolOrgRel = RepairPoolOrgRel.builder()
                        .orgCode(org)
                        .repairPoolCode(repairPool.getCode())
                        .build();
                orgRelMapper.insert(repairPoolOrgRel);
            });
        }

        // 处理检修标准、检修项目、检修设备、检修计划与检修标准的关联关系
        List<RepairPoolCodeReq> repairPoolCodes = repairPoolDTO.getRepairPoolCodes();
        // 处理：只保存对应站点下的设备
        if (CollUtil.isNotEmpty(addStationCode)) {
            List<String> stationCodes = addStationCode.stream().map(StationDTO::getStationCode).collect(Collectors.toList());
            handleDevice(stationCodes, repairPoolCodes);
        }

        handle(jxCode, repairPoolCodes);
    }

    /**
     * 过滤出stationCodes站点下的设备
     *
     * @param stationCodes
     * @param repairPoolCodes
     */
    private void handleDevice(List<String> stationCodes, List<RepairPoolCodeReq> repairPoolCodes) {
        if (CollUtil.isNotEmpty(repairPoolCodes) && CollUtil.isNotEmpty(stationCodes)) {

            for (RepairPoolCodeReq repairPoolCode : repairPoolCodes) {
                InspectionCode inspectionCode = inspectionCodeMapper.selectOne(
                        new LambdaQueryWrapper<InspectionCode>()
                                .eq(InspectionCode::getCode, repairPoolCode.getCode())
                                .eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));

                if (ObjectUtil.isNotEmpty(inspectionCode)
                        && InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionCode.getIsAppointDevice())) {
                    // 获取前端传输的设备
                    List<String> deviceCodes = repairPoolCode.getDeviceCodes();
                    List<RepairDeviceDTO> repairDeviceDTOS = manager.queryDeviceByCodes(deviceCodes);

                    // 根据站点过滤设备
                    if (CollUtil.isNotEmpty(repairDeviceDTOS)) {
                        List<String> result = repairDeviceDTOS.stream()
                                .filter(stationCode -> stationCodes.contains(stationCode.getStationCode()))
                                .map(RepairDeviceDTO::getCode)
                                .collect(Collectors.toList());
                        repairPoolCode.setDeviceCodes(new ArrayList<>());
                        repairPoolCode.setDeviceCodes(result);
                        if (CollUtil.isEmpty(repairPoolCode.getDeviceCodes())) {
                            throw new AiurtBootException("有检修标准未指定设备");
                        }
                    } else {
                        throw new AiurtBootException("有检修标准未指定设备");
                    }
                }

            }
        }
    }


    /**
     * 处理检修标准、检修项目、检修设备、检修计划与检修标准的关联关系
     *
     * @param jx              计划单号
     * @param repairPoolCodes 检修标准
     */
    @Override
    public void handle(String jx, List<RepairPoolCodeReq> repairPoolCodes) {
        if (CollUtil.isNotEmpty(repairPoolCodes)) {
            // <K,V> K是旧的id,V新生成的id，用来维护复制检修项目时的pid更新
            HashMap<String, String> map = new HashMap<>(48);

            repairPoolCodes.forEach(re -> {
                InspectionCode inspectionCode = inspectionCodeMapper.selectOne(
                        new LambdaQueryWrapper<InspectionCode>()
                                .eq(InspectionCode::getCode, re.getCode())
                                .eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));

                if (ObjectUtil.isNotEmpty(inspectionCode)) {
                    // 保存检修标准
                    RepairPoolCode repairPoolCode = new RepairPoolCode();
                    UpdateHelperUtils.copyNullProperties(inspectionCode, repairPoolCode);
                    repairPoolCode.setId(null);
                    repairPoolCodeMapper.insert(repairPoolCode);
                    String staId = repairPoolCode.getId();

                    // 查询旧的检修标准对应的检修项目
                    List<InspectionCodeContent> inspectionCodeContentList = inspectionCodeContentMapper.selectList(
                            new LambdaQueryWrapper<InspectionCodeContent>()
                                    .eq(InspectionCodeContent::getInspectionCodeId, inspectionCode.getId())
                                    .eq(InspectionCodeContent::getDelFlag, CommonConstant.DEL_FLAG_0));

                    if (CollUtil.isNotEmpty(inspectionCodeContentList)) {
                        inspectionCodeContentList.forEach(ins -> {
                            RepairPoolCodeContent repairPoolCodeContent = new RepairPoolCodeContent();
                            UpdateHelperUtils.copyNullProperties(ins, repairPoolCodeContent);
                            repairPoolCodeContent.setRepairPoolCodeId(staId);
                            repairPoolCodeContent.setId(null);

                            // 保存检修检查项
                            repairPoolCodeContentMapper.insert(repairPoolCodeContent);

                            // 记录旧的检修检查项id和新的检修检查项id
                            map.put(ins.getId(), repairPoolCodeContent.getId());
                        });

                        // 更新新检修检查项的pid
                        List<RepairPoolCodeContent> repairPoolCodeContents = repairPoolCodeContentMapper.selectList(
                                new LambdaQueryWrapper<RepairPoolCodeContent>()
                                        .eq(RepairPoolCodeContent::getRepairPoolCodeId, staId)
                                        .ne(RepairPoolCodeContent::getPid, 0)
                                        .eq(RepairPoolCodeContent::getDelFlag, CommonConstant.DEL_FLAG_0));
                        if (CollUtil.isNotEmpty(repairPoolCodeContents)) {
                            for (RepairPoolCodeContent repairPoolCodeContent : repairPoolCodeContents) {
                                repairPoolCodeContent.setPid(map.get(repairPoolCodeContent.getPid()));
                                repairPoolCodeContentMapper.updateById(repairPoolCodeContent);
                            }
                        }
                    }

                    // 保存检修计划关联标准信息
                    RepairPoolRel repairPoolRel = RepairPoolRel.builder()
                            .repairPoolCode(jx)
                            .repairPoolStaId(staId)
                            .build();
                    relMapper.insert(repairPoolRel);
                    String relId = repairPoolRel.getId();

                    // 保存检修标准对应的设备
                    List<String> repairDeviceDTOList = re.getDeviceCodes();
                    if (CollUtil.isNotEmpty(repairDeviceDTOList)) {
                        repairDeviceDTOList.forEach(red -> {
                            RepairPoolDeviceRel poolDeviceRel = RepairPoolDeviceRel.builder()
                                    .deviceCode(red)
                                    .repairPoolRelId(relId)
                                    .build();
                            repairPoolDeviceRel.insert(poolDeviceRel);
                        });
                    }
                }
            });
        }
    }

    /**
     * 校验必填信息
     *
     * @param repairPoolDTO
     */
    private void chekcRepairPoolDTO(RepairPoolReq repairPoolDTO) {
        if (ObjectUtil.isEmpty(repairPoolDTO)) {
            throw new AiurtBootException("必填参数为空");
        }

        if (CollUtil.isEmpty(repairPoolDTO.getAddStationCode())) {
            throw new AiurtBootException("站点信息为空");
        }

        if (CollUtil.isEmpty(repairPoolDTO.getOrgCodes())) {
            throw new AiurtBootException("组织机构为空");
        }

        List<RepairPoolCodeReq> repairPoolCodes = repairPoolDTO.getRepairPoolCodes();
        if (CollUtil.isEmpty(repairPoolCodes)) {
            throw new AiurtBootException("请选择检修标准");
        }

        // 如果是否需要审核选否，那么直接不接收是否需要验收的参数
        if (repairPoolDTO.getIsConfirm() != null && InspectionConstant.IS_CONFIRM_0.equals(repairPoolDTO.getIsConfirm())) {
            repairPoolDTO.setIsReceipt(null);
        }

        // 不能存在相同设备类型的检修标准
        if (CollUtil.isNotEmpty(repairPoolCodes)) {
            List<String> deviceTypes = new ArrayList<>();

            repairPoolCodes.forEach(re -> {
                InspectionCode inspectionCode = inspectionCodeMapper.selectOne(
                        new LambdaQueryWrapper<InspectionCode>()
                                .eq(InspectionCode::getCode, re.getCode())
                                .eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));
                if (ObjectUtil.isEmpty(inspectionCode)) {
                    throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
                }

                if (StrUtil.isNotEmpty(inspectionCode.getDeviceTypeCode())) {
                    if (deviceTypes.contains(inspectionCode.getDeviceTypeCode())) {
                        throw new AiurtBootException("存在相同设备类型的检修标准");
                    } else {
                        deviceTypes.add(inspectionCode.getDeviceTypeCode());
                    }
                }

                if (InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionCode.getIsAppointDevice()) && CollUtil.isEmpty(re.getDeviceCodes())) {
                    throw new AiurtBootException(String.format("名字为%s需要指定设备", ObjectUtil.isNotEmpty(inspectionCode) ? inspectionCode.getTitle() : ""));
                }
            });
        }
    }

    /**
     * 通过id查询手工下发检修任务信息
     *
     * @param id
     * @return
     */
    @Override
    public RepairPoolDTO queryManualTaskById(String id) {
        RepairPool repairPool = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(repairPool)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        RepairPoolDTO repairPoolDTO = new RepairPoolDTO();
        String code = repairPool.getCode();
        UpdateHelperUtils.copyNullProperties(repairPool, repairPoolDTO);

        // 站点信息
        List<RepairPoolStationRel> repairPoolStationRels = repairPoolStationRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolStationRel>()
                        .eq(RepairPoolStationRel::getRepairPoolCode, code)
                        .eq(RepairPoolStationRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairPoolStationRels)) {
            List<StationDTO> arr = new ArrayList<>();
            repairPoolStationRels.forEach(re -> {
                StationDTO st = new StationDTO();
                st.setStationCode(re.getStationCode());
                st.setPositionCode(re.getPositionCode());
                st.setLineCode(re.getLineCode());
                arr.add(st);
            });
            repairPoolDTO.setAddStationCode(arr);
            repairPoolDTO.setStationCodes(repairPoolStationRels.stream().map(RepairPoolStationRel::getStationCode).collect(Collectors.toList()));
        }

        // 组织机构信息
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .eq(RepairPoolOrgRel::getRepairPoolCode, code)
                        .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            repairPoolDTO.setOrgCodes(repairPoolOrgRels.stream().map(RepairPoolOrgRel::getOrgCode).collect(Collectors.toList()));
        }

        // 检修计划关联检修标准信息
        List<RepairPoolRel> repairPoolRels = relMapper.selectList(
                new LambdaQueryWrapper<RepairPoolRel>()
                        .eq(RepairPoolRel::getRepairPoolCode, code)
                        .eq(RepairPoolRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        // 检修标准信息
        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<RepairPoolCode> temp = new ArrayList<>();
            repairPoolRels.forEach(sl -> {
                RepairPoolCode repairPoolCodes = repairPoolCodeMapper.selectOne(
                        new LambdaQueryWrapper<RepairPoolCode>()
                                .eq(RepairPoolCode::getId, sl.getRepairPoolStaId())
                                .eq(RepairPoolCode::getDelFlag, CommonConstant.DEL_FLAG_0));

                if (ObjectUtil.isNotEmpty(repairPoolCodes)) {

                    // 翻译检修标准字典
                    repairPoolCodes.setMajorName(manager.translateMajor(Arrays.asList(repairPoolCodes.getMajorCode()), InspectionConstant.MAJOR));
                    repairPoolCodes.setSubsystemName(manager.translateMajor(Arrays.asList(repairPoolCodes.getSubsystemCode()), InspectionConstant.SUBSYSTEM));
                    repairPoolCodes.setDeviceTypeName(manager.queryNameByCode(repairPoolCodes.getDeviceTypeCode()));
                    repairPoolCodes.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairPoolCodes.getType())));
                    repairPoolCodes.setIsAppointDeviceName(sysBaseApi.translateDict(DictConstant.IS_APPOINT_DEVICE, String.valueOf(repairPoolCodes.getIsAppointDevice())));

                    // 判断是否指定了设备
                    List<RepairPoolDeviceRel> repairPoolDeviceRels = repairPoolDeviceRel.selectList(
                            new LambdaQueryWrapper<RepairPoolDeviceRel>()
                                    .eq(RepairPoolDeviceRel::getRepairPoolRelId, sl.getId()));
                    if (CollUtil.isNotEmpty(repairPoolDeviceRels)) {
                        repairPoolCodes.setDeviceCodes(repairPoolDeviceRels.stream().map(RepairPoolDeviceRel::getDeviceCode).collect(Collectors.toList()));
                    }
                    repairPoolCodes.setSpecifyDevice(CollUtil.isNotEmpty(repairPoolDeviceRels) ? "是" : "否");
                    repairPoolCodes.setName(ObjectUtil.isNotEmpty(repairPoolCodes.getTitle())?repairPoolCodes.getTitle():null);
                    temp.add(repairPoolCodes);
                }
            });

            repairPoolDTO.setRepairPoolCodes(temp);
        }

        return repairPoolDTO;
    }

    /**
     * 修改手工下发检修任务信息
     *
     * @param repairPoolReq
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateManualTaskById(RepairPoolReq repairPoolReq) {
        // 校验
        checkTask(repairPoolReq);

        // 更新基本信息
        RepairPool repairPool = new RepairPool();
        UpdateHelperUtils.copyNullProperties(repairPoolReq, repairPool);
        baseMapper.updateById(repairPool);

        // 更新站点
        repairPoolStationRelMapper.delete(new LambdaQueryWrapper<RepairPoolStationRel>().eq(RepairPoolStationRel::getRepairPoolCode, repairPoolReq.getCode()));
        List<StationDTO> addStationCode = repairPoolReq.getAddStationCode();
        if (CollUtil.isNotEmpty(addStationCode)) {
            addStationCode.forEach(station -> {
                RepairPoolStationRel repairPoolStationRel = new RepairPoolStationRel();
                repairPoolStationRel.setRepairPoolCode(repairPool.getCode());
                UpdateHelperUtils.copyNullProperties(station, repairPoolStationRel);
                repairPoolStationRelMapper.insert(repairPoolStationRel);
            });
        }

        // 更新组织机构
        orgRelMapper.delete(new LambdaQueryWrapper<RepairPoolOrgRel>().eq(RepairPoolOrgRel::getRepairPoolCode, repairPoolReq.getCode()));
        List<String> orgCodes = repairPoolReq.getOrgCodes();
        if (CollUtil.isNotEmpty(orgCodes)) {
            orgCodes.forEach(org -> {
                RepairPoolOrgRel repairPoolOrgRel = RepairPoolOrgRel.builder()
                        .orgCode(org)
                        .repairPoolCode(repairPoolReq.getCode())
                        .build();
                orgRelMapper.insert(repairPoolOrgRel);
            });
        }

        // 更新检修标准
        List<RepairPoolRel> repairPoolRels = relMapper.selectList(
                new LambdaQueryWrapper<RepairPoolRel>()
                        .eq(RepairPoolRel::getRepairPoolCode, repairPoolReq.getCode()));

        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> collect = repairPoolRels.stream().map(RepairPoolRel::getId).collect(Collectors.toList());
            repairPoolDeviceRel.delete(new LambdaQueryWrapper<RepairPoolDeviceRel>().in(RepairPoolDeviceRel::getRepairPoolRelId, collect));
            relMapper.deleteBatchIds(repairPoolRels);
        }

        // 处理检修标准、检修项目、检修设备、检修计划与检修标准的关联关系
        List<RepairPoolCodeReq> repairPoolCodes = repairPoolReq.getRepairPoolCodes();

        // 处理：只保存对应站点下的设备
        if (CollUtil.isNotEmpty(addStationCode)) {
            List<String> stationCodes = addStationCode.stream().map(StationDTO::getStationCode).collect(Collectors.toList());
            handleDevice(stationCodes, repairPoolCodes);
        }

        handle(repairPoolReq.getCode(), repairPoolCodes);
    }

    /**
     * 校验
     *
     * @param repairPoolDTO
     */
    private void checkTask(RepairPoolReq repairPoolDTO) {
        if (ObjectUtil.isEmpty(repairPoolDTO)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        RepairPool repairPool = baseMapper.selectById(repairPoolDTO.getId());
        if (ObjectUtil.isEmpty(repairPool)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 状态为已指派和已退回才能进行修改
        if (!InspectionConstant.TO_BE_ASSIGNED.equals(repairPool.getStatus())
                && !InspectionConstant.GIVE_BACK.equals(repairPool.getStatus())) {
            throw new AiurtBootException("状态为待指派和已退回才能进行修改");
        }

        if (CollUtil.isEmpty(repairPoolDTO.getAddStationCode())) {
            throw new AiurtBootException("站点信息为空");
        }

        if (CollUtil.isEmpty(repairPoolDTO.getOrgCodes())) {
            throw new AiurtBootException("组织机构为空");
        }

        List<RepairPoolCodeReq> repairPoolCodes = repairPoolDTO.getRepairPoolCodes();
        if (CollUtil.isEmpty(repairPoolCodes)) {
            throw new AiurtBootException("请选择检修标准");
        }

        // 如果是否需要审核选否，那么直接不接收是否需要验收的参数
        if (repairPoolDTO.getIsConfirm() != null && InspectionConstant.IS_CONFIRM_0.equals(repairPoolDTO.getIsConfirm())) {
            repairPoolDTO.setIsReceipt(null);
        }

        // 不能存在相同设备类型的检修标准
        List<String> deviceTypes = new ArrayList<>();
        repairPoolCodes.forEach(re -> {
            InspectionCode inspectionCode = inspectionCodeMapper.selectOne(
                    new LambdaQueryWrapper<InspectionCode>()
                            .eq(InspectionCode::getCode, re.getCode())
                            .eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (ObjectUtil.isEmpty(inspectionCode)) {
                throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
            }
            if (StrUtil.isNotEmpty(inspectionCode.getDeviceTypeCode())) {
                if (deviceTypes.contains(inspectionCode.getDeviceTypeCode())) {
                    throw new AiurtBootException("存在相同设备类型的检修标准");
                } else {
                    deviceTypes.add(inspectionCode.getDeviceTypeCode());
                }
            }
            if (InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionCode.getIsAppointDevice()) && CollUtil.isEmpty(re.getDeviceCodes())) {
                throw new AiurtBootException(String.format("名字为%s需要指定设备", ObjectUtil.isNotEmpty(inspectionCode) ? inspectionCode.getTitle() : ""));
            }
        });
    }

    /**
     * 根据任务id删除手工下发检修任务
     *
     * @param id
     */
    @Override
    public void deleteManualTaskById(String id) {
        RepairPool repairPool = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(repairPool)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        if (!InspectionConstant.TO_BE_ASSIGNED.equals(repairPool.getStatus())) {
            if(!InspectionConstant.GIVE_BACK.equals(repairPool.getStatus())){
                throw new AiurtBootException("已指派不允许删除");
            }
        }

        // 删除站点机构
        repairPoolStationRelMapper.delete(new LambdaQueryWrapper<RepairPoolStationRel>().eq(RepairPoolStationRel::getRepairPoolCode, repairPool.getCode()));
        orgRelMapper.delete(new LambdaQueryWrapper<RepairPoolOrgRel>().eq(RepairPoolOrgRel::getRepairPoolCode, repairPool.getCode()));

        // 删除检修标准和设备
        List<RepairPoolRel> repairPoolRels = relMapper.selectList(
                new LambdaQueryWrapper<RepairPoolRel>()
                        .eq(RepairPoolRel::getRepairPoolCode, repairPool.getCode()));

        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> collect = repairPoolRels.stream().map(RepairPoolRel::getId).collect(Collectors.toList());
            repairPoolDeviceRel.delete(new LambdaQueryWrapper<RepairPoolDeviceRel>().in(RepairPoolDeviceRel::getRepairPoolRelId, collect));
            relMapper.deleteBatchIds(repairPoolRels);
        }

        // 删除计划信息
        baseMapper.deleteById(repairPool);
    }

    /**
     * 根据检修任务code和检修标准id查询检修标准对应的设备
     *
     * @param page
     * @param code 检修任务code
     * @param id   检修标准id
     * @return
     */
    @Override
    public IPage<RepairDeviceDTO> queryDeviceByCodeAndId(Page<RepairDeviceDTO> page, String code, String id) {
        RepairPoolRel repairPoolRels = relMapper.selectOne(
                new LambdaQueryWrapper<RepairPoolRel>()
                        .eq(RepairPoolRel::getRepairPoolCode, code)
                        .eq(RepairPoolRel::getRepairPoolStaId, id)
                        .eq(RepairPoolRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (ObjectUtil.isNotEmpty(repairPoolRels)) {
            // 查询对应的设备
            List<RepairPoolDeviceRel> repairPoolDeviceRels = repairPoolDeviceRel.selectList(
                    new LambdaQueryWrapper<RepairPoolDeviceRel>()
                            .eq(RepairPoolDeviceRel::getRepairPoolRelId, repairPoolRels.getId()));

            // 分页处理设备信息
            if (CollUtil.isNotEmpty(repairPoolDeviceRels)) {
                List<String> deviceCodeList = repairPoolDeviceRels.stream().map(RepairPoolDeviceRel::getDeviceCode).collect(Collectors.toList());
                List<RepairDeviceDTO> repairDeviceDto = manager.queryDeviceByCodesPage(deviceCodeList, page);
                page.setRecords(repairDeviceDto);
            }
        }

        return page;
    }

    /**
     * 根据检修计划单号查询对应的检修标准
     *
     * @param planCode code值
     * @return
     */
    private List<RepairPoolCode> queryStandardByCode(String planCode) {
        List<RepairPoolCode> repairPoolCodes = new ArrayList<>();
        List<RepairPoolRel> repairPoolRels = relMapper.selectList(
                new QueryWrapper<RepairPoolRel>()
                        .eq("repair_pool_code", planCode)
                        .eq("del_flag", CommonConstant.DEL_FLAG_0));

        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> standardList = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            repairPoolCodes = repairPoolCodeMapper.selectList(
                    new QueryWrapper<RepairPoolCode>()
                            .in("id", standardList)
                            .eq("del_flag", CommonConstant.DEL_FLAG_0));
        }
        return repairPoolCodes;
    }
    /**
     * 获取检查周期类型字典映射
     *
     * @return 映射的 Map，键为周期类型值，值为周期类型文本描述
     */
    private Map<String, String> getCycleTypeMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(DictConstant.INSPECTION_CYCLE_TYPE);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 获取检查任务状态字典映射
     *
     * @return 映射的 Map，键为任务状态值，值为任务状态文本描述
     */
    private Map<String, String> getInspectionTaskStateMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(DictConstant.INSPECTION_TASK_STATE);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 获取工作类型映射
     *
     * @return 映射的 Map，键为工作类型值，值为工作类型文本描述
     */
    private Map<String, String> getWorkTypeMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(DictConstant.WORK_TYPE);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 查询所有相关的组织机构
     *
     * @param repairPoolCodes 维修池代码列表
     * @return 与给定维修池代码相关的组织机构列表
     */
    private List<RepairPoolOrgRel> getAllRepairPoolOrgRels(List<String> repairPoolCodes) {
        if(CollUtil.isEmpty(repairPoolCodes)){
            return CollUtil.newArrayList();
        }

        return orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .in(RepairPoolOrgRel::getRepairPoolCode, repairPoolCodes)
                        .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
    }

    /**
     * 查询所有相关的站点
     *
     * @param repairPoolCodes 维修池代码列表
     * @return 与给定维修池代码相关的站点映射，键为维修池代码，值为站点DTO列表
     */
    private Map<String, List<StationDTO>> getAllRepairPoolStationRels(List<String> repairPoolCodes) {
        if(CollUtil.isEmpty(repairPoolCodes)){
            return CollUtil.newHashMap();
        }

        return Optional.ofNullable(repairPoolStationRelMapper.selectBatchStationList(repairPoolCodes))
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.groupingBy(StationDTO::getRepairPoolCode));
    }

    //

    /**
     * 创建并返回一个新的 RepairTask 对象
     * @param assignDTO    AssignDTO对象
     * @param repairPool   RepairPool对象
     * @return
     */
    private RepairTask createRepairTask(AssignDTO assignDTO, RepairPool repairPool) {
        // 添加任务
        RepairTask repairTask = new RepairTask();
        repairTask.setRepairPoolId(repairPool.getId());
        repairTask.setYear(DateUtil.year(ObjectUtil.isNotEmpty(repairPool.getStartTime()) ? repairPool.getStartTime() : new Date()));
        repairTask.setType(repairPool.getType());
        repairTask.setIsOutsource(repairPool.getIsOutsource());
        repairTask.setSource(assignDTO.getIsManual().equals(InspectionConstant.NO_IS_MANUAL) ? InspectionConstant.REGULAR_ASSIGNMENT : InspectionConstant.MANUAL_ASSIGNMENT);
        repairTask.setCode(repairPool.getCode());
        repairTask.setWeeks(repairPool.getWeeks());
        repairTask.setStartTime(assignDTO.getStartTime());
        repairTask.setEndTime(assignDTO.getEndTime());
        repairTask.setStatus(InspectionConstant.TO_BE_CONFIRMED);
        repairTask.setIsConfirm(repairPool.getIsConfirm());
        repairTask.setIsReceipt(repairPool.getIsReceipt());
        repairTask.setWorkType(assignDTO.getWorkType());
        repairTask.setPlanOrderCode(assignDTO.getPlanOrderCode());
        repairTask.setPlanOrderCodeUrl(assignDTO.getPlanOrderCodeUrl());

        // 指派人信息
        repairTask.setAssignUserId(manager.checkLogin().getId());
        repairTask.setAssignUserName(manager.checkLogin().getRealname());
        repairTask.setAssignTime(new Date());

        return repairTask;
    }

    /**
     * 保存站点关联信息。
     *
     * @param repairPool  RepairPool对象
     */
    private void saveRepairTaskStationRel(RepairPool repairPool) {
        List<RepairPoolStationRel> repairPoolStationRels = repairPoolStationRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolStationRel>()
                        .eq(RepairPoolStationRel::getRepairPoolCode, repairPool.getCode())
                        .eq(RepairPoolStationRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairPoolStationRels)) {
            // 创建一个 RepairTaskStationRel 对象的列表，用于批量保存
            List<RepairTaskStationRel> repairTaskStationRelList = new ArrayList<>();

            for (RepairPoolStationRel repairPoolStationRel : repairPoolStationRels) {
                RepairTaskStationRel repairTaskStationRel = new RepairTaskStationRel();
                repairTaskStationRel.setLineCode(repairPoolStationRel.getLineCode());
                repairTaskStationRel.setStationCode(repairPoolStationRel.getStationCode());
                repairTaskStationRel.setPositionCode(repairPoolStationRel.getPositionCode());
                repairTaskStationRel.setRepairTaskCode(repairPool.getCode());
                repairTaskStationRel.setId(String.valueOf(IdWorker.getId()));
                // 将新创建的 RepairTaskStationRel 对象添加到列表中
                repairTaskStationRelList.add(repairTaskStationRel);
            }

            // 在循环结束后批量保存所有 RepairTaskStationRel 对象
            repairTaskStationRelMapper.batchInsert(repairTaskStationRelList);
        }
    }

    /**
     * 保存检修人信息。
     *
     * @param assignDTO   AssignDTO对象
     * @param repairPool  RepairPool对象
     */
    private void saveRepairTaskUser(AssignDTO assignDTO, RepairPool repairPool) {
        List<String> userIds = assignDTO.getUserIds();
        if (CollUtil.isNotEmpty(userIds)) {
            // 创建一个 RepairTaskUser 对象的列表，用于批量保存
            List<RepairTaskUser> repairTaskUserList = new ArrayList<>();

            userIds.forEach(userId -> {
                LoginUser userById = sysBaseApi.getUserById(userId);
                if (ObjectUtil.isNotEmpty(userById)) {
                    RepairTaskUser repairTaskUser = new RepairTaskUser();
                    repairTaskUser.setRepairTaskCode(repairPool.getCode());
                    repairTaskUser.setUserId(userById.getId());
                    repairTaskUser.setName(userById.getRealname());
                    repairTaskUser.setId(String.valueOf(IdWorker.getId()));
                    // 将新创建的 RepairTaskUser 对象添加到列表中
                    repairTaskUserList.add(repairTaskUser);
                }
            });

            // 在循环结束后批量保存所有 RepairTaskUser 对象
            repairTaskUserMapper.batchInsert(repairTaskUserList);
        }
    }

    /**
     * 保存组织机构信息。
     *
     * @param repairPool  RepairPool对象
     */
    private void saveRepairTaskOrgRel(RepairPool repairPool) {
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .eq(RepairPoolOrgRel::getRepairPoolCode, repairPool.getCode())
                        .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            // 创建一个 RepairTaskOrgRel 对象的列表，用于批量保存
            List<RepairTaskOrgRel> repairTaskOrgRelList = new ArrayList<>();

            for (RepairPoolOrgRel repairPoolOrgRel : repairPoolOrgRels) {
                RepairTaskOrgRel repairTaskOrgRel = new RepairTaskOrgRel();
                repairTaskOrgRel.setRepairTaskCode(repairPool.getCode());
                repairTaskOrgRel.setOrgCode(repairPoolOrgRel.getOrgCode());
                repairTaskOrgRel.setId(String.valueOf(IdWorker.getId()));
                // 将新创建的 RepairTaskOrgRel 对象添加到列表中
                repairTaskOrgRelList.add(repairTaskOrgRel);
            }

            // 在循环结束后批量保存所有 RepairTaskOrgRel 对象
            repairTaskOrgRelMapper.batchInsert(repairTaskOrgRelList);
        }
    }
    @Override
    public IPage<StationPlanDTO> queryPlanStationList(Page<StationPlanDTO> page,SelectPlanReq selectPlanReq) {
        //根据数据权限，查询站所信息
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        selectPlanReq.setIsManual(InspectionConstant.IS_MANUAL_0);
        List<StationPlanDTO> csStationList =baseMapper.queryPlanStationList(page,user.getId());
        if(CollUtil.isNotEmpty(csStationList)){
            //循环站点信息
            for (StationPlanDTO stationPlanDTO : csStationList) {
                //部门-站点统计数目
                selectPlanReq.setStationCode(stationPlanDTO.getStationCode());
                List<RepairPool> repairPools = baseMapper.queryPlanOrgList(selectPlanReq);
                //计划数
                stationPlanDTO.setPlanNum(CollUtil.isNotEmpty(repairPools) ? repairPools.size() : 0L);
                // 已完成(计划状态：6，7，8)
                stationPlanDTO.setPlanFinishNum(CollUtil.isNotEmpty(repairPools) ? repairPools.stream().filter(re -> InspectionConstant.COMPLETED.equals(re.getStatus())||
                        InspectionConstant.PENDING_REVIEW.equals(re.getStatus())||InspectionConstant.PENDING_RECEIPT.equals(re.getStatus())).count() : 0L);
                // 未完成
                stationPlanDTO.setUnPlanFinishNum(CollUtil.isNotEmpty(repairPools) ? stationPlanDTO.getPlanNum()-stationPlanDTO.getPlanFinishNum() : 0L);
            }

        }
        return page.setRecords(csStationList);
    }
}
