package com.aiurt.modules.sparepart.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.aiurt.modules.sparepart.service.ISparePartReturnOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: spare_part_return_order
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Api(tags="spare_part_return_order")
@RestController
@RequestMapping("/sparepart/sparePartReturnOrder")
@Slf4j
public class SparePartReturnOrderController extends BaseController<SparePartReturnOrder, ISparePartReturnOrderService> {
	@Autowired
	private ISparePartReturnOrderService sparePartReturnOrderService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartReturnOrder
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "spare_part_return_order-分页列表查询")
	@ApiOperation(value="spare_part_return_order-分页列表查询", notes="spare_part_return_order-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartReturnOrder>> queryPageList(SparePartReturnOrder sparePartReturnOrder,
															 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
															 HttpServletRequest req) {
		QueryWrapper<SparePartReturnOrder> queryWrapper = QueryGenerator.initQueryWrapper(sparePartReturnOrder, req.getParameterMap());
		Page<SparePartReturnOrder> page = new Page<SparePartReturnOrder>(pageNo, pageSize);
		IPage<SparePartReturnOrder> pageList = sparePartReturnOrderService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param sparePartReturnOrder
	 * @return
	 */
	@AutoLog(value = "spare_part_return_order-添加")
	@ApiOperation(value="spare_part_return_order-添加", notes="spare_part_return_order-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartReturnOrder sparePartReturnOrder) {
		sparePartReturnOrderService.save(sparePartReturnOrder);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sparePartReturnOrder
	 * @return
	 */
	@AutoLog(value = "spare_part_return_order-编辑")
	@ApiOperation(value="spare_part_return_order-编辑", notes="spare_part_return_order-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SparePartReturnOrder sparePartReturnOrder) {
		sparePartReturnOrderService.updateById(sparePartReturnOrder);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "spare_part_return_order-通过id删除")
	@ApiOperation(value="spare_part_return_order-通过id删除", notes="spare_part_return_order-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sparePartReturnOrderService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "spare_part_return_order-批量删除")
	@ApiOperation(value="spare_part_return_order-批量删除", notes="spare_part_return_order-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sparePartReturnOrderService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "spare_part_return_order-通过id查询")
	@ApiOperation(value="spare_part_return_order-通过id查询", notes="spare_part_return_order-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartReturnOrder> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartReturnOrder sparePartReturnOrder = sparePartReturnOrderService.getById(id);
		if(sparePartReturnOrder==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartReturnOrder);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sparePartReturnOrder
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SparePartReturnOrder sparePartReturnOrder) {
        return super.exportXls(request, sparePartReturnOrder, SparePartReturnOrder.class, "spare_part_return_order");
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
        return super.importExcel(request, response, SparePartReturnOrder.class);
    }

}
