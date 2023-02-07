package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanMaterialsDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanMaterials;
import com.aiurt.common.constant.enums.ModuleType;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.service.IEmergencyPlanMaterialsService;

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
 * @Description: emergency_plan_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="应急预案应急物资关联")
@RestController
@RequestMapping("/emergency/emergencyPlanMaterials")
@Slf4j
public class EmergencyPlanMaterialsController extends BaseController<EmergencyPlanMaterials, IEmergencyPlanMaterialsService> {
	@Autowired
	private IEmergencyPlanMaterialsService emergencyPlanMaterialsService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanMaterials
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="emergency_plan_materials-分页列表查询", notes="emergency_plan_materials-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlanMaterials>> queryPageList(EmergencyPlanMaterials emergencyPlanMaterials,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlanMaterials> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanMaterials, req.getParameterMap());
		Page<EmergencyPlanMaterials> page = new Page<EmergencyPlanMaterials>(pageNo, pageSize);
		IPage<EmergencyPlanMaterials> pageList = emergencyPlanMaterialsService.page(page, queryWrapper);
		return Result.OK(pageList);
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
		 Page<EmergencyPlanMaterialsDTO> repairTaskPage = emergencyPlanMaterialsService.getMaterialAccountList(pageList, condition);
		 return Result.OK(repairTaskPage);
	 }


	/**
	 *   添加
	 *
	 * @param emergencyPlanMaterials
	 * @return
	 */
	@AutoLog(value = "emergency_plan_materials-添加")
	@ApiOperation(value="emergency_plan_materials-添加", notes="emergency_plan_materials-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyPlanMaterials emergencyPlanMaterials) {
		emergencyPlanMaterialsService.save(emergencyPlanMaterials);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyPlanMaterials
	 * @return
	 */
	@AutoLog(value = "emergency_plan_materials-编辑")
	@ApiOperation(value="emergency_plan_materials-编辑", notes="emergency_plan_materials-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyPlanMaterials emergencyPlanMaterials) {
		emergencyPlanMaterialsService.updateById(emergencyPlanMaterials);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_plan_materials-通过id删除")
	@ApiOperation(value="emergency_plan_materials-通过id删除", notes="emergency_plan_materials-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanMaterialsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_materials-批量删除")
	@ApiOperation(value="emergency_plan_materials-批量删除", notes="emergency_plan_materials-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanMaterialsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="应急预案物资-通过id查询", notes="应急预案物资-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<List<EmergencyPlanMaterialsDTO>> queryById(@RequestParam(name="id",required=true) String id) {
		List<EmergencyPlanMaterialsDTO> emergencyPlanMaterialsDtoS = emergencyPlanMaterialsService.queryById(id);
		if(CollUtil.isEmpty(emergencyPlanMaterialsDtoS)) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyPlanMaterialsDtoS);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlanMaterials
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlanMaterials emergencyPlanMaterials) {
        return super.exportXls(request, emergencyPlanMaterials, EmergencyPlanMaterials.class, "emergency_plan_materials");
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
        return super.importExcel(request, response, EmergencyPlanMaterials.class);
    }

}
