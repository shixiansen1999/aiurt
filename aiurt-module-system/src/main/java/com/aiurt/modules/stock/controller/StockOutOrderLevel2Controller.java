package com.aiurt.modules.stock.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.aiurt.modules.stock.service.IStockOutOrderLevel2Service;
import com.aiurt.modules.stock.service.IStockOutboundMaterialsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @Description: 二级库出库管理
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "二级库出库管理")
@RestController
@RequestMapping("/stock/stockOutOrderLevel2")
public class StockOutOrderLevel2Controller {

    @Autowired
    private IStockOutOrderLevel2Service iStockOutOrderLevel2Service;
    /**
     * 分页列表查询
     *
     * @param stockOutOrderLevel2
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "二级库出库管理-分页列表查询")
    @ApiOperation(value = "二级库出库管理-分页列表查询", notes = "二级库出库管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StockOutOrderLevel2>> queryPageList(StockOutOrderLevel2 stockOutOrderLevel2,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        Result<IPage<StockOutOrderLevel2>> result = new Result<IPage<StockOutOrderLevel2>>();
        Page<StockOutOrderLevel2> page = new Page<StockOutOrderLevel2>(pageNo, pageSize);
        IPage<StockOutOrderLevel2> pageList = iStockOutOrderLevel2Service.pageList(page, stockOutOrderLevel2);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 二级库出库管理详情查询
     * @param id
     * @return
     */
    @ApiOperation(value = "二级库出库管理-详情查询", notes = "二级库出库管理-详情查询")
    @GetMapping(value = "/queryById")
    public Result<SparePartApply> queryById(@RequestParam(name = "id", required = true) String id) {
		SparePartApply sparePartApply = iStockOutOrderLevel2Service.getList(id);
		return Result.ok(sparePartApply);
    }

	/**
	 * 二级库出库管理提交
	 * @return
	 */
	@ApiOperation(value = "二级库出库管理-确认出库", notes = "二级库出库管理-确认出库")
	@PostMapping(value = "/confirmOutOrder")
	public Result<?> confirmOutOrder(@RequestBody SparePartApply sparePartApply) {
		try {
			iStockOutOrderLevel2Service.confirmOutOrder(sparePartApply);
			return Result.ok("出库成功！");
		}catch (Exception e){
		    e.printStackTrace();
			return Result.ok("出库失败！");
		}
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
		iStockOutOrderLevel2Service.save(stockOutOrderLevel2);
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
		iStockOutOrderLevel2Service.updateById(stockOutOrderLevel2);
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
		iStockOutOrderLevel2Service.removeById(id);
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
		this.iStockOutOrderLevel2Service.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

}
