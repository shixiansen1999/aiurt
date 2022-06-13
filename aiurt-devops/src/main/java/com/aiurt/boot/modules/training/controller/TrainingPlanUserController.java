package com.aiurt.boot.modules.training.controller;

import com.aiurt.boot.modules.training.entity.TrainingPlanUser;
import com.aiurt.boot.modules.training.param.PlanUserParam;
import com.aiurt.boot.modules.training.service.ITrainingPlanUserService;
import com.aiurt.boot.modules.training.vo.PlanUserVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * 培训人员
 *
 * @Description: 培训人员
 * @Author: Mr. zhao
 * @Date: 2021-11-28
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "培训人员")
@RestController
@RequestMapping("/training/trainingPlanUser")
public class TrainingPlanUserController {

	@Resource
	private ITrainingPlanUserService trainingPlanUserService;


	/**
	 * 分页列表查询
	 *
	 * @param param
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "培训计划-分页查询个人列表")
	@ApiOperation(value = "培训计划-分页查询个人列表", notes = "培训计划-分页查询个人列表")
	@GetMapping(value = "/listPlan")
	public Result<IPage<PlanUserVO>> queryPageList(HttpServletRequest req,
	                                               PlanUserParam param,
	                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		if (param.getUserId() == null) {
			LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			param.setUserId(loginUser.getId());
		}

		IPage<PlanUserVO> page = this.trainingPlanUserService.listPlan(req, param, pageNo, pageSize);

		return Result.ok(page);
	}


	/**
	 * 分页列表查询
	 *
	 * @param trainingPlanUser
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "培训人员-纯分页列表查询")
	@ApiOperation(value = "培训人员-纯分页列表查询", notes = "培训人员-纯分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TrainingPlanUser>> queryPageList(HttpServletRequest req,
	                                                     TrainingPlanUser trainingPlanUser,
	                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		QueryWrapper<TrainingPlanUser> queryWrapper = QueryGenerator.initQueryWrapper(trainingPlanUser, req.getParameterMap());
		queryWrapper.lambda().eq(TrainingPlanUser::getDelFlag, CommonConstant.DEL_FLAG_0);

		IPage<TrainingPlanUser> pageList = trainingPlanUserService.page(new Page<>(pageNo, pageSize), queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 分页列表查询
	 *
	 * @param planId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "培训人员-id列表查询")
	@ApiOperation(value = "培训人员-id列表查询", notes = "培训人员-id列表查询")
	@GetMapping(value = "/listByPlanId")
	public Result<IPage<TrainingPlanUser>> queryPageList(@RequestParam(name = "planId") @NotNull(message = "id不能为空") Long planId,
	                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		IPage<TrainingPlanUser> pageList = this.trainingPlanUserService.listByPlanId(pageNo, pageSize, planId);

		return Result.ok(pageList);
	}


	/**
	 * 编辑
	 *
	 * @param trainingPlan
	 * @return
	 */
	@AutoLog(value = "培训人员-编辑")
	@ApiOperation(value = "培训人员-编辑", notes = "培训人员-编辑")
	@PutMapping(value = "/edit")
	public Result<TrainingPlanUser> edit(@RequestBody TrainingPlanUser trainingPlan) {
		Result<TrainingPlanUser> result = new Result<>();
		TrainingPlanUser trainingPlanEntity = trainingPlanUserService.getById(trainingPlan.getId());
		if (trainingPlanEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			boolean ok = trainingPlanUserService.updateById(trainingPlan);
			if (ok) {
				result.success("修改成功!");
			}
		}
		return result;
	}


}
