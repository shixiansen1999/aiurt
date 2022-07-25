package com.aiurt.modules.sparepart.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.entity.dto.StockApplyExcel;
import com.aiurt.modules.sparepart.service.ISparePartApplyService;
import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: spare_part_apply
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Api(tags="备件管理-备件申领")
@RestController
@RequestMapping("/sparepart/sparePartApply")
@Slf4j
public class SparePartApplyController extends BaseController<SparePartApply, ISparePartApplyService> {
	@Autowired
	private ISparePartApplyService sparePartApplyService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartApply
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "spare_part_apply-分页列表查询")
	@ApiOperation(value="spare_part_apply-分页列表查询", notes="spare_part_apply-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartApply>> queryPageList(SparePartApply sparePartApply,
													   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													   HttpServletRequest req) {
		QueryWrapper<SparePartApply> queryWrapper = QueryGenerator.initQueryWrapper(sparePartApply, req.getParameterMap());
		Page<SparePartApply> page = new Page<SparePartApply>(pageNo, pageSize);
		IPage<SparePartApply> pageList = sparePartApplyService.page(page, queryWrapper.lambda().eq(SparePartApply::getDelFlag, CommonConstant.DEL_FLAG_0));
		return Result.OK(pageList);
	}


	 /**
	  * 生成申领单号
	  * @param
	  * @return
	  */
	 @ApiOperation(value = "生成申领单号", notes = "生成申领单号")
	 @GetMapping(value = "/getCode")
	 public Result<String> getCode() {
		 return Result.ok(sparePartApplyService.getCode());
	 }

	 /**
	 *   添加
	 *
	 * @param sparePartApply
	 * @return
	 */
	@AutoLog(value = "spare_part_apply-添加")
	@ApiOperation(value="spare_part_apply-添加", notes="spare_part_apply-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SparePartApply sparePartApply) {
		return sparePartApplyService.add(sparePartApply);
	}

	/**
	 *  编辑
	 *
	 * @param sparePartApply
	 * @return
	 */
	@AutoLog(value = "spare_part_apply-编辑")
	@ApiOperation(value="spare_part_apply-编辑", notes="spare_part_apply-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SparePartApply sparePartApply) {
		return sparePartApplyService.update(sparePartApply);
	}

	 /**
	  *  提交
	  *
	  * @param sparePartApply
	  * @return
	  */
	 @AutoLog(value = "spare_part_apply-提交")
	 @ApiOperation(value="spare_part_apply-提交", notes="spare_part_apply-提交")
	 @RequestMapping(value = "/submit", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<?> submit(@RequestBody SparePartApply sparePartApply) {
		 return sparePartApplyService.submit(sparePartApply);
	 }

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "spare_part_apply-通过id删除")
	@ApiOperation(value="spare_part_apply-通过id删除", notes="spare_part_apply-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		SparePartApply sparePartApply = sparePartApplyService.getById(id);
		//判断备件管理，备件入库管理、备件库存信息是否使用  todo



		sparePartApply.setDelFlag(CommonConstant.DEL_FLAG_1);
		sparePartApplyService.updateById(sparePartApply);
		return Result.OK("删除成功!");
	}



	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "spare_part_apply-通过id查询")
	@ApiOperation(value="spare_part_apply-通过id查询", notes="spare_part_apply-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartApply> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartApply sparePartApply = sparePartApplyService.getById(id);
		if(sparePartApply==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartApply);
	}

	 /**
	  * 导出excel
	  *
	  * @param request
	  * @param response
	  */
	 @ApiOperation("导出excel")
	 @GetMapping(value = "/exportXls")
	 public ModelAndView exportXls(
			 @ApiParam(value = "行数据ids" ,required = true) @RequestParam("ids") List<Integer> ids,
			 HttpServletRequest request, HttpServletResponse response) {
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 List<StockApplyExcel> list = sparePartApplyService.exportXls(ids);
		 //导出文件名称
		 mv.addObject(NormalExcelConstants.FILE_NAME, "备件申领列表");
		 mv.addObject(NormalExcelConstants.CLASS, StockApplyExcel.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件申领列表数据", "导出人:Jeecg", "导出信息"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, list);
		 return mv;
	 }




 }
