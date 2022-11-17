package com.aiurt.modules.workarea.controller;

import com.aiurt.boot.weeklyplan.entity.BdSite;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.workarea.dto.MajorUserDTO;
import com.aiurt.modules.workarea.dto.WorkAreaDTO;
import com.aiurt.modules.workarea.entity.WorkArea;
import com.aiurt.modules.workarea.service.IWorkAreaService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.SiteModel;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: work_area
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
@Api(tags="工区")
@RestController
@RequestMapping("/workarea/workArea")
@Slf4j
public class WorkAreaController extends BaseController<WorkArea, IWorkAreaService> {
	@Autowired
	private IWorkAreaService workAreaService;

	/**
	 * 分页列表查询
	 *
	 * @param workArea
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "工区列表-分页列表查询")
	@ApiOperation(value="工区列表-分页列表查询", notes="工区列表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WorkAreaDTO>> queryPageList(WorkAreaDTO workArea,
												 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												 HttpServletRequest req) {
		Page<WorkAreaDTO> pageList = new Page<>(pageNo,pageSize);
		pageList = workAreaService.getWorkAreaList(pageList,workArea);
		return Result.OK(pageList);
	}

	/**
	 *  添加
	 * @param workAreaDTO
	 * @return
	 */
	@AutoLog(value = "工区-添加")
	@ApiOperation(value="工区-添加", notes="工区-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WorkAreaDTO workAreaDTO) {
		workAreaService.addWorkArea(workAreaDTO);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param workAreaDTO
	 * @return
	 */
	@AutoLog(value = "工区-编辑")
	@ApiOperation(value="工区-编辑", notes="工区-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WorkAreaDTO workAreaDTO) {
		workAreaService.updateWorkArea(workAreaDTO);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "工区-通过id删除")
	@ApiOperation(value="工区-通过id删除", notes="工区-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		workAreaService.deleteWorkArea(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "工区-批量删除")
	@ApiOperation(value="工区-批量删除", notes="工区-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.workAreaService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 根据专业Code,查询专业下的全部用户
	 * @param majorCode
	 * @return
	 */
	@AutoLog(value = "根据专业Code,查询专业下的全部用户")
	@ApiOperation(value="根据专业Code,查询专业下的全部用户", notes="根据专业Code,查询专业下的全部用户")
	@GetMapping(value = "/majorUser")
	public Result<IPage<MajorUserDTO>> getMajorUser(@RequestParam(name="majorCode",required=true) String majorCode,
													@RequestParam(name="realname",required=false) String realname,
													@RequestParam(name="orgId",required=false) String orgId,
													@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													@RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<MajorUserDTO> pageList = new Page<>(pageNo,pageSize);
		pageList = workAreaService.getMajorUser(pageList,majorCode,realname,orgId);
		return Result.OK(pageList);
	}

	/**
	 * 根据线路获取班组(根据登录用户专业过滤)
	 *
	 * @param lineCode 线路code
	 * @return
	 */
	@AutoLog(value = "根据线路获取班组(根据登录用户专业过滤)", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
	@ApiOperation(value = "根据线路获取班组(根据登录用户专业过滤)", notes = "根据线路获取班组(根据登录用户专业过滤)")
	@RequestMapping(value = "/getTeamBylineAndMajors", method = RequestMethod.GET)
	public Result<List<SysDepartModel>> getTeamBylineAndMajors(@ApiParam(name = "lineCode", value = "线路code,多个用,隔开") @RequestParam(value = "lineCode", required = false) String lineCode) {
		List<SysDepartModel> result = workAreaService.getTeamBylineAndMajors(lineCode);
		return Result.OK(result);
	}



	@ApiOperation("查询本工区的站所")
	@GetMapping("/queryOriginStation")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "param", value = "标志: 1:全部,0:本工区", required = true, paramType = "query")
	})
	public Result<List<CsStation>> queryOriginStation(@RequestParam(value = "param", required = true, defaultValue = "1") String param) {
		List<CsStation> list = workAreaService.queryOriginStation(param);
		return Result.OK(list);
	}

	@ApiOperation("查询本工区的人员")
	@GetMapping("/queryOriginUser")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "param", value = "标志: 1:全部,0:本工区", required = true, paramType = "query")
	})
	public Result<List<SysUser>> queryOriginUser(@RequestParam(value = "param", required = true, defaultValue = "1") String param) {
		List<SysUser> list = workAreaService.queryOriginUser(param);
		return Result.OK(list);
	}

	/**
	 * 获取当前用户管辖班组下工区
	 * @return
	 */
	@AutoLog(value = "工区-获取当前用户管辖班组下工区")
	@ApiOperation(value = "工区-获取当前用户管辖班组下工区", notes = "工区-获取当前用户管辖班组下工区")
	@GetMapping(value = "/querySiteByTeam")
	public Result<?> querySiteByTeam() {
		List<SiteModel> modelList = workAreaService.querySiteByTeam();
		return Result.OK(modelList);
	}
}
