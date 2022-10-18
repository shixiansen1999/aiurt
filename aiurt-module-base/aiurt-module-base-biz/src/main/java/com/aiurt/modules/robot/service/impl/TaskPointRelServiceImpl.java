package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.aiurt.modules.robot.dto.AreaPointDTO;
import com.aiurt.modules.robot.entity.TaskPointRel;
import com.aiurt.modules.robot.manager.AreaPointTreeUtils;
import com.aiurt.modules.robot.mapper.TaskPointRelMapper;
import com.aiurt.modules.robot.service.IPatrolAreaInfoService;
import com.aiurt.modules.robot.service.ITaskPointRelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: task_point_rel
 * @Author: aiurt
 * @Date: 2022-09-27
 * @Version: V1.0
 */
@Slf4j
@Service
public class TaskPointRelServiceImpl extends ServiceImpl<TaskPointRelMapper, TaskPointRel> implements ITaskPointRelService {

    @Resource
    @Lazy
    private IPatrolAreaInfoService patrolAreaInfoService;

    /**
     * 通过任务模板id查询巡检点位
     *
     * @param taskPathId 任务模板id
     * @return
     */
    @Override
    public List<AreaPointDTO> queryPointByTaskPathId(String taskPathId) {
        // 1、查询任务模板id对应的巡检点位
        List<AreaPointDTO> pointList = baseMapper.queryPointByTaskPathId(taskPathId);

        // 2、查询巡检点位对应的所有巡检区域
        if (CollUtil.isNotEmpty(pointList)) {
            // 存放结果
            Set<AreaPointDTO> parentNodeSet = CollUtil.newHashSet();

            // 巡检点位对应的巡检区域
            Set<String> areaIdSet = pointList.stream().map(AreaPointDTO::getPid).collect(Collectors.toSet());

            // 所有的巡检区域
            List<AreaPointDTO> areaAllList = patrolAreaInfoService.selectAreaList();

            // 封装对应的父节点
            areaIdSet.forEach(areaId -> {
                parentNodeSet.addAll(AreaPointTreeUtils.selectNode(areaId, areaAllList, CollUtil.newHashSet()));
            });

            // 将巡检区域添加到树形列表
            pointList.addAll(parentNodeSet);
        }

        // 3、构造区域点位树
        List<AreaPointDTO> result = AreaPointTreeUtils.treeFirst(pointList);
        return result;
    }

    /**
     * 更新任务模板对应的点位信息
     *
     * @param taskPointRelMap （key为任务模板id,value为点位id集合）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleTaskPointRel(Map<String, List<String>> taskPointRelMap) {
        if (MapUtil.isEmpty(taskPointRelMap)) {
            return;
        }

        // 先将存在的对应关系全部删除
        baseMapper.delete(new LambdaQueryWrapper<TaskPointRel>().in(TaskPointRel::getTaskPathId, taskPointRelMap.keySet()));

        // 统一封装成对象，目的是批量保存
        List<TaskPointRel> result = CollUtil.newArrayList();
        for (Map.Entry<String, List<String>> entry : taskPointRelMap.entrySet()) {
            List<String> pointList = entry.getValue();
            if (CollUtil.isEmpty(pointList)) {
                continue;
            }

            // 封装对象
            TaskPointRel taskPointRel = null;
            for (String point : pointList) {
                taskPointRel = TaskPointRel
                        .builder()
                        .pointId(point)
                        .taskPathId(entry.getKey())
                        .build();
                result.add(taskPointRel);
            }
        }

        // 批量保存
        saveBatch(result);
    }
}
