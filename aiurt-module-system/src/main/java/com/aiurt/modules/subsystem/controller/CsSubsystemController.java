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
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.entity.CsSubsystemUser;
import com.aiurt.modules.subsystem.mapper.CsSubsystemUserMapper;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
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
 * @Description: cs_subsystem
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="子系统")
@RestController
@RequestMapping("/subsystem")
@Slf4j
public class CsSubsystemController  {
	@Autowired
	private ICsSubsystemService csSubsystemService;
	 @Autowired
	 private CsSubsystemUserMapper csSubsystemUserMapper;

	/**
	 * 分页列表查询
	 *
	 * @param csSubsystem
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "子系统分页列表查询")
	@ApiOperation(value="子系统分页列表查询", notes="子系统分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CsSubsystem csSubsystem,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CsSubsystem> queryWrapper = QueryGenerator.initQueryWrapper(csSubsystem, req.getParameterMap());
		Page<CsSubsystem> page = new Page<CsSubsystem>(pageNo, pageSize);
		IPage<CsSubsystem> pageList = csSubsystemService.page(page, queryWrapper.eq("del_flag",0));
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param csSubsystem
	 * @return
	 */
	@AutoLog(value = "子系统添加")
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
	@AutoLog(value = "子系统编辑")
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
	@AutoLog(value = "子系统通过id删除")
	@ApiOperation(value="子系统通过id删除", notes="子系统通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		CsSubsystem csSubsystem = csSubsystemService.getById(id);
		csSubsystem.setDelFlag(1);
		csSubsystemService.updateById(csSubsystem);
		//关联删除
		QueryWrapper<CsSubsystemUser> userQueryWrapper = new QueryWrapper<CsSubsystemUser>();
		userQueryWrapper.eq("subsystem_id", id);
		csSubsystemUserMapper.delete(userQueryWrapper);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "子系统批量删除")
	@ApiOperation(value="子系统批量删除", notes="子系统批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Arrays.asList(ids.split(",")).stream().forEach(id -> {
			delete(id);
		});
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "子系统通过id查询")
	@ApiOperation(value="子系统通过id查询", notes="子系统通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CsSubsystem csSubsystem = csSubsystemService.getById(id);
		if(csSubsystem==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csSubsystem);
	}


}
