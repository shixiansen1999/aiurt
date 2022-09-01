package com.aiurt.modules.modeler.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.modeler.entity.ActCustomVariable;
import com.aiurt.modules.modeler.service.IActCustomVariableService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

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
	 * @param modelId 流程模板id
	 * @param variableType 变量类型
	 * @return
	 */
	@ApiOperation(value="流程变量查询", notes="流程变量")
	@GetMapping(value = "/list")
	@ApiImplicitParams({
			@ApiImplicitParam(dataTypeClass = String.class, name = "modelId", value = "流程模板id", required = true, paramType = "query"),
			@ApiImplicitParam(dataTypeClass = Integer.class, name = "variableType", value = "变量类型（1：变量，0：状态）", required = true, paramType = "query")
	})
	public Result<List<ActCustomVariable>> queryPageList(@RequestParam(value = "modelId") String modelId,  @RequestParam(value = "variableType")Integer variableType) {
		LambdaQueryWrapper<ActCustomVariable> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ActCustomVariable::getModelId, modelId).eq(ActCustomVariable::getVariableType,
				variableType);
		// 需要默认添加两个变量
		//
		List<ActCustomVariable> list = actCustomVariableService.list(wrapper);
		return Result.OK(list);
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
	public Result<String> add(@Valid @RequestBody ActCustomVariable actCustomVariable) {
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
