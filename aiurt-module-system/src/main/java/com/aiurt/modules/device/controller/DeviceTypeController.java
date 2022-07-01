package com.aiurt.modules.device.controller;


import javax.servlet.http.HttpServletRequest;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.service.IDeviceComposeService;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.api.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

import java.util.ArrayList;
import java.util.List;
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

	 /**
	  * 设备类型左侧树、子系统树
	  * @param
	  * @return
	  */
	 @AutoLog(value = "设备类型左侧树")
	 @ApiOperation(value = "设备类型左侧树")
	 @GetMapping(value = "/treeList")
	 public Result<?> treeList(Integer level) {
		 List<CsMajor> majorList = csMajorService.list(new LambdaQueryWrapper<CsMajor>().eq(CsMajor::getDelFlag,0));
		 List<CsSubsystem> systemList = csSubsystemService.list(new LambdaQueryWrapper<CsSubsystem>().eq(CsSubsystem::getDelFlag,0));
		 List<DeviceType> deviceTypeList = deviceTypeService.list(new LambdaQueryWrapper<DeviceType>().eq(DeviceType::getDelFlag,0));
		 List<DeviceType> deviceTypeTree = deviceTypeService.treeList(deviceTypeList,"0");
		 List<DeviceType> newList = new ArrayList<>();
		 majorList.forEach(one -> {
			 DeviceType major = setEntity(one.getId(),"zy",one.getMajorCode(),one.getMajorName(),null,null,null,one.getMajorCode(),null);
			 List<CsSubsystem> sysList = systemList.stream().filter(system-> system.getMajorCode().equals(one.getMajorCode())).collect(Collectors.toList());
			 List<DeviceType> majorDeviceType = deviceTypeTree.stream().filter(type-> one.getMajorCode().equals(type.getMajorCode()) && (null==type.getSystemCode() || "".equals(type.getSystemCode())) && type.getPid().equals("0")).collect(Collectors.toList());
			 List<DeviceType> twoList = new ArrayList<>();
			 if(level>2) {
				//添加设备类型数据
				twoList.addAll(majorDeviceType);
			 }
			 //判断是否有子系统数据
			 sysList.forEach(two ->{
				 DeviceType system = setEntity(two.getId()+"","zxt",two.getSystemCode(),two.getSystemName(),null,null,null,two.getMajorCode(),two.getSystemCode());
				 if(level>2) {
					 List<DeviceType> sysDeviceType = deviceTypeTree.stream().filter(type -> system.getMajorCode().equals(type.getMajorCode()) && (null != type.getSystemCode() && !"".equals(type.getSystemCode()) && system.getSystemCode().equals(type.getSystemCode()))).collect(Collectors.toList());
					 system.setDeviceTypeChildren(sysDeviceType);
				 }
				 twoList.add(system);
			 });
			 major.setDeviceTypeChildren(twoList);
			 newList.add(major);
		 });
		 return Result.OK(newList);
	 }

	/**
	 * 物资分类列表结构查询（无分页。用于左侧树）
	 * @param majorCode
	 * @param systemCode
	 * @param req
	 * @return
	 */
	@AutoLog(value = "设备分类列表结构查询")
	@ApiOperation(value = "设备分类列表结构查询", notes = "设备分类列表结构查询")
	@GetMapping(value = "/selectList")
	public Result<List<DeviceType>> selectList(
			@RequestParam(name = "majorCode", required = false) String majorCode,
			@RequestParam(name = "systemCode", required = false) String systemCode,
			HttpServletRequest req) {
		Result<List<DeviceType>> result = new Result<List<DeviceType>>();
		QueryWrapper<DeviceType> deviceTypeQueryWrapper = new QueryWrapper<DeviceType>();
		deviceTypeQueryWrapper.eq("del_flag", 0);
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
	public DeviceType setEntity(String id,String treeType,String code,String name,Integer status,Integer isSpecialDevice,Integer isEnd,String majorCode,String systemCode){
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
	@ApiOperation(value="设备类型分页列表查询", notes="设备类型分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<DeviceType>> queryPageList(DeviceType deviceType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		LambdaQueryWrapper<DeviceCompose> composeWrapper = new LambdaQueryWrapper<>();
		List<DeviceCompose> deviceComposeList = deviceComposeService.list(composeWrapper.eq(DeviceCompose::getDelFlag,0));
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
		if(null != deviceType.getPid() && !"".equals(deviceType.getPid())){
			queryWrapper.eq(DeviceType::getPid, deviceType.getPid());
			if(null != deviceType.getSystemCode() && !"".equals(deviceType.getSystemCode())){
				queryWrapper.eq(DeviceType::getSystemCode, deviceType.getSystemCode());
			}else{
				queryWrapper.isNull(DeviceType::getSystemCode);
			}
		}
		Page<DeviceType> page = new Page<DeviceType>(pageNo, pageSize);
		IPage<DeviceType> pageList = deviceTypeService.page(page, queryWrapper.eq(DeviceType::getDelFlag,0));
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
			if(!type.getPid().equals("0")){
				type.setPUrl(deviceTypeService.getById(type.getPid()).getCode());
			}else if(null!=type.getMajorCode() && null!= type.getSystemCode() && type.getPid().equals("0")){
				LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper();
				wrapper.eq(CsSubsystem::getSystemCode,type.getSystemCode());
				wrapper.eq(CsSubsystem::getDelFlag,0);
				List<CsSubsystem> subList = csSubsystemService.list(wrapper);
				if(!subList.isEmpty()){
					type.setPUrl(subList.get(0).getSystemName());
				}
			}else{
				LambdaQueryWrapper<CsMajor> wrapper = new LambdaQueryWrapper();
				wrapper.eq(CsMajor::getMajorCode,type.getMajorCode());
				wrapper.eq(CsMajor::getDelFlag,0);
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
	@AutoLog(value = "设备类型添加")
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
	@AutoLog(value = "设备类型编辑")
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
	@AutoLog(value = "设备类型通过id删除")
	@ApiOperation(value="设备类型通过id删除", notes="设备类型通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		//查询设备主数据是否使用
		DeviceType deviceType = deviceTypeService.getById(id);
		LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Device::getDeviceTypeCode,deviceType.getCode());
		wrapper.eq(Device::getDelFlag,0);
		List<Device> deviceList = deviceService.list(wrapper);
		if(!deviceList.isEmpty()){
			return Result.error("设备类型被设备主数据使用中，无法删除");
		}
		//查询是否存在子节点，如存在，则不能删除
		LambdaQueryWrapper<DeviceType> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(DeviceType::getPid, id);
		queryWrapper.eq(DeviceType::getDelFlag, 0);
		List<DeviceType> list = deviceTypeService.list(queryWrapper);
		if(!list.isEmpty()){
			return Result.error("存在子节点，无法删除");
		}
		deviceType.setDelFlag(1);
		deviceTypeService.updateById(deviceType);
		return Result.OK("删除成功!");
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="设备类型通过id查询", notes="设备类型通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DeviceType> queryById(@RequestParam(name="id",required=true) String id) {
		DeviceType deviceType = deviceTypeService.getById(id);
		if(deviceType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(deviceType);
	}

}
