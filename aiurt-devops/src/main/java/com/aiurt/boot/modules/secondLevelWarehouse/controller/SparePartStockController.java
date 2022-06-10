package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartStock;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartStockInfo;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartStockDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SpareMaterialVO;
import com.swsc.copsms.modules.secondLevelWarehouse.service.ISparePartStockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
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

	 @AutoLog("某个备件仓库下的物料信息-查询")
	 @ApiOperation("某个备件仓库下的物料-查询")
	 @GetMapping("/materialByWarehouse")
	 public Result<List<SpareMaterialVO>> queryMaterialByWarehouse(
	 		@ApiParam("备件仓库编号") @RequestParam("warehouseCode") String warehouseCode) {
		 Result<List<SpareMaterialVO>> result = new Result<List<SpareMaterialVO>>();
		 List<SpareMaterialVO> sparePartStockInfos = sparePartStockService.queryMaterialByWarehouse(warehouseCode);
		 result.setSuccess(true);
		 result.setResult(sparePartStockInfos);
		 return result;
	 }

}
