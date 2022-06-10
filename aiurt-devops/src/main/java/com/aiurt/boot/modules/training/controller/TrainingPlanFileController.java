package com.aiurt.boot.modules.training.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.training.service.ITrainingPlanFileService;
import com.aiurt.boot.modules.training.vo.TrainingPlanFileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * @description: TrainingPlanFileController
 * @author: Mr.zhao
 * @date: 2021/11/28 18:22
 */
@Slf4j
@Api(tags = "培训文件")
@RestController
@RequestMapping("/training/trainingPlanFile")
public class TrainingPlanFileController {

	@Resource
	private ITrainingPlanFileService trainingPlanFileService;


	/**
	 * 分页列表查询
	 *
	 * @param planId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "培训文件-分页列表查询")
	@ApiOperation(value = "培训文件-分页列表查询", notes = "培训文件-分页列表查询")
	@GetMapping(value = "/listByPlanId")
	public Result<IPage<TrainingPlanFileVO>> queryPageList(@RequestParam(name = "planId")@NotNull(message = "id不能为空") Long planId,
	                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		IPage<TrainingPlanFileVO> pageList = this.trainingPlanFileService.listByPlanId(pageNo,pageSize,planId);

		return Result.ok(pageList);
	}

}
