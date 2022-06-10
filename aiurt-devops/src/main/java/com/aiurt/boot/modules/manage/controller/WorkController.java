package com.aiurt.boot.modules.manage.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.manage.entity.Work;
import com.aiurt.boot.modules.manage.service.IWorkService;
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
 * @Description: cs_work
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="作业类型")
@RestController
@RequestMapping("/manage/work")
public class WorkController {
	@Autowired
	private IWorkService workService;

	/**
	  * 分页列表查询
	 * @param work
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "作业类型-分页列表查询")
	@ApiOperation(value="作业类型-分页列表查询", notes="作业类型-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Work>> queryPageList(Work work,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Work>> result = new Result<IPage<Work>>();
		QueryWrapper<Work> queryWrapper = QueryGenerator.initQueryWrapper(work, req.getParameterMap());
		Page<Work> page = new Page<Work>(pageNo, pageSize);
		IPage<Work> pageList = workService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param work
	 * @return
	 */
	@AutoLog(value = "作业类型-添加")
	@ApiOperation(value="作业类型-添加", notes="作业类型-添加")
	@PostMapping(value = "/add")
	public Result<Work> add(@RequestBody Work work) {
		Result<Work> result = new Result<Work>();
		try {
			workService.save(work);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param work
	 * @return
	 */
	@AutoLog(value = "作业类型-编辑")
	@ApiOperation(value="作业类型-编辑", notes="作业类型-编辑")
	@PutMapping(value = "/edit")
	public Result<Work> edit(@RequestBody Work work) {
		Result<Work> result = new Result<Work>();
		Work workEntity = workService.getById(work.getId());
		if(workEntity==null) {
			result.onnull("未找到对应实体");
		}else {
			boolean ok = workService.updateById(work);

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
	@AutoLog(value = "作业类型-通过id删除")
	@ApiOperation(value="作业类型-通过id删除", notes="作业类型-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			workService.removeById(id);
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
	@AutoLog(value = "作业类型-批量删除")
	@ApiOperation(value="作业类型-批量删除", notes="作业类型-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<Work> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Work> result = new Result<Work>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.workService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "作业类型-通过id查询")
	@ApiOperation(value="作业类型-通过id查询", notes="作业类型-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Work> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Work> result = new Result<Work>();
		Work work = workService.getById(id);
		if(work==null) {
			result.onnull("未找到对应实体");
		}else {
			result.setResult(work);
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
      QueryWrapper<Work> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Work work = JSON.parseObject(deString, Work.class);
              queryWrapper = QueryGenerator.initQueryWrapper(work, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Work> pageList = workService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "cs_work列表");
      mv.addObject(NormalExcelConstants.CLASS, Work.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("cs_work列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Work> listWorks = ExcelImportUtil.importExcel(file.getInputStream(), Work.class, params);
              workService.saveBatch(listWorks);
              return Result.ok("文件导入成功！数据行数:" + listWorks.size());
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
