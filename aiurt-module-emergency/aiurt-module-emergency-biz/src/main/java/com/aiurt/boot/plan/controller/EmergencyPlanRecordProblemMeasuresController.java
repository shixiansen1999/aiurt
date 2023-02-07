package com.aiurt.boot.plan.controller;

import com.aiurt.boot.plan.entity.EmergencyPlanRecordProblemMeasures;
import com.aiurt.boot.plan.service.IEmergencyPlanRecordProblemMeasuresService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: emergency_plan_record_problem_measures
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_plan_record_problem_measures")
@RestController
@RequestMapping("/emergency/emergencyPlanRecordProblemMeasures")
@Slf4j
public class EmergencyPlanRecordProblemMeasuresController extends BaseController<EmergencyPlanRecordProblemMeasures, IEmergencyPlanRecordProblemMeasuresService> {
	@Autowired
	private IEmergencyPlanRecordProblemMeasuresService emergencyPlanRecordProblemMeasuresService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanRecordProblemMeasures
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="emergency_plan_record_problem_measures-分页列表查询", notes="emergency_plan_record_problem_measures-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlanRecordProblemMeasures>> queryPageList(EmergencyPlanRecordProblemMeasures emergencyPlanRecordProblemMeasures,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlanRecordProblemMeasures> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanRecordProblemMeasures, req.getParameterMap());
		Page<EmergencyPlanRecordProblemMeasures> page = new Page<EmergencyPlanRecordProblemMeasures>(pageNo, pageSize);
		IPage<EmergencyPlanRecordProblemMeasures> pageList = emergencyPlanRecordProblemMeasuresService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyPlanRecordProblemMeasures
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_problem_measures-添加")
	@ApiOperation(value="emergency_plan_record_problem_measures-添加", notes="emergency_plan_record_problem_measures-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyPlanRecordProblemMeasures emergencyPlanRecordProblemMeasures) {
		emergencyPlanRecordProblemMeasuresService.save(emergencyPlanRecordProblemMeasures);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyPlanRecordProblemMeasures
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_problem_measures-编辑")
	@ApiOperation(value="emergency_plan_record_problem_measures-编辑", notes="emergency_plan_record_problem_measures-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyPlanRecordProblemMeasures emergencyPlanRecordProblemMeasures) {
		emergencyPlanRecordProblemMeasuresService.updateById(emergencyPlanRecordProblemMeasures);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_problem_measures-通过id删除")
	@ApiOperation(value="emergency_plan_record_problem_measures-通过id删除", notes="emergency_plan_record_problem_measures-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanRecordProblemMeasuresService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_problem_measures-批量删除")
	@ApiOperation(value="emergency_plan_record_problem_measures-批量删除", notes="emergency_plan_record_problem_measures-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanRecordProblemMeasuresService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="emergency_plan_record_problem_measures-通过id查询", notes="emergency_plan_record_problem_measures-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyPlanRecordProblemMeasures> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyPlanRecordProblemMeasures emergencyPlanRecordProblemMeasures = emergencyPlanRecordProblemMeasuresService.getById(id);
		if(emergencyPlanRecordProblemMeasures==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyPlanRecordProblemMeasures);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlanRecordProblemMeasures
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlanRecordProblemMeasures emergencyPlanRecordProblemMeasures) {
        return super.exportXls(request, emergencyPlanRecordProblemMeasures, EmergencyPlanRecordProblemMeasures.class, "emergency_plan_record_problem_measures");
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
        return super.importExcel(request, response, EmergencyPlanRecordProblemMeasures.class);
    }

}
