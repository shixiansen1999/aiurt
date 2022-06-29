package com.aiurt.modules.faultanalysisreport.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultanalysisreport.entity.dto.FaultDTO;
import com.aiurt.modules.faultknowledgebase.dto.DeviceTypeDTO;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.modules.faulttype.entity.FaultType;
import com.aiurt.modules.faulttype.mapper.FaultTypeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultanalysisreport.service.IFaultAnalysisReportService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * @Description: fault_analysis_report
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Api(tags="故障分析")
@RestController
@RequestMapping("/faultanalysisreport/faultAnalysisReport")
@Slf4j
public class FaultAnalysisReportController extends BaseController<FaultAnalysisReport, IFaultAnalysisReportService> {
	@Autowired
	private IFaultAnalysisReportService faultAnalysisReportService;
	 @Autowired
	 private IFaultKnowledgeBaseService faultKnowledgeBaseService;
	 @Autowired
	 private IFaultService faultService;
	 @Autowired
	 private FaultTypeMapper faultTypeMapper;
	 @Autowired
	 private FaultKnowledgeBaseTypeMapper faultKnowledgeBaseTypeMapper;
	/**
	 * 分页列表查询
	 *
	 * @param faultAnalysisReport
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "fault_analysis_report-分页列表查询")
	@ApiOperation(value="故障分析-分页列表查询", notes="故障分析-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultAnalysisReport>> queryPageList(FaultAnalysisReport faultAnalysisReport,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<FaultAnalysisReport> page = new Page<FaultAnalysisReport>(pageNo, pageSize);
		IPage<FaultAnalysisReport> faultAnalysisReports = faultAnalysisReportService.readAll(page, faultAnalysisReport);
		return Result.OK(faultAnalysisReports);
	}

	/**
	 *   添加
	 *
	 * @param faultAnalysisReport
	 * @return
	 */
	@AutoLog(value = "故障分析-添加")
	@ApiOperation(value="fault_analysis_report-添加", notes="fault_analysis_report-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FaultAnalysisReport faultAnalysisReport) {
		faultAnalysisReportService.save(faultAnalysisReport);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param faultAnalysisReport
	 * @return
	 */
	@AutoLog(value = "故障分析-编辑")
	@ApiOperation(value="fault_analysis_report-编辑", notes="fault_analysis_report-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultAnalysisReport faultAnalysisReport) {
		faultAnalysisReportService.updateById(faultAnalysisReport);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障分析-通过id删除")
	@ApiOperation(value="故障分析-通过id删除", notes="故障分析-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		faultAnalysisReportService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "故障分析-批量删除")
	@ApiOperation(value="故障分析-批量删除", notes="故障分析-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultAnalysisReportService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "fault_analysis_report-通过id查询")
	@ApiOperation(value="故障分析-通过id查询", notes="故障分析-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultAnalysisReport> queryById(@RequestParam(name="id",required=true) String id) {

		FaultAnalysisReport faultAnalysisReport = faultAnalysisReportService.getById(id);
		if(faultAnalysisReport==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultAnalysisReport);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultAnalysisReport
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FaultAnalysisReport faultAnalysisReport) {
        return super.exportXls(request, faultAnalysisReport, FaultAnalysisReport.class, "fault_analysis_report");
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
        return super.importExcel(request, response, FaultAnalysisReport.class);
    }

	 /**
	  * 新增故障分析的故障分页查询
	  *
	  * @param fault
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "新增故障分析的故障分页查询")
	 @ApiOperation(value="新增故障分析的故障分页查询", notes="fault-分页列表查询")
	 @GetMapping(value = "/getFault")
	 public Result<IPage<Fault>> getFault(Fault fault,
											   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											   HttpServletRequest req) {
		 Page<Fault> page = new Page<>(pageNo, pageSize);
		 IPage<Fault> pageList = faultAnalysisReportService.getFault(page, fault);
		 return Result.OK(pageList);
	 }

	 /**
	  * 提交中的故障分析的故障详情
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "提交中的故障分析的故障详情")
	 @ApiOperation(value="提交中的故障分析的故障详情", notes="fault-提交中的故障分析的故障详情")
	 @GetMapping(value = "/getDetail")
	 public Result<FaultDTO> getDetail(String id) {
		 FaultDTO detail = faultAnalysisReportService.getDetail(id);
		 return Result.OK(detail);
	 }

	/**
	 * 提交故障分析
	 * @param faultDTO
	 * @return
	 */
	@AutoLog(value = "提交故障分析")
	@ApiOperation(value="提交故障分析", notes="fault-提交故障分析")
	@PostMapping(value = "/addDetail")
	public Result<String> addDetail(@RequestBody FaultDTO faultDTO) {
		FaultAnalysisReport faultAnalysisReport = faultDTO.getFaultAnalysisReport();
		FaultKnowledgeBase faultKnowledgeBase = faultDTO.getFaultKnowledgeBase();
		faultAnalysisReportService.save(faultAnalysisReport);
		if (ObjectUtil.isNotNull(faultKnowledgeBase)) {
			faultKnowledgeBaseService.save(faultKnowledgeBase);
		}
		return Result.OK("提交成功");
	}


	 /**
	  * 故障类别
	  * @return
	  */
	 @ApiOperation(value="故障分析-故障类别", notes="故障分析-故障类别")
	 @GetMapping(value = "/getFaultType")
	 public Result<List<FaultType>> getFaultType() {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 //用户拥有的专业
		 List<String> majorByUser = faultKnowledgeBaseTypeMapper.getMajorByUser(sysUser.getId());
		 LambdaQueryWrapper<FaultType> faultTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
		 List<FaultType> faultTypes = faultTypeMapper.selectList(faultTypeLambdaQueryWrapper
				 .eq(FaultType::getDelFlag, 0)
				 .in(FaultType::getMajorCode,majorByUser)
				 .select(FaultType::getId, FaultType::getCode, FaultType::getName));
		 return Result.OK(faultTypes);
	 }

	 /**
	  * 通过id查询详情
	  * @param id
	  * @return
	  */
	 @ApiOperation(value="故障分析-通过id查询详情", notes="故障分析-通过id查询详情")
	 @GetMapping(value = "/readone")
	 public Result<FaultDTO> readone(@RequestParam(name="id",required=true) String id) {
		 FaultDTO faultDTO = faultAnalysisReportService.readOne(id);
		 if(faultDTO==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(faultDTO);
	 }
}
