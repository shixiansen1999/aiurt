package com.aiurt.modules.robot.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.robot.entity.TaskExcuteInfo;
import com.aiurt.modules.robot.service.ITaskExcuteInfoService;

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
 * @Description: task_excute_info
 * @Author: aiurt
 * @Date:   2022-09-29
 * @Version: V1.0
 */
@Api(tags="task_excute_info")
@RestController
@RequestMapping("/robot/taskExcuteInfo")
@Slf4j
public class TaskExcuteInfoController extends BaseController<TaskExcuteInfo, ITaskExcuteInfoService> {
	@Autowired
	private ITaskExcuteInfoService taskExcuteInfoService;

	/**
	 * 分页列表查询
	 *
	 * @param taskExcuteInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "task_excute_info-分页列表查询")
	@ApiOperation(value="task_excute_info-分页列表查询", notes="task_excute_info-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TaskExcuteInfo>> queryPageList(TaskExcuteInfo taskExcuteInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TaskExcuteInfo> queryWrapper = QueryGenerator.initQueryWrapper(taskExcuteInfo, req.getParameterMap());
		Page<TaskExcuteInfo> page = new Page<TaskExcuteInfo>(pageNo, pageSize);
		IPage<TaskExcuteInfo> pageList = taskExcuteInfoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param taskExcuteInfo
	 * @return
	 */
	@AutoLog(value = "task_excute_info-添加")
	@ApiOperation(value="task_excute_info-添加", notes="task_excute_info-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TaskExcuteInfo taskExcuteInfo) {
		taskExcuteInfoService.save(taskExcuteInfo);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param taskExcuteInfo
	 * @return
	 */
	@AutoLog(value = "task_excute_info-编辑")
	@ApiOperation(value="task_excute_info-编辑", notes="task_excute_info-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TaskExcuteInfo taskExcuteInfo) {
		taskExcuteInfoService.updateById(taskExcuteInfo);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "task_excute_info-通过id删除")
	@ApiOperation(value="task_excute_info-通过id删除", notes="task_excute_info-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		taskExcuteInfoService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "task_excute_info-批量删除")
	@ApiOperation(value="task_excute_info-批量删除", notes="task_excute_info-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.taskExcuteInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "task_excute_info-通过id查询")
	@ApiOperation(value="task_excute_info-通过id查询", notes="task_excute_info-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TaskExcuteInfo> queryById(@RequestParam(name="id",required=true) String id) {
		TaskExcuteInfo taskExcuteInfo = taskExcuteInfoService.getById(id);
		if(taskExcuteInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(taskExcuteInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param taskExcuteInfo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TaskExcuteInfo taskExcuteInfo) {
        return super.exportXls(request, taskExcuteInfo, TaskExcuteInfo.class, "task_excute_info");
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
        return super.importExcel(request, response, TaskExcuteInfo.class);
    }

}
