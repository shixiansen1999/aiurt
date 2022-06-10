package com.aiurt.boot.modules.fault.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.result.FaultAnalysisReportResult;
import com.aiurt.boot.common.result.FaultRepairRecordResult;
import com.aiurt.boot.common.result.FaultResult;
import com.aiurt.boot.common.result.SpareResult;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.fault.dto.FaultAnalysisReportDTO;
import com.aiurt.boot.modules.fault.entity.FaultAnalysisReport;
import com.aiurt.boot.modules.fault.param.FaultAnalysisReportParam;
import com.aiurt.boot.modules.fault.service.IFaultAnalysisReportService;
import com.aiurt.boot.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.boot.modules.fault.service.IFaultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * @Description: 故障分析报告
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "故障分析报告")
@RestController
@RequestMapping("/fault/faultAnalysisReport")
public class FaultAnalysisReportController {
	@Autowired
	private IFaultAnalysisReportService faultAnalysisReportService;

	@Autowired
	private IFaultService faultService;

	@Autowired
	private IFaultRepairRecordService faultRepairRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param pageNo
	 * @param pageSize
	 * @param param
	 * @return
	 */
	@AutoLog(value = "故障分析报告-分页列表查询")
	@ApiOperation(value = "故障分析报告-分页列表查询", notes = "故障分析报告-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultAnalysisReportResult>> queryPageList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
	                                                              @Valid FaultAnalysisReportParam param) {
		Result<IPage<FaultAnalysisReportResult>> result = new Result<IPage<FaultAnalysisReportResult>>();
		Page<FaultAnalysisReportResult> page = new Page<FaultAnalysisReportResult>(pageNo, pageSize);
		IPage<FaultAnalysisReportResult> pageList = faultAnalysisReportService.pageList(page, param);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * 添加
	 *
	 * @param dto
	 * @return
	 */
	@AutoLog(value = "故障分析报告-添加")
	@ApiOperation(value = "故障分析报告-添加", notes = "故障分析报告-添加")
	@PostMapping(value = "/add")
	public Result<FaultAnalysisReportDTO> add(@Valid @RequestBody FaultAnalysisReportDTO dto, HttpServletRequest req) {
		Result<FaultAnalysisReportDTO> result = new Result<FaultAnalysisReportDTO>();
		try {
			faultAnalysisReportService.add(dto, req);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	 * 编辑
	 *
	 * @param faultAnalysisReport 此处id为必传
	 * @return
	 */
	@AutoLog(value = "故障分析报告-编辑")
	@ApiOperation(value = "故障分析报告-编辑", notes = "故障分析报告-编辑")
	@PostMapping(value = "/edit")
	public Result<FaultAnalysisReport> edit(@RequestBody FaultAnalysisReport faultAnalysisReport) {
		Result<FaultAnalysisReport> result = new Result<FaultAnalysisReport>();
		FaultAnalysisReport faultAnalysisReportEntity = faultAnalysisReportService.getById(faultAnalysisReport.getId());
		if (faultAnalysisReportEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			boolean ok = faultAnalysisReportService.updateById(faultAnalysisReport);
			if (ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障分析报告-通过id删除")
	@ApiOperation(value = "故障分析报告-通过id删除", notes = "故障分析报告-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) Integer id) {
		try {
			faultAnalysisReportService.removeById(id);
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
	@AutoLog(value = "故障分析报告-批量删除")
	@ApiOperation(value = "故障分析报告-批量删除", notes = "故障分析报告-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<FaultAnalysisReport> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<FaultAnalysisReport> result = new Result<FaultAnalysisReport>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.faultAnalysisReportService.removeByIds(Arrays.asList(ids.split(",")));
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
	@AutoLog(value = "故障分析报告-通过id查询")
	@ApiOperation(value = "故障分析报告-通过id查询", notes = "故障分析报告-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultAnalysisReport> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<FaultAnalysisReport> result = new Result<FaultAnalysisReport>();
		FaultAnalysisReport faultAnalysisReport = faultAnalysisReportService.getById(id);
		if (faultAnalysisReport == null) {
			result.onnull("未找到对应实体");
		} else {
			result.setResult(faultAnalysisReport);
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
		QueryWrapper<FaultAnalysisReport> queryWrapper = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				FaultAnalysisReport faultAnalysisReport = JSON.parseObject(deString, FaultAnalysisReport.class);
				queryWrapper = QueryGenerator.initQueryWrapper(faultAnalysisReport, request.getParameterMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<FaultAnalysisReport> pageList = faultAnalysisReportService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "故障分析报告列表");
		mv.addObject(NormalExcelConstants.CLASS, FaultAnalysisReport.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("故障分析报告列表数据", "导出人:Jeecg", "导出信息"));
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
				List<FaultAnalysisReport> listFaultAnalysisReports = ExcelImportUtil.importExcel(file.getInputStream(), FaultAnalysisReport.class, params);
				faultAnalysisReportService.saveBatch(listFaultAnalysisReports);
				return Result.ok("文件导入成功！数据行数:" + listFaultAnalysisReports.size());
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


	/**
	 * 根据故障编号查询故障基本信息
	 *
	 * @param code
	 * @return
	 */
	@AutoLog(value = "根据故障编号查询故障基本信息")
	@ApiOperation(value = "根据故障编号查询故障基本信息", notes = "根据故障编号查询故障基本信息")
	@GetMapping(value = "/getFaultDetail")
	public Result<?> getFaultDetail(@RequestParam(name = "code", required = true) String code) {
		try {
			FaultResult faultDetail = faultService.getFaultDetail(code);
			return Result.ok(faultDetail);
		} catch (Exception e) {
			return Result.error(0, "无数据");
		}
	}

	/**
	 * 根据故障编号查询维修记录
	 *
	 * @param code
	 * @return
	 */
	@AutoLog(value = "根据故障编号查询维修记录")
	@ApiOperation(value = "根据故障编号查询维修记录", notes = "根据故障编号查询维修记录")
	@GetMapping(value = "/getRepairRecord")
	public Result<List<FaultRepairRecordResult>> getRepairRecord(@RequestParam(name = "code", required = true) String code) {
		Result<List<FaultRepairRecordResult>> result = new Result<List<FaultRepairRecordResult>>();
		List<FaultRepairRecordResult> repairRecord = faultRepairRecordService.getRepairRecord(code);
		result.setResult(repairRecord);
		return result;
	}

	/**
	 * 根据故障编号查询故障分析报告
	 *
	 * @param code
	 * @return
	 */
	@AutoLog(value = "根据故障编号查询故障分析报告")
	@ApiOperation(value = "根据故障编号查询故障分析报告", notes = "根据故障编号查询故障分析报告")
	@GetMapping(value = "/getAnalysisReport")
	public Result<?> getAnalysisReport(@RequestParam(name = "code", required = true) String code) {
		FaultAnalysisReportResult report = faultAnalysisReportService.getAnalysisReport(code);
		return Result.ok(report);
	}

	/**
	 * 根据故障编号查询更换备件
	 *
	 * @param code
	 * @return
	 */
	@AutoLog(value = "根据故障编号查询更换备件")
	@ApiOperation(value = "根据故障编号查询更换备件", notes = "根据故障编号查询更换备件")
	@GetMapping(value = "/changeSpare")
	public Result<List<SpareResult>> changeSpare(@RequestParam(name = "code", required = true) String code) {
		Result<List<SpareResult>> result = new Result<List<SpareResult>>();
		List<SpareResult> spareResults = faultRepairRecordService.changeSpare(code);
		result.setResult(spareResults);
		return result;
	}


}
