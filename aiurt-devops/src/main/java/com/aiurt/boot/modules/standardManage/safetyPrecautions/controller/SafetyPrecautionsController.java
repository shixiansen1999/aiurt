package com.aiurt.boot.modules.standardManage.safetyPrecautions.controller;

import com.aiurt.boot.modules.standardManage.safetyPrecautions.constant.Constant;
import com.aiurt.boot.modules.standardManage.safetyPrecautions.entity.SafetyPrecautions;
import com.aiurt.boot.modules.standardManage.safetyPrecautions.service.IsafetyPrecautionsService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 安全事项
 * @Author: qian
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="安全事项")
@RestController
@RequestMapping("/safetyPrecautions/safetyPrecautions")
public class SafetyPrecautionsController {
	@Autowired
	private IsafetyPrecautionsService safetyPrecautionsService;

	 @Value("${support.downFilePath.safetyPrecautionsTemplatePath}")
	 private String excelPath;

	/**
	  * 分页列表查询
	 * @param safetyPrecautions
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "安全事项-分页列表查询")
	@ApiOperation(value="安全事项-分页列表查询", notes="安全事项-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SafetyPrecautions>> queryPageList(SafetyPrecautions safetyPrecautions,
														  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														  HttpServletRequest req) {
		Result<IPage<SafetyPrecautions>> result = new Result<IPage<SafetyPrecautions>>();
		QueryWrapper<SafetyPrecautions> queryWrapper = QueryGenerator.initQueryWrapper(safetyPrecautions, req.getParameterMap());
		Page<SafetyPrecautions> page = new Page<SafetyPrecautions>(pageNo, pageSize);
		IPage<SafetyPrecautions> pageList = safetyPrecautionsService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param safetyPrecautions
	 * @return
	 */
	@AutoLog(value = "安全事项-添加")
	@ApiOperation(value="安全事项-添加", notes="安全事项-添加")
	@PostMapping(value = "/add")
	public Result<SafetyPrecautions> add(@RequestBody SafetyPrecautions safetyPrecautions) {
		Result<SafetyPrecautions> result = new Result<SafetyPrecautions>();
		try {
			//查询title是否重复
			SafetyPrecautions precautions = safetyPrecautionsService.getOne(new LambdaQueryWrapper<SafetyPrecautions>().eq(SafetyPrecautions::getTitle, safetyPrecautions.getTitle()).last("limit 1"));
			if (precautions != null){
				return result.error500("标题不能重复");
			}
			safetyPrecautionsService.save(safetyPrecautions);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param safetyPrecautions
	 * @return
	 */
	@AutoLog(value = "安全事项-编辑")
	@ApiOperation(value="安全事项-编辑", notes="安全事项-编辑")
	@PutMapping(value = "/edit")
	public Result<SafetyPrecautions> edit(@RequestBody SafetyPrecautions safetyPrecautions) {
		Result<SafetyPrecautions> result = new Result<SafetyPrecautions>();
		SafetyPrecautions safetyPrecautionsEntity = safetyPrecautionsService.getById(safetyPrecautions.getId());
		if(safetyPrecautionsEntity==null) {
			result.onnull("未找到对应实体");
		}else {
			boolean ok = safetyPrecautionsService.updateById(safetyPrecautions);

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
	@AutoLog(value = "安全事项-通过id删除")
	@ApiOperation(value="安全事项-通过id删除", notes="安全事项-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			safetyPrecautionsService.removeById(id);
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
	@AutoLog(value = "安全事项-批量删除")
	@ApiOperation(value="安全事项-批量删除", notes="安全事项-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<SafetyPrecautions> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SafetyPrecautions> result = new Result<SafetyPrecautions>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.safetyPrecautionsService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "安全事项-通过id查询")
	@ApiOperation(value="安全事项-通过id查询", notes="安全事项-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SafetyPrecautions> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SafetyPrecautions> result = new Result<SafetyPrecautions>();
		SafetyPrecautions safetyPrecautions = safetyPrecautionsService.getById(id);
		if(safetyPrecautions==null) {
			result.onnull("未找到对应实体");
		}else {
			result.setResult(safetyPrecautions);
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
      QueryWrapper<SafetyPrecautions> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              SafetyPrecautions safetyPrecautions = JSON.parseObject(deString, SafetyPrecautions.class);
              queryWrapper = QueryGenerator.initQueryWrapper(safetyPrecautions, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<SafetyPrecautions> pageList = safetyPrecautionsService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "安全事项列表");
      mv.addObject(NormalExcelConstants.CLASS, SafetyPrecautions.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("安全事项列表数据", "导出人:Jeecg", "导出信息"));
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
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(0);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<SafetyPrecautions> listsafetyPrecautionsses = ExcelImportUtil.importExcel(file.getInputStream(), SafetyPrecautions.class, params);
              safetyPrecautionsService.saveBatch(listsafetyPrecautionsses);
              return Result.ok("文件导入成功！数据行数:" + listsafetyPrecautionsses.size());
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


	 /**
	  * 下载模板
	  *
	  * @param response
	  * @param request
	  * @throws IOException
	  */
	 @AutoLog(value = "下载模板")
	 @ApiOperation(value = "下载模板", notes = "下载模板")
	 @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
	 public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
		 String filePath = excelPath;
		 InputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
		 response.setContentType("application/vnd.ms-excel");
		 // 获得请求头中的User-Agent
		 String agent = request.getHeader("USER-AGENT").toLowerCase();
		 // 根据不同的客户端进行不同的编码
		 String filename="安全事项模版";
		 if (agent.contains(Constant.MSIE)) {
			 // IE浏览器
			 filename = URLEncoder.encode(filename, "utf-8");
		 } else if (agent.contains(Constant.FIREFOX)) {
			 // 火狐浏览器
			 //BASE64Encoder base64Encoder = new BASE64Encoder();
			 //filename = "=?utf-8?B?" + base64Encoder.encode(filename.getBytes("utf-8")) + "?=";
		 } else if(agent.contains(Constant.SAFARI) ){
			 //处理safari的乱码问题
			 byte[] bytesName = filename.getBytes("UTF-8");
			 filename = new String(bytesName, "ISO-8859-1");
			 response.setHeader("content-disposition", "attachment;fileName="+ filename);
		 }else {
			 // 其它浏览器
			 filename = URLEncoder.encode(filename, "utf-8");
		 }
		 response.setHeader("content-disposition", "attachment;filename=" + filename + ".xlsx");
		 BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		 int len = 0;
		 while ((len = bis.read()) != -1) {
			 out.write(len);
			 out.flush();
		 }
		 out.close();
	 }
}
