package com.aiurt.boot.plan.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.dto.EmergencyPlanDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordQueryDTO;
import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.plan.entity.EmergencyPlanRecord;
import com.aiurt.boot.plan.vo.EmergencyPlanRecordVO;
import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.vo.EmergencyRecordReadOneVO;
import com.aiurt.boot.team.entity.EmergencyTeam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.service.IEmergencyPlanRecordService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDeptUserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: emergency_plan_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="应急预案-应急启动记录")
@RestController
@RequestMapping("/emergency/emergencyPlanRecord")
@Slf4j
public class EmergencyPlanRecordController extends BaseController<EmergencyPlanRecord, IEmergencyPlanRecordService> {
	@Autowired
	private IEmergencyPlanRecordService emergencyPlanRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanRecordQueryDto
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_plan_record-分页列表查询")
	@ApiOperation(value="应急启动记录-分页列表查询", notes="应急启动记录-分页列表查询")
	@GetMapping(value = "/queryPageList")
	public Result<IPage<EmergencyPlanRecordVO>> queryPageList(EmergencyPlanRecordQueryDTO emergencyPlanRecordQueryDto,
															  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
															  HttpServletRequest req) {
		Page<EmergencyPlanRecordVO> page = new Page<>(pageNo, pageSize);
		IPage<EmergencyPlanRecordVO> pageList = emergencyPlanRecordService.queryPageList(page, emergencyPlanRecordQueryDto);
		return Result.OK(pageList);
	}


	 /**
	  * 应急预案启动记录保存新增
	  * @param emergencyPlanRecordDto
	  * @return
	  */
	 @AutoLog(value = "应急预案启动记录保存新增")
	 @ApiOperation(value="应急预案启动记录保存新增", notes="应急预案启动记录保存新增")
	 @PostMapping(value = "/add")
	 public Result<String> add(@RequestBody EmergencyPlanRecordDTO emergencyPlanRecordDto) {
		 emergencyPlanRecordService.saveAndAdd(emergencyPlanRecordDto);
		 return Result.OK("保存成功!");
	 }



	 @AutoLog(value = "应急预案启动记录-编辑")
	 @ApiOperation(value="应急预案启动记录-编辑", notes="应急预案台账-编辑")
	 @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> edit(@RequestBody  EmergencyPlanRecordDTO emergencyPlanRecordDto) {
		 emergencyPlanRecordService.edit(emergencyPlanRecordDto);
		 return Result.OK("编辑成功!");
	 }



	@AutoLog(value = "应急预案启动记录-通过id删除")
	@ApiOperation(value="应急预案启动记录-通过id删除", notes="应急预案启动-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanRecordService.delete(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record-批量删除")
	@ApiOperation(value="emergency_plan_record-批量删除", notes="emergency_plan_record-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}


	@AutoLog(value = "应急预案启动记录-通过id查询")
	@ApiOperation(value = "应急预案启动记录-通过id查询", notes = "应急预案启动记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyPlanRecordDTO> queryById(@RequestParam(name = "id", required = true) String id) {
		EmergencyPlanRecordDTO planRecordDTO = emergencyPlanRecordService.queryById(id);
		return Result.OK(planRecordDTO);
	}

	 /**
	  * 应急模块-责任部门和用户联动信息
	  */
	 @AutoLog(value = "启动记录新增-责任部门和用户联动信息")
	 @ApiOperation(value = "应急模块-责任部门和用户联动信息", notes = "应急模块-责任部门和用户联动信息")
	 @GetMapping(value = "/getDeptUserGanged")
	 public Result<List<SysDeptUserModel>> getDeptUserGanged() {
		 List<SysDeptUserModel> deptUsers = emergencyPlanRecordService.getDeptUserGanged();
		 return Result.OK(deptUsers);
	 }

	 /**
	  * 应急模块-责任人信息
	  */
	 @AutoLog(value = "启动记录新增-责任人信息")
	 @ApiOperation(value = "应急模块-责任人信息", notes = "应急模块-责任人信息")
	 @GetMapping(value = "/getDutyUser")
	 public Result<List<LoginUser>> getDutyUser() {
		 List<LoginUser> users = emergencyPlanRecordService.getDutyUser();
		 return Result.OK(users);
	 }

	 @AutoLog(value = "应急预案-应急预案启动记录提交")
	 @ApiOperation(value = "应急预案-应急预案启动记录提交", notes = "应急预案-应急预案启动记录提交")
	 @PostMapping(value = "/submit")
	 public Result<String> startProcess(@RequestBody EmergencyPlanRecordDTO emergencyPlanRecordDto) {
		 emergencyPlanRecordService.submit(emergencyPlanRecordDto);
		 return Result.OK("提交成功!");
	 }


	 /**
	  * 应急预案导入模板下载
	  */
	 @AutoLog(value = "下载导入模板", operateType = 6, operateTypeAlias = "下载导入模板", permissionUrl = "")
	 @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
	 @RequestMapping(value = "/exportTemplateXls", method = RequestMethod.GET)
	 public void exportTemplateXl(HttpServletResponse response, HttpServletRequest request) throws IOException {
		 emergencyPlanRecordService.exportTemplateXls(response,request);
	 }

	 /**
	  * 通过excel导入数据
	  *
	  * @param request
	  * @param response
	  * @return
	  */
	 @ApiOperation(value = "应急预案启动记录-导入excel", notes = "应急预案启动记录-导入excel")
	 @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	 public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		 return emergencyPlanRecordService.importExcel(request, response);
	 }

	 /**
	  * 应急预案台账导出数据
	  * @param request
	  * @param response
	  * @param emergencyPlanRecordDto
	  */
	 @AutoLog(value = "应急预案-应急预案台账导出数据")
	 @GetMapping(value = "/exportXls")
	 public void exportXls(HttpServletRequest request, HttpServletResponse response, EmergencyPlanRecordDTO emergencyPlanRecordDto) {
		 emergencyPlanRecordService.exportXls(request,response,emergencyPlanRecordDto);
	 }

}
