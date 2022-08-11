package com.aiurt.modules.workarea.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.workarea.entity.WorkAreaStation;
import com.aiurt.modules.workarea.service.IWorkAreaStationService;

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
 * @Description: work_area_station
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
@Api(tags="work_area_station")
@RestController
@RequestMapping("/workarea/workAreaStation")
@Slf4j
public class WorkAreaStationController extends BaseController<WorkAreaStation, IWorkAreaStationService> {
	@Autowired
	private IWorkAreaStationService workAreaStationService;

	/**
	 * 分页列表查询
	 *
	 * @param workAreaStation
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "work_area_station-分页列表查询")
	@ApiOperation(value="work_area_station-分页列表查询", notes="work_area_station-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WorkAreaStation>> queryPageList(WorkAreaStation workAreaStation,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<WorkAreaStation> queryWrapper = QueryGenerator.initQueryWrapper(workAreaStation, req.getParameterMap());
		Page<WorkAreaStation> page = new Page<WorkAreaStation>(pageNo, pageSize);
		IPage<WorkAreaStation> pageList = workAreaStationService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param workAreaStation
	 * @return
	 */
	@AutoLog(value = "work_area_station-添加")
	@ApiOperation(value="work_area_station-添加", notes="work_area_station-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WorkAreaStation workAreaStation) {
		workAreaStationService.save(workAreaStation);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param workAreaStation
	 * @return
	 */
	@AutoLog(value = "work_area_station-编辑")
	@ApiOperation(value="work_area_station-编辑", notes="work_area_station-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WorkAreaStation workAreaStation) {
		workAreaStationService.updateById(workAreaStation);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "work_area_station-通过id删除")
	@ApiOperation(value="work_area_station-通过id删除", notes="work_area_station-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		workAreaStationService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "work_area_station-批量删除")
	@ApiOperation(value="work_area_station-批量删除", notes="work_area_station-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.workAreaStationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "work_area_station-通过id查询")
	@ApiOperation(value="work_area_station-通过id查询", notes="work_area_station-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WorkAreaStation> queryById(@RequestParam(name="id",required=true) String id) {
		WorkAreaStation workAreaStation = workAreaStationService.getById(id);
		if(workAreaStation==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(workAreaStation);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param workAreaStation
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WorkAreaStation workAreaStation) {
        return super.exportXls(request, workAreaStation, WorkAreaStation.class, "work_area_station");
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
        return super.importExcel(request, response, WorkAreaStation.class);
    }

}
