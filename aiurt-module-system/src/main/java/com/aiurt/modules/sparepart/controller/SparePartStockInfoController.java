package com.aiurt.modules.sparepart.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.aiurt.modules.sparepart.service.ISparePartStockInfoService;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
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

import java.io.IOException;
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
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "备件仓库分页列表查询",permissionUrl = "/sparepart/sparePartStockInfo/list")
	@ApiOperation(value="spare_part_stock_info-分页列表查询", notes="spare_part_stock_info-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartStockInfo>> queryPageList(SparePartStockInfo sparePartStockInfo,
														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														   HttpServletRequest req) {
		QueryWrapper<SparePartStockInfo> queryWrapper = QueryGenerator.initQueryWrapper(sparePartStockInfo, req.getParameterMap());
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if(ObjectUtil.isNotNull(sparePartStockInfo.getModule())){
			queryWrapper.lambda().notIn(SparePartStockInfo::getOrganizationId,user.getOrgId());
		}
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
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加备件仓库",permissionUrl = "/sparepart/sparePartStockInfo/list")
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
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑备件仓库分",permissionUrl = "/sparepart/sparePartStockInfo/list")
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
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "删除备件仓库",permissionUrl = "/sparepart/sparePartStockInfo/list")
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
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询备件仓库",permissionUrl = "/sparepart/sparePartStockInfo/list")
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
	 * @param

	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "登录人所选班组的仓库",permissionUrl = "/sparepart/sparePartStockInfo/list")
	@ApiOperation(value="备件管理-备件仓库-登录人所选班组的仓库", notes="备件管理-备件仓库-登录人所选班组的仓库")
	@GetMapping(value = "/stockInfoList")
	public Result<?> queryPageList() {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SparePartStockInfo::getOrganizationId,user.getOrgId());
		List<SparePartStockInfo> list = sparePartStockInfoService.list(wrapper);
		return Result.OK(list);
	}

	@AutoLog(value = "系统管理-基础数据-备件仓库-下拉列表查询（部门权限）", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/sparepart/sparePartStockInfo/list")
	@ApiOperation(value = "系统管理-基础数据-备件仓库-下拉列表查询", notes = "系统管理-基础数据-备件仓库-下拉列表查询")
	@GetMapping(value = "/selectListAuth")
	@PermissionData(pageComponent = "manage/StockSparePartList")
	public Result<List<SparePartStockInfo>> selectList(SparePartStockInfo sparePartStockInfo,
													HttpServletRequest req) {
		Result<List<SparePartStockInfo>> result = new Result<List<SparePartStockInfo>>();
		QueryWrapper<SparePartStockInfo> queryWrapper = new QueryWrapper<>();
		if(sparePartStockInfo.getWarehouseName() != null && !"".equals(sparePartStockInfo.getWarehouseName())){
			queryWrapper.like("warehouse_name",sparePartStockInfo.getWarehouseName());
		}
		if(sparePartStockInfo.getWarehouseCode() != null && !"".equals(sparePartStockInfo.getWarehouseCode())){
			queryWrapper.like("warehouse_code",sparePartStockInfo.getWarehouseCode());
		}
		if(sparePartStockInfo.getOrganizationId() != null && !"".equals(sparePartStockInfo.getOrganizationId())){
			queryWrapper.like("organization_id",sparePartStockInfo.getOrganizationId());
		}
		if(sparePartStockInfo.getWarehouseStatus() != null && !"".equals(sparePartStockInfo.getWarehouseStatus())){
			queryWrapper.like("warehouse_status",sparePartStockInfo.getWarehouseStatus());
		}
		queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		queryWrapper.orderByDesc("create_time");
		List<SparePartStockInfo> sparePartStockInfos = sparePartStockInfoService.list(queryWrapper);
		result.setSuccess(true);
		result.setResult(sparePartStockInfos);
		return result;
	}

	/**
	 * 下载导入模板
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	@ApiOperation(value = "系统管理-基础数据-下载导入模板", notes = "系统管理-基础数据-下载导入模板")
	@GetMapping("/downloadTemplateExcel")
	public void downloadTemplateExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
		sparePartStockInfoService.downloadTemplateExcel(request, response);
	}

	/**
	 * 系统管理-基础数据-备件仓库-导入
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "系统管理-基础数据-备件仓库-导入", notes = "系统管理-基础数据-备件仓库-导入")
	@PostMapping(value = "/importExcel")
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response){
		return sparePartStockInfoService.importExcel(request, response);

	}
}
