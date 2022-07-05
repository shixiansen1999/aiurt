package com.aiurt.modules.fault.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
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
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Comparator;
import java.util.Date;
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
			}
			//process.setRoleName(loginUser.get());

			if (i+1< list.size()) {
				OperationProcess process2 = list.get(i + 1);
				long between = DateUtil.between(process2.getProcessTime(), process.getProcessTime(), DateUnit.MINUTE);
				long day = between / (24 * 60);
				long hours = between % (24 * 60) / 60;
				long min = between % (24 * 60) % 60;
				process.setProcessingTime(day+"天"+hours+"小时"+min + "分");
			}else {
				long between = DateUtil.between(new Date(), process.getProcessTime(), DateUnit.MINUTE);
				long day = between / (24 * 60);
				long hours = between % (24 * 60) / 60;
				long min = between % (24 * 60) % 60;
				process.setProcessingTime(day+"天"+hours+"小时"+min + "分");
			}

		}
		return Result.OK(list);
	}

	public static void main(String[] args) {
		long between = 1926;
		long day = between / (24 * 60);
		long hours = between % (24 * 60) / 60;
		long min = between % (24 * 60) %60;
		System.out.println(day);
	}

}
