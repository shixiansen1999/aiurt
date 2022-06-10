package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockInDetailVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockInOrderLevel2DetailService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 二级入库单详细信息
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="二级入库单详细信息")
@RestController
@RequestMapping("/secondLevelWarehouse/stockInOrderLevel2Detail")
public class StockInOrderLevel2DetailController {

	@Resource
	private IStockInOrderLevel2DetailService stockInOrderLevel2DetailService;
	/**
	  * 分页列表查询
	 * @param orderCode
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "二级入库单详细信息-分页列表查询")
	@ApiOperation(value="二级入库单详细信息-分页列表查询", notes="二级入库单详细信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StockInDetailVO>> queryPageList(
										@RequestParam(value = "orderCode",required = false) String orderCode,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		if (StringUtils.isBlank(orderCode)){
			return Result.ok(new Page<>());
		}
		IPage<StockInDetailVO> page = new Page<>(pageNo, pageSize);
		Result<IPage<StockInDetailVO>> result = new Result<>();
		IPage<StockInDetailVO> stockInDetailVOIPage = stockInOrderLevel2DetailService.queryPageList(page, orderCode);
		result.setSuccess(true);
		result.setResult(stockInDetailVOIPage);
		return result;
	}


	 /**
	  * 根据id添加数量
	  * @param dto
	  * @return
	  */
	 @AutoLog(value = "根据id添加数量")
	 @ApiOperation(value="根据id添加数量", notes="根据id添加数量")
	 @PostMapping(value = "/addNumById")
	 public Result addNumById(@RequestBody List<StockDTO> dto){
	 	try {
			stockInOrderLevel2DetailService.addNumById(dto);
		}catch (Exception e){
			return Result.error("添加数量失败："+e.getMessage());
		}
		return Result.ok("添加数量成功");
	 }

	 /**
	  * 通过id删除
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "通过id删除")
	 @ApiOperation(value = "通过id删除", notes = "通过id删除")
	 @DeleteMapping(value = "/delete")
	 public Result<?> delete(@RequestParam(name = "id", required = true) Integer id) {
		 try {
			 stockInOrderLevel2DetailService.removeById(id);
		 } catch (Exception e) {
			 log.error("删除失败", e.getMessage());
			 return Result.error("删除失败!");
		 }
		 return Result.ok("删除成功!");
	 }
}
