package com.aiurt.boot.task.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.aiurt.boot.task.service.IPatrolTaskStandardService;

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
 * @Description: patrol_task_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="巡检任务标准")
@RestController
@RequestMapping("/patrolTaskStandard")
@Slf4j
public class PatrolTaskStandardController extends BaseController<PatrolTaskStandard, IPatrolTaskStandardService> {
	@Autowired
	private IPatrolTaskStandardService patrolTaskStandardService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolTaskStandard
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	/*//@AutoLog(value = "patrol_task_standard-分页列表查询")
	@ApiOperation(value="patrol_task_standard-分页列表查询", notes="patrol_task_standard-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolTaskStandard>> queryPageList(PatrolTaskStandard patrolTaskStandard,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolTaskStandard> queryWrapper = QueryGenerator.initQueryWrapper(patrolTaskStandard, req.getParameterMap());
		Page<PatrolTaskStandard> page = new Page<PatrolTaskStandard>(pageNo, pageSize);
		IPage<PatrolTaskStandard> pageList = patrolTaskStandardService.page(page, queryWrapper);
		return Result.OK(pageList);
	}*/

	/**
	 *   添加
	 *
	 * @param patrolTaskStandard
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_standard-添加")
	@ApiOperation(value="patrol_task_standard-添加", notes="patrol_task_standard-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolTaskStandard patrolTaskStandard) {
		patrolTaskStandardService.save(patrolTaskStandard);
		return Result.OK("添加成功！");
	}*/

	/**
	 *  编辑
	 *
	 * @param patrolTaskStandard
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_standard-编辑")
	@ApiOperation(value="patrol_task_standard-编辑", notes="patrol_task_standard-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolTaskStandard patrolTaskStandard) {
		patrolTaskStandardService.updateById(patrolTaskStandard);
		return Result.OK("编辑成功!");
	}*/

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_standard-通过id删除")
	@ApiOperation(value="patrol_task_standard-通过id删除", notes="patrol_task_standard-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolTaskStandardService.removeById(id);
		return Result.OK("删除成功!");
	}*/

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_standard-批量删除")
	@ApiOperation(value="patrol_task_standard-批量删除", notes="patrol_task_standard-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolTaskStandardService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	/*//@AutoLog(value = "patrol_task_standard-通过id查询")
	@ApiOperation(value="patrol_task_standard-通过id查询", notes="patrol_task_standard-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolTaskStandard> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolTaskStandard patrolTaskStandard = patrolTaskStandardService.getById(id);
		if(patrolTaskStandard==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolTaskStandard);
	}*/

    /**
    * 导出excel
    *
    * @param request
    * @param patrolTaskStandard
    */
   /* @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolTaskStandard patrolTaskStandard) {
        return super.exportXls(request, patrolTaskStandard, PatrolTaskStandard.class, "patrol_task_standard");
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
        return super.importExcel(request, response, PatrolTaskStandard.class);
    }*/

}
