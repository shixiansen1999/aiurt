package com.aiurt.boot.modules.manage.controller;

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
import com.swsc.copsms.modules.manage.entity.SpecialSituation;
import com.swsc.copsms.modules.manage.service.ISpecialSituationService;
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
 * @Description: cs_special_situation
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="特情")
@RestController
@RequestMapping("/manage/specialSituation")
public class SpecialSituationController {
	@Autowired
	private ISpecialSituationService specialSituationService;

	/**
	  * 分页列表查询
	 * @param specialSituation
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "cs_special_situation-分页列表查询")
	@ApiOperation(value="cs_special_situation-分页列表查询", notes="cs_special_situation-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SpecialSituation>> queryPageList(SpecialSituation specialSituation,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<SpecialSituation>> result = new Result<IPage<SpecialSituation>>();
		QueryWrapper<SpecialSituation> queryWrapper = QueryGenerator.initQueryWrapper(specialSituation, req.getParameterMap());
		Page<SpecialSituation> page = new Page<SpecialSituation>(pageNo, pageSize);
		IPage<SpecialSituation> pageList = specialSituationService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param specialSituation
	 * @return
	 */
	@AutoLog(value = "cs_special_situation-添加")
	@ApiOperation(value="cs_special_situation-添加", notes="cs_special_situation-添加")
	@PostMapping(value = "/add")
	public Result<SpecialSituation> add(@RequestBody SpecialSituation specialSituation) {
		Result<SpecialSituation> result = new Result<SpecialSituation>();
		try {
			specialSituationService.save(specialSituation);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param specialSituation
	 * @return
	 */
	@AutoLog(value = "cs_special_situation-编辑")
	@ApiOperation(value="cs_special_situation-编辑", notes="cs_special_situation-编辑")
	@PutMapping(value = "/edit")
	public Result<SpecialSituation> edit(@RequestBody SpecialSituation specialSituation) {
		Result<SpecialSituation> result = new Result<SpecialSituation>();
		SpecialSituation specialSituationEntity = specialSituationService.getById(specialSituation.getId());
		if(specialSituationEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = specialSituationService.updateById(specialSituation);
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
	@AutoLog(value = "cs_special_situation-通过id删除")
	@ApiOperation(value="cs_special_situation-通过id删除", notes="cs_special_situation-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			specialSituationService.removeById(id);
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
	@AutoLog(value = "cs_special_situation-批量删除")
	@ApiOperation(value="cs_special_situation-批量删除", notes="cs_special_situation-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<SpecialSituation> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SpecialSituation> result = new Result<SpecialSituation>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.specialSituationService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "cs_special_situation-通过id查询")
	@ApiOperation(value="cs_special_situation-通过id查询", notes="cs_special_situation-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SpecialSituation> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SpecialSituation> result = new Result<SpecialSituation>();
		SpecialSituation specialSituation = specialSituationService.getById(id);
		if(specialSituation==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(specialSituation);
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
      QueryWrapper<SpecialSituation> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              SpecialSituation specialSituation = JSON.parseObject(deString, SpecialSituation.class);
              queryWrapper = QueryGenerator.initQueryWrapper(specialSituation, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<SpecialSituation> pageList = specialSituationService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "cs_special_situation列表");
      mv.addObject(NormalExcelConstants.CLASS, SpecialSituation.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("cs_special_situation列表数据", "导出人:Jeecg", "导出信息"));
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
              List<SpecialSituation> listSpecialSituations = ExcelImportUtil.importExcel(file.getInputStream(), SpecialSituation.class, params);
              specialSituationService.saveBatch(listSpecialSituations);
              return Result.ok("文件导入成功！数据行数:" + listSpecialSituations.size());
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
