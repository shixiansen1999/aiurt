package com.aiurt.modules.fault.controller;

import cn.hutool.core.date.BetweenFormater;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.fault.entity.OperationProcess;
import com.aiurt.modules.fault.service.IOperationProcessService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

	@Autowired
	private ISysBaseAPI sysBaseAPI;

	@Autowired
	private ISysParamAPI iSysParamAPI;

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

		queryWrapper.eq(OperationProcess::getFaultCode, operationProcess.getFaultCode());
		List<OperationProcess> operationProcessList = operationProcessService.getBaseMapper().selectList(queryWrapper);
		List<OperationProcess> list = operationProcessList.stream().
				sorted(Comparator.comparing(OperationProcess::getProcessTime)).collect(Collectors.toList());

		for (int i = 0; i < list.size(); i++) {
			OperationProcess process = list.get(i);
			LoginUser loginUser = sysBaseAPI.getUserByName(process.getProcessPerson());
			if (Objects.isNull(loginUser)) {
				loginUser = sysBaseAPI.getUserById(process.getProcessPerson());
			}
			if (Objects.nonNull(loginUser)) {
				process.setProcessPersonName(loginUser.getRealname());
				process.setRoleName(loginUser.getRoleNames());
			}

			if (i+1< list.size()) {
				OperationProcess process2 = list.get(i + 1);
				long between = DateUtil.between(process2.getProcessTime(), process.getProcessTime(), DateUnit.MS);
				dealTime(process, between);
			}
		}
		// 获取根据处理时间排序配置
		SysParamModel orderParam = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_OPERATION_ORDER);
		boolean b = ObjectUtil.isNotEmpty(orderParam) && "1".equals(orderParam.getValue());
		if (b) {
			list =  list.stream().sorted(Comparator.comparing(OperationProcess::getProcessTime)).collect(Collectors.toList());
		} else {
			list = list.stream().
					sorted(Comparator.comparing(OperationProcess::getProcessTime).reversed()).collect(Collectors.toList());
		}
		return Result.OK(list);
	}

	private void dealTime(OperationProcess process, long between) {

		process.setProcessingTime(DateUtil.formatBetween(between, BetweenFormater.Level.SECOND));

	}

}
