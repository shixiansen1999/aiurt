package com.aiurt.boot.task.controller;

import com.aiurt.boot.task.dto.PatrolTaskDeviceDTO;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
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
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="patrol_task_device")
@RestController
@RequestMapping("/patrolTaskDevice")
@Slf4j
public class PatrolTaskDeviceController extends BaseController<PatrolTaskDevice, IPatrolTaskDeviceService> {
	@Autowired
	private IPatrolTaskDeviceService patrolTaskDeviceService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolTaskDevice
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_task_device-分页列表查询")
	@ApiOperation(value="patrol_task_device-分页列表查询", notes="patrol_task_device-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolTaskDevice>> queryPageList(PatrolTaskDevice patrolTaskDevice,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolTaskDevice> queryWrapper = QueryGenerator.initQueryWrapper(patrolTaskDevice, req.getParameterMap());
		Page<PatrolTaskDevice> page = new Page<PatrolTaskDevice>(pageNo, pageSize);
		IPage<PatrolTaskDevice> pageList = patrolTaskDeviceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	 /**
	  * app巡检任务-巡检清单列表（巡检工单列表）
	  * @return
	  */
	 @AutoLog(value = "patrol_task- app巡检任务-巡检清单列表")
	 @ApiOperation(value="patrol_task- app巡检任务-巡检清单列表", notes="patrol_task- app巡检任务-巡检清单列表")
	 @GetMapping(value = "/patrolTaskDeviceList")
	 public Result<Page<PatrolTaskDeviceDTO>>  patrolTaskDeviceList(String id,
																	@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
																	@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		 Page<PatrolTaskDeviceDTO> pageList = new Page<PatrolTaskDeviceDTO>(pageNo, pageSize);
		 pageList = patrolTaskDeviceService.getPatrolTaskDeviceList(pageList, id);
		 return Result.OK(pageList);
	 }
	/**
	 *   添加
	 *
	 * @param patrolTaskDevice
	 * @return
	 */
	@AutoLog(value = "patrol_task_device-添加")
	@ApiOperation(value="patrol_task_device-添加", notes="patrol_task_device-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolTaskDevice patrolTaskDevice) {
		patrolTaskDeviceService.save(patrolTaskDevice);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param patrolTaskDevice
	 * @return
	 */
	@AutoLog(value = "patrol_task_device-编辑")
	@ApiOperation(value="patrol_task_device-编辑", notes="patrol_task_device-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolTaskDevice patrolTaskDevice) {
		patrolTaskDeviceService.updateById(patrolTaskDevice);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "patrol_task_device-通过id删除")
	@ApiOperation(value="patrol_task_device-通过id删除", notes="patrol_task_device-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolTaskDeviceService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "patrol_task_device-批量删除")
	@ApiOperation(value="patrol_task_device-批量删除", notes="patrol_task_device-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolTaskDeviceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "patrol_task_device-通过id查询")
	@ApiOperation(value="patrol_task_device-通过id查询", notes="patrol_task_device-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolTaskDevice> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolTaskDevice patrolTaskDevice = patrolTaskDeviceService.getById(id);
		if(patrolTaskDevice==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolTaskDevice);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolTaskDevice
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolTaskDevice patrolTaskDevice) {
        return super.exportXls(request, patrolTaskDevice, PatrolTaskDevice.class, "patrol_task_device");
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
        return super.importExcel(request, response, PatrolTaskDevice.class);
    }

}
