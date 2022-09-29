package com.aiurt.modules.robot.service;

import com.aiurt.modules.robot.entity.RobotInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: robot_info
 * @Author: aiurt
 * @Date: 2022-09-23
 * @Version: V1.0
 */
public interface IRobotInfoService extends IService<RobotInfo> {

    /**
     * 添加机器人
     *
     * @param robotInfo
     */
    void saveRobot(RobotInfo robotInfo);

    /**
     * 编辑机器人
     *
     * @param robotInfo
     */
    void updateRobotById(RobotInfo robotInfo);

    /**
     * 删除机器人
     *
     * @param id
     */
    void deleteRobot(String id);

    /**
     * 查询机器人ip对应的机器人id映射关系
     * @param robotIpList 机器人ip集合，如果为空则是查询全部机器人数据
     * @return ip对应的id映射表 [key机器人ip,value机器人id]
     */
    Map<String, String> queryRobotIpMappingId(List<String> robotIpList);

    /**
     * 查询机器人id对应的机器人ip映射关系
     *
     * @param robotIdList 机器人id集合，如果为空则是查询全部机器人数据
     * @return id对应的ip映射表 [key机器人id,value机器人ip]
     */
    Map<String, String> queryIdMappingRobotIp(List<String> robotIdList);
}
