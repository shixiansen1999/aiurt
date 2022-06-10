package com.aiurt.boot.modules.training.controller;

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
import com.swsc.copsms.modules.training.entity.CoursewareStock;
import com.swsc.copsms.modules.training.mapper.CoursewareStockMapper;
import com.swsc.copsms.modules.training.service.ICoursewareStockService;
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
 * @Description: 课件库
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags="课件库")
@RestController
@RequestMapping("/training/coursewareStock")
public class CoursewareStockController {
	@Autowired
	private ICoursewareStockService coursewareStockService;

	private CoursewareStockMapper coursewareStockMapper;

	/**
	  * 分页列表查询
	 * @param coursewareStock
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "课件库-分页列表查询")
	@ApiOperation(value="课件库-分页列表查询", notes="课件库-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CoursewareStock>> queryPageList(CoursewareStock coursewareStock,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Result<IPage<CoursewareStock>> result = new Result<IPage<CoursewareStock>>();
		QueryWrapper<CoursewareStock> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("del_flag",0);
		if(coursewareStock.getStartTime()!=null && coursewareStock.getEndTime()!=null){
			queryWrapper.between("update_time",coursewareStock.getStartTime(),coursewareStock.getEndTime());
		}
		if(coursewareStock.getName()!=null){
			queryWrapper.eq("name",coursewareStock.getName());
		}
		Page<CoursewareStock> page = new Page<CoursewareStock>(pageNo, pageSize);
		IPage<CoursewareStock> pageList = coursewareStockService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param coursewareStock
	 * @return
	 */
	@AutoLog(value = "课件库-添加")
	@ApiOperation(value="课件库-添加", notes="课件库-添加")
	@PostMapping(value = "/add")
	public Result<CoursewareStock> add(@RequestBody CoursewareStock coursewareStock) {
		Result<CoursewareStock> result = new Result<CoursewareStock>();
		try {
			coursewareStockService.save(coursewareStock);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param coursewareStock
	 * @return
	 */
	@AutoLog(value = "课件库-编辑")
	@ApiOperation(value="课件库-编辑", notes="课件库-编辑")
	@PutMapping(value = "/edit")
	public Result<CoursewareStock> edit(@RequestBody CoursewareStock coursewareStock) {
		Result<CoursewareStock> result = new Result<CoursewareStock>();
		CoursewareStock coursewareStockEntity = coursewareStockService.getById(coursewareStock.getId());
		if(coursewareStockEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = coursewareStockService.updateById(coursewareStock);
			//TODO 返回false说明什么？
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
	@AutoLog(value = "课件库-通过id删除")
	@ApiOperation(value="课件库-通过id删除", notes="课件库-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			coursewareStockService.removeById(id);
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
	@AutoLog(value = "课件库-批量删除")
	@ApiOperation(value="课件库-批量删除", notes="课件库-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<CoursewareStock> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<CoursewareStock> result = new Result<CoursewareStock>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.coursewareStockService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "课件库-通过id查询")
	@ApiOperation(value="课件库-通过id查询", notes="课件库-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CoursewareStock> queryById(@RequestParam(name="id",required=true) String id) {
		Result<CoursewareStock> result = new Result<CoursewareStock>();
		CoursewareStock coursewareStock = coursewareStockService.getById(id);
		if(coursewareStock==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(coursewareStock);
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
      QueryWrapper<CoursewareStock> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              CoursewareStock coursewareStock = JSON.parseObject(deString, CoursewareStock.class);
              queryWrapper = QueryGenerator.initQueryWrapper(coursewareStock, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<CoursewareStock> pageList = coursewareStockService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "课件库列表");
      mv.addObject(NormalExcelConstants.CLASS, CoursewareStock.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("课件库列表数据", "导出人:Jeecg", "导出信息"));
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
              List<CoursewareStock> listCoursewareStocks = ExcelImportUtil.importExcel(file.getInputStream(), CoursewareStock.class, params);
              coursewareStockService.saveBatch(listCoursewareStocks);
              return Result.ok("文件导入成功！数据行数:" + listCoursewareStocks.size());
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
