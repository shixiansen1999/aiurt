package com.aiurt.modules.robot.controller;


import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.robot.entity.PatrolPointInfo;
import com.aiurt.modules.robot.service.IPatrolPointInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Description: patrol_point_info
 * @Author: aiurt
 * @Date: 2022-09-26
 * @Version: V1.0
 */
@Api(tags = "巡检点位")
@RestController
@RequestMapping("/robot/patrolPointInfo")
@Slf4j
public class PatrolPointInfoController extends BaseController<PatrolPointInfo, IPatrolPointInfoService> {
    @Resource
    private IPatrolPointInfoService patrolPointInfoService;

    /**
     * 编辑巡检点位
     *
     * @param patrolPointInfo
     * @return
     */
    @AutoLog(value = "编辑巡检点位")
    @ApiOperation(value = "编辑巡检点位", notes = "编辑巡检点位")
    @PostMapping(value = "/edit")
    public Result<String> edit(@Valid @RequestBody PatrolPointInfo patrolPointInfo) {
        patrolPointInfoService.updateById(patrolPointInfo);
        return Result.OK("编辑成功!");
    }


    /**
     * 通过id查询巡检点位
     *
     * @param id
     * @return
     */
    @AutoLog(value = "通过id查询巡检点位")
    @ApiOperation(value = "通过id查询巡检点位", notes = "通过id查询巡检点位")
    @GetMapping(value = "/queryById")
    @ApiImplicitParam(name = "id", value = "巡检点位id", required = true, example = "a001_10", dataTypeClass = String.class)
    public Result<PatrolPointInfo> queryById(@RequestParam(name = "id") String id) {
        PatrolPointInfo patrolPointInfo = patrolPointInfoService.getById(id);
        return Result.OK(patrolPointInfo);
    }


}
