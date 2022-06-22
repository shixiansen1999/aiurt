package com.aiurt.boot.task.controller;

import com.aiurt.boot.task.entity.RepairTaskResult;
import com.aiurt.boot.task.service.IRepairTaskResultService;
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
 * @Description: repair_task_result
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="repair_task_result")
@RestController
@RequestMapping("/task/repairTaskResult")
@Slf4j
public class RepairTaskResultController extends BaseController<RepairTaskResult, IRepairTaskResultService> {
	@Autowired
	private IRepairTaskResultService repairTaskResultService;

	/**
	 * 分页列表查询
	 *
	 * @param repairTaskResult
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "repair_task_result-分页列表查询")
	@ApiOperation(value="repair_task_result-分页列表查询", notes="repair_task_result-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RepairTaskResult>> queryPageList(RepairTaskResult repairTaskResult,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<RepairTaskResult> queryWrapper = QueryGenerator.initQueryWrapper(repairTaskResult, req.getParameterMap());
		Page<RepairTaskResult> page = new Page<RepairTaskResult>(pageNo, pageSize);
		IPage<RepairTaskResult> pageList = repairTaskResultService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param repairTaskResult
	 * @return
	 */
	@AutoLog(value = "repair_task_result-添加")
	@ApiOperation(value="repair_task_result-添加", notes="repair_task_result-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RepairTaskResult repairTaskResult) {
		repairTaskResultService.save(repairTaskResult);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param repairTaskResult
	 * @return
	 */
	@AutoLog(value = "repair_task_result-编辑")
	@ApiOperation(value="repair_task_result-编辑", notes="repair_task_result-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RepairTaskResult repairTaskResult) {
		repairTaskResultService.updateById(repairTaskResult);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "repair_task_result-通过id删除")
	@ApiOperation(value="repair_task_result-通过id删除", notes="repair_task_result-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		repairTaskResultService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "repair_task_result-批量删除")
	@ApiOperation(value="repair_task_result-批量删除", notes="repair_task_result-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.repairTaskResultService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "repair_task_result-通过id查询")
	@ApiOperation(value="repair_task_result-通过id查询", notes="repair_task_result-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RepairTaskResult> queryById(@RequestParam(name="id",required=true) String id) {
		RepairTaskResult repairTaskResult = repairTaskResultService.getById(id);
		if(repairTaskResult==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(repairTaskResult);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param repairTaskResult
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RepairTaskResult repairTaskResult) {
        return super.exportXls(request, repairTaskResult, RepairTaskResult.class, "repair_task_result");
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
        return super.importExcel(request, response, RepairTaskResult.class);
    }

}
