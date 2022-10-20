package com.aiurt.modules.robot.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.robot.constant.RobotConstant;
import com.aiurt.modules.robot.dto.TaskPatrolDTO;
import com.aiurt.modules.robot.entity.IpMapping;
import com.aiurt.modules.robot.entity.TaskExcuteInfo;
import com.aiurt.modules.robot.mapper.TaskExcuteInfoMapper;
import com.aiurt.modules.robot.service.IIpMappingService;
import com.aiurt.modules.robot.service.IPatrolPointInfoService;
import com.aiurt.modules.robot.service.ITaskExcuteInfoService;
import com.aiurt.modules.robot.taskfinish.service.TaskFinishService;
import com.aiurt.modules.robot.taskfinish.wsdl.TaskExcuteInfos;
import com.aiurt.modules.robot.vo.DeviceInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description: task_excute_info
 * @Author: aiurt
 * @Date: 2022-09-29
 * @Version: V1.0
 */
@Service
public class TaskExcuteInfoServiceImpl extends ServiceImpl<TaskExcuteInfoMapper, TaskExcuteInfo> implements ITaskExcuteInfoService {
    @Resource
    private TaskFinishService taskFinishService;
    @Resource
    private IPatrolPointInfoService patrolPointInfoService;
    @Resource
    TaskExcuteInfoMapper taskExcuteInfoMapper;
    @Resource
    private ISysBaseAPI sysBaseApi;
    @Resource
    private IIpMappingService ipMappingService;

    @Override
    public List<TaskExcuteInfo> getSynchronizeRobotTaskExcuteInfo(String taskId) {
        TaskExcuteInfos taskExcuteInfos = taskFinishService.getTaskExcuteInfoByTaskId(taskId);
        List<com.aiurt.modules.robot.taskfinish.wsdl.TaskExcuteInfo> infos = Optional.ofNullable(taskExcuteInfos)
                .orElseGet(TaskExcuteInfos::new).getInfos();
        List<String> points = infos.stream().map(com.aiurt.modules.robot.taskfinish.wsdl.TaskExcuteInfo::getPointId)
                .collect(Collectors.toList());
        // 根据点位ID获取设备Code
        Map<String, String> deviceCodeMap = patrolPointInfoService.getDeviceCodeByPointId(points);

        List<TaskExcuteInfo> list = new ArrayList<>();
        String format = "yyyy-MM-dd HH:mm:ss";
        TaskExcuteInfo taskExcuteInfo = null;
        for (com.aiurt.modules.robot.taskfinish.wsdl.TaskExcuteInfo info : infos) {
            taskExcuteInfo = new TaskExcuteInfo();
            taskExcuteInfo.setTaskId(info.getTaskId())
                    .setTaskPathId(info.getTaskPathId())
                    .setTargetId(info.getTargetId())
                    .setPointId(info.getPointId())
                    .setExcuteValue(info.getExcuteValue())
                    .setExcuteState(info.getExcuteState())
                    .setExcuteDesc(info.getExcuteDesc())
                    .setHdPicture(info.getHDPicture())
                    .setInfraredPicture(info.getInfraredPicture())
                    .setExcuteTime(StrUtil.isEmpty(info.getExcuteTime()) ? null : DateUtil.parse(info.getExcuteTime(), format))
                    .setDevice(deviceCodeMap.get(info.getPointId()));
            list.add(taskExcuteInfo);
        }
        return list;
    }

