package com.aiurt.modules.system.controller;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.system.dto.SysUserUsageRespDTO;
import com.aiurt.modules.system.entity.SysUserUsage;
import com.aiurt.modules.system.service.ISysUserUsageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 系统用户被选用频率表
 * @Author: fgw
 * @Date:   2023-07-24
 * @Version: V1.0
 */
@Api(tags="系统用户被选用频率表")
@RestController
@RequestMapping("/system/sysUserUsage")
@Slf4j
public class SysUserUsageController extends BaseController<SysUserUsage, ISysUserUsageService> {
	@Autowired
	private ISysUserUsageService sysUserUsageService;

	/**
	 * 分页列表查询
	 *
	 * @param name
	 * @return
	 */
	@ApiOperation(value="搜索", notes="搜索")
	@GetMapping(value = "/globalSearch")
	public Result<List<SysUserUsageRespDTO>> globalSearch(@RequestParam(required = false, value = "name") String name) {
		List<SysUserUsageRespDTO> result = sysUserUsageService.globalSearch(name);
		return Result.OK(result);
	}

	/**
	 * 查询常用的用户信息
	 * @return
	 */
	@ApiOperation(value="常用选择", notes="常用选择下拉列表")
	@GetMapping(value = "/queryList")
	public Result<List<SysUserUsageRespDTO>> queryList(@RequestParam(required = false, value = "search")String search) {
		List<SysUserUsageRespDTO> result = sysUserUsageService.queryList(search);
		return Result.OK(result);
	}


}
