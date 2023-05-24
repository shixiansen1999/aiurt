package com.aiurt.modules.faultknowledgebase.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.common.entity.DeviceTypeTable;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.faultanalysisreport.constants.FaultConstant;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.service.IFaultAnalysisReportService;
import com.aiurt.modules.faultcausesolution.dto.FaultCauseSolutionDTO;
import com.aiurt.modules.faultcausesolution.dto.FaultSparePartDTO;
import com.aiurt.modules.faultcausesolution.entity.FaultCauseSolution;
import com.aiurt.modules.faultcausesolution.service.IFaultCauseSolutionService;
import com.aiurt.modules.faultknowledgebase.dto.*;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	 @Autowired
	 private IFaultAnalysisReportService faultAnalysisReportService;
	 @Autowired
	 private ISysBaseAPI iSysBaseAPI;
	@Autowired
	private IFaultCauseSolutionService faultCauseSolutionService;

	/**
	 * 分页列表查询
	 *
	 * @param faultKnowledgeBase
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "故障知识库-故障知识库分页列表-查询", operateType =  1, operateTypeAlias = "查询", permissionUrl = "/fault/faultKnowledgeBaseList")
	@ApiOperation(value="故障知识库-分页列表查询", notes="故障知识库-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent = "fault/FaultKnowledgeBaseListChange")
	public Result<IPage<FaultKnowledgeBase>> queryPageList(FaultKnowledgeBase faultKnowledgeBase,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<FaultKnowledgeBase> page = new Page<FaultKnowledgeBase>(pageNo, pageSize);
		IPage<FaultKnowledgeBase> faultKnowledgeBasePage = faultKnowledgeBaseService.readAll(page, faultKnowledgeBase);
		return Result.OK(faultKnowledgeBasePage);
	}

	/**
	 * 添加
	 *
	 * @param faultKnowledgeBase
	 * @return
	 */
	@AutoLog(value = "故障知识库-故障知识库分页列表-添加", operateType = 2, operateTypeAlias = "添加", permissionUrl = "/fault/faultKnowledgeBaseList")
	@ApiOperation(value = "故障知识库-添加", notes = "故障知识库-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FaultKnowledgeBase faultKnowledgeBase) {
		//list转string
		getFaultCodeList(faultKnowledgeBase);
		faultKnowledgeBase.setStatus(FaultConstant.PENDING);
		faultKnowledgeBase.setDelFlag(0);
		if (org.apache.commons.lang.StringUtils.isEmpty(faultKnowledgeBase.getDeviceTypeCode())|| StringUtils.isEmpty(faultKnowledgeBase.getMaterialCode())) {
			Result<String> result = new Result<>();
			result.error500("设备或组件不能为空");
		}
		faultKnowledgeBaseMapper.insert(faultKnowledgeBase);
		String id = faultKnowledgeBase.getId();
		// 添加故障原因和解决方案
		List<FaultCauseSolutionDTO> faultCauseSolutions = faultKnowledgeBase.getFaultCauseSolutions();
		if (CollUtil.isNotEmpty(faultCauseSolutions)) {
			List<FaultCauseSolution> causeSolutions = new ArrayList<>();
			FaultCauseSolution causeSolution = null;
			for (FaultCauseSolutionDTO faultCauseSolution : faultCauseSolutions) {
				List<FaultSparePartDTO> spareParts = faultCauseSolution.getSpareParts();
				// 存在备件信息
				if(CollUtil.isNotEmpty(spareParts)){
					for (FaultSparePartDTO sparePart : spareParts) {
						causeSolution = new FaultCauseSolution();
						BeanUtils.copyProperties(faultCauseSolution, causeSolution);
						causeSolution.setKnowledgeBaseId(id);
						causeSolution.setSparePartCode(sparePart.getSparePartCode());
						causeSolution.setNumber(sparePart.getNumber());
						causeSolutions.add(causeSolution);
					}
					continue;
				}
				// 备件信息为空
				causeSolution = new FaultCauseSolution();
				BeanUtils.copyProperties(faultCauseSolution, causeSolution);
				causeSolution.setKnowledgeBaseId(id);
				causeSolutions.add(causeSolution);
			}
			faultCauseSolutionService.saveBatch(causeSolutions);
		}
		return Result.OK("添加成功！");
	}

	 /**
	  *  审批
	  *
	  * @param approvedRemark
	  * @param approvedResult
	  * @return
	  */
	 @AutoLog(value = "故障知识库-故障知识库分页列表-审批", operateType =  3, operateTypeAlias = "修改-审批", permissionUrl = "/fault/faultKnowledgeBaseList")
	 @ApiOperation(value="故障知识库-审批", notes="故障知识库-审批")
	 @RequestMapping(value = "/approval", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> approval(@RequestParam(name = "approvedRemark") String approvedRemark,
									@RequestParam(name = "approvedResult") Integer approvedResult,
									@RequestParam(name = "id") String id) {
		 return faultKnowledgeBaseService.approval(approvedRemark, approvedResult, id);
	 }
	/**
	 *  编辑
	 *
	 * @param faultKnowledgeBase
	 * @return
	 */
	@AutoLog(value = "故障知识库-故障知识库分页列表-编辑", operateType =  3, operateTypeAlias = "修改-编辑", permissionUrl = "/fault/faultKnowledgeBaseList")
	@ApiOperation(value="故障知识库-编辑", notes="故障知识库-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultKnowledgeBase faultKnowledgeBase) {
		getFaultCodeList(faultKnowledgeBase);
		faultKnowledgeBaseService.updateById(faultKnowledgeBase);
		String id = faultKnowledgeBase.getId();
		// 修改故障原因和解决方案
		List<FaultCauseSolutionDTO> faultCauseSolutions = faultKnowledgeBase.getFaultCauseSolutions();
		if (CollUtil.isNotEmpty(faultCauseSolutions)) {
			List<FaultCauseSolution> causeSolutions = new ArrayList<>();
			FaultCauseSolution causeSolution = null;
			for (FaultCauseSolutionDTO faultCauseSolution : faultCauseSolutions) {
				List<FaultSparePartDTO> spareParts = faultCauseSolution.getSpareParts();
				// 存在备件信息
				if(CollUtil.isNotEmpty(spareParts)){
					for (FaultSparePartDTO sparePart : spareParts) {
						causeSolution = new FaultCauseSolution();
						BeanUtils.copyProperties(faultCauseSolution, causeSolution);
						causeSolution.setKnowledgeBaseId(id);
						causeSolution.setSparePartCode(sparePart.getSparePartCode());
						causeSolution.setNumber(sparePart.getNumber());
						causeSolutions.add(causeSolution);
					}
					continue;
				}
				// 备件信息为空
				causeSolution = new FaultCauseSolution();
				BeanUtils.copyProperties(faultCauseSolution, causeSolution);
				causeSolution.setKnowledgeBaseId(id);
				causeSolutions.add(causeSolution);
			}
			faultCauseSolutionService.updateBatchById(causeSolutions);
		}
		return Result.OK("编辑成功!");
	}

	 /**list转string*/
	 private void getFaultCodeList(FaultKnowledgeBase faultKnowledgeBase) {
		 List<String> faultCodeList = faultKnowledgeBase.getFaultCodeList();
		 if (CollectionUtils.isNotEmpty(faultCodeList)) {
			 StringBuilder stringBuilder = new StringBuilder();
			 for (String faultCode : faultCodeList) {
				 stringBuilder.append(faultCode);
				 stringBuilder.append(",");
			 }
			 // 判断字符串长度是否有效
			 if (stringBuilder.length() > 0)
			 {
				 // 截取字符
				 stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			 }
			 faultKnowledgeBase.setFaultCodes(stringBuilder.toString());
		 }
		 faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
	 }
	 /**
	  *  已驳回-提交审核
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "已驳回-提交审核")
	 @ApiOperation(value = "已驳回-提交审核", notes = "已驳回-提交审核")
	 @PutMapping("/submitFaultKnowledgeResult")
	 public Result<?> submitResult(@RequestParam  String id) {
		 faultKnowledgeBaseService.update(new LambdaUpdateWrapper<FaultKnowledgeBase>().set(FaultKnowledgeBase::getStatus,FaultConstant.PENDING).eq(FaultKnowledgeBase::getId,id));
		 return Result.OK("操作成功");
	 }
	 /**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障知识库-故障知识库分页列表-通过id删除", operateType =  4, operateTypeAlias = "删除-通过id删除", permissionUrl = "/fault/faultKnowledgeBaseList")
	@ApiOperation(value="故障知识库-通过id删除", notes="故障知识库-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		return faultKnowledgeBaseService.delete(id);
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "故障知识库-故障知识库分页列表-批量删除", operateType =  4, operateTypeAlias = "删除-批量删除", permissionUrl = "/fault/faultKnowledgeBaseList")
	@ApiOperation(value="故障知识库-批量删除", notes="故障知识库-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		return faultKnowledgeBaseService.deleteBatch(Arrays.asList(ids.split(",")));
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障知识库-故障知识库分页列表-通过id查询", operateType =  1, operateTypeAlias = "查询-通过id查询", permissionUrl = "/fault/faultKnowledgeBaseList")
	@ApiOperation(value="故障知识库-通过id查询", notes="故障知识库-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultKnowledgeBase> queryById(@RequestParam(name="id",required=true) String id) {
		FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseService.readOne(id);
		return Result.OK(faultKnowledgeBase);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultKnowledgeBase
    */
	@AutoLog(value = "故障知识库-导出excel", operateType =  6, operateTypeAlias = "导出excel", permissionUrl = "/fault/faultKnowledgeBaseList")
	@ApiOperation(value="故障知识库-导出excel", notes="故障知识库-导出excel")
    @RequestMapping(value = "/exportXls",method = RequestMethod.GET)
    public void exportXls(HttpServletRequest request,
						  HttpServletResponse response,
						  FaultKnowledgeBase faultKnowledgeBase) {
		//查询导出的数据
		List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseService.queryAll(faultKnowledgeBase);

		List<FaultKnowledgeBaseDTO> faultKnowledgeBaseDTOList = new ArrayList<>();

		// 过滤选中数据
		String selections = request.getParameter("selections");
		if (oConvertUtils.isNotEmpty(selections)) {
			List<String> selectionList = Arrays.asList(selections.split(","));
			List<FaultKnowledgeBase> collect = faultKnowledgeBases.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
			if (CollUtil.isNotEmpty(collect)) {
				collect.forEach(e -> {
					FaultKnowledgeBaseDTO faultKnowledgeBaseDTO = new FaultKnowledgeBaseDTO();
					BeanUtil.copyProperties(e, faultKnowledgeBaseDTO);
					faultKnowledgeBaseDTOList.add(faultKnowledgeBaseDTO);
				});
			}
		} else {
			if (CollUtil.isNotEmpty(faultKnowledgeBases)){
				faultKnowledgeBases.forEach(e->{
					FaultKnowledgeBaseDTO faultKnowledgeBaseDTO = new FaultKnowledgeBaseDTO();
					BeanUtil.copyProperties(e,faultKnowledgeBaseDTO);
					faultKnowledgeBaseDTOList.add(faultKnowledgeBaseDTO);
				});

			}
		}

        if (CollUtil.isNotEmpty(faultKnowledgeBaseDTOList)){
			faultKnowledgeBaseDTOList.forEach(q->{
				if (StrUtil.isNotBlank(q.getKnowledgeBaseTypeCode())) {
					List<DictModel> dictModels = iSysBaseAPI.queryTableDictItemsByCode("fault_knowledge_base_type", "name", "code");
					DictModel dictModel = dictModels.stream().filter(t -> t.getValue().equals(q.getKnowledgeBaseTypeCode())).findFirst().orElse(null);
					if (ObjectUtil.isNotEmpty(dictModel)){
						assert dictModel != null;
						q.setKnowledgeBaseTypeName(dictModel.getText());
					}

				}
				if (StrUtil.isNotBlank(q.getDeviceTypeCode())){
					List<DictModel> dictModels = iSysBaseAPI.queryTableDictItemsByCode("device_Type", "name", "code");
					DictModel dictModel = dictModels.stream().filter(t -> t.getValue().equals(q.getDeviceTypeCode())).findFirst().orElse(null);
					if (ObjectUtil.isNotEmpty(dictModel)) {
						assert dictModel != null;
						q.setDeviceTypeName(dictModel.getText());
					}
				}
				if (StrUtil.isNotBlank(q.getMaterialCode())){
					List<DictModel> dictModels = iSysBaseAPI.queryTableDictItemsByCode("device_assembly", "material_name", "material_code");
					DictModel dictModel = dictModels.stream().filter(t -> t.getValue().equals(q.getMaterialCode())).findFirst().orElse(null);
					if (ObjectUtil.isNotEmpty(dictModel)) {
						assert dictModel != null;
						q.setMaterialName(dictModel.getText());
					}
				}

			});
		}


		String title = "故障知识库";
		cn.afterturn.easypoi.excel.entity.ExportParams exportParams=new ExportParams(title + "报表", "故障知识库导出模板", ExcelType.XSSF);
		//调用ExcelExportUtil.exportExcel方法生成workbook
		Workbook wb = ExcelExportUtil.exportExcel(exportParams, FaultKnowledgeBaseDTO.class,faultKnowledgeBaseDTOList);
		String fileName = "故障知识库";
		try {
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
			//xlsx格式设置
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
			wb.write(bufferedOutPut);
			bufferedOutPut.flush();
			bufferedOutPut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
	@AutoLog(value = "故障知识库-通过excel导入数据", operateType =  5, operateTypeAlias = "通过excel导入数据", permissionUrl = "/fault/faultKnowledgeBaseList")
	@ApiOperation(value="故障知识库-通过excel导入数据", notes="故障知识库-通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return faultKnowledgeBaseService.importExcel(request,response);
	}

	 /**
	  * 知识库的故障分页查询
	  *
	  * @param faultDTO
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "故障知识库-故障知识库分页列表-知识库的故障分页查询", operateType =  1, operateTypeAlias = "查询-知识库的故障分页查询", permissionUrl = "/fault/faultKnowledgeBaseList")
	 @ApiOperation(value="知识库的故障分页查询", notes="fault-分页列表查询")
	 @GetMapping(value = "/getFault")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = Fault.class)
	 })
	 @PermissionData(pageComponent = "fault/FaultKnowledgeBaseListChange")
	 public Result<IPage<FaultDTO>> getFault(FaultDTO faultDTO,
											 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											 HttpServletRequest req) {
		 Page<FaultDTO> page = new Page<>(pageNo, pageSize);
		 IPage<FaultDTO> pageList =faultKnowledgeBaseService.getFault(page, faultDTO);
		 return Result.OK(pageList);
	 }

	 /**
	  * pc设备分类查询
	  * @return
	  */
	 @AutoLog(value = "故障知识库-故障知识库分页列表-设备分类查询", operateType =  1, operateTypeAlias = "查询-设备分类查询", permissionUrl = "/fault/faultKnowledgeBaseList")
	 @ApiOperation(value="故障知识库-设备分类查询", notes="device_type-设备分类查询")
	 @GetMapping(value = "/getDeviceType")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = DeviceTypeTable.class)
	 })
	 public Result<List<DeviceTypeTable>> getDeviceType(@RequestParam(name="majorCode",required = false) String majorCode,
													  @RequestParam(name="systemCode",required = false) String systemCode,
														@RequestParam(name="deviceCode",required = false) String deviceCode,
														@RequestParam(name="name",required = false) String name) {
		 List<DeviceTypeTable> deviceTypes = iSysBaseAPI.selectList(majorCode, systemCode,deviceCode,name);
		 return Result.OK(deviceTypes);
	 }

	 /**
	  * 设备组件查询
	  * @return
	  */
	 @AutoLog(value = "故障知识库-故障知识库分页列表-设备组件查询", operateType =  1, operateTypeAlias = "查询-设备组件查询", permissionUrl = "/fault/faultKnowledgeBaseList")
	 @ApiOperation(value="故障知识库-设备组件查询", notes="device_assembly-设备组件查询")
	 @GetMapping(value = "/getDeviceAssembly")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = DeviceAssemblyDTO.class)
	 })
	 public Result<List<DeviceAssemblyDTO>> getDeviceAssembly(@RequestParam(name="deviceTypeCode",required = false) String deviceTypeCode,
															  @RequestParam(name="majorCode",required = false) String majorCode,
															  @RequestParam(name="systemCode",required = false) String systemCode) {
		 if (StrUtil.isNotEmpty(deviceTypeCode)) {
			 List<DeviceAssemblyDTO> deviceAssembly = faultKnowledgeBaseMapper.getDeviceAssembly(deviceTypeCode);
			 return Result.OK(deviceAssembly);
		 } else {
		 	//如果没有传入具体设备分类，则查该专业子系统下的所有设备分类的设备组件
			 if (StrUtil.isNotEmpty(majorCode)&&StrUtil.isNotEmpty(systemCode)) {
				 List<DeviceTypeTable> deviceTypes = iSysBaseAPI.selectList(majorCode, systemCode, null,null);
				 if (CollUtil.isNotEmpty(deviceTypes)) {
					 List<String> collect = deviceTypes.stream().map(DeviceTypeTable::getCode).collect(Collectors.toList());
					 List<DeviceAssemblyDTO> deviceAssembly = faultKnowledgeBaseMapper.getAllDeviceAssembly(collect);
					 return Result.OK(deviceAssembly);
				 } else {
					 return Result.OK(new ArrayList<>());
				 }
			 } else {
				 return Result.OK(new ArrayList<>());
			 }
		 }
	 }


	/**
	 * 导出excel
	 *
	 */
	@AutoLog(value = "故障知识库导入模板下载", operateType =  6, operateTypeAlias = "导出excel", permissionUrl = "/fault/faultKnowledgeBaseList")
	@ApiOperation(value="故障知识库导入模板下载", notes="故障知识库导入模板下载")
	@RequestMapping(value = "/exportTemplateXls")
	public void  exportTemplateXl(HttpServletResponse response, HttpServletRequest request) throws IOException {
		faultKnowledgeBaseService.exportTemplateXls(response);
	}


	/**
	 * 查找故障现象模板
	 * @param symptomReqDTO 请求参数
	 * @return
	 */
	@GetMapping("/querySymptomTemplate")
	@ApiOperation(value="查找故障现象模板", notes="查故障现象模板")
	public Result<IPage<SymptomResDTO>> querySymptomTemplate(SymptomReqDTO symptomReqDTO) {
		Page<SymptomResDTO> page = faultKnowledgeBaseService.querySymptomTemplate(symptomReqDTO);
		return Result.OK(page);
	}


	/**
	 *  维修建议
	 * @param knowledgeId 知识库id
	 * @return
	 */
	@GetMapping("/queryRepairSolRec")
	@ApiOperation(value="维修建议", notes="维修建议")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "knowledgeId", value = "知识库id", required = true, paramType = "query", dataType = "String")
	})
	public Result<RepairSolRecDTO> queryRepairSolRecDTO(@RequestParam(value = "knowledgeId", required = false) String knowledgeId) {
		RepairSolRecDTO repairSolRecDTO = faultKnowledgeBaseService.queryRepairSolRecDTO(knowledgeId);
		return Result.OK(repairSolRecDTO);
	}
 }
