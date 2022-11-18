package com.aiurt.modules.sparepart.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.task.dto.OverhaulStatisticsDTOS;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.entity.dto.SparePartConsume;
import com.aiurt.modules.sparepart.entity.dto.SparePartStatistics;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.service.ISparePartStockService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: spare_part_stock
 * @Author: aiurt
 * @Date:   2022-07-25
 * @Version: V1.0
 */
@Api(tags="备件管理-备件库存信息")
@RestController
@RequestMapping("/sparepart/sparePartStock")
@Slf4j
public class SparePartStockController extends BaseController<SparePartStock, ISparePartStockService> {
	@Autowired
	private ISparePartStockService sparePartStockService;

	/**
	 * 分页列表查询
	 *
	 * @param sparePartStock
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件库存信息分页列表查询",permissionUrl = "/sparepart/sparePartStock/list")
	@ApiOperation(value="spare_part_stock-分页列表查询", notes="spare_part_stock-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData(pageComponent = "sparePartsFor/SparePartStockList")
	public Result<IPage<SparePartStock>> queryPageList(SparePartStock sparePartStock,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if(ObjectUtil.isNotNull(sparePartStock.getModule())){
			sparePartStock.setOrgId(user.getOrgId());
		}
		Page<SparePartStock> page = new Page<SparePartStock>(pageNo, pageSize);
		List<SparePartStock> list = sparePartStockService.selectList(page, sparePartStock);
		list = list.stream().distinct().collect(Collectors.toList());
		page.setRecords(list);
		return Result.OK(page);
	}
	 /**
	  * APP-分页列表查询
	  *
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "APP-列表查询",operateType = 1,operateTypeAlias = "App备件库存信息分页列表查询",permissionUrl = "/sparepart/sparePartStock/list")
	 @ApiOperation(value="spare_part_stock-app分页列表查询", notes="spare_part_stock-app分页列表查询")
	 @GetMapping(value = "/appList")
	 @PermissionData(pageComponent = "sparePartsFor/SparePartStockAppList")
	 public Result<IPage<SparePartStock>> appQueryPageList(@RequestParam(name="text",required=false) String text,
														@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														HttpServletRequest req) {
		 LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 Page<SparePartStock> page = new Page<SparePartStock>(pageNo, pageSize);
		 List<SparePartStock> list = sparePartStockService.selectAppList(page, user.getOrgId(),text);
		 list = list.stream().distinct().collect(Collectors.toList());
		 page.setRecords(list);
		 return Result.OK(page);
	 }
	 /**
	  * 备件库存-获取存放仓库查询条件
	  *
	  * @param sparePartStock
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件库存-获取存放仓库查询条件",permissionUrl = "/sparepart/sparePartStock/list")
	 @ApiOperation(value="备件库存-获取存放仓库查询条件", notes="备件库存-获取存放仓库查询条件")
	 @GetMapping(value = "/selectList")
	 @PermissionData(pageComponent = "sparePartsFor/SparePartStockList")
	 public Result<?> selectList(SparePartStock sparePartStock,HttpServletRequest req) {
		 LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 if(ObjectUtil.isNotNull(sparePartStock.getModule())){
			 sparePartStock.setOrgId(user.getOrgId());
		 }
		 List<SparePartStock> list = sparePartStockService.selectList(null,sparePartStock);
		 List<String> newList = list.stream().map(SparePartStock::getWarehouseName).collect(Collectors.toList());
		 newList = newList.stream().distinct().collect(Collectors.toList());
		 return Result.OK(newList);
	 }
	 /**
	  * 分页列表查询
	  *
	  * @param sparePartStock
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件库存信息分页列表查询",permissionUrl = "/sparepart/sparePartStock/list")
	 @ApiOperation(value="spare_part_stock-分页列表查询", notes="spare_part_stock-分页列表查询")
	 @GetMapping(value = "/queryLendList")
	 public Result<IPage<SparePartStock>> queryLendList(SparePartStock sparePartStock,
														@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														HttpServletRequest req) {
		 LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 if(ObjectUtil.isNotNull(sparePartStock.getModule())){
			 sparePartStock.setOrgId(user.getOrgId());
		 }
		 Page<SparePartStock> page = new Page<SparePartStock>(pageNo, pageSize);
		 List<SparePartStock> list = sparePartStockService.selectLendList(page, sparePartStock);
		 list = list.stream().distinct().collect(Collectors.toList());
		 page.setRecords(list);
		 return Result.OK(page);
	 }
	/**
	 *   添加
	 *
	 * @param sparePartStock
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加备件库存信息",permissionUrl = "/sparepart/sparePartStock/list")
	@ApiOperation(value="spare_part_stock-添加", notes="spare_part_stock-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SparePartStock sparePartStock) {
		sparePartStockService.save(sparePartStock);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sparePartStock
	 * @return
	 */
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑备件库存信息",permissionUrl = "/sparepart/sparePartStock/list")
	@ApiOperation(value="spare_part_stock-编辑", notes="spare_part_stock-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SparePartStock sparePartStock) {
		sparePartStockService.updateById(sparePartStock);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "删除备件库存信息",permissionUrl = "/sparepart/sparePartStock/list")
	@ApiOperation(value="spare_part_stock-通过id删除", notes="spare_part_stock-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sparePartStockService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询备件库存信息",permissionUrl = "/sparepart/sparePartStock/list")
	@ApiOperation(value="spare_part_stock-通过id查询", notes="spare_part_stock-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartStock> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartStock sparePartStock = sparePartStockService.getById(id);
		if(sparePartStock==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartStock);
	}
	 /**
	  * 登录人所选班组的仓库的备件
	  *
	  * @param sparePartStock

	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "登录人所选班组的仓库的备件",permissionUrl = "/sparepart/sparePartOutOrder/list")
	 @ApiOperation(value="备件管理-备件仓库-登录人所选班组的仓库的备件", notes="备件管理-备件仓库-登录人所选班组的仓库的备件")
	 @GetMapping(value = "/stockList")
	 public Result<?> queryPageList(SparePartStock sparePartStock) {
		 sparePartStock.setNum(999);
		 List<SparePartStock> list = sparePartStockService.selectList(null, sparePartStock);
		 return Result.OK(list);
	 }

	 /**
	  * 备件类型数量统计分析表
	  * @param sparePartStatistics
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "备件类型数量统计分析表",operateType = 1,operateTypeAlias = "备件类型数量统计分析表",permissionUrl = "/sparepart/sparePartStock/list")
	 @ApiOperation(value="备件类型数量统计分析表", notes="备件类型数量统计分析表")
	 @GetMapping(value = "/selectSparePartStatistics")
	 public Result<IPage<SparePartStatistics>> selectSparePartStatistics(SparePartStatistics sparePartStatistics,
																	@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
																	@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
																	HttpServletRequest req) {
		 Page<SparePartStatistics> pageList = new Page<>(pageNo, pageSize);
		 Page<SparePartStatistics> sparePartStatisticsPage = sparePartStockService.selectSparePartStatistics(pageList, sparePartStatistics);
		 return Result.OK(sparePartStatisticsPage);
	 }

	 /**
	  * 备件类型数量消耗态势
	  * @param sparePartConsume
	  * @return
	  */
	 @AutoLog(value = "备件类型数量消耗态势",operateType = 1,operateTypeAlias = "备件类型数量消耗态势",permissionUrl = "/sparepart/sparePartStock/list")
	 @ApiOperation(value="备件类型数量消耗态势", notes="备件类型数量消耗态势")
	 @GetMapping(value = "/selectConsume")
	 public Result<?> selectConsume(SparePartConsume sparePartConsume){
		 List<MaterialBaseType> sparePartConsumeList = sparePartStockService.selectConsume(sparePartConsume);
		 return Result.OK(sparePartConsumeList);
	 }

	 /**
	  * 统计分析-检修报表导出
	  *
	  * @param request
	  * @return
	  */
	 @AutoLog(value = "备件统计-报表导出",operateType = 1,operateTypeAlias = "备件统计-报表导出",permissionUrl = "/sparepart/sparePartStock/list")
	 @ApiOperation(value = "备件统计-报表导出", notes = "备件统计-报表导出")
	 @GetMapping(value = "/reportExport")
	 public ModelAndView reportExport(HttpServletRequest request, SparePartStatistics sparePartStatistics) {
		 return sparePartStockService.reportExport(request,sparePartStatistics);
	 }
}
