package com.aiurt.boot.team.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.team.entity.EmergencyTrainingTeam;
import com.aiurt.boot.team.service.IEmergencyTrainingTeamService;

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
 * @Description: emergency_training_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_training_team")
@RestController
@RequestMapping("/emergency/emergencyTrainingTeam")
@Slf4j
public class EmergencyTrainingTeamController extends BaseController<EmergencyTrainingTeam, IEmergencyTrainingTeamService> {
	@Autowired
	private IEmergencyTrainingTeamService emergencyTrainingTeamService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyTrainingTeam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_training_team-分页列表查询")
	@ApiOperation(value="emergency_training_team-分页列表查询", notes="emergency_training_team-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyTrainingTeam>> queryPageList(EmergencyTrainingTeam emergencyTrainingTeam,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyTrainingTeam> queryWrapper = QueryGenerator.initQueryWrapper(emergencyTrainingTeam, req.getParameterMap());
		Page<EmergencyTrainingTeam> page = new Page<EmergencyTrainingTeam>(pageNo, pageSize);
		IPage<EmergencyTrainingTeam> pageList = emergencyTrainingTeamService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyTrainingTeam
	 * @return
	 */
	@AutoLog(value = "emergency_training_team-添加")
	@ApiOperation(value="emergency_training_team-添加", notes="emergency_training_team-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyTrainingTeam emergencyTrainingTeam) {
		emergencyTrainingTeamService.save(emergencyTrainingTeam);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyTrainingTeam
	 * @return
	 */
	@AutoLog(value = "emergency_training_team-编辑")
	@ApiOperation(value="emergency_training_team-编辑", notes="emergency_training_team-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyTrainingTeam emergencyTrainingTeam) {
		emergencyTrainingTeamService.updateById(emergencyTrainingTeam);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_training_team-通过id删除")
	@ApiOperation(value="emergency_training_team-通过id删除", notes="emergency_training_team-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyTrainingTeamService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_training_team-批量删除")
	@ApiOperation(value="emergency_training_team-批量删除", notes="emergency_training_team-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyTrainingTeamService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_training_team-通过id查询")
	@ApiOperation(value="emergency_training_team-通过id查询", notes="emergency_training_team-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyTrainingTeam> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyTrainingTeam emergencyTrainingTeam = emergencyTrainingTeamService.getById(id);
		if(emergencyTrainingTeam==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyTrainingTeam);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyTrainingTeam
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyTrainingTeam emergencyTrainingTeam) {
        return super.exportXls(request, emergencyTrainingTeam, EmergencyTrainingTeam.class, "emergency_training_team");
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
        return super.importExcel(request, response, EmergencyTrainingTeam.class);
    }

}
