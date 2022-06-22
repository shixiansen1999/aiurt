package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.entity.PatrolPlan;
import com.aiurt.boot.plan.service.IPatrolPlanService;

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
 * @Description: patrol_plan
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="patrol_plan")
@RestController
@RequestMapping("/patrolPlan")
@Slf4j
public class PatrolPlanController extends BaseController<PatrolPlan, IPatrolPlanService> {
	@Autowired
	private IPatrolPlanService patrolPlanService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolPlan
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_plan-分页列表查询")
	@ApiOperation(value="patrol_plan-分页列表查询", notes="patrol_plan-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolPlan>> queryPageList(PatrolPlan patrolPlan,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolPlan> queryWrapper = QueryGenerator.initQueryWrapper(patrolPlan, req.getParameterMap());
		Page<PatrolPlan> page = new Page<PatrolPlan>(pageNo, pageSize);
		IPage<PatrolPlan> pageList = patrolPlanService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param patrolPlan
	 * @return
	 */
	@AutoLog(value = "patrol_plan-添加")
	@ApiOperation(value="patrol_plan-添加", notes="patrol_plan-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolPlan patrolPlan) {
		patrolPlanService.save(patrolPlan);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param patrolPlan
	 * @return
	 */
	@AutoLog(value = "patrol_plan-编辑")
	@ApiOperation(value="patrol_plan-编辑", notes="patrol_plan-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolPlan patrolPlan) {
		patrolPlanService.updateById(patrolPlan);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "patrol_plan-通过id删除")
	@ApiOperation(value="patrol_plan-通过id删除", notes="patrol_plan-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolPlanService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "patrol_plan-批量删除")
	@ApiOperation(value="patrol_plan-批量删除", notes="patrol_plan-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolPlanService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "patrol_plan-通过id查询")
	@ApiOperation(value="patrol_plan-通过id查询", notes="patrol_plan-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolPlan> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolPlan patrolPlan = patrolPlanService.getById(id);
		if(patrolPlan==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolPlan);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolPlan
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolPlan patrolPlan) {
        return super.exportXls(request, patrolPlan, PatrolPlan.class, "patrol_plan");
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
        return super.importExcel(request, response, PatrolPlan.class);
    }

}
