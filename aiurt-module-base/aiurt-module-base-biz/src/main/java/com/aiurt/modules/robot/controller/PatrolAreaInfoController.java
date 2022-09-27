package com.aiurt.modules.robot.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.robot.dto.AreaPointDTO;
import com.aiurt.modules.robot.entity.PatrolAreaInfo;
import com.aiurt.modules.robot.service.IPatrolAreaInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 巡检区域和点位
 * @Author: aiurt
 * @Date: 2022-09-26
 * @Version: V1.0
 */
@Api(tags = "巡检区域")
@RestController
@RequestMapping("/robot/patrolAreaInfo")
@Slf4j
public class PatrolAreaInfoController extends BaseController<PatrolAreaInfo, IPatrolAreaInfoService> {
    @Autowired
    private IPatrolAreaInfoService patrolAreaInfoService;

    /**
     * 巡检区域和点位树形查询
     *
     * @param name 巡检区域名称或点位名称
     * @return 树形结构
     */
    @AutoLog(value = "巡检区域和点位树形查询")
    @ApiOperation(value = "巡检区域和点位树形查询", notes = "巡检区域和点位树形查询")
    @GetMapping(value = "/treelist")
    public Result<List<AreaPointDTO>> treelist(@RequestParam(name = "name", required = false) String name) {
        List<AreaPointDTO> result = patrolAreaInfoService.treelist(name);
        return Result.OK(result);
    }

    /**
     * 编辑巡检区域
     *
     * @param patrolAreaInfo
     * @return
     */
    @AutoLog(value = "编辑巡检区域")
    @ApiOperation(value = "编辑巡检区域", notes = "编辑巡检区域")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody PatrolAreaInfo patrolAreaInfo) {
        patrolAreaInfoService.updatePoint(patrolAreaInfo);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id查询巡检区域
     *
     * @param id
     * @return
     */
    @AutoLog(value = "通过id查询巡检区域")
    @ApiOperation(value = "通过id查询巡检区域", notes = "通过id查询巡检区域")
    @GetMapping(value = "/queryById")
    @ApiImplicitParam(name = "id", value = "区域id", required = true, example = "100100101", dataTypeClass = String.class)
    public Result<PatrolAreaInfo> queryById(@RequestParam(name = "id") String id) {
        PatrolAreaInfo patrolAreaInfo = patrolAreaInfoService.getById(id);
        return Result.OK(patrolAreaInfo);
    }

    /**
     * 同步巡检区域和点位
     *
     * @return
     */
    @AutoLog(value = "同步巡检区域和点位")
    @ApiOperation(value = "同步巡检区域和点位", notes = "同步巡检区域和点位")
    @GetMapping(value = "/synchronizeAreaAndPoint")
    public Result<String> synchronizeAreaAndPoint() {
        patrolAreaInfoService.synchronizeAreaAndPoint();
        return Result.OK("同步巡检区域和点位成功");
    }

}
