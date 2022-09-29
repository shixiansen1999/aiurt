package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.robot.constant.RobotConstant;
import com.aiurt.modules.robot.constant.RobotDictConstant;
import com.aiurt.modules.robot.dto.TaskFinishDTO;
import com.aiurt.modules.robot.entity.TaskExcuteInfo;
import com.aiurt.modules.robot.entity.TaskFinishInfo;
import com.aiurt.modules.robot.mapper.TaskExcuteInfoMapper;
import com.aiurt.modules.robot.mapper.TaskFinishInfoMapper;
import com.aiurt.modules.robot.service.IRobotInfoService;
import com.aiurt.modules.robot.service.ITaskFinishInfoService;
import com.aiurt.modules.robot.taskfinish.service.TaskFinishService;
import com.aiurt.modules.robot.taskfinish.wsdl.TaskFinishInfos;
import com.aiurt.modules.robot.vo.TaskFinishInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: task_finish_info
 * @Author: aiurt
 * @Date: 2022-09-28
 * @Version: V1.0
 */
@Service
public class TaskFinishInfoServiceImpl extends ServiceImpl<TaskFinishInfoMapper, TaskFinishInfo> implements ITaskFinishInfoService {
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private TaskFinishService taskFinishService;
    @Autowired
    private IRobotInfoService robotInfoService;
    @Autowired
    private TaskFinishInfoMapper taskFinishInfoMapper;
    @Autowired
    private TaskExcuteInfoMapper taskExcuteInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronizeRobotTask(Date startTime, Date endTime) {
        if (ObjectUtil.isEmpty(startTime) || ObjectUtil.isEmpty(endTime)) {
            Date now = new Date();
            startTime = DateUtil.offsetHour(now, -1);
            endTime = now;
        }
        String format = "yyyy-MM-dd HH:mm:ss";
        String formatStartTime = DateUtil.format(startTime, format);
        String formatEndTime = DateUtil.format(endTime, format);
        TaskFinishInfos taskFinishInfos = taskFinishService.getTaskFinishInfoByTime(formatStartTime, formatEndTime);
        List<com.aiurt.modules.robot.taskfinish.wsdl.TaskFinishInfo> infos = Optional.ofNullable(taskFinishInfos)
                .orElseGet(TaskFinishInfos::new).getInfos();
        List<TaskFinishInfo> list = new ArrayList<>();
        // 获取所有机器人的id
        Map<String, String> robotIds = robotInfoService.queryIdMappingRobotIp(new ArrayList<>());
        TaskFinishInfo taskFinishInfo = null;
        for (com.aiurt.modules.robot.taskfinish.wsdl.TaskFinishInfo info : infos) {
            taskFinishInfo = new TaskFinishInfo();
            taskFinishInfo.setTaskId(info.getTaskId())
                    .setTaskName(info.getTaskName())
                    .setTaskType(info.getTaskType())
                    .setTaskPathId(info.getTaskPathId())
                    .setStartTime(StrUtil.isEmpty(info.getStartTime()) ? null : DateUtil.parse(info.getStartTime(), format))
                    .setEndTime(StrUtil.isEmpty(info.getEndTime()) ? null : DateUtil.parse(info.getEndTime(), format))
                    .setFinishState(info.getFinishState())
                    .setRobotId(robotIds.get(info.getExcuteRobot()));
            list.add(taskFinishInfo);
        }
        // todo 处理旧数据
        // 删除系统上的任务模板数据
        LambdaQueryWrapper<TaskFinishInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.notIn(TaskFinishInfo::getTaskPathId, list.stream().map(TaskFinishInfo::getTaskPathId).collect(Collectors.toList()));
        baseMapper.delete(wrapper);
        // 批量更新任务模板信息
        saveOrUpdateBatch(list);
    }

    @Override
    public IPage<TaskFinishInfoVO> queryPageList(Page<TaskFinishInfoVO> page, TaskFinishDTO taskFinishDTO) {
        IPage<TaskFinishInfoVO> pageList = taskFinishInfoMapper.queryPageList(page, taskFinishDTO);
        List<String> lineCodes = pageList.getRecords().stream().map(TaskFinishInfoVO::getLineCode).collect(Collectors.toList());
        List<String> stationCodes = pageList.getRecords().stream().map(TaskFinishInfoVO::getStationCode).collect(Collectors.toList());
        Map<String, String> lineNames = sysBaseApi.getLineNameByCode(lineCodes);
        Map<String, String> stationNames = sysBaseApi.getStationNameByCode(stationCodes);
        String normal = "正常";
        String abnormal = "异常";
        // 字典翻译
        Map<String, String> disposeItems = sysBaseApi.getDictItems(RobotDictConstant.ROBOT_DISPOSE)
                .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        for (TaskFinishInfoVO infoVO : pageList.getRecords()) {
            // 任务结果
            LambdaQueryWrapper<TaskExcuteInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TaskExcuteInfo::getExcuteState, abnormal).last("limit 1");
            TaskExcuteInfo taskExcuteInfo = taskExcuteInfoMapper.selectOne(wrapper);
            infoVO.setTaskResult(normal);
            if (ObjectUtil.isNotEmpty(taskExcuteInfo)) {
                infoVO.setTaskResult(abnormal);
            }
            infoVO.setIsHandleDictName(disposeItems.get(infoVO.getIsHandle()));
            infoVO.setLineName(lineNames.get(infoVO.getLineCode()));
            infoVO.setStationName(stationNames.get(infoVO.getStationCode()));
        }
        return pageList;
    }

    @Override
    public void taskDispose(String id, String handleExplain) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到当前为未登录状态，请先登录！");
        }
        TaskFinishInfo info = new TaskFinishInfo();
        info.setId(id);
        info.setHandleUserId(loginUser.getId());
        info.setHandleTime(new Date());
        info.setHandleExplain(handleExplain);
        info.setIsHandle(RobotConstant.TASK_DISPOSE_1);
        taskFinishInfoMapper.updateById(info);
    }

}
