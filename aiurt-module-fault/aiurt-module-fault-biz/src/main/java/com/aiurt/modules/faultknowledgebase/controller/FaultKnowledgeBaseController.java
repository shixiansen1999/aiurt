package com.aiurt.modules.faultknowledgebase.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.modules.faultanalysisreport.constant.FaultConstant;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultknowledgebase.dto.DeviceAssemblyDTO;
import com.aiurt.modules.faultknowledgebase.dto.DeviceTypeDTO;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faulttype.entity.FaultType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Api(tags="故障知识库")
@RestController
@RequestMapping("/faultknowledgebase/faultKnowledgeBase")
@Slf4j
public class FaultKnowledgeBaseController extends BaseController<FaultKnowledgeBase, IFaultKnowledgeBaseService> {
	@Autowired
	private IFaultKnowledgeBaseService faultKnowledgeBaseService;
	 @Autowired
	 private FaultKnowledgeBaseMapper faultKnowledgeBaseMapper;
	/**
	 * 分页列表查询
	 *
	 * @param faultKnowledgeBase
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "故障知识库-分页列表查询")
	@ApiOperation(value="故障知识库-分页列表查询", notes="故障知识库-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultKnowledgeBase>> queryPageList(FaultKnowledgeBase faultKnowledgeBase,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<FaultKnowledgeBase> page = new Page<FaultKnowledgeBase>(pageNo, pageSize);
		IPage<FaultKnowledgeBase> faultKnowledgeBasePage = faultKnowledgeBaseService.readAll(page, faultKnowledgeBase);
		return Result.OK(faultKnowledgeBasePage);
	}

	/**
	 *   添加
	 *
	 * @param faultKnowledgeBase
	 * @return
	 */
	@AutoLog(value = "故障知识库-添加")
	@ApiOperation(value="故障知识库-添加", notes="故障知识库-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FaultKnowledgeBase faultKnowledgeBase) {
		faultKnowledgeBase.setStatus(FaultConstant.PENDING);
		faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
		faultKnowledgeBaseService.save(faultKnowledgeBase);
		return Result.OK("添加成功！");
	}

	 /**
	  *  审批
	  *
	  * @param approvedRemark
	  * @param approvedResult
	  * @return
	  */
	 @AutoLog(value = "故障知识库-审批")
	 @ApiOperation(value="故障知识库-审批", notes="故障知识库-审批")
	 @RequestMapping(value = "/approval", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> approval(@RequestParam(name = "approvedRemark") String approvedRemark,
									@RequestParam(name = "approvedResult") Integer approvedResult,
									@RequestParam(name = "id") String id) {
		 FaultKnowledgeBase faultKnowledgeBase = new FaultKnowledgeBase();
		 faultKnowledgeBase.setId(id);
		 faultKnowledgeBase.setApprovedRemark(approvedRemark);
		 faultKnowledgeBase.setApprovedResult(approvedResult);
		 faultKnowledgeBaseService.updateById(faultKnowledgeBase);
		 return Result.OK("审批成功!");
	 }
	/**
	 *  编辑
	 *
	 * @param faultKnowledgeBase
	 * @return
	 */
	@AutoLog(value = "故障知识库-编辑")
	@ApiOperation(value="故障知识库-编辑", notes="故障知识库-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultKnowledgeBase faultKnowledgeBase) {
		faultKnowledgeBase.setStatus(FaultConstant.PENDING);
		faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
		faultKnowledgeBaseService.updateById(faultKnowledgeBase);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障知识库-通过id删除")
	@ApiOperation(value="故障知识库-通过id删除", notes="故障知识库-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		faultKnowledgeBaseService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "故障知识库-批量删除")
	@ApiOperation(value="故障知识库-批量删除", notes="故障知识库-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultKnowledgeBaseService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "故障知识库-通过id查询")
	@ApiOperation(value="故障知识库-通过id查询", notes="故障知识库-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultKnowledgeBase> queryById(@RequestParam(name="id",required=true) String id) {
		FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseMapper.readOne(id);
		//FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseService.getById(id);
		if(faultKnowledgeBase==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultKnowledgeBase);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultKnowledgeBase
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FaultKnowledgeBase faultKnowledgeBase) {
        return super.exportXls(request, faultKnowledgeBase, FaultKnowledgeBase.class, "故障知识库");
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
        return super.importExcel(request, response, FaultKnowledgeBase.class);
    }

	 /**
	  * 设备分类查询
	  * @return
	  */
	 @ApiOperation(value="故障知识库-设备分类查询", notes="device_type-设备分类查询")
	 @GetMapping(value = "/getDeviceType")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = DeviceTypeDTO.class)
	 })
	 public Result<List<DeviceTypeDTO>> getDeviceType(@RequestParam(name="majorCode") String majorCode,
													  @RequestParam(name="systemCode") String systemCode) {
	 	List<DeviceTypeDTO> deviceTypes = faultKnowledgeBaseMapper.getDeviceType(majorCode,systemCode);
	 	return Result.OK(deviceTypes);
	 }

	 /**
	  * 设备组件查询
	  * @return
	  */
	 @ApiOperation(value="故障知识库-设备组件查询", notes="device_assembly-设备组件查询")
	 @GetMapping(value = "/getDeviceAssembly")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = DeviceAssemblyDTO.class)
	 })
	 public Result<List<DeviceAssemblyDTO>> getDeviceAssembly(@RequestParam(name="deviceTypeCode") String deviceTypeCode) {
		 List<DeviceAssemblyDTO> deviceAssembly = faultKnowledgeBaseMapper.getDeviceAssembly(deviceTypeCode);
		 return Result.OK(deviceAssembly);
	 }
 }
