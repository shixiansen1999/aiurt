package com.aiurt.boot.plan.controller;

import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="检修计划池")
@RestController
@RequestMapping("/plan/repairPool")
@Slf4j
public class RepairPoolController extends BaseController<RepairPool, IRepairPoolService> {
	@Autowired
	private IRepairPoolService repairPoolService;

	/**
	 * 检修计划池列表查询
	 *
	 * @return
	 */
	@AutoLog(value = "检修计划池列表查询")
	@ApiOperation(value="检修计划池列表查询", notes="检修计划池列表查询")
	@ApiResponses({
			@ApiResponse(code = 200,message = "OK",response = RepairPool.class)
	})
	@GetMapping(value = "/list")
	public Result<?> queryList(@RequestParam @ApiParam(required = true, value = "开始时间", name = "startTime") Date startTime,
							   @RequestParam @ApiParam(required = true, value = "结束时间", name = "endTime") Date endTime) {
		List<RepairPool> repairPoolList = repairPoolService.queryList(startTime,endTime);
		return Result.OK(repairPoolList);
	}

	/**
	 *   添加
	 *
	 * @param repairPool
	 * @return
	 */
	@AutoLog(value = "repair_pool-添加")
	@ApiOperation(value="repair_pool-添加", notes="repair_pool-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RepairPool repairPool) {
		repairPoolService.save(repairPool);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param repairPool
	 * @return
	 */
	@AutoLog(value = "repair_pool-编辑")
	@ApiOperation(value="repair_pool-编辑", notes="repair_pool-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RepairPool repairPool) {
		repairPoolService.updateById(repairPool);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "repair_pool-通过id删除")
	@ApiOperation(value="repair_pool-通过id删除", notes="repair_pool-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		repairPoolService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "repair_pool-批量删除")
	@ApiOperation(value="repair_pool-批量删除", notes="repair_pool-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.repairPoolService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "repair_pool-通过id查询")
	@ApiOperation(value="repair_pool-通过id查询", notes="repair_pool-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RepairPool> queryById(@RequestParam(name="id",required=true) String id) {
		RepairPool repairPool = repairPoolService.getById(id);
		if(repairPool==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(repairPool);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param repairPool
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RepairPool repairPool) {
        return super.exportXls(request, repairPool, RepairPool.class, "repair_pool");
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
        return super.importExcel(request, response, RepairPool.class);
    }

}
