package com.aiurt.boot.rehearsal.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordStep;
import com.aiurt.boot.rehearsal.service.IEmergencyRecordStepService;

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
 * @Description: emergency_record_step
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_record_step")
@RestController
@RequestMapping("/emergency/emergencyRecordStep")
@Slf4j
public class EmergencyRecordStepController extends BaseController<EmergencyRecordStep, IEmergencyRecordStepService> {
	@Autowired
	private IEmergencyRecordStepService emergencyRecordStepService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyRecordStep
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_record_step-分页列表查询")
	@ApiOperation(value="emergency_record_step-分页列表查询", notes="emergency_record_step-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyRecordStep>> queryPageList(EmergencyRecordStep emergencyRecordStep,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyRecordStep> queryWrapper = QueryGenerator.initQueryWrapper(emergencyRecordStep, req.getParameterMap());
		Page<EmergencyRecordStep> page = new Page<EmergencyRecordStep>(pageNo, pageSize);
		IPage<EmergencyRecordStep> pageList = emergencyRecordStepService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyRecordStep
	 * @return
	 */
	@AutoLog(value = "emergency_record_step-添加")
	@ApiOperation(value="emergency_record_step-添加", notes="emergency_record_step-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyRecordStep emergencyRecordStep) {
		emergencyRecordStepService.save(emergencyRecordStep);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyRecordStep
	 * @return
	 */
	@AutoLog(value = "emergency_record_step-编辑")
	@ApiOperation(value="emergency_record_step-编辑", notes="emergency_record_step-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyRecordStep emergencyRecordStep) {
		emergencyRecordStepService.updateById(emergencyRecordStep);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_record_step-通过id删除")
	@ApiOperation(value="emergency_record_step-通过id删除", notes="emergency_record_step-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyRecordStepService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_record_step-批量删除")
	@ApiOperation(value="emergency_record_step-批量删除", notes="emergency_record_step-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyRecordStepService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_record_step-通过id查询")
	@ApiOperation(value="emergency_record_step-通过id查询", notes="emergency_record_step-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyRecordStep> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyRecordStep emergencyRecordStep = emergencyRecordStepService.getById(id);
		if(emergencyRecordStep==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyRecordStep);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyRecordStep
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyRecordStep emergencyRecordStep) {
        return super.exportXls(request, emergencyRecordStep, EmergencyRecordStep.class, "emergency_record_step");
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
        return super.importExcel(request, response, EmergencyRecordStep.class);
    }

}
