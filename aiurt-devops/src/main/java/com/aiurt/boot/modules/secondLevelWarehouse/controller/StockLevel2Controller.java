package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2;
import com.swsc.copsms.modules.secondLevelWarehouse.service.IStockLevel2Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static com.swsc.copsms.common.util.DateUtils.datetimeFormat;

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
	  * 列表查询
	 * @param stockLevel2
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "二级库库存信息-列表查询")
	@ApiOperation(value="二级库库存信息-列表查询", notes="二级库库存信息-列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StockLevel2>> queryPageList(StockLevel2 stockLevel2,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  @RequestParam(name="startTime",required = false) String startTime,
									  @RequestParam(name="endTime",required = false) String endTime,
									  HttpServletRequest req) throws ParseException {
		Result<IPage<StockLevel2>> result = new Result<IPage<StockLevel2>>();
		QueryWrapper<StockLevel2> queryWrapper = QueryGenerator.initQueryWrapper(stockLevel2, req.getParameterMap());
		if(StrUtil.isNotEmpty(startTime)&&StrUtil.isNotEmpty(endTime)){
			Date startDate = datetimeFormat.parse(startTime);
			Date endData = datetimeFormat.parse(endTime);
			queryWrapper.between(startTime!=null&&endTime!=null,"stock_in_time",startDate,endData);
		}
		Page<StockLevel2> page = new Page<StockLevel2>(pageNo, pageSize);
		IPage<StockLevel2> pageList = stockLevel2Service.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}



}
