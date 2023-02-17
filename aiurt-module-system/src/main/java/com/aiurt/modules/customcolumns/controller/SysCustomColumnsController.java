package com.aiurt.modules.customcolumns.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.customcolumns.entity.SysCustomColumns;
import com.aiurt.modules.customcolumns.service.ISysCustomColumnsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: sys_custom_columns
 * @Author: aiurt
 * @Date:   2023-02-16
 * @Version: V1.0
 */
@Api(tags="sys_custom_columns")
@RestController
@RequestMapping("/customcolumns/sysCustomColumns")
@Slf4j
public class SysCustomColumnsController extends BaseController<SysCustomColumns, ISysCustomColumnsService> {
	@Autowired
	private ISysCustomColumnsService sysCustomColumnsService;


	 /**
	  * 列表查询
	  * @param moduleKey
	  * @param req
	  * @return
	  */
	 @ApiOperation(value="sys_custom_columns-列表查询", notes="sys_custom_columns-列表查询")
	 @GetMapping(value = "/list")
	 public Result<List<SysCustomColumns>> queryPageList(@RequestParam(name="moduleKey",required = false) String moduleKey,
														 HttpServletRequest req) {
		 List<SysCustomColumns> list = new ArrayList<>();
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 LambdaQueryWrapper<SysCustomColumns> wrapper = new LambdaQueryWrapper<>();
		 wrapper.eq(SysCustomColumns::getUserName,sysUser.getUsername());
		 if (StrUtil.isNotEmpty(moduleKey)){
		 	wrapper.eq(SysCustomColumns::getModuleKey,moduleKey);
		 }
		 list = sysCustomColumnsService.list(wrapper);
		 return Result.OK(list);
	 }
	/**
	 *   添加
	 *
	 * @param sysCustomColumns
	 * @return
	 */
	@AutoLog(value = "sys_custom_columns-添加")
	@ApiOperation(value="sys_custom_columns-添加", notes="sys_custom_columns-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SysCustomColumns sysCustomColumns) {
		if (ObjectUtil.isEmpty(sysCustomColumns)){
			return Result.error("数据为空,添加失败");
		}
		sysCustomColumnsService.getBaseMapper().delete(new LambdaQueryWrapper<SysCustomColumns>()
				.eq(SysCustomColumns::getModuleKey,sysCustomColumns.getModuleKey())
				.eq(SysCustomColumns::getUserName,sysCustomColumns.getUserName()));
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		sysCustomColumns.setUserName(sysUser.getUsername());
		sysCustomColumnsService.save(sysCustomColumns);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sysCustomColumns
	 * @return
	 */
	@AutoLog(value = "sys_custom_columns-编辑")
	@ApiOperation(value="sys_custom_columns-编辑", notes="sys_custom_columns-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SysCustomColumns sysCustomColumns) {
		sysCustomColumnsService.updateById(sysCustomColumns);
		return Result.OK("编辑成功!");
	}


}
