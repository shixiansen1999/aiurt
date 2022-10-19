package com.aiurt.modules.weeklyPlan.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.weeklyPlan.entity.BdTemplate;
import com.aiurt.modules.weeklyPlan.service.IBdTemplateService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 周计划表 Controller
 * @Author: Lai W.
 * @Version: V1.0
 */
@Api(tags="周计划模板表")
@RestController
@RequestMapping("/weeklyPlan/bdTemplate")
@Slf4j
public class BdTemplateController extends BaseController<BdTemplate, IBdTemplateService> {
	@Autowired
	private IBdTemplateService bdTemplateService;

	 /**
	  * 获取施工类型
	  */
	@AutoLog(value = "周计划模板-查询")
	@ApiOperation(value = "周计划模板-查询", notes = "周计划模板-查询")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = BdTemplate.class)})
	@GetMapping(value = "/queryAll")
	public Result<?> queryAll() {
		List<BdTemplate> list = bdTemplateService.queryAll();
		return Result.OK(list);
	}

	/**
	 * 添加
	 * @return
	 */
	@AutoLog(value = "周计划模板-添加")
	@ApiOperation(value = "周计划模板-添加", notes = "周计划模板-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdTemplate bdTemplate) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		bdTemplate.setUserId(sysUser.getId());
		bdTemplateService.save(bdTemplate);
		return Result.OK("添加成功！");
	}

	/**
	 * 修改
	 * @return
	 */
	@AutoLog(value = "周计划模板-修改")
	@ApiOperation(value = "周计划模板-修改", notes = "周计划模板-修改")
	@PostMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdTemplate bdTemplate) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		bdTemplate.setUserId(sysUser.getId());
		bdTemplateService.updateById(bdTemplate);
		return Result.OK("修改成功！");
	}

	/**
	 * 删除
	 * @return
	 */
	@AutoLog(value = "周计划模板-删除")
	@ApiOperation(value = "周计划模板-删除", notes = "周计划模板-删除")
	@PostMapping(value = "/remove")
	public Result<?> remove(@RequestBody BdTemplate bdTemplate) {
		bdTemplateService.removeById(bdTemplate.getId());
		return Result.OK("删除成功！");
	}
}
