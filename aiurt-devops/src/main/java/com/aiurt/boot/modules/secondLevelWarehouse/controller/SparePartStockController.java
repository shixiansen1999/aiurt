package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.result.SparePartStockResult;
import com.aiurt.boot.modules.fault.param.SparePartStockParam;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartStockDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SpareMaterialVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

 /**
 * @Description: 备件库存
 * @Author: qian
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags="备件库存")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartStock")
public class SparePartStockController {
	@Autowired
	private ISparePartStockService sparePartStockService;
	/**
	  * 分页列表查询
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "备件库存-分页列表查询")
	@ApiOperation(value="备件库存-分页列表查询", notes="备件库存-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartStockDTO>> queryPageList(SparePartStockDTO sparePartStockDTO,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Result<IPage<SparePartStockDTO>> result = new Result<IPage<SparePartStockDTO>>();
		IPage<SparePartStockDTO> page = new Page<>(pageNo, pageSize);
		IPage<SparePartStockDTO> sparePartStockDTOIPage = sparePartStockService.queryPageList(page, sparePartStockDTO);
		result.setSuccess(true);
		result.setResult(sparePartStockDTOIPage);
		return result;
	}

	 @AutoLog("物料信息-查询")
	 @ApiOperation("物料信息-查询")
	 @GetMapping("/materialByWarehouse")
	 public Result<List<SpareMaterialVO>> queryMaterialByWarehouse(HttpServletRequest req) {
		 Result<List<SpareMaterialVO>> result = new Result<List<SpareMaterialVO>>();
		 List<SpareMaterialVO> sparePartStockInfos = sparePartStockService.queryMaterialByWarehouse(req);
		 result.setSuccess(true);
		 result.setResult(sparePartStockInfos);
		 return result;
	 }

	 /**
	  * 查询本班组的备件信息
	  * @param param
	  * @return
	  */
	 @AutoLog(value = "查询本班组的备件信息")
	 @ApiOperation(value="查询本班组的备件信息", notes="查询本班组的备件信息")
	 @GetMapping(value = "/queryStockList")
	 public Result<IPage<SparePartStockResult>> queryStockList(@Valid SparePartStockParam param,
															   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		 Result<IPage<SparePartStockResult>> result = new Result<>();
		 IPage<SparePartStockResult> page = new Page<>(pageNo, pageSize);
		 IPage<SparePartStockResult> sparePartStockResults = sparePartStockService.queryStockList(page,param);
		 result.setSuccess(true);
		 result.setResult(sparePartStockResults);
		 return result;
	 }

	 /**
	  * 添加备注
	  * @param id
	  * @param remark
	  * @return
	  */
	 @AutoLog(value = "添加备注")
	 @ApiOperation(value = "添加备注", notes = "添加备注")
	 @GetMapping("addRemark")
	 public Result addRemark(@RequestParam(name = "id", required = true) Integer id, @RequestParam(name = "remark", required = true) String remark) {
		sparePartStockService.addRemark(id,remark);
		return Result.ok();
	 }
}
