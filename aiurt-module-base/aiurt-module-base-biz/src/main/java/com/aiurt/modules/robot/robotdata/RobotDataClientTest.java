package com.aiurt.modules.robot.robotdata;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.util.webservice.WebServiceUtils;
import com.aiurt.modules.robot.robotdata.wsdl.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1916:03
 */
@Slf4j
public class RobotDataClientTest {
    /**
     * WebService服务地址
     */
    private static final String ADDRESS = "http://2.0.1.16:11456?wsdl";
    /**
     * 机器人ip
     */
    private static final String ROBOT_IP = "192.168.1.10";


    /**
     * 获取机器人系统连接信息
     */
    public void getRobotConnect() {
        ConnectInfos result = WebServiceUtils.getWebService(ServicePortType.class, ADDRESS).getRobotConnect();
        if (ObjectUtil.isNotEmpty(result)) {
            List<ConnectInfo> infos = result.getInfos();
            for (ConnectInfo info : infos) {
                System.out.print("机器人ip：" + info.getRobotIp());
                System.out.print(" | 是否正常：" + info.isState());
                System.out.print(" | 描述：" + info.getConnectDesc());
                System.out.println();
            }
        }
    }


    /**
     * 设置当前关注的机器人
     */
    public void setCurrentRobot() {
        int result = WebServiceUtils.getWebService(ServicePortType.class, ADDRESS).setCurrentRobot(ROBOT_IP);
        System.out.println(result > 0 ? "设置当前关注的机器人失败" : "设置当前关注的机器人成功");
    }

    /**
     * 设置当前关注机器人的控制模式
     * ControlType  0任务模式，1遥控模式
     */
    public void setControlMode() {
        int result = WebServiceUtils.getWebService(ServicePortType.class, ADDRESS).setControlMode(ROBOT_IP, 0);
        System.out.println(result > 0 ? "设置当前关注机器人的控制模式失败" : "设置当前关注机器人的控制模式成功");
    }


    /**
     * 机器人监测气体
     */
    public void getRobotGasInfo() {
        // 是否需要所有机器人数据:True是，False否
        RobotGasInfos robotGasInfo = WebServiceUtils.getWebService(ServicePortType.class, ADDRESS).getRobotGasInfo(true);
        if (ObjectUtil.isNotEmpty(robotGasInfo)) {
            List<RobotGasInfo> gasContents = robotGasInfo.getGasContents();
            for (RobotGasInfo gasContent : gasContents) {
                System.out.print("机器人ip：" + gasContent.getRobotIp());
                System.out.print(" | 硫化氢：" + gasContent.getH2S());
                System.out.print(" | 一氧化碳：" + gasContent.getCO());
                System.out.print(" | 氧气：" + gasContent.getO2());
                System.out.print(" | 甲烷：" + gasContent.getCH4());
                System.out.print(" | 温度：" + gasContent.getTEMP());
                System.out.print(" | 湿度：" + gasContent.getHUM());
                System.out.print(" | PM2.5：" + gasContent.getPM25());
                System.out.print(" | PM10：" + gasContent.getPM10());
                System.out.print(" | 臭氧：" + gasContent.getO3());
                System.out.print(" | SF6：" + gasContent.getSF6());
                System.out.print(" | 报警描述（无报警为空）：" + gasContent.getDesc());
                System.out.println();
            }
        }
    }


}
