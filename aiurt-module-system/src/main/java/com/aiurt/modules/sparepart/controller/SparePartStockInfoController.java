package com.aiurt.modules.sparepart.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import lombok.extern.slf4j.Slf4j;


import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

import java.util.List;

/**
 * @Description: spare_part_stock_info
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Api(tags="备件管理-备件仓库")
@RestController
@RequestMapping("/sparepart/sparePartStockInfo")
@Slf4j
public class SparePartStockInfoController extends BaseController<SparePartStockInfo, ISparePartStockInfoService> {
	@Autowired
	private ISparePartStockInfoService sparePartStockInfoService;
	@Autowired
	private ISparePartInOrderService sparePartInOrderService;
	@Autowired
	private ISparePartStockService sparePartStockService;
	/**
	 * 分页列表查询
	 *
	 * @param sparePartStockInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "spare_part_stock_info-分页列表查询")
	@ApiOperation(value="spare_part_stock_info-分页列表查询", notes="spare_part_stock_info-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartStockInfo>> queryPageList(SparePartStockInfo sparePartStockInfo,
														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														   HttpServletRequest req) {
		QueryWrapper<SparePartStockInfo> queryWrapper = QueryGenerator.initQueryWrapper(sparePartStockInfo, req.getParameterMap());
		Page<SparePartStockInfo> page = new Page<SparePartStockInfo>(pageNo, pageSize);
		IPage<SparePartStockInfo> pageList = sparePartStockInfoService.page(page, queryWrapper.lambda().eq(SparePartStockInfo::getDelFlag, CommonConstant.DEL_FLAG_0));
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param sparePartStockInfo
	 * @return
	 */
	@AutoLog(value = "spare_part_stock_info-添加")
	@ApiOperation(value="spare_part_stock_info-添加", notes="spare_part_stock_info-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SparePartStockInfo sparePartStockInfo) {
		return sparePartStockInfoService.add(sparePartStockInfo);
	}

	/**
	 *  编辑
	 *
	 * @param sparePartStockInfo
	 * @return
	 */
	@AutoLog(value = "spare_part_stock_info-编辑")
	@ApiOperation(value="spare_part_stock_info-编辑", notes="spare_part_stock_info-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SparePartStockInfo sparePartStockInfo) {
		return sparePartStockInfoService.update(sparePartStockInfo);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "spare_part_stock_info-通过id删除")
	@ApiOperation(value="spare_part_stock_info-通过id删除", notes="spare_part_stock_info-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		SparePartStockInfo sparePartStockInfo = sparePartStockInfoService.getById(id);
		//判断是否被备件入库使用
		LambdaQueryWrapper<SparePartInOrder> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SparePartInOrder::getWarehouseCode,sparePartStockInfo.getWarehouseCode());
		wrapper.eq(SparePartInOrder::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<SparePartInOrder> list = sparePartInOrderService.list(wrapper);
		if(!list.isEmpty()){
			return Result.error("被备件入库使用中，不能删除!");
		}
        //判断是否被备件库存信息使用
		LambdaQueryWrapper<SparePartStock> stockWrapper = new LambdaQueryWrapper<>();
		stockWrapper.eq(SparePartStock::getWarehouseCode,sparePartStockInfo.getWarehouseCode());
		stockWrapper.eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<SparePartStock> stockList = sparePartStockService.list(stockWrapper);
		if(!stockList.isEmpty()){
			return Result.error("被备件库存信息使用中，不能删除!");
		}
		sparePartStockInfo.setDelFlag(CommonConstant.DEL_FLAG_1);
		sparePartStockInfoService.updateById(sparePartStockInfo);
		return Result.OK("删除成功!");
	}



	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "spare_part_stock_info-通过id查询")
	@ApiOperation(value="spare_part_stock_info-通过id查询", notes="spare_part_stock_info-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SparePartStockInfo> queryById(@RequestParam(name="id",required=true) String id) {
		SparePartStockInfo sparePartStockInfo = sparePartStockInfoService.getById(id);
		if(sparePartStockInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sparePartStockInfo);
	}
	/**
	 * 登录人所选班组的仓库
	 *
	 * @param sparePartStockInfo

	 * @return
	 */
	@AutoLog(value = "备件管理-备件仓库-登录人所选班组的仓库")
	@ApiOperation(value="备件管理-备件仓库-登录人所选班组的仓库", notes="备件管理-备件仓库-登录人所选班组的仓库")
	@GetMapping(value = "/stockInfoList")
	public Result<?> queryPageList() {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SparePartStockInfo::getOrganizationId,user.getOrgId());
		List<SparePartStockInfo> list = sparePartStockInfoService.list(wrapper);
		return Result.OK(list);
	}

}
