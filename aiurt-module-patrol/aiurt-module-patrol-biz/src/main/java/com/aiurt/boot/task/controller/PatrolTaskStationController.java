package com.aiurt.boot.task.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.boot.task.entity.PatrolTaskStation;
import com.aiurt.boot.task.service.IPatrolTaskStationService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: patrol_task_station
 * @Author: aiurt
 * @Date:   2022-06-27
 * @Version: V1.0
 */
@Api(tags="巡检任务站点")
@RestController
@RequestMapping("/patrolTaskStation")
@Slf4j
public class PatrolTaskStationController extends BaseController<PatrolTaskStation, IPatrolTaskStationService> {
	@Autowired
	private IPatrolTaskStationService patrolTaskStationService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolTaskStation
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	/*//@AutoLog(value = "patrol_task_station-分页列表查询")
	@ApiOperation(value="patrol_task_station-分页列表查询", notes="patrol_task_station-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolTaskStation>> queryPageList(PatrolTaskStation patrolTaskStation,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolTaskStation> queryWrapper = QueryGenerator.initQueryWrapper(patrolTaskStation, req.getParameterMap());
		Page<PatrolTaskStation> page = new Page<PatrolTaskStation>(pageNo, pageSize);
		IPage<PatrolTaskStation> pageList = patrolTaskStationService.page(page, queryWrapper);
		return Result.OK(pageList);
	}*/

	/**
	 *   添加
	 *
	 * @param patrolTaskStation
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_station-添加")
	@ApiOperation(value="patrol_task_station-添加", notes="patrol_task_station-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolTaskStation patrolTaskStation) {
		patrolTaskStationService.save(patrolTaskStation);
		return Result.OK("添加成功！");
	}*/

	/**
	 *  编辑
	 *
	 * @param patrolTaskStation
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_station-编辑")
	@ApiOperation(value="patrol_task_station-编辑", notes="patrol_task_station-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolTaskStation patrolTaskStation) {
		patrolTaskStationService.updateById(patrolTaskStation);
		return Result.OK("编辑成功!");
	}*/

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_station-通过id删除")
	@ApiOperation(value="patrol_task_station-通过id删除", notes="patrol_task_station-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolTaskStationService.removeById(id);
		return Result.OK("删除成功!");
	}*/

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_station-批量删除")
	@ApiOperation(value="patrol_task_station-批量删除", notes="patrol_task_station-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolTaskStationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	/*//@AutoLog(value = "patrol_task_station-通过id查询")
	@ApiOperation(value="patrol_task_station-通过id查询", notes="patrol_task_station-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolTaskStation> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolTaskStation patrolTaskStation = patrolTaskStationService.getById(id);
		if(patrolTaskStation==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolTaskStation);
	}*/

    /**
    * 导出excel
    *
    * @param request
    * @param patrolTaskStation
    */
   /* @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolTaskStation patrolTaskStation) {
        return super.exportXls(request, patrolTaskStation, PatrolTaskStation.class, "patrol_task_station");
    }*/

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    /*@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PatrolTaskStation.class);
    }*/

}
