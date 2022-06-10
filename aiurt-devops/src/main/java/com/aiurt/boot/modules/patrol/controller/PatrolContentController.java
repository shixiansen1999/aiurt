package com.aiurt.boot.modules.patrol.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.entity.PatrolContent;
import com.swsc.copsms.modules.patrol.service.IPatrolContentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 巡检项内容
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "巡检项内容")
@RestController
@RequestMapping("/patrol.patrol_content/patrolContent")
public class PatrolContentController {
	@Autowired
	private IPatrolContentService patrolContentService;


	/**
	 * 查询树
	 *
	 * @param id  id
	 * @param req 要求的事情
	 * @return {@link Result}<{@link ?}>
	 */
	@AutoLog(value = "巡检项内容-树型查询")
	@ApiOperation(value = "巡检项内容-树型查询", notes = "巡检项内容-分页列表查询")
	@GetMapping(value = "/tree")
	public Result<?> queryTree(@RequestParam(name = "id") Long id,
	                           HttpServletRequest req) {

		return patrolContentService.queryTree(req, id);
	}
	/**
	 * 分页列表查询
	 *
	 * @param id  id
	 * @param req 要求的事情
	 * @return {@link Result}<{@link ?}>
	 */
	@AutoLog(value = "巡检项内容-分页列表查询")
	@ApiOperation(value = "巡检项内容-分页列表查询", notes = "巡检项内容-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> list(@RequestParam(name = "id") Long id,
	                           HttpServletRequest req) {

		return patrolContentService. queryList(req, id);
	}


	/**
	 * 添加
	 *
	 * @param patrolContent
	 * @return
	 */
	@AutoLog(value = "巡检项内容-添加")
	@ApiOperation(value = "巡检项内容-添加", notes = "巡检项内容-添加")
	@PostMapping(value = "/add")
	public Result<?> add(HttpServletRequest req, @RequestBody PatrolContent patrolContent) {

		if (patrolContent.getSequence() < 1) {
			Result.error("排序不能小于1");
		}

		patrolContent.setDelFlag(PatrolConstant.UN_DEL_FLAG);
		return patrolContentService.add(patrolContent);
	}

	/**
	 * 编辑
	 *
	 * @param patrolContent
	 * @return
	 */
	@AutoLog(value = "巡检项内容-编辑")
	@ApiOperation(value = "巡检项内容-编辑", notes = "巡检项内容-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody PatrolContent patrolContent) {

		if (patrolContent.getSequence() < 1) {
			Result.error("排序不能小于1");
		}

		return patrolContentService.edit(patrolContent);
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "巡检项内容-通过id删除")
	@ApiOperation(value = "巡检项内容-通过id删除", notes = "巡检项内容-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			patrolContentService.removeById(id);
		} catch (Exception e) {
			log.error("删除失败", e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "巡检项内容-批量删除")
	@ApiOperation(value = "巡检项内容-批量删除", notes = "巡检项内容-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<PatrolContent> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<PatrolContent> result = new Result<PatrolContent>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.patrolContentService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "巡检项内容-通过id查询")
	@ApiOperation(value = "巡检项内容-通过id查询", notes = "巡检项内容-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolContent> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<PatrolContent> result = new Result<PatrolContent>();
		PatrolContent patrolContent = patrolContentService.getById(id);
		if (patrolContent == null) {
			result.error500("未找到对应实体");
		} else {
			result.setResult(patrolContent);
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
		QueryWrapper<PatrolContent> queryWrapper = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				PatrolContent patrolContent = JSON.parseObject(deString, PatrolContent.class);
				queryWrapper = QueryGenerator.initQueryWrapper(patrolContent, request.getParameterMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<PatrolContent> pageList = patrolContentService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "巡检项内容列表");
		mv.addObject(NormalExcelConstants.CLASS, PatrolContent.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("巡检项内容列表数据", "导出人:Jeecg", "导出信息"));
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
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<PatrolContent> listPatrolContents = ExcelImportUtil.importExcel(file.getInputStream(), PatrolContent.class, params);
				patrolContentService.saveBatch(listPatrolContents);
				return Result.ok("文件导入成功！数据行数:" + listPatrolContents.size());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("文件导入失败:" + e.getMessage());
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
