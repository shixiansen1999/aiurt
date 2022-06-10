package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import java.util.ArrayList;
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
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartLend;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartLendQuery;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SparePartLendVO;
import com.swsc.copsms.modules.secondLevelWarehouse.service.ISparePartLendService;
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
 * @Description: 备件借出表
 * @Author: qian
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="备件借出表")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartLend")
public class SparePartLendController {
	@Autowired
	private ISparePartLendService sparePartLendService;

	 /**
	  * 备件借出表-分页列表查询
	  * @param sparePartLendQuery
	  * @param req
	  * @return
	  */
	@AutoLog(value = "备件借出表-分页列表查询")
	@ApiOperation(value="备件借出表-分页列表查询", notes="备件借出表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartLendVO>> queryPageList(SparePartLendQuery sparePartLendQuery,
									  HttpServletRequest req) {
		Result<IPage<SparePartLendVO>> result = new Result<IPage<SparePartLendVO>>();
		Page<SparePartLendVO> page = new Page<SparePartLendVO>(sparePartLendQuery.getPageNo(), sparePartLendQuery.getPageSize());
		IPage<SparePartLendVO> pageList = sparePartLendService.queryPageList(page, sparePartLendQuery);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param sparePartLend
	 * @return
	 */
	@AutoLog(value = "备件借出表-添加")
	@ApiOperation(value="备件借出表-添加", notes="备件借出表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SparePartLend sparePartLend) {
		Result<?> result = new Result<SparePartLend>();
		try {
			result=sparePartLendService.addLend(result,sparePartLend);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  备件还回
	 * @return
	 */
	@AutoLog(value = "备件还回-编辑")
	@ApiOperation(value="备件还回-编辑", notes="备件还回-编辑")
	@GetMapping(value = "/edit")
	public Result<SparePartLend> edit(@ApiParam("id")@RequestParam("id") Integer id,
									  @ApiParam("还回数量")@RequestParam("returnNum") Integer returnNum) {
		Result<SparePartLend> result = new Result<SparePartLend>();
		SparePartLend sparePartLendEntity = sparePartLendService.getById(id);
		if(returnNum==null||returnNum<0){
			result.error500("还回数量不能为空或者小于0");
		}
		if(sparePartLendEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = sparePartLendService.returnMaterial(sparePartLendEntity,returnNum);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}


  /**
      * 导出excel
   *
   * @param request
   * @param response
   */
  @AutoLog("备件借出信息-导出")
  @ApiOperation("备件借出信息导出")
  @GetMapping(value = "/exportXls")
  public ModelAndView exportXls(SparePartLendQuery sparePartLendQuery,
								HttpServletRequest request,
								HttpServletResponse response) {
		// 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
	  List<SparePartLendExcel> list = sparePartLendService.exportXls(sparePartLendQuery);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "备件借出表列表");
      mv.addObject(NormalExcelConstants.CLASS, SparePartLendExcel.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件借出表列表数据", "导出人:Jeecg", "导出信息"));
      mv.addObject(NormalExcelConstants.DATA_LIST, list);
      return mv;
  }


}
