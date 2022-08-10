package com.aiurt.modules.sparepart.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.mapper.SparePartStockInfoMapper;
import com.aiurt.modules.sparepart.service.ISparePartReturnOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import lombok.extern.slf4j.Slf4j;


import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
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
@Api(tags="备件管理-备件退库")
@RestController
@RequestMapping("/sparepart/sparePartReturnOrder")
@Slf4j
public class SparePartReturnOrderController extends BaseController<SparePartReturnOrder, ISparePartReturnOrderService> {
	@Autowired
	private ISparePartReturnOrderService sparePartReturnOrderService;
	@Autowired
	private SparePartStockInfoMapper sparePartStockInfoMapper;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartReturnOrder
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件退库分页列表查询",permissionUrl = "/sparepart/sparePartReturnOrder/list")
	@ApiOperation(value="spare_part_return_order-分页列表查询", notes="spare_part_return_order-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent = "sparePartsFor/back")
	public Result<IPage<SparePartReturnOrder>> queryPageList(SparePartReturnOrder sparePartReturnOrder,
															 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
															 HttpServletRequest req) {
		//QueryWrapper<SparePartReturnOrder> queryWrapper = QueryGenerator.initQueryWrapper(sparePartReturnOrder, req.getParameterMap());
		Page<SparePartReturnOrder> page = new Page<SparePartReturnOrder>(pageNo, pageSize);
		List<SparePartReturnOrder> list = sparePartReturnOrderService.selectList(page, sparePartReturnOrder);
		page.setRecords(list);
		return Result.OK(page);
	}

	/**
	 *   添加
	 *
	 * @param sparePartReturnOrder
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加备件退库",permissionUrl = "/sparepart/sparePartReturnOrder/list")
	@ApiOperation(value="spare_part_return_order-添加", notes="spare_part_return_order-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SparePartReturnOrder sparePartReturnOrder) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SparePartStockInfo::getWarehouseCode,sparePartReturnOrder.getMaterialCode());
		wrapper.eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0);
		SparePartStockInfo stockInfo = sparePartStockInfoMapper.selectOne(wrapper);
		if(null!=stockInfo){
			sparePartReturnOrder.setOrgId(stockInfo.getOrganizationId());
		}
		sparePartReturnOrder.setSysOrgCode(user.getOrgCode());
		sparePartReturnOrder.setUserId(user.getUsername());
		sparePartReturnOrderService.save(sparePartReturnOrder);
		return Result.OK("添加成功！");

	}

	/**
	 *  编辑
	 *
	 * @param sparePartReturnOrder
	 * @return
	 */
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑备件退库",permissionUrl = "/sparepart/sparePartReturnOrder/list")
	@ApiOperation(value="spare_part_return_order-编辑", notes="spare_part_return_order-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SparePartReturnOrder sparePartReturnOrder) {
		return sparePartReturnOrderService.update(sparePartReturnOrder);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "通过id删除备件退库",permissionUrl = "/sparepart/sparePartReturnOrder/list")
	@ApiOperation(value="spare_part_return_order-通过id删除", notes="spare_part_return_order-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		SparePartReturnOrder sparePartReturnOrder = sparePartReturnOrderService.getById(id);
		sparePartReturnOrder.setDelFlag(CommonConstant.DEL_FLAG_1);
		sparePartReturnOrderService.updateById(sparePartReturnOrder);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询备件退库",permissionUrl = "/sparepart/sparePartReturnOrder/list")
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
    * @param
    */
	@AutoLog(value = "导出",operateType = 6,operateTypeAlias = "导出备件退库",permissionUrl = "/sparepart/sparePartReturnOrder/list")
    @RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(@ApiParam(value = "行数据ids" ,required = true) @RequestParam("ids") String ids, HttpServletRequest request, HttpServletResponse response) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		SparePartReturnOrder sparePartReturnOrder = new SparePartReturnOrder();
		sparePartReturnOrder.setIds(Arrays.asList(ids.split(",")));
		List<SparePartReturnOrder> list = sparePartReturnOrderService.selectList(null, sparePartReturnOrder);
		list = list.stream().distinct().collect(Collectors.toList());
		for(int i=0;i<list.size();i++){
			SparePartReturnOrder order = list.get(i);
			order.setNumber(i+1+"");
		}
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "备件退库管理列表");
		mv.addObject(NormalExcelConstants.CLASS, SparePartReturnOrder.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件退库管理列表数据", "导出人:"+user.getRealname(), "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, list);
		return mv;
	}


}
