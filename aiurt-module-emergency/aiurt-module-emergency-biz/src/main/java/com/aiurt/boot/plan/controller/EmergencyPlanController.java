package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.plan.entity.EmergencyPlan;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.service.IEmergencyPlanService;

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
 * @Description: emergency_plan
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_plan")
@RestController
@RequestMapping("/emergency/emergencyPlan")
@Slf4j
public class EmergencyPlanController extends BaseController<EmergencyPlan, IEmergencyPlanService> {
	@Autowired
	private IEmergencyPlanService emergencyPlanService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlan
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_plan-分页列表查询")
	@ApiOperation(value="emergency_plan-分页列表查询", notes="emergency_plan-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlan>> queryPageList(EmergencyPlan emergencyPlan,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlan> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlan, req.getParameterMap());
		Page<EmergencyPlan> page = new Page<EmergencyPlan>(pageNo, pageSize);
		IPage<EmergencyPlan> pageList = emergencyPlanService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyPlan
	 * @return
	 */
	@AutoLog(value = "emergency_plan-添加")
	@ApiOperation(value="emergency_plan-添加", notes="emergency_plan-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyPlan emergencyPlan) {
		emergencyPlanService.save(emergencyPlan);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyPlan
	 * @return
	 */
	@AutoLog(value = "emergency_plan-编辑")
	@ApiOperation(value="emergency_plan-编辑", notes="emergency_plan-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyPlan emergencyPlan) {
		emergencyPlanService.updateById(emergencyPlan);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_plan-通过id删除")
	@ApiOperation(value="emergency_plan-通过id删除", notes="emergency_plan-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan-批量删除")
	@ApiOperation(value="emergency_plan-批量删除", notes="emergency_plan-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_plan-通过id查询")
	@ApiOperation(value="emergency_plan-通过id查询", notes="emergency_plan-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyPlan> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyPlan emergencyPlan = emergencyPlanService.getById(id);
		if(emergencyPlan==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyPlan);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlan
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlan emergencyPlan) {
        return super.exportXls(request, emergencyPlan, EmergencyPlan.class, "emergency_plan");
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
        return super.importExcel(request, response, EmergencyPlan.class);
    }

}
