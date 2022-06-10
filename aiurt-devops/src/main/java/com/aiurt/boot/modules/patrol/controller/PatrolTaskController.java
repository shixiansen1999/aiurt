package com.aiurt.boot.modules.patrol.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.constant.RoleConstant;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.PatrolTask;
import com.aiurt.boot.modules.patrol.param.*;
import com.aiurt.boot.modules.patrol.service.IPatrolTaskService;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskVO;
import com.aiurt.boot.modules.patrol.vo.statistics.AppStationPatrolStatisticsVO;
import com.aiurt.boot.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 巡检人员任务
 * @Author: Mr.zhao
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "巡检人员任务")
@RestController
@RequestMapping("/patrol/patrolTask")
public class PatrolTaskController {

	@Resource
	private IPatrolTaskService patrolTaskService;
	@Autowired
	private IStationService stationService;
	@Autowired
	private ISysUserService sysUserService;

	@AutoLog(value = "巡检人员任务-分页列表")
	@ApiOperation(value = "巡检人员任务-分页列表", notes = "巡检人员任务-分页列表")
	@PostMapping(value = "/pageList")
	public Result<IPage<PatrolTaskVO>> pageList(@RequestBody PatrolPoolParam param) {
		return patrolTaskService.pageList(param);
	}

	@AutoLog(value = "巡检人员任务-手动新增任务")
	@ApiOperation(value = "巡检人员任务-手动新增任务", notes = "巡检人员任务-手动新增任务")
	@PostMapping(value = "/manuallyAddTasks")
	public Result<?> manuallyAddTasks(HttpServletRequest req,
	                                  @RequestBody TaskAddParam param) {
		return patrolTaskService.manuallyAddTasks(req, param);
	}


	@AutoLog(value = "巡检人员任务-app分页列表")
	@ApiOperation(value = "巡检人员任务-app分页列表", notes = "巡检人员任务-app分页列表")
	@PostMapping(value = "/appPage")
	public Result<IPage<PatrolTaskVO>> appPage(HttpServletRequest req,
	                                           @RequestBody PatrolPoolParam param) {
		return patrolTaskService.appPage(req, param);
	}

	@AutoLog(value = "巡检人员任务-app单项树形")
	@ApiOperation(value = "巡检人员任务-app单项树形", notes = "巡检人员任务-app单项树形")
	@PostMapping(value = "/appOneDetail")
	public Result<?> appOneDetail(HttpServletRequest req,
	                              @RequestBody @Validated OneTreeParam param) {
		return patrolTaskService.appOneDetail(req, param);
	}


	@AutoLog(value = "巡检人员任务-漏检任务处理")
	@ApiOperation(value = "巡检人员任务-漏检任务处理", notes = "巡检人员任务-漏检任务处理")
	@PostMapping(value = "/ignoreTasks")
	public Result<?> ignoreTasks(@RequestBody IgnoreTaskParam param) {

		List<Long> ids = param.getIds();

		if (ids.size() < 1) {
			throw new SwscException("处理数据不能为空");
		}

		boolean update = this.patrolTaskService.lambdaUpdate().in(PatrolTask::getId, param.getIds())
				.update(new PatrolTask().setIgnoreContent(param.getContent()));
		if (!update) {
			throw new SwscException("处理失败");
		}


		return Result.ok();
	}

	@AutoLog(value = "巡检人员任务-详情")
	@ApiOperation(value = "巡检人员任务-详情", notes = "巡检人员任务-详情")
	@PostMapping(value = "/detail")
	public Result<?> detail(HttpServletRequest req, @RequestBody PatrolTaskDetailParam param) {

		return patrolTaskService.detail(req, param);
	}


	@AutoLog(value = "巡检人员任务-根据id查看详情")
	@ApiOperation(value = "巡检人员任务-根据id查看详情", notes = "巡检人员任务-根据id查看详情")
	@GetMapping(value = "/detailById")
	public Result<?> detail(HttpServletRequest req,
	                        @RequestParam("id") @NotNull(message = "id不能为空") Long id) {
		PatrolTaskDetailParam param = new PatrolTaskDetailParam();
		param.setId(id);
		return patrolTaskService.detail(req, param);
	}



	@AutoLog(value = "巡检人员任务-添加抽查信息")
	@ApiOperation(value = "巡检人员任务-添加抽查信息", notes = "巡检人员任务-添加抽查信息")
	@PostMapping(value = "/spotTest")
	public Result<?> spotTest(@RequestBody @Validated SpotTestParam param) {

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		PatrolTask task = patrolTaskService.getById(param.getId());
		if (task==null || task.getStatus() == null){
			return Result.error("未找到此条数据");
		}
		if (Objects.equals(task.getStatus(), PatrolConstant.DISABLE)){
			return Result.error("未完成不可抽查");
		}
		List<String> roleCodeList;
		roleCodeList = sysUserService.getRoleCodeById(user.getId());
		if (ObjectUtil.isNotEmpty(roleCodeList)&&roleCodeList.size()>0&&roleCodeList.contains(RoleConstant.TEAM_LEADER)){
			task.setSpotTest(param.getContent()).setSpotTestUser(user.getId());
		}else if (ObjectUtil.isNotEmpty(roleCodeList)&&roleCodeList.size()>0&&roleCodeList.contains(RoleConstant.TECHNICIAN)){
			task.setSpotTestTechnician(param.getContent()).setSpotTestTechnicianId(user.getId());
		}else{
			return Result.error("没有抽查权限，无法抽查");
		}
		patrolTaskService.updateById(task);

		return Result.ok();
	}

	/**
	 *  批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "巡检人员任务-批量删除")
	@ApiOperation(value="巡检人员任务-批量删除", notes="巡检人员任务-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		if(ids==null || "".equals(ids.trim())) {
			return Result.error("参数不识别！");
		}else {
			return patrolTaskService.deleteTaskByIds(Arrays.asList(ids.split(",")));
		}
	}


	/**
	 * 查询班组下个站点计划数、完成数、漏检数
	 * @return
	 */
	@AutoLog(value = "查询班组下个站点计划数、完成数、漏检数")
	@ApiOperation(value="查询班组下个站点计划数、完成数、漏检数", notes="查询班组下个站点计划数、完成数、漏检数")
	@PostMapping(value = "/appStationPatrolStatistics")
	public Result<List<AppStationPatrolStatisticsVO>> appStationPatrolStatistics() {
		return patrolTaskService.appStationPatrolStatistics();
	}

}
