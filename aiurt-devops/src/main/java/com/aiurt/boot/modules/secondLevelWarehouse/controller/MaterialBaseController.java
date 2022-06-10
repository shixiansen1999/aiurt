package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.enums.MaterialTypeEnum;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.MaterialBase;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2Info;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.Warehouse;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.EnumTypeVO;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.MaterialBaseMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.service.IMaterialBaseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.service.IStockLevel2InfoService;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import sun.misc.BASE64Encoder;

/**
 * @Description: 物资基础信息
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags="物资基础信息")
@RestController
@RequestMapping("/secondLevelWarehouse/materialBase")
public class MaterialBaseController {
	@Resource
	private IMaterialBaseService materialBaseService;

	@Resource
	private MaterialBaseMapper materialBaseMapper;


	@Resource
	private IStockLevel2InfoService iStockLevel2InfoService;
	 @Value("${support.downFilePath.materialBasicInfoPath}")
	 private String materialBasicInfoPath;
	 /**
	  * 物资类型下拉列表-查询
	  * @return
	  */
	 @AutoLog(value = "物资类型下拉列表-查询")
	 @ApiOperation(value="物资类型下拉列表-查询", notes="物资类型下拉列表-查询")
	 @GetMapping(value = "/materialTypeList")
	 public Result<List<EnumTypeVO>> materialTypeList() {
		 Result<List<EnumTypeVO>> result = new Result<>();
		 List<EnumTypeVO> enumTypeVOS = new ArrayList<>();
		 for (MaterialTypeEnum value : MaterialTypeEnum.values()) {
			 EnumTypeVO enumTypeVO = new EnumTypeVO();
			 enumTypeVO.setCode(value.getCode());
			 enumTypeVO.setName(value.getName());
			 enumTypeVOS.add(enumTypeVO);
		 }
		 result.setSuccess(true);
		 result.setResult(enumTypeVOS);
		 return result;
	 }

	/**
	  * 分页列表查询
	 * @param materialBase
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "物资基础信息-分页列表查询")
	@ApiOperation(value="物资基础信息-分页列表查询", notes="物资基础信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<MaterialBase>> queryPageList(MaterialBase materialBase,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<MaterialBase>> result = new Result<>();
		QueryWrapper<MaterialBase> queryWrapper = QueryGenerator.initQueryWrapper(materialBase, req.getParameterMap());
		IPage<MaterialBase> page = new Page<>(pageNo,pageSize);
		IPage<MaterialBase> materialBases = materialBaseMapper.selectPage(page,queryWrapper);
		materialBases.getRecords().forEach(e->{
			if(e.getType()!=null){
				e.setTypeName(MaterialTypeEnum.getNameByCode(e.getType()));
			}
		});
		result.setSuccess(true);
		result.setResult(materialBases);
		return result;
	}

	@ApiOperation("根据物料编号查询物料类型")
	@GetMapping("/getTypeByMaterialCode")
	public Integer getTypeByMaterialCode(@RequestParam("code") String code){
		Integer type = materialBaseService.getTypeByMaterialCode(code);
		return type;
	}

	/**
	  *   添加
	 * @param materialBase
	 * @return
	 */
	@AutoLog(value = "物资基础信息-添加")
	@ApiOperation(value="物资基础信息-添加", notes="物资基础信息-添加")
	@PostMapping(value = "/add")
	public Result<MaterialBase> add(@RequestBody MaterialBase materialBase) {
		Result<MaterialBase> result = new Result<MaterialBase>();
		try {
			if(materialBase.getWarehouseCode()!=null){
				StockLevel2Info stockLevel2Info = iStockLevel2InfoService.
						getOne(new QueryWrapper<StockLevel2Info>()
								.eq("code", materialBase.getWarehouseCode()),false);
				if(ObjectUtil.isNotEmpty(stockLevel2Info)){
					materialBase.setWarehouseName(stockLevel2Info.getWarehouseCode());
				}
			}
			materialBaseService.save(materialBase);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}




	 /**
	  * 下载物资基础信息模板
	  *
	  * @param response
	  * @param request
	  * @throws IOException
	  */
	 @AutoLog(value = "下载物资基础信息模板")
	 @ApiOperation(value = "下载物资基础信息模板", notes = "下载物资基础信息模板")
	 @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
	 public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
		 //获取输入流，原始模板位置
		 String filePath = materialBasicInfoPath;
//		 ClassPathResource classPathResource = new ClassPathResource(filePath);
//		 File file = classPathResource.getFile();
		 InputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)));

		 response.setContentType("application/vnd.ms-excel");
		 // 获得请求头中的User-Agent
		 String agent = request.getHeader("USER-AGENT").toLowerCase();
		 // 根据不同的客户端进行不同的编码
		 String filename="物资基础信息模板";
		 if (agent.contains("MSIE")) {
			 // IE浏览器
			 filename = URLEncoder.encode(filename, "utf-8");
		 } else if (agent.contains("Firefox")) {
			 // 火狐浏览器
			 BASE64Encoder base64Encoder = new BASE64Encoder();
			 filename = "=?utf-8?B?" + base64Encoder.encode(filename.getBytes("utf-8")) + "?=";
		 } else {
			 // 其它浏览器
			 filename = URLEncoder.encode(filename, "utf-8");
		 }
		 response.setHeader("content-disposition", "attachment;filename=" + filename + ".xlsx");
		 BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		 int len = 0;
		 while ((len = bis.read()) != -1) {
			 out.write(len);
			 out.flush();
		 }
		 out.close();
	 }
	 /**
	  * 通过excel导入数据
	  *
	  * @param request
	  * @param response
	  * @return
	  */
	 @ApiOperation(value="通过excel导入数据", notes="")
	 @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	 public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 MultipartFile file = entity.getValue();// 获取上传文件对象
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<MaterialBase> listMaterialBases = ExcelImportUtil.importExcel(file.getInputStream(), MaterialBase.class, params);
				 materialBaseService.saveBatch(listMaterialBases);
				 return Result.ok("文件导入成功！数据行数:" + listMaterialBases.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(),e);
				 return Result.error("文件导入失败:"+e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.ok("文件导入失败！");
	 }
	/**
	  *  编辑
	 * @param materialBase
	 * @return
	 */
	@AutoLog(value = "物资基础信息-编辑")
	@ApiOperation(value="物资基础信息-编辑", notes="物资基础信息-编辑")
	@PutMapping(value = "/edit")
	public Result<MaterialBase> edit(@RequestBody MaterialBase materialBase) {
		Result<MaterialBase> result = new Result<MaterialBase>();
		String warehouseCode = materialBase.getWarehouseCode();
		if(warehouseCode!=null){
			StockLevel2Info stockLevel2Info = iStockLevel2InfoService.getOne(new QueryWrapper<StockLevel2Info>().eq("warehouse_code", warehouseCode), false);
			if(ObjectUtil.isNotEmpty(stockLevel2Info)){
				materialBase.setWarehouseName(stockLevel2Info.getWarehouseName());
			}
		}
		MaterialBase materialBaseEntity = materialBaseService.getById(materialBase.getId());
		if(materialBaseEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = materialBaseService.updateById(materialBase);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}

	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物资基础信息-通过id删除")
	@ApiOperation(value="物资基础信息-通过id删除", notes="物资基础信息-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			materialBaseService.removeById(id);
		} catch (Exception e) {
			log.error("删除失败",e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}
//
//	/**
//	  *  批量删除
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "物资基础信息-批量删除")
//	@ApiOperation(value="物资基础信息-批量删除", notes="物资基础信息-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<MaterialBase> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		Result<MaterialBase> result = new Result<MaterialBase>();
//		if(ids==null || "".equals(ids.trim())) {
//			result.error500("参数不识别！");
//		}else {
//			this.materialBaseService.removeByIds(Arrays.asList(ids.split(",")));
//			result.success("删除成功!");
//		}
//		return result;
//	}

//	/**
//	  * 通过id查询
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "物资基础信息-通过id查询")
//	@ApiOperation(value="物资基础信息-通过id查询", notes="物资基础信息-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<MaterialBase> queryById(@RequestParam(name="id",required=true) String id) {
//		Result<MaterialBase> result = new Result<MaterialBase>();
//		MaterialBase materialBase = materialBaseService.getById(id);
//		if(materialBase==null) {
//			result.error500("未找到对应实体");
//		}else {
//			result.setResult(materialBase);
//			result.setSuccess(true);
//		}
//		return result;
//	}

//  /**
//      * 导出excel
//   *
//   * @param request
//   * @param response
//   */
//  @RequestMapping(value = "/exportXls")
//  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
//      // Step.1 组装查询条件
//      QueryWrapper<MaterialBase> queryWrapper = null;
//      try {
//          String paramsStr = request.getParameter("paramsStr");
//          if (oConvertUtils.isNotEmpty(paramsStr)) {
//              String deString = URLDecoder.decode(paramsStr, "UTF-8");
//              MaterialBase materialBase = JSON.parseObject(deString, MaterialBase.class);
//              queryWrapper = QueryGenerator.initQueryWrapper(materialBase, request.getParameterMap());
//          }
//      } catch (UnsupportedEncodingException e) {
//          e.printStackTrace();
//      }
//
//      //Step.2 AutoPoi 导出Excel
//      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//      List<MaterialBase> pageList = materialBaseService.list(queryWrapper);
//      //导出文件名称
//      mv.addObject(NormalExcelConstants.FILE_NAME, "物资基础信息列表");
//      mv.addObject(NormalExcelConstants.CLASS, MaterialBase.class);
//      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("物资基础信息列表数据", "导出人:Jeecg", "导出信息"));
//      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
//      return mv;
//  }



}
