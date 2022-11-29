package com.aiurt.boot.materials.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.modules.materials.entity.EmergencyMaterialsUsage;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.materials.service.IEmergencyMaterialsUsageService;

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
 * @Description: emergency_materials_usage
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_materials_usage")
@RestController
@RequestMapping("/emergency/emergencyMaterialsUsage")
@Slf4j
public class EmergencyMaterialsUsageController extends BaseController<EmergencyMaterialsUsage, IEmergencyMaterialsUsageService> {
	@Autowired
	private IEmergencyMaterialsUsageService emergencyMaterialsUsageService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyMaterialsUsage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_materials_usage-分页列表查询")
	@ApiOperation(value="emergency_materials_usage-分页列表查询", notes="emergency_materials_usage-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyMaterialsUsage>> queryPageList(EmergencyMaterialsUsage emergencyMaterialsUsage,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyMaterialsUsage> queryWrapper = QueryGenerator.initQueryWrapper(emergencyMaterialsUsage, req.getParameterMap());
		Page<EmergencyMaterialsUsage> page = new Page<EmergencyMaterialsUsage>(pageNo, pageSize);
		IPage<EmergencyMaterialsUsage> pageList = emergencyMaterialsUsageService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyMaterialsUsage
	 * @return
	 */
	@AutoLog(value = "emergency_materials_usage-添加")
	@ApiOperation(value="emergency_materials_usage-添加", notes="emergency_materials_usage-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyMaterialsUsage emergencyMaterialsUsage) {
		emergencyMaterialsUsageService.save(emergencyMaterialsUsage);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyMaterialsUsage
	 * @return
	 */
	@AutoLog(value = "emergency_materials_usage-编辑")
	@ApiOperation(value="emergency_materials_usage-编辑", notes="emergency_materials_usage-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyMaterialsUsage emergencyMaterialsUsage) {
		emergencyMaterialsUsageService.updateById(emergencyMaterialsUsage);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_materials_usage-通过id删除")
	@ApiOperation(value="emergency_materials_usage-通过id删除", notes="emergency_materials_usage-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyMaterialsUsageService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_materials_usage-批量删除")
	@ApiOperation(value="emergency_materials_usage-批量删除", notes="emergency_materials_usage-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyMaterialsUsageService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_materials_usage-通过id查询")
	@ApiOperation(value="emergency_materials_usage-通过id查询", notes="emergency_materials_usage-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyMaterialsUsage> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyMaterialsUsage emergencyMaterialsUsage = emergencyMaterialsUsageService.getById(id);
		if(emergencyMaterialsUsage==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyMaterialsUsage);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyMaterialsUsage
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyMaterialsUsage emergencyMaterialsUsage) {
        return super.exportXls(request, emergencyMaterialsUsage, EmergencyMaterialsUsage.class, "emergency_materials_usage");
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
        return super.importExcel(request, response, EmergencyMaterialsUsage.class);
    }

}
