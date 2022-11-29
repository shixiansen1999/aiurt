package com.aiurt.boot.team.controller;

import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.team.entity.EmergencyTeam;
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
 * @Description: emergency_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_team")
@RestController
@RequestMapping("/emergency/emergencyTeam")
@Slf4j
public class EmergencyTeamController extends BaseController<EmergencyTeam, IEmergencyTeamService> {
	@Autowired
	private IEmergencyTeamService emergencyTeamService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyTeam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_team-分页列表查询")
	@ApiOperation(value="emergency_team-分页列表查询", notes="emergency_team-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyTeam>> queryPageList(EmergencyTeam emergencyTeam,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyTeam> queryWrapper = QueryGenerator.initQueryWrapper(emergencyTeam, req.getParameterMap());
		Page<EmergencyTeam> page = new Page<EmergencyTeam>(pageNo, pageSize);
		IPage<EmergencyTeam> pageList = emergencyTeamService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyTeam
	 * @return
	 */
	@AutoLog(value = "emergency_team-添加")
	@ApiOperation(value="emergency_team-添加", notes="emergency_team-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyTeam emergencyTeam) {
		emergencyTeamService.save(emergencyTeam);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyTeam
	 * @return
	 */
	@AutoLog(value = "emergency_team-编辑")
	@ApiOperation(value="emergency_team-编辑", notes="emergency_team-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyTeam emergencyTeam) {
		emergencyTeamService.updateById(emergencyTeam);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_team-通过id删除")
	@ApiOperation(value="emergency_team-通过id删除", notes="emergency_team-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyTeamService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_team-批量删除")
	@ApiOperation(value="emergency_team-批量删除", notes="emergency_team-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyTeamService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_team-通过id查询")
	@ApiOperation(value="emergency_team-通过id查询", notes="emergency_team-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyTeam> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyTeam emergencyTeam = emergencyTeamService.getById(id);
		if(emergencyTeam==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyTeam);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyTeam
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyTeam emergencyTeam) {
        return super.exportXls(request, emergencyTeam, EmergencyTeam.class, "emergency_team");
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
        return super.importExcel(request, response, EmergencyTeam.class);
    }

}
