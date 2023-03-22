package com.aiurt.boot.plan.controller;

import com.aiurt.boot.plan.entity.EmergencyPlanTeam;
import com.aiurt.boot.plan.service.IEmergencyPlanTeamService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
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
 * @Description: emergency_plan_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="应急预案应急队伍关联")
@RestController
@RequestMapping("/emergency/emergencyPlanTeam")
@Slf4j
public class EmergencyPlanTeamController extends BaseController<EmergencyPlanTeam, IEmergencyPlanTeamService> {
	@Autowired
	private IEmergencyPlanTeamService emergencyPlanTeamService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyPlanTeam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="emergency_plan_team-分页列表查询", notes="emergency_plan_team-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyPlanTeam>> queryPageList(EmergencyPlanTeam emergencyPlanTeam,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyPlanTeam> queryWrapper = QueryGenerator.initQueryWrapper(emergencyPlanTeam, req.getParameterMap());
		Page<EmergencyPlanTeam> page = new Page<EmergencyPlanTeam>(pageNo, pageSize);
		IPage<EmergencyPlanTeam> pageList = emergencyPlanTeamService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyPlanTeam
	 * @return
	 */
	@AutoLog(value = "emergency_plan_team-添加")
	@ApiOperation(value="emergency_plan_team-添加", notes="emergency_plan_team-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyPlanTeam emergencyPlanTeam) {
		emergencyPlanTeamService.save(emergencyPlanTeam);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyPlanTeam
	 * @return
	 */
	@AutoLog(value = "emergency_plan_team-编辑")
	@ApiOperation(value="emergency_plan_team-编辑", notes="emergency_plan_team-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyPlanTeam emergencyPlanTeam) {
		emergencyPlanTeamService.updateById(emergencyPlanTeam);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_plan_team-通过id删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "")
	@ApiOperation(value="emergency_plan_team-通过id删除", notes="emergency_plan_team-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyPlanTeamService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_plan_team-批量删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "")
	@ApiOperation(value="emergency_plan_team-批量删除", notes="emergency_plan_team-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyPlanTeamService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="emergency_plan_team-通过id查询", notes="emergency_plan_team-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyPlanTeam> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyPlanTeam emergencyPlanTeam = emergencyPlanTeamService.getById(id);
		if(emergencyPlanTeam==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyPlanTeam);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyPlanTeam
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyPlanTeam emergencyPlanTeam) {
        return super.exportXls(request, emergencyPlanTeam, EmergencyPlanTeam.class, "emergency_plan_team");
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
        return super.importExcel(request, response, EmergencyPlanTeam.class);
    }

}
