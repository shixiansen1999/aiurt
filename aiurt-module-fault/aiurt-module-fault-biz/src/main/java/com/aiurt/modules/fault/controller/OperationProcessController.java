package com.aiurt.modules.fault.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.fault.entity.OperationProcess;
import com.aiurt.modules.fault.service.IOperationProcessService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 故障操作日志
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Api(tags="故障操作日志")
@RestController
@RequestMapping("/fault/operationProcess")
@Slf4j
public class OperationProcessController extends BaseController<OperationProcess, IOperationProcessService> {

	@Autowired
	private IOperationProcessService operationProcessService;

	/**
	 * 分页列表查询
	 *
	 * @param operationProcess
	 * @return
	 */
	@AutoLog(value = "故障操作日志查询")
	@ApiOperation(value="故障操作日志-分页列表查询", notes="故障操作日志-分页列表查询")
	@GetMapping(value = "/list")
	public Result<List<OperationProcess>> queryPageList(OperationProcess operationProcess) {
		LambdaQueryWrapper<OperationProcess> queryWrapper = new LambdaQueryWrapper<>();

		queryWrapper.eq(OperationProcess::getProcessCode, operationProcess.getFaultCode());
		List<OperationProcess> operationProcesses = operationProcessService.getBaseMapper().selectList(queryWrapper);

		//todo 人员姓名处理

		return Result.OK(operationProcesses);
	}

}
