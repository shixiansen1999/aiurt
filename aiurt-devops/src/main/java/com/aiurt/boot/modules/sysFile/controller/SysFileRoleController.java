package com.aiurt.boot.modules.sysFile.controller;

import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.sysFile.entity.DefaultUser;
import com.aiurt.boot.modules.sysFile.entity.SysFileRole;
import com.aiurt.boot.modules.sysFile.service.DefaultUserService;
import com.aiurt.boot.modules.sysFile.service.ISysFileRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 文档权限表
 * @Author: qian
 * @Date: 2021-10-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "文档权限表")
@RestController
@RequestMapping("/sysfile/sysFileRole")
public class SysFileRoleController {

	@Autowired
	private ISysFileRoleService sysFileRoleService;

	@Autowired
	private DefaultUserService defaultUserService;

	/**
	 * 查询所有的列表
	 *
	 * @param request 请求
	 * @return {@link Result}<{@link List}<{@link SysFileRole}>>
	 */
	@AutoLog(value = "文档权限表-树状查询")
	@ApiOperation(value = "文档权限表-树状查询", notes = "文档权限表-树状查询")
	@GetMapping(value = "/allList")
	public Result<List<SysFileRole>> queryAllList(HttpServletRequest request) {

		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = loginUser.getId();

		Result<List<SysFileRole>> result = new Result<>();
		result.setSuccess(true);
		List<SysFileRole> list = this.sysFileRoleService.lambdaQuery()
				.eq(SysFileRole::getDelFlag, CommonConstant.DEL_FLAG_0)
				.like(SysFileRole::getUserId, userId).list();
		result.setResult(list);

		return result;
	}

	/**
	 * 添加常用人员
	 *
	 * @param request 请求
	 * @return {@link Result}<{@link List}<{@link SysFileRole}>>
	 */
	@AutoLog(value = "文档权限表-添加常用人员")
	@ApiOperation(value = "文档权限表-添加常用人员", notes = "文档权限表-添加常用人员")
	@PostMapping(value = "/addDefault")
	public Result<?> addDefault(HttpServletRequest request,
	                            @RequestBody List<String> userIds) {

		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = loginUser.getId();

		List<DefaultUser> users = defaultUserService.lambdaQuery()
				.eq(DefaultUser::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(DefaultUser::getUserId, userId).list();

		if (CollectionUtils.isNotEmpty(users)) {
			List<String> collect = users.stream().map(DefaultUser::getDefaultId).collect(Collectors.toList());
			userIds = userIds.stream().filter(u -> !collect.contains(u)).collect(Collectors.toList());
		}
		if (CollectionUtils.isNotEmpty(userIds)) {
			List<DefaultUser> list = new ArrayList<>();
			Date date = new Date();
			for (String id : userIds) {
				DefaultUser defaultUser = new DefaultUser();
				defaultUser.setDefaultId(id).setUserId(userId).setDelFlag(0)
						.setCreateTime(date).setUpdateTime(date);
				list.add(defaultUser);
			}
			boolean saveBatch = this.defaultUserService.saveBatch(list);
			if (!saveBatch) {
				Result.error("加入默认人员失败");
			}
		}
		return Result.ok();
	}



	/**
	 * 添加常用人员
	 *
	 * @param request 请求
	 * @return {@link Result}<{@link List}<{@link SysFileRole}>>
	 */
	@AutoLog(value = "文档权限表-获取常用人员列表")
	@ApiOperation(value = "文档权限表-获取常用人员列表", notes = "文档权限表-获取常用人员列表")
	@GetMapping(value = "/listDefault")
	public Result<List<DefaultUser>> listDefault(HttpServletRequest request) {

		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String userId = loginUser.getId();

		List<DefaultUser> users = defaultUserService.listDefault(userId);

		return Result.ok(users);
	}


}
