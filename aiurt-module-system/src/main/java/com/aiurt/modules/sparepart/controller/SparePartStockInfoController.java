package com.aiurt.modules.sparepart.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

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
		//判断备件管理，物资入库、物资库存是否使用  todo



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


}
