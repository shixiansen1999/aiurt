package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.plan.entity.EmergencyPlanRecordTeam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.service.IEmergencyPlanRecordTeamService;

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
 * @Description: emergency_plan_record_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_plan_record_team")
@RestController
@RequestMapping("/emergency/emergencyPlanRecordTeam")
@Slf4j
public class EmergencyPlanRecordTeamController extends BaseController<EmergencyPlanRecordTeam, IEmergencyPlanRecordTeamService> {
	@Autowired
	private IEmergencyPlanRecordTeamService emergencyPlanRecordTeamService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanRecordTeam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="emergency_plan_record_team-分页列表查询", notes="emergency_plan_record_team-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlanRecordTeam>> queryPageList(EmergencyPlanRecordTeam emergencyPlanRecordTeam,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlanRecordTeam> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanRecordTeam, req.getParameterMap());
		Page<EmergencyPlanRecordTeam> page = new Page<EmergencyPlanRecordTeam>(pageNo, pageSize);
		IPage<EmergencyPlanRecordTeam> pageList = emergencyPlanRecordTeamService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyPlanRecordTeam
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_team-添加")
	@ApiOperation(value="emergency_plan_record_team-添加", notes="emergency_plan_record_team-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyPlanRecordTeam emergencyPlanRecordTeam) {
		emergencyPlanRecordTeamService.save(emergencyPlanRecordTeam);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyPlanRecordTeam
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_team-编辑")
	@ApiOperation(value="emergency_plan_record_team-编辑", notes="emergency_plan_record_team-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyPlanRecordTeam emergencyPlanRecordTeam) {
		emergencyPlanRecordTeamService.updateById(emergencyPlanRecordTeam);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_team-通过id删除")
	@ApiOperation(value="emergency_plan_record_team-通过id删除", notes="emergency_plan_record_team-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanRecordTeamService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_record_team-批量删除")
	@ApiOperation(value="emergency_plan_record_team-批量删除", notes="emergency_plan_record_team-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanRecordTeamService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="emergency_plan_record_team-通过id查询", notes="emergency_plan_record_team-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyPlanRecordTeam> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyPlanRecordTeam emergencyPlanRecordTeam = emergencyPlanRecordTeamService.getById(id);
		if(emergencyPlanRecordTeam==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyPlanRecordTeam);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlanRecordTeam
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlanRecordTeam emergencyPlanRecordTeam) {
        return super.exportXls(request, emergencyPlanRecordTeam, EmergencyPlanRecordTeam.class, "emergency_plan_record_team");
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
        return super.importExcel(request, response, EmergencyPlanRecordTeam.class);
    }

}
