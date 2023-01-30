package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.plan.entity.EmergencyPlanRecordDepart;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.service.IEmergencyPlanRecordDepartService;

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
 * @Description: emergency_plan_record_depart
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_plan_record_depart")
@RestController
@RequestMapping("/emergency/emergencyPlanRecordDepart")
@Slf4j
public class EmergencyPlanRecordDepartController extends BaseController<EmergencyPlanRecordDepart, IEmergencyPlanRecordDepartService> {
	@Autowired
	private IEmergencyPlanRecordDepartService emergencyPlanRecordDepartService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanRecordDepart
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="emergency_plan_record_depart-分页列表查询", notes="emergency_plan_record_depart-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlanRecordDepart>> queryPageList(EmergencyPlanRecordDepart emergencyPlanRecordDepart,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlanRecordDepart> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanRecordDepart, req.getParameterMap());
		Page<EmergencyPlanRecordDepart> page = new Page<EmergencyPlanRecordDepart>(pageNo, pageSize);
		IPage<EmergencyPlanRecordDepart> pageList = emergencyPlanRecordDepartService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyPlanRecordDepart
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_depart-添加")
	@ApiOperation(value="emergency_plan_record_depart-添加", notes="emergency_plan_record_depart-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyPlanRecordDepart emergencyPlanRecordDepart) {
		emergencyPlanRecordDepartService.save(emergencyPlanRecordDepart);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyPlanRecordDepart
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_depart-编辑")
	@ApiOperation(value="emergency_plan_record_depart-编辑", notes="emergency_plan_record_depart-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyPlanRecordDepart emergencyPlanRecordDepart) {
		emergencyPlanRecordDepartService.updateById(emergencyPlanRecordDepart);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_depart-通过id删除")
	@ApiOperation(value="emergency_plan_record_depart-通过id删除", notes="emergency_plan_record_depart-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanRecordDepartService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_depart-批量删除")
	@ApiOperation(value="emergency_plan_record_depart-批量删除", notes="emergency_plan_record_depart-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanRecordDepartService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="emergency_plan_record_depart-通过id查询", notes="emergency_plan_record_depart-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyPlanRecordDepart> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyPlanRecordDepart emergencyPlanRecordDepart = emergencyPlanRecordDepartService.getById(id);
		if(emergencyPlanRecordDepart==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyPlanRecordDepart);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlanRecordDepart
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlanRecordDepart emergencyPlanRecordDepart) {
        return super.exportXls(request, emergencyPlanRecordDepart, EmergencyPlanRecordDepart.class, "emergency_plan_record_depart");
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
        return super.importExcel(request, response, EmergencyPlanRecordDepart.class);
    }

}
