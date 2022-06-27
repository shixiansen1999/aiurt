package com.aiurt.modules.major.controller;

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
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.material.service.IMaterialBaseTypeService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


 /**
 * @Description: cs_major
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="系统管理-基础数据-专业")
@RestController
@RequestMapping("/major")
@Slf4j
public class CsMajorController  {
	@Autowired
	private ICsMajorService csMajorService;
	@Autowired
	private ICsSubsystemService csSubsystemService;
	@Autowired
	private IMaterialBaseTypeService materialBaseTypeService;
	/**
	 * 分页列表查询
	 *
	 * @param csMajor
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "专业分页列表查询")
	@ApiOperation(value="专业分页列表查询", notes="专业分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CsMajor csMajor,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CsMajor> queryWrapper = QueryGenerator.initQueryWrapper(csMajor, req.getParameterMap());
		Page<CsMajor> page = new Page<CsMajor>(pageNo, pageSize);
		IPage<CsMajor> pageList = csMajorService.page(page, queryWrapper.eq("del_flag",0));
		return Result.OK(pageList);
	}

	 @ApiOperation(value="专业列表查询", notes="专业列表查询")
	 @GetMapping(value = "/selectList")
	 public Result<?> selectList(CsMajor csMajor,
									HttpServletRequest req) {
		 QueryWrapper<CsMajor> queryWrapper = QueryGenerator.initQueryWrapper(csMajor, req.getParameterMap());
		 List<CsMajor> pageList = csMajorService.list(queryWrapper.eq("del_flag",0));
		 return Result.OK(pageList);
	 }

	/**
	 *   添加
	 *
	 * @param csMajor
	 * @return
	 */
	@AutoLog(value = "专业添加")
	@ApiOperation(value="专业添加", notes="专业添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsMajor csMajor) {
		return csMajorService.add(csMajor);
	}

	/**
	 *  编辑
	 *
	 * @param csMajor
	 * @return
	 */
	@AutoLog(value = "专业编辑")
	@ApiOperation(value="专业编辑", notes="专业编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CsMajor csMajor) {
		return csMajorService.update(csMajor);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "专业通过id删除")
	@ApiOperation(value="专业通过id删除", notes="专业通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		CsMajor csMajor = csMajorService.getById(id);
		//判断是否被子系统使用
		LambdaQueryWrapper<CsSubsystem> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(CsSubsystem::getMajorCode,csMajor.getMajorCode());
		wrapper.eq(CsSubsystem::getDelFlag,0);
		List<CsSubsystem> list = csSubsystemService.list(wrapper);
		if(!list.isEmpty()){
			return Result.error("该专业被子系统使用中，不能删除!");
		}
		//判断是否被设备类型使用 todo

		//判断是否被物资分类使用
		LambdaQueryWrapper<MaterialBaseType> materWrapper = new LambdaQueryWrapper<>();
		materWrapper.eq(MaterialBaseType::getMajorCode,csMajor.getMajorCode());
		materWrapper.eq(MaterialBaseType::getDelFlag,0);
		List<MaterialBaseType> materList = materialBaseTypeService.list(materWrapper);
		if(!materList.isEmpty()){
			return Result.error("该专业被物资分类使用中，不能删除!");
		}
		csMajor.setDelFlag(1);
		csMajorService.updateById(csMajor);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "专业批量删除")
	@ApiOperation(value="专业批量删除", notes="专业批量删除")
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
	//@AutoLog(value = "专业通过id查询")
	@ApiOperation(value="专业通过id查询", notes="专业通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CsMajor csMajor = csMajorService.getById(id);
		if(csMajor==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csMajor);
	}



}
