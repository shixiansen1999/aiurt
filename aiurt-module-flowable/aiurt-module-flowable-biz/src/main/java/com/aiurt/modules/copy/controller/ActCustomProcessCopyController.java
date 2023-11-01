package com.aiurt.modules.copy.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.modules.copy.entity.ActCustomProcessCopy;
import com.aiurt.modules.copy.service.IActCustomProcessCopyService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.common.util.oConvertUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: act_custom_process_copy
 * @Author: aiurt
 * @Date:   2023-08-17
 * @Version: V1.0
 */
@Api(tags="流程抄送")
@RestController
@RequestMapping("/copy")
@Slf4j
public class ActCustomProcessCopyController extends BaseController<ActCustomProcessCopy, IActCustomProcessCopyService> {
	@Autowired
	private IActCustomProcessCopyService actCustomProcessCopyService;

	/**
	 * 分页列表查询
	 *
	 * @param actCustomProcessCopy
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="act_custom_process_copy-分页列表查询", notes="act_custom_process_copy-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ActCustomProcessCopy>> queryPageList(ActCustomProcessCopy actCustomProcessCopy,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ActCustomProcessCopy> queryWrapper = QueryGenerator.initQueryWrapper(actCustomProcessCopy, req.getParameterMap());
		Page<ActCustomProcessCopy> page = new Page<ActCustomProcessCopy>(pageNo, pageSize);
		IPage<ActCustomProcessCopy> pageList = actCustomProcessCopyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}



	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "act_custom_process_copy-通过id删除")
	@ApiOperation(value="act_custom_process_copy-通过id删除", notes="act_custom_process_copy-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		actCustomProcessCopyService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "act_custom_process_copy-批量删除")
	@ApiOperation(value="act_custom_process_copy-批量删除", notes="act_custom_process_copy-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.actCustomProcessCopyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="act_custom_process_copy-通过id查询", notes="act_custom_process_copy-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ActCustomProcessCopy> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomProcessCopy actCustomProcessCopy = actCustomProcessCopyService.getById(id);
		if(actCustomProcessCopy==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(actCustomProcessCopy);
	}


}
