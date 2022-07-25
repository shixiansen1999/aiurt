package com.aiurt.modules.sparepart.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import lombok.extern.slf4j.Slf4j;
import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

/**
 * @Description: spare_part_in_order
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Api(tags="备件管理-备件入库")
@RestController
@RequestMapping("/sparepart/sparePartInOrder")
@Slf4j
public class SparePartInOrderController extends BaseController<SparePartInOrder, ISparePartInOrderService> {
	@Autowired
	private ISparePartInOrderService sparePartInOrderService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartInOrder
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "spare_part_in_order-分页列表查询")
	@ApiOperation(value="spare_part_in_order-分页列表查询", notes="spare_part_in_order-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartInOrder>> queryPageList(SparePartInOrder sparePartInOrder,
														 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														 HttpServletRequest req) {
/*		QueryWrapper<SparePartInOrder> queryWrapper = QueryGenerator.initQueryWrapper(sparePartInOrder, req.getParameterMap());*/
		Page<SparePartInOrder> page = new Page<SparePartInOrder>(pageNo, pageSize);
		List<SparePartInOrder> list = sparePartInOrderService.selectList(page, sparePartInOrder);
        page.setRecords(list);
		return Result.OK(page);
	}

	/**
	 *   添加
	 *
	 * @param sparePartInOrder
	 * @return
	 */
	@AutoLog(value = "spare_part_in_order-添加")
	@ApiOperation(value="spare_part_in_order-添加", notes="spare_part_in_order-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartInOrder sparePartInOrder) {
		sparePartInOrderService.save(sparePartInOrder);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sparePartInOrder
	 * @return
	 */
	@AutoLog(value = "spare_part_in_order-编辑")
	@ApiOperation(value="spare_part_in_order-编辑", notes="spare_part_in_order-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SparePartInOrder sparePartInOrder) {
		sparePartInOrderService.updateById(sparePartInOrder);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "spare_part_in_order-通过id删除")
	@ApiOperation(value="spare_part_in_order-通过id删除", notes="spare_part_in_order-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sparePartInOrderService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "spare_part_in_order-通过id查询")
	@ApiOperation(value="spare_part_in_order-通过id查询", notes="spare_part_in_order-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartInOrder> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartInOrder sparePartInOrder = sparePartInOrderService.getById(id);
		if(sparePartInOrder==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartInOrder);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param sparePartInOrder
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SparePartInOrder sparePartInOrder) {
		return super.exportXls(request, sparePartInOrder, SparePartInOrder.class, "spare_part_in_order");
	}


}
