package com.aiurt.modules.robot.robotdata.service.impl;


import com.aiurt.common.util.webservice.WebServiceUtils;
import com.aiurt.modules.robot.robotdata.service.RobotDataService;
import com.aiurt.modules.robot.robotdata.wsdl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/219:03
 */
@Service
public class RobotDataServiceImpl implements RobotDataService {
    @Value("${robot.wsdl.address}")
    private String address;

    /**
     * 获取机器人系统连接信息
     *
     * @return ConnectInfos
     */
    @Override
    public ConnectInfos getRobotConnect() {
        ConnectInfos result = WebServiceUtils.getWebService(ServicePortType.class, address).getRobotConnect();
        return result;
    }

    /**
     * 获取机器人台账信息
     *
     * @return
     */
    @Override
    public RobotInfos getRobotInfo() {
        return WebServiceUtils.getWebService(ServicePortType.class, address).getRobotInfo();
    }

    /**
     * 设置当前关注的机器人
     *
     * @param robotIp 机器人ip
     * @return 0成功，1失败
     */
    @Override
    public int setCurrentRobot(String robotIp) {
        int result = WebServiceUtils.getWebService(ServicePortType.class, address).setCurrentRobot(robotIp);
        return result;
    }

    /**
     * 设置当前关注机器人的控制模式
     *
     * @param robotIp     机器人ip
     * @param controlType 0任务模式，1遥控模式
     * @return 0成功，1失败
     */
    @Override
    public int setControlMode(String robotIp, int controlType) {
        int result = WebServiceUtils.getWebService(ServicePortType.class, address).setControlMode(robotIp, controlType);
        return result;
    }

    /**
     * 机器人监测气体
     *
     * @param needAll 是否需要所有机器人数据 true是，false否
     * @return RobotGasInfo数组
     */
    @Override
    public RobotGasInfos getRobotGasInfo(boolean needAll) {
        RobotGasInfos robotGasInfo = WebServiceUtils.getWebService(ServicePortType.class, address).getRobotGasInfo(needAll);
        return robotGasInfo;
    }

    /**
     * 机器人高清相机控制
     *
     * @param type
     * @return
     */
    @Override
    public int robotCameraControl(CameraControlType type) {
        return WebServiceUtils.getWebService(ServicePortType.class, address).robotCameraControl(type);
    }

    /**
     * 机器人高清相机补光灯控制
     *
     * @param type
     * @return
     */
    @Override
    public int robotLightControl(LightControlType type) {
        return WebServiceUtils.getWebService(ServicePortType.class, address).robotLightControl(type);
    }

    /**
     * 机器人高清相机雨刷控制
     *
     * @param type
     * @return
     */
    @Override
    public int robotWiperControl(WiperControlType type) {
        return WebServiceUtils.getWebService(ServicePortType.class, address).robotWiperControl(type);
    }

    /**
     * 机器人红外相机控制
     *
     * @param type
     * @return
     */
    @Override
    public int robotFlirControl(FilrControlType type) {
        return WebServiceUtils.getWebService(ServicePortType.class, address).robotFlirControl(type);
    }

    /**
     * 机器人云台控制
     *
     * @param type
     * @return
     */
    @Override
    public int robotYuntaiControl(YuntaiControlType type) {
        return WebServiceUtils.getWebService(ServicePortType.class, address).robotYuntaiControl(type);
    }

    /**
     * 获取当前关注机器人的控制模式
     *
     * @param robotIp 机器人ip
     * @return 0任务模式，1遥控模式
     */
    @Override
    public int getControlMode(String robotIp) {
        return WebServiceUtils.getWebService(ServicePortType.class, address).getControlMode(robotIp);
    }
}
