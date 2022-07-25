package com.aiurt.modules.stock.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.aiurt.modules.stock.service.IStockOutOrderLevel2Service;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
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
 * @Description: stock_out_order_level2
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Api(tags="stock_out_order_level2")
@RestController
@RequestMapping("/stock/stockOutOrderLevel2")
@Slf4j
public class StockOutOrderLevel2Controller extends BaseController<StockOutOrderLevel2, IStockOutOrderLevel2Service> {
	@Autowired
	private IStockOutOrderLevel2Service stockOutOrderLevel2Service;

	/**
	 * 分页列表查询
	 *
	 * @param stockOutOrderLevel2
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "stock_out_order_level2-分页列表查询")
	@ApiOperation(value="stock_out_order_level2-分页列表查询", notes="stock_out_order_level2-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StockOutOrderLevel2>> queryPageList(StockOutOrderLevel2 stockOutOrderLevel2,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<StockOutOrderLevel2> queryWrapper = QueryGenerator.initQueryWrapper(stockOutOrderLevel2, req.getParameterMap());
		Page<StockOutOrderLevel2> page = new Page<StockOutOrderLevel2>(pageNo, pageSize);
		IPage<StockOutOrderLevel2> pageList = stockOutOrderLevel2Service.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param stockOutOrderLevel2
	 * @return
	 */
	@AutoLog(value = "stock_out_order_level2-添加")
	@ApiOperation(value="stock_out_order_level2-添加", notes="stock_out_order_level2-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody StockOutOrderLevel2 stockOutOrderLevel2) {
		stockOutOrderLevel2Service.save(stockOutOrderLevel2);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param stockOutOrderLevel2
	 * @return
	 */
	@AutoLog(value = "stock_out_order_level2-编辑")
	@ApiOperation(value="stock_out_order_level2-编辑", notes="stock_out_order_level2-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody StockOutOrderLevel2 stockOutOrderLevel2) {
		stockOutOrderLevel2Service.updateById(stockOutOrderLevel2);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "stock_out_order_level2-通过id删除")
	@ApiOperation(value="stock_out_order_level2-通过id删除", notes="stock_out_order_level2-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		stockOutOrderLevel2Service.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "stock_out_order_level2-批量删除")
	@ApiOperation(value="stock_out_order_level2-批量删除", notes="stock_out_order_level2-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.stockOutOrderLevel2Service.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "stock_out_order_level2-通过id查询")
	@ApiOperation(value="stock_out_order_level2-通过id查询", notes="stock_out_order_level2-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<StockOutOrderLevel2> queryById(@RequestParam(name="id",required=true) String id) {
		StockOutOrderLevel2 stockOutOrderLevel2 = stockOutOrderLevel2Service.getById(id);
		if(stockOutOrderLevel2==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(stockOutOrderLevel2);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param stockOutOrderLevel2
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StockOutOrderLevel2 stockOutOrderLevel2) {
        return super.exportXls(request, stockOutOrderLevel2, StockOutOrderLevel2.class, "stock_out_order_level2");
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
        return super.importExcel(request, response, StockOutOrderLevel2.class);
    }

}
