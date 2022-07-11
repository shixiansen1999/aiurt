package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.manager.utils.CodeGenerateUtils;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.plan.req.RepairStrategyReq;
import com.aiurt.boot.plan.req.ManualTaskReq;
import com.aiurt.boot.plan.req.RepairPoolCodeReq;
import com.aiurt.boot.plan.req.RepairPoolReq;
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
import com.aiurt.common.util.DateUtils;
import com.aiurt.common.util.UpdateHelperUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
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
    private ISysBaseAPI sysBaseAPI;
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

    /**
     * 检修计划池列表查询
     *
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param status      状态
     * @param workType    作业类型
     * @param stationCode 站点编码
     * @return
     */
    @Override
    public List<RepairPool> queryList(Date startTime, Date endTime, Integer status, Integer workType, String stationCode) {
        // 构造查询条件
        QueryWrapper<RepairPool> queryWrapper = doQuery(startTime, endTime, status, workType, stationCode);
        List<RepairPool> repairPoolList = baseMapper.selectList(queryWrapper);

        if (CollUtil.isNotEmpty(repairPoolList)) {
            repairPoolList.forEach(repair -> {
                // 查询检修计划对应的专业和专业子系统
                String planCode = repair.getCode();

                // 修计划单号查询对应的检修标准
                List<RepairPoolCode> repairPoolCodes = queryStandardByCode(planCode);

                if (CollUtil.isNotEmpty(repairPoolCodes)) {
                    // 专业
                    repair.setMajorName(manager.translateMajor(repairPoolCodes.stream().map(RepairPoolCode::getMajorCode).collect(Collectors.toList()), InspectionConstant.MAJOR));
                    // 子系统
                    repair.setSubsystemName(manager.translateMajor(repairPoolCodes.stream().map(RepairPoolCode::getSubsystemCode).collect(Collectors.toList()), InspectionConstant.SUBSYSTEM));
                }

                // 组织机构
                List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                        new LambdaQueryWrapper<RepairPoolOrgRel>()
                                .eq(RepairPoolOrgRel::getRepairPoolCode, planCode)
                                .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
                if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
                    repair.setOrgName(manager.translateOrg(repairPoolOrgRels.stream().map(r -> r.getOrgCode()).collect(Collectors.toList())));
                }

                // 站点
                repair.setStationName(manager.translateStation(repairPoolStationRelMapper.selectStationList(planCode)));
                // 周期类型
                repair.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repair.getType())));
                // 状态
                repair.setStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repair.getStatus())));
                // 作业类型
                repair.setWorkTypeName(sysBaseAPI.translateDict(DictConstant.WORK_TYPE, String.valueOf(repair.getWorkType())));

            });

        }

        return repairPoolList;
    }

    /**
     * 构造查询条件
     *
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param status      状态
     * @param workType    作业类型
     * @param stationCode 站点编码
     * @return
     */
    @NotNull
    public QueryWrapper<RepairPool> doQuery(Date startTime, Date endTime, Integer status, Integer workType, String stationCode) {
        QueryWrapper<RepairPool> queryWrapper = new QueryWrapper();
        queryWrapper.ge("start_time", startTime);
        queryWrapper.le("start_time", endTime);
        queryWrapper.eq("is_manual", InspectionConstant.NO_IS_MANUAL);
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        queryWrapper.orderByAsc("type");
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (workType != null) {
            queryWrapper.eq("work_type", workType);
        }

        // 根据站所查询
        if (StrUtil.isNotEmpty(stationCode)) {
            List<RepairPoolStationRel> repairPoolStationRels = repairPoolStationRelMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolStationRel>()
                            .eq(RepairPoolStationRel::getStationCode, stationCode)
                            .eq(RepairPoolStationRel::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (CollUtil.isNotEmpty(repairPoolStationRels)) {
                queryWrapper.in("code", repairPoolStationRels.stream().map(RepairPoolStationRel::getRepairPoolCode).collect(Collectors.toList()));
            }
        }

        return queryWrapper;
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
                result.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairPoolCode.getType())));
                result.setMajorName(manager.translateMajor(Arrays.asList(repairPoolCode.getMajorCode()), InspectionConstant.MAJOR));
                result.setSubsystemName(manager.translateMajor(Arrays.asList(repairPoolCode.getSubsystemCode()), InspectionConstant.SUBSYSTEM));
                result.setDeviceTypeName(manager.queryNameByCode(repairPoolCode.getDeviceTypeCode()));
                result.setIsAppointDevice(CollUtil.isNotEmpty(repairDeviceDTOList) ? "是" : "否");
                result.setIsAppointDeviceType(sysBaseAPI.translateDict(DictConstant.IS_APPOINT_DEVICE, String.valueOf(repairPoolCode.getIsAppointDevice())));

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
                        .orderByAsc(RepairPoolCodeContent::getSortNo));

        if (CollUtil.isNotEmpty(result)) {
            result.forEach(r -> {
                r.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_PROJECT, String.valueOf(r.getType())));
                r.setStatusItemName(sysBaseAPI.translateDict(DictConstant.INSPECTION_STATUS_ITEM, String.valueOf(r.getStatusItem())));
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
    private static List<RepairPoolCodeContent> addChildren(List<RepairPoolCodeContent> list, Map<String, RepairPoolCodeContent> map) {
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

            // 计划名称
            re.setName(repairPool.getName());
            // 计划编码
            re.setCode(repairPool.getCode());
            // 计划所属周
            re.setWeeks(repairPool.getWeeks());
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
            re.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairPool.getType())));
            // 状态
            re.setStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repairPool.getStatus())));

            // 组织机构
            List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(new LambdaQueryWrapper<RepairPoolOrgRel>().eq(RepairPoolOrgRel::getRepairPoolCode, code));
            List<String> orgList = new ArrayList<>();
            if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
                orgList = repairPoolOrgRels.stream().map(r -> r.getOrgCode()).collect(Collectors.toList());
                re.setOrgName(manager.translateOrg(orgList));
            }

            // 年份
            re.setYear(ObjectUtil.isNotEmpty(repairPool.getStartTime()) ? DateUtil.year(repairPool.getStartTime()) : null);
            // 所属策略
            InspectionStrategy inspectionStrategy = inspectionStrategyMapper.selectOne(new QueryWrapper<InspectionStrategy>().eq("code", repairPool.getInspectionStrCode()));
            re.setStrategy(ObjectUtil.isNotEmpty(inspectionStrategy) ? inspectionStrategy.getName() : "");

            // 是否审核
            re.setIsConfirm(sysBaseAPI.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(repairPool.getIsConfirm())));

            // 是否审核
            re.setIsReceipt(sysBaseAPI.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(repairPool.getIsReceipt())));

            // 作业类型
            re.setWorkTypeName(sysBaseAPI.translateDict(DictConstant.WORK_TYPE, String.valueOf(repairPool.getWorkType())));

            // 是否委外
            re.setIsManual(sysBaseAPI.translateDict(DictConstant.INSPECTION_IS_MANUAL, String.valueOf(repairPool.getIsManual())));
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
            int week = DateUtil.weekOfYear(DateUtil.parse(startTime));
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
                        .eq(RepairPoolRel::getRepairPoolCode, code));

        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> rid = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            List<RepairPoolCode> repairPoolCodes = repairPoolCodeMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolCode>()
                            .in(RepairPoolCode::getId, rid));

            // 查询并处理标准对应的专业、专业子系统
            if (CollUtil.isNotEmpty(repairPoolCodes)) {
                List<String> majorCode = repairPoolCodes.stream().map(RepairPoolCode::getMajorCode).collect(Collectors.toList());
                List<String> subSystemCode = repairPoolCodes.stream().map(RepairPoolCode::getSubsystemCode).collect(Collectors.toList());
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
            if (assignDTO.getIsManual().equals(InspectionConstant.IS_MANUAL)) {
                repairPool.setStartTime(assignDTO.getStartTime());
                repairPool.setStartTime(assignDTO.getEndTime());
            }

            repairPool.setStatus(InspectionConstant.TO_BE_CONFIRMED);
            baseMapper.updateById(repairPool);

            // 添加任务
            RepairTask repairTask = new RepairTask();
            repairTask.setRepairPoolId(id);
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

            // 保存检修任务信息
            repairTaskMapper.insert(repairTask);

            // 保存站点关联信息
            List<RepairPoolStationRel> repairPoolStationRels = repairPoolStationRelMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolStationRel>()
                            .eq(RepairPoolStationRel::getRepairPoolCode, repairPool.getCode())
                            .eq(RepairPoolStationRel::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (CollUtil.isNotEmpty(repairPoolStationRels)) {
                for (RepairPoolStationRel repairPoolStationRel : repairPoolStationRels) {
                    RepairTaskStationRel repairTaskStationRel = new RepairTaskStationRel();
                    repairTaskStationRel.setLineCode(repairPoolStationRel.getLineCode());
                    repairTaskStationRel.setStationCode(repairPoolStationRel.getStationCode());
                    repairTaskStationRel.setPositionCode(repairPoolStationRel.getPositionCode());
                    repairTaskStationRel.setRepairTaskCode(repairPool.getCode());
                    repairTaskStationRelMapper.insert(repairTaskStationRel);
                }
            }

            // 保存检修人信息
            List<String> userIds = assignDTO.getUserIds();
            if (CollUtil.isNotEmpty(userIds)) {
                userIds.forEach(userId -> {
                    LoginUser userById = sysBaseAPI.getUserById(userId);
                    if (ObjectUtil.isNotEmpty(userById)) {
                        RepairTaskUser repairTaskUser = new RepairTaskUser();
                        repairTaskUser.setRepairTaskCode(repairPool.getCode());
                        repairTaskUser.setUserId(userById.getId());
                        repairTaskUser.setName(userById.getRealname());
                        repairTaskUserMapper.insert(repairTaskUser);
                    }
                });
            }

            // 保存组织机构信息
            List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolOrgRel>()
                            .eq(RepairPoolOrgRel::getRepairPoolCode, repairPool.getCode())
                            .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
                for (RepairPoolOrgRel repairPoolOrgRel : repairPoolOrgRels) {
                    RepairTaskOrgRel repairTaskOrgRel = new RepairTaskOrgRel();
                    repairTaskOrgRel.setRepairTaskCode(repairPool.getCode());
                    repairTaskOrgRel.setOrgCode(repairPoolOrgRel.getOrgCode());
                    repairTaskOrgRelMapper.insert(repairTaskOrgRel);
                }
            }

            // 生成检修标准关联、检修设备清单、检修结果信息
            this.generate(repairPool, repairTask.getId(), repairPool.getCode());

            // 发送消息给对用的检修人
            this.sendMessage(userIds);
        }
        return Result.ok();
    }

    /**
     * 检修消息发送
     *
     * @param userIds
     */
    private void sendMessage(List<String> userIds) {
        if (CollUtil.isNotEmpty(userIds)) {
            String toUser = StrUtil.join(",", userIds);
            sysBaseAPI.sendSysAnnouncement(new MessageDTO(manager.checkLogin().getId(), toUser, "消息通知", "您有一条新的检修任务!", CommonConstant.MSG_CATEGORY_1));
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
     * 生成检修设备清单
     *
     * @param oldStaId        计划中的关联的检修标准id
     * @param newStaId        任务中新的检修标准id
     * @param taskId          任务id
     * @param taskCode        是否与设备类型相关
     * @param isAppointDevice
     */
    private void generateInventory(String oldStaId, String newStaId, String taskId, String taskCode, Integer isAppointDevice) {

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
        HashMap<String, String> map = new HashMap<>();

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
        if (CollUtil.isEmpty(ids)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
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
        List<LoginUser> resutlt = new ArrayList<>();
        if (StrUtil.isEmpty(code)) {
            return resutlt;
        }
        // 当前计划关联的组织机构里的人员
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .eq(RepairPoolOrgRel::getRepairPoolCode, code));

        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            List<String> orgCodes = repairPoolOrgRels.stream().map(RepairPoolOrgRel::getOrgCode).collect(Collectors.toList());
            // todo 需要查找当前登录管理的组织机构比较后再查询
            resutlt = sysBaseAPI.getUserByDepIds(orgCodes);
        }
        return resutlt;
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
    public List<StandardDTO> queryStandardList(String code, String majorCode, String systemCode) {
        List<StandardDTO> result = new ArrayList<>();
        if (StrUtil.isEmpty(code)) {
            return result;
        }

        LambdaQueryWrapper<RepairPoolRel> lambdaQueryWrapper = new LambdaQueryWrapper<RepairPoolRel>();
        lambdaQueryWrapper.eq(RepairPoolRel::getRepairPoolCode, code);

        List<RepairPoolRel> repairPoolRels = relMapper.selectList(lambdaQueryWrapper);

        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> codeList = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(codeList)) {

                LambdaQueryWrapper<RepairPoolCode> poolCodeLambdaQueryWrapper = new LambdaQueryWrapper<RepairPoolCode>();
                poolCodeLambdaQueryWrapper.in(RepairPoolCode::getId, codeList);

                if (StrUtil.isNotEmpty(majorCode)) {
                    poolCodeLambdaQueryWrapper.eq(RepairPoolCode::getMajorCode, majorCode);
                }
                if (StrUtil.isNotEmpty(systemCode)) {
                    poolCodeLambdaQueryWrapper.eq(RepairPoolCode::getSubsystemCode, systemCode);
                }
                List<RepairPoolCode> repairPoolCodes = repairPoolCodeMapper.selectList(poolCodeLambdaQueryWrapper);

                if (CollUtil.isNotEmpty(repairPoolCodes)) {
                    repairPoolCodes.forEach(r -> {
                        StandardDTO standardDTO = new StandardDTO();
                        standardDTO.setId(r.getId());
                        standardDTO.setName(r.getTitle());
                        result.add(standardDTO);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 分页查询手工下发任务列表
     *
     * @param page
     * @param manualTaskReq
     * @return
     */
    @Override
    public IPage<RepairPool> listPage(Page<RepairPool> page, ManualTaskReq manualTaskReq) {
        // 处理查询参数
        QueryWrapper<RepairPool> queryWrapper = doQuery(manualTaskReq);

        page = baseMapper.selectPage(page, queryWrapper);

        page.getRecords().forEach(re -> {
            // 组织机构
            List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolOrgRel>()
                            .eq(RepairPoolOrgRel::getRepairPoolCode, re.getCode())
                            .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));

            List<String> orgList = new ArrayList<>();
            if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
                orgList = repairPoolOrgRels.stream().map(r -> r.getOrgCode()).collect(Collectors.toList());
                re.setOrgName(manager.translateOrg(orgList));
            }

            // 站点
            List<StationDTO> repairPoolStationRels = repairPoolStationRelMapper.selectStationList(re.getCode());
            re.setStationName(manager.translateStation(repairPoolStationRels));
        });
        return page;
    }

    /**
     * 处理查询参数
     *
     * @param manualTaskReq
     * @return
     */
    @NotNull
    public QueryWrapper<RepairPool> doQuery(ManualTaskReq manualTaskReq) {
        QueryWrapper<RepairPool> queryWrapper = new QueryWrapper<>();

        // 只查询是手工下发的检修任务
        queryWrapper.eq("is_manual", InspectionConstant.IS_MANUAL);
        queryWrapper.orderByDesc("create_time");
        if (ObjectUtil.isNotEmpty(manualTaskReq.getStartTime())) {
            queryWrapper.ge("start_time", manualTaskReq.getStartTime());
            queryWrapper.le("start_time", manualTaskReq.getEndTime());
        }

        // 检修任务单号
        if (StrUtil.isNotEmpty(manualTaskReq.getCode())) {
            queryWrapper.like("code", manualTaskReq.getCode());
        }

        // 检修类型
        if (manualTaskReq.getType() != null) {
            queryWrapper.eq("type", manualTaskReq.getType());
        }

        // 状态
        if (manualTaskReq.getStatus() != null) {
            queryWrapper.eq("status", manualTaskReq.getStatus());
        }

        // 检修计划codes
        Set<String> codes = new HashSet<>();
        if (StrUtil.isNotEmpty(manualTaskReq.getOrgList())) {
            List<String> orgList = StrUtil.split(manualTaskReq.getOrgList(), ',');
            List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(new LambdaQueryWrapper<RepairPoolOrgRel>().in(RepairPoolOrgRel::getOrgCode, orgList));
            if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
                codes.addAll(repairPoolOrgRels.stream().map(RepairPoolOrgRel::getRepairPoolCode).collect(Collectors.toList()));
            }
        }

        // 组织机构和站点
        if (StrUtil.isNotEmpty(manualTaskReq.getStationList())) {
            List<String> stationList = StrUtil.split(manualTaskReq.getStationList(), ',');
            List<RepairPoolStationRel> repairPoolStationRels = repairPoolStationRelMapper.selectList(new LambdaQueryWrapper<RepairPoolStationRel>().in(RepairPoolStationRel::getStationCode, stationList));
            if (CollUtil.isNotEmpty(repairPoolStationRels)) {
                codes.addAll(repairPoolStationRels.stream().map(RepairPoolStationRel::getRepairPoolCode).collect(Collectors.toList()));
            }
        }
        if (CollUtil.isNotEmpty(codes)) {
            queryWrapper.in("code", codes);
        }

        return queryWrapper;
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
        handle(jxCode, repairPoolCodes);
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

        repairPoolCodes.forEach(re -> {

            InspectionCode inspectionCode = inspectionCodeMapper.selectOne(
                    new LambdaQueryWrapper<InspectionCode>()
                            .eq(InspectionCode::getCode, re.getCode())
                            .eq(InspectionCode::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (ObjectUtil.isEmpty(inspectionCode)) {
                throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
            }
            if (InspectionConstant.IS_APPOINT_DEVICE.equals(inspectionCode.getIsAppointDevice()) && CollUtil.isEmpty(re.getDeviceCodes())) {
                throw new AiurtBootException(String.format("名字为%s需要指定设备", ObjectUtil.isNotEmpty(inspectionCode) ? inspectionCode.getTitle() : ""));
            }
        });

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
                    repairPoolCodes.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairPoolCodes.getType())));
                    repairPoolCodes.setIsAppointDeviceName(sysBaseAPI.translateDict(DictConstant.IS_APPOINT_DEVICE, String.valueOf(repairPoolCodes.getIsAppointDevice())));

                    // 判断是否指定了设备
                    List<RepairPoolDeviceRel> repairPoolDeviceRels = repairPoolDeviceRel.selectList(
                            new LambdaQueryWrapper<RepairPoolDeviceRel>()
                                    .eq(RepairPoolDeviceRel::getRepairPoolRelId, sl.getId()));
                    repairPoolCodes.setSpecifyDevice(CollUtil.isNotEmpty(repairPoolDeviceRels) ? "是" : "否");

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
            throw new AiurtBootException("状态为已指派和已退回才能进行修改");
        }
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
        if (InspectionConstant.ASSIGNED.equals(repairPool.getStatus())) {
            throw new AiurtBootException("已指派不允许删除");
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
                List<RepairDeviceDTO> repairDeviceDTOS = manager.queryDeviceByCodesPage(deviceCodeList, page);
                page.setRecords(repairDeviceDTOS);
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
}
