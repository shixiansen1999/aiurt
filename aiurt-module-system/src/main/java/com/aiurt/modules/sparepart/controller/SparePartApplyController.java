package com.aiurt.modules.sparepart.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.entity.dto.StockApplyExcel;
import com.aiurt.modules.sparepart.service.*;
import com.aiurt.modules.stock.entity.StockOutboundMaterials;
import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.vo.LoginUser;
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
	 @Autowired
	 private ISparePartInOrderService sparePartInOrderService;
	 @Autowired
	 private ISparePartStockService sparePartStockService;
	 @Autowired
	 private ISparePartStockInfoService sparePartStockInfoService;
	 @Autowired
	 private ISparePartApplyMaterialService sparePartApplyMaterialService;


	/**
	 * 分页列表查询
	 *
	 * @param sparePartApply
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
    @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件分页列表查询",permissionUrl = "/sparepart/sparePartApply/list")
	@ApiOperation(value="spare_part_apply-分页列表查询", notes="spare_part_apply-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent = "sparePartsFor/SparePartApplyList")
	public Result<IPage<SparePartApply>> queryPageList(SparePartApply sparePartApply,
													   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													   HttpServletRequest req) {
		Page<SparePartApply> page = new Page<SparePartApply>(pageNo, pageSize);
		List<SparePartApply> list = sparePartApplyService.selectList(page, sparePartApply);
		List<SparePartApplyMaterial> applyMaterials = sparePartApplyMaterialService.selectList();
		list.forEach(apply ->{
			List<SparePartApplyMaterial> materials = applyMaterials.stream().filter(meterials -> meterials.getApplyId().equals(apply.getId()) ).collect(Collectors.toList());

			apply.setStockLevel2List(materials);
		});
		list = list.stream().distinct().collect(Collectors.toList());
		page.setRecords(list);
		return Result.OK(page);
	}
	 /**
	  * 备件申领-获取保管仓库查询条件
	  *
	  * @param sparePartApply
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件申领-获取保管仓库查询条件",permissionUrl = "/sparepart/sparePartApply/list")
	 @ApiOperation(value="备件申领-获取保管仓库查询条件", notes="备件申领-获取保管仓库查询条件")
	 @GetMapping(value = "/selectList")
	 @PermissionData(pageComponent = "sparePartsFor/SparePartApplyList")
	 public Result<?> selectList(SparePartApply sparePartApply,HttpServletRequest req) {
		 List<SparePartApply> list = sparePartApplyService.selectList(null, sparePartApply);
		 List<String> codeList = list.stream().map(SparePartApply::getWarehouseName).collect(Collectors.toList());
		 codeList = codeList.stream().distinct().collect(Collectors.toList());
		 codeList.remove(null);
		 return Result.OK(codeList);
	 }

	 /**
	  * 生成申领单号
	  * @param
	  * @return
	  */
     @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询申领单号",permissionUrl = "/sparepart/sparePartApply/list")
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
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加备件申领",permissionUrl = "/sparepart/sparePartApply/list")
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
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑备件申领",permissionUrl = "/sparepart/sparePartApply/list")
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
	 @AutoLog(value = "提交",operateType = 3,operateTypeAlias = "提交备件申领",permissionUrl = "/sparepart/sparePartApply/list")
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
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "通过id删除备件申领",permissionUrl = "/sparepart/sparePartApply/list")
	@ApiOperation(value="spare_part_apply-通过id删除", notes="spare_part_apply-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		SparePartApply sparePartApply = sparePartApplyService.getById(id);
		sparePartApply.setDelFlag(CommonConstant.DEL_FLAG_1);
		sparePartApplyService.updateById(sparePartApply);
		//删除申领物资
        LambdaQueryWrapper<SparePartApplyMaterial> materialWrapper = new LambdaQueryWrapper<>();
        materialWrapper.eq(SparePartApplyMaterial::getApplyId,id);
        sparePartApplyMaterialService.remove(materialWrapper);
		return Result.OK("删除成功!");
	}



	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
    @AutoLog(value = "spare_part_apply-查询",operateType = 1,operateTypeAlias = "通过id查询备件申领",permissionUrl = "/sparepart/sparePartApply/list")
	@ApiOperation(value="spare_part_apply-通过id查询", notes="spare_part_apply-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartApply> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartApply sparePartApply = sparePartApplyService.getById(id);

		List<SparePartApply> list = sparePartApplyService.selectListById(sparePartApply);
		List<SparePartApplyMaterial> applyMaterials = sparePartApplyMaterialService.selectList();
		list.forEach(apply ->{
			List<SparePartApplyMaterial> materials = applyMaterials.stream().filter(meterials -> meterials.getApplyId().equals(apply.getId()) ).collect(Collectors.toList());

			apply.setStockLevel2List(materials);
		});
		list = list.stream().filter(sparePartApply1 -> sparePartApply1.getId().equals(id)).distinct().collect(Collectors.toList());
		if(CollUtil.isNotEmpty(list)){
			for (SparePartApply partApply : list) {
				sparePartApply = partApply;
			}
		}
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
     @AutoLog(value = "导出",operateType = 6,operateTypeAlias = "导出备件申领",permissionUrl = "/sparepart/sparePartApply/list")
	 @ApiOperation("导出excel")
	 @GetMapping(value = "/exportXls")
	 public ModelAndView exportXls(@ApiParam(value = "行数据ids" ,required = true) @RequestParam("ids") String ids,HttpServletRequest request, HttpServletResponse response) {
		 LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 List<SparePartApply> list = sparePartApplyService.exportXls( Arrays.asList(ids.split(",")));
         list = list.stream().distinct().collect(Collectors.toList());
		 //导出文件名称
		 mv.addObject(NormalExcelConstants.FILE_NAME, "备件申领列表");
		 mv.addObject(NormalExcelConstants.CLASS, SparePartApply.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件申领列表数据", "导出人:"+user.getRealname(), "导出信息"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, list);
		 return mv;
	 }




 }
