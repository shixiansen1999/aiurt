package com.aiurt.boot.team.controller;

import com.aiurt.modules.team.entity.EmergencyCrew;
import com.aiurt.boot.team.service.IEmergencyCrewService;
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
 * @Description: emergency_crew
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="emergency_crew")
@RestController
@RequestMapping("/emergency/emergencyCrew")
@Slf4j
public class EmergencyCrewController extends BaseController<EmergencyCrew, IEmergencyCrewService> {
	@Autowired
	private IEmergencyCrewService emergencyCrewService;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyCrew
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_crew-分页列表查询")
	@ApiOperation(value="emergency_crew-分页列表查询", notes="emergency_crew-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyCrew>> queryPageList(EmergencyCrew emergencyCrew,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<EmergencyCrew> queryWrapper = QueryGenerator.initQueryWrapper(emergencyCrew, req.getParameterMap());
		Page<EmergencyCrew> page = new Page<EmergencyCrew>(pageNo, pageSize);
		IPage<EmergencyCrew> pageList = emergencyCrewService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param emergencyCrew
	 * @return
	 */
	@AutoLog(value = "emergency_crew-添加")
	@ApiOperation(value="emergency_crew-添加", notes="emergency_crew-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyCrew emergencyCrew) {
		emergencyCrewService.save(emergencyCrew);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param emergencyCrew
	 * @return
	 */
	@AutoLog(value = "emergency_crew-编辑")
	@ApiOperation(value="emergency_crew-编辑", notes="emergency_crew-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmergencyCrew emergencyCrew) {
		emergencyCrewService.updateById(emergencyCrew);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "emergency_crew-通过id删除")
	@ApiOperation(value="emergency_crew-通过id删除", notes="emergency_crew-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		emergencyCrewService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "emergency_crew-批量删除")
	@ApiOperation(value="emergency_crew-批量删除", notes="emergency_crew-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyCrewService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "emergency_crew-通过id查询")
	@ApiOperation(value="emergency_crew-通过id查询", notes="emergency_crew-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyCrew> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyCrew emergencyCrew = emergencyCrewService.getById(id);
		if(emergencyCrew==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(emergencyCrew);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyCrew
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyCrew emergencyCrew) {
        return super.exportXls(request, emergencyCrew, EmergencyCrew.class, "emergency_crew");
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
        return super.importExcel(request, response, EmergencyCrew.class);
    }

}
