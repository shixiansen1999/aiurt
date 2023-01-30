package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.plan.entity.EmergencyPlanDisposalProcedure;
import com.aiurt.boot.plan.entity.EmergencyPlanRecordDisposalProcedure;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.service.IEmergencyPlanDisposalProcedureService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: emergency_plan_disposal_procedure
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="应急预案处置程序")
@RestController
@RequestMapping("/emergency/emergencyPlanDisposalProcedure")
@Slf4j
public class EmergencyPlanDisposalProcedureController extends BaseController<EmergencyPlanDisposalProcedure, IEmergencyPlanDisposalProcedureService> {
	@Autowired
	private IEmergencyPlanDisposalProcedureService emergencyPlanDisposalProcedureService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanDisposalProcedure
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="预案处置程序-分页列表查询", notes="预案处置程序-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlanDisposalProcedure>> queryPageList(EmergencyPlanDisposalProcedure emergencyPlanDisposalProcedure,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlanDisposalProcedure> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanDisposalProcedure, req.getParameterMap());
		Page<EmergencyPlanDisposalProcedure> page = new Page<EmergencyPlanDisposalProcedure>(pageNo, pageSize);
		IPage<EmergencyPlanDisposalProcedure> pageList = emergencyPlanDisposalProcedureService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 根据预案Id查询所有处置程序
	  * @param emergencyPlanDisposalProcedure
	  * @param id
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @ApiOperation(value="根据预案Id查询所有处置程序分页", notes="根据预案Id查询所有处置程序分页")
	 @GetMapping(value = "/queryPageListById")
	 public Result<IPage<EmergencyPlanDisposalProcedure>> queryPageListById(EmergencyPlanDisposalProcedure emergencyPlanDisposalProcedure,
																		String id,
																		@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
																		@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
																		HttpServletRequest req) {
		 QueryWrapper<EmergencyPlanDisposalProcedure> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanDisposalProcedure, req.getParameterMap());
		 queryWrapper.lambda().eq(EmergencyPlanDisposalProcedure::getEmergencyPlanId,id);
		 Page<EmergencyPlanDisposalProcedure> page = new Page<EmergencyPlanDisposalProcedure>(pageNo, pageSize);
		 IPage<EmergencyPlanDisposalProcedure> pageList = emergencyPlanDisposalProcedureService.page(page, queryWrapper);
		 return Result.OK(pageList);
	 }

	 @ApiOperation(value = "查询所有应急预案处置程序", notes = "查询所有应急预案处置程序")
	 @GetMapping(value = "/getAllPlanList")
	 public Result<List<EmergencyPlanDisposalProcedure>> getAllPlanList() {
		 List<EmergencyPlanDisposalProcedure> list = emergencyPlanDisposalProcedureService.list();
		 return Result.OK(list);
	 }

	/**
	 *   添加
	 *
	 * @param emergencyPlanDisposalProcedure
	 * @return
	 */
	@AutoLog(value = "emergency_plan_disposal_procedure-添加")
	@ApiOperation(value="emergency_plan_disposal_procedure-添加", notes="emergency_plan_disposal_procedure-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyPlanDisposalProcedure emergencyPlanDisposalProcedure) {
		emergencyPlanDisposalProcedureService.save(emergencyPlanDisposalProcedure);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyPlanDisposalProcedure
	 * @return
	 */
	@AutoLog(value = "emergency_plan_disposal_procedure-编辑")
	@ApiOperation(value="emergency_plan_disposal_procedure-编辑", notes="emergency_plan_disposal_procedure-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyPlanDisposalProcedure emergencyPlanDisposalProcedure) {
		emergencyPlanDisposalProcedureService.updateById(emergencyPlanDisposalProcedure);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_plan_disposal_procedure-通过id删除")
	@ApiOperation(value="emergency_plan_disposal_procedure-通过id删除", notes="emergency_plan_disposal_procedure-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanDisposalProcedureService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_disposal_procedure-批量删除")
	@ApiOperation(value="emergency_plan_disposal_procedure-批量删除", notes="emergency_plan_disposal_procedure-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanDisposalProcedureService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}


	 @ApiOperation(value="应急预案启动记录处置程序-通过id查询", notes="应急预案启动记录处置程序-通过id查询")
	 @GetMapping(value = "/queryById")
	 public Result<List<EmergencyPlanDisposalProcedure>> queryById(@RequestParam(name="id",required=false) String id) {
		 List<EmergencyPlanDisposalProcedure> emergencyPlanDisposalProcedures = emergencyPlanDisposalProcedureService.queryById(id);
		 if(CollUtil.isEmpty(emergencyPlanDisposalProcedures)) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(emergencyPlanDisposalProcedures);
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlanDisposalProcedure
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlanDisposalProcedure emergencyPlanDisposalProcedure) {
        return super.exportXls(request, emergencyPlanDisposalProcedure, EmergencyPlanDisposalProcedure.class, "emergency_plan_disposal_procedure");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, EmergencyPlanDisposalProcedure.class);
    }

}
