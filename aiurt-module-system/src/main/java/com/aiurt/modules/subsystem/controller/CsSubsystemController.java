package com.aiurt.modules.subsystem.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
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
import com.aiurt.modules.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
public class CsSubsystemController extends BaseController<CsSubsystem, ICsSubsystemService> {
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
	@Autowired
	private ISysUserService sysUserService;
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
			 if( systemName != null && !"".equals(systemName) ){
				 wrapper.like(CsSubsystem::getSystemName,systemName);
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
																   @RequestParam(name = "startTime",required = false) String startTime,
																   @RequestParam(name = "endTime",required = false) String endTime,
																   HttpServletRequest req) {
		 Page<SubsystemFaultDTO> page = new Page<SubsystemFaultDTO>(pageNo, pageSize);
		 if (startTime != null && endTime != null) {
			 startTime = DateUtil.format(DateUtil.beginOfMonth(DateUtil.parse(startTime,"yyyy-MM")),"yyyy-MM-dd") ;
			 endTime =DateUtil.format(DateUtil.beginOfMonth(DateUtil.parse(endTime,"yyyy-MM")),"yyyy-MM-dd") ;
		 }
		 page= csSubsystemService.getSubsystemFailureReport(page,startTime,endTime,subsystemCode,deviceTypeCode);
		 return Result.ok(page);
	 }
	/**
	 * 统计报表-子系统分析-年次数据图
	 * @param
	 * @return
	 */
	@ApiOperation(value="统计报表-子系统分析-年次数据图", notes="统计报表-子系统分析-年次数据图")
	@GetMapping(value = "/yearNumFault")
	public Result<List<YearFaultDTO>> yearFault(@RequestParam(name = "name",required = false) String name) {
		List<YearFaultDTO> pages = csSubsystemService.yearFault(name);
		return Result.ok(pages);
	}
	/**
	 * 统计报表-子系统分析-年分钟数据图
	 * @param
	 * @return
	 */
	@ApiOperation(value="统计报表-子系统分析-年分钟数据图", notes="统计报表-子系统分析-年分钟数据图")
	@GetMapping(value = "/yearMinuteFault")
	public Result<List<YearFaultDTO>> yearMinuteFault(@RequestParam(name = "name",required = false) String name) {
		List<YearFaultDTO> pages = csSubsystemService.yearMinuteFault(name);
		return Result.ok(pages);
	}

	/**
	 * 统计报表-子系统分析-趋势图
	 * @param
	 * @return
	 */
	@ApiOperation(value="统计报表-子系统分析-趋势图", notes="统计报表-子系统分析-趋势图")
	@GetMapping(value = "/yearTrendChartFault")
	public Result<List<YearFaultDTO>> yearTrendChartFault(@RequestParam(name = "systemCodes",required = false) List<String> systemCodes,
														  @RequestParam(name = "startTime",required = false) String startTime,
														  @RequestParam(name = "endTime",required = false) String endTime) {
		if (startTime != null && endTime != null) {
			startTime =DateUtil.format(DateUtil.beginOfMonth(DateUtil.parse(startTime,"yyyy-MM")),"yyyy-MM-dd") ;
			endTime =DateUtil.format(DateUtil.beginOfMonth(DateUtil.parse(endTime,"yyyy-MM")),"yyyy-MM-dd") ;
		}
		List<YearFaultDTO> pages = csSubsystemService.yearTrendChartFault(startTime,endTime,systemCodes);
		return Result.ok(pages);
	}

	/**
	 * 统计报表-子系统分析-下拉框
	 * @param
	 * @return
	 */
	@ApiOperation(value="统计报表-子系统分析-下拉框", notes="统计报表-子系统分析-下拉框")
	@GetMapping(value = "/DeviceTypeComboBox")
	public Result<List<SubsystemFaultDTO>> deviceTypeComboBox(@RequestParam(name = "subsystemCode",required = false) List<String> subsystemCode) {
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
									 @RequestParam(name = "startTime") String startTime,
									 @RequestParam(name = "endTime") String endTime,
									 @RequestParam(name = "exportField",required = false)String exportField) {
		return csSubsystemService.reportSystemExport(request,subsystemCode,deviceTypeCode,startTime,endTime,exportField);
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
	public ModelAndView downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
		String remark ="子系统信息导入模板\n" +
				"填写须知：\n" +
				"1.请勿增加、删除、或修改表格中的字段顺序、字段名称；\n" +
				"2.请严格按照数据规范填写，并填写完所有必填项，红底白字列为必填项；\n" +
				"3.单次最多导入10000条数据；\n" +
				"字段说明：\n" +
				"1.所属专业：必填字段，且在系统中存在该专业；\n" +
				"2.子系统编号：必填字段，且不能重复，支持数字、英文字母、符号等；\n" +
				"3.子系统名称：必填字段，且不能重复，支持数字、英文字母、符号等；\n" +
				"4.技术员：支持有多个人名，用英文“;”分隔，且在系统用户管理中存在技术员名字；";
		return super.exportTemplateXls("", CsSubsystemDTO.class, "子系统导入模板",remark);
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
	@ApiOperation(value = "导入数据", notes = "导入数据")
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return  csSubsystemService.importExcel(request,response);
	}


}
