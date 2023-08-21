package com.aiurt.modules.sparepart.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.sparepart.entity.SparePartStockNum;
import com.aiurt.modules.sparepart.service.ISparePartStockNumService;
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
 * @Description: spare_part_stock_num
 * @Author: aiurt
 * @Date:   2023-08-09
 * @Version: V1.0
 */
@Api(tags="spare_part_stock_num")
@RestController
@RequestMapping("/sparepartstocknum/sparePartStockNum")
@Slf4j
public class SparePartStockNumController extends BaseController<SparePartStockNum, ISparePartStockNumService> {
	@Autowired
	private ISparePartStockNumService sparePartStockNumService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartStockNum
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "spare_part_stock_num-分页列表查询")
	@ApiOperation(value="spare_part_stock_num-分页列表查询", notes="spare_part_stock_num-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartStockNum>> queryPageList(SparePartStockNum sparePartStockNum,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SparePartStockNum> queryWrapper = QueryGenerator.initQueryWrapper(sparePartStockNum, req.getParameterMap());
		Page<SparePartStockNum> page = new Page<SparePartStockNum>(pageNo, pageSize);
		IPage<SparePartStockNum> pageList = sparePartStockNumService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param sparePartStockNum
	 * @return
	 */
	@AutoLog(value = "spare_part_stock_num-添加")
	@ApiOperation(value="spare_part_stock_num-添加", notes="spare_part_stock_num-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartStockNum sparePartStockNum) {
		sparePartStockNumService.save(sparePartStockNum);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sparePartStockNum
	 * @return
	 */
	@AutoLog(value = "spare_part_stock_num-编辑")
	@ApiOperation(value="spare_part_stock_num-编辑", notes="spare_part_stock_num-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SparePartStockNum sparePartStockNum) {
		sparePartStockNumService.updateById(sparePartStockNum);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "spare_part_stock_num-通过id删除")
	@ApiOperation(value="spare_part_stock_num-通过id删除", notes="spare_part_stock_num-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sparePartStockNumService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "spare_part_stock_num-批量删除")
	@ApiOperation(value="spare_part_stock_num-批量删除", notes="spare_part_stock_num-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sparePartStockNumService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "spare_part_stock_num-通过id查询")
	@ApiOperation(value="spare_part_stock_num-通过id查询", notes="spare_part_stock_num-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartStockNum> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartStockNum sparePartStockNum = sparePartStockNumService.getById(id);
		if(sparePartStockNum==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartStockNum);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sparePartStockNum
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SparePartStockNum sparePartStockNum) {
        return super.exportXls(request, sparePartStockNum, SparePartStockNum.class, "spare_part_stock_num");
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
        return super.importExcel(request, response, SparePartStockNum.class);
    }

}
