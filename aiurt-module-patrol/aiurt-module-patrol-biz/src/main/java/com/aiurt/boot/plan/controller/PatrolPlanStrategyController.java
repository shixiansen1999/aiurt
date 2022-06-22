package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.entity.PatrolPlanStrategy;
import com.aiurt.boot.plan.service.IPatrolPlanStrategyService;

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
 * @Description: patrol_plan_strategy
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="patrol_plan_strategy")
@RestController
@RequestMapping("/patrolPlanStrategy")
@Slf4j
public class PatrolPlanStrategyController extends BaseController<PatrolPlanStrategy, IPatrolPlanStrategyService> {
	@Autowired
	private IPatrolPlanStrategyService patrolPlanStrategyService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolPlanStrategy
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_plan_strategy-分页列表查询")
	@ApiOperation(value="patrol_plan_strategy-分页列表查询", notes="patrol_plan_strategy-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolPlanStrategy>> queryPageList(PatrolPlanStrategy patrolPlanStrategy,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolPlanStrategy> queryWrapper = QueryGenerator.initQueryWrapper(patrolPlanStrategy, req.getParameterMap());
		Page<PatrolPlanStrategy> page = new Page<PatrolPlanStrategy>(pageNo, pageSize);
		IPage<PatrolPlanStrategy> pageList = patrolPlanStrategyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param patrolPlanStrategy
	 * @return
	 */
	@AutoLog(value = "patrol_plan_strategy-添加")
	@ApiOperation(value="patrol_plan_strategy-添加", notes="patrol_plan_strategy-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolPlanStrategy patrolPlanStrategy) {
		patrolPlanStrategyService.save(patrolPlanStrategy);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param patrolPlanStrategy
	 * @return
	 */
	@AutoLog(value = "patrol_plan_strategy-编辑")
	@ApiOperation(value="patrol_plan_strategy-编辑", notes="patrol_plan_strategy-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolPlanStrategy patrolPlanStrategy) {
		patrolPlanStrategyService.updateById(patrolPlanStrategy);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "patrol_plan_strategy-通过id删除")
	@ApiOperation(value="patrol_plan_strategy-通过id删除", notes="patrol_plan_strategy-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolPlanStrategyService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "patrol_plan_strategy-批量删除")
	@ApiOperation(value="patrol_plan_strategy-批量删除", notes="patrol_plan_strategy-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolPlanStrategyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "patrol_plan_strategy-通过id查询")
	@ApiOperation(value="patrol_plan_strategy-通过id查询", notes="patrol_plan_strategy-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolPlanStrategy> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolPlanStrategy patrolPlanStrategy = patrolPlanStrategyService.getById(id);
		if(patrolPlanStrategy==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolPlanStrategy);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolPlanStrategy
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolPlanStrategy patrolPlanStrategy) {
        return super.exportXls(request, patrolPlanStrategy, PatrolPlanStrategy.class, "patrol_plan_strategy");
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
        return super.importExcel(request, response, PatrolPlanStrategy.class);
    }

}
