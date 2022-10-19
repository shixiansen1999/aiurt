package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.encryption.CryptoUtils;
import com.aiurt.modules.robot.constant.RobotConstant;
import com.aiurt.modules.robot.entity.IpMapping;
import com.aiurt.modules.robot.entity.RobotInfo;
import com.aiurt.modules.robot.manager.AreaPointTreeUtils;
import com.aiurt.modules.robot.mapper.PatrolPointInfoMapper;
import com.aiurt.modules.robot.mapper.RobotInfoMapper;
import com.aiurt.modules.robot.mapper.TaskFinishInfoMapper;
import com.aiurt.modules.robot.robotdata.service.RobotDataService;
import com.aiurt.modules.robot.robotdata.wsdl.*;
import com.aiurt.modules.robot.service.IIpMappingService;
import com.aiurt.modules.robot.service.IRobotInfoService;
import com.aiurt.modules.robot.service.ITaskPathInfoService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: robot_info
 * @Author: aiurt
 * @Date: 2022-09-23
 * @Version: V1.0
 */
@Slf4j
@Service
public class RobotInfoServiceImpl extends ServiceImpl<RobotInfoMapper, RobotInfo> implements IRobotInfoService {
    // 密钥
    private final static String KEY = "eGDKUPcAhMET7FBKPiYCmA==";
    @Resource
    private RobotDataService robotDataService;
    @Resource
    private TaskFinishInfoMapper taskFinishInfoMapper;
    @Resource
    private PatrolPointInfoMapper patrolPointInfoMapper;
    @Resource
    private ITaskPathInfoService taskPathInfoService;
    @Resource
    private IIpMappingService ipMappingService;

    /**
     * 添加机器人
     *
     * @param robotInfo
     */
    @Override
    public void saveRobot(RobotInfo robotInfo) {
        // 校验是否有不符合的原因
        checkCreateOrUpdate(robotInfo);

        // 同步远程机器人基础数据
        synchronizeRobotData(CollUtil.newArrayList(robotInfo));

        // 保存
        baseMapper.insert(robotInfo);
    }


    /**
     * 编辑机器人
     *
     * @param robotInfo
     */
    @Override
    public void updateRobotById(RobotInfo robotInfo) {
        // 校验是否有不符合的原因
        checkCreateOrUpdate(robotInfo);

        // 同步远程机器人基础数据
        synchronizeRobotData(CollUtil.newArrayList(robotInfo));

        // 更新
        baseMapper.updateById(robotInfo);
    }

    /**
     * 删除机器人
     *
     * @param id
     */
    @Override
    public void deleteRobot(String id) {
        // 校验机器人是否存在
        RobotInfo robotInfo = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(robotInfo)) {
            throw new AiurtBootException("删除失败，机器人信息不存在");
        }

        // 查询是否有关联信息，有则无法删除
        isRelation(id);

