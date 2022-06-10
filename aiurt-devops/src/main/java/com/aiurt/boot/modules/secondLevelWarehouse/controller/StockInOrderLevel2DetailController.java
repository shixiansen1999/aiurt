package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.enums.MaterialTypeEnum;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.PageLimitUtil;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.MaterialBase;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockInOrderLevel2Detail;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.StockInDetailVO;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.MaterialBaseMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.StockInOrderLevel2DetailMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.service.IMaterialBaseService;
import com.swsc.copsms.modules.secondLevelWarehouse.service.IStockInOrderLevel2DetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 二级入库单详细信息
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="二级入库单详细信息")
@RestController
@RequestMapping("/secondLevelWarehouse/stockInOrderLevel2Detail")
public class StockInOrderLevel2DetailController {
	@Autowired
	private IStockInOrderLevel2DetailService stockInOrderLevel2DetailService;

	@Resource
	private StockInOrderLevel2DetailMapper stockInOrderLevel2DetailMapper;

	@Resource
	private IMaterialBaseService iMaterialBaseService;
	/**
	  * 分页列表查询
	 * @param orderCode
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "二级入库单详细信息-分页列表查询")
	@ApiOperation(value="二级入库单详细信息-分页列表查询", notes="二级入库单详细信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<PageLimitUtil<StockInDetailVO>> queryPageList(
										@RequestParam("orderCode") String orderCode,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Result<PageLimitUtil<StockInDetailVO>> result = new Result<>();
		List<StockInOrderLevel2Detail> detailList = stockInOrderLevel2DetailMapper
				.selectList(new QueryWrapper<StockInOrderLevel2Detail>()
				.eq("order_code", orderCode));
		List<StockInDetailVO> detailVOS = new ArrayList<>();
		if(CollUtil.isNotEmpty(detailList)){
			detailList.forEach(e->{
				StockInDetailVO stockInDetailVO = new StockInDetailVO();
				MaterialBase materialBase = iMaterialBaseService.getOne(new QueryWrapper<MaterialBase>()
						.eq(e.getMaterialCode() != null, "code", e.getMaterialCode()), false);
				BeanUtils.copyProperties(e,stockInDetailVO);
				stockInDetailVO.setMaterialName(materialBase.getName());
				stockInDetailVO.setType(materialBase.getType());
				stockInDetailVO.setSpecifications(materialBase.getSpecifications());
				stockInDetailVO.setCountryOrigin(materialBase.getCountryOrigin());
				stockInDetailVO.setManufacturer(materialBase.getManufacturer());
				stockInDetailVO.setBrand(materialBase.getBrand());
				stockInDetailVO.setUnit(materialBase.getUnit());
				stockInDetailVO.setTypeName(MaterialTypeEnum.getNameByCode(materialBase.getType()));
				detailVOS.add(stockInDetailVO);

			});
		}
		PageLimitUtil<StockInDetailVO> pageLimitUtil =
				new PageLimitUtil<>(pageNo, pageSize, true, detailVOS);
		result.setSuccess(true);
		result.setResult(pageLimitUtil);
		return result;
	}



//	/**
//	  *   添加
//	 * @param stockInOrderLevel2Detail
//	 * @return
//	 */
//	@AutoLog(value = "二级入库单详细信息-添加")
//	@ApiOperation(value="二级入库单详细信息-添加", notes="二级入库单详细信息-添加")
//	@PostMapping(value = "/add")
//	public Result<StockInOrderLevel2Detail> add(@RequestBody StockInOrderLevel2Detail stockInOrderLevel2Detail) {
//		Result<StockInOrderLevel2Detail> result = new Result<StockInOrderLevel2Detail>();
//		try {
//			stockInOrderLevel2DetailService.save(stockInOrderLevel2Detail);
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
//	 * @param stockInOrderLevel2Detail
//	 * @return
//	 */
//	@AutoLog(value = "二级入库单详细信息-编辑")
//	@ApiOperation(value="二级入库单详细信息-编辑", notes="二级入库单详细信息-编辑")
//	@PutMapping(value = "/edit")
//	public Result<StockInOrderLevel2Detail> edit(@RequestBody StockInOrderLevel2Detail stockInOrderLevel2Detail) {
//		Result<StockInOrderLevel2Detail> result = new Result<StockInOrderLevel2Detail>();
//		StockInOrderLevel2Detail stockInOrderLevel2DetailEntity = stockInOrderLevel2DetailService.getById(stockInOrderLevel2Detail.getId());
//		if(stockInOrderLevel2DetailEntity==null) {
//			result.error500("未找到对应实体");
//		}else {
//			boolean ok = stockInOrderLevel2DetailService.updateById(stockInOrderLevel2Detail);
//			//TODO 返回false说明什么？
//			if(ok) {
//				result.success("修改成功!");
//			}
//		}
//
//		return result;
//	}
//
//	/**
//	  *   通过id删除
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "二级入库单详细信息-通过id删除")
//	@ApiOperation(value="二级入库单详细信息-通过id删除", notes="二级入库单详细信息-通过id删除")
//	@DeleteMapping(value = "/delete")
//	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
//		try {
//			stockInOrderLevel2DetailService.removeById(id);
//		} catch (Exception e) {
//			log.error("删除失败",e.getMessage());
//			return Result.error("删除失败!");
//		}
//		return Result.ok("删除成功!");
//	}
//
//	/**
//	  *  批量删除
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "二级入库单详细信息-批量删除")
//	@ApiOperation(value="二级入库单详细信息-批量删除", notes="二级入库单详细信息-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<StockInOrderLevel2Detail> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		Result<StockInOrderLevel2Detail> result = new Result<StockInOrderLevel2Detail>();
//		if(ids==null || "".equals(ids.trim())) {
//			result.error500("参数不识别！");
//		}else {
//			this.stockInOrderLevel2DetailService.removeByIds(Arrays.asList(ids.split(",")));
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
//	@AutoLog(value = "二级入库单详细信息-通过id查询")
//	@ApiOperation(value="二级入库单详细信息-通过id查询", notes="二级入库单详细信息-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<StockInOrderLevel2Detail> queryById(@RequestParam(name="id",required=true) String id) {
//		Result<StockInOrderLevel2Detail> result = new Result<StockInOrderLevel2Detail>();
//		StockInOrderLevel2Detail stockInOrderLevel2Detail = stockInOrderLevel2DetailService.getById(id);
//		if(stockInOrderLevel2Detail==null) {
//			result.error500("未找到对应实体");
//		}else {
//			result.setResult(stockInOrderLevel2Detail);
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
//      QueryWrapper<StockInOrderLevel2Detail> queryWrapper = null;
//      try {
//          String paramsStr = request.getParameter("paramsStr");
//          if (oConvertUtils.isNotEmpty(paramsStr)) {
//              String deString = URLDecoder.decode(paramsStr, "UTF-8");
//              StockInOrderLevel2Detail stockInOrderLevel2Detail = JSON.parseObject(deString, StockInOrderLevel2Detail.class);
//              queryWrapper = QueryGenerator.initQueryWrapper(stockInOrderLevel2Detail, request.getParameterMap());
//          }
//      } catch (UnsupportedEncodingException e) {
//          e.printStackTrace();
//      }
//
//      //Step.2 AutoPoi 导出Excel
//      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//      List<StockInOrderLevel2Detail> pageList = stockInOrderLevel2DetailService.list(queryWrapper);
//      //导出文件名称
//      mv.addObject(NormalExcelConstants.FILE_NAME, "二级入库单详细信息列表");
//      mv.addObject(NormalExcelConstants.CLASS, StockInOrderLevel2Detail.class);
//      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("二级入库单详细信息列表数据", "导出人:Jeecg", "导出信息"));
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
//              List<StockInOrderLevel2Detail> listStockInOrderLevel2Details = ExcelImportUtil.importExcel(file.getInputStream(), StockInOrderLevel2Detail.class, params);
//              stockInOrderLevel2DetailService.saveBatch(listStockInOrderLevel2Details);
//              return Result.ok("文件导入成功！数据行数:" + listStockInOrderLevel2Details.size());
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
