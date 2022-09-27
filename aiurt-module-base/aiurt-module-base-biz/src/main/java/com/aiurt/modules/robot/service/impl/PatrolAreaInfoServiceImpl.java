package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.robot.dto.AreaPointDTO;
import com.aiurt.modules.robot.entity.PatrolAreaInfo;
import com.aiurt.modules.robot.manager.AreaPointTreeUtils;
import com.aiurt.modules.robot.mapper.PatrolAreaInfoMapper;
import com.aiurt.modules.robot.service.IPatrolAreaInfoService;
import com.aiurt.modules.robot.service.IPatrolPointInfoService;
import com.aiurt.modules.robot.service.IRobotInfoService;
import com.aiurt.modules.robot.taskdata.service.TaskDataService;
import com.aiurt.modules.robot.taskdata.wsdl.PatrolAreaInfos;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: patrol_area_info
 * @Author: aiurt
 * @Date: 2022-09-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class PatrolAreaInfoServiceImpl extends ServiceImpl<PatrolAreaInfoMapper, PatrolAreaInfo> implements IPatrolAreaInfoService {

    @Resource
    private TaskDataService taskDataService;
    @Resource
    private IPatrolPointInfoService pointInfoService;
    @Resource
    private IRobotInfoService robotInfoService;

    /**
     * 巡检区域和点位树形查询
     *
     * @param name 巡检区域名称或点位名称
     * @return 树形结构
     */
    @Override
    public List<AreaPointDTO> treelist(String name) {
        List<AreaPointDTO> result = baseMapper.treelist(name);
        return AreaPointTreeUtils.treeFirst(result);
    }

    /**
     * 同步巡检区域和点位
     *
     * @return
     */
    @Override
    public void synchronizeAreaAndPoint() {
        // 同步巡检区域
        this.synchronizeArea();

        // 同步巡检点位
        pointInfoService.synchronizePoint();
    }

    /**
     * 同步巡检区域
     */
    @Override
    public void synchronizeArea() {
        // 远程巡检区域数据
        PatrolAreaInfos patrolAreaInfo = taskDataService.getPatrolAreaInfo();

        if (ObjectUtil.isNotEmpty(patrolAreaInfo) && CollUtil.isNotEmpty(patrolAreaInfo.getInfos())) {
            // 目的是系统中的巡检区域名称不为空的，则不需要同步远程的巡检区域名称
            List<PatrolAreaInfo> patrolAreaInfos = baseMapper.selectList(new LambdaQueryWrapper<PatrolAreaInfo>().isNotNull(PatrolAreaInfo::getAreaName));
            Map<String, String> areaMap = Optional.ofNullable(patrolAreaInfos)
                    .orElse(CollUtil.newArrayList())
                    .stream().collect(Collectors.toMap(PatrolAreaInfo::getAreaId, PatrolAreaInfo::getAreaName));

            // 查询机器人ip对应的机器人id映射关系
            Map<String, String> map = robotInfoService.queryRobotIpMappingId();

            // 封装数据到list集合中，以便批量更新
            List<PatrolAreaInfo> result = CollUtil.newArrayList();
            List<com.aiurt.modules.robot.taskdata.wsdl.PatrolAreaInfo> infos = patrolAreaInfo.getInfos();
            PatrolAreaInfo p = null;
            for (com.aiurt.modules.robot.taskdata.wsdl.PatrolAreaInfo info : infos) {
                p = PatrolAreaInfo
                        .builder()
                        .areaId(info.getAreaId())
                        .areaName(isExistAreaName(areaMap, info.getAreaId()) ? areaMap.get(info.getAreaId()) : info.getAreaName())
                        .pid(info.getParentId())
                        .robotId(map.get(info.getRobotIp()))
                        .build();
                result.add(p);
            }


            // 批量更新巡检区域
            saveOrUpdateBatch(result);
        }
    }

    /**
     * 编辑巡检点位
     *
     * @param patrolAreaInfo
     */
    @Override
    public void updatePoint(PatrolAreaInfo patrolAreaInfo) {
        // 校验设备是否已经被绑定
        checkDeviceBind(patrolAreaInfo);

        // 更新
        baseMapper.updateById(patrolAreaInfo);
    }

    /**
     * 查询所有巡检区域
     *
     * @return
     */
    @Override
    public List<AreaPointDTO> selectAreaList() {
        return baseMapper.selectAreaList();
    }

    /**
     * 校验设备是否已经被绑定
     *
     * @param patrolAreaInfo
     */
    private void checkDeviceBind(PatrolAreaInfo patrolAreaInfo) {
        if (ObjectUtil.isEmpty(patrolAreaInfo)) {
            throw new AiurtBootException("巡检区域不存在");
        }
        if (StrUtil.isNotEmpty(patrolAreaInfo.getDeviceCode())) {
            int result = baseMapper.queryAreaByDeviceCode(patrolAreaInfo.getDeviceCode());
            if (result > 0) {
                throw new AiurtBootException("设备已被其他区域绑定,请更换设备后重试");
            }
        }
    }

    /**
     * 一条巡检区域数据是否已经存在区域名称
     *
     * @param areaMap 系统的巡检区域数据
     * @param areaId  区域id
     * @return
     */
    public boolean isExistAreaName(Map<String, String> areaMap, String areaId) {
        return MapUtil.isNotEmpty(areaMap) && StrUtil.isNotEmpty(areaMap.get(areaId));
    }


}
