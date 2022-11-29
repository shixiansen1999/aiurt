package com.aiurt.boot.rehearsal.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.rehearsal.entity.EmergencyMaterialsInvoices;
import com.aiurt.boot.rehearsal.service.IEmergencyMaterialsInvoicesService;

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
 * @Description: emergency_materials_invoices
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_materials_invoices")
@RestController
@RequestMapping("/emergency/emergencyMaterialsInvoices")
@Slf4j
public class EmergencyMaterialsInvoicesController extends BaseController<EmergencyMaterialsInvoices, IEmergencyMaterialsInvoicesService> {
	@Autowired
	private IEmergencyMaterialsInvoicesService emergencyMaterialsInvoicesService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyMaterialsInvoices
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_materials_invoices-分页列表查询")
	@ApiOperation(value="emergency_materials_invoices-分页列表查询", notes="emergency_materials_invoices-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyMaterialsInvoices>> queryPageList(EmergencyMaterialsInvoices emergencyMaterialsInvoices,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyMaterialsInvoices> queryWrapper = QueryGenerator.initQueryWrapper(emergencyMaterialsInvoices, req.getParameterMap());
		Page<EmergencyMaterialsInvoices> page = new Page<EmergencyMaterialsInvoices>(pageNo, pageSize);
		IPage<EmergencyMaterialsInvoices> pageList = emergencyMaterialsInvoicesService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyMaterialsInvoices
	 * @return
	 */
	@AutoLog(value = "emergency_materials_invoices-添加")
	@ApiOperation(value="emergency_materials_invoices-添加", notes="emergency_materials_invoices-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyMaterialsInvoices emergencyMaterialsInvoices) {
		emergencyMaterialsInvoicesService.save(emergencyMaterialsInvoices);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyMaterialsInvoices
	 * @return
	 */
	@AutoLog(value = "emergency_materials_invoices-编辑")
	@ApiOperation(value="emergency_materials_invoices-编辑", notes="emergency_materials_invoices-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyMaterialsInvoices emergencyMaterialsInvoices) {
		emergencyMaterialsInvoicesService.updateById(emergencyMaterialsInvoices);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_materials_invoices-通过id删除")
	@ApiOperation(value="emergency_materials_invoices-通过id删除", notes="emergency_materials_invoices-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyMaterialsInvoicesService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_materials_invoices-批量删除")
	@ApiOperation(value="emergency_materials_invoices-批量删除", notes="emergency_materials_invoices-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyMaterialsInvoicesService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_materials_invoices-通过id查询")
	@ApiOperation(value="emergency_materials_invoices-通过id查询", notes="emergency_materials_invoices-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyMaterialsInvoices> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyMaterialsInvoices emergencyMaterialsInvoices = emergencyMaterialsInvoicesService.getById(id);
		if(emergencyMaterialsInvoices==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyMaterialsInvoices);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyMaterialsInvoices
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyMaterialsInvoices emergencyMaterialsInvoices) {
        return super.exportXls(request, emergencyMaterialsInvoices, EmergencyMaterialsInvoices.class, "emergency_materials_invoices");
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
        return super.importExcel(request, response, EmergencyMaterialsInvoices.class);
    }

}
