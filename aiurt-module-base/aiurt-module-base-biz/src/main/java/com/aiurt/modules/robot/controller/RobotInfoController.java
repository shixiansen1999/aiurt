package com.aiurt.modules.robot.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.robot.entity.RobotInfo;
import com.aiurt.modules.robot.service.IRobotInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private IRobotInfoService robotInfoService;

    /**
     * 机器人列表查询
     *
     * @param robotInfo
     * @param req
     * @return
     */
    @AutoLog(value = "机器人列表查询")
    @ApiOperation(value = "机器人列表查询", notes = "机器人列表查询")
    @GetMapping(value = "/list")
    public Result<List<RobotInfo>> queryPageList(RobotInfo robotInfo,
                                                 HttpServletRequest req) {
        QueryWrapper<RobotInfo> queryWrapper = QueryGenerator.initQueryWrapper(robotInfo, req.getParameterMap());
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
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
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

}
