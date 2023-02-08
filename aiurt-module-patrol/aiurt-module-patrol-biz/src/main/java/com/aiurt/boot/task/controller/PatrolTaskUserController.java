package com.aiurt.boot.task.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.task.dto.PatrolAccompanyDTO;
import com.aiurt.boot.task.dto.PatrolTaskAppointSaveDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.boot.task.service.IPatrolTaskUserService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description: patrol_task_user
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="任务巡检用户")
@RestController
@RequestMapping("/patrolTaskUser")
@Slf4j
public class PatrolTaskUserController extends BaseController<PatrolTaskUser, IPatrolTaskUserService> {
	@Autowired
	private IPatrolTaskUserService patrolTaskUserService;
	@Autowired
	private IPatrolTaskService patrolTaskService;

	 /**
	  *app巡检任务列表-指派
	  * @param patrolAccompanyList
	  * @return
	  */
	 @AutoLog(value = "app巡检任务列表-指派", operateType = 3, operateTypeAlias = "修改", module = ModuleType.PATROL,permissionUrl = "/Inspection/pool")
	 @ApiOperation(value="app巡检任务列表-指派", notes="app巡检任务列表-指派")
	 @PostMapping(value = "/patrolTaskAppointed")
	 public Result<String> patrolTaskAppointed(@RequestBody PatrolTaskAppointSaveDTO patrolAccompanyList) {
		 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 Assert.notNull(loginUser,"检测到未登录，请登录后操作！");
		 //将任务来源改为常规指派,将任务状态改为待确认
		 SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		 Date startTime = null;
		 Date endTime = null;
		 try {
		 	if(ObjectUtil.isNotEmpty(patrolAccompanyList.getStartTime())){
				 startTime = format.parse(patrolAccompanyList.getStartTime());
			 }
			 if(ObjectUtil.isNotEmpty(patrolAccompanyList.getEndTime())){
				 endTime = format.parse(patrolAccompanyList.getEndTime());
			 }
		 } catch (ParseException e) {
			 e.printStackTrace();
		 }
		List <PatrolTaskUser> taskUserList = patrolTaskUserService.list(new LambdaQueryWrapper<PatrolTaskUser>().eq(PatrolTaskUser::getTaskCode, patrolAccompanyList.getCode()));
		 if(CollUtil.isNotEmpty(taskUserList))
		 {
			 patrolTaskUserService.removeBatchByIds(taskUserList);
		 }
		 LambdaUpdateWrapper<PatrolTask> updateWrapper = new LambdaUpdateWrapper<>();
		 updateWrapper.set(PatrolTask::getSource, 2).set(PatrolTask::getStatus, 1)
				 .set(PatrolTask::getPlanCode, patrolAccompanyList.getPlanCode()).set(PatrolTask::getType, patrolAccompanyList.getType())
				 .set(PatrolTask::getPlanOrderCodeUrl, patrolAccompanyList.getPlanOrderCodeUrl()).eq(PatrolTask::getCode, patrolAccompanyList.getCode());
		 if(ObjectUtil.isNotEmpty(patrolAccompanyList.getStartTime())){
			 updateWrapper.set(PatrolTask::getStartTime, startTime);
		 }
		 if(ObjectUtil.isNotEmpty(patrolAccompanyList.getEndTime())){
			 updateWrapper.set(PatrolTask::getEndTime, endTime);
		 }
		 // 添加指派人
		 updateWrapper.set(PatrolTask::getAssignId, loginUser.getId());
		 patrolTaskService.update(updateWrapper);
		 //添加巡检人
		 List<PatrolAccompanyDTO> list = patrolAccompanyList.getAccompanyDTOList();
		 list.stream().forEach(e->{
			 PatrolTaskUser patrolTaskUser = new PatrolTaskUser();
			 patrolTaskUser.setTaskCode(patrolAccompanyList.getCode());
			 patrolTaskUser.setUserId(e.getUserId());
			 patrolTaskUser.setUserName(e.getUsername());
			 patrolTaskUser.setDelFlag(0);
			 patrolTaskUserService.save(patrolTaskUser);
		 });
		 // 发送消息
		 patrolTaskService.sendMessageApp(patrolAccompanyList);
		 return Result.OK("指派成功！");
	 }
}
