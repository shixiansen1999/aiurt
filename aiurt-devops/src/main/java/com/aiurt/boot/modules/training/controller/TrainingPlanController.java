package com.aiurt.boot.modules.training.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysUserService;
import com.aiurt.boot.modules.training.entity.TrainingPlan;
import com.aiurt.boot.modules.training.entity.TrainingPlanFile;
import com.aiurt.boot.modules.training.entity.TrainingPlanUser;
import com.aiurt.boot.modules.training.service.ITrainingPlanFileService;
import com.aiurt.boot.modules.training.service.ITrainingPlanService;
import com.aiurt.boot.modules.training.service.ITrainingPlanUserService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 培训计划
 * @Author: swsc
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "培训计划")
@RestController
@RequestMapping("/training/trainingPlan")
public class TrainingPlanController {

	private static final String TRAINING_PLAN_SING_URL = "/training/trainingPlan/signIn/";

	@Resource
	private ITrainingPlanService trainingPlanService;

	@Resource
	private ITrainingPlanUserService trainingPlanUserService;

	@Resource
	private ISysUserService sysUserService;

	@Resource
	private ITrainingPlanFileService trainingPlanFileService;

	/**
	 * 分页列表查询
	 *
	 * @param trainingPlan
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "培训计划-分页列表查询")
	@ApiOperation(value = "培训计划-分页列表查询", notes = "培训计划-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TrainingPlan>> queryPageList(HttpServletRequest req,
	                                                 TrainingPlan trainingPlan,
	                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		QueryWrapper<TrainingPlan> queryWrapper = QueryGenerator.initQueryWrapper(trainingPlan, req.getParameterMap());
		queryWrapper.lambda().eq(TrainingPlan::getDelFlag, CommonConstant.DEL_FLAG_0);
		IPage<TrainingPlan> pageList = trainingPlanService.page(new Page<>(pageNo, pageSize), queryWrapper);
		pageList.getRecords().forEach(f->{
			f.setQrCode(TRAINING_PLAN_SING_URL.concat(f.getId().toString()));
		});

		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param trainingPlan
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@AutoLog(value = "培训计划-添加")
	@ApiOperation(value = "培训计划-添加", notes = "培训计划-添加")
	@PostMapping(value = "/add")
	public Result<TrainingPlan> add(@RequestBody @Validated TrainingPlan trainingPlan) {
		Result<TrainingPlan> result = new Result<>();
		try {
			//插入主表
			trainingPlanService.save(trainingPlan);
			if (trainingPlan.getId() == null) {
				throw new AiurtBootException("插入培训计划失败");
			}
			Long planId = trainingPlan.getId();

			//存人员与文件
			saveUserAndFile(trainingPlan, planId);


			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new AiurtBootException("操作失败");
		}
		return result;
	}

	/**
	 * 编辑
	 *
	 * @param trainingPlan
	 * @return
	 */
	@AutoLog(value = "培训计划-编辑")
	@ApiOperation(value = "培训计划-编辑", notes = "培训计划-编辑")
	@PutMapping(value = "/edit")
	public Result<TrainingPlan> edit(@RequestBody TrainingPlan trainingPlan) {
		Result<TrainingPlan> result = new Result<>();
		TrainingPlan trainingPlanEntity = trainingPlanService.getById(trainingPlan.getId());
		if (trainingPlanEntity == null) {
			result.error500("未找到对应实体");
		} else {
			boolean ok = trainingPlanService.updateById(trainingPlan);

			this.trainingPlanFileService.remove(new LambdaQueryWrapper<TrainingPlanFile>().eq(TrainingPlanFile::getPlanId, trainingPlan.getId()));
			this.trainingPlanUserService.remove(new LambdaQueryWrapper<TrainingPlanUser>().eq(TrainingPlanUser::getPlanId, trainingPlan.getId()));

			//存人员与文件
			saveUserAndFile(trainingPlan, trainingPlan.getId());


			if (ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}


	/**
	 * 保存用户和文件
	 *
	 * @param trainingPlan 培训计划
	 * @param planId       计划id
	 * @return boolean
	 */
	private boolean saveUserAndFile(TrainingPlan trainingPlan, Long planId) {
		//存人员信息
		List<String> userIds = trainingPlan.getUserIds();
		List<SysUser> list = sysUserService.list(new LambdaQueryWrapper<SysUser>()
				.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
				.in(SysUser::getId, userIds)
				.select(SysUser::getId, SysUser::getRealname)
		);
		Map<String, String> realNameMap = list.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getRealname));
		List<TrainingPlanUser> planUserList = new ArrayList<>();
		for (String userId : userIds) {
			TrainingPlanUser planUser = new TrainingPlanUser();
			planUser.setPlanId(planId)
					.setUserId(userId)
					.setRealName(realNameMap.get(userId))
					.setSignStatus(CommonConstant.STATUS_NORMAL);
			planUserList.add(planUser);
		}

		this.trainingPlanUserService.saveBatch(planUserList);

		//存文件
		List<Long> fileIds = trainingPlan.getFileIds();
		if (CollectionUtils.isNotEmpty(fileIds)) {
			List<TrainingPlanFile> planFileList = new ArrayList<>();
			for (Long fileId : fileIds) {
				TrainingPlanFile planFile = new TrainingPlanFile();
				planFile.setFileId(fileId).setPlanId(planId);
				planFileList.add(planFile);
			}
			this.trainingPlanFileService.saveBatch(planFileList);
		}

		return true;
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培训计划-通过id删除")
	@ApiOperation(value = "培训计划-通过id删除", notes = "培训计划-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			trainingPlanService.removeById(id);
			this.trainingPlanFileService.remove(new LambdaQueryWrapper<TrainingPlanFile>().eq(TrainingPlanFile::getPlanId, id));
			this.trainingPlanUserService.remove(new LambdaQueryWrapper<TrainingPlanUser>().eq(TrainingPlanUser::getPlanId, id));
		} catch (Exception e) {
			log.error("删除失败", e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "培训计划-批量删除")
	@ApiOperation(value = "培训计划-批量删除", notes = "培训计划-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<TrainingPlan> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<TrainingPlan> result = new Result<>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			List<String> idList = Arrays.asList(ids.split(","));
			this.trainingPlanService.removeByIds(idList);
			this.trainingPlanFileService.remove(new LambdaQueryWrapper<TrainingPlanFile>().in(TrainingPlanFile::getPlanId, idList));
			this.trainingPlanUserService.remove(new LambdaQueryWrapper<TrainingPlanUser>().in(TrainingPlanUser::getPlanId, idList));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培训计划-通过id查询")
	@ApiOperation(value = "培训计划-通过id查询", notes = "培训计划-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TrainingPlan> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<TrainingPlan> result = new Result<>();
		TrainingPlan trainingPlan = trainingPlanService.getById(id);
		if (trainingPlan == null) {
			result.onnull("未找到对应实体");
		} else {
			result.setResult(trainingPlan);
			result.setSuccess(true);
		}
		return result;
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培训计划-通过id查询")
	@ApiOperation(value = "培训计划-通过id查询", notes = "培训计划-通过id查询")
	@GetMapping(value = "/signIn/{id}")
	public Result<?> signIn(@PathVariable(name = "id", required = true)@NotNull(message = "id不能未空") Long id) {

		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		TrainingPlanUser planUser = trainingPlanUserService.getOne(new LambdaQueryWrapper<TrainingPlanUser>()
				.eq(TrainingPlanUser::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(TrainingPlanUser::getPlanId, id)
				.eq(TrainingPlanUser::getUserId, loginUser.getId())
				.last("limit 1")
		);
		if (planUser == null) {
			return Result.error("未找到对应课程");
		} else if (ObjectUtil.equal(CommonConstant.STATUS_ENABLE,planUser.getSignStatus())) {
			return Result.error("已签到,请勿重复签到");
		}else {
			planUser.setSignStatus(CommonConstant.STATUS_ENABLE).setSignTime(new Date());
			trainingPlanUserService.updateById(planUser);
			return Result.ok("签到成功");
		}
	}
}