        // 删除机器人信息
        baseMapper.deleteById(id);
    }

    /**
     * 查询机器人任务列表是否有关联数据
     *
     * @param id
     */
    private void isRelation(String id) {
        // 机器人任务列表关联数据
        Long taskNum = taskFinishInfoMapper.selectCount(new LambdaQueryWrapper<com.aiurt.modules.robot.entity.TaskFinishInfo>().eq(com.aiurt.modules.robot.entity.TaskFinishInfo::getRobotId, id));
        // 机器人点位关联数据
        Long patrolPointNum = patrolPointInfoMapper.selectCount(new LambdaQueryWrapper<com.aiurt.modules.robot.entity.PatrolPointInfo>().eq(com.aiurt.modules.robot.entity.PatrolPointInfo::getRobotId, id));
        if (taskNum > 0 || patrolPointNum > 0) {
            throw new AiurtBootException("该机器人存在关联数据，无法删除");
        }
    }

    /**
     * 查询机器人ip对应的机器人id映射关系
     *
     * @param robotIpList 机器人ip集合，如果为空则是查询全部机器人数据
     * @return ip对应的id映射表 [key机器人ip,value机器人id]
     */
    @Override
    public Map<String, String> queryRobotIpMappingId(List<String> robotIpList) {
        LambdaQueryWrapper<RobotInfo> lam = new LambdaQueryWrapper<>();
        lam.isNotNull(RobotInfo::getRobotIp);
        if (CollUtil.isNotEmpty(robotIpList)) {
            lam.eq(RobotInfo::getRobotIp, robotIpList);
        }
        List<RobotInfo> robotInfos = baseMapper.selectList(lam);

        // 没有数据直接返回
        if (CollUtil.isEmpty(robotInfos)) {
            return CollUtil.newHashMap();
        }

        // 将list转成map[key机器人ip,value机器人id]
        Map<String, String> robotIpMap = robotInfos.stream().collect(Collectors.toMap(RobotInfo::getRobotIp, RobotInfo::getRobotId));
        return MapUtil.isNotEmpty(robotIpMap) ? robotIpMap : CollUtil.newHashMap();
    }

    /**
     * 查询机器人id对应的机器人ip映射关系
     *
     * @param robotIdList 机器人id集合,如果为空则是查询全部机器人数据
     * @return id对应的ip映射表 [key机器人Id,value机器人ip]
     */
    @Override
    public Map<String, String> queryIdMappingRobotIp(List<String> robotIdList) {
        // 查询机器人数据
        LambdaQueryWrapper<RobotInfo> lam = new LambdaQueryWrapper<>();
        lam.isNotNull(RobotInfo::getRobotIp);
        if (CollUtil.isNotEmpty(robotIdList)) {
            lam.eq(RobotInfo::getRobotId, robotIdList);
        }
        List<RobotInfo> robotInfos = baseMapper.selectList(lam);

        // 没有数据直接返回
        if (CollUtil.isEmpty(robotInfos)) {
            return CollUtil.newHashMap();
        }

        // 将list转成map[key机器人id,value机器人ip]
        Map<String, String> robotIdMap = robotInfos.stream().collect(Collectors.toMap(RobotInfo::getRobotId, RobotInfo::getRobotIp));
        return MapUtil.isNotEmpty(robotIdMap) ? robotIdMap : CollUtil.newHashMap();
    }

    /**
     * 机器人高清相机控制
     *
     * @param robotIp           机器人ip
     * @param cameraControlType 控制类型
     * @return
     */
    @Override
    public int robotCameraControl(String robotIp, CameraControlType cameraControlType) {
        if (ObjectUtil.isEmpty(cameraControlType) || StrUtil.isEmpty(robotIp)) {
            return RobotConstant.RESULT_ERROR_1;
        }

        // 设置机器人的控制模式为遥控模式
        this.setControlMode(robotIp, RobotConstant.CONTROL_TYPE_1);

        return robotDataService.robotCameraControl(cameraControlType);
    }

    /**
     * 设置当前机器人的控制模式
     *
     * @param robotIp     机器人ip
     * @param controlType 机器人的控制模式
     * @return
     */
    @Override
    public int setControlMode(String robotIp, Integer controlType) {
        if (StrUtil.isEmpty(robotIp) || ObjectUtil.isEmpty(controlType)) {
            return RobotConstant.RESULT_ERROR_1;
        }

        // 如果当前控制模式已经为需要设置的模式，则直接返回
        if (controlType.equals(this.getControlMode(robotIp))) {
            return RobotConstant.RESULT_SUCCESS_0;
        }

        // 关注机器人
        this.setCurrentRobot(robotIp);

        // 判断如果要设置为控制模式，需要把当前执行的任务取消掉
        if (RobotConstant.CONTROL_TYPE_1.equals(controlType)) {
            taskPathInfoService.robotControlTask(robotIp, com.aiurt.modules.robot.taskdata.wsdl.ControlTaskType.CANCEL_TASK);
        }

        // 设置控制模式
        return robotDataService.setControlMode(robotIp, controlType);
    }

    /**
     * 获取当前关注机器人的控制模式
     *
     * @param robotIp 机器人ip
     * @return
     */
    @Override
    public int getControlMode(String robotIp) {
        if (StrUtil.isEmpty(robotIp)) {
            throw new AiurtBootException("机器人ip参数为空");
        }
        return robotDataService.getControlMode(robotIp);
    }

    /**
     * 机器人高清相机补光灯控制
     *
     * @param robotIp          机器人ip
     * @param lightControlType 控制类型
     * @return
     */
    @Override
    public int robotLightControl(String robotIp, LightControlType lightControlType) {
        if (ObjectUtil.isEmpty(lightControlType) || StrUtil.isEmpty(robotIp)) {
            return RobotConstant.RESULT_ERROR_1;
        }

        // 设置机器人的控制模式为遥控模式
        this.setControlMode(robotIp, RobotConstant.CONTROL_TYPE_1);

        return robotDataService.robotLightControl(lightControlType);
    }

    /**
     * 机器人高清相机雨刷控制
     *
     * @param robotIp          机器人ip
     * @param wiperControlType 控制类型
     * @return
     */
    @Override
    public int robotWiperControl(String robotIp, WiperControlType wiperControlType) {
        if (ObjectUtil.isEmpty(wiperControlType) || StrUtil.isEmpty(robotIp)) {
            return RobotConstant.RESULT_ERROR_1;
        }

        // 设置机器人的控制模式为遥控模式
        this.setControlMode(robotIp, RobotConstant.CONTROL_TYPE_1);

        return robotDataService.robotWiperControl(wiperControlType);
    }

    /**
     * 机器人红外相机控制
     *
     * @param robotIp         机器人ip
     * @param filrControlType 控制类型
     * @return
     */
    @Override
    public int robotFlirControl(String robotIp, FilrControlType filrControlType) {
        if (ObjectUtil.isEmpty(filrControlType) || StrUtil.isEmpty(robotIp)) {
            return RobotConstant.RESULT_ERROR_1;
        }

        // 设置机器人的控制模式为遥控模式
        this.setControlMode(robotIp, RobotConstant.CONTROL_TYPE_1);

        return robotDataService.robotFlirControl(filrControlType);
    }

    /**
     * 机器人云台控制
     *
     * @param robotIp           机器人ip
     * @param yuntaiControlType 控制类型
     * @return
     */
    @Override
    public int robotYuntaiControl(String robotIp, YuntaiControlType yuntaiControlType) {
        if (ObjectUtil.isEmpty(yuntaiControlType) || StrUtil.isEmpty(robotIp)) {
            return RobotConstant.RESULT_ERROR_1;
        }

        // 设置机器人的控制模式为遥控模式
        this.setControlMode(robotIp, RobotConstant.CONTROL_TYPE_1);

        return robotDataService.robotYuntaiControl(yuntaiControlType);
    }


    /**
     * 判断ip是否重复并且合法
     *
     * @param robotInfo
     */
    private void checkCreateOrUpdate(RobotInfo robotInfo) {
        if (ObjectUtil.isEmpty(robotInfo)) {
            throw new AiurtBootException("未接收到参数");
        }

        log.info("添加机器人基础数据： 请求参数：{}", JSON.toJSONString(robotInfo));

        // 校验ip是否合法
        checkIpLegal(robotInfo.getRobotIp());

        // 机器人ip唯一校验
        checkRobotIpUnique(robotInfo.getRobotIp(), robotInfo.getRobotId());
    }

    /**
     * 机器人ip唯一校验
     *
     * @param robotIp 机器人ip
     * @param robotId 机器人id
     */
    private void checkRobotIpUnique(String robotIp, String robotId) {
        if (StrUtil.isEmpty(robotIp)) {
            return;
        }

        LambdaQueryWrapper<RobotInfo> lam = new LambdaQueryWrapper<>();
        lam.eq(RobotInfo::getRobotIp, robotIp);
        // 兼容修改时的验证
        if (StrUtil.isNotEmpty(robotId)) {
            lam.ne(RobotInfo::getRobotId, robotId);
        }
        if (baseMapper.selectCount(lam) > 0) {
            throw new AiurtBootException("机器人ip已被其他机器人使用，请更换机器人ip");
        }
    }

    /**
     * 校验ip是否合法
     *
     * @param robotIp 机器人ip
     */
    private void checkIpLegal(String robotIp) {
        if (StrUtil.isEmpty(robotIp)) {
            return;
        }
        if (!AreaPointTreeUtils.ipCheck(robotIp)) {
            throw new AiurtBootException("非法机器人ip");
        }
    }

    /**
     * 同步远程机器人基础数据
     *
     * @param robotInfo 支持同时同步多个
     */
    @Override
    public void synchronizeRobotData(List<RobotInfo> robotInfo) {
        if (CollUtil.isEmpty(robotInfo)) {
            return;
        }

        // 远程全部机器人信息
        RobotInfos info = null;
        try {
            info = robotDataService.getRobotInfo();
        } catch (WebServiceException e) {
            log.error("远程机器人连接超时,同步数据失败");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        if (ObjectUtil.isEmpty(info) || CollUtil.isEmpty(info.getInfos())) {
            return;
        }

        log.info("同步远程机器人基础数据:{}", info);

        Map<String, List<com.aiurt.modules.robot.robotdata.wsdl.RobotInfo>> map = Optional.ofNullable(info.getInfos()).orElse(CollUtil.newArrayList()).stream().collect(Collectors.groupingBy(com.aiurt.modules.robot.robotdata.wsdl.RobotInfo::getRobotIp));
        robotInfo.forEach(robot -> {
            List<com.aiurt.modules.robot.robotdata.wsdl.RobotInfo> robotInfos = map.get(robot.getRobotIp());
            if (CollUtil.isNotEmpty(robotInfos)) {
                com.aiurt.modules.robot.robotdata.wsdl.RobotInfo ro = robotInfos.get(0);
                robot.setCameraIp(ro.getCameraIp());
                robot.setCameraPort(ro.getCameraPort());
                robot.setFlirIp(ro.getFlirIp());
                robot.setFlirPort(ro.getFlirPort());
                robot.setCameraUser(ro.getCameraUser());
                robot.setFlirUser(ro.getFlirUser());
                robot.setCameraPassword(ro.getCameraPassword());
                robot.setFlirPassword(ro.getFlirPassword());
            }
        });
    }

    /**
     * 查询机器人视屏ip、账号、密码信息
     *
     * @param id 机器人id
     * @return
     */
    @Override
    public RobotInfo queryRobotById(String id) {
        if (StrUtil.isEmpty(id)) {
            return new RobotInfo();
        }

        RobotInfo robotInfo = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(robotInfo)) {
            return new RobotInfo();
        }

        // 视频ip地址和端口进行外网地址转换
        ipAndPortConvert(robotInfo);

        // 对视频的密码进行加密
        this.aesEncryption(robotInfo);

        return robotInfo;
    }

    /**
     * 视频ip地址和端口进行外网地址转换
     *
     * @param robotInfo
     */
    private void ipAndPortConvert(RobotInfo robotInfo) {
        // 外网地址转换、端口转换
        LambdaQueryWrapper<IpMapping> lam = new LambdaQueryWrapper<>();
        lam.eq(IpMapping::getIsMapping, RobotConstant.IS_MAPPING_1);
        List<IpMapping> ipList = ipMappingService.getBaseMapper().selectList(lam);
        Map<String, List<IpMapping>> ipMapping = Optional.ofNullable(ipList).orElse(CollUtil.newArrayList()).stream().collect(Collectors.groupingBy(IpMapping::getInsideIp));

        // 没有ip地址映射表则直接返回
        if (MapUtil.isEmpty(ipMapping) || ObjectUtil.isEmpty(robotInfo)) {
            return;
        }

        // 高清视频转换
        List<IpMapping> cameraIpMappings = ipMapping.get(robotInfo.getCameraIp());
        if (CollUtil.isNotEmpty(cameraIpMappings)) {
            // 高清视频ip
            IpMapping oneCameraIpMapping = cameraIpMappings.get(0);
            String newCameraIp = oneCameraIpMapping.getOutsideIp();
            robotInfo.setCameraIp(StrUtil.isNotEmpty(newCameraIp) ? newCameraIp : robotInfo.getCameraIp());

            // 高清视频登录端口
            Integer newCameraPort = oneCameraIpMapping.getOutsidePort();
            robotInfo.setCameraPort(ObjectUtil.isNotEmpty(newCameraPort) ? newCameraPort : robotInfo.getCameraPort());
        }

        // 红外视频转换
        List<IpMapping> flirIpMappings = ipMapping.get(robotInfo.getFlirIp());
        if (CollUtil.isNotEmpty(flirIpMappings)) {
            // 红外视频ip
            IpMapping oneflirIpMapping = flirIpMappings.get(0);
            String newFlirIp = oneflirIpMapping.getOutsideIp();
            robotInfo.setFlirIp(StrUtil.isNotEmpty(newFlirIp) ? newFlirIp : robotInfo.getFlirIp());

            // 红外视频登录端口
            Integer newFlirPort = oneflirIpMapping.getOutsidePort();
            robotInfo.setFlirPort(ObjectUtil.isNotEmpty(newFlirPort) ? newFlirPort : robotInfo.getFlirPort());
        }
    }

    /**
     * 根据机器人ip关注机器人
     *
     * @param robotIp
     * @return
     */
    @Override
    public int setCurrentRobot(String robotIp) {
        if (StrUtil.isEmpty(robotIp)) {
            return RobotConstant.RESULT_ERROR_1;
        }
        return robotDataService.setCurrentRobot(robotIp);
    }

    /**
     * 使用AES加密算法加密
     *
     * @param robotInfo
     * @return
     */
    public void aesEncryption(RobotInfo robotInfo) {
        if (ObjectUtil.isEmpty(robotInfo)) {
            return;
        }

        try {
            if (StrUtil.isNotEmpty(robotInfo.getCameraPassword())) {
                robotInfo.setCameraPassword(CryptoUtils.encryptSymmetrically(KEY, KEY, robotInfo.getCameraPassword(), CryptoUtils.Algorithm.AES_CBC_PKCS5));
                robotInfo.setFlirPassword(CryptoUtils.encryptSymmetrically(KEY, KEY, robotInfo.getFlirPassword(), CryptoUtils.Algorithm.AES_CBC_PKCS5));
            }
        } catch (Exception e) {
            log.error("AES加密异常:{}", e);
            e.printStackTrace();
        }
    }
}
