package com.aiurt.modules.modeler.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.modeler.entity.ActCustomVariable;
import com.aiurt.modules.modeler.service.IActCustomVariableService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

 /**
 * @Description: 流程变量
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Api(tags="流程变量")
@RestController
@RequestMapping("/modeler/actCustomVariable")
@Slf4j
public class ActCustomVariableController extends BaseController<ActCustomVariable, IActCustomVariableService> {
	@Autowired
	private IActCustomVariableService actCustomVariableService;

	/**
	 * 分页列表查询
	 *
	 * @param actCustomVariable
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="流程变量-分页列表查询", notes="流程变量-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ActCustomVariable>> queryPageList(ActCustomVariable actCustomVariable,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ActCustomVariable> queryWrapper = QueryGenerator.initQueryWrapper(actCustomVariable, req.getParameterMap());
		Page<ActCustomVariable> page = new Page<>(pageNo, pageSize);
		// 根据mo
		IPage<ActCustomVariable> pageList = actCustomVariableService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param actCustomVariable
	 * @return
	 */
	@AutoLog(value = "流程变量-添加")
	@ApiOperation(value="流程变量-添加", notes="流程变量-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ActCustomVariable actCustomVariable) {
		actCustomVariableService.save(actCustomVariable);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param actCustomVariable
	 * @return
	 */
	@AutoLog(value = "流程变量-编辑")
	@ApiOperation(value="流程变量-编辑", notes="流程变量-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ActCustomVariable actCustomVariable) {
		actCustomVariableService.updateById(actCustomVariable);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "流程变量-通过id删除")
	@ApiOperation(value="流程变量-通过id删除", notes="流程变量-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		actCustomVariableService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "流程变量-批量删除")
	@ApiOperation(value="流程变量-批量删除", notes="流程变量-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.actCustomVariableService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}



}
