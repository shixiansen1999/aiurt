package com.aiurt.modules.device.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.common.entity.DeviceTypeTable;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.service.IDeviceComposeService;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.sm.mapper.CsSafetyAttentionMapper;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.CsUserSubsystemModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="设备管理-设备类型")
@RestController
@RequestMapping("/deviceType")
@Slf4j
public class DeviceTypeController extends BaseController<DeviceType, IDeviceTypeService> {
	public static final int LEVEL_2 =2;
	public static final String PID_0 = "0";
	@Autowired
	@Lazy
	private IDeviceTypeService deviceTypeService;
	@Autowired
	private ICsSubsystemService csSubsystemService;
	@Autowired
	private ICsMajorService csMajorService;
	@Autowired
	private IDeviceService deviceService;
	@Autowired
	private IDeviceComposeService deviceComposeService;
	@Autowired
	@Lazy
	private ISysBaseAPI sysBaseAPI;
	@Autowired
	private CsSafetyAttentionMapper csSafetyAttentionMapper;

	 /**
	  * 设备类型左侧树、子系统树
	  * @param
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询设备类型左侧树",permissionUrl = "/deviceType/list")
	 @ApiOperation(value = "设备类型左侧树")
	 @GetMapping(value = "/treeList")
	 @PermissionData(pageComponent = "manage/MajorList")
	 public Result<?> treeList(Integer level,@RequestParam(name="name",required = false) String name) {
		 List<CsMajor> majorList = csMajorService.list(new LambdaQueryWrapper<CsMajor>().eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0));
		 List<CsSubsystem> systemList = csSubsystemService.list(new LambdaQueryWrapper<CsSubsystem>().eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0).orderByDesc(CsSubsystem::getCreateTime));
		 List<DeviceType> deviceTypeList = deviceTypeService.selectList();
		 //name赋值给title，code赋值给value
		 for (DeviceType deviceType : deviceTypeList) {
			 deviceType.setTitle(deviceType.getName());
			 deviceType.setValue(deviceType.getCode());
		 }
		 List<DeviceType> deviceTypeTree = deviceTypeService.treeList(deviceTypeList,"0");
		 List<DeviceType> newList = new ArrayList<>();
		 majorList.forEach(one -> {
			 DeviceType major = setEntity(one.getId(),"zy",one.getMajorCode(),one.getMajorName(),null,null,null,one.getMajorCode(),null,"-",null);
			 major.setTitle(major.getName());
			 major.setValue(major.getCode());
			 List<CsSubsystem> sysList = systemList.stream().filter(system-> system.getMajorCode().equals(one.getMajorCode())).collect(Collectors.toList());
			 List<DeviceType> majorDeviceType = deviceTypeTree.stream().filter(type-> one.getMajorCode().equals(type.getMajorCode()) && (null==type.getSystemCode() || "".equals(type.getSystemCode())) && ("0").equals(type.getPid())).collect(Collectors.toList());
			 List<DeviceType> twoList = new ArrayList<>();
			 if(level>LEVEL_2) {
				//添加设备类型数据
				twoList.addAll(majorDeviceType);
			 }
			 //判断是否有子系统数据
			 sysList.forEach(two ->{
				 DeviceType system = setEntity(two.getId()+"","zxt",two.getSystemCode(),two.getSystemName(),null,null,null,two.getMajorCode(),two.getSystemCode(),one.getMajorName(),null);
				 if(level>LEVEL_2) {
					 List<DeviceType> sysDeviceType = deviceTypeTree.stream().filter(type -> system.getMajorCode().equals(type.getMajorCode()) && (null != type.getSystemCode() && !"".equals(type.getSystemCode()) && system.getSystemCode().equals(type.getSystemCode()))).collect(Collectors.toList());
					 List<DeviceType> collect = sysDeviceType.stream().distinct().collect(Collectors.toList());
					 //name赋值给title，code赋值给value
					 for (DeviceType deviceType : collect) {
						 deviceType.setTitle(deviceType.getName());
						 deviceType.setValue(deviceType.getCode());
					 }
					 system.setChildren(collect);
				 }
				 //name赋值给title，code赋值给value
				 system.setValue(system.getCode());
				 system.setTitle(system.getName());
				 twoList.add(system);
			 });
             if(!sysList.isEmpty()){
                 major.setPIsHaveSystem(1);
             }else{
                 major.setPIsHaveSystem(0);
             }
			 major.setChildren(twoList);
			 newList.add(major);
		 });
		 //做树形搜索处理
		 if (StrUtil.isNotBlank(name) && CollUtil.isNotEmpty(newList)){
			 this.assetTreeList(newList,name);
		 }
		 return Result.OK(newList);
	 }

	/**
	 * 设备类型左侧树、子系统树
	 * @param
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "权限过滤子系统设备类型左侧树",permissionUrl = "/deviceType/list")
	@ApiOperation(value = "权限过滤子系统设备类型左侧树")
	@GetMapping(value = "/powerTreeList")
	@PermissionData(pageComponent = "manage/MajorList")
	public Result<?> powerTreeList(Integer level,@RequestParam(name="name",required = false) String name) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		//专业权限
		List<CsUserMajorModel> list = sysBaseAPI.getMajorByUserId(sysUser.getId());
		//子系统权限
		List<CsUserSubsystemModel> list1 = sysBaseAPI.getSubsystemByUserId(sysUser.getId());
		List <String> majorCode =  list.stream().map(s-> s.getMajorCode()).collect(Collectors.toList());
		List <String> majorCode1 = list1.stream().map(s -> s.getMajorCode()).distinct().collect(Collectors.toList());
		List<String> majorCode2 =new ArrayList<>();
		//专业下的子系统
		majorCode2 = majorCode.stream()
				.filter((String s) -> !majorCode1.contains(s))
				.collect(Collectors.toList());
		List<String> systemCodes = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(majorCode2)) {
			systemCodes = csSafetyAttentionMapper.selectSystemCodes(majorCode2);
		}
		List<String> systemList1 = list1.stream().map(s-> s.getSystemCode()).collect(Collectors.toList());
		systemList1.addAll(systemCodes);
		Set<String> userRoleSet = sysBaseAPI.getUserRoleSet(sysUser.getUsername());
	    LambdaQueryWrapper<CsMajor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
	    LambdaQueryWrapper<CsSubsystem> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
		lambdaQueryWrapper1.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0)
				.orderByDesc(CsSubsystem::getCreateTime);
		if (CollectionUtil.isNotEmpty(userRoleSet)){
			if (!userRoleSet.contains("admin")){
				lambdaQueryWrapper.in(CsMajor::getMajorCode,majorCode);
				lambdaQueryWrapper1.in(CsSubsystem::getSystemCode,systemList1);
			}
		}
		List<CsMajor> majorList = csMajorService.list(lambdaQueryWrapper);
		List<CsSubsystem> systemList = csSubsystemService.list(lambdaQueryWrapper1);
		List<DeviceType> deviceTypeList = deviceTypeService.selectList();
		List<DeviceType> deviceTypeTree = deviceTypeService.treeList(deviceTypeList,"0");
		List<DeviceType> newList = new ArrayList<>();
		majorList.forEach(one -> {
			DeviceType major = setEntity(one.getId(),"zy",one.getMajorCode(),one.getMajorName(),null,null,null,one.getMajorCode(),null,"-",null);
			List<CsSubsystem> sysList = systemList.stream().filter(system-> system.getMajorCode().equals(one.getMajorCode())).collect(Collectors.toList());
			List<DeviceType> majorDeviceType = deviceTypeTree.stream().filter(type-> one.getMajorCode().equals(type.getMajorCode()) && (null==type.getSystemCode() || "".equals(type.getSystemCode())) && ("0").equals(type.getPid())).collect(Collectors.toList());
			List<DeviceType> twoList = new ArrayList<>();
			if(level>LEVEL_2) {
				//添加设备类型数据
				twoList.addAll(majorDeviceType);
			}
			//判断是否有子系统数据
			sysList.forEach(two ->{
				DeviceType system = setEntity(two.getId()+"","zxt",two.getSystemCode(),two.getSystemName(),null,null,null,two.getMajorCode(),two.getSystemCode(),one.getMajorName(),null);
				if(level>LEVEL_2) {
					List<DeviceType> sysDeviceType = deviceTypeTree.stream().filter(type -> system.getMajorCode().equals(type.getMajorCode()) && (null != type.getSystemCode() && !"".equals(type.getSystemCode()) && system.getSystemCode().equals(type.getSystemCode()))).collect(Collectors.toList());
					List<DeviceType> collect = sysDeviceType.stream().distinct().collect(Collectors.toList());
					system.setChildren(collect);
				}
				system.setTitle(system.getName());
				system.setValue(system.getCode());
				twoList.add(system);
			});
			if(!sysList.isEmpty()){
				major.setPIsHaveSystem(1);
			}else{
				major.setPIsHaveSystem(0);
			}
			major.setChildren(twoList);
			major.setTitle(major.getName());
			major.setValue(major.getMajorCode());
			newList.add(major);
		});
		//做树形搜索处理
		if (StrUtil.isNotBlank(name) && CollUtil.isNotEmpty(newList)){
			this.assetTreeList(newList,name);
		}
		return Result.OK(newList);
	}

	private void assetTreeList(List<DeviceType> deviceTypeTree,String name){
		Iterator<DeviceType> iterator = deviceTypeTree.iterator();
		while (iterator.hasNext()) {
			DeviceType next = iterator.next();
			if (StrUtil.containsAnyIgnoreCase(next.getName(), name)) {
				//名称匹配则赋值颜色
				next.setColor("#FF5B05");
			}
			List<DeviceType> children = next.getChildren();
			if (CollUtil.isNotEmpty(children)) {
				assetTreeList(children,name);
			}
			//如果没有子级，并且当前不匹配，则去除
			if (CollUtil.isEmpty(next.getChildren()) && StrUtil.isEmpty(next.getColor())) {
				iterator.remove();
			}
		}
	}

	/**
	 * 物资分类列表结构查询（无分页。用于左侧树）
	 * @param majorCode
	 * @param systemCode
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询物资分类列表左侧树",permissionUrl = "/material/materialBaseType/list")
	@ApiOperation(value = "无分页物资分类列表左侧树", notes = "无分页物资分类列表左侧树查询")
	@GetMapping(value = "/selectList")
	@PermissionData(pageComponent = "/equipmentData/classify")
	public Result<List<DeviceType>> selectList(
			@RequestParam(name = "majorCode", required = false) String majorCode,
			@RequestParam(name = "systemCode", required = false) String systemCode,
			HttpServletRequest req) {
		Result<List<DeviceType>> result = new Result<List<DeviceType>>();
		QueryWrapper<DeviceType> deviceTypeQueryWrapper = new QueryWrapper<DeviceType>();
		deviceTypeQueryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		if(majorCode != null && !"".equals(majorCode)){
			deviceTypeQueryWrapper.eq("major_code", majorCode);
		}
		if(systemCode != null && !"".equals(systemCode)){
			deviceTypeQueryWrapper.eq("system_code", systemCode);
		}else {
			deviceTypeQueryWrapper.apply(" (system_code = '' or system_code is null) ");
		}
		deviceTypeQueryWrapper.orderByDesc("create_time");
		List<DeviceType> deviceTypeList = deviceTypeService.list(deviceTypeQueryWrapper);
		List<DeviceType> deviceTypes = deviceTypeService.treeList(deviceTypeList,"0");
		result.setSuccess(true);
		result.setResult(deviceTypes);
		return result;
	}
	/**
	 * 设备类型-转换实体
	 * @param id
	 * @param treeType
	 * @param code
	 * @param name
	 * @param status
	 * @param isSpecialDevice
	 * @param isEnd
	 * @return
	 */
	public DeviceType setEntity(String id,String treeType,String code,String name,Integer status,Integer isSpecialDevice,Integer isEnd,String majorCode,String systemCode,String pUrl,Integer pIsSpecialDevice){
		DeviceType type = new DeviceType();
		type.setId(id);
		type.setTreeType(treeType);
		type.setCode(code);
		type.setName(name);
		type.setStatus(status);
		type.setIsSpecialDevice(isSpecialDevice);
		type.setIsEnd(isEnd);
		type.setMajorCode(majorCode);
		type.setSystemCode(systemCode);
		type.setPUrl(pUrl);
		type.setPIsSpecialDevice(pIsSpecialDevice);
		return type;
	}
	 /**
	 * 分页列表查询
	 *
	 * @param deviceType
	 * @param pageNo
	 * @param pageSize
	 * @param
	 * @return
	 */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询设备类型",permissionUrl = "/deviceType/list")
	@ApiOperation(value="设备类型分页列表查询", notes="设备类型分页列表查询")
	@GetMapping(value = "/list")
     @PermissionData(pageComponent = "manage/MajorList")
	public Result<IPage<DeviceType>> queryPageList(DeviceType deviceType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		LambdaQueryWrapper<DeviceCompose> composeWrapper = new LambdaQueryWrapper<>();
		List<DeviceCompose> deviceComposeList = deviceComposeService.list(composeWrapper.eq(DeviceCompose::getDelFlag, CommonConstant.DEL_FLAG_0));
		//查询条件
		LambdaQueryWrapper<DeviceType> queryWrapper = new LambdaQueryWrapper<>();
		if(null != deviceType.getName() && !"".equals(deviceType.getName())){
			queryWrapper.like(DeviceType::getName, deviceType.getName());
		}
		if(null != deviceType.getStatus() && !"".equals(deviceType.getStatus())){
			queryWrapper.eq(DeviceType::getStatus, deviceType.getStatus());
		}
		if(null != deviceType.getIsSpecialDevice() && !"".equals(deviceType.getIsSpecialDevice())){
			queryWrapper.eq(DeviceType::getIsSpecialDevice, deviceType.getIsSpecialDevice());
		}
		//左侧树点击，拼接条件
		if(null != deviceType.getMajorCode() && !"".equals(deviceType.getMajorCode())){
			queryWrapper.eq(DeviceType::getMajorCode, deviceType.getMajorCode());
		}
		if(null != deviceType.getSystemCode() && !"".equals(deviceType.getSystemCode())){
			queryWrapper.eq(DeviceType::getSystemCode, deviceType.getSystemCode());
		}
		if(null != deviceType.getCodeCc() && !"".equals(deviceType.getCodeCc())){
			queryWrapper.like(DeviceType::getCodeCc, deviceType.getCodeCc());
		}
		Page<DeviceType> page = new Page<DeviceType>(pageNo, pageSize);
		IPage<DeviceType> pageList = deviceTypeService.page(page, queryWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0).orderByAsc(DeviceType::getCreateTime));
		pageList.getRecords().forEach(type->{
			//查询设备组成
			List<DeviceCompose> composeList = deviceComposeList.stream().filter(compose -> compose.getDeviceTypeCode().equals(type.getCode()) ).collect(Collectors.toList());
			type.setDeviceComposeList(composeList);
			//是否有设备组成
			if(!composeList.isEmpty()){
				type.setIsHaveDevice(1);
			}else{
				type.setIsHaveDevice(0);
			}
			//查询上级节点
			if(!(PID_0).equals(type.getPid())){
				type.setPUrl(deviceTypeService.getById(type.getPid()).getName());
			}else if(null!=type.getMajorCode() && null!= type.getSystemCode() && (PID_0).equals(type.getPid())){
				LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper();
				wrapper.eq(CsSubsystem::getSystemCode,type.getSystemCode());
				wrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
				List<CsSubsystem> subList = csSubsystemService.list(wrapper);
				if(!subList.isEmpty()){
					type.setPUrl(subList.get(0).getSystemName());
				}
			}else{
				LambdaQueryWrapper<CsMajor> wrapper = new LambdaQueryWrapper();
				wrapper.eq(CsMajor::getMajorCode,type.getMajorCode());
				wrapper.eq(CsMajor::getDelFlag, CommonConstant.DEL_FLAG_0);
				List<CsMajor> majorList = csMajorService.list(wrapper);
				if(!majorList.isEmpty()){
					type.setPUrl(majorList.get(0).getMajorName());
				}
			}
		});
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param deviceType
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加设备类型",permissionUrl = "/deviceType/list")
	@ApiOperation(value="设备类型添加", notes="设备类型添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DeviceType deviceType) {
		return deviceTypeService.add(deviceType);
	}

