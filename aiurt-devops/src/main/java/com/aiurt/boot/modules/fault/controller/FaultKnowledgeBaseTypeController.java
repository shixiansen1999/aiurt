package com.aiurt.boot.modules.fault.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.fault.entity.FaultKnowledgeBaseType;
import com.aiurt.boot.modules.fault.service.IFaultKnowledgeBaseTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 故障知识库类型
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "故障知识库类型")
@RestController
@RequestMapping("/fault/faultKnowledgeBaseType")
public class FaultKnowledgeBaseTypeController {
	@Autowired
	private IFaultKnowledgeBaseTypeService faultKnowledgeBaseTypeService;

	/**
	 * 分页列表查询
	 *
	 * @param faultKnowledgeBaseType
	 * @param req
	 * @return
	 */
	@AutoLog(value = "故障知识库类型-分页列表查询")
	@ApiOperation(value = "故障知识库类型-分页列表查询", notes = "故障知识库类型-分页列表查询")
	@GetMapping(value = "/list")
	public Result<List<FaultKnowledgeBaseType>> queryPageList(FaultKnowledgeBaseType faultKnowledgeBaseType,
	                                                          HttpServletRequest req) {
		Result<List<FaultKnowledgeBaseType>> result = new Result<List<FaultKnowledgeBaseType>>();
		QueryWrapper<FaultKnowledgeBaseType> queryWrapper = QueryGenerator.initQueryWrapper(faultKnowledgeBaseType, req.getParameterMap());
		List<FaultKnowledgeBaseType> list = faultKnowledgeBaseTypeService.list(queryWrapper);
		result.setSuccess(true);
		result.setResult(list);
		return result;
	}

	/**
	 * 添加
	 *
	 * @param faultKnowledgeBaseType
	 * @return
	 */
	@AutoLog(value = "故障知识库类型-添加")
	@ApiOperation(value = "故障知识库类型-添加", notes = "故障知识库类型-添加")
	@PostMapping(value = "/add")
	public Result<FaultKnowledgeBaseType> add(@Valid @RequestBody FaultKnowledgeBaseType faultKnowledgeBaseType, HttpServletRequest req) {
		Result<FaultKnowledgeBaseType> result = new Result<FaultKnowledgeBaseType>();
		try {
			faultKnowledgeBaseTypeService.add(faultKnowledgeBaseType, req);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500(e.getMessage());
		}
		return result;
	}

	/**
	 * 编辑
	 *
	 * @param faultKnowledgeBaseType
	 * @return
	 */
	@AutoLog(value = "故障知识库类型-编辑")
	@ApiOperation(value = "故障知识库类型-编辑", notes = "故障知识库类型-编辑")
	@PutMapping(value = "/edit")
	public Result<FaultKnowledgeBaseType> edit(@RequestBody FaultKnowledgeBaseType faultKnowledgeBaseType) {
		Result<FaultKnowledgeBaseType> result = new Result<FaultKnowledgeBaseType>();
		FaultKnowledgeBaseType faultKnowledgeBaseTypeEntity = faultKnowledgeBaseTypeService.getById(faultKnowledgeBaseType.getId());
		if (faultKnowledgeBaseTypeEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			boolean ok = faultKnowledgeBaseTypeService.updateById(faultKnowledgeBaseType);

			if (ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}

	/**
	 * 通过id假删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障知识库类型-通过id删除")
	@ApiOperation(value = "故障知识库类型-通过id删除", notes = "故障知识库类型-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) Integer id) {
		try {

			faultKnowledgeBaseTypeService.removeById(id);
		} catch (Exception e) {
			log.error("删除失败", e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "故障知识库类型-批量删除")
	@ApiOperation(value = "故障知识库类型-批量删除", notes = "故障知识库类型-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<FaultKnowledgeBaseType> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<FaultKnowledgeBaseType> result = new Result<FaultKnowledgeBaseType>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.faultKnowledgeBaseTypeService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障知识库类型-通过id查询")
	@ApiOperation(value = "故障知识库类型-通过id查询", notes = "故障知识库类型-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultKnowledgeBaseType> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<FaultKnowledgeBaseType> result = new Result<FaultKnowledgeBaseType>();
		FaultKnowledgeBaseType faultKnowledgeBaseType = faultKnowledgeBaseTypeService.getById(id);
		if (faultKnowledgeBaseType == null) {
			result.onnull("未找到对应实体");
		} else {
			result.setResult(faultKnowledgeBaseType);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
		// Step.1 组装查询条件
		QueryWrapper<FaultKnowledgeBaseType> queryWrapper = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				FaultKnowledgeBaseType faultKnowledgeBaseType = JSON.parseObject(deString, FaultKnowledgeBaseType.class);
				queryWrapper = QueryGenerator.initQueryWrapper(faultKnowledgeBaseType, request.getParameterMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<FaultKnowledgeBaseType> pageList = faultKnowledgeBaseTypeService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "故障知识库类型列表");
		mv.addObject(NormalExcelConstants.CLASS, FaultKnowledgeBaseType.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("故障知识库类型列表数据", "导出人:Jeecg", "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
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
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<FaultKnowledgeBaseType> listFaultKnowledgeBaseTypes = ExcelImportUtil.importExcel(file.getInputStream(), FaultKnowledgeBaseType.class, params);
				faultKnowledgeBaseTypeService.saveBatch(listFaultKnowledgeBaseTypes);
				return Result.ok("文件导入成功！数据行数:" + listFaultKnowledgeBaseTypes.size());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("文件导入失败:" + e.getMessage());
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


}
