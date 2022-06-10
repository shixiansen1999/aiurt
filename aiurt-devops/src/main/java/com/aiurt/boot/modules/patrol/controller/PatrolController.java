package com.aiurt.boot.modules.patrol.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.patrol.constant.PatrolConstant;
import com.swsc.copsms.modules.patrol.entity.Patrol;
import com.swsc.copsms.modules.patrol.entity.PatrolContent;
import com.swsc.copsms.modules.patrol.param.PatrolPageParam;
import com.swsc.copsms.modules.patrol.service.IPatrolContentService;
import com.swsc.copsms.modules.patrol.service.IPatrolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * @Description: 巡检标准
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "巡检标准")
@RestController
@RequestMapping("/patrol.patrol/patrol")
@RequiredArgsConstructor
public class PatrolController {

	private final IPatrolService patrolService;

	private final IPatrolContentService patrolContentService;

	/**
	 * 分页列表查询
	 *
	 * @param param
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "巡检标准-分页列表查询")
	@ApiOperation(value = "巡检标准-分页列表查询", notes = "巡检标准-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(PatrolPageParam param,
	                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
	                               HttpServletRequest req) {
		return patrolService.pageList(param, pageNo, pageSize, req);
	}

	/**
	 * 添加
	 *
	 * @param patrol
	 * @return
	 */
	@AutoLog(value = "巡检标准-添加")
	@ApiOperation(value = "巡检标准-添加", notes = "巡检标准-添加")
	@PostMapping(value = "/add")
	public Result<Patrol> add(@RequestBody Patrol patrol) {
		Result<Patrol> result = new Result<Patrol>();
		patrol.setDelFlag(0);
		try {
			patrolService.save(patrol);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	 * 编辑
	 *
	 * @param patrol
	 * @return
	 */
	@AutoLog(value = "巡检标准-编辑")
	@ApiOperation(value = "巡检标准-编辑", notes = "巡检标准-编辑")
	@PutMapping(value = "/edit")
	public Result<Patrol> edit(@RequestBody Patrol patrol) {
		Result<Patrol> result = new Result<Patrol>();
		Patrol patrolEntity = patrolService.getById(patrol.getId());
		if (patrolEntity == null) {
			result.error500("未找到对应实体");
		} else {
			boolean ok = patrolService.updateById(patrol);

			if (ok) {
				result.success("修改成功!");
			}else {
				result.error500("修改失败");
			}
		}

		return result;
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "巡检标准-通过id删除")
	@ApiOperation(value = "巡检标准-通过id删除", notes = "巡检标准-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) Long id) {
		try {
			patrolService.updateById(new Patrol().setId(id).setDelFlag(1));
			patrolContentService.update(new PatrolContent().setDelFlag(1),new QueryWrapper<PatrolContent>().eq(PatrolContent.RECORD_ID,id));
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
	@AutoLog(value = "巡检标准-批量删除")
	@ApiOperation(value = "巡检标准-批量删除", notes = "巡检标准-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<Patrol> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<Patrol> result = new Result<Patrol>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.patrolService.update(new Patrol().setDelFlag(PatrolConstant.DEL_FLAG),
					new QueryWrapper<Patrol>().in(Patrol.ID,ids));
			patrolContentService.update(new PatrolContent().setDelFlag(PatrolConstant.DEL_FLAG),
					new QueryWrapper<PatrolContent>().in(PatrolContent.RECORD_ID,ids));
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
	@AutoLog(value = "巡检标准-通过id查询")
	@ApiOperation(value = "巡检标准-通过id查询", notes = "巡检标准-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Patrol> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<Patrol> result = new Result<Patrol>();
		Patrol patrol = patrolService.getById(id);
		if (patrol == null) {
			result.error500("未找到对应实体");
		} else {
			result.setResult(patrol);
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
		QueryWrapper<Patrol> queryWrapper = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				Patrol patrol = JSON.parseObject(deString, Patrol.class);
				queryWrapper = QueryGenerator.initQueryWrapper(patrol, request.getParameterMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<Patrol> pageList = patrolService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "巡检标准列表");
		mv.addObject(NormalExcelConstants.CLASS, Patrol.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("巡检标准列表数据", "导出人:Jeecg", "导出信息"));
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
				List<Patrol> listPatrols = ExcelImportUtil.importExcel(file.getInputStream(), Patrol.class, params);
				patrolService.saveBatch(listPatrols);
				return Result.ok("文件导入成功！数据行数:" + listPatrols.size());
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
