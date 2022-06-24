package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.plan.rep.RepairStrategyReq;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.aiurt.boot.strategy.mapper.InspectionStrategyMapper;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;

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
        queryWrapper.orderByAsc("type");
        List<RepairPool> repairPoolList = baseMapper.selectList(queryWrapper);

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
            List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(new LambdaQueryWrapper<RepairPoolOrgRel>().eq(RepairPoolOrgRel::getRepairPoolCode, planCode));
            List<String> orgList = new ArrayList<>();
            if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
                orgList = repairPoolOrgRels.stream().map(r -> r.getOrgCode()).collect(Collectors.toList());
            }
            repair.setOrgName(manager.translateOrg(orgList));

            // 站点
            List<StationDTO> repairPoolStationRels = repairPoolStationRelMapper.selectStationList(planCode);
            repair.setStationName(manager.translateStation(repairPoolStationRels));

            // 周期类型
            repair.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repair.getType())));
            // 状态
            repair.setStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repair.getStatus())));

        });

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
                        .eq(RepairPoolRel::getRepairPoolStaId, req.getStandardId()));
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
                new LambdaQueryWrapper<RepairPoolCodeContent>().eq(RepairPoolCodeContent::getRepairPoolCodeId, id).orderByAsc(RepairPoolCodeContent::getSortNo));
        result.forEach(r->{
            r.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_PROJECT, String.valueOf(r.getType())));
            r.setStatusItemName(sysBaseAPI.translateDict(DictConstant.INSPECTION_STATUS_ITEM, String.valueOf(r.getStatusItem())));
        });

        return treeFirst(result);
    }

    /**
     * 构造树，不固定根节点
     *
     * @param list 全部数据
     * @return 构造好以后的树形
     */
    public static List<RepairPoolCodeContent> treeFirst(List<RepairPoolCodeContent> list) {
        //这里的Menu是我自己的实体类，参数只需要菜单id和父id即可，其他元素可任意增添
        Map<String, RepairPoolCodeContent> map = new HashMap<>(50);
        for (RepairPoolCodeContent treeNode : list) {
            map.put(treeNode.getId(), treeNode);
        }
        return addChildren(list, map);
    }

    /**
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
            }
            re.setOrgName(manager.translateOrg(orgList));

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
     * 检修详情里的适用专业下拉列表
     *
     * @param id
     * @return
     */
    @Override
    public List<ListDTO> queryMajorList(String id) {
        return null;
    }

    /**
     * 指派检修任务
     *
     * @param assignDTO
     * @return
     */
    @Override
    public Result assigned(AssignDTO assignDTO) {
        return null;
    }

    /**
     * 指派检修任务人员下拉列表
     *
     * @return
     */
    @Override
    public List<LoginUser> queryUserList() {
        return null;
    }

    /**
     * 根据检修计划单号查询对应的检修标准
     *
     * @param planCode code值
     * @return
     */
    private List<RepairPoolCode> queryStandardByCode(String planCode) {
        List<RepairPoolCode> repairPoolCodes = new ArrayList<>();
        List<RepairPoolRel> repairPoolRels = relMapper.selectList(new QueryWrapper<RepairPoolRel>().eq("repair_pool_code", planCode));
        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> standardList = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            repairPoolCodes = repairPoolCodeMapper.selectList(new QueryWrapper<RepairPoolCode>().in("id", standardList));
        }
        return repairPoolCodes;

    }
}
