package com.aiurt.modules.faultanalysisreport.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultanalysisreport.constant.FaultConstant;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultanalysisreport.service.IFaultAnalysisReportService;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.modules.faulttype.entity.FaultType;
import com.aiurt.modules.faulttype.mapper.FaultTypeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
	 @Resource
	 private ISysBaseAPI sysBaseAPI;
	/**
	 * 分页列表查询
	 *
	 * @param faultAnalysisReport
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "故障分析-故障分析列表-查询", operateType =  1, operateTypeAlias = "查询", permissionUrl = "/fault/faultAnalysisReportList")
	@ApiOperation(value="故障分析-分页列表查询", notes="故障分析-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent = "fault/FaultAnalysisReportListChange")
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
	@AutoLog(value = "故障分析-故障分析列表-添加", operateType =  2, operateTypeAlias = "添加", permissionUrl = "/fault/faultAnalysisReportList")
	@ApiOperation(value="故障分析-添加", notes="故障分析-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FaultAnalysisReport faultAnalysisReport) {
		faultAnalysisReport.setStatus(FaultConstant.PENDING);
		faultAnalysisReport.setApprovedResult(FaultConstant.NO_PASS);
		faultAnalysisReportService.save(faultAnalysisReport);
		return Result.OK("添加成功！");
	}

	 /**
	  *  审批
	  *
	  * @param approvedRemark
	  * @param approvedResult
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "故障分析-故障分析列表-审批", operateType =  3, operateTypeAlias = "修改-审批", permissionUrl = "/fault/faultAnalysisReportList")
	 @ApiOperation(value="故障分析-审批", notes="故障分析-审批")
	 @RequestMapping(value = "/approval", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> approval(@RequestParam(name = "approvedRemark") String approvedRemark,
									@RequestParam(name = "approvedResult") Integer approvedResult,
									@RequestParam(name = "id") String id) {
		 return faultAnalysisReportService.approval(approvedRemark, approvedResult, id);
	 }

	 /**
	 *  编辑提交
	 *
	 * @param faultDTO
	 * @return
	 */
	@AutoLog(value = "故障分析-故障分析列表-编辑提交", operateType =  3, operateTypeAlias = "修改-编辑提交", permissionUrl = "/fault/faultAnalysisReportList")
	@ApiOperation(value="故障分析-编辑提交", notes="故障分析-编辑提交")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = FaultDTO.class)
	})
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultDTO faultDTO) {
		return faultAnalysisReportService.edit(faultDTO);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障分析-故障分析列表-通过id删除", operateType =  4, operateTypeAlias = "删除-通过id删除", permissionUrl = "/fault/faultAnalysisReportList")
	@ApiOperation(value="故障分析-通过id删除", notes="故障分析-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		return faultAnalysisReportService.delete(id);
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "故障分析-故障分析列表-批量删除", operateType =  4, operateTypeAlias = "删除-批量删除", permissionUrl = "/fault/faultAnalysisReportList")
	@ApiOperation(value="故障分析-批量删除", notes="故障分析-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		return faultAnalysisReportService.deleteBatch(ids);
	}


    /**
    * 导出excel
    *
    * @param request
    * @param faultAnalysisReport
    */
	@AutoLog(value = "故障分析-故障分析列表-导出excel", operateType =  6, operateTypeAlias = "导出excel", permissionUrl = "/fault/faultAnalysisReportList")
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
	@AutoLog(value = "故障分析-故障分析列表-通过excel导入数据", operateType =  5, operateTypeAlias = "通过excel导入数据", permissionUrl = "/fault/faultAnalysisReportList")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, FaultAnalysisReport.class);
    }

	 /**
	  * 新增故障分析中的故障分页查询
	  *
	  * @param faultDTO
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "故障分析-故障分析列表-新增故障分析中的故障分页查询", operateType =  1, operateTypeAlias = "查询-新增故障分析中的故障分页查询", permissionUrl = "/fault/faultAnalysisReportList")
	 @ApiOperation(value="新增故障分析的故障分页查询", notes="fault-分页列表查询")
	 @GetMapping(value = "/getFault")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = Fault.class)
	 })
	 public Result<IPage<FaultDTO>> getFault(FaultDTO faultDTO,
											   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											   HttpServletRequest req) {
		 Page<FaultDTO> page = new Page<>(pageNo, pageSize);
		 IPage<FaultDTO> pageList = faultAnalysisReportService.getFault(page, faultDTO);
		 return Result.OK(pageList);
	 }

	 /**
	  * 提交中的故障分析的故障详情
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "故障分析-故障分析列表-故障分析的详情", operateType =  1, operateTypeAlias = "查询-故障分析的详情", permissionUrl = "/fault/faultAnalysisReportList")
	 @ApiOperation(value="故障分析的详情", notes="fault-故障分析的详情")
	 @GetMapping(value = "/getDetail")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = FaultDTO.class)
	 })
	 public Result<FaultDTO> getDetail(String id) {
		 FaultDTO detail = faultAnalysisReportService.getDetail(id);
		 return Result.OK(detail);
	 }

	/**
	 * 提交故障分析
	 * @param faultDTO
	 * @return
	 */
	@AutoLog(value = "故障分析-故障分析列表-提交故障分析", operateType =  2, operateTypeAlias = "查询-提交故障分析", permissionUrl = "/fault/faultAnalysisReportList")
	@ApiOperation(value="提交故障分析", notes="fault-提交故障分析")
	@PostMapping(value = "/addDetail")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = FaultDTO.class)
	})
	public Result<String> addDetail(@RequestBody FaultDTO faultDTO) {
		return faultAnalysisReportService.addDetail(faultDTO);
	}


	 /**
	  * 故障类别
	  * @return
	  */
	 @AutoLog(value = "故障分析-故障分析列表-故障类别", operateType =  1, operateTypeAlias = "查询-故障类别", permissionUrl = "/fault/faultAnalysisReportList")
	 @ApiOperation(value="故障分析-故障类别", notes="故障分析-故障类别")
	 @GetMapping(value = "/getFaultType")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = FaultType.class)
	 })
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
	  * @param faultCode
	  * @return
	  */
	 @AutoLog(value = "故障分析-故障分析列表-通过id查询详情", operateType =  1, operateTypeAlias = "查询-通过id查询详情", permissionUrl = "/fault/faultAnalysisReportList")
	 @ApiOperation(value="故障分析-通过id查询详情", notes="故障分析-通过id查询详情")
	 @GetMapping(value = "/readone")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = FaultAnalysisReport.class)
	 })
	 public Result<FaultAnalysisReport> readone(@RequestParam(name="id",required=false) String id,
												@RequestParam(name="faultCode",required=false) String faultCode) {
		 FaultAnalysisReport faultAnalysisReport = faultAnalysisReportService.readOne(id, faultCode);
		 if (faultAnalysisReport == null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(faultAnalysisReport);
	 }
}
