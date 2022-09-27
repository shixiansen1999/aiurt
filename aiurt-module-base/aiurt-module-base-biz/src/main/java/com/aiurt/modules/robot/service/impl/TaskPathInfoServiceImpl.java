package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.robot.dto.TaskPathInfoDTO;
import com.aiurt.modules.robot.entity.TaskPathInfo;
import com.aiurt.modules.robot.mapper.TaskPathInfoMapper;
import com.aiurt.modules.robot.service.ITaskPathInfoService;
import com.aiurt.modules.robot.service.ITaskPointRelService;
import com.aiurt.modules.robot.taskdata.service.TaskDataService;
import com.aiurt.modules.robot.taskdata.wsdl.TaskPathInfos;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: task_path_info
 * @Author: aiurt
 * @Date: 2022-09-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class TaskPathInfoServiceImpl extends ServiceImpl<TaskPathInfoMapper, TaskPathInfo> implements ITaskPathInfoService {

    @Resource
    private TaskDataService taskDataService;
    @Resource
    private ITaskPointRelService taskPointRelService;

    /**
     * 任务模板列表分页查询
     *
     * @param page         分页参数
     * @param taskPathInfo 查询条件
     * @return
     */
    @Override
    public IPage<TaskPathInfoDTO> queryPageList(Page<TaskPathInfoDTO> page, TaskPathInfoDTO taskPathInfo) {
        return page.setRecords(baseMapper.queryPageList(page, taskPathInfo));
    }

    /**
     * 同步机器人任务模板
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronizeTaskPathInfo() {
        // 远程机器人任务模板信息
        TaskPathInfos taskPathInfo = taskDataService.getTaskPathInfo();

        if (ObjectUtil.isEmpty(taskPathInfo) || CollUtil.isEmpty(taskPathInfo.getInfos())) {
            log.info("机器人任务模板无信息同步");
            return;
        }

        // 统一构建对象，目的批量更新
        List<TaskPathInfo> result = CollUtil.newArrayList();
        List<com.aiurt.modules.robot.taskdata.wsdl.TaskPathInfo> infos = taskPathInfo.getInfos();
        TaskPathInfo taskInfo = null;
        for (com.aiurt.modules.robot.taskdata.wsdl.TaskPathInfo info : infos) {
            taskInfo = TaskPathInfo
                    .builder()
                    .taskPathId(info.getTaskPathId())
                    .taskPathName(info.getTaskPathName())
                    .taskPathType(info.getTaskPathType())
                    .finishAction(info.getFinishAction())
                    .createTime(StrUtil.isNotEmpty(info.getCreateTime()) ? DateUtil.parse(info.getCreateTime()) : new Date())
                    .build();
            result.add(taskInfo);
        }

        // 批量更新任务模板信息
        saveOrUpdateBatch(result);

        // 更新关联的点位数据
        Map<String, List<String>> taskPointRelMap = infos.stream().collect(Collectors.toMap(com.aiurt.modules.robot.taskdata.wsdl.TaskPathInfo::getTaskPathId, com.aiurt.modules.robot.taskdata.wsdl.TaskPathInfo::getPointList));
        taskPointRelService.handleTaskPointRel(taskPointRelMap);
    }

}
