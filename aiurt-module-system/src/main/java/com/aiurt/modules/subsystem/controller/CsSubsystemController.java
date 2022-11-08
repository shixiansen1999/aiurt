package com.aiurt.modules.subsystem.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.ImportExcelUtil;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.subsystem.dto.CsSubsystemDTO;
import com.aiurt.modules.subsystem.dto.SubsystemFaultDTO;
import com.aiurt.modules.subsystem.dto.SystemByCodeDTO;
import com.aiurt.modules.subsystem.dto.YearFaultDTO;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.entity.CsSubsystemUser;
import com.aiurt.modules.subsystem.mapper.CsSubsystemUserMapper;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Description: cs_subsystem
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="系统管理-基础数据-子系统")
@RestController
@RequestMapping("/subsystem")
@Slf4j
public class CsSubsystemController  {
	@Autowired
	private ICsSubsystemService csSubsystemService;
	@Autowired
	private CsSubsystemUserMapper csSubsystemUserMapper;
	@Autowired
	private ICsMajorService csMajorService;
	@Autowired
	private ISysBaseAPI sysBaseApi;
	@Autowired
	private IMaterialBaseTypeService materialBaseTypeService;
	 @Autowired
	 private IDeviceTypeService deviceTypeService;
	 /**
	  * 专业子系统树
	  *
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询专业子系统树",permissionUrl = "/subsystem/list")
	 @ApiOperation(value="专业子系统树", notes="专业子系统树")
	 @GetMapping(value = "/treeList")
	 @PermissionData(pageComponent = "manage/SubsystemList")
	 public Result<?> queryTreeList() {
		 List<CsMajor> majorList = csMajorService.list(new LambdaQueryWrapper<CsMajor>().eq(CsMajor::getDelFlag,0));
		 List<CsSubsystem> systemList = csSubsystemService.list(new LambdaQueryWrapper<CsSubsystem>().eq(CsSubsystem::getDelFlag,0));
		 majorList.forEach(major -> {
			 List sysList = systemList.stream().filter(system-> system.getMajorCode().equals(major.getMajorCode())).collect(Collectors.toList());
			 major.setChildren(sysList);
		 });
		 return Result.OK(majorList);
	 }
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "子系统列表查询",permissionUrl = "/subsystem/list")
	 @ApiOperation(value="子系统列表查询", notes="子系统列表查询")
	 @GetMapping(value = "/selectList")
	 @PermissionData(pageComponent = "manage/MajorList")
	 public Result<?> selectlist(
									@RequestParam(name="majorCode", required = false) String majorCode,
									@RequestParam(name="systemName", required = false) String systemName,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
	 	 LambdaQueryWrapper<CsSubsystem> queryWrapper = new LambdaQueryWrapper<>();
		 if( majorCode != null && !"".equals(majorCode) ){
			 queryWrapper.eq(CsSubsystem::getMajorCode,majorCode);
		 }
		 if( systemName != null && !"".equals(systemName) ){
			 queryWrapper.like(CsSubsystem::getSystemName,systemName);
		 }
		 queryWrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);

		 queryWrapper.orderByDesc(CsSubsystem::getCreateTime);
		 List<CsSubsystem> pageList = csSubsystemService.list(queryWrapper);
		 Page<CsSubsystem> page = new Page<CsSubsystem>(pageNo, pageSize);
		 if(pageList.isEmpty()){
			 LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper<>();
			 if( majorCode != null && !"".equals(majorCode) ){
				 wrapper.eq(CsSubsystem::getSystemCode,majorCode);
			 }
			 wrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
			 wrapper.orderByDesc(CsSubsystem::getCreateTime);
			 pageList = csSubsystemService.list(wrapper);
		 }
		 pageList.forEach(system->{
			 LambdaQueryWrapper<CsSubsystemUser> userQueryWrapper = new LambdaQueryWrapper<>();
			 userQueryWrapper.eq(CsSubsystemUser::getSubsystemId,system.getId());
			 List<CsSubsystemUser> userList = csSubsystemUserMapper.selectList(userQueryWrapper);
			 String realNames = "";
			 String userNames = "";
			 if(!userList.isEmpty()){
				 for(CsSubsystemUser systemUser:userList){
					 userNames += systemUser.getUsername() + ",";
					 LoginUser user = sysBaseApi.getUserById(systemUser.getUserId()+"");
					 if(null!=user){
						 realNames += user.getRealname() + ",";
					 }
				 }
			 }
			 if(!("").equals(realNames)){
				 system.setSystemUserName(realNames.substring(0,realNames.length()-1));
			 }
			 if(!("").equals(userNames)){
				 system.setSystemUserList(userNames.substring(0,userNames.length()-1));
			 }
		 });
		 page.setRecords(pageList);
		 return Result.OK(page);
	 }

	/**
	 *   添加
	 *
	 * @param csSubsystem
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加子系统",permissionUrl = "/subsystem/list")
	@ApiOperation(value="子系统添加", notes="子系统添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsSubsystem csSubsystem) {
		return csSubsystemService.add(csSubsystem);
	}

	/**
	 *  编辑
	 *
	 * @param csSubsystem
	 * @return
	 */
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑子系统",permissionUrl = "/subsystem/list")
	@ApiOperation(value="子系统编辑", notes="子系统编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CsSubsystem csSubsystem) {
		return csSubsystemService.update(csSubsystem);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "子系统通过id删除",permissionUrl = "/subsystem/list")
	@ApiOperation(value="子系统通过id删除", notes="子系统通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		CsSubsystem csSubsystem = csSubsystemService.getById(id);
		//判断是否被设备类型使用
		LambdaQueryWrapper<DeviceType> deviceWrapper = new LambdaQueryWrapper<>();
		deviceWrapper.eq(DeviceType::getSystemCode,csSubsystem.getSystemCode());
		deviceWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<DeviceType> deviceList = deviceTypeService.list(deviceWrapper);
		if(!deviceList.isEmpty()){
			return Result.error("该子系统被设备类型使用中，不能删除!");
		}
		//判断是否被物资分类使用
		LambdaQueryWrapper<MaterialBaseType> materWrapper = new LambdaQueryWrapper<>();
		materWrapper.eq(MaterialBaseType::getSystemCode,csSubsystem.getSystemCode());
		materWrapper.eq(MaterialBaseType::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<MaterialBaseType> materList = materialBaseTypeService.list(materWrapper);
		if(!materList.isEmpty()){
			return Result.error("该子系统被物资分类使用中，不能删除!");
		}
		csSubsystem.setDelFlag(CommonConstant.DEL_FLAG_1);
		csSubsystemService.updateById(csSubsystem);
		//关联删除
		QueryWrapper<CsSubsystemUser> userQueryWrapper = new QueryWrapper<CsSubsystemUser>();
		userQueryWrapper.eq("subsystem_id", id);
		csSubsystemUserMapper.delete(userQueryWrapper);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "子系统通过id查询",permissionUrl = "/subsystem/list")
	@ApiOperation(value="子系统通过id查询", notes="子系统通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CsSubsystem csSubsystem = csSubsystemService.getById(id);
		if(csSubsystem==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csSubsystem);
	}

	 /**
	  * 根据专业查子系统
	  * @param majorIds
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "根据专业查子系统",permissionUrl = "/subsystem/list")
	 @ApiOperation(value="根据专业查子系统", notes="根据专业查子系统")
	 @GetMapping(value = "/getList")
	 public Result<?> getList(@RequestParam(name="majorIds",required=true) List<String> majorIds) {
		 List<CsMajor> majorList = csMajorService.list(new LambdaQueryWrapper<CsMajor>()
				 .eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0)
				 .in(CsMajor::getMajorCode,majorIds)
				 .select(CsMajor::getMajorCode,CsMajor::getMajorName));
		 majorList.forEach(major -> {
			 List<CsSubsystem> systemList = csSubsystemService.list(new LambdaQueryWrapper<CsSubsystem>()
					 .eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0)
					 .eq(CsSubsystem::getMajorCode,major.getMajorCode())
					 .select(CsSubsystem::getId,CsSubsystem::getSystemName));
			 major.setChildren(systemList);
		 });
		 return Result.OK(majorList);
	 }


	 /**
	  * 根据专业查子系统
	  * @param majorIds
	  * @return
	  */
	 @ApiOperation(value="根据专业id查子系统", notes="根据专业id查子系统")
	 @GetMapping(value = "/queryCsSubsystemBy")
	 public Result<?> queryCsSubsystemBy(@RequestParam(name="majorIds",required=false) String majorIds) {

	 	 if (StrUtil.isBlank(majorIds)) {
	 		return Result.OK(Collections.emptyList());
		 }
		 List<CsMajor> majorList = csMajorService.list(new LambdaQueryWrapper<CsMajor>()
				 .eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0)
				 .in(CsMajor::getId,StrUtil.split(majorIds, ',')));

		 Set<String> set = majorList.stream().map(CsMajor::getMajorCode).collect(Collectors.toSet());

		 if (CollUtil.isEmpty(set)) {
		 	return Result.OK(Collections.emptyList());
		 }

		 List<CsSubsystem> systemList = csSubsystemService.list(new LambdaQueryWrapper<CsSubsystem>()
				 .eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0)
				 .in(CsSubsystem::getMajorCode,set));
		 systemList.forEach(s->{s.setKey(s.getId());s.setLabel(s.getSystemName());s.setValue(s.getId());});
		 return Result.OK(systemList);
	 }



	 /**
	  * 统计报表-子系统分析
	  * @param
	  * @return
	  */
	 @ApiOperation(value="统计报表-子系统分析", notes="统计报表-子系统分析")
	 @GetMapping(value = "/csSubsystemFault")
	 public Result<Page<SubsystemFaultDTO>> queryCsSubsystemFault(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
																   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
																   SubsystemFaultDTO subsystemCode,
																   @RequestParam(name = "deviceTypeCode",required = false) List<String> deviceTypeCode,
																   @RequestParam(name = "time",required = false) String time,
																   HttpServletRequest req) {
		 Page<SubsystemFaultDTO> page = new Page<SubsystemFaultDTO>(pageNo, pageSize);
		 page= csSubsystemService.getSubsystemFailureReport(page,time,subsystemCode,deviceTypeCode);
		 return Result.ok(page);
	 }
	/**
	 * 统计报表-子系统分析-年次数据图
	 * @param
	 * @return
	 */
	@ApiOperation(value="统计报表-子系统分析-年次数据图", notes="统计报表-子系统分析-年次数据图")
	@GetMapping(value = "/yearNumFault")
	public Result<List<YearFaultDTO>> yearFault() {
		List<YearFaultDTO> pages = csSubsystemService.yearFault();
		return Result.ok(pages);
	}
	/**
	 * 统计报表-子系统分析-年分钟数据图
	 * @param
	 * @return
	 */
	@ApiOperation(value="统计报表-子系统分析-年分钟数据图", notes="统计报表-子系统分析-年分钟数据图")
	@GetMapping(value = "/yearMinuteFault")
	public Result<List<YearFaultDTO>> yearMinuteFault() {
		List<YearFaultDTO> pages = csSubsystemService.yearMinuteFault();
		return Result.ok(pages);
	}
	/**
	 * 统计报表-子系统分析-下拉框
	 * @param
	 * @return
	 */
	@ApiOperation(value="统计报表-子系统分析-下拉框", notes="统计报表-子系统分析-下拉框")
	@GetMapping(value = "/DeviceTypeComboBox")
	public Result<List<SubsystemFaultDTO>> deviceTypeComboBox(@RequestParam(name = "subsystemCode",required = false) String subsystemCode) {
		List<SubsystemFaultDTO> pages = csSubsystemService.deviceTypeCodeByNameDTO(subsystemCode);
		return Result.ok(pages);
	}
	/**
	 * 统计报表-子系统分析-根据查询子系统
	 * @param
	 * @return
	 */
	@ApiOperation(value="统计报表-子系统分析-根据查询子系统", notes="统计报表-子系统分析-根据查询子系统")
	@GetMapping(value = "/csSubsystemByCode")
	public Result<SystemByCodeDTO> csSubsystemByCode(@RequestParam(name = "subsystemCode") String subsystemCode) {
		SystemByCodeDTO pages = csSubsystemService.csSubsystemByCode(subsystemCode);
		return Result.ok(pages);
	}
	/**
	 * 统计报表-子系统分析导出
	 *
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "统计报表-子系统分析导出", notes = "统计报表-子系统分析导出")
	@GetMapping(value = "/reportSubSystemExport")
	public ModelAndView reportExport(HttpServletRequest request,
									 SubsystemFaultDTO subsystemCode,
									 @RequestParam(name = "deviceTypeCode",required = false) List<String> deviceTypeCode,
									 @RequestParam(name = "time",required = false) String time,
									 @RequestParam(name = "exportField",required = false)String exportField) {
		return csSubsystemService.reportSystemExport(request,subsystemCode,deviceTypeCode,time,exportField);
	}
	/**
	 * 下载子系统导入模板
	 *
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	@AutoLog(value = "下载子系统导入模板")
	@ApiOperation(value = "下载子系统导入模板", notes = "下载子系统导入模板")
	@RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
	public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("templates/子系统导入模板.xls");
		InputStream bis = classPathResource.getInputStream();
		BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		int len = 0;
		while ((len = bis.read()) != -1) {
			out.write(len);
			out.flush();
		}
		out.close();
	}
	/**
	 * 子系统导出
	 *
	 * @param systemName
	 * @param request
	 * @return
	 */
	@AutoLog(value = "子系统导出")
	@ApiOperation(value = "子系统导出", notes = "子系统导出")
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(@RequestParam(name="systemName", required = false) String systemName, HttpServletRequest request) {
		// Step.1 组装查询条件
		LambdaQueryWrapper<CsSubsystem> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
		if(ObjectUtil.isNotEmpty(systemName))
		{
			queryWrapper.like(CsSubsystem::getSystemName,systemName);
		}
		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<CsSubsystem> pageList = csSubsystemService.list(queryWrapper);
		List<CsSubsystemDTO> dtoList = new ArrayList<>();
		for (CsSubsystem subsystem : pageList) {
			CsSubsystemDTO dto = new CsSubsystemDTO();
			BeanUtil.copyProperties(subsystem,dto);
			String realNames = "";
			LambdaQueryWrapper<CsSubsystemUser> userQueryWrapper = new LambdaQueryWrapper<>();
			userQueryWrapper.eq(CsSubsystemUser::getSubsystemId,subsystem.getId());
			List<CsSubsystemUser> userList = csSubsystemUserMapper.selectList(userQueryWrapper);
			if(CollUtil.isNotEmpty(userList))
			{
				for(CsSubsystemUser systemUser:userList){
					LoginUser user = sysBaseApi.getUserById(systemUser.getUserId()+"");
					if(null!=user){
						realNames += user.getRealname() + ",";
					}
				}
			}
			if(!("").equals(realNames)){
				dto.setSystemUserName(realNames.substring(0,realNames.length()-1));
			}
			dtoList.add(dto);
		}
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "子系统导出");
		//excel注解对象Class
		mv.addObject(NormalExcelConstants.CLASS, CsSubsystemDTO.class);
		//自定义表格参数
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("子系统导出", "子系统导出"));
		//导出数据列表
		mv.addObject(NormalExcelConstants.DATA_LIST, dtoList);
		return mv;
	}
	/**
	 * 通过excel导入数据
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		// 错误信息
		List<String> errorMessage = new ArrayList<>();
		int successLines = 0, errorLines = 0;
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<CsSubsystemDTO> csSubsystemDTOList = ExcelImportUtil.importExcel(file.getInputStream(), CsSubsystemDTO.class, params);
				List<CsSubsystem> list = new ArrayList<>();
				for (int i = 0; i < csSubsystemDTOList.size(); i++) {
					CsSubsystemDTO csSubsystemDTO = csSubsystemDTOList.get(i);
					if (ObjectUtil.isNull(csSubsystemDTO.getMajorCode())) {
						errorMessage.add("专业编码为必填项，忽略导入");
						errorLines++;
						break;
					}
					else
					{
						CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().lambda().eq(CsMajor::getMajorName, csSubsystemDTO.getMajorName()).eq(CsMajor::getDelFlag, 0));
						if (csMajor == null) {
							errorMessage.add(csSubsystemDTO.getMajorName() + "专业名称不存在，忽略导入");
							errorLines++;
							break;
						}
						else
						{
							csSubsystemDTO.setMajorCode(csMajor.getMajorCode());
						}

					}
						if (ObjectUtil.isNull(csSubsystemDTO.getSystemCode())) {
							errorMessage.add("系统编码为必填项，忽略导入");
							errorLines++;
							break;
						}
						else
						{
							CsSubsystem csSubsystem = csSubsystemService.getOne(new QueryWrapper<CsSubsystem>().lambda().eq(CsSubsystem::getSystemCode, csSubsystemDTO.getSystemCode()).eq(CsSubsystem::getDelFlag, 0));
							if (csSubsystem != null) {
								errorMessage.add(csSubsystemDTO.getMajorCode() + "系统编码已经存在，忽略导入");
								errorLines++;
								break;
							}
						}

						if (ObjectUtil.isNull(csSubsystemDTO.getSystemName())) {
							errorMessage.add("系统名称为必填项，忽略导入");
							errorLines++;
							break;
						}
						else {
							CsSubsystem csSubsystem = csSubsystemService.getOne(new QueryWrapper<CsSubsystem>().lambda().eq(CsSubsystem::getSystemName, csSubsystemDTO.getSystemName()).eq(CsSubsystem::getDelFlag, 0));
							if (csSubsystem != null) {
								errorMessage.add(csSubsystem.getMajorCode() + "系统名称已经存在，忽略导入");
								errorLines++;
								break;
							}
						}
					CsSubsystem csSubsystem = new CsSubsystem();
					BeanUtils.copyProperties(csSubsystemDTO, csSubsystem);

					list.add(csSubsystem);
					successLines++;
				}
				if(errorLines==0)
				{
					csSubsystemService.saveBatch(list);
				}
				else
				{
					successLines =0;
				}
			} catch (Exception e) {
				errorMessage.add("发生异常：" + e.getMessage());
				log.error(e.getMessage(), e);
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return ImportExcelUtil.imporReturnRes(errorLines, successLines, errorMessage);
	}

}
