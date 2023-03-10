package com.aiurt.boot.task.controller;

import com.aiurt.boot.task.entity.PatrolSamplePerson;
import com.aiurt.boot.task.service.IPatrolSamplePersonService;
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname :  PatrolSamplePersonController
 * @Description : PatrolSamplePersonController
 * @Date :2023/3/8 17:23
 * @Created by   : sbx
 */

@Api(tags="巡检抽检人")
@RestController
@RequestMapping("/patrolSamplePerson")
@Slf4j
public class PatrolSamplePersonController extends BaseController<PatrolSamplePerson, IPatrolSamplePersonService> {

    @Autowired
    private IPatrolSamplePersonService patrolSamplePersonService;
    @Autowired
    private IPatrolTaskDeviceService patrolTaskDeviceService;
    @Autowired
    private IPatrolTaskService patrolTaskService;

    /**
     * app填写巡检工单-添加抽检人
     * @param patrolNumber
     * @param sampleId
     * @return
     */
    @AutoLog(value = "app填写巡检工单-添加抽检人||巡检位置", operateType = 3, operateTypeAlias = "修改", module = ModuleType.PATROL,permissionUrl = "/Patrol/pool")
    @ApiOperation(value="app填写巡检工单-添加抽检人||巡检位置", notes="app填写巡检工单-添加抽检人||巡检位置")
    @PostMapping(value = "/addPatrolSamplePerson")
    public Result<?> addPatrolSamplePerson(@RequestParam String patrolNumber, @RequestParam String sampleId) {
        patrolSamplePersonService.addPatrolSamplePerson(patrolNumber, sampleId);
        return Result.OK("添加成功");
    }
}
