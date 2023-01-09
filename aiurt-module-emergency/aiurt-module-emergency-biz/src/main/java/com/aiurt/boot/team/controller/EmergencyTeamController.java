package com.aiurt.boot.team.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.constants.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTeamDTO;
import com.aiurt.boot.team.entity.EmergencyCrew;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.service.IEmergencyCrewService;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.aiurt.boot.team.vo.EmergencyCrewVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

	@Autowired
	private IEmergencyCrewService emergencyCrewService;
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
	@PermissionData(pageComponent = "emergency/emergencyTeam/EmergencyTeamList")
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
	public Result<String> add(@RequestBody   @Validated(EmergencyTeam.Save.class) EmergencyTeam emergencyTeam) {
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
	public Result<String> edit(@RequestBody @Validated(EmergencyTeam.Update.class)EmergencyTeam emergencyTeam) {
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
		emergencyTeam.setDelFlag(TeamConstant.DEL_FLAG1);
		emergencyTeamService.updateById(emergencyTeam);
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
    * 应急队伍台账导出excel
    *
    * @param request
    * @param emergencyTeamDTO
    */
	@ApiOperation(value="应急队伍台账导出excel", notes="应急队伍台账导出excel")
    @RequestMapping(value = "/exportTeamXls",method = RequestMethod.GET)
	@PermissionData(pageComponent = "emergency/emergencyTeam/EmergencyTeamList")
    public ModelAndView exportTeamXls(HttpServletRequest request, EmergencyTeamDTO emergencyTeamDTO) {
        return emergencyTeamService.exportTeamXls(request, emergencyTeamDTO);
    }

	/**
	 * 应急队伍人员导出excel
	 *
	 * @param request
	 * @param id
	 */
	@ApiOperation(value="应急队伍人员导出excel", notes="应急队伍人员导出excel")
	@RequestMapping(value = "/exportCrewXls",method = RequestMethod.GET)
	public ModelAndView exportCrewXls(HttpServletRequest request, String id) {
		return emergencyTeamService.exportCrewXls(request, id);
	}


	/**
	 * 应急队伍模板下载
	 *
	 */
	@AutoLog(value = "应急队伍模板下载", operateType =  6, operateTypeAlias = "导出excel", permissionUrl = "")
	@ApiOperation(value="应急队伍模板下载", notes="应急队伍模板下载")
	@RequestMapping(value = "/exportTemplateXls",method = RequestMethod.GET)
	public void exportTemplateXl(HttpServletResponse response, HttpServletRequest request) throws IOException {
		emergencyTeamService.exportTemplateXls(response);
	}


	/**
    *  应急队伍通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
	@ApiOperation(value="应急队伍通过excel导入数据", notes="应急队伍通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return emergencyTeamService.importExcel(request, response);
    }

	/**
	 * 根据权限查找应急队伍
	 *
	 * @return
	 */
	@ApiOperation(value="应急训练计划-根据权限查找应急队伍", notes="应急训练计划-根据部门查找应急队伍")
	@GetMapping(value = "/getTeamByCode")
	@PermissionData(pageComponent = "emergency/emergencyTeam/EmergencyTeamList")
	public Result<List<EmergencyTeam>> getTeamByCode() {
		return emergencyTeamService.getTeamByCode();
	}

	/**
	 * 根据权限查找应急队伍
	 *
	 * @param
	 * @return
	 */
	@ApiOperation(value="应急训练记录-根据权限查找应急队伍", notes="应急训练记录-根据权限查找应急队伍")
	@GetMapping(value = "/getTeamByMajor")
	@PermissionData(pageComponent = "emergency/emergencyTeam/EmergencyTrainingRecordList")
	public Result<List<EmergencyTeam>> getTeamByMajor() {
		return emergencyTeamService.getTeamByMajor();
	}

	/**
	 * 应急队伍台账-根据应急队伍查询应急人员
	 *
	 * @param emergencyCrewVO
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="应急队伍台账-根据应急队伍查询应急人员", notes="应急队伍台账-根据应急队伍查询应急人员")
	@GetMapping(value = "/queryCrewPageList")
	public Result<IPage<EmergencyCrew>> queryCrewPageList(EmergencyCrewVO emergencyCrewVO,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													  HttpServletRequest req) {
		EmergencyCrew emergencyCrew = new EmergencyCrew();
		BeanUtil.copyProperties(emergencyCrewVO, emergencyCrew);
		emergencyCrew.setDelFlag(TeamConstant.DEL_FLAG0);
		QueryWrapper<EmergencyCrew> queryWrapper = QueryGenerator.initQueryWrapper(emergencyCrew, req.getParameterMap());
		Page<EmergencyCrew> page = new Page<EmergencyCrew>(pageNo, pageSize);
		IPage<EmergencyCrew> pageList = emergencyCrewService.page(page, queryWrapper);
		List<EmergencyCrew> records = pageList.getRecords();
		if (CollUtil.isNotEmpty(records)) {
			for (EmergencyCrew record : records) {
				LoginUser userById = iSysBaseAPI.getUserById(record.getUserId());
				record.setRealname(userById.getRealname());
				List<String> roleNamesById = iSysBaseAPI.getRoleNamesById(record.getUserId());
				if (CollUtil.isNotEmpty(roleNamesById)) {
					String join = StrUtil.join(",", roleNamesById);
					record.setRoleNames(join);
				}
			}
		}
		return Result.OK(pageList);
	}
}
