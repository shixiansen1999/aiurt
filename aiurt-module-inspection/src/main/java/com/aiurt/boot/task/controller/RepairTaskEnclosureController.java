package com.aiurt.boot.task.controller;

import com.aiurt.boot.entity.inspection.task.RepairTaskEnclosure;
import com.aiurt.boot.task.service.IRepairTaskEnclosureService;
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
 * @Description: repair_task_enclosure
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="repair_task_enclosure")
@RestController
@RequestMapping("/task/repairTaskEnclosure")
@Slf4j
public class RepairTaskEnclosureController extends BaseController<RepairTaskEnclosure, IRepairTaskEnclosureService> {
	@Autowired
	private IRepairTaskEnclosureService repairTaskEnclosureService;

	/**
	 * 分页列表查询
	 *
	 * @param repairTaskEnclosure
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "repair_task_enclosure-分页列表查询")
	@ApiOperation(value="repair_task_enclosure-分页列表查询", notes="repair_task_enclosure-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RepairTaskEnclosure>> queryPageList(RepairTaskEnclosure repairTaskEnclosure,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<RepairTaskEnclosure> queryWrapper = QueryGenerator.initQueryWrapper(repairTaskEnclosure, req.getParameterMap());
		Page<RepairTaskEnclosure> page = new Page<RepairTaskEnclosure>(pageNo, pageSize);
		IPage<RepairTaskEnclosure> pageList = repairTaskEnclosureService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param repairTaskEnclosure
	 * @return
	 */
	@AutoLog(value = "repair_task_enclosure-添加")
	@ApiOperation(value="repair_task_enclosure-添加", notes="repair_task_enclosure-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RepairTaskEnclosure repairTaskEnclosure) {
		repairTaskEnclosureService.save(repairTaskEnclosure);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param repairTaskEnclosure
	 * @return
	 */
	@AutoLog(value = "repair_task_enclosure-编辑")
	@ApiOperation(value="repair_task_enclosure-编辑", notes="repair_task_enclosure-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RepairTaskEnclosure repairTaskEnclosure) {
		repairTaskEnclosureService.updateById(repairTaskEnclosure);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "repair_task_enclosure-通过id删除")
	@ApiOperation(value="repair_task_enclosure-通过id删除", notes="repair_task_enclosure-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		repairTaskEnclosureService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "repair_task_enclosure-批量删除")
	@ApiOperation(value="repair_task_enclosure-批量删除", notes="repair_task_enclosure-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.repairTaskEnclosureService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "repair_task_enclosure-通过id查询")
	@ApiOperation(value="repair_task_enclosure-通过id查询", notes="repair_task_enclosure-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RepairTaskEnclosure> queryById(@RequestParam(name="id",required=true) String id) {
		RepairTaskEnclosure repairTaskEnclosure = repairTaskEnclosureService.getById(id);
		if(repairTaskEnclosure==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(repairTaskEnclosure);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param repairTaskEnclosure
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RepairTaskEnclosure repairTaskEnclosure) {
        return super.exportXls(request, repairTaskEnclosure, RepairTaskEnclosure.class, "repair_task_enclosure");
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
        return super.importExcel(request, response, RepairTaskEnclosure.class);
    }

}
