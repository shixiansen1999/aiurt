package com.aiurt.modules.robot.controller;


import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.robot.constant.RobotConstant;
import com.aiurt.modules.robot.entity.RobotInfo;
import com.aiurt.modules.robot.robotdata.wsdl.*;
import com.aiurt.modules.robot.service.IRobotInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @Description: 机器人信息类
 * @Author: aiurt
 * @Date: 2022-09-23
 * @Version: V1.0
 */
@Api(tags = "机器人分类")
@RestController
@RequestMapping("/robot/robotInfo")
@Slf4j
public class RobotInfoController extends BaseController<RobotInfo, IRobotInfoService> {
    @Resource
    private IRobotInfoService robotInfoService;

    /**
     * 机器人列表查询
     *
     * @param robotName 机器人名称
     * @param req
     * @return
     */
    @AutoLog(value = "机器人列表查询")
    @ApiOperation(value = "机器人列表查询", notes = "机器人列表查询")
    @GetMapping(value = "/list")
    public Result<List<RobotInfo>> queryPageList(@RequestParam(value = "robotName", required = false) String robotName,
                                                 HttpServletRequest req) {
        QueryWrapper<RobotInfo> queryWrapper = QueryGenerator.initQueryWrapper(new RobotInfo().setRobotName(robotName), req.getParameterMap());
        List<RobotInfo> result = robotInfoService.list(queryWrapper);
        return Result.OK(result);
    }

