package com.aiurt.boot.modules.patrol.controller;

import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.modules.patrol.param.ReportAllParam;
import com.aiurt.boot.modules.patrol.param.ReportOneParam;
import com.aiurt.boot.modules.patrol.param.ReportSignParam;
import com.aiurt.boot.modules.patrol.param.UrlParam;
import com.aiurt.boot.modules.patrol.service.IPatrolTaskReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 巡检人员巡检项报告
 * @Author: Mr.zhao
 * @Date: 2021-09-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "巡检人员巡检项报告")
@RestController
@RequestMapping("/patrol/patrolTaskReport")
public class PatrolTaskReportController {
	@Resource
	private IPatrolTaskReportService patrolTaskReportService;


	@AutoLog(value = "巡检人员巡检项报告-树状查询")
	@ApiOperation(value = "巡检人员巡检项报告-树状查询", notes = "巡检人员巡检项报告-树状查询")
	@GetMapping(value = "/tree")
	public Result<?> tree(HttpServletRequest req, @RequestParam("id") Long id) {
		return patrolTaskReportService.tree(req, id, null);
	}


	@AutoLog(value = "巡检人员巡检项报告-查看结果-获取url")
	@ApiOperation(value = "巡检人员巡检项报告-查看结果-获取url", notes = "巡检人员巡检项报告-查看结果-获取url")
	@GetMapping(value = "/getUrl")
	public Result<?> getUrl(HttpServletRequest req, UrlParam param) {
		return patrolTaskReportService.getUrl(req, param);
	}


	@AutoLog(value = "巡检人员巡检项报告表-单项报告提交/保存")
	@ApiOperation(value = "巡检人员巡检项报告表-单项报告提交/保存", notes = "巡检人员巡检项报告表-单项报告提交/保存")
	@PostMapping(value = "/oneReport")
	public Result<?> oneReport(HttpServletRequest req, @RequestBody @Validated ReportOneParam param) {
		if (param.getTaskId() == null) {
			throw new SwscException("报告表id不能为空");
		}
		if (param.getSaveStatus() == null) {
			throw new SwscException("保存状态不能为空");
		}
		return patrolTaskReportService.oneReport(req, param);
	}


	@AutoLog(value = "巡检人员巡检项报告-多项报告提交/保存")
	@ApiOperation(value = "巡检人员巡检项报告-多项报告提交/保存", notes = "巡检人员巡检项报告-多项报告提交/保存")
	@PostMapping(value = "/reportAll")
	public Result<?> reportAll(HttpServletRequest req, @RequestBody @Validated ReportAllParam param) {

		return patrolTaskReportService.reportAll(req, param);
	}


	@AutoLog(value = "巡检人员巡检项报告-提交签字")
	@ApiOperation(value = "巡检人员巡检项报告-提交签字", notes = "巡检人员巡检项报告-提交签字")
	@PostMapping(value = "/sign")
	public Result<?> sign(HttpServletRequest req,
	                      @RequestBody ReportSignParam param) {

		return patrolTaskReportService.sign(req, param);
	}


}
