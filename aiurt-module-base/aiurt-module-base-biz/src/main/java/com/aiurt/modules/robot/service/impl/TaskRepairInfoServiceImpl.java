package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.robot.dto.TaskRepairInfoDTO;
import com.aiurt.modules.robot.entity.TaskRepairInfo;
import com.aiurt.modules.robot.mapper.TaskRepairInfoMapper;
import com.aiurt.modules.robot.service.ITaskRepairInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description: task_repair_info
 * @Author: jeecg-boot
 * @Date: 2022-10-08
 * @Version: V1.0
 */
@Service
public class TaskRepairInfoServiceImpl extends ServiceImpl<TaskRepairInfoMapper, TaskRepairInfo> implements ITaskRepairInfoService {
    @Resource
    private TaskRepairInfoMapper taskRepairInfoMapper;

    @Override
    public void add(TaskRepairInfoDTO taskRepairInfoDTO) {
        String taskId = taskRepairInfoDTO.getTaskId();
        List<String> repairCodes = taskRepairInfoDTO.getRepairCodes();
        if (CollectionUtil.isEmpty(repairCodes)) {
            throw new AiurtBootException("报修编码为空!");
        }
        List<TaskRepairInfo> list = new ArrayList<>();
        Set<String> codes = new HashSet<>(repairCodes);
        codes.forEach(code -> {
            TaskRepairInfo taskRepairInfo = new TaskRepairInfo();
            taskRepairInfo.setTaskId(taskId);
            taskRepairInfo.setRepairCode(code);
            list.add(taskRepairInfo);

        });
        this.saveBatch(list);
    }

    @Override
    public List<TaskRepairInfo> queryByTaskId(String taskId) {
        List<TaskRepairInfo> taskRepairInfos = taskRepairInfoMapper.queryByTaskId(taskId);
        return taskRepairInfos;
    }
}
