package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.plan.dto.EmergencyPlanDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanRecord;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.service.IEmergencyPlanRecordService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: emergency_plan_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_plan_record")
@RestController
@RequestMapping("/emergency/emergencyPlanRecord")
@Slf4j
public class EmergencyPlanRecordController extends BaseController<EmergencyPlanRecord, IEmergencyPlanRecordService> {
	@Autowired
	private IEmergencyPlanRecordService emergencyPlanRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_plan_record-分页列表查询")
	@ApiOperation(value="emergency_plan_record-分页列表查询", notes="emergency_plan_record-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlanRecord>> queryPageList(EmergencyPlanRecord emergencyPlanRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlanRecord> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanRecord, req.getParameterMap());
		Page<EmergencyPlanRecord> page = new Page<EmergencyPlanRecord>(pageNo, pageSize);
		IPage<EmergencyPlanRecord> pageList = emergencyPlanRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}


	 /**
	  * 应急预案启动记录保存新增
	  * @param emergencyPlanRecordDto
	  * @return
	  */
	 @AutoLog(value = "应急预案启动记录保存新增")
	 @ApiOperation(value="应急预案启动记录保存新增", notes="应急预案启动记录保存新增")
	 @PostMapping(value = "/add")
	 public Result<String> add(@RequestBody EmergencyPlanRecordDTO emergencyPlanRecordDto) {
		 String id = emergencyPlanRecordService.saveAndAdd(emergencyPlanRecordDto);
		 return Result.OK("保存成功!");
	 }


	 @AutoLog(value = "应急预案启动记录-编辑")
	 @ApiOperation(value="应急预案启动记录-编辑", notes="应急预案台账-编辑")
	 @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> edit(@RequestBody EmergencyPlanRecordDTO emergencyPlanRecordDto) {
		 emergencyPlanRecordService.edit(emergencyPlanRecordDto);
		 return Result.OK("编辑成功!");
	 }

	/**
	 *  编辑
	 *
	 * @param emergencyPlanRecord
	 * @return
	 */
//	@AutoLog(value = "emergency_plan_record-编辑")
//	@ApiOperation(value="emergency_plan_record-编辑", notes="emergency_plan_record-编辑")
//	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<String> edit(@RequestBody EmergencyPlanRecord emergencyPlanRecord) {
//		emergencyPlanRecordService.updateById(emergencyPlanRecord);
//		return Result.OK("编辑成功!");
//	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
//	@AutoLog(value = "emergency_plan_record-通过id删除")
//	@ApiOperation(value="emergency_plan_record-通过id删除", notes="emergency_plan_record-通过id删除")
//	@DeleteMapping(value = "/delete")
//	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
//		emergencyPlanRecordService.removeById(id);
//		return Result.OK("删除成功!");
//	}

	@AutoLog(value = "应急预案启动记录-通过id删除")
	@ApiOperation(value="应急预案启动记录-通过id删除", notes="应急预案启动-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanRecordService.delete(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record-批量删除")
	@ApiOperation(value="emergency_plan_record-批量删除", notes="emergency_plan_record-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_plan_record-通过id查询")
	@ApiOperation(value="emergency_plan_record-通过id查询", notes="emergency_plan_record-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyPlanRecord> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyPlanRecord emergencyPlanRecord = emergencyPlanRecordService.getById(id);
		if(emergencyPlanRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyPlanRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlanRecord
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlanRecord emergencyPlanRecord) {
        return super.exportXls(request, emergencyPlanRecord, EmergencyPlanRecord.class, "emergency_plan_record");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, EmergencyPlanRecord.class);
    }

}
