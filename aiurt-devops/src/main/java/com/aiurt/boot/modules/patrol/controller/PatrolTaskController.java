package com.aiurt.boot.modules.patrol.controller;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.modules.patrol.param.IgnoreTaskParam;
import com.swsc.copsms.modules.patrol.param.PatrolPoolParam;
import com.swsc.copsms.modules.patrol.param.TaskAddParam;
import com.swsc.copsms.modules.patrol.service.IPatrolTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 巡检人员任务
 * @Author: qian
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "巡检人员任务")
@RestController
@RequestMapping("/patrol/patrolTask")
public class PatrolTaskController {

	@Autowired
	private IPatrolTaskService patrolTaskService;


	@AutoLog(value = "巡检人员任务-分页列表")
	@ApiOperation(value = "巡检人员任务-分页列表", notes = "巡检人员任务-分页列表")
	@PostMapping(value = "/pageList")
	public Result<?> pageList(HttpServletRequest req,
	                         @RequestBody PatrolPoolParam param) {
		return patrolTaskService.pageList(req, param.getPageNo(), param.getPageSize(), param);
	}

	@AutoLog(value = "巡检人员任务-手动新增任务")
	@ApiOperation(value = "巡检人员任务-手动新增任务", notes = "巡检人员任务-手动新增任务")
	@PostMapping(value = "/manuallyAddTasks")
	public Result<?> manuallyAddTasks(HttpServletRequest req,
	                                  @RequestBody TaskAddParam param) {

		return patrolTaskService.manuallyAddTasks(req, param);
	}


	@AutoLog(value = "巡检人员任务-漏检任务处理")
	@ApiOperation(value = "巡检人员任务-漏检任务处理", notes = "巡检人员任务-漏检任务处理")
	@PostMapping(value = "/ignoreTasks")
	public Result<?> ignoreTasks(HttpServletRequest req,
	                             @RequestBody IgnoreTaskParam param) {

		return patrolTaskService.ignoreTasks(req, param);
	}

	@AutoLog(value = "巡检人员任务-详情")
	@ApiOperation(value = "巡检人员任务-详情", notes = "巡检人员任务-详情")
	@PostMapping(value = "/detail")
	public Result<?> detail(HttpServletRequest req,
	                        @RequestParam(value = "id",required = false)Long id ,
	                        @RequestParam(value = "code",required = false)String code) {


		return patrolTaskService.detail(req, id,code);
	}

}
