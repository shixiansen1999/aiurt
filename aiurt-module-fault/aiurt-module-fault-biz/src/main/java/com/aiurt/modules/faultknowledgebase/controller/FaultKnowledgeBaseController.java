package com.aiurt.modules.faultknowledgebase.controller;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.faultanalysisreport.constant.FaultConstant;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.service.IFaultAnalysisReportService;
import com.aiurt.modules.faultknowledgebase.dto.DeviceAssemblyDTO;
import com.aiurt.modules.faultknowledgebase.dto.DeviceTypeDTO;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
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
	 @Autowired
	 private IFaultAnalysisReportService faultAnalysisReportService;
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
		//list转string
		getFaultCodeList(faultKnowledgeBase);
		faultKnowledgeBase.setDelFlag(0);
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
		 return faultKnowledgeBaseService.approval(approvedRemark, approvedResult, id);
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
		getFaultCodeList(faultKnowledgeBase);
		faultKnowledgeBaseService.updateById(faultKnowledgeBase);
		return Result.OK("编辑成功!");
	}

	 //list转string
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
		 faultKnowledgeBase.setStatus(FaultConstant.PENDING);
		 faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
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
		if(faultKnowledgeBase==null) {
			return Result.error("未找到对应数据");
		}
		String faultCodes = faultKnowledgeBase.getFaultCodes();
		String[] split = faultCodes.split(",");
		List<String> list = Arrays.asList(split);
		faultKnowledgeBase.setFaultCodeList(list);
		return Result.OK(faultKnowledgeBase);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultKnowledgeBase
    */
	@ApiOperation(value="故障知识库-导出excel", notes="故障知识库-导出excel")
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
	@ApiOperation(value="故障知识库-通过excel导入数据", notes="故障知识库-通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
       // return super.importExcel(request, response, FaultKnowledgeBase.class);
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
				List<FaultKnowledgeBase> list = ExcelImportUtil.importExcel(file.getInputStream(), FaultKnowledgeBase.class, params);
				for (FaultKnowledgeBase faultKnowledgeBase : list) {
					String picture = faultKnowledgeBase.getPicture();
					log.info("图片1："+picture);
					if (StringUtils.isNotEmpty(picture)) {
						File file1 = new File(picture);
						FileInputStream inputStream = new FileInputStream(file1);
						byte[] buffer = new byte[inputStream.available()];
						if (inputStream.read(buffer) == -1) {
							inputStream.close();
						}
						StringBuilder imageBase64 = new StringBuilder(Base64.getEncoder().encodeToString(buffer));
						faultKnowledgeBase.setPicture(new String(imageBase64));
						log.info("图片2："+faultKnowledgeBase.getPicture());
					}
				}
				long start = System.currentTimeMillis();
				log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				return Result.ok("文件导入成功！数据" + list);
			} catch (Exception e) {
				String msg = e.getMessage();
				log.error(msg, e);
				if(msg!=null && msg.contains("Duplicate entry")){
					return Result.error("文件导入失败:有重复数据！");
				}else{
					return Result.error("文件导入失败:" + e.getMessage());
				}
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.error("文件导入失败！");
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
	 @AutoLog(value = "知识库的故障分页查询")
	 @ApiOperation(value="知识库的故障分页查询", notes="fault-分页列表查询")
	 @GetMapping(value = "/getFault")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = Fault.class)
	 })
	 public Result<IPage<FaultDTO>> getFault(FaultDTO faultDTO,
											 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											 HttpServletRequest req) {
		 Page<FaultDTO> page = new Page<>(pageNo, pageSize);
		 IPage<FaultDTO> pageList =faultKnowledgeBaseService.getFault(page, faultDTO);
		 return Result.OK(pageList);
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
	 public Result<List<DeviceTypeDTO>> getDeviceType(@RequestParam(name="majorCode",required = false) String majorCode,
													  @RequestParam(name="systemCode",required = false) String systemCode,
													  @RequestParam(name="name",required = false) String name) {
		 List<DeviceTypeDTO> deviceTypes = faultKnowledgeBaseMapper.getDeviceType(majorCode,systemCode,name);
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
	 public Result<List<DeviceAssemblyDTO>> getDeviceAssembly(@RequestParam(name="deviceTypeCode",required = false) String deviceTypeCode) {
		 List<DeviceAssemblyDTO> deviceAssembly = faultKnowledgeBaseMapper.getDeviceAssembly(deviceTypeCode);
		 return Result.OK(deviceAssembly);
	 }
 }
