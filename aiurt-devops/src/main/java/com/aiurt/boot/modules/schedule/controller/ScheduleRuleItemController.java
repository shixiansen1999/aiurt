package com.aiurt.boot.modules.schedule.controller;


import com.aiurt.boot.modules.schedule.entity.ScheduleRuleItem;
import com.aiurt.boot.modules.schedule.service.IScheduleRuleItemService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.fastjson.JSON;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

 /**
 * @Description: 排班规则关联班次
 * @Author: qian
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags="排班规则关联班次")
@RestController
@RequestMapping("/schedule/scheduleRuleItem")
public class ScheduleRuleItemController {
	@Autowired
	private IScheduleRuleItemService scheduleRuleItemService;

	/**
	  * 分页列表查询
	 * @param scheduleRuleItem
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "排班规则关联班次-分页列表查询")
	@ApiOperation(value="排班规则关联班次-分页列表查询", notes="排班规则关联班次-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ScheduleRuleItem>> queryPageList(ScheduleRuleItem scheduleRuleItem,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<ScheduleRuleItem>> result = new Result<IPage<ScheduleRuleItem>>();
		QueryWrapper<ScheduleRuleItem> queryWrapper = QueryGenerator.initQueryWrapper(scheduleRuleItem, req.getParameterMap());
		Page<ScheduleRuleItem> page = new Page<ScheduleRuleItem>(pageNo, pageSize);
		IPage<ScheduleRuleItem> pageList = scheduleRuleItemService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param scheduleRuleItem
	 * @return
	 */
	@AutoLog(value = "排班规则关联班次-添加")
	@ApiOperation(value="排班规则关联班次-添加", notes="排班规则关联班次-添加")
	@PostMapping(value = "/add")
	public Result<ScheduleRuleItem> add(@RequestBody ScheduleRuleItem scheduleRuleItem) {
		Result<ScheduleRuleItem> result = new Result<ScheduleRuleItem>();
		try {
			scheduleRuleItemService.save(scheduleRuleItem);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param scheduleRuleItem
	 * @return
	 */
	@AutoLog(value = "排班规则关联班次-编辑")
	@ApiOperation(value="排班规则关联班次-编辑", notes="排班规则关联班次-编辑")
	@PutMapping(value = "/edit")
	public Result<ScheduleRuleItem> edit(@RequestBody ScheduleRuleItem scheduleRuleItem) {
		Result<ScheduleRuleItem> result = new Result<ScheduleRuleItem>();
		ScheduleRuleItem scheduleRuleItemEntity = scheduleRuleItemService.getById(scheduleRuleItem.getId());
		if(scheduleRuleItemEntity==null) {
			result.onnull("未找到对应实体");
		}else {
			boolean ok = scheduleRuleItemService.updateById(scheduleRuleItem);

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
	@AutoLog(value = "排班规则关联班次-通过id删除")
	@ApiOperation(value="排班规则关联班次-通过id删除", notes="排班规则关联班次-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			scheduleRuleItemService.removeById(id);
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
	@AutoLog(value = "排班规则关联班次-批量删除")
	@ApiOperation(value="排班规则关联班次-批量删除", notes="排班规则关联班次-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<ScheduleRuleItem> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<ScheduleRuleItem> result = new Result<ScheduleRuleItem>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.scheduleRuleItemService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "排班规则关联班次-通过id查询")
	@ApiOperation(value="排班规则关联班次-通过id查询", notes="排班规则关联班次-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ScheduleRuleItem> queryById(@RequestParam(name="id",required=true) String id) {
		Result<ScheduleRuleItem> result = new Result<ScheduleRuleItem>();
		ScheduleRuleItem scheduleRuleItem = scheduleRuleItemService.getById(id);
		if(scheduleRuleItem==null) {
			result.onnull("未找到对应实体");
		}else {
			result.setResult(scheduleRuleItem);
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
      QueryWrapper<ScheduleRuleItem> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              ScheduleRuleItem scheduleRuleItem = JSON.parseObject(deString, ScheduleRuleItem.class);
              queryWrapper = QueryGenerator.initQueryWrapper(scheduleRuleItem, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<ScheduleRuleItem> pageList = scheduleRuleItemService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "排班规则关联班次列表");
      mv.addObject(NormalExcelConstants.CLASS, ScheduleRuleItem.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("排班规则关联班次列表数据", "导出人:Jeecg", "导出信息"));
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
              List<ScheduleRuleItem> listScheduleRuleItems = ExcelImportUtil.importExcel(file.getInputStream(), ScheduleRuleItem.class, params);
              scheduleRuleItemService.saveBatch(listScheduleRuleItems);
              return Result.ok("文件导入成功！数据行数:" + listScheduleRuleItems.size());
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
