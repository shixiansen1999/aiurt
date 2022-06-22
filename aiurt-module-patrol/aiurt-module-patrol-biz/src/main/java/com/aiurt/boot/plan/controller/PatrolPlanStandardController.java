package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.entity.PatrolPlanStandard;
import com.aiurt.boot.plan.service.IPatrolPlanStandardService;

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
 * @Description: patrol_plan_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="patrol_plan_standard")
@RestController
@RequestMapping("/patrolPlanStandard")
@Slf4j
public class PatrolPlanStandardController extends BaseController<PatrolPlanStandard, IPatrolPlanStandardService> {
	@Autowired
	private IPatrolPlanStandardService patrolPlanStandardService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolPlanStandard
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_plan_standard-分页列表查询")
	@ApiOperation(value="patrol_plan_standard-分页列表查询", notes="patrol_plan_standard-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolPlanStandard>> queryPageList(PatrolPlanStandard patrolPlanStandard,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolPlanStandard> queryWrapper = QueryGenerator.initQueryWrapper(patrolPlanStandard, req.getParameterMap());
		Page<PatrolPlanStandard> page = new Page<PatrolPlanStandard>(pageNo, pageSize);
		IPage<PatrolPlanStandard> pageList = patrolPlanStandardService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param patrolPlanStandard
	 * @return
	 */
	@AutoLog(value = "patrol_plan_standard-添加")
	@ApiOperation(value="patrol_plan_standard-添加", notes="patrol_plan_standard-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolPlanStandard patrolPlanStandard) {
		patrolPlanStandardService.save(patrolPlanStandard);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param patrolPlanStandard
	 * @return
	 */
	@AutoLog(value = "patrol_plan_standard-编辑")
	@ApiOperation(value="patrol_plan_standard-编辑", notes="patrol_plan_standard-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolPlanStandard patrolPlanStandard) {
		patrolPlanStandardService.updateById(patrolPlanStandard);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "patrol_plan_standard-通过id删除")
	@ApiOperation(value="patrol_plan_standard-通过id删除", notes="patrol_plan_standard-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolPlanStandardService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "patrol_plan_standard-批量删除")
	@ApiOperation(value="patrol_plan_standard-批量删除", notes="patrol_plan_standard-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolPlanStandardService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "patrol_plan_standard-通过id查询")
	@ApiOperation(value="patrol_plan_standard-通过id查询", notes="patrol_plan_standard-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolPlanStandard> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolPlanStandard patrolPlanStandard = patrolPlanStandardService.getById(id);
		if(patrolPlanStandard==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolPlanStandard);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolPlanStandard
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolPlanStandard patrolPlanStandard) {
        return super.exportXls(request, patrolPlanStandard, PatrolPlanStandard.class, "patrol_plan_standard");
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
        return super.importExcel(request, response, PatrolPlanStandard.class);
    }

}
