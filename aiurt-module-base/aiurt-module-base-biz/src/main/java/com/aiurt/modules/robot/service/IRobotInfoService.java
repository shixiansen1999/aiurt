package com.aiurt.modules.robot.service;


import com.aiurt.modules.robot.entity.RobotInfo;
import com.aiurt.modules.robot.robotdata.wsdl.*;
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
     *
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

    /**
     * 机器人高清相机控制
     *
     * @param robotIp           机器人ip
     * @param cameraControlType 控制类型
     * @return
     */
    int robotCameraControl(String robotIp, CameraControlType cameraControlType);

    /**
     * 设置当前机器人的控制模式
     *
     * @param robotIp     机器人ip
     * @param controlType 机器人的控制模式
     * @return
     */
    int setControlMode(String robotIp, Integer controlType);

    /**
     * 获取当前关注机器人的控制模式
     *
     * @param robotIp 机器人ip
     * @return
     */
    int getControlMode(String robotIp);

    /**
     * 机器人高清相机补光灯控制
     *
     * @param robotIp          机器人ip
     * @param lightControlType 控制类型
     * @return
     */
    int robotLightControl(String robotIp, LightControlType lightControlType);

    /**
     * 机器人高清相机雨刷控制
     *
     * @param robotIp          机器人ip
     * @param wiperControlType 控制类型
     * @return
     */
    int robotWiperControl(String robotIp, WiperControlType wiperControlType);

    /**
     * 机器人红外相机控制
     *
     * @param robotIp         机器人ip
     * @param filrControlType 控制类型
     * @return
     */
    int robotFlirControl(String robotIp, FilrControlType filrControlType);

    /**
     * 机器人云台控制
     *
     * @param robotIp           机器人ip
     * @param yuntaiControlType 控制类型
     * @return
     */
    int robotYuntaiControl(String robotIp, YuntaiControlType yuntaiControlType);

    /**
     * 同步远程机器人基础数据
     *
     * @param robotInfo 支持同时同步多个
     */
    void synchronizeRobotData(List<RobotInfo> robotInfo);

    /**
     * 查询机器人视屏ip、账号、密码信息
     *
     * @param id 机器人id
     * @return
     */
    RobotInfo queryRobotById(String id);

    /**
     * 根据机器人ip关注机器人
     * @param robotIp
     * @return
     */
    int setCurrentRobot(String robotIp);
}