    /**
     * 同步任务完成数据信息
     *
     * @param taskExcuteInfos
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronizeRobotTaskExcuteInfo(List<TaskExcuteInfo> taskExcuteInfos) {
        List<String> taskIds = taskExcuteInfos.stream().map(TaskExcuteInfo::getTaskId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(taskIds)) {
            QueryWrapper<TaskExcuteInfo> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.lambda().in(TaskExcuteInfo::getTaskId, taskIds);
            this.remove(deleteWrapper);
        }
        this.saveBatch(taskExcuteInfos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronizeTaskExcuteInfo(List<TaskExcuteInfo> taskExcuteInfos) {
        List<String> taskIds = taskExcuteInfos.stream().map(TaskExcuteInfo::getTaskId).collect(Collectors.toList());
        if (CollUtil.isEmpty(taskIds) || CollUtil.isEmpty(taskExcuteInfos)) {
            return;
        }

        // 需要下载图片的数据集合
        List<TaskExcuteInfo> list = new ArrayList<>();

        // 查询本地存在的任务记录
        QueryWrapper<TaskExcuteInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(TaskExcuteInfo::getTaskId, taskIds);
        List<TaskExcuteInfo> localInfos = this.list(wrapper);

        // 遍历远程任务数据taskExcuteInfos
        for (TaskExcuteInfo taskExcuteInfo : taskExcuteInfos) {
            String targetId = taskExcuteInfo.getTargetId();
            // 根据巡检结果id查询一条记录
            TaskExcuteInfo localExcuteInfo = localInfos.stream().filter(l -> targetId.equals(l.getTargetId())).findAny().orElse(null);

            // 本地不存在的新数据,记录图片地址
            if (ObjectUtil.isEmpty(localExcuteInfo)) {
                list.add(taskExcuteInfo);
                continue;
            }

            // 将本地记录的id字段值赋值给新记录的id
            taskExcuteInfo.setId(localExcuteInfo.getId());
            // 本地数据的图片地址为空，远程同步的数据不为空时
            if (StrUtil.isEmpty(localExcuteInfo.getHdPicture()) && StrUtil.isNotEmpty(taskExcuteInfo.getHdPicture())) {
                list.add(taskExcuteInfo);
            }
        }

        // 更新不需要下载图片的记录
        this.saveOrUpdateBatch(taskExcuteInfos);

        // 更新需要下载图片的记录
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(l -> {
                l.setHdPicture(sysBaseApi.remoteUploadLocal(l.getHdPicture(), ""));
            });
            this.saveOrUpdateBatch(list);
        }
    }

    @Override
    public List<DeviceInfoVO> getDeviceInfo(String taskId) {
        List<TaskExcuteInfo> excuteInfos = this.lambdaQuery().eq(TaskExcuteInfo::getTaskId, taskId)
                .select(TaskExcuteInfo::getDevice).list();
        List<String> deviceCodes = excuteInfos.stream()
                .distinct()
                .map(TaskExcuteInfo::getDevice)
                .collect(Collectors.toList());
        List<DeviceInfoVO> list = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(deviceCodes)) {
            Map<String, String> deviceMap = sysBaseApi.getDeviceNameByCode(deviceCodes);
            DeviceInfoVO deviceInfo = null;
            for (String code : deviceCodes) {
                deviceInfo = new DeviceInfoVO();
                deviceInfo.setDevice(code);
                deviceInfo.setDeviceName(deviceMap.get(code));
                list.add(deviceInfo);
            }
        }
        return list;
    }

    @Override
    public IPage<TaskPatrolDTO> getPatrolListPage(Page<TaskPatrolDTO> page, String taskId, String device, String excuteState) {
        IPage<TaskPatrolDTO> patrolListPage = taskExcuteInfoMapper.getPatrolListPage(page, taskId, device, excuteState);

        List<TaskPatrolDTO> records = patrolListPage.getRecords();

        // 图片地址外网转换
        for (TaskPatrolDTO record : records) {
            ipAndPortMapping(record);
        }

        return patrolListPage;
    }

    /**
     * 将图片地址中的ip+端口替换成外网ip+端口
     *
     * @param record
     */
    private void ipAndPortMapping(TaskPatrolDTO record) {
        //匹配ip+port的正则表达式
        String regIpAndPort = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?\\:(\\d+))";

        // 判空
        if (ObjectUtil.isEmpty(record) || StrUtil.isEmpty(record.getHdPicture())) {
            return;
        }

        // 匹配图片地址上面的ip+port
        Pattern pattern = Pattern.compile(regIpAndPort);
        Matcher matcher = pattern.matcher(record.getHdPicture());
        String oldIpAndPort = "";
        while (matcher.find()) {
            oldIpAndPort = matcher.group();
        }

        // 没有匹配到ip+port，则直接返回
        if (StrUtil.isEmpty(oldIpAndPort)) {
            return;
        }

        // 根据旧的ip去获取映射表，如果有对应的外网地址映射才进入
        String oldIp = oldIpAndPort.substring(0, oldIpAndPort.indexOf(":"));
        String oldPort = oldIpAndPort.substring(oldIpAndPort.indexOf(":") + 1);

        // 图片地址外网转换
        LambdaQueryWrapper<IpMapping> lam = new LambdaQueryWrapper<>();
        lam.eq(IpMapping::getIsMapping, RobotConstant.IS_MAPPING_1);
        lam.in(IpMapping::getInsideIp, oldIp);
        lam.in(IpMapping::getInsidePort, oldPort);
        List<IpMapping> ipList = ipMappingService.getBaseMapper().selectList(lam);

        // 没有对应外网映射地址
        if (CollUtil.isEmpty(ipList)) {
            return;
        }

        IpMapping newIpMapping = ipList.get(0);
        String newIp = newIpMapping.getOutsideIp();
        Integer newPort = newIpMapping.getOutsidePort();

        // 没有对应的外网ip+port
        if (StrUtil.isEmpty(newIp) || ObjectUtil.isEmpty(newPort)) {
            return;
        }

        String newIpAndPort = newIp + ":" + newPort;
        String result = record.getHdPicture().replaceAll(regIpAndPort, newIpAndPort);
        record.setHdPicture(result);

    }
}
