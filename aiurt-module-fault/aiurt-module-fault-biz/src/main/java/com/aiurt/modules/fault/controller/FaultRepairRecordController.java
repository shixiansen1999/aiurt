package com.aiurt.modules.fault.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.fault.service.IFaultRepairRecordService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 维修记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Api(tags="故障管理-维修记录")
@RestController
@RequestMapping("/fault/faultRepairRecord")
@Slf4j
public class FaultRepairRecordController extends BaseController<FaultRepairRecord, IFaultRepairRecordService> {
	@Autowired
	private IFaultRepairRecordService faultRepairRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param faultCode 故障编码
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "维修记录查询")
	@ApiOperation(value="维修记录-分页列表查询", notes="维修记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultRepairRecord>> queryPageList( @RequestParam(name="faultCode")String  faultCode,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		LambdaQueryWrapper<FaultRepairRecord> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(FaultRepairRecord::getFaultCode, faultCode).orderByDesc(FaultRepairRecord::getCreateTime);
		Page<FaultRepairRecord> page = new Page<>(pageNo, pageSize);
		IPage<FaultRepairRecord> pageList = faultRepairRecordService.page(page, wrapper);
		return Result.OK(pageList);
	}



}
