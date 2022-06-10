package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartInExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartInQuery;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartInVO;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartInOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

 /**
 * @Description: 备件入库表
 * @Author: qian
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags="备件入库表")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartInOrder")
public class SparePartInOrderController {
	@Autowired
	private ISparePartInOrderService sparePartInOrderService;

	 /**
	  *
	  * @param sparePartInQuery
	  * @param req
	  * @return
	  */
	@AutoLog(value = "备件入库表-分页列表查询")
	@ApiOperation(value="备件入库表-分页列表查询", notes="备件入库表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SparePartInVO>> queryPageList(SparePartInQuery sparePartInQuery,
														 HttpServletRequest req) {
		Result<IPage<SparePartInVO>> result = new Result<IPage<SparePartInVO>>();
		Page<SparePartInVO> page = new Page<SparePartInVO>(sparePartInQuery.getPageNo(),sparePartInQuery.getPageSize());
		IPage<SparePartInVO> pageList = sparePartInOrderService.queryPageList(page, sparePartInQuery);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "备件入库表-通过id删除")
	@ApiOperation(value="备件入库表-通过id删除", notes="备件入库表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			sparePartInOrderService.removeById(id);
		} catch (Exception e) {
			log.error("删除失败 {}",e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}


	 /**
	  * 导出excel
	  * @param sparePartInQuery
	  * @return
	  */
  @AutoLog("备件入库信息-导出")
  @ApiOperation("备件入库导出")
  @GetMapping(value = "/exportXls")
  public ModelAndView exportXls(SparePartInQuery sparePartInQuery) {
	  //Step.2 AutoPoi 导出Excel
	  ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

	  List<SparePartInExcel> sparePartInExcelIPage = sparePartInOrderService.exportXls(sparePartInQuery);
	  //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "备件入库表列表");
      mv.addObject(NormalExcelConstants.CLASS, SparePartInExcel.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件入库表列表数据","导出信息", ExcelType.XSSF));
      mv.addObject(NormalExcelConstants.DATA_LIST, sparePartInExcelIPage);
      return mv;
  }

	 /**
	  * 批量确认
	  * @param ids
	  * @return
	  */
  @AutoLog(value = "批量确认")
  @ApiOperation(value="批量确认", notes="批量确认")
  @GetMapping(value = "/confirmBatch")
  public Result<?> confirmBatch(@RequestParam String ids,HttpServletRequest req) {
  	try {
  		sparePartInOrderService.confirmBatch(ids,req);
  		return Result.ok("确认成功");
	}catch (Exception e) {
  		log.error("确认失败");
  		return Result.error(e.getMessage());
	}
  }


}
