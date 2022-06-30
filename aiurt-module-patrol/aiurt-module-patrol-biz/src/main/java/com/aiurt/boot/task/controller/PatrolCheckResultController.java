package com.aiurt.boot.task.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.task.entity.PatrolCheckResult;
import com.aiurt.boot.task.service.IPatrolCheckResultService;

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
 * @Description: patrol_check_result
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="patrol_check_result")
@RestController
@RequestMapping("/patrolCheckResult")
@Slf4j
public class PatrolCheckResultController extends BaseController<PatrolCheckResult, IPatrolCheckResultService> {
	@Autowired
	private IPatrolCheckResultService patrolCheckResultService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolCheckResult
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	/*//@AutoLog(value = "patrol_check_result-分页列表查询")
	@ApiOperation(value="patrol_check_result-分页列表查询", notes="patrol_check_result-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolCheckResult>> queryPageList(PatrolCheckResult patrolCheckResult,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolCheckResult> queryWrapper = QueryGenerator.initQueryWrapper(patrolCheckResult, req.getParameterMap());
		Page<PatrolCheckResult> page = new Page<PatrolCheckResult>(pageNo, pageSize);
		IPage<PatrolCheckResult> pageList = patrolCheckResultService.page(page, queryWrapper);
		return Result.OK(pageList);
	}*/

	/**
	 *   添加
	 *
	 * @param patrolCheckResult
	 * @return
	 */
	/*@AutoLog(value = "patrol_check_result-添加")
	@ApiOperation(value="patrol_check_result-添加", notes="patrol_check_result-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolCheckResult patrolCheckResult) {
		patrolCheckResultService.save(patrolCheckResult);
		return Result.OK("添加成功！");
	}*/

	/**
	 *  编辑
	 *
	 * @param patrolCheckResult
	 * @return
	 */
	/*@AutoLog(value = "patrol_check_result-编辑")
	@ApiOperation(value="patrol_check_result-编辑", notes="patrol_check_result-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolCheckResult patrolCheckResult) {
		patrolCheckResultService.updateById(patrolCheckResult);
		return Result.OK("编辑成功!");
	}
*/
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	/*@AutoLog(value = "patrol_check_result-通过id删除")
	@ApiOperation(value="patrol_check_result-通过id删除", notes="patrol_check_result-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolCheckResultService.removeById(id);
		return Result.OK("删除成功!");
	}
*/
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "patrol_check_result-批量删除")
	@ApiOperation(value="patrol_check_result-批量删除", notes="patrol_check_result-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolCheckResultService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	/*//@AutoLog(value = "patrol_check_result-通过id查询")
	@ApiOperation(value="patrol_check_result-通过id查询", notes="patrol_check_result-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolCheckResult> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolCheckResult patrolCheckResult = patrolCheckResultService.getById(id);
		if(patrolCheckResult==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolCheckResult);
	}*/

    /**
    * 导出excel
    *
    * @param request
    * @param patrolCheckResult
    */
  /*  @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolCheckResult patrolCheckResult) {
        return super.exportXls(request, patrolCheckResult, PatrolCheckResult.class, "patrol_check_result");
    }*/

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
   /* @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PatrolCheckResult.class);
    }*/

}
