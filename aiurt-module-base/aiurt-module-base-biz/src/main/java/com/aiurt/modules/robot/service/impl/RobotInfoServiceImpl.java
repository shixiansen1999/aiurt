package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.IpUtils;
import com.aiurt.modules.robot.entity.RobotInfo;
import com.aiurt.modules.robot.mapper.RobotInfoMapper;
import com.aiurt.modules.robot.robotdata.service.RobotDataService;
import com.aiurt.modules.robot.robotdata.wsdl.RobotInfos;
import com.aiurt.modules.robot.service.IRobotInfoService;
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
 * @Description: robot_info
 * @Author: aiurt
 * @Date: 2022-09-23
 * @Version: V1.0
 */
@Slf4j
@Service
public class RobotInfoServiceImpl extends ServiceImpl<RobotInfoMapper, RobotInfo> implements IRobotInfoService {

    @Resource
    private RobotDataService robotDataService;

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

        // 删除机器人信息
        baseMapper.deleteById(id);
    }

    /**
     * 查询机器人ip对应的机器人id映射关系
     *
     * @return ip对应的id映射表
     */
    @Override
    public Map<String, String> queryRobotIpMappingId() {
        LambdaQueryWrapper<RobotInfo> lam = new LambdaQueryWrapper<>();
        lam.isNotNull(RobotInfo::getRobotIp);
        List<RobotInfo> robotInfos = baseMapper.selectList(lam);
        if (CollUtil.isEmpty(robotInfos)) {
            return CollUtil.newHashMap();
        }

        // 将list转成map[key机器人ip,value机器人id]
        Map<String, String> ipMap = robotInfos.stream().collect(Collectors.toMap(RobotInfo::getRobotIp, RobotInfo::getRobotId));
        return MapUtil.isNotEmpty(ipMap) ? ipMap : CollUtil.newHashMap();
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
        if (!IpUtils.ipCheck(robotIp)) {
            throw new AiurtBootException("非法机器人ip");
        }
    }

    /**
     * 同步远程机器人基础数据
     *
     * @param robotInfo 支持同时同步多个
     */
    private void synchronizeRobotData(List<RobotInfo> robotInfo) {
        if (CollUtil.isNotEmpty(robotInfo)) {
            return;
        }

        // 远程全部机器人信息
        RobotInfos info = robotDataService.getRobotInfo();

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

}
