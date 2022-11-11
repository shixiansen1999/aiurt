package com.aiurt.modules.maplocation.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.maplocation.dto.AlarmRecordDTO;
import com.aiurt.modules.maplocation.entity.BdAlarmRecord;
import com.aiurt.modules.maplocation.service.IBdAlarmRecordService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

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
 * @Description: bd_alarm_record
 * @Author: jeecg-boot
 * @Date:   2021-05-07
 * @Version: V1.0
 */
@Api(tags="报警记录模块")
@RestController
@RequestMapping("/maplocaltion/bdAlarmRecord")
@Slf4j
public class BdAlarmRecordController {
	@Autowired
	private IBdAlarmRecordService bdAlarmRecordService;

	/**
	 * 分页列表查询
	 *
	 * @param bdAlarmRecord
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "bd_alarm_record-分页列表查询")
	@ApiOperation(value="bd_alarm_record-分页列表查询", notes="bd_alarm_record-分页列表查询")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = AlarmRecordDTO.class),
	})
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BdAlarmRecord bdAlarmRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<AlarmRecordDTO> pageList = new Page<AlarmRecordDTO>(pageNo, pageSize);
		pageList = bdAlarmRecordService.listAlarmRecord(bdAlarmRecord,pageList);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param bdAlarmRecord
	 * @return
	 */
	@AutoLog(value = "bd_alarm_record-添加")
	@ApiOperation(value="bd_alarm_record-添加", notes="bd_alarm_record-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdAlarmRecord bdAlarmRecord) {
		bdAlarmRecordService.save(bdAlarmRecord);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param bdAlarmRecord
	 * @return
	 */
	@AutoLog(value = "bd_alarm_record-编辑")
	@ApiOperation(value="bd_alarm_record-编辑", notes="bd_alarm_record-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdAlarmRecord bdAlarmRecord) {
		bdAlarmRecordService.updateById(bdAlarmRecord);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bd_alarm_record-通过id删除")
	@ApiOperation(value="bd_alarm_record-通过id删除", notes="bd_alarm_record-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdAlarmRecordService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "bd_alarm_record-批量删除")
	@ApiOperation(value="bd_alarm_record-批量删除", notes="bd_alarm_record-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.bdAlarmRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bd_alarm_record-通过id查询")
	@ApiOperation(value="bd_alarm_record-通过id查询", notes="bd_alarm_record-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdAlarmRecord bdAlarmRecord = bdAlarmRecordService.getById(id);
		if(bdAlarmRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(bdAlarmRecord);
	}

//    /**
//    * 导出excel
//    *
//    * @param request
//    * @param bdAlarmRecord
//    */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, BdAlarmRecord bdAlarmRecord) {
//        return bdAlarmRecordService.exportXls(request, bdAlarmRecord, BdAlarmRecord.class, "bd_alarm_record");
//    }
//
//    /**
//      * 通过excel导入数据
//    *
//    * @param request
//    * @param response
//    * @return
//    */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, BdAlarmRecord.class);
//    }

}
