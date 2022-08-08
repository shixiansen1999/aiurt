package com.aiurt.modules.subsystem.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.service.IDeviceTypeService;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.entity.CsSubsystemUser;
import com.aiurt.modules.subsystem.mapper.CsSubsystemUserMapper;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;

import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


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
	private ISysBaseAPI sysBaseAPI;
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
					 LoginUser user = sysBaseAPI.getUserById(systemUser.getUserId()+"");
					 if(null!=user){
						 realNames += user.getRealname() + ",";
					 }
				 }
			 }
			 if(!realNames.equals("")){
				 system.setSystemUserName(realNames.substring(0,realNames.length()-1));
			 }
			 if(!userNames.equals("")){
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


}
