package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.entity.patrol.plan.PatrolPlanOrganization;
import com.aiurt.boot.plan.service.IPatrolPlanOrganizationService;

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
 * @Description: patrol_plan_organization
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="patrol_plan_organization")
@RestController
@RequestMapping("/patrolPlanOrganization")
@Slf4j
public class PatrolPlanOrganizationController extends BaseController<PatrolPlanOrganization, IPatrolPlanOrganizationService> {
	@Autowired
	private IPatrolPlanOrganizationService patrolPlanOrganizationService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolPlanOrganization
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_plan_organization-分页列表查询")
	@ApiOperation(value="patrol_plan_organization-分页列表查询", notes="patrol_plan_organization-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolPlanOrganization>> queryPageList(PatrolPlanOrganization patrolPlanOrganization,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolPlanOrganization> queryWrapper = QueryGenerator.initQueryWrapper(patrolPlanOrganization, req.getParameterMap());
		Page<PatrolPlanOrganization> page = new Page<PatrolPlanOrganization>(pageNo, pageSize);
		IPage<PatrolPlanOrganization> pageList = patrolPlanOrganizationService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param patrolPlanOrganization
	 * @return
	 */
	@AutoLog(value = "patrol_plan_organization-添加")
	@ApiOperation(value="patrol_plan_organization-添加", notes="patrol_plan_organization-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolPlanOrganization patrolPlanOrganization) {
		patrolPlanOrganizationService.save(patrolPlanOrganization);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param patrolPlanOrganization
	 * @return
	 */
	@AutoLog(value = "patrol_plan_organization-编辑")
	@ApiOperation(value="patrol_plan_organization-编辑", notes="patrol_plan_organization-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolPlanOrganization patrolPlanOrganization) {
		patrolPlanOrganizationService.updateById(patrolPlanOrganization);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "patrol_plan_organization-通过id删除")
	@ApiOperation(value="patrol_plan_organization-通过id删除", notes="patrol_plan_organization-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolPlanOrganizationService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "patrol_plan_organization-批量删除")
	@ApiOperation(value="patrol_plan_organization-批量删除", notes="patrol_plan_organization-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolPlanOrganizationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "patrol_plan_organization-通过id查询")
	@ApiOperation(value="patrol_plan_organization-通过id查询", notes="patrol_plan_organization-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolPlanOrganization> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolPlanOrganization patrolPlanOrganization = patrolPlanOrganizationService.getById(id);
		if(patrolPlanOrganization==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolPlanOrganization);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolPlanOrganization
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolPlanOrganization patrolPlanOrganization) {
        return super.exportXls(request, patrolPlanOrganization, PatrolPlanOrganization.class, "patrol_plan_organization");
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
        return super.importExcel(request, response, PatrolPlanOrganization.class);
    }

}