	/**
	 *  编辑
	 *
	 * @param deviceType
	 * @return
	 */
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑设备类型",permissionUrl = "/deviceType/list")
	@ApiOperation(value="设备类型编辑", notes="设备类型编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody DeviceType deviceType) {
		return deviceTypeService.update(deviceType);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "删除设备类型",permissionUrl = "/deviceType/list")
	@ApiOperation(value="设备类型通过id删除", notes="设备类型通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		//查询设备主数据是否使用
		DeviceType deviceType = deviceTypeService.getById(id);
		LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Device::getDeviceTypeCode,deviceType.getCode());
		wrapper.eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<Device> deviceList = deviceService.list(wrapper);
		if(!deviceList.isEmpty()){
			return Result.error("设备类型被设备主数据使用中，无法删除");
		}
		//查询是否存在子节点，如存在，则不能删除
		LambdaQueryWrapper<DeviceType> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(DeviceType::getPid, id);
		queryWrapper.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<DeviceType> list = deviceTypeService.list(queryWrapper);
		if(!list.isEmpty()){
			return Result.error("存在子节点，无法删除");
		}
		deviceType.setDelFlag(CommonConstant.DEL_FLAG_1);
		deviceTypeService.removeById(deviceType);
//		deviceTypeService.updateById(deviceType);
		//删除设备组成
		LambdaQueryWrapper<DeviceCompose> comWrapper = new LambdaQueryWrapper<>();
		comWrapper.eq(DeviceCompose::getDeviceTypeCode,deviceType.getCode());
		comWrapper.eq(DeviceCompose::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<DeviceCompose> comList = deviceComposeService.list(comWrapper);
		if(!comList.isEmpty()){
			comList.forEach(com->{
				com.setDelFlag(CommonConstant.DEL_FLAG_1);
				deviceComposeService.removeById(com);
			});
		}
		return Result.OK("删除成功!");
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "设备类型通过id查询",permissionUrl = "/deviceType/list")
	@ApiOperation(value="设备类型通过id查询", notes="设备类型通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DeviceType> queryById(@RequestParam(name="id",required=true) String id) {
		LambdaQueryWrapper<DeviceCompose> composeWrapper = new LambdaQueryWrapper<>();
		List<DeviceCompose> deviceComposeList = deviceComposeService.list(composeWrapper.eq(DeviceCompose::getDelFlag, CommonConstant.DEL_FLAG_0));

		DeviceType deviceType = deviceTypeService.getById(id);
		if(deviceType==null) {
			return Result.error("未找到对应数据");
		}
		//查询设备组成
		List<DeviceCompose> composeList = deviceComposeList.stream().filter(compose -> compose.getDeviceTypeCode().equals(deviceType.getCode()) ).collect(Collectors.toList());
		deviceType.setDeviceComposeList(composeList);
		List<DeviceType> deviceTypeList = deviceTypeService.list(new LambdaQueryWrapper<DeviceType>().eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0));
		List<DeviceType> childList = deviceTypeList.stream().filter(type -> deviceType.getId().equals(type.getPid())).collect(Collectors.toList());
		if(childList != null && childList.size()>0){
			deviceType.setChildren(childList);
		}
		return Result.OK(deviceType);
	}

	/**
	 * 查询所有设备类型
	 * @return
	 */
    @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询所有设备类型")
    @ApiOperation(value="查询所有设备类型", notes="查询所有设备类型")
    @GetMapping(value = "/getDeviceTypeList")
    public List<DeviceType> getDeviceTypeList() {
        LambdaQueryWrapper<DeviceType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceType::getDelFlag ,CommonConstant.DEL_FLAG_0);
        List<DeviceType> list = deviceTypeService.list(wrapper);
        return list;
    }
	@ApiOperation(value = "下载设备类型导入模板", notes = "下载设备类型导入模板")
	@RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
	public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("templates/deviceType.xlsx");
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
	 * 通过excel导入数据
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "通过excel导入数据", notes = "通过excel导入数据")
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
				return deviceTypeService.importExcelMaterial(file, params);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("文件导入失败:" + e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return Result.error("文件导入失败！");
	}

	/**
	 * 设备类型导出
	 *
	 * @return
	 */
	@ApiOperation(value = "设备类型导出", notes = "设备类型导出")
	@RequestMapping(value = "/exportXls", method = {RequestMethod.GET, RequestMethod.POST})
	public void exportXls(HttpServletRequest request, HttpServletResponse response ,
						  DeviceType deviceType) {
		  deviceTypeService.exportXls(request, response,deviceType);
	}
}
