package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.robot.constant.RobotConstant;
import com.aiurt.modules.robot.constant.RobotDictConstant;
import com.aiurt.modules.robot.dto.TaskFinishDTO;
import com.aiurt.modules.robot.entity.TaskExcuteInfo;
import com.aiurt.modules.robot.entity.TaskFinishInfo;
import com.aiurt.modules.robot.mapper.TaskExcuteInfoMapper;
import com.aiurt.modules.robot.mapper.TaskFinishInfoMapper;
import com.aiurt.modules.robot.service.IRobotInfoService;
import com.aiurt.modules.robot.service.ITaskExcuteInfoService;
import com.aiurt.modules.robot.service.ITaskFinishInfoService;
import com.aiurt.modules.robot.taskfinish.service.TaskFinishService;
import com.aiurt.modules.robot.taskfinish.wsdl.TaskFinishInfos;
import com.aiurt.modules.robot.vo.TaskFinishInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    @Resource
    private ISysBaseAPI sysBaseApi;
    @Resource
    private TaskFinishService taskFinishService;
    @Resource
    private IRobotInfoService robotInfoService;
    @Resource
    private ITaskExcuteInfoService taskExcuteInfoService;
    @Resource
    private TaskFinishInfoMapper taskFinishInfoMapper;
    @Resource
    private TaskExcuteInfoMapper taskExcuteInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronizeRobotTask(Date startTime, Date endTime) {
        if (ObjectUtil.isEmpty(startTime) || ObjectUtil.isEmpty(endTime)) {
            Date now = new Date();
            startTime = DateUtil.parse(DateUtil.format(now, "yyyy-MM-dd 00:00:00"));
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
        Map<String, String> robotIds = robotInfoService.queryRobotIpMappingId(new ArrayList<>());
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
        if (CollUtil.isEmpty(list)) {
            return;
        }
        // taskId能唯一确定一条记录
        List<String> taskIds = list.stream().map(TaskFinishInfo::getTaskId).collect(Collectors.toList());
        QueryWrapper<TaskFinishInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(TaskFinishInfo::getTaskId, taskIds);
        // 数据库已有的数据
        List<TaskFinishInfo> oldInfos = taskFinishInfoMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(oldInfos)) {
            // 需要更新的字段
            for (TaskFinishInfo info : list) {
                TaskFinishInfo oldInfo = oldInfos.stream().filter(l -> info.getTaskId().equals(l.getTaskId())).findAny().orElse(null);
                if (ObjectUtil.isNotEmpty(info) && ObjectUtil.isNotEmpty(oldInfo)) {
                    oldInfo.setTaskName(info.getTaskName());
                    oldInfo.setTaskType(info.getTaskType());
                    oldInfo.setTaskPathId(info.getTaskPathId());
                    oldInfo.setStartTime(info.getStartTime());
                    oldInfo.setEndTime(info.getEndTime());
                    oldInfo.setFinishState(info.getFinishState());
                    oldInfo.setRobotId(info.getRobotId());
                    // 把更新后的对象属性拷贝回新的数据列表中
                    BeanUtils.copyProperties(oldInfo, info);
                }
            }
        }
        this.saveOrUpdateBatch(list);

        // 同步任务的巡检记录
        List<TaskExcuteInfo> excuteInfos = new ArrayList<>();
        list.stream().forEach(l -> {
            List<TaskExcuteInfo> taskExcuteInfos = taskExcuteInfoService.getSynchronizeRobotTaskExcuteInfo(l.getTaskId());
            excuteInfos.addAll(taskExcuteInfos);
        });
        taskExcuteInfoService.synchronizeRobotTaskExcuteInfo(excuteInfos);
    }

    @Override
    public IPage<TaskFinishInfoVO> queryPageList(Page<TaskFinishInfoVO> page, TaskFinishDTO taskFinishDTO) {
        // 前端只能传线路id
        if (ObjectUtil.isNotEmpty(taskFinishDTO) && ObjectUtil.isNotEmpty(taskFinishDTO.getLineId())) {
            String lineCode = sysBaseApi.getLineCodeById(taskFinishDTO.getLineId());
            taskFinishDTO.setLineCode(lineCode);
        }
        IPage<TaskFinishInfoVO> pageList = taskFinishInfoMapper.queryPageList(page, taskFinishDTO);
        List<String> lineCodes = pageList.getRecords().stream().map(TaskFinishInfoVO::getLineCode).collect(Collectors.toList());
        List<String> stationCodes = pageList.getRecords().stream().map(TaskFinishInfoVO::getStationCode).collect(Collectors.toList());

        Map<String, String> lineNames = sysBaseApi.getLineNameByCode(lineCodes);
        Map<String, String> stationNames = sysBaseApi.getStationNameByCode(stationCodes);
        String normal = "正常";
        String abnormal = "异常";

        // 字典翻译
        Map<String, String> stateItems = sysBaseApi.queryDictItemsByCode(RobotDictConstant.ROBOT_TASK_STATE)
                .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        Map<String, String> disposeItems = sysBaseApi.queryDictItemsByCode(RobotDictConstant.ROBOT_DISPOSE)
                .stream().collect(Collectors.toMap(k -> k.getValue(), v -> v.getText(), (a, b) -> a));
        for (TaskFinishInfoVO infoVO : pageList.getRecords()) {
            // 任务结果
            LambdaQueryWrapper<TaskExcuteInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TaskExcuteInfo::getTaskId, infoVO.getTaskId())
                    .eq(TaskExcuteInfo::getExcuteState, abnormal)
                    .last("limit 1");
            TaskExcuteInfo taskExcuteInfo = taskExcuteInfoMapper.selectOne(wrapper);
            infoVO.setTaskResult(normal);
            if (ObjectUtil.isNotEmpty(taskExcuteInfo)) {
                infoVO.setTaskResult(abnormal);
            }
            infoVO.setFinishStateDictName(stateItems.get(infoVO.getFinishState()));
            infoVO.setIsHandleDictName(disposeItems.get(String.valueOf(infoVO.getIsHandle())));
            infoVO.setLineName(lineNames.get(infoVO.getLineCode()));
            infoVO.setStationName(stationNames.get(infoVO.getStationCode()));
        }
        return pageList;
    }

    @Override
    public void taskDispose(String id, String handleExplain) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new JeecgBootException("检测到当前为未登录状态，请先登录！");
        }
        TaskFinishInfo info = this.getById(id);
        if (ObjectUtil.isEmpty(info)) {
            throw new JeecgBootException("未找到id为【" + id + "】的记录！");
        }
        info.setHandleUserId(loginUser.getId());
        info.setHandleTime(new Date());
        info.setHandleExplain(handleExplain);
        info.setIsHandle(RobotConstant.TASK_DISPOSE_1);
        taskFinishInfoMapper.updateById(info);
    }

}
