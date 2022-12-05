package com.aiurt.boot.task.controller;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/11/29
 * @desc
 */

import com.aiurt.boot.task.entity.PatrolTaskDeviceReadRecord;
import com.aiurt.boot.task.service.IPatrolTaskDeviceReadRecordService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@Api(tags = "工单安全事项阅读记录")
@RestController
@RequestMapping("/patrolTaskDeviceReadRecord")
@Slf4j
public class PatrolTaskDeviceReadRecordController extends BaseController<PatrolTaskDeviceReadRecord, IPatrolTaskDeviceReadRecordService> {
    @Autowired
    private IPatrolTaskDeviceReadRecordService taskDeviceReadRecordService;

    /**
     * 判断当前用户是否已经阅读过安全事项
     * @return
     */
    @AutoLog(value = "app巡检清单列表-判断当前用户是否已经阅读过安全事项")
    @ApiOperation(value = "app巡检清单列表-判断当前用户是否已经阅读过安全事项", notes = "app巡检清单列表-判断当前用户是否已经阅读过安全事项")
    @GetMapping(value = "/isRead")
    public Result<?> patrolTaskDeviceList(@RequestParam(name="taskDeviceId",required=true)String taskDeviceId,
                                          @RequestParam(name="majorCode",required=true)String majorCode ,
                                          @RequestParam(name = "subsystemCode",required=false) String subsystemCode,
                                          @RequestParam(name = "taskId", required=true) String taskId, HttpServletRequest req) {
         boolean isRead =  taskDeviceReadRecordService.getPatrolTaskDeviceList(taskDeviceId,majorCode,subsystemCode,taskId);
        JSONObject result = new JSONObject(1);
        result.put("show", isRead);
        return Result.ok(result);
    }
    /**
     *   工单阅读记录添加
     * @param taskDeviceReadRecord
     * @return
     */
    @AutoLog(value = "工单阅读记录添加")
    @ApiOperation(value="工单阅读记录添加", notes="工单阅读记录添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody PatrolTaskDeviceReadRecord taskDeviceReadRecord) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        taskDeviceReadRecord.setUserId(user.getId());
        taskDeviceReadRecordService.save(taskDeviceReadRecord);
        return Result.OK("添加成功！");
    }
}
