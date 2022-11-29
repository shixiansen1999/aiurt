package com.aiurt.boot.rehearsal.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.rehearsal.entity.EmergencyMaterialsCategory;
import com.aiurt.boot.rehearsal.service.IEmergencyMaterialsCategoryService;

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
 * @Description: emergency_materials_category
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_materials_category")
@RestController
@RequestMapping("/emergency/emergencyMaterialsCategory")
@Slf4j
public class EmergencyMaterialsCategoryController extends BaseController<EmergencyMaterialsCategory, IEmergencyMaterialsCategoryService> {
	@Autowired
	private IEmergencyMaterialsCategoryService emergencyMaterialsCategoryService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyMaterialsCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_materials_category-分页列表查询")
	@ApiOperation(value="emergency_materials_category-分页列表查询", notes="emergency_materials_category-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyMaterialsCategory>> queryPageList(EmergencyMaterialsCategory emergencyMaterialsCategory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyMaterialsCategory> queryWrapper = QueryGenerator.initQueryWrapper(emergencyMaterialsCategory, req.getParameterMap());
		Page<EmergencyMaterialsCategory> page = new Page<EmergencyMaterialsCategory>(pageNo, pageSize);
		IPage<EmergencyMaterialsCategory> pageList = emergencyMaterialsCategoryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyMaterialsCategory
	 * @return
	 */
	@AutoLog(value = "emergency_materials_category-添加")
	@ApiOperation(value="emergency_materials_category-添加", notes="emergency_materials_category-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyMaterialsCategory emergencyMaterialsCategory) {
		emergencyMaterialsCategoryService.save(emergencyMaterialsCategory);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyMaterialsCategory
	 * @return
	 */
	@AutoLog(value = "emergency_materials_category-编辑")
	@ApiOperation(value="emergency_materials_category-编辑", notes="emergency_materials_category-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyMaterialsCategory emergencyMaterialsCategory) {
		emergencyMaterialsCategoryService.updateById(emergencyMaterialsCategory);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_materials_category-通过id删除")
	@ApiOperation(value="emergency_materials_category-通过id删除", notes="emergency_materials_category-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyMaterialsCategoryService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_materials_category-批量删除")
	@ApiOperation(value="emergency_materials_category-批量删除", notes="emergency_materials_category-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyMaterialsCategoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_materials_category-通过id查询")
	@ApiOperation(value="emergency_materials_category-通过id查询", notes="emergency_materials_category-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyMaterialsCategory> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyMaterialsCategory emergencyMaterialsCategory = emergencyMaterialsCategoryService.getById(id);
		if(emergencyMaterialsCategory==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyMaterialsCategory);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyMaterialsCategory
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyMaterialsCategory emergencyMaterialsCategory) {
        return super.exportXls(request, emergencyMaterialsCategory, EmergencyMaterialsCategory.class, "emergency_materials_category");
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
        return super.importExcel(request, response, EmergencyMaterialsCategory.class);
    }

}
