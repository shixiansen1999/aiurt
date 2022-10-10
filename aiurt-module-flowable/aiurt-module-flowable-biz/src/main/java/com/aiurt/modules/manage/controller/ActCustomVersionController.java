package com.aiurt.modules.manage.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.aiurt.modules.manage.service.IActCustomVersionService;
import com.aiurt.modules.modeler.service.IFlowableBpmnService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

 /**
 * @Description: 版本管理
 * @Author: aiurt
 * @Date:   2022-07-15
 * @Version: V1.0
 */
@Api(tags="版本管理")
@RestController
@RequestMapping("/manage/actCustomVersion")
@Slf4j
public class ActCustomVersionController extends BaseController<ActCustomVersion, IActCustomVersionService> {


	@Autowired
	private IActCustomVersionService actCustomVersionService;

	@Autowired
	private IFlowableBpmnService flowableBpmnService;

	/**
	 * 分页列表查询-根据modelId查询历史版本信息
	 *
	 * @param modelId 流程模板id
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value="根据modelId查询历史版本信息", notes="根据modelId查询历史版本信息")
	@GetMapping(value = "/list")
	public Result<IPage<ActCustomVersion>> queryPageList(@RequestParam(name = "modelId") String modelId,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		LambdaQueryWrapper<ActCustomVersion> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ActCustomVersion::getModelId, modelId).orderByDesc(ActCustomVersion::getDeployTime);
		Page<ActCustomVersion> page = new Page<>(pageNo, pageSize);
		IPage<ActCustomVersion> pageList = actCustomVersionService.page(page, wrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param actCustomVersion
	 * @return
	 */
	@AutoLog(value = "版本管理-添加")
	@ApiOperation(value="版本管理-添加", notes="版本管理-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ActCustomVersion actCustomVersion) {
		actCustomVersionService.save(actCustomVersion);
		return Result.OK("添加成功！");
	}

	/**
	 *  挂起
	 *
	 * @param actCustomVersion
	 * @return
	 */
	@AutoLog(value = "版本管理-挂起")
	@ApiOperation(value="版本管理-挂起, 只需要传id", notes="版本管理-挂起,只需要传id")
	@RequestMapping(value = "/suspendFlowProcess", method = {RequestMethod.PUT})
	public Result<String> suspendFlowProcess(@RequestBody ActCustomVersion actCustomVersion) {
		actCustomVersionService.suspendFlowProcess(actCustomVersion);
		return Result.OK("挂起成功!");
	}

	 /**
	  *  激活
	  *
	  * @param actCustomVersion
	  * @return
	  */
	 @AutoLog(value = "流程版本管理-激活")
	 @ApiOperation(value="版本管理-激活, 只需要传id", notes="版本管理-激活,只需要传id")
	 @RequestMapping(value = "/activeFlowProcess", method = {RequestMethod.PUT})
	 public Result<String> activeFlowProcess(@RequestBody ActCustomVersion actCustomVersion) {
		 actCustomVersionService.activeFlowProcess(actCustomVersion);
		 return Result.OK("激活成功!");
	 }

	 @AutoLog(value = "设置主版本")
	 @RequestMapping(value = "/updateMainVersion", method = {RequestMethod.PUT})
	 public Result<?> updateMainVersion(@RequestBody ActCustomVersion actCustomVersion) {
		 actCustomVersionService.updateMainVersion(actCustomVersion);
		 return Result.OK("设置主版本成功");
	 }

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "版本管理-通过id删除")
	@ApiOperation(value="版本管理-通过id删除", notes="版本管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id", required=true) String id) {
		actCustomVersionService.removeById(id);
		return Result.OK("删除成功!");
	}

	 /**
	  * 获取指定流程定义的流程图。
	  *
	  * @param processDefinitionId 流程定义Id。
	  * @return 流程图。
	  */
	 @GetMapping("/viewProcessBpmn")
	 @ApiOperation("流程图")
	 public Result<String> viewProcessBpmn(@RequestParam String processDefinitionId) throws IOException {
		 BpmnXMLConverter converter = new BpmnXMLConverter();
		 BpmnModel bpmnModel = flowableBpmnService.getBpmnModelByDefinitionId(processDefinitionId);
		 byte[] xmlBytes = converter.convertToXML(bpmnModel);
		 InputStream in = new ByteArrayInputStream(xmlBytes);
		 return Result.OK(StreamUtils.copyToString(in, StandardCharsets.UTF_8));
	 }


}
