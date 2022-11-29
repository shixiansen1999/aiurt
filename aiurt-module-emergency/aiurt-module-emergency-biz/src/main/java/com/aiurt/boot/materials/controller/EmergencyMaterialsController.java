package com.aiurt.boot.materials.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.materials.service.IEmergencyMaterialsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: emergency_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="物资信息")
@RestController
@RequestMapping("/emergency/emergencyMaterials")
@Slf4j
public class EmergencyMaterialsController extends BaseController<EmergencyMaterials, IEmergencyMaterialsService> {
	@Autowired
	private IEmergencyMaterialsService emergencyMaterialsService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyMaterials
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_materials-分页列表查询")
	@ApiOperation(value="物资信息-分页列表查询", notes="物资信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyMaterials>> queryPageList(EmergencyMaterials emergencyMaterials,
														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														   HttpServletRequest req) {
		QueryWrapper<EmergencyMaterials> queryWrapper = QueryGenerator.initQueryWrapper(emergencyMaterials, req.getParameterMap());
		Page<EmergencyMaterials> page = new Page<EmergencyMaterials>(pageNo, pageSize);
		IPage<EmergencyMaterials> pageList = emergencyMaterialsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyMaterials
	 * @return
	 */
	@AutoLog(value = "物资信息-添加")
	@ApiOperation(value="物资信息-添加", notes="物资信息-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyMaterials emergencyMaterials) {
		emergencyMaterialsService.save(emergencyMaterials);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyMaterials
	 * @return
	 */
	@AutoLog(value = "物资信息-编辑")
	@ApiOperation(value="物资信息-编辑", notes="物资信息-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyMaterials emergencyMaterials) {
		emergencyMaterialsService.updateById(emergencyMaterials);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物资信息-通过id删除")
	@ApiOperation(value="物资信息-通过id删除", notes="物资信息-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyMaterialsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "物资信息-批量删除")
	@ApiOperation(value="物资信息-批量删除", notes="物资信息-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyMaterialsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_materials-通过id查询")
	@ApiOperation(value="物资信息-通过id查询", notes="物资信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyMaterials> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyMaterials emergencyMaterials = emergencyMaterialsService.getById(id);
		if(emergencyMaterials==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyMaterials);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyMaterials
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyMaterials emergencyMaterials) {
        return super.exportXls(request, emergencyMaterials, EmergencyMaterials.class, "emergency_materials");
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
        return super.importExcel(request, response, EmergencyMaterials.class);
    }

}
