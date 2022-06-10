package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2Query;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockLevel2VO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IStockLevel2Service;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

/**
 * @Description: 二级库库存信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
@Slf4j
@Api(tags="二级库库存信息")
@RestController
@RequestMapping("/secondLevelWarehouse/stockLevel2")
public class StockLevel2Controller {
	@Autowired
	private IStockLevel2Service stockLevel2Service;

	/**
	 * 二级库库存信息
	 * @param stockLevel2Query
	 * @param req
	 * @return
	 * @throws ParseException
	 */
	@AutoLog(value = "二级库库存信息-列表查询")
	@ApiOperation(value="二级库库存信息-列表查询", notes="二级库库存信息-列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StockLevel2VO>> queryPageList(StockLevel2Query stockLevel2Query,
									  HttpServletRequest req) {
		Result<IPage<StockLevel2VO>> result = new Result<IPage<StockLevel2VO>>();
		Page<StockLevel2VO> page = new Page<StockLevel2VO>(stockLevel2Query.getPageNo(), stockLevel2Query.getPageSize());
		IPage<StockLevel2VO> pageList = stockLevel2Service.queryPageList(page, stockLevel2Query);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}


	/**
	 * 填写备注
	 * @param id
	 * @param remark
	 * @return
	 */
	@AutoLog(value = "填写备注")
	@ApiOperation(value = "填写备注", notes = "填写备注")
	@GetMapping("/addRemark")
	public Result addRemark(@RequestParam(name = "id", required = true) Integer id, @RequestParam(name = "remark", required = true) String remark) {
		stockLevel2Service.addRemark(id, remark);
		return Result.ok();
	}

	/**
	 * 导出excel
	 * @param stockLevel2Query
	 * @return
	 */
	@GetMapping(value = "/exportXls")
	public ModelAndView exportXls(StockLevel2Query stockLevel2Query){

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<StockLevel2VO> pageList = stockLevel2Service.exportXls(stockLevel2Query);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME,"二级库库存管理列表");
		mv.addObject(NormalExcelConstants.CLASS,StockLevel2VO.class);
		mv.addObject(NormalExcelConstants.PARAMS,new ExportParams("二级库库存列表数据","导出信息", ExcelType.XSSF));
		mv.addObject(NormalExcelConstants.DATA_LIST,pageList);
		return mv;
	}


}
