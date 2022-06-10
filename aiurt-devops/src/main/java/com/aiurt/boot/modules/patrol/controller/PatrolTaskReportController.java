package com.aiurt.boot.modules.patrol.controller;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.exception.SwscException;
import com.swsc.copsms.modules.patrol.param.ReportAllParam;
import com.swsc.copsms.modules.patrol.param.ReportOneParam;
import com.swsc.copsms.modules.patrol.service.IPatrolTaskReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * @Description: 巡检人员巡检项报告
 * @Author: qian
 * @Date: 2021-09-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "巡检人员巡检项报告")
@RestController
@RequestMapping("/patrol/patrolTaskReport")
public class PatrolTaskReportController {
	@Autowired
	private IPatrolTaskReportService patrolTaskReportService;

	/**
	 * 树列表查询
	 *
	 * @param req
	 * @return
	 */
	@AutoLog(value = "巡检人员巡检项报告-树状查询")
	@ApiOperation(value = "巡检人员巡检项报告-树状查询", notes = "巡检人员巡检项报告-树状查询")
	@GetMapping(value = "/tree")
	public Result<?> tree(HttpServletRequest req,@RequestParam("id") Long id) {

		return patrolTaskReportService.tree(req,id);
	}


	/**
	 * 分页列表查询
	 *
	 * @param req
	 * @return
	 */
	@AutoLog(value = "巡检人员巡检项报告-查看结果-获取url")
	@ApiOperation(value = "巡检人员巡检项报告-查看结果-获取url", notes = "巡检人员巡检项报告-查看结果-获取url")
	@GetMapping(value = "/getUrl")
	public Result<?> getUrl(HttpServletRequest req,@RequestParam("id") Long id) {

		return patrolTaskReportService.getUrl(req,id);
	}

	/**
	 * 单项报告提交/保存
	 *
	 * @param req
	 * @return
	 */
	@AutoLog(value = "巡检人员巡检项报告表-单项报告提交/保存")
	@ApiOperation(value = "巡检人员巡检项报告表-单项报告提交/保存", notes = "巡检人员巡检项报告表-单项报告提交/保存")
	@PostMapping(value = "/oneReport")
	public Result<?> oneReport(HttpServletRequest req,@RequestBody ReportOneParam  param) {
		if (param.getId() == null) {
			throw new SwscException("报告表id不能为空");
		}
		if (param.getSaveStatus()==null) {
			throw new SwscException("保存状态不能为空");
		}

		return patrolTaskReportService.oneReport(req,param);
	}


	@AutoLog(value = "巡检人员巡检项报告-多项报告提交/保存")
	@ApiOperation(value = "巡检人员巡检项报告-多项报告提交/保存", notes = "巡检人员巡检项报告-多项报告提交/保存")
	@PostMapping(value = "/reportAll")
	public Result<?> reportAll(HttpServletRequest req,@RequestBody ReportAllParam param) {

		return patrolTaskReportService.reportAll(req,param);
	}



	@AutoLog(value = "巡检人员巡检项报告-提交签字")
	@ApiOperation(value = "巡检人员巡检项报告-提交签字", notes = "巡检人员巡检项报告-提交签字")
	@PostMapping(value = "/sign")
	public Result<?> sign(HttpServletRequest req,
	                      @RequestParam("id") @NotBlank(message = "id不能为空") Long id,
	                      @RequestParam("url") @NotBlank(message = "url不能为空") String url) {

		return patrolTaskReportService.sign(req,id,url);
	}




}
