package com.aiurt.modules.subsystem.controller;

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
import com.aiurt.modules.subsystem.entity.CsSubsystemUser;
import com.aiurt.modules.subsystem.service.ICsSubsystemUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

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
 * @Description: cs_subsystem_user
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="cs_subsystem_user")
@RestController
@RequestMapping("/subsystem/csSubsystemUser")
@Slf4j
public class CsSubsystemUserController  {
	@Autowired
	private ICsSubsystemUserService csSubsystemUserService;

	/**
	 * 分页列表查询
	 *
	 * @param csSubsystemUser
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "cs_subsystem_user-分页列表查询")
	@ApiOperation(value="cs_subsystem_user-分页列表查询", notes="cs_subsystem_user-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CsSubsystemUser csSubsystemUser,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CsSubsystemUser> queryWrapper = QueryGenerator.initQueryWrapper(csSubsystemUser, req.getParameterMap());
		Page<CsSubsystemUser> page = new Page<CsSubsystemUser>(pageNo, pageSize);
		IPage<CsSubsystemUser> pageList = csSubsystemUserService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param csSubsystemUser
	 * @return
	 */
	@AutoLog(value = "cs_subsystem_user-添加")
	@ApiOperation(value="cs_subsystem_user-添加", notes="cs_subsystem_user-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsSubsystemUser csSubsystemUser) {
		csSubsystemUserService.save(csSubsystemUser);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param csSubsystemUser
	 * @return
	 */
	@AutoLog(value = "cs_subsystem_user-编辑")
	@ApiOperation(value="cs_subsystem_user-编辑", notes="cs_subsystem_user-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CsSubsystemUser csSubsystemUser) {
		csSubsystemUserService.updateById(csSubsystemUser);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "cs_subsystem_user-通过id删除")
	@ApiOperation(value="cs_subsystem_user-通过id删除", notes="cs_subsystem_user-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		csSubsystemUserService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "cs_subsystem_user-批量删除")
	@ApiOperation(value="cs_subsystem_user-批量删除", notes="cs_subsystem_user-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.csSubsystemUserService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "cs_subsystem_user-通过id查询")
	@ApiOperation(value="cs_subsystem_user-通过id查询", notes="cs_subsystem_user-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CsSubsystemUser csSubsystemUser = csSubsystemUserService.getById(id);
		if(csSubsystemUser==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csSubsystemUser);
	}



}
