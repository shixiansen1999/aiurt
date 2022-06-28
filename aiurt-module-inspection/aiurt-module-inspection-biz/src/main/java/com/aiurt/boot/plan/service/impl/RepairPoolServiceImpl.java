package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.plan.rep.RepairStrategyReq;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.aiurt.boot.strategy.mapper.InspectionStrategyMapper;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
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

    /**
     * 检修计划池列表查询
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @Override
    public List<RepairPool> queryList(Date startTime, Date endTime) {

        QueryWrapper<RepairPool> queryWrapper = new QueryWrapper();
        queryWrapper.ge("start_time", startTime);
        queryWrapper.le("end_time", endTime);
        queryWrapper.eq("is_manual", InspectionConstant.NO_IS_MANUAL);
        queryWrapper.eq("del_flag", 0);
        queryWrapper.orderByAsc("type");
        List<RepairPool> repairPoolList = baseMapper.selectList(queryWrapper);

        if (CollUtil.isNotEmpty(repairPoolList)) {
            repairPoolList.forEach(repair -> {
                // 查询检修计划对应的专业和专业子系统
                String planCode = repair.getCode();
                List<RepairPoolCode> repairPoolCodes = queryStandardByCode(planCode);
                List<String> majorCodes = new ArrayList<>();
                List<String> majorSubSystemCodes = new ArrayList<>();
                if (CollUtil.isNotEmpty(repairPoolCodes)) {
                    majorCodes = repairPoolCodes.stream().map(RepairPoolCode::getMajorCode).collect(Collectors.toList());
                    majorSubSystemCodes = repairPoolCodes.stream().map(RepairPoolCode::getSubsystemCode).collect(Collectors.toList());
                }

                // 专业
                repair.setMajorName(manager.translateMajor(majorCodes, InspectionConstant.MAJOR));

                // 子系统
                repair.setSubsystemName(manager.translateMajor(majorSubSystemCodes, InspectionConstant.SUBSYSTEM));

                // 组织机构
                List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                        new LambdaQueryWrapper<RepairPoolOrgRel>()
                                .eq(RepairPoolOrgRel::getRepairPoolCode, planCode)
                                .eq(RepairPoolOrgRel::getDelFlag, 0));
                List<String> orgList = new ArrayList<>();
                if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
                    orgList = repairPoolOrgRels.stream().map(r -> r.getOrgCode()).collect(Collectors.toList());
                    repair.setOrgName(manager.translateOrg(orgList));
                }

                // 站点
                List<StationDTO> repairPoolStationRels = repairPoolStationRelMapper.selectStationList(planCode);
                repair.setStationName(manager.translateStation(repairPoolStationRels));

                // 周期类型
                repair.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repair.getType())));
                // 状态
                repair.setStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repair.getStatus())));

            });

        }

        return repairPoolList;
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
            if (StrUtil.isNotEmpty(req.getMajorCode())) {
                poolCodeQueryWrapper.eq(RepairPoolCode::getMajorCode, req.getMajorCode());
            }
            if (StrUtil.isNotEmpty(req.getSubSystemCode())) {
                poolCodeQueryWrapper.eq(RepairPoolCode::getSubsystemCode, req.getSubSystemCode());
            }
            poolCodeQueryWrapper.eq(RepairPoolCode::getId, req.getStandardId());

            RepairPoolCode repairPoolCode = repairPoolCodeMapper.selectOne(poolCodeQueryWrapper);
            if (ObjectUtil.isNotEmpty(repairPoolCode)) {
                // 设备清单
                List<RepairDeviceDTO> repairDeviceDTOList = new ArrayList<>();
                RepairPoolRel repairPoolRel = relMapper.selectOne(new LambdaQueryWrapper<RepairPoolRel>()
                        .eq(RepairPoolRel::getRepairPoolCode, req.getCode())
                        .eq(RepairPoolRel::getRepairPoolStaId, req.getStandardId())
                        .eq(RepairPoolRel::getDelFlag, 0));
                if (ObjectUtil.isNotEmpty(repairPoolRel)) {
                    List<RepairPoolDeviceRel> repairPoolDeviceRels = repairPoolDeviceRel.selectList(new LambdaQueryWrapper<RepairPoolDeviceRel>().eq(RepairPoolDeviceRel::getRepairPoolRelId, repairPoolRel.getId()));
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
                result.setIsAppointDeviceTyep(sysBaseAPI.translateDict(DictConstant.IS_APPOINT_DEVICE, String.valueOf(repairPoolCode.getIsAppointDevice())));

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
    private List<RepairPoolCodeContent> selectCodeContentList(String id) {
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
                //当前位置显示实体类中的List元素定义的参数为null，出现空指针异常错误
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
            List<RepairPoolCode> repairPoolCodes = queryStandardByCode(code);
            List<String> majorCodes = new ArrayList<>();
            List<String> majorSubSystemCodes = new ArrayList<>();
            if (CollUtil.isNotEmpty(repairPoolCodes)) {
                majorCodes = repairPoolCodes.stream().map(RepairPoolCode::getMajorCode).collect(Collectors.toList());
                majorSubSystemCodes = repairPoolCodes.stream().map(RepairPoolCode::getSubsystemCode).collect(Collectors.toList());
            }

            // 计划名称
            re.setName(repairPool.getName());
            // 计划编码
            re.setCode(repairPool.getCode());
            // 计划所属周
            re.setWeeks(repairPool.getWeeks());
            // 退回理由
            re.setRemark(repairPool.getRemark());

            // 站点
            List<StationDTO> repairPoolStationRels = repairPoolStationRelMapper.selectStationList(code);
            re.setStationName(manager.translateStation(repairPoolStationRels));

            // 周期类型
            re.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairPool.getType())));
            // 状态
            re.setStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repairPool.getStatus())));
            // 专业
            re.setMajorName(manager.translateMajor(majorCodes, InspectionConstant.MAJOR));
            // 子系统
            re.setSubsystemName(manager.translateMajor(majorSubSystemCodes, InspectionConstant.SUBSYSTEM));

            // 组织机构
            List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(new LambdaQueryWrapper<RepairPoolOrgRel>().eq(RepairPoolOrgRel::getRepairPoolCode, code));
            List<String> orgList = new ArrayList<>();
            if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
                orgList = repairPoolOrgRels.stream().map(r -> r.getOrgCode()).collect(Collectors.toList());
                re.setOrgName(manager.translateOrg(orgList));
            }

            // 年份
            re.setYear(DateUtil.year(repairPool.getStartTime()));
            // 所属策略
            InspectionStrategy inspectionStrategy = inspectionStrategyMapper.selectOne(new QueryWrapper<InspectionStrategy>().eq("code", repairPool.getInspectionStrCode()));
            re.setStrategy(ObjectUtil.isNotEmpty(inspectionStrategy) ? inspectionStrategy.getName() : "");

            // 是否审核
            re.setIsConfirm(sysBaseAPI.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(repairPool.getIsConfirm())));

            // 是否审核
            re.setIsReceipt(sysBaseAPI.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(repairPool.getIsReceipt())));

            // 作业类型
            re.setWorkType(sysBaseAPI.translateDict(DictConstant.WORK_TYPE, String.valueOf(repairPool.getWorkType())));

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
    @Transactional
    @Override
    public Result assigned(AssignDTO assignDTO) {
        // 校验
        this.check(assignDTO);
        List<String> ids = assignDTO.getIds();
        for (String id : ids) {
            // 修改检修计划状态
            RepairPool repairPool = baseMapper.selectById(id);
            if (ObjectUtil.isNotEmpty(repairPool)) {
                throw new AiurtBootException("非法操作");
            }
            repairPool.setStatus(InspectionConstant.TO_BE_CONFIRMED);
            baseMapper.updateById(repairPool);

            // 添加任务
            RepairTask repairTask = new RepairTask();
            repairTask.setRepairPoolId(id);
            repairTask.setType(repairTask.getType());
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
            // 检修单号
            // TODO 单号生成规则
            String code = "";
            repairTask.setCode(code);

            // 指派人信息
            repairTask.setAssignUserId(manager.checkLogin().getId());
            repairTask.setAssignUserName(manager.checkLogin().getRealname());
            repairTask.setAssignTime(new Date());

            // 保存检修任务信息
            repairTaskMapper.insert(repairTask);

            // 站点信息
            List<RepairPoolStationRel> repairPoolStationRels = repairPoolStationRelMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolStationRel>()
                            .eq(RepairPoolStationRel::getRepairPoolCode, repairPool.getCode())
                            .eq(RepairPoolStationRel::getDelFlag, 0));
            if (CollUtil.isNotEmpty(repairPoolStationRels)) {
                for (RepairPoolStationRel repairPoolStationRel : repairPoolStationRels) {
                    RepairTaskStationRel repairTaskStationRel = new RepairTaskStationRel();
                    repairTaskStationRel.setLineCode(repairPoolStationRel.getLineCode());
                    repairTaskStationRel.setStationCode(repairPoolStationRel.getStationCode());
                    repairTaskStationRel.setPositionCode(repairPoolStationRel.getPositionCode());
                    repairTaskStationRel.setRepairTaskCode(code);
                    // 保存站点关联信息
                    repairTaskStationRelMapper.insert(repairTaskStationRel);
                }
            }

            // 检修人信息
            List<String> userIds = assignDTO.getUserIds();
            if (CollUtil.isNotEmpty(userIds)) {
                userIds.forEach(userId -> {
                    LoginUser userById = sysBaseAPI.getUserById(userId);
                    if (ObjectUtil.isNotEmpty(userById)) {
                        RepairTaskUser repairTaskUser = new RepairTaskUser();
                        repairTaskUser.setRepairTaskCode(code);
                        repairTaskUser.setUserId(userById.getId());
                        repairTaskUser.setName(userById.getRealname());
                        // 保存人员关联信息
                        repairTaskUserMapper.insert(repairTaskUser);
                    }
                });
            }

            // 组织机构信息
            List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolOrgRel>()
                            .eq(RepairPoolOrgRel::getRepairPoolCode, repairPool.getCode())
                            .eq(RepairPoolOrgRel::getDelFlag, 0));
            if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
                for (RepairPoolOrgRel repairPoolOrgRel : repairPoolOrgRels) {
                    RepairTaskOrgRel repairTaskOrgRel = new RepairTaskOrgRel();
                    repairTaskOrgRel.setRepairTaskCode(code);
                    repairTaskOrgRel.setOrgCode(repairPoolOrgRel.getOrgCode());
                    // 保存组织机构信息
                    repairTaskOrgRelMapper.insert(repairTaskOrgRel);
                }
            }

            // 生成检修标准关联、检修设备清单、检修结果信息
            this.generate(repairPool, repairTask.getId());

            // TODO 发送消息给对用的维修人
            this.sendMessage();
        }
        return Result.ok();
    }

    /**
     * 消息发送
     */
    private void sendMessage() {


    }

    /**
     * 生成检修标准关联、检修设备清单、检修结果信息
     *
     * @param repairPool
     * @param taskId
     */
    private void generate(RepairPool repairPool, String taskId) {
        List<RepairPoolRel> repairPoolRels = relMapper.selectList(
                new LambdaQueryWrapper<RepairPoolRel>()
                        .eq(RepairPoolRel::getRepairPoolCode, repairPool.getCode())
                        .eq(RepairPoolRel::getDelFlag, 0));
        if (CollUtil.isNotEmpty(repairPoolRels)) {
            // 生成检修标准关联
            List<String> staIds = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            List<RepairPoolCode> repairPoolCodes = repairPoolCodeMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolCode>()
                            .in(RepairPoolCode::getId, staIds)
                            .eq(RepairPoolCode::getDelFlag, 0));

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
                    this.generateInventory(repairPoolRels, re.getId(), repairTaskStandardRel.getId(), repairTaskStandardRel.getIsAppointDevice());
                });

            }
        }
    }

    /**
     * 生成检修设备清单
     *
     * @param repairPoolRels
     * @param staId           计划中的关联的检修标准id
     * @param taskId          任务id
     * @param isAppointDevice 是否与设备类型相关
     */
    private void generateInventory(List<RepairPoolRel> repairPoolRels, String staId, String taskId, Integer isAppointDevice) {
        if (InspectionConstant.NO_ISAPPOINT_DEVICE.equals(isAppointDevice)) {
            // 与设备不相关
            // TODO 单号生成规则
            String jxdCode = "";
            RepairTaskDeviceRel repairTaskDeviceRel = new RepairTaskDeviceRel();
            repairTaskDeviceRel.setCode(jxdCode);
            repairTaskDeviceRel.setRepairTaskId(taskId);
            repairTaskDeviceRel.setTaskStandardRelId(staId);
            repairTaskDeviceRelMapper.insert(repairTaskDeviceRel);

            // 生成检修结果表
            this.generateItemResult(staId, repairTaskDeviceRel.getId());
        } else {
            List<String> idList = repairPoolRels.stream().map(RepairPoolRel::getId).collect(Collectors.toList());
            List<RepairPoolDeviceRel> repairPoolDeviceRels = repairPoolDeviceRel.selectList(
                    new LambdaQueryWrapper<RepairPoolDeviceRel>().in(RepairPoolDeviceRel::getRepairPoolRelId, idList));

            List<String> deviceCodeList = new ArrayList<>();
            if (CollUtil.isNotEmpty(repairPoolDeviceRels)) {
                // 与设备相关并且已经指定了设备
                deviceCodeList = repairPoolDeviceRels.stream().map(RepairPoolDeviceRel::getDeviceCode).collect(Collectors.toList());
            } else {
                // 根据站点、组织机构、设备类型查找指定的设备
                // TODO 设备生成规则
                deviceCodeList = null;
            }

            for (String deviceCode : deviceCodeList) {
                RepairTaskDeviceRel repairTaskDeviceRel = new RepairTaskDeviceRel();
                // TODO 单号生成规则
                String jxdCode = "";
                repairTaskDeviceRel.setCode(jxdCode);
                repairTaskDeviceRel.setDeviceCode(deviceCode);
                repairTaskDeviceRel.setRepairTaskId(taskId);
                repairTaskDeviceRel.setTaskStandardRelId(staId);
                repairTaskDeviceRelMapper.insert(repairTaskDeviceRel);
                // 生成检修结果表
                this.generateItemResult(staId, repairTaskDeviceRel.getId());
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
                        .eq(RepairPoolCodeContent::getDelFlag, 0));
        //<K,V> K是旧的id,V新生成的id
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
                repairTaskResult.setStatus(repairPoolCodeContent.getStatusItem());
                repairTaskResult.setSortNo(repairPoolCodeContent.getSortNo());
                repairTaskResult.setType(repairPoolCodeContent.getType());
                repairTaskResult.setCode(repairPoolCodeContent.getCode());
                repairTaskResult.setPid(repairPoolCodeContent.getPid());
                repairTaskResultMapper.insert(repairTaskResult);
                map.put(repairPoolCodeContent.getId(), repairTaskResult.getId());
            }
            // 更新pid
            List<RepairTaskResult> repairTaskResults = repairTaskResultMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskResult>()
                            .eq(RepairTaskResult::getTaskDeviceRelId, id)
                            .ne(RepairTaskResult::getPid, 0)
                            .eq(RepairTaskResult::getDelFlag, 0));
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
            throw new AiurtBootException("非法操作");
        }
        // 是否已经被指派过
        List<String> ids = assignDTO.getIds();
        ids.forEach(id -> {
            RepairPool repairPool = baseMapper.selectById(id);
            if (ObjectUtil.isNotEmpty(repairPool)
                    && (!InspectionConstant.TO_BE_ASSIGNED.equals(repairPool.getStatus()) || !InspectionConstant.GIVE_BACK.equals(repairPool.getStatus()))) {
                throw new AiurtBootException(String.format("检修计划名称为%s已被指派任务，请勿重复指派", repairPool.getName()));
            }
        });

    }

    /**
     * 指派检修任务人员下拉列表
     *
     * @return
     */
    @Override
    public List<LoginUser> queryUserList(String code) {
        List<LoginUser> resutlt = new ArrayList<>();
        if (StrUtil.isNotEmpty(code)) {
            return resutlt;
        }
        // 当前计划关联的组织机构里的人员
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .eq(RepairPoolOrgRel::getRepairPoolCode, code));

        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            List<String> orgCodes = repairPoolOrgRels.stream().map(RepairPoolOrgRel::getOrgCode).collect(Collectors.toList());
            resutlt = sysBaseAPI.getUserByDepIds(orgCodes);
        }
        return resutlt;
    }

    /**
     * 检修详情里的检修标准下拉列表
     *
     * @param code 检修计划单号
     * @return
     */
    @Override
    public List<StandardDTO> queryStandardList(String code) {
        List<StandardDTO> result = new ArrayList<>();
        if (StrUtil.isEmpty(code)) {
            return result;
        }

        List<RepairPoolRel> repairPoolRels = relMapper.selectList(
                new LambdaQueryWrapper<RepairPoolRel>()
                        .eq(RepairPoolRel::getRepairPoolCode, code));

        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> codeList = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(codeList)) {
                List<RepairPoolCode> repairPoolCodes = repairPoolCodeMapper.selectList(
                        new LambdaQueryWrapper<RepairPoolCode>()
                                .in(RepairPoolCode::getId, codeList));

                if (CollUtil.isNotEmpty(repairPoolCodes)) {
                    repairPoolCodes.forEach(r -> {
                        StandardDTO standardDTO = new StandardDTO();
                        standardDTO.setId(r.getId());
                        standardDTO.setName(r.getTitle());
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
     * @param queryWrapper
     * @return
     */
    @Override
    public IPage<RepairPool> listPage(Page<RepairPool> page, QueryWrapper<RepairPool> queryWrapper) {
        // 只查询是手工下发的检修任务
        queryWrapper.eq("is_manual", InspectionConstant.IS_MANUAL);
        page = baseMapper.selectPage(page, queryWrapper);

        page.getRecords().forEach(re -> {
            // 组织机构
            List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                    new LambdaQueryWrapper<RepairPoolOrgRel>()
                            .eq(RepairPoolOrgRel::getRepairPoolCode, re.getCode())
                            .eq(RepairPoolOrgRel::getDelFlag, 0));

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
     * 添加手工下发检修任务
     *
     * @param repairPoolDTO
     */
    @Override
    public void addManualTask(RepairPoolDTO repairPoolDTO) {
        // 校验必填信息
        chekcRepairPoolDTO(repairPoolDTO);

        // TODO 根据规则生成单号
        String jx = "";
        repairPoolDTO.setCode(jx);

        // 保存任务信息
        baseMapper.insert(repairPoolDTO);

        // 保存站点
        List<StationDTO> addStationCode = repairPoolDTO.getAddStationCode();
        if (CollUtil.isNotEmpty(addStationCode)) {
            addStationCode.forEach(re -> {
                RepairPoolStationRel repairPoolStationRel = new RepairPoolStationRel();
                repairPoolStationRel.setRepairPoolCode(repairPoolDTO.getCode());
                UpdateHelperUtils.copyNullProperties(re, repairPoolStationRel);
                repairPoolStationRelMapper.insert(repairPoolStationRel);
            });
        }

        // 保存组织机构
        List<String> orgCodes = repairPoolDTO.getOrgCodes();
        if (CollUtil.isNotEmpty(orgCodes)) {
            orgCodes.forEach(org -> {
                RepairPoolOrgRel rel = new RepairPoolOrgRel();
                rel.setRepairPoolCode(repairPoolDTO.getCode());
                rel.setOrgCode(org);
                orgRelMapper.insert(rel);
            });
        }

        // 处理检修标准、检修项目、检修设备、检修计划与检修标准的关联关系
        List<RepairPoolCode> repairPoolCodes = repairPoolDTO.getRepairPoolCodes();
        if (CollUtil.isNotEmpty(repairPoolCodes)) {
            // <K,V> K是旧的id,V新生成的id，用来维护复制检修项目时的pid更新
            HashMap<String, String> map = new HashMap<>(50);

            repairPoolCodes.forEach(re -> {
                // 保存检修标准
                repairPoolCodeMapper.insert(re);
                String staId = re.getId();

                // 查询检修标准对应的检修项目
                List<InspectionCodeContent> inspectionCodeContentList = inspectionCodeContentMapper.selectList(
                        new LambdaQueryWrapper<InspectionCodeContent>()
                                .eq(InspectionCodeContent::getCode, re.getCode())
                                .eq(InspectionCodeContent::getDelFlag, 0));

                if (CollUtil.isNotEmpty(inspectionCodeContentList)) {
                    inspectionCodeContentList.forEach(ins -> {
                        RepairPoolCodeContent repairPoolCodeContent = new RepairPoolCodeContent();
                        UpdateHelperUtils.copyNullProperties(ins, repairPoolCodeContent);
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
                                    .eq(RepairPoolCodeContent::getDelFlag, 0));
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
                List<RepairDeviceDTO> repairDeviceDTOList = re.getRepairDeviceDTOList();
                if (CollUtil.isNotEmpty(repairDeviceDTOList)) {
                    repairDeviceDTOList.forEach(red -> {
                        RepairPoolDeviceRel poolDeviceRel = RepairPoolDeviceRel.builder()
                                .deviceCode(red.getCode())
                                .repairPoolRelId(relId)
                                .build();
                        repairPoolDeviceRel.insert(poolDeviceRel);
                    });
                }
            });
        }
    }

    /**
     * 校验必填信息
     *
     * @param repairPoolDTO
     */
    private void chekcRepairPoolDTO(RepairPoolDTO repairPoolDTO) {
        if (ObjectUtil.isEmpty(repairPoolDTO)) {
            throw new AiurtBootException("必填参数为空");
        }
        if (CollUtil.isEmpty(repairPoolDTO.getAddStationCode())) {
            throw new AiurtBootException("站点信息为空");
        }
        if (CollUtil.isEmpty(repairPoolDTO.getOrgCodes())) {
            throw new AiurtBootException("组织机构为空");
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
        return null;
    }

    /**
     * 修改手工下发检修任务信息
     *
     * @param repairPoolDTO
     */
    @Override
    public void updateManualTaskById(RepairPoolDTO repairPoolDTO) {

    }

    /**
     * 根据任务id删除手工下发检修任务
     *
     * @param id
     */
    @Override
    public void deleteManualTaskById(String id) {

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
                        .eq("repair_pool_code", planCode));

        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> standardList = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            repairPoolCodes = repairPoolCodeMapper.selectList(
                    new QueryWrapper<RepairPoolCode>()
                            .in("id", standardList));
        }
        return repairPoolCodes;
    }
}
