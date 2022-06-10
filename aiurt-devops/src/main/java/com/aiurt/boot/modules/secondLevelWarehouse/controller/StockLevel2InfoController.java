package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.CsStockLevelTwoVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2InfoService;
import com.aiurt.common.aspect.annotation.AutoLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

 /**
 * @Description: 二级库仓库信息
 * @Author: qian
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="二级库仓库信息")
@RestController
@RequestMapping("/secondLevelWarehouse/stockLevel2Info")
public class StockLevel2InfoController {
	@Resource
	private IStockLevel2InfoService iStockLevel2InfoService;

	/**
	 * 从stock_level2_info表查询改为cs_stock_level_two查询
	  * 列表查询
	 * @return
	 */
	@AutoLog(value = "二级库仓库信息-列表查询")
	@ApiOperation(value="二级库仓库信息-列表查询", notes="二级库仓库信息-列表查询")
	@GetMapping(value = "/list")
	public Result<List<CsStockLevelTwoVO>> selectStockList() {
		Result<List<CsStockLevelTwoVO>> result = new Result<List<CsStockLevelTwoVO>>();
		List<CsStockLevelTwoVO> stockLevel2Infos = iStockLevel2InfoService.selectStockList();
		result.setSuccess(true);
		result.setResult(stockLevel2Infos);
		return result;
	}



}
