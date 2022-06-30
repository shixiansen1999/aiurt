package com.aiurt.modules.manufactor.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.manufactor.service.ICsManufactorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


 /**
 * @Description: cs_manufactor
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="系统管理-基础数据-厂商信息")
@RestController
@RequestMapping("/manufactor")
@Slf4j
public class CsManufactorController  {
	@Autowired
	private ICsManufactorService csManufactorService;
	@Autowired
	private IDeviceService deviceService;
	@Autowired
	private IMaterialBaseService materialBaseService;
	/**
	 * 分页列表查询
	 *
	 * @param csManufactor
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "厂商信息分页列表查询")
	@ApiOperation(value="厂商信息分页列表查询", notes="厂商信息分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CsManufactor csManufactor,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CsManufactor> queryWrapper = QueryGenerator.initQueryWrapper(csManufactor, req.getParameterMap());
		Page<CsManufactor> page = new Page<CsManufactor>(pageNo, pageSize);
		IPage<CsManufactor> pageList = csManufactorService.page(page, queryWrapper.eq("del_flag",0));
		return Result.OK(pageList);
	}

	 @ApiOperation(value="厂商信息列表查询", notes="厂商信息列表查询")
	 @GetMapping(value = "/selectList")
	 public Result<?> selectList(CsManufactor csManufactor,
									HttpServletRequest req) {
		 QueryWrapper<CsManufactor> queryWrapper = QueryGenerator.initQueryWrapper(csManufactor, req.getParameterMap());
		 List<CsManufactor> pageList = csManufactorService.list(queryWrapper.eq("del_flag",0));
		 return Result.OK(pageList);
	 }

	/**
	 *   添加
	 *
	 * @param csManufactor
	 * @return
	 */
	@AutoLog(value = "厂商信息添加")
	@ApiOperation(value="厂商信息添加", notes="厂商信息添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsManufactor csManufactor) {
		return csManufactorService.add(csManufactor);
	}

	/**
	 *  编辑
	 *
	 * @param csManufactor
	 * @return
	 */
	@AutoLog(value = "厂商信息编辑")
	@ApiOperation(value="厂商信息编辑", notes="厂商信息编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CsManufactor csManufactor) {
		return csManufactorService.update(csManufactor);
	}

	/**
	 *  通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "厂商信息通过id删除")
	@ApiOperation(value="厂商信息通过id删除", notes="厂商信息通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		CsManufactor csManufactor = csManufactorService.getById(id);
		//判断设备主数据是否使用
		LambdaQueryWrapper<Device> deviceWrapper =  new LambdaQueryWrapper<Device>();
		deviceWrapper.eq(Device::getManufactorCode,csManufactor.getCode());
		deviceWrapper.eq(Device::getDelFlag,0);
		List<Device> deviceList = deviceService.list(deviceWrapper);
		if(!deviceList.isEmpty()){
			return Result.error("该位置信息被设备主数据使用中，无法删除");
		}
		//判断物资主数据是否使用
		LambdaQueryWrapper<MaterialBase> materWrapper =  new LambdaQueryWrapper<MaterialBase>();
		materWrapper.eq(MaterialBase::getManufactorCode,csManufactor.getCode());
		materWrapper.eq(MaterialBase::getDelFlag,0);
		List<MaterialBase> materList = materialBaseService.list(materWrapper);
		if(!materList.isEmpty()){
			return Result.error("该位置信息被物资主数据使用中，无法删除");
		}
		csManufactor.setDelFlag(1);
		csManufactorService.updateById(csManufactor);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "厂商信息批量删除")
	@ApiOperation(value="厂商信息批量删除", notes="厂商信息批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Arrays.asList(ids.split(",")).stream().forEach(id -> {
			delete(id);
		});
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "厂商信息通过id查询")
	@ApiOperation(value="厂商信息通过id查询", notes="厂商信息通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CsManufactor csManufactor = csManufactorService.getById(id);
		if(csManufactor==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csManufactor);
	}

}
