package com.aiurt.modules.robot.robotdata.service;


import com.aiurt.modules.robot.robotdata.wsdl.ConnectInfos;
import com.aiurt.modules.robot.robotdata.wsdl.RobotGasInfos;
import com.aiurt.modules.robot.robotdata.wsdl.RobotInfos;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/219:03
 */
public interface RobotDataService {
    /**
     * 获取机器人系统连接信息
     *
     * @return ConnectInfos
     */
    ConnectInfos getRobotConnect();

    /**
     * 获取机器人台账信息
     * @return
     */
    RobotInfos getRobotInfo();

    /**
     * 设置当前关注的机器人
     *
     * @param robotIp 机器人ip
     * @return 0成功，1失败
     */
    int setCurrentRobot(String robotIp);

    /**
     * 设置当前关注机器人的控制模式
     *
     * @param robotIp     机器人ip
     * @param controlType 0任务模式，1遥控模式
     * @return 0成功，1失败
     */
    int setControlMode(String robotIp, int controlType);

    /**
     * 机器人监测气体
     * @param needAll 是否需要所有机器人数据 true是，false否
     * @return RobotGasInfo数组
     */
    RobotGasInfos getRobotGasInfo(boolean needAll);
}
