package com.aiurt.boot.modules.schedule.controller;

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
import com.swsc.copsms.modules.schedule.entity.Schedule;
import com.swsc.copsms.modules.schedule.service.IScheduleService;
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
 * @Description: schedule
 * @Author: qian
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags="schedule")
@RestController
@RequestMapping("/schedule/schedule")
public class ScheduleController {
	@Autowired
	private IScheduleService scheduleService;

	/**
	  * 分页列表查询
	 * @param schedule
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "schedule-分页列表查询")
	@ApiOperation(value="schedule-分页列表查询", notes="schedule-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Schedule>> queryPageList(Schedule schedule,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Schedule>> result = new Result<IPage<Schedule>>();
		QueryWrapper<Schedule> queryWrapper = QueryGenerator.initQueryWrapper(schedule, req.getParameterMap());
		Page<Schedule> page = new Page<Schedule>(pageNo, pageSize);
		IPage<Schedule> pageList = scheduleService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param schedule
	 * @return
	 */
	@AutoLog(value = "schedule-添加")
	@ApiOperation(value="schedule-添加", notes="schedule-添加")
	@PostMapping(value = "/add")
	public Result<Schedule> add(@RequestBody Schedule schedule) {
		Result<Schedule> result = new Result<Schedule>();
		try {
			scheduleService.save(schedule);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param schedule
	 * @return
	 */
	@AutoLog(value = "schedule-编辑")
	@ApiOperation(value="schedule-编辑", notes="schedule-编辑")
	@PutMapping(value = "/edit")
	public Result<Schedule> edit(@RequestBody Schedule schedule) {
		Result<Schedule> result = new Result<Schedule>();
		Schedule scheduleEntity = scheduleService.getById(schedule.getId());
		if(scheduleEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = scheduleService.updateById(schedule);
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
	@AutoLog(value = "schedule-通过id删除")
	@ApiOperation(value="schedule-通过id删除", notes="schedule-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			scheduleService.removeById(id);
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
	@AutoLog(value = "schedule-批量删除")
	@ApiOperation(value="schedule-批量删除", notes="schedule-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<Schedule> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Schedule> result = new Result<Schedule>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.scheduleService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "schedule-通过id查询")
	@ApiOperation(value="schedule-通过id查询", notes="schedule-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Schedule> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Schedule> result = new Result<Schedule>();
		Schedule schedule = scheduleService.getById(id);
		if(schedule==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(schedule);
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
      QueryWrapper<Schedule> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Schedule schedule = JSON.parseObject(deString, Schedule.class);
              queryWrapper = QueryGenerator.initQueryWrapper(schedule, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Schedule> pageList = scheduleService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "schedule列表");
      mv.addObject(NormalExcelConstants.CLASS, Schedule.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("schedule列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Schedule> listSchedules = ExcelImportUtil.importExcel(file.getInputStream(), Schedule.class, params);
              scheduleService.saveBatch(listSchedules);
              return Result.ok("文件导入成功！数据行数:" + listSchedules.size());
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
