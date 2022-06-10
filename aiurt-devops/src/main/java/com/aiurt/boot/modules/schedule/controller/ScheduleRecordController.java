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
import com.swsc.copsms.modules.schedule.entity.ScheduleRecord;
import com.swsc.copsms.modules.schedule.service.IScheduleRecordService;
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
 * @Description: schedule_record
 * @Author: qian
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags="schedule_record")
@RestController
@RequestMapping("/schedule/scheduleRecord")
public class ScheduleRecordController {
	@Autowired
	private IScheduleRecordService scheduleRecordService;

	/**
	  * 分页列表查询
	 * @param scheduleRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "schedule_record-分页列表查询")
	@ApiOperation(value="schedule_record-分页列表查询", notes="schedule_record-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ScheduleRecord>> queryPageList(ScheduleRecord scheduleRecord,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<ScheduleRecord>> result = new Result<IPage<ScheduleRecord>>();
		QueryWrapper<ScheduleRecord> queryWrapper = QueryGenerator.initQueryWrapper(scheduleRecord, req.getParameterMap());
		Page<ScheduleRecord> page = new Page<ScheduleRecord>(pageNo, pageSize);
		IPage<ScheduleRecord> pageList = scheduleRecordService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param scheduleRecord
	 * @return
	 */
	@AutoLog(value = "schedule_record-添加")
	@ApiOperation(value="schedule_record-添加", notes="schedule_record-添加")
	@PostMapping(value = "/add")
	public Result<ScheduleRecord> add(@RequestBody ScheduleRecord scheduleRecord) {
		Result<ScheduleRecord> result = new Result<ScheduleRecord>();
		try {
			scheduleRecordService.save(scheduleRecord);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param scheduleRecord
	 * @return
	 */
	@AutoLog(value = "schedule_record-编辑")
	@ApiOperation(value="schedule_record-编辑", notes="schedule_record-编辑")
	@PutMapping(value = "/edit")
	public Result<ScheduleRecord> edit(@RequestBody ScheduleRecord scheduleRecord) {
		Result<ScheduleRecord> result = new Result<ScheduleRecord>();
		ScheduleRecord scheduleRecordEntity = scheduleRecordService.getById(scheduleRecord.getId());
		if(scheduleRecordEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = scheduleRecordService.updateById(scheduleRecord);
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
	@AutoLog(value = "schedule_record-通过id删除")
	@ApiOperation(value="schedule_record-通过id删除", notes="schedule_record-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			scheduleRecordService.removeById(id);
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
	@AutoLog(value = "schedule_record-批量删除")
	@ApiOperation(value="schedule_record-批量删除", notes="schedule_record-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<ScheduleRecord> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<ScheduleRecord> result = new Result<ScheduleRecord>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.scheduleRecordService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "schedule_record-通过id查询")
	@ApiOperation(value="schedule_record-通过id查询", notes="schedule_record-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ScheduleRecord> queryById(@RequestParam(name="id",required=true) String id) {
		Result<ScheduleRecord> result = new Result<ScheduleRecord>();
		ScheduleRecord scheduleRecord = scheduleRecordService.getById(id);
		if(scheduleRecord==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(scheduleRecord);
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
      QueryWrapper<ScheduleRecord> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              ScheduleRecord scheduleRecord = JSON.parseObject(deString, ScheduleRecord.class);
              queryWrapper = QueryGenerator.initQueryWrapper(scheduleRecord, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<ScheduleRecord> pageList = scheduleRecordService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "schedule_record列表");
      mv.addObject(NormalExcelConstants.CLASS, ScheduleRecord.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("schedule_record列表数据", "导出人:Jeecg", "导出信息"));
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
              List<ScheduleRecord> listScheduleRecords = ExcelImportUtil.importExcel(file.getInputStream(), ScheduleRecord.class, params);
              scheduleRecordService.saveBatch(listScheduleRecords);
              return Result.ok("文件导入成功！数据行数:" + listScheduleRecords.size());
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
