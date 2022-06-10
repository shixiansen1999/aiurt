package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartApplyMaterial;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SpareApplyMaterialDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.service.ISparePartApplyMaterialService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 备件申领物资
 * @Author: qian
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags="备件申领物资")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartApplyMaterial")
public class SparePartApplyMaterialController {
	@Autowired
	private ISparePartApplyMaterialService sparePartApplyMaterialService;

	/**
	  * 备件申领物资详情
	 * @param id
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "备件申领物资详情-分页列表查询")
	@ApiOperation(value="备件申领物资详情-分页列表查询", notes="备件申领物资详情-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SpareApplyMaterialDTO>> queryPageList(
			@RequestParam("applyCode") String applyCode,
			@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<SpareApplyMaterialDTO>> result = new Result<IPage<SpareApplyMaterialDTO>>();
		IPage<SpareApplyMaterialDTO> page = new Page<>(pageNo, pageSize);
		IPage<SpareApplyMaterialDTO> pageList = sparePartApplyMaterialService.queryPageList(page, applyCode);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

//	/**
//	  *   添加
//	 * @param sparePartApplyMaterial
//	 * @return
//	 */
//	@AutoLog(value = "备件申领物资-添加")
//	@ApiOperation(value="备件申领物资-添加", notes="备件申领物资-添加")
//	@PostMapping(value = "/add")
//	public Result<SparePartApplyMaterial> add(@RequestBody SparePartApplyMaterial sparePartApplyMaterial) {
//		Result<SparePartApplyMaterial> result = new Result<SparePartApplyMaterial>();
//		try {
//			sparePartApplyMaterialService.save(sparePartApplyMaterial);
//			result.success("添加成功！");
//		} catch (Exception e) {
//			log.error(e.getMessage(),e);
//			result.error500("操作失败");
//		}
//		return result;
//	}
//
//	/**
//	  *  编辑
//	 * @param sparePartApplyMaterial
//	 * @return
//	 */
//	@AutoLog(value = "备件申领物资-编辑")
//	@ApiOperation(value="备件申领物资-编辑", notes="备件申领物资-编辑")
//	@PutMapping(value = "/edit")
//	public Result<SparePartApplyMaterial> edit(@RequestBody SparePartApplyMaterial sparePartApplyMaterial) {
//		Result<SparePartApplyMaterial> result = new Result<SparePartApplyMaterial>();
//		SparePartApplyMaterial sparePartApplyMaterialEntity = sparePartApplyMaterialService.getById(sparePartApplyMaterial.getId());
//		if(sparePartApplyMaterialEntity==null) {
//			result.error500("未找到对应实体");
//		}else {
//			boolean ok = sparePartApplyMaterialService.updateById(sparePartApplyMaterial);
//			//TODO 返回false说明什么？
//			if(ok) {
//				result.success("修改成功!");
//			}
//		}
//
//		return result;
//	}

//	/**
//	  *   通过id删除
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "备件申领物资-通过id删除")
//	@ApiOperation(value="备件申领物资-通过id删除", notes="备件申领物资-通过id删除")
//	@DeleteMapping(value = "/delete")
//	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
//		try {
//			sparePartApplyMaterialService.removeById(id);
//		} catch (Exception e) {
//			log.error("删除失败",e.getMessage());
//			return Result.error("删除失败!");
//		}
//		return Result.ok("删除成功!");
//	}

//	/**
//	  *  批量删除
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "备件申领物资-批量删除")
//	@ApiOperation(value="备件申领物资-批量删除", notes="备件申领物资-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<SparePartApplyMaterial> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		Result<SparePartApplyMaterial> result = new Result<SparePartApplyMaterial>();
//		if(ids==null || "".equals(ids.trim())) {
//			result.error500("参数不识别！");
//		}else {
//			this.sparePartApplyMaterialService.removeByIds(Arrays.asList(ids.split(",")));
//			result.success("删除成功!");
//		}
//		return result;
//	}
//
//	/**
//	  * 通过id查询
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "备件申领物资-通过id查询")
//	@ApiOperation(value="备件申领物资-通过id查询", notes="备件申领物资-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<SparePartApplyMaterial> queryById(@RequestParam(name="id",required=true) String id) {
//		Result<SparePartApplyMaterial> result = new Result<SparePartApplyMaterial>();
//		SparePartApplyMaterial sparePartApplyMaterial = sparePartApplyMaterialService.getById(id);
//		if(sparePartApplyMaterial==null) {
//			result.error500("未找到对应实体");
//		}else {
//			result.setResult(sparePartApplyMaterial);
//			result.setSuccess(true);
//		}
//		return result;
//	}
//
//  /**
//      * 导出excel
//   *
//   * @param request
//   * @param response
//   */
//  @RequestMapping(value = "/exportXls")
//  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
//      // Step.1 组装查询条件
//      QueryWrapper<SparePartApplyMaterial> queryWrapper = null;
//      try {
//          String paramsStr = request.getParameter("paramsStr");
//          if (oConvertUtils.isNotEmpty(paramsStr)) {
//              String deString = URLDecoder.decode(paramsStr, "UTF-8");
//              SparePartApplyMaterial sparePartApplyMaterial = JSON.parseObject(deString, SparePartApplyMaterial.class);
//              queryWrapper = QueryGenerator.initQueryWrapper(sparePartApplyMaterial, request.getParameterMap());
//          }
//      } catch (UnsupportedEncodingException e) {
//          e.printStackTrace();
//      }
//
//      //Step.2 AutoPoi 导出Excel
//      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//      List<SparePartApplyMaterial> pageList = sparePartApplyMaterialService.list(queryWrapper);
//      //导出文件名称
//      mv.addObject(NormalExcelConstants.FILE_NAME, "备件申领物资列表");
//      mv.addObject(NormalExcelConstants.CLASS, SparePartApplyMaterial.class);
//      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件申领物资列表数据", "导出人:Jeecg", "导出信息"));
//      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
//      return mv;
//  }
//
//  /**
//      * 通过excel导入数据
//   *
//   * @param request
//   * @param response
//   * @return
//   */
//  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
//      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
//          MultipartFile file = entity.getValue();// 获取上传文件对象
//          ImportParams params = new ImportParams();
//          params.setTitleRows(2);
//          params.setHeadRows(1);
//          params.setNeedSave(true);
//          try {
//              List<SparePartApplyMaterial> listSparePartApplyMaterials = ExcelImportUtil.importExcel(file.getInputStream(), SparePartApplyMaterial.class, params);
//              sparePartApplyMaterialService.saveBatch(listSparePartApplyMaterials);
//              return Result.ok("文件导入成功！数据行数:" + listSparePartApplyMaterials.size());
//          } catch (Exception e) {
//              log.error(e.getMessage(),e);
//              return Result.error("文件导入失败:"+e.getMessage());
//          } finally {
//              try {
//                  file.getInputStream().close();
//              } catch (IOException e) {
//                  e.printStackTrace();
//              }
//          }
//      }
//      return Result.ok("文件导入失败！");
//  }

}
