package com.aiurt.modules.param.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.param.dto.ParamTypeTreeDTO;
import com.aiurt.modules.param.entity.SysParamType;
import com.aiurt.modules.param.service.ISysParamTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 系统参数分类树
 * @Author: aiurt
 * @Date:   2023-01-04
 * @Version: V1.0
 */
@Api(tags="系统参数分类树")
@RestController
@RequestMapping("/param/sysParamType")
@Slf4j
public class SysParamTypeController extends BaseController<SysParamType, ISysParamTypeService>{
	@Autowired
	private ISysParamTypeService sysParamTypeService;

	/**
	 * 分页列表查询
	 *
	 * @param sysParamType
	 * @return
	 */
	@ApiOperation(value="系统参数分类树", notes="系统参数分类树")
	@GetMapping(value = "/queryParamTypeTree")
	public Result<List<ParamTypeTreeDTO>> queryParamTypeTree(SysParamType sysParamType) {
		List<SysParamType> list = sysParamTypeService.list();

		List<ParamTypeTreeDTO> treeList = list.stream().map(entity -> {
			ParamTypeTreeDTO table = new ParamTypeTreeDTO();
			table.setValue(String.valueOf(entity.getId()));
			table.setId(String.valueOf(entity.getId()));
			table.setLabel(entity.getTypeName());
			table.setKey(String.valueOf(entity.getId()));
			table.setTitle(entity.getTypeName());
			table.setPid(StrUtil.isBlank(String.valueOf(entity.getPid())) ? "-9999" : String.valueOf(entity.getPid()));
			return table;
		}).collect(Collectors.toList());

		Map<String, ParamTypeTreeDTO> root = new LinkedHashMap<>();

		for (ParamTypeTreeDTO item : treeList) {
			ParamTypeTreeDTO parent = root.get(item.getPid());
			if (Objects.isNull(parent)) {
				parent = new ParamTypeTreeDTO();
				root.put(item.getPid(), parent);
			}
			ParamTypeTreeDTO table = root.get(item.getId());
			if (Objects.nonNull(table)) {
				item.setChildren(table.getChildren());
			}
			root.put(item.getValue(), item);
			parent.addChildren(item);
		}

		List<ParamTypeTreeDTO> resultList = new ArrayList<>();
		List<ParamTypeTreeDTO> collect = root.values().stream().filter(entity -> StrUtil.isBlank(entity.getPid())).collect(Collectors.toList());
		for (ParamTypeTreeDTO entity : collect) {
			resultList.addAll(CollectionUtil.isEmpty(entity.getChildren()) ? Collections.emptyList() : entity.getChildren());
		}
		return Result.OK(resultList);
	}



	/**
	 *   添加
	 *
	 * @param sysParamType
	 * @return
	 */
	@AutoLog(value = "系统参数分类树-添加")
	@ApiOperation(value="系统参数分类树-添加", notes="系统参数分类树-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SysParamType sysParamType) {
		sysParamTypeService.addSysParamType(sysParamType);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sysParamType
	 * @return
	 */
	@AutoLog(value = "系统参数分类树-编辑")
	@ApiOperation(value="系统参数分类树-编辑", notes="系统参数分类树-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SysParamType sysParamType) {
		sysParamTypeService.updateSysParamType(sysParamType);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "系统参数分类树-通过id删除")
	@ApiOperation(value="系统参数分类树-通过id删除", notes="系统参数分类树-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sysParamTypeService.deleteSysParamType(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "系统参数分类树-批量删除")
	@ApiOperation(value="系统参数分类树-批量删除", notes="系统参数分类树-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysParamTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="系统参数分类树-通过id查询", notes="系统参数分类树-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SysParamType> queryById(@RequestParam(name="id",required=true) String id) {
		SysParamType sysParamType = sysParamTypeService.getById(id);
		if(sysParamType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sysParamType);
	}

}
