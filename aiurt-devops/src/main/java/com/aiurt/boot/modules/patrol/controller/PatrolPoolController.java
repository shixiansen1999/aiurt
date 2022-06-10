package com.aiurt.boot.modules.patrol.controller;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.modules.patrol.entity.PatrolPool;
import com.swsc.copsms.modules.patrol.param.PoolAppointParam;
import com.swsc.copsms.modules.patrol.param.PoolPageParam;
import com.swsc.copsms.modules.patrol.service.IPatrolPoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * @Description: 巡检计划池
 * @Author: qian
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "巡检计划池")
@RestController
@RequestMapping("/patrol/patrolPool")
public class PatrolPoolController {

	@Autowired
	private IPatrolPoolService patrolPoolService;

	/**
	 * 页面列表
	 * 分页列表查询
	 *
	 * @param param 参数
	 * @param req   要求的事情
	 * @return {@link Result}<{@link ?}>
	 */
	@AutoLog(value = "巡检计划池-分页列表查询")
	@ApiOperation(value = "巡检计划池-分页列表查询", notes = "巡检计划池-分页列表查询")
	@GetMapping(value = "/pageList")
	public Result<?> pageList( PoolPageParam param,
	                               HttpServletRequest req) {

		return patrolPoolService.selectPage(param, req);
	}

	/**
	 * 添加
	 *
	 * @param patrolPool
	 * @return
	 */
	@AutoLog(value = "巡检计划池-添加")
	@ApiOperation(value = "巡检计划池-添加", notes = "巡检计划池-添加")
	@PostMapping(value = "/add")
	public Result<PatrolPool> add(@RequestBody PatrolPool patrolPool) {
		patrolPool.setDelFlag(0);
		Result<PatrolPool> result = new Result<PatrolPool>();
		try {
			patrolPoolService.save(patrolPool);
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
	 * @param patrolPool
	 * @return
	 */
	@AutoLog(value = "巡检计划池-编辑")
	@ApiOperation(value = "巡检计划池-编辑", notes = "巡检计划池-编辑")
	@PutMapping(value = "/edit")
	public Result<PatrolPool> edit(@RequestBody PatrolPool patrolPool) {
		Result<PatrolPool> result = new Result<PatrolPool>();
		PatrolPool patrolPoolEntity = patrolPoolService.getById(patrolPool.getId());
		if (patrolPoolEntity == null) {
			result.error500("未找到对应实体");
		} else {
			boolean ok = patrolPoolService.updateById(patrolPool);
			//TODO 返回false说明什么？
			if (ok) {
				result.success("修改成功!");
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
	@AutoLog(value = "巡检计划池-通过id删除")
	@ApiOperation(value = "巡检计划池-通过id删除", notes = "巡检计划池-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			patrolPoolService.removeById(id);
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
	@AutoLog(value = "巡检计划池-批量删除")
	@ApiOperation(value = "巡检计划池-批量删除", notes = "巡检计划池-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<PatrolPool> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<PatrolPool> result = new Result<PatrolPool>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.patrolPoolService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	///**
	// * 通过id查询
	// *
	// * @param id
	// * @return
	// */
	//@AutoLog(value = "巡检计划池-通过id查询")
	//@ApiOperation(value = "巡检计划池-通过id查询", notes = "巡检计划池-通过id查询")
	//@GetMapping(value = "/queryById")
	//public Result<PatrolPool> queryById(@RequestParam(name = "id", required = true) String id) {
	//	Result<PatrolPool> result = new Result<PatrolPool>();
	//	PatrolPool patrolPool = patrolPoolService.getById(id);
	//	if (patrolPool == null) {
	//		result.error500("未找到对应实体");
	//	} else {
	//		result.setResult(patrolPool);
	//		result.setSuccess(true);
	//	}
	//	return result;
	//}

	///**
	// * 导出excel
	// *
	// * @param request
	// * @param response
	// */
	//@RequestMapping(value = "/exportXls")
	//public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
	//	// Step.1 组装查询条件
	//	QueryWrapper<PatrolPool> queryWrapper = null;
	//	try {
	//		String paramsStr = request.getParameter("paramsStr");
	//		if (oConvertUtils.isNotEmpty(paramsStr)) {
	//			String deString = URLDecoder.decode(paramsStr, "UTF-8");
	//			PatrolPool patrolPool = JSON.parseObject(deString, PatrolPool.class);
	//			queryWrapper = QueryGenerator.initQueryWrapper(patrolPool, request.getParameterMap());
	//		}
	//	} catch (UnsupportedEncodingException e) {
	//		e.printStackTrace();
	//	}
	//
	//	//Step.2 AutoPoi 导出Excel
	//	ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
	//	List<PatrolPool> pageList = patrolPoolService.list(queryWrapper);
	//	//导出文件名称
	//	mv.addObject(NormalExcelConstants.FILE_NAME, "巡检计划池列表");
	//	mv.addObject(NormalExcelConstants.CLASS, PatrolPool.class);
	//	mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("巡检计划池列表数据", "导出人:Jeecg", "导出信息"));
	//	mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
	//	return mv;
	//}
	//
	///**
	// * 通过excel导入数据
	// *
	// * @param request
	// * @param response
	// * @return
	// */
	//@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	//public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
	//	MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	//	Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
	//	for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
	//		MultipartFile file = entity.getValue();// 获取上传文件对象
	//		ImportParams params = new ImportParams();
	//		params.setTitleRows(2);
	//		params.setHeadRows(1);
	//		params.setNeedSave(true);
	//		try {
	//			List<PatrolPool> listPatrolPools = ExcelImportUtil.importExcel(file.getInputStream(), PatrolPool.class, params);
	//			patrolPoolService.saveBatch(listPatrolPools);
	//			return Result.ok("文件导入成功！数据行数:" + listPatrolPools.size());
	//		} catch (Exception e) {
	//			log.error(e.getMessage(), e);
	//			return Result.error("文件导入失败:" + e.getMessage());
	//		} finally {
	//			try {
	//				file.getInputStream().close();
	//			} catch (IOException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}
	//	return Result.ok("文件导入失败！");
	//}
	//

	@AutoLog(value = "巡检计划池-指派人员")
	@ApiOperation(value = "巡检计划池-指派人员", notes = "巡检计划池-指派人员")
	@PostMapping(value = "/appoint")
	public Result<?> appoint(HttpServletRequest req, @RequestBody PoolAppointParam param) {
		return patrolPoolService.appoint(req, param);
	}


	@AutoLog(value = "巡检计划池-领取任务")
	@ApiOperation(value = "巡检计划池-领取任务", notes = "巡检计划池-领取任务")
	@PostMapping(value = "/receive")
	public Result<?> receive(HttpServletRequest req, @RequestParam("id") @NotNull(message = "id不能为空") Long id) {
		return patrolPoolService.receive(req, id);
	}


}
