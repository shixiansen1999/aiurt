package com.aiurt.boot.modules.worklog.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.result.WorkLogResult;
import com.swsc.copsms.common.system.api.ISysBaseAPI;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.worklog.dto.WorkLogDTO;
import com.swsc.copsms.modules.worklog.entity.WorkLog;
import com.swsc.copsms.modules.worklog.param.WorkLogParam;
import com.swsc.copsms.modules.worklog.service.IWorkLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * @Description: 工作日志
 * @Author: qian
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="工作日志")
@RestController
@RequestMapping("/worklog/workLogDepot")
public class WorkLogController {
	@Autowired
	private IWorkLogService workLogDepotService;

	/**
	  * 分页列表查询
	 * @param workLogDepot
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "工作日志-分页列表查询")
	@ApiOperation(value="工作日志-分页列表查询", notes="工作日志-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WorkLogResult>> queryPageList(WorkLogResult workLogDepot,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													  @Valid WorkLogParam param,
													  HttpServletRequest req) {
		Result<IPage<WorkLogResult>> result = new Result<IPage<WorkLogResult>>();
		QueryWrapper<WorkLogResult> queryWrapper = QueryGenerator.initQueryWrapper(workLogDepot, req.getParameterMap());
		Page<WorkLogResult> page = new Page<WorkLogResult>(pageNo, pageSize);
		IPage<WorkLogResult> pageList = workLogDepotService.pageList(page, queryWrapper,param);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加工作日志
	 * @param dto
	 * @return
	 */
	@AutoLog(value = "工作日志-添加")
	@ApiOperation(value="工作日志-添加", notes="工作日志-添加")
	@PostMapping(value = "/add")
	public Result<WorkLog> add(@RequestBody WorkLogDTO dto, HttpServletRequest req) {
		Result<WorkLog> result = new Result<WorkLog>();
		try {
			workLogDepotService.add(dto,req);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500(e.getMessage());
		}
		return result;
	}

	/**
	  *  编辑
	 * @param workLogDepot
	 * @return
	 */
	@AutoLog(value = "工作日志-编辑")
	@ApiOperation(value="工作日志-编辑", notes="工作日志-编辑")
	@PutMapping(value = "/edit")
	public Result<WorkLog> edit(@RequestBody WorkLog workLogDepot) {
		Result<WorkLog> result = new Result<WorkLog>();
		WorkLog workLogDepotEntity = workLogDepotService.getById(workLogDepot.getId());
		if(workLogDepotEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = workLogDepotService.updateById(workLogDepot);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}

	/**
	  *   通过id假删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "工作日志-通过id删除")
	@ApiOperation(value="工作日志-通过id删除", notes="工作日志-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) Integer id) {
		try {
			workLogDepotService.deleteById(id);
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
	@AutoLog(value = "工作日志-批量删除")
	@ApiOperation(value="工作日志-批量删除", notes="工作日志-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<WorkLog> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<WorkLog> result = new Result<WorkLog>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.workLogDepotService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "工作日志-通过id查询")
	@ApiOperation(value="工作日志-通过id查询", notes="工作日志-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WorkLog> queryById(@RequestParam(name="id",required=true) String id) {
		Result<WorkLog> result = new Result<WorkLog>();
		WorkLog workLogDepot = workLogDepotService.getById(id);
		if(workLogDepot==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(workLogDepot);
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
      QueryWrapper<WorkLog> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              WorkLog workLogDepot = JSON.parseObject(deString, WorkLog.class);
              queryWrapper = QueryGenerator.initQueryWrapper(workLogDepot, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<WorkLog> pageList = workLogDepotService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "工作日志列表");
      mv.addObject(NormalExcelConstants.CLASS, WorkLog.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("工作日志列表数据", "导出人:Jeecg", "导出信息"));
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
              List<WorkLog> listWorkLogDepots = ExcelImportUtil.importExcel(file.getInputStream(), WorkLog.class, params);
              workLogDepotService.saveBatch(listWorkLogDepots);
              return Result.ok("文件导入成功！数据行数:" + listWorkLogDepots.size());
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
	  * 工作日志查看
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "工作日志查看")
	 @ApiOperation(value="工作日志查看", notes="工作日志查看")
	 @GetMapping(value = "/queryDetail")
	 public WorkLogResult queryDetail(@RequestParam Integer id) {
		 return workLogDepotService.getDetailById(id);
	 }

	 /**
	  * 工作日志确认
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "工作日志确认")
	 @ApiOperation(value="工作日志确认", notes="工作日志确认")
	 @GetMapping(value = "/confirm")
	 public Result confirm(@RequestParam Integer id) {
		  workLogDepotService.confirm(id);
		  return Result.ok("确认成功");
	 }

	 /**
	  *  批量确认
	  * @param ids
	  * @return
	  */
	 @AutoLog(value = "工作日志-批量确认")
	 @ApiOperation(value="工作日志-批量确认", notes="工作日志-批量确认")
	 @GetMapping(value = "/checkBatch")
	 public Result<?> checkBatch(@RequestParam String ids) {
			 this.workLogDepotService.checkByIds(ids);
		 return Result.ok("确认成功");
	 }

 }
