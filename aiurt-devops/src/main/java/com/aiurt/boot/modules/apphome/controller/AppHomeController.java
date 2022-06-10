package com.aiurt.boot.modules.apphome.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.apphome.entity.UserTask;
import com.aiurt.boot.modules.apphome.param.HomeListParam;
import com.aiurt.boot.modules.apphome.service.AppHomeService;
import com.aiurt.boot.modules.apphome.vo.AppHomeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @description: AppHomeController
 * @author: Mr.zhao
 * @date: 2021/10/1 10:14
 */
@Slf4j
@RestController
@RequestMapping("/appHome")
@Api(tags = "app主页")
public class AppHomeController {

	@Resource
	private AppHomeService appHomeService;
	/*
	@AutoLog(value = "查询-待办/已完成")
	@ApiOperation(value = "查询-待办/已完成", notes = "查询-待办/已完成")
	@PostMapping(value = "/getHomeList")
	public Result<HomeVO> getHomeList(HttpServletRequest req, @Validated HomeListParam param) {
		return appHomeService.getHomeList(req,param);
	}*/

	@AutoLog(value = "查询-待办/已完成")
	@ApiOperation(value = "查询-待办/已完成", notes = "查询-待办/已完成")
	@PostMapping(value = "/getHomeTaskList")
	public Result<AppHomeVO> getHomeTaskList(HttpServletRequest req, @Validated HomeListParam param ,
	                                         @PageableDefault(page = 1,size = 10) Pageable pageable) {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		param.setUserId(loginUser.getId());
		AppHomeVO vo = new AppHomeVO();

		//查询分页数据
		IPage<UserTask> page = appHomeService.getHomeTaskList(req, param, pageable);
		vo.setPage(page);

		vo = appHomeService.getHomeCount(req, param,vo);


		return Result.ok(vo);
	}

}
