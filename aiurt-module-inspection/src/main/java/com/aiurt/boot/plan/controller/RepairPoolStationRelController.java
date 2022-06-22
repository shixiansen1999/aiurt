package com.aiurt.boot.plan.controller;

import com.aiurt.boot.entity.inspection.plan.RepairPoolStationRel;
import com.aiurt.boot.plan.service.IRepairPoolStationRelService;
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
 * @Description: repair_pool_station_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="repair_pool_station_rel")
@RestController
@RequestMapping("/plan/repairPoolStationRel")
@Slf4j
public class RepairPoolStationRelController extends BaseController<RepairPoolStationRel, IRepairPoolStationRelService> {
	@Autowired
	private IRepairPoolStationRelService repairPoolStationRelService;

	/**
	 * 分页列表查询
	 *
	 * @param repairPoolStationRel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "repair_pool_station_rel-分页列表查询")
	@ApiOperation(value="repair_pool_station_rel-分页列表查询", notes="repair_pool_station_rel-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RepairPoolStationRel>> queryPageList(RepairPoolStationRel repairPoolStationRel,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<RepairPoolStationRel> queryWrapper = QueryGenerator.initQueryWrapper(repairPoolStationRel, req.getParameterMap());
		Page<RepairPoolStationRel> page = new Page<RepairPoolStationRel>(pageNo, pageSize);
		IPage<RepairPoolStationRel> pageList = repairPoolStationRelService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param repairPoolStationRel
	 * @return
	 */
	@AutoLog(value = "repair_pool_station_rel-添加")
	@ApiOperation(value="repair_pool_station_rel-添加", notes="repair_pool_station_rel-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody RepairPoolStationRel repairPoolStationRel) {
		repairPoolStationRelService.save(repairPoolStationRel);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param repairPoolStationRel
	 * @return
	 */
	@AutoLog(value = "repair_pool_station_rel-编辑")
	@ApiOperation(value="repair_pool_station_rel-编辑", notes="repair_pool_station_rel-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody RepairPoolStationRel repairPoolStationRel) {
		repairPoolStationRelService.updateById(repairPoolStationRel);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "repair_pool_station_rel-通过id删除")
	@ApiOperation(value="repair_pool_station_rel-通过id删除", notes="repair_pool_station_rel-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		repairPoolStationRelService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "repair_pool_station_rel-批量删除")
	@ApiOperation(value="repair_pool_station_rel-批量删除", notes="repair_pool_station_rel-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.repairPoolStationRelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "repair_pool_station_rel-通过id查询")
	@ApiOperation(value="repair_pool_station_rel-通过id查询", notes="repair_pool_station_rel-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RepairPoolStationRel> queryById(@RequestParam(name="id",required=true) String id) {
		RepairPoolStationRel repairPoolStationRel = repairPoolStationRelService.getById(id);
		if(repairPoolStationRel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(repairPoolStationRel);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param repairPoolStationRel
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, RepairPoolStationRel repairPoolStationRel) {
        return super.exportXls(request, repairPoolStationRel, RepairPoolStationRel.class, "repair_pool_station_rel");
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
        return super.importExcel(request, response, RepairPoolStationRel.class);
    }

}
