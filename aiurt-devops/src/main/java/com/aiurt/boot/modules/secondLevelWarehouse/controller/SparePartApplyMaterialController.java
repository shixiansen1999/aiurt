package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SpareApplyMaterialDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartApplyMaterialService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 备件申领物资
 * @Author: qian
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags="备件申领物资")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartApplyMaterial")
public class SparePartApplyMaterialController {
	@Autowired
	private ISparePartApplyMaterialService sparePartApplyMaterialService;

	/**
	  * 备件申领物资详情
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "备件申领物资详情/出库详情/出库确认的列表-分页列表查询")
	@ApiOperation(value="备件申领物资详情/出库详情/出库确认的列表-分页列表查询", notes="备件申领物资详情/出库详情/出库确认的列表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SpareApplyMaterialDTO>> queryPageList(
										@ApiParam("申领单号/出库单号") @RequestParam("applyCode") String applyCode,
										@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  	@RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Result<IPage<SpareApplyMaterialDTO>> result = new Result<IPage<SpareApplyMaterialDTO>>();
		IPage<SpareApplyMaterialDTO> page = new Page<>(pageNo, pageSize);
		IPage<SpareApplyMaterialDTO> pageList = sparePartApplyMaterialService.queryPageList(page, applyCode);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}


}