    /**
     * 添加机器人
     *
     * @param robotInfo
     * @return
     */
    @AutoLog(value = "添加机器人")
    @ApiOperation(value = "添加机器人", notes = "添加机器人")
    @PostMapping(value = "/add")
    public Result<String> add(@Valid @RequestBody RobotInfo robotInfo) {
        robotInfoService.saveRobot(robotInfo);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑机器人
     *
     * @param robotInfo
     * @return
     */
    @AutoLog(value = "编辑机器人")
    @ApiOperation(value = "编辑机器人", notes = "编辑机器人")
    @PostMapping(value = "/edit")
    public Result<String> edit(@Valid @RequestBody RobotInfo robotInfo) {
        robotInfoService.updateRobotById(robotInfo);
        return Result.OK("编辑成功!");
    }

    /**
     * 删除机器人
     *
     * @param id
     * @return
     */
    @AutoLog(value = "删除机器人")
    @ApiOperation(value = "删除机器人", notes = "删除机器人")
    @DeleteMapping(value = "/delete")
    @ApiImplicitParam(name = "id", value = "机器人id", required = true, example = "1542055710450204673", dataTypeClass = String.class)
    public Result<String> delete(@RequestParam(name = "id") String id) {
        robotInfoService.deleteRobot(id);
        return Result.OK("删除成功!");
    }

    /**
     * 查询机器人信息
     *
     * @param id
     * @return
     */
    @AutoLog(value = "查询机器人信息")
    @ApiOperation(value = "查询机器人信息", notes = "查询机器人信息")
    @GetMapping(value = "/queryById")
    @ApiImplicitParam(name = "id", value = "机器人id", required = true, example = "1542055710450204673", dataTypeClass = String.class)
    public Result<RobotInfo> queryById(@RequestParam(name = "id") String id) {
        RobotInfo robotInfo = robotInfoService.getById(id);
        return Result.OK(robotInfo);
    }

    /**
     * 设置当前机器人的控制模式
     *
     * @param robotIp     机器人ip
     * @param controlType 机器人的控制模式
     * @return
     */
    @AutoLog(value = "设置当前机器人的控制模式")
    @ApiOperation(value = "设置当前机器人的控制模式", notes = "设置当前机器人的控制模式")
    @GetMapping(value = "/setControlMode")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "robotIp", value = "机器人ip", required = true, example = "192.168.0.3", dataTypeClass = String.class),
            @ApiImplicitParam(name = "controlType", value = "机器人的控制模式(0任务模式，1遥控模式)", required = true, example = "0", dataTypeClass = Integer.class)
    })
    public Result<?> setControlMode(@RequestParam("robotIp") String robotIp,
                                    @RequestParam("controlType") Integer controlType) {
        int result = robotInfoService.setControlMode(robotIp, controlType);
        return result == RobotConstant.CONTROL_TYPE_0 ? Result.OK("设置成功") : Result.error("设置失败");
    }

    /**
     * 获取当前关注机器人的控制模式
     *
     * @param robotIp 机器人ip
     * @return
     */
    @AutoLog(value = "获取当前关注机器人的控制模式")
    @ApiOperation(value = "获取当前关注机器人的控制模式", notes = "获取当前关注机器人的控制模式")
    @GetMapping(value = "/getControlMode")
    @ApiImplicitParam(name = "robotIp", value = "机器人ip", required = true, example = "192.168.0.3", dataTypeClass = String.class)
    public Result<?> getControlMode(@RequestParam("robotIp") String robotIp) {
        int result = robotInfoService.getControlMode(robotIp);
        return result == RobotConstant.CONTROL_TYPE_0 ? Result.OK("任务模式") : Result.OK("遥控模式");
    }

    /**
     * 机器人高清相机控制
     *
     * @param cameraControlType 控制类型
     * @return
     */
    @AutoLog(value = "机器人高清相机控制")
    @ApiOperation(value = "机器人高清相机控制", notes = "机器人高清相机控制")
    @GetMapping(value = "/robotCameraControl")
    public Result<?> robotCameraControl(@RequestParam("robotIp") String robotIp,
                                        CameraControlType cameraControlType) {
        int result = robotInfoService.robotCameraControl(robotIp, cameraControlType);
        return result == RobotConstant.CONTROL_TYPE_0 ? Result.OK("操作成功") : Result.error("操作失败");
    }

    /**
     * 机器人高清相机补光灯控制
     *
     * @param lightControlType 控制类型
     * @return
     */
    @AutoLog(value = "机器人高清相机补光灯控制")
    @ApiOperation(value = "机器人高清相机补光灯控制", notes = "机器人高清相机补光灯控制")
    @GetMapping(value = "/robotLightControl")
    public Result<?> robotLightControl(@RequestParam("robotIp") String robotIp,
                                       LightControlType lightControlType) {
        int result = robotInfoService.robotLightControl(robotIp, lightControlType);
        return result == RobotConstant.CONTROL_TYPE_0 ? Result.OK("操作成功") : Result.error("操作失败");
    }

    /**
     * 机器人高清相机雨刷控制
     *
     * @param wiperControlType 控制类型
     * @return
     */
    @AutoLog(value = "机器人高清相机雨刷控制")
    @ApiOperation(value = "机器人高清相机雨刷控制", notes = "机器人高清相机雨刷控制")
    @GetMapping(value = "/robotWiperControl")
    public Result<?> robotWiperControl(@RequestParam("robotIp") String robotIp,
                                       WiperControlType wiperControlType) {
        int result = robotInfoService.robotWiperControl(robotIp, wiperControlType);
        return result == RobotConstant.CONTROL_TYPE_0 ? Result.OK("操作成功") : Result.error("操作失败");
    }

    /**
     * 机器人红外相机控制
     *
     * @param filrControlType 控制类型
     * @return
     */
    @AutoLog(value = "机器人红外相机控制")
    @ApiOperation(value = "机器人红外相机控制", notes = "机器人红外相机控制")
    @GetMapping(value = "/robotFlirControl")
    public Result<?> robotFlirControl(@RequestParam("robotIp") String robotIp,
                                      FilrControlType filrControlType) {
        int result = robotInfoService.robotFlirControl(robotIp, filrControlType);
        return result == RobotConstant.CONTROL_TYPE_0 ? Result.OK("操作成功") : Result.error("操作失败");
    }

    /**
     * 机器人云台控制
     *
     * @param yuntaiControlType 控制类型
     * @return
     */
    @AutoLog(value = "机器人云台控制")
    @ApiOperation(value = "机器人云台控制", notes = "机器人云台控制")
    @GetMapping(value = "/robotYuntaiControl")
    public Result<?> robotYuntaiControl(@RequestParam("robotIp") String robotIp,
                                        YuntaiControlType yuntaiControlType) {
        int result = robotInfoService.robotYuntaiControl(robotIp, yuntaiControlType);
        return result == RobotConstant.CONTROL_TYPE_0 ? Result.OK("操作成功") : Result.error("操作失败");
    }

    /**
     * 查询机器人视屏ip、账号、密码信息
     *
     * @param id
     * @return
     */
    @AutoLog(value = "查询机器人视屏ip、账号、密码信息")
    @ApiOperation(value = "查询机器人视屏ip、账号、密码信息", notes = "查询机器人视屏ip、账号、密码信息")
    @GetMapping(value = "/queryRobotById")
    @ApiImplicitParam(name = "id", value = "机器人id", required = true, example = "1542055710450204673", dataTypeClass = String.class)
    public Result<RobotInfo> queryRobotById(@RequestParam(name = "id") String id) {
        RobotInfo robotInfo = robotInfoService.queryRobotById(id);
        return Result.OK(robotInfo);
    }

}
