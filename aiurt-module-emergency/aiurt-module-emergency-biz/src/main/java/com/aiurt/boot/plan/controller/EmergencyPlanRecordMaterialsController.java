package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.plan.dto.EmergencyPlanMaterialsDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanRecordMaterials;
import com.aiurt.common.constant.enums.ModuleType;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.service.IEmergencyPlanRecordMaterialsService;

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
 * @Description: emergency_plan_record_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="应急预案启动记录应急物资关联")
@RestController
@RequestMapping("/emergency/emergencyPlanRecordMaterials")
@Slf4j
public class EmergencyPlanRecordMaterialsController extends BaseController<EmergencyPlanRecordMaterials, IEmergencyPlanRecordMaterialsService> {
	@Autowired
	private IEmergencyPlanRecordMaterialsService emergencyPlanRecordMaterialsService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanRecordMaterials
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="emergency_plan_record_materials-分页列表查询", notes="emergency_plan_record_materials-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlanRecordMaterials>> queryPageList(EmergencyPlanRecordMaterials emergencyPlanRecordMaterials,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlanRecordMaterials> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanRecordMaterials, req.getParameterMap());
		Page<EmergencyPlanRecordMaterials> page = new Page<EmergencyPlanRecordMaterials>(pageNo, pageSize);
		IPage<EmergencyPlanRecordMaterials> pageList = emergencyPlanRecordMaterialsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyPlanRecordMaterials
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_materials-添加")
	@ApiOperation(value="emergency_plan_record_materials-添加", notes="emergency_plan_record_materials-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyPlanRecordMaterials emergencyPlanRecordMaterials) {
		emergencyPlanRecordMaterialsService.save(emergencyPlanRecordMaterials);
		return Result.OK("添加成功！");
	}

	 /**
	  * 应急预案物资列表查询
	  * @param condition
	  * @param pageNo
	  * @param pageSize
	  * @return
	  */
	 @AutoLog(value = "应急预案物资列表查询", operateType =  1, operateTypeAlias = "应急预案物资列表查询", module = ModuleType.INSPECTION)
	 @ApiOperation(value = "应急预案物资列表查询", notes = "应急预案物资列表查询")
	 @GetMapping(value = "/getMaterialAccountList")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = EmergencyPlanMaterialsDTO.class)
	 })
	 public Result<Page<EmergencyPlanMaterialsDTO>> repairTaskPageList(EmergencyPlanMaterialsDTO condition,
																	   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
																	   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
	 ) {
		 Page<EmergencyPlanMaterialsDTO> pageList = new Page<>(pageNo, pageSize);
		 Page<EmergencyPlanMaterialsDTO> repairTaskPage = emergencyPlanRecordMaterialsService.getMaterialAccountList(pageList, condition);
		 return Result.OK(repairTaskPage);
	 }


	 /**
	 *  编辑
	 *
	 * @param emergencyPlanRecordMaterials
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_materials-编辑")
	@ApiOperation(value="emergency_plan_record_materials-编辑", notes="emergency_plan_record_materials-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyPlanRecordMaterials emergencyPlanRecordMaterials) {
		emergencyPlanRecordMaterialsService.updateById(emergencyPlanRecordMaterials);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_materials-通过id删除")
	@ApiOperation(value="emergency_plan_record_materials-通过id删除", notes="emergency_plan_record_materials-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanRecordMaterialsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_materials-批量删除")
	@ApiOperation(value="emergency_plan_record_materials-批量删除", notes="emergency_plan_record_materials-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanRecordMaterialsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="emergency_plan_record_materials-通过id查询", notes="emergency_plan_record_materials-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyPlanRecordMaterials> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyPlanRecordMaterials emergencyPlanRecordMaterials = emergencyPlanRecordMaterialsService.getById(id);
		if(emergencyPlanRecordMaterials==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyPlanRecordMaterials);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlanRecordMaterials
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlanRecordMaterials emergencyPlanRecordMaterials) {
        return super.exportXls(request, emergencyPlanRecordMaterials, EmergencyPlanRecordMaterials.class, "emergency_plan_record_materials");
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
        return super.importExcel(request, response, EmergencyPlanRecordMaterials.class);
    }

}
