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
import com.aiurt.boot.modules.manage.entity.StationPosition;
import com.aiurt.boot.modules.manage.service.IStationPositionService;
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
 * @Description: cs_station_position
 * @Author: qian
 * @Date:   2021-09-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags="站点位置")
@RestController
@RequestMapping("/manage/stationPosition")
public class StationPositionController {
	@Autowired
	private IStationPositionService stationPositionService;

	/**
	  * 分页列表查询
	 * @param stationPosition
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "站点位置-分页列表查询")
	@ApiOperation(value="站点位置-分页列表查询", notes="站点位置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<StationPosition>> queryPageList(StationPosition stationPosition,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<StationPosition>> result = new Result<IPage<StationPosition>>();
		QueryWrapper<StationPosition> queryWrapper = QueryGenerator.initQueryWrapper(stationPosition, req.getParameterMap());
		Page<StationPosition> page = new Page<StationPosition>(pageNo, pageSize);
		IPage<StationPosition> pageList = stationPositionService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param stationPosition
	 * @return
	 */
	@AutoLog(value = "站点位置-添加")
	@ApiOperation(value="站点位置-添加", notes="站点位置-添加")
	@PostMapping(value = "/add")
	public Result<StationPosition> add(@RequestBody StationPosition stationPosition) {
		Result<StationPosition> result = new Result<StationPosition>();
		try {
			stationPositionService.save(stationPosition);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param stationPosition
	 * @return
	 */
	@AutoLog(value = "站点位置-编辑")
	@ApiOperation(value="站点位置-编辑", notes="站点位置-编辑")
	@PutMapping(value = "/edit")
	public Result<StationPosition> edit(@RequestBody StationPosition stationPosition) {
		Result<StationPosition> result = new Result<StationPosition>();
		StationPosition stationPositionEntity = stationPositionService.getById(stationPosition.getId());
		if(stationPositionEntity==null) {
			result.onnull("未找到对应实体");
		}else {
			boolean ok = stationPositionService.updateById(stationPosition);

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
	@AutoLog(value = "站点位置-通过id删除")
	@ApiOperation(value="站点位置-通过id删除", notes="站点位置-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			stationPositionService.removeById(id);
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
	@AutoLog(value = "站点位置-批量删除")
	@ApiOperation(value="站点位置-批量删除", notes="站点位置-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<StationPosition> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<StationPosition> result = new Result<StationPosition>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.stationPositionService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "站点位置-通过id查询")
	@ApiOperation(value="站点位置-通过id查询", notes="站点位置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<StationPosition> queryById(@RequestParam(name="id",required=true) String id) {
		Result<StationPosition> result = new Result<StationPosition>();
		StationPosition stationPosition = stationPositionService.getById(id);
		if(stationPosition==null) {
			result.onnull("未找到对应实体");
		}else {
			result.setResult(stationPosition);
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
      QueryWrapper<StationPosition> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              StationPosition stationPosition = JSON.parseObject(deString, StationPosition.class);
              queryWrapper = QueryGenerator.initQueryWrapper(stationPosition, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<StationPosition> pageList = stationPositionService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "cs_station_position列表");
      mv.addObject(NormalExcelConstants.CLASS, StationPosition.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("cs_station_position列表数据", "导出人:Jeecg", "导出信息"));
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
              List<StationPosition> listStationPositions = ExcelImportUtil.importExcel(file.getInputStream(), StationPosition.class, params);
              stationPositionService.saveBatch(listStationPositions);
              return Result.ok("文件导入成功！数据行数:" + listStationPositions.size());
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
