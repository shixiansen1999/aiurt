package com.aiurt.boot.plan.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.plan.entity.PatrolPlanDevice;
import com.aiurt.boot.plan.service.IPatrolPlanDeviceService;

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
 * @Description: patrol_plan_device
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="patrol_plan_device")
@RestController
@RequestMapping("/patrolPlanDevice")
@Slf4j
public class PatrolPlanDeviceController extends BaseController<PatrolPlanDevice, IPatrolPlanDeviceService> {
	@Autowired
	private IPatrolPlanDeviceService patrolPlanDeviceService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolPlanDevice
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_plan_device-分页列表查询")
	@ApiOperation(value="patrol_plan_device-分页列表查询", notes="patrol_plan_device-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolPlanDevice>> queryPageList(PatrolPlanDevice patrolPlanDevice,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolPlanDevice> queryWrapper = QueryGenerator.initQueryWrapper(patrolPlanDevice, req.getParameterMap());
		Page<PatrolPlanDevice> page = new Page<PatrolPlanDevice>(pageNo, pageSize);
		IPage<PatrolPlanDevice> pageList = patrolPlanDeviceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param patrolPlanDevice
	 * @return
	 */
	@AutoLog(value = "patrol_plan_device-添加")
	@ApiOperation(value="patrol_plan_device-添加", notes="patrol_plan_device-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolPlanDevice patrolPlanDevice) {
		patrolPlanDeviceService.save(patrolPlanDevice);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param patrolPlanDevice
	 * @return
	 */
	@AutoLog(value = "patrol_plan_device-编辑")
	@ApiOperation(value="patrol_plan_device-编辑", notes="patrol_plan_device-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolPlanDevice patrolPlanDevice) {
		patrolPlanDeviceService.updateById(patrolPlanDevice);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "patrol_plan_device-通过id删除")
	@ApiOperation(value="patrol_plan_device-通过id删除", notes="patrol_plan_device-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolPlanDeviceService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "patrol_plan_device-批量删除")
	@ApiOperation(value="patrol_plan_device-批量删除", notes="patrol_plan_device-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolPlanDeviceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "patrol_plan_device-通过id查询")
	@ApiOperation(value="patrol_plan_device-通过id查询", notes="patrol_plan_device-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolPlanDevice> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolPlanDevice patrolPlanDevice = patrolPlanDeviceService.getById(id);
		if(patrolPlanDevice==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolPlanDevice);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolPlanDevice
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolPlanDevice patrolPlanDevice) {
        return super.exportXls(request, patrolPlanDevice, PatrolPlanDevice.class, "patrol_plan_device");
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
        return super.importExcel(request, response, PatrolPlanDevice.class);
    }

}
