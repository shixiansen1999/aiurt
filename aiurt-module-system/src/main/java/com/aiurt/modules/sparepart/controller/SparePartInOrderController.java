package com.aiurt.modules.sparepart.controller;


import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.sparepart.entity.dto.SparePartInExcel;
import com.aiurt.modules.sparepart.entity.dto.SparePartInQuery;
import com.aiurt.modules.sparepart.entity.vo.SparePartInVO;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
													  HttpServletRequest req,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Result<IPage<SparePartInVO>> result = new Result<IPage<SparePartInVO>>();
		Page<SparePartInVO> page = new Page<SparePartInVO>(pageNo,pageSize);
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
			log.error("删除失败",e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}


  /**
      * 导出excel
   *
   * @param request
   * @param response
   */
  @AutoLog("备件入库信息-导出")
  @ApiOperation("备件入库导出")
  @GetMapping(value = "/exportXls")
  public ModelAndView exportXls(SparePartInQuery sparePartInQuery,
                                HttpServletRequest request, HttpServletResponse response) {
      // 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<SparePartInExcel> list = sparePartInOrderService.exportXls(sparePartInQuery);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "备件入库表列表");
      mv.addObject(NormalExcelConstants.CLASS, SparePartInExcel.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备件入库表列表数据", "导出人:Jeecg", "导出信息"));
      mv.addObject(NormalExcelConstants.DATA_LIST, list);
      return mv;
  }


}
