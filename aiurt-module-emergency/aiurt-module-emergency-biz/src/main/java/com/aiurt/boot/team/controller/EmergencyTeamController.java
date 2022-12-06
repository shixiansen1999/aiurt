package com.aiurt.boot.team.controller;

import com.aiurt.boot.team.dto.EmergencyTeamDTO;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: emergency_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="应急队伍台账")
@RestController
@RequestMapping("/emergency/emergencyTeam")
@Slf4j
public class EmergencyTeamController extends BaseController<EmergencyTeam, IEmergencyTeamService> {


	@Autowired
	private IEmergencyTeamService emergencyTeamService;

	@Autowired
	private ISysBaseAPI iSysBaseAPI;
	/**
	 * 分页列表查询
	 *
	 * @param emergencyTeamDTO
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="应急队伍台账-分页列表查询", notes="emergency_team-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyTeam>> queryPageList(EmergencyTeamDTO emergencyTeamDTO,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													  HttpServletRequest req) {
		IPage<EmergencyTeam> pageList = emergencyTeamService.queryPageList(emergencyTeamDTO,pageNo,pageSize);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyTeam
	 * @return
	 */
	@AutoLog(value = "应急队伍台账-添加")
	@ApiOperation(value="应急队伍台账-添加", notes="应急队伍台账-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyTeam emergencyTeam) {
		return emergencyTeamService.add(emergencyTeam);
	}

	/**
	 *  编辑
	 *
	 * @param emergencyTeam
	 * @return
	 */
	@AutoLog(value = "应急队伍台账-编辑")
	@ApiOperation(value="应急队伍台账-编辑", notes="应急队伍台账-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT})
	public Result<String> edit(@RequestBody EmergencyTeam emergencyTeam) {
		return emergencyTeamService.edit(emergencyTeam);

	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "应急队伍台账-通过id删除")
	@ApiOperation(value="应急队伍台账-通过id删除", notes="应急队伍台账-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		EmergencyTeam emergencyTeam = emergencyTeamService.getById(id);
		if(emergencyTeam==null) {
			return Result.error("未找到对应数据");
		}
		emergencyTeamService.delete(emergencyTeam);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "应急队伍台账-批量删除")
	@ApiOperation(value="应急队伍台账-批量删除", notes="应急队伍台账-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> list = Arrays.asList(ids.split(","));
		for (String s : list) {
			EmergencyTeam emergencyTeam = emergencyTeamService.getById(s);
			if(emergencyTeam==null) {
				return Result.error("未找到对应数据");
			}
			emergencyTeamService.delete(emergencyTeam);
		}
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="应急队伍台账-通过id查询", notes="应急队伍台账-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyTeam> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyTeam emergencyTeam = emergencyTeamService.getById(id);
		if(emergencyTeam==null) {
			return Result.error("未找到对应数据");
		}
		EmergencyTeam team = emergencyTeamService.getCrew(emergencyTeam);
		return Result.OK(team);
	}

	/**
	 * 通过id查询训练记录
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="应急队伍台账-训练记录", notes="应急队伍台账-训练记录")
	@GetMapping(value = "/getTrainingRecordById")
	public Result<EmergencyTeam> getTrainingRecordById(@RequestParam(name="id",required=true) String id) {
		return emergencyTeamService.getTrainingRecordById(id);
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

	/**
	 * 根据部门查找应急队伍
	 *
	 * @param orgCode
	 * @return
	 */
	@ApiOperation(value="应急队伍台账-根据部门查找应急队伍", notes="应急队伍台账-根据部门查找应急队伍")
	@GetMapping(value = "/getTeamByCode")
	public Result<List<EmergencyTeam>> getTeamByCode(@RequestParam(name="orgCode",required=false) String orgCode) {
		return emergencyTeamService.getTeamByCode(orgCode);
	}

	/**
	 * 根据专业权限查找应急队伍
	 *
	 * @param
	 * @return
	 */
	@ApiOperation(value="应急队伍台账-根据专业权限查找应急队伍", notes="应急队伍台账-根据专业权限查找应急队伍")
	@GetMapping(value = "/getTeamByMajor")
	public Result<List<EmergencyTeam>> getTeamByMajor() {
		return emergencyTeamService.getTeamByMajor();
	}

	/**
	 * 根据训练计划查找应急队伍
	 *
	 * @param
	 * @return
	 */
	@ApiOperation(value="应急队伍台账-根据训练计划查找应急队伍", notes="应急队伍台账-根据训练计划查找应急队伍")
	@GetMapping(value = "/getTeamByTrainingProgram")
	public Result<List<EmergencyTeam>> getTeamByTrainingProgram(@RequestParam(name="id",required=false) String id) {
		return emergencyTeamService.getTeamByTrainingProgram(id);
	}
}
