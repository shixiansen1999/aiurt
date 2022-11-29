package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.plan.entity.EmergencyPlanRecordAtt;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.service.IEmergencyPlanRecordAttService;

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
 * @Description: emergency_plan_record_att
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_plan_record_att")
@RestController
@RequestMapping("/emergency/emergencyPlanRecordAtt")
@Slf4j
public class EmergencyPlanRecordAttController extends BaseController<EmergencyPlanRecordAtt, IEmergencyPlanRecordAttService> {
	@Autowired
	private IEmergencyPlanRecordAttService emergencyPlanRecordAttService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanRecordAtt
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_plan_record_att-分页列表查询")
	@ApiOperation(value="emergency_plan_record_att-分页列表查询", notes="emergency_plan_record_att-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlanRecordAtt>> queryPageList(EmergencyPlanRecordAtt emergencyPlanRecordAtt,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlanRecordAtt> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanRecordAtt, req.getParameterMap());
		Page<EmergencyPlanRecordAtt> page = new Page<EmergencyPlanRecordAtt>(pageNo, pageSize);
		IPage<EmergencyPlanRecordAtt> pageList = emergencyPlanRecordAttService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyPlanRecordAtt
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_att-添加")
	@ApiOperation(value="emergency_plan_record_att-添加", notes="emergency_plan_record_att-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyPlanRecordAtt emergencyPlanRecordAtt) {
		emergencyPlanRecordAttService.save(emergencyPlanRecordAtt);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyPlanRecordAtt
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_att-编辑")
	@ApiOperation(value="emergency_plan_record_att-编辑", notes="emergency_plan_record_att-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyPlanRecordAtt emergencyPlanRecordAtt) {
		emergencyPlanRecordAttService.updateById(emergencyPlanRecordAtt);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_att-通过id删除")
	@ApiOperation(value="emergency_plan_record_att-通过id删除", notes="emergency_plan_record_att-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanRecordAttService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_att-批量删除")
	@ApiOperation(value="emergency_plan_record_att-批量删除", notes="emergency_plan_record_att-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanRecordAttService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_plan_record_att-通过id查询")
	@ApiOperation(value="emergency_plan_record_att-通过id查询", notes="emergency_plan_record_att-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyPlanRecordAtt> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyPlanRecordAtt emergencyPlanRecordAtt = emergencyPlanRecordAttService.getById(id);
		if(emergencyPlanRecordAtt==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyPlanRecordAtt);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlanRecordAtt
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlanRecordAtt emergencyPlanRecordAtt) {
        return super.exportXls(request, emergencyPlanRecordAtt, EmergencyPlanRecordAtt.class, "emergency_plan_record_att");
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
        return super.importExcel(request, response, EmergencyPlanRecordAtt.class);
    }

}
