package com.aiurt.modules.modeler.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.common.constant.FlowVariableConstant;
import com.aiurt.modules.common.enums.SystemVariableEnum;
import com.aiurt.modules.modeler.dto.ActCustomVariableDTO;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	public Result<List<ActCustomVariable>> queryPageList(@RequestParam(value = "modelId",required = false) String modelId,  @RequestParam(value = "variableType")Integer variableType) {

		if (StrUtil.isBlank(modelId)) {
			throw new AiurtBootException("缺失请求参数！");
		}
		LambdaQueryWrapper<ActCustomVariable> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ActCustomVariable::getModelId, modelId).eq(ActCustomVariable::getVariableType,
				variableType);
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
	public Result<ActCustomVariable> add(@Valid @RequestBody ActCustomVariable actCustomVariable) {
		// todo 系统字段
		actCustomVariableService.save(actCustomVariable);
		return Result.OK(actCustomVariable);
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
	public Result<ActCustomVariable> edit(@RequestBody ActCustomVariable actCustomVariable) {
		actCustomVariableService.updateById(actCustomVariable);
		return Result.OK(actCustomVariable);
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


	@ApiOperation(value = "流程变量唯一标识校验")
	@GetMapping(value = "/checkOnly")
	public Result<?> checkOnly(@RequestParam(value = "modelId") String modelId, @RequestParam(value = "variableName") String variableName) {
		LambdaQueryWrapper<ActCustomVariable> wrapper = new LambdaQueryWrapper();
		wrapper.eq(ActCustomVariable::getModelId, modelId).eq(ActCustomVariable::getVariableName, variableName);
		long count = actCustomVariableService.count(wrapper);
		if (count >= 1) {
			throw new AiurtBootException("流程标识已存在，请重新填写!");
		}
		return Result.OK();
	}

	@ApiOperation(value = "流转条件名称")
	@GetMapping(value = "/selectConditionVar")
	public Result<List<ActCustomVariableDTO>> selectConditionVar(@RequestParam(value = "modelId", required = true) String modelId) {
		LambdaQueryWrapper<ActCustomVariable> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ActCustomVariable::getModelId, modelId).eq(ActCustomVariable::getVariableType,
				FlowVariableConstant.VARIABLE_TYPE_1);
		List<ActCustomVariable> list = actCustomVariableService.list(wrapper);

		if (Objects.isNull(list)) {
			list = new ArrayList<>();
		}

		List<ActCustomVariableDTO> resultList = list.stream().map(variable -> {
			ActCustomVariableDTO variableDTO = new ActCustomVariableDTO();
			variableDTO.setValue(variable.getVariableName());
			variableDTO.setLabel(variable.getShowName());
			return variableDTO;
		}).collect(Collectors.toList());
		// 构建系统字段
		SystemVariableEnum[] variableEnums = SystemVariableEnum.values();
		for (int i = 0; i < variableEnums.length; i++) {
			SystemVariableEnum variableEnum = variableEnums[i];
			ActCustomVariableDTO variableDTO = new ActCustomVariableDTO();
			variableDTO.setValue(variableEnum.getCode());
			variableDTO.setLabel(variableEnum.getName());
			resultList.add(variableDTO);
		}
		return Result.OK(resultList);
	}


}
