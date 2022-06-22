package com.aiurt.boot.task.controller;

import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.service.IRepairTaskService;
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
 * @Description: repair_task
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="repair_task")
@RestController
@RequestMapping("/task/repairTask")
@Slf4j
public class RepairTaskController extends BaseController<RepairTask, IRepairTaskService> {
	@Autowired
	private IRepairTaskService repairTaskService;

	/**
	 * 分页列表查询
	 *
	 * @param repairTask
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "repair_task-分页列表查询")
	@ApiOperation(value="repair_task-分页列表查询", notes="repair_task-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RepairTask>> queryPageList(RepairTask repairTask,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<RepairTask> queryWrapper = QueryGenerator.initQueryWrapper(repairTask, req.getParameterMap());
		Page<RepairTask> page = new Page<RepairTask>(pageNo, pageSize);
		IPage<RepairTask> pageList = repairTaskService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param repairTask
	 * @return
	 */
	@AutoLog(value = "repair_task-添加")
	@ApiOperation(value="repair_task-添加", notes="repair_task-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RepairTask repairTask) {
		repairTaskService.save(repairTask);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param repairTask
	 * @return
	 */
	@AutoLog(value = "repair_task-编辑")
	@ApiOperation(value="repair_task-编辑", notes="repair_task-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RepairTask repairTask) {
		repairTaskService.updateById(repairTask);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "repair_task-通过id删除")
	@ApiOperation(value="repair_task-通过id删除", notes="repair_task-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		repairTaskService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "repair_task-批量删除")
	@ApiOperation(value="repair_task-批量删除", notes="repair_task-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.repairTaskService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "repair_task-通过id查询")
	@ApiOperation(value="repair_task-通过id查询", notes="repair_task-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RepairTask> queryById(@RequestParam(name="id",required=true) String id) {
		RepairTask repairTask = repairTaskService.getById(id);
		if(repairTask==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(repairTask);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param repairTask
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RepairTask repairTask) {
        return super.exportXls(request, repairTask, RepairTask.class, "repair_task");
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
        return super.importExcel(request, response, RepairTask.class);
    }

}
