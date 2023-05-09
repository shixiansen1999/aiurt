package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolCode;
import com.aiurt.boot.plan.entity.RepairPoolOrgRel;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.plan.mapper.RepairPoolStationRelMapper;
import org.jeecg.common.system.api.ISysBaseAPI;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description: 线程池处理类
 * @date 2022/7/129:05
 */

public class PoolThreadService implements Callable<RepairPool> {
    private RepairPool repairPool;

    private ISysBaseAPI sysBaseApi;

    private InspectionManager manager;

    private RepairPoolStationRelMapper repairPoolStationRelMapper;

    private RepairPoolMapper repairPoolMapper;

    private Map<String, String> cycleTypeMap;

    private Map<String, String> inspectionTaskStateMap;

    private Map<String, String> workTypeMap;

    private List<RepairPoolOrgRel> allRepairPoolOrgRels;

    private Map<String, List<StationDTO>> allRepairPoolStationRels;

    public PoolThreadService(RepairPool repairPool,
                             ISysBaseAPI sysBaseApi,
                             InspectionManager manager,
                             RepairPoolStationRelMapper repairPoolStationRelMapper,
                             RepairPoolMapper repairPoolMapper,
                             Map<String, String> cycleTypeMap,
                             Map<String, String> inspectionTaskStateMap,
                             Map<String, String> workTypeMap,
                             List<RepairPoolOrgRel> allRepairPoolOrgRels,
                             Map<String, List<StationDTO>> allRepairPoolStationRels) {
        this.repairPool = repairPool;
        this.sysBaseApi = sysBaseApi;
        this.manager = manager;
        this.repairPoolStationRelMapper = repairPoolStationRelMapper;
        this.repairPoolMapper = repairPoolMapper;
        this.cycleTypeMap = cycleTypeMap;
        this.inspectionTaskStateMap = inspectionTaskStateMap;
        this.workTypeMap = workTypeMap;
        this.allRepairPoolOrgRels = allRepairPoolOrgRels;
        this.allRepairPoolStationRels = allRepairPoolStationRels;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public RepairPool call() throws Exception {
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            // 查询检修计划对应的专业和专业子系统
            String planCode = repairPool.getCode();

            // 检修计划单号查询对应的检修标准
            List<RepairPoolCode> repairPoolPoolCodes = repairPoolMapper.queryStandardByCode(planCode);

            if (CollUtil.isNotEmpty(repairPoolPoolCodes)) {
                // 专业
                repairPool.setMajorName(manager.translateMajor(repairPoolPoolCodes.stream().map(RepairPoolCode::getMajorCode).collect(Collectors.toList()), InspectionConstant.MAJOR));
                // 子系统
                repairPool.setSubsystemName(manager.translateMajor(repairPoolPoolCodes.stream().map(RepairPoolCode::getSubsystemCode).collect(Collectors.toList()), InspectionConstant.SUBSYSTEM));
            }

            // 组织机构
            List<String> orgList = allRepairPoolOrgRels.stream()
                    .filter(orgRel -> orgRel.getRepairPoolCode().equals(repairPool.getCode()))
                    .map(RepairPoolOrgRel::getOrgCode)
                    .collect(Collectors.toList());
            repairPool.setOrgName(manager.translateOrg(orgList));

            // 站点
            List<StationDTO> repairPoolStationRels = allRepairPoolStationRels.getOrDefault(repairPool.getCode(), Collections.emptyList());
            repairPool.setStationName(manager.translateStation(repairPoolStationRels));
            // 周期类型
            repairPool.setTypeName(cycleTypeMap.get(String.valueOf(repairPool.getType())));
            // 状态
            repairPool.setStatusName(inspectionTaskStateMap.get(String.valueOf(repairPool.getStatus())));
            // 作业类型
            repairPool.setWorkTypeName(workTypeMap.get(String.valueOf(repairPool.getWorkType())));
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
        return repairPool;
    }


}
