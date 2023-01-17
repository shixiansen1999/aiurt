package com.aiurt.modules.sparepart.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.dto.StockApplyExcel;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import lombok.extern.slf4j.Slf4j;
import com.aiurt.common.system.base.controller.BaseController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
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
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询备件入库",permissionUrl = "/sparepart/sparePartInOrder/list")
	@ApiOperation(value="spare_part_in_order-分页列表查询", notes="spare_part_in_order-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent = "sparePartsFor/SparePartInOrderList")
	public Result<IPage<SparePartInOrder>> queryPageList(SparePartInOrder sparePartInOrder,
														 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														 HttpServletRequest req) {
/*		QueryWrapper<SparePartInOrder> queryWrapper = QueryGenerator.initQueryWrapper(sparePartInOrder, req.getParameterMap());*/
		Page<SparePartInOrder> page = new Page<>(pageNo, pageSize);
		List<SparePartInOrder> list = sparePartInOrderService.selectList(page, sparePartInOrder);
        page.setRecords(list);
		return Result.OK(page);
	}
	/**
	 * 备件入库-获取保管仓库查询条件
	 *
	 * @param sparePartInOrder
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件入库-获取保管仓库查询条件",permissionUrl = "/sparepart/sparePartInOrder/list")
	@ApiOperation(value="备件入库-获取保管仓库查询条件", notes="备件入库-获取保管仓库查询条件")
	@GetMapping(value = "/selectList")
	@PermissionData(pageComponent = "sparePartsFor/SparePartInOrderList")
	public Result<?> selectList(SparePartInOrder sparePartInOrder, HttpServletRequest req) {
		List<SparePartInOrder> list = sparePartInOrderService.selectList(null, sparePartInOrder);
		List<String> newList = list.stream().map(SparePartInOrder::getWarehouseName).collect(Collectors.toList());
		newList = newList.stream().distinct().collect(Collectors.toList());
		newList.remove(null);
		return Result.OK(newList);
	}
	/**
	 *  确认
	 *
	 * @param sparePartInOrder
	 * @return
	 */
	@AutoLog(value = "确认",operateType = 3,operateTypeAlias = "确认备件入库",permissionUrl = "/sparepart/sparePartInOrder/list")
	@ApiOperation(value="spare_part_in_order-确认", notes="spare_part_in_order-确认")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SparePartInOrder sparePartInOrder) {
		return sparePartInOrderService.update(sparePartInOrder);
	}

	/**
	 *  批量入库
	 *
	 * @param list
	 * @return
	 */
	@AutoLog(value = "批量入库",operateType = 3,operateTypeAlias = "备件批量入库",permissionUrl = "/sparepart/sparePartInOrder/list")
	@ApiOperation(value="spare_part_in_order-批量入库", notes="spare_part_in_order-批量入库")
	@RequestMapping(value = "/batchStorage", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> batchStorage(@RequestBody List<SparePartInOrder> list) {
		return sparePartInOrderService.batchStorage(list);
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询备件入库",permissionUrl = "/sparepart/sparePartInOrder/list")
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
	 * @param ids
	 */
	@AutoLog(value = "导出",operateType = 6,operateTypeAlias = "导出备件入库",permissionUrl = "/sparepart/sparePartInOrder/list")
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(@ApiParam(value = "行数据ids" ,required = true) @RequestParam("ids") String ids, HttpServletRequest request, HttpServletResponse response) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		SparePartInOrder sparePartInOrder = new SparePartInOrder();
		sparePartInOrder.setIds(Arrays.asList(ids.split(",")));
		List<SparePartInOrder> list = sparePartInOrderService.selectList(null, sparePartInOrder);
		list = list.stream().distinct().collect(Collectors.toList());
        for(int i=0;i<list.size();i++){
			SparePartInOrder order = list.get(i);
			order.setNumber(i+1+"");
		}
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "备件入库单列表");
		mv.addObject(NormalExcelConstants.CLASS, SparePartInOrder.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件入库单列表数据", "导出人:"+user.getRealname(), "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, list);
		return mv;
	}

	/**
	 * 备件入库导入模板下载
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	@AutoLog(value = "备件入库导入模板下载", operateType =  6, operateTypeAlias = "导出excel", permissionUrl = "")
	@ApiOperation(value="备件入库导入模板下载", notes="备件入库导入模板下载")
	@RequestMapping(value = "/exportTemplateXls",method = RequestMethod.GET)
	public void exportTemplateXl(HttpServletResponse response, HttpServletRequest request) throws IOException {
		sparePartInOrderService.getImportTemplate(response,request);
	}

	@AutoLog(value = "备件入库-通过excel导入数据", operateType =  6, operateTypeAlias = "通过excel导入数据", module = ModuleType.INSPECTION)
	@ApiOperation(value="备件入库-通过excel导入数据", notes="备件入库-通过excel导入数据")
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception{
		return sparePartInOrderService.importExcel(request,response);
	}


}
