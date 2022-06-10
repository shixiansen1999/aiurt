package com.aiurt.boot.modules.manage.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.manage.entity.StockLevelTwo;
import com.aiurt.boot.modules.manage.service.IStockLevelTwoService;
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

 /**
 * @Description: 二级仓库基础表
 * @Author: qian
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="二级仓库基础表")
@RestController
@RequestMapping("/manage/stockLevelTwo")
public class StockLevelTwoController {
	@Autowired
	private IStockLevelTwoService stockLevelTwoService;

	/**
	  * 分页列表查询
	 * @param stockLevelTwo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "二级仓库基础表-分页列表查询")
	@ApiOperation(value="二级仓库基础表-分页列表查询", notes="二级仓库基础表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StockLevelTwo>> queryPageList(StockLevelTwo stockLevelTwo,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<StockLevelTwo>> result = new Result<IPage<StockLevelTwo>>();
		QueryWrapper<StockLevelTwo> queryWrapper = QueryGenerator.initQueryWrapper(stockLevelTwo, req.getParameterMap());
		Page<StockLevelTwo> page = new Page<StockLevelTwo>(pageNo, pageSize);
		IPage<StockLevelTwo> pageList = stockLevelTwoService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param stockLevelTwo
	 * @return
	 */
	@AutoLog(value = "二级仓库基础表-添加")
	@ApiOperation(value="二级仓库基础表-添加", notes="二级仓库基础表-添加")
	@PostMapping(value = "/add")
	public Result<StockLevelTwo> add(@RequestBody StockLevelTwo stockLevelTwo) {
		Result<StockLevelTwo> result = new Result<StockLevelTwo>();
		try {
			stockLevelTwoService.save(stockLevelTwo);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param stockLevelTwo
	 * @return
	 */
	@AutoLog(value = "二级仓库基础表-编辑")
	@ApiOperation(value="二级仓库基础表-编辑", notes="二级仓库基础表-编辑")
	@PutMapping(value = "/edit")
	public Result<StockLevelTwo> edit(@RequestBody StockLevelTwo stockLevelTwo) {
		Result<StockLevelTwo> result = new Result<StockLevelTwo>();
		StockLevelTwo stockLevelTwoEntity = stockLevelTwoService.getById(stockLevelTwo.getId());
		if(stockLevelTwoEntity==null) {
			result.onnull("未找到对应实体");
		}else {
			boolean ok = stockLevelTwoService.updateById(stockLevelTwo);

			if(ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}

	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "二级仓库基础表-通过id删除")
	@ApiOperation(value="二级仓库基础表-通过id删除", notes="二级仓库基础表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			stockLevelTwoService.removeById(id);
		} catch (Exception e) {
			log.error("删除失败",e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}

	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "二级仓库基础表-批量删除")
	@ApiOperation(value="二级仓库基础表-批量删除", notes="二级仓库基础表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<StockLevelTwo> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<StockLevelTwo> result = new Result<StockLevelTwo>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.stockLevelTwoService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "二级仓库基础表-通过id查询")
	@ApiOperation(value="二级仓库基础表-通过id查询", notes="二级仓库基础表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<StockLevelTwo> queryById(@RequestParam(name="id",required=true) String id) {
		Result<StockLevelTwo> result = new Result<StockLevelTwo>();
		StockLevelTwo stockLevelTwo = stockLevelTwoService.getById(id);
		if(stockLevelTwo==null) {
			result.onnull("未找到对应实体");
		}else {
			result.setResult(stockLevelTwo);
			result.setSuccess(true);
		}
		return result;
	}

  /**
      * 导出excel
   *
   * @param request
   * @param response
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
      // Step.1 组装查询条件
      QueryWrapper<StockLevelTwo> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              StockLevelTwo stockLevelTwo = JSON.parseObject(deString, StockLevelTwo.class);
              queryWrapper = QueryGenerator.initQueryWrapper(stockLevelTwo, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<StockLevelTwo> pageList = stockLevelTwoService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "二级仓库基础表列表");
      mv.addObject(NormalExcelConstants.CLASS, StockLevelTwo.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("二级仓库基础表列表数据", "导出人:Jeecg", "导出信息"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
  }

  /**
      * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<StockLevelTwo> listStockLevelTwos = ExcelImportUtil.importExcel(file.getInputStream(), StockLevelTwo.class, params);
              stockLevelTwoService.saveBatch(listStockLevelTwos);
              return Result.ok("文件导入成功！数据行数:" + listStockLevelTwos.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("文件导入失败！");
  }

}
