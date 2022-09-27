package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.robot.entity.PatrolPointInfo;
import com.aiurt.modules.robot.mapper.PatrolPointInfoMapper;
import com.aiurt.modules.robot.service.IPatrolPointInfoService;
import com.aiurt.modules.robot.service.IRobotInfoService;
import com.aiurt.modules.robot.taskdata.service.TaskDataService;
import com.aiurt.modules.robot.taskdata.wsdl.PatrolPointInfos;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: patrol_point_info
 * @Author: aiurt
 * @Date: 2022-09-26
 * @Version: V1.0
 */
@Service
public class PatrolPointInfoServiceImpl extends ServiceImpl<PatrolPointInfoMapper, PatrolPointInfo> implements IPatrolPointInfoService {

    @Resource
    private TaskDataService taskDataService;
    @Resource
    private IRobotInfoService robotInfoService;

    /**
     * 同步巡检点位
     */
    @Override
    public void synchronizePoint() {
        // 远程巡检点位数据
        PatrolPointInfos patrolPointInfo = taskDataService.getPatrolPointInfo();

        if (ObjectUtil.isNotEmpty(patrolPointInfo) && CollUtil.isNotEmpty(patrolPointInfo.getInfos())) {
            // 目的是系统中的巡检点位名称不为空的，则不需要同步远程的巡检区域名称
            List<PatrolPointInfo> patrolPointInfos = baseMapper.selectList(new LambdaQueryWrapper<PatrolPointInfo>().isNotNull(PatrolPointInfo::getPointName));
            Map<String, String> pointMap = Optional.ofNullable(patrolPointInfos)
                    .orElse(CollUtil.newArrayList())
                    .stream().collect(Collectors.toMap(PatrolPointInfo::getPointId, PatrolPointInfo::getPointName));

            // 查询机器人ip对应的机器人id映射关系,[key机器人ip,value机器人id]
            Map<String, String> map = robotInfoService.queryRobotIpMappingId();

            // 封装数据到list集合中，以便批量更新
            List<PatrolPointInfo> result = CollUtil.newArrayList();
            List<com.aiurt.modules.robot.taskdata.wsdl.PatrolPointInfo> infos = patrolPointInfo.getInfos();
            PatrolPointInfo p = null;
            for (com.aiurt.modules.robot.taskdata.wsdl.PatrolPointInfo info : infos) {
                p = PatrolPointInfo
                        .builder()
                        .areaId(info.getAreaId())
                        .pointId(info.getPointId())
                        .robotId(map.get(info.getRobotIp()))
                        .pointName(isExistPointName(pointMap, info.getPointId()) ? pointMap.get(info.getPointId()) : info.getPointName())
                        .pointType(info.getPointType())
                        .build();
                result.add(p);
            }

            // 批量更新巡检区域
            saveOrUpdateBatch(result);
        }
    }

    /**
     * 一条巡检点位数据是否已经存在点位名称
     *
     * @param pointMap 系统的巡检点位数据
     * @param pointId  点位id
     * @return
     */
    public boolean isExistPointName(Map<String, String> pointMap, String pointId) {
        return MapUtil.isNotEmpty(pointMap) && StrUtil.isNotEmpty(pointMap.get(pointId));
    }
}
