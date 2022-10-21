package com.aiurt.modules.modeler.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: flowable流程模板定义信息
 * @Author: aiurt
 * @Date:   2022-07-08
 * @Version: V1.0
 */
@Api(tags="flowable流程模板定义信息")
@RestController
@RequestMapping("/modeler/actCustomModelInfo")
@Slf4j
public class ActCustomModelInfoController extends BaseController<ActCustomModelInfo, IActCustomModelInfoService> {

	@Autowired
	private IActCustomModelInfoService actCustomModelInfoService;

	@Lazy
	@Autowired
	private FlowApiService flowApiService;

	/**
	 * 分页列表查询
	 *
	 * @param actCustomModelInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询流程模板")
	@ApiOperation(value="flowable流程模板定义信息-分页列表查询", notes="flowable流程模板定义信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ActCustomModelInfo>> queryPageList(ActCustomModelInfo actCustomModelInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ActCustomModelInfo> queryWrapper = QueryGenerator.initQueryWrapper(actCustomModelInfo, req.getParameterMap());
		Page<ActCustomModelInfo> page = new Page<>(pageNo, pageSize);
		IPage<ActCustomModelInfo> pageList = actCustomModelInfoService.page(page, queryWrapper);

		pageList.getRecords().stream().forEach(modeInfo->{

			try {
				if (StrUtil.equalsIgnoreCase("3", String.valueOf(modeInfo.getStatus()))) {
					TaskInfoDTO taskInfoDTO = flowApiService.viewInitialTaskInfo(modeInfo.getModelKey());
					modeInfo.setRouterName(modeInfo.getBusinessUrl());
					if (Objects.nonNull(taskInfoDTO)) {
						String routerName = taskInfoDTO.getRouterName();
						if (StrUtil.isNotBlank(routerName)) {
							modeInfo.setRouterName(routerName);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param actCustomModelInfo
	 * @return
	 */
	@AutoLog(value = "flowable流程模板定义信息-添加")
	@ApiOperation(value="flowable流程模板定义信息-添加", notes="flowable流程模板定义信息-添加")
	@PostMapping(value = "/add")
	public Result<ActCustomModelInfo> add(@RequestBody ActCustomModelInfo actCustomModelInfo) {
		ActCustomModelInfo a = actCustomModelInfoService.add(actCustomModelInfo);
		return Result.OK("添加成功", a);
	}

	/**
	 *  编辑
	 *
	 * @param actCustomModelInfo
	 * @return
	 */
	@AutoLog(value = "flowable流程模板定义信息-编辑")
	@ApiOperation(value="flowable流程模板定义信息-编辑", notes="flowable流程模板定义信息-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ActCustomModelInfo actCustomModelInfo) {
		actCustomModelInfoService.updateById(actCustomModelInfo);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "flowable流程模板定义信息-通过id删除")
	@ApiOperation(value="flowable流程模板定义信息-通过id删除", notes="flowable流程模板定义信息-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		List<String> list = new ArrayList<>();
		list.add(id);
		actCustomModelInfoService.deleteById(list);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "flowable流程模板定义信息-批量删除")
	@ApiOperation(value="flowable流程模板定义信息-批量删除", notes="flowable流程模板定义信息-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.actCustomModelInfoService.deleteById(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="flowable流程模板定义信息-通过id查询", notes="flowable流程模板定义信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ActCustomModelInfo> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomModelInfo actCustomModelInfo = actCustomModelInfoService.getById(id);
		return Result.OK(actCustomModelInfo);
	}


	/**
	 * 查询单个 - 通过modelId
	 *
	 * @param modelId
	 * @return
	 */
	@AutoLog(value = "flowable流程模板定义信息-通过id查询")
	@ApiOperation(value="flowable流程模板定义信息-通过id查询", notes="flowable流程模板定义信息-通过id查询")
	@GetMapping(value = "/queryByModelId")
	public Result<ActCustomModelInfo> queryByModelId(@RequestParam(name="modelId",required=true) String modelId) {
		ActCustomModelInfo actCustomModelInfo = actCustomModelInfoService.queryByModelId(modelId);
		return Result.OK(actCustomModelInfo);
	}

	@AutoLog(value = "禁用流程")
	@ApiOperation(value="禁用流程", notes="禁用流程")
	@PutMapping(value = "/forbiddenModel")
	public Result<?> forbiddenModel(@RequestParam(value = "modelId") String modelId) {
		return Result.OK();
	}

}
