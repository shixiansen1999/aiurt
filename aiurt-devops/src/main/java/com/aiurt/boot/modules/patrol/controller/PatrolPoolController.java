package com.aiurt.boot.modules.patrol.controller;


import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.Patrol;
import com.aiurt.boot.modules.patrol.entity.PatrolPool;
import com.aiurt.boot.modules.patrol.entity.PatrolTask;
import com.aiurt.boot.modules.patrol.param.*;
import com.aiurt.boot.modules.patrol.service.IPatrolPoolService;
import com.aiurt.boot.modules.patrol.service.IPatrolService;
import com.aiurt.boot.modules.patrol.service.IPatrolTaskService;
import com.aiurt.boot.modules.patrol.utils.ExportUtils;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskVO;
import com.aiurt.boot.modules.patrol.vo.export.*;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 巡检计划池
 * @Author: Mr.zhao
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "巡检计划池")
@RestController
@RequestMapping("/patrol/patrolPool")
public class PatrolPoolController {

	@Resource
	private IPatrolPoolService patrolPoolService;
	@Resource
	private IPatrolTaskService patrolTaskService;
	@Resource
	private IStationService stationService;
	@Resource
	private IPatrolService patrolService;
	@Resource
	private ISubsystemService subsystemService;

	@Value("${swsc.host}")
	private String host;


	@AutoLog(value = "巡检计划池-分页列表查询")
	@ApiOperation(value = "巡检计划池-分页列表查询", notes = "巡检计划池-分页列表查询")
	@GetMapping(value = "/pageList")
	public Result<?> pageList(PoolPageParam param) {
		return patrolPoolService.selectPage(param);
	}


	@AutoLog(value = "巡检计划池-添加")
	@ApiOperation(value = "巡检计划池-添加", notes = "巡检计划池-添加")
	@PostMapping(value = "/add")
	public Result<PatrolPool> add(@RequestBody PatrolPool patrolPool) {
		patrolPool.setDelFlag(0);
		Result<PatrolPool> result = new Result<>();
		try {
			patrolPoolService.save(patrolPool);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}


	@AutoLog(value = "巡检计划池-编辑")
	@ApiOperation(value = "巡检计划池-编辑", notes = "巡检计划池-编辑")
	@PutMapping(value = "/edit")
	public Result<PatrolPool> edit(@RequestBody PatrolPool patrolPool) {
		Result<PatrolPool> result = new Result<>();
		PatrolPool patrolPoolEntity = patrolPoolService.getById(patrolPool.getId());
		if (patrolPoolEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			boolean ok = patrolPoolService.updateById(patrolPool);
			if (ok) {
				result.success("修改成功!");
			} else {
				result.error500("修改失败");
			}
		}

		return result;
	}


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


	@AutoLog(value = "巡检计划池-批量删除")
	@ApiOperation(value = "巡检计划池-批量删除", notes = "巡检计划池-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<PatrolPool> deleteBatch(@RequestParam(name = "ids") String ids) {
		Result<PatrolPool> result = new Result<>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.patrolPoolService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}


	@AutoLog(value = "巡检计划池-指派人员")
	@ApiOperation(value = "巡检计划池-指派人员", notes = "巡检计划池-指派人员")
	@PostMapping(value = "/appoint")
	public Result<?> appoint(HttpServletRequest req, @RequestBody @Validated PoolAppointParam param) {
		return patrolPoolService.appoint(req, param);
	}


	@AutoLog(value = "巡检计划池-批量指派人员")
	@ApiOperation(value = "巡检计划池-批量指派人员", notes = "巡检计划池-批量指派人员")
	@PostMapping(value = "/appointList")
	public Result<?> appointList(HttpServletRequest req, @RequestBody @Validated PoolAppointListParam param) {

		List<Long> ids = param.getIds();
		for (Long id : ids) {
			PoolAppointParam appointParam = new PoolAppointParam();
			BeanUtils.copyProperties(param, appointParam);
			appointParam.setId(id);
			this.patrolPoolService.appoint(req, appointParam);
		}

		return Result.ok();
	}


	@AutoLog(value = "巡检计划池-重新指派人员")
	@ApiOperation(value = "巡检计划池-重新指派人员", notes = "巡检计划池-重新指派人员")
	@PostMapping(value = "/reAppoint")
	public Result<?> reAppoint(HttpServletRequest req, @RequestBody @Validated PoolAppointParam param) {
		return patrolPoolService.reAppoint(req, param);
	}


	@AutoLog(value = "巡检计划池-领取任务")
	@ApiOperation(value = "巡检计划池-领取任务", notes = "巡检计划池-领取任务")
	@PostMapping(value = "/receive")
	public Result<?> receive(HttpServletRequest req, @RequestParam("id") @NotNull(message = "id不能为空") Long id) {
		return patrolPoolService.receive(req, id);
	}

	@AutoLog(value = "巡检计划池-详情")
	@ApiOperation(value = "巡检计划池-详情", notes = "巡检计划池-详情")
	@PostMapping(value = "/detail")
	public Result<?> detail(HttpServletRequest req, @RequestParam("id") @NotNull(message = "id不能为空") Long id) {
		return patrolPoolService.detail(req, id);
	}


	@AutoLog(value = "巡检计划池-指派人员详情")
	@ApiOperation(value = "巡检计划池-指派人员详情", notes = "巡检计划池-指派人员详情")
	@PostMapping(value = "/appointDetail")
	public Result<PatrolTask> appointDetail(HttpServletRequest req, @RequestParam("id") @NotNull(message = "id不能为空") Long poolId) {
		return patrolPoolService.appointDetail(req, poolId);
	}


	@AutoLog(value = "巡检计划池-导出excel")
	@RequestMapping(value = "/exportXls")
	@ApiOperation(value = "巡检计划池-导出excel", notes = "巡检计划池-导出excel")
	public ModelAndView exportXls(PatrolPoolParam param) {
		// Step.1 组装查询条件
		// Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

		if (param.getIgnoreStatus() != null && param.getIgnoreStatus() == 0) {
			LocalDate now = LocalDate.now();
			param.setCreateStartTime(now.atTime(0, 0, 0))
					.setCreateEndTime(now.atTime(23, 59, 59));
		}

		if (param.getStationId() == null && param.getLineId() != null) {
			List<Station> list = stationService.list(new LambdaQueryWrapper<Station>()
					.eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0)
					.eq(Station::getLineCode, param.getLineId()).select(Station::getId));
			if (CollectionUtils.isNotEmpty(list)) {
				param.setStationIds(list.stream().map(Station::getId).collect(Collectors.toList()));
			}
		}

		//查询所有符合条件的值
		List<PatrolTaskVO> list = this.patrolTaskService.selectExportListVO(param);

		List exportList = new ArrayList<>();

		for (PatrolTaskVO record : list) {
			//漏检状态处理
			if (record != null && record.getIgnoreStatus() != null && record.getIgnoreStatus() == 1) {
				if (StringUtils.isBlank(record.getIgnoreContent())) {
					//若为空则未处理
					record.setIgnoreStatus(0);
				}
			}
			if (param.getIgnoreStatus() != null && param.getIgnoreStatus() == 1) {
				ExportIgnoreVO vo = new ExportIgnoreVO();
				BeanUtils.copyProperties(record, vo);
				exportList.add(vo);
			} else if (param.getType() != null) {
				if (param.getType() == 0) {
					ExportTaskVO vo = new ExportTaskVO();
					BeanUtils.copyProperties(record, vo);
					exportList.add(vo);
				}
				if (param.getType() == 1) {
					ExportManuallyAddVO vo = new ExportManuallyAddVO();
					BeanUtils.copyProperties(record, vo);
					exportList.add(vo);
				}
			}
		}

		String titleName = "";

		//导出文件类型
		if (param.getIgnoreStatus() != null && param.getIgnoreStatus() == 1) {
			//漏检任务列表
			titleName = "漏检任务列表";
			mv.addObject(NormalExcelConstants.CLASS, ExportIgnoreVO.class);
		} else if (param.getType() != null) {
			if (param.getType() == 0) {
				//正常生成列表
				titleName = "巡检单任务列表";
				mv.addObject(NormalExcelConstants.CLASS, ExportTaskVO.class);
			}
			if (param.getType() == 1) {
				//手动添加列表
				titleName = "手动下发任务列表";
				mv.addObject(NormalExcelConstants.CLASS, ExportManuallyAddVO.class);
			}
		}


		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, titleName);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(titleName
				, "导出时间:".concat(LocalDate.now().toString())
				, ExcelType.XSSF));
		mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		return mv;
	}


	@AutoLog(value = "巡检计划池-导入手动发布任务")
	@PostMapping(value = "/importManuallyAddTasks")
	@ApiOperation(value = "巡检计划池-导入手动发布任务")
	@Transactional(rollbackFor = Exception.class)
	public Result<?> importManuallyAddTasks(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// 获取上传文件对象
			ImportParams params = new ImportParams();
			params.setTitleRows(1);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<ImportTaskAddVO> tempTaskList = ExcelImportUtil.importExcel(file.getInputStream(), ImportTaskAddVO.class, params);
				List<ImportTaskAddVO> taskList = new ArrayList<>();

				Set<String> nameSet = new HashSet<>();
				Set<String> stationSet = new HashSet<>();
				Set<String> systemSet = new HashSet<>();

				//检测是否有空值
				for (ImportTaskAddVO addVO : tempTaskList) {
					if (addVO == null) {
						return Result.error("导入失败,数据不能为空");
					}
					if (addVO.getTime() == null) {
						return Result.error("导入失败,执行时间不能为空");
					}
					if (StringUtils.isBlank(addVO.getPatrolName())) {
						return Result.error("导入失败,巡检名称不能为空");
					}
					if (StringUtils.isBlank(addVO.getStationCode())) {
						return Result.error("导入失败,站点不能为空");
					}
					if (StringUtils.isBlank(addVO.getSystemName())) {
						return Result.error("导入失败,系统名称不能为空");
					}

					addVO.setPatrolName(addVO.getPatrolName().trim());
					addVO.setStationCode(addVO.getStationCode().trim());
					addVO.setSystemName(addVO.getSystemName().trim());
					if (StringUtils.isNotBlank(addVO.getNote())) {
						addVO.setNote(addVO.getNote().trim());
					}

					nameSet.add(addVO.getPatrolName());

					systemSet.add(addVO.getSystemName());

					if (addVO.getStationCode().contains(PatrolConstant.SPL)) {
						String[] split = addVO.getStationCode().split(PatrolConstant.SPL);
						if (split.length < 1) {
							return Result.error("站点不能为空");
						}
						stationSet.addAll(Arrays.asList(split));

					} else {
						stationSet.add(addVO.getStationCode());
					}
					taskList.add(addVO);
				}

				if (CollectionUtils.isEmpty(systemSet) || CollectionUtils.isEmpty(nameSet) || CollectionUtils.isEmpty(stationSet)) {
					return Result.error("导入失败,"
							.concat(CollectionUtils.isEmpty(systemSet) ? "系统名称 " : "")
							.concat(CollectionUtils.isEmpty(nameSet) ? "巡检表名称 " : "")
							.concat(CollectionUtils.isEmpty(nameSet) ? "站点名称 " : "")
							.concat("不能为空!"));
				}

				List<Subsystem> subsystemList = subsystemService.lambdaQuery()
						.eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0)
						.in(Subsystem::getSystemName, systemSet).list();

				List<Patrol> patrolList = patrolService.lambdaQuery().eq(Patrol::getDelFlag, CommonConstant.DEL_FLAG_0)
						.in(Patrol::getTitle, nameSet)
						.eq(Patrol::getStatus, PatrolConstant.ENABLE)
						.select(Patrol::getTitle, Patrol::getId, Patrol::getTypes).list();

				List<Station> stationList = stationService.lambdaQuery().eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0)
						.in(Station::getStationCode, stationSet)
						.select(Station::getStationCode, Station::getId)
						.list();

				if (CollectionUtils.isEmpty(patrolList)) {
					return Result.error("导入失败,未查询到巡检表");
				}
				if (CollectionUtils.isEmpty(stationList)) {
					return Result.error("导入失败,未查询到站点");
				}
				if (CollectionUtils.isEmpty(subsystemList)) {
					return Result.error("导入失败,未查询到系统");
				}

				//系统
				Map<String, String> systemCodeMap = subsystemList.stream().collect(Collectors.toMap(Subsystem::getSystemName, Subsystem::getSystemCode));
				//巡检表
				Map<String, Long> patrolMap = null;
				try {
					patrolMap = patrolList.stream().collect(Collectors.toMap(p -> p.getTypes().concat("!-!").concat(p.getTitle()), Patrol::getId));
				} catch (Exception e) {
					throw new AiurtBootException("巡检标准内有重复名称数据,请更改或置为无效状态后重新导入");
				}
				//站点
				Map<String, Integer> stationMap = stationList.stream().collect(Collectors.toMap(Station::getStationCode, Station::getId));

				//发放任务
				for (ImportTaskAddVO listPatrol : taskList) {
					TaskAddParam param = new TaskAddParam();


					String code = systemCodeMap.get(listPatrol.getSystemName());
					if (StringUtils.isBlank(code)) {
						throw new AiurtBootException("未找到系统名称:" + listPatrol.getSystemName());
					}

					List<Long> ids = new ArrayList<>();
					Long id = patrolMap.get(code + "!-!" + listPatrol.getPatrolName());
					if (id == null) {
						throw new AiurtBootException("未找到巡检表! 系统: " + listPatrol.getSystemName() + " 中名称: " + listPatrol.getPatrolName());
					}
					ids.add(id);


					List<String> stationIds = new ArrayList<>();

					String stationCode = listPatrol.getStationCode();

					//添加站点id
					if (stationCode.contains(PatrolConstant.SPL)) {
						String[] split = stationCode.split(PatrolConstant.SPL);

						for (String splCode : split) {
							Integer stationId = stationMap.get(splCode);
							if (stationId == null) {
								throw new AiurtBootException("未找到站点,code:" + splCode);
							}
							stationIds.add(stationId.toString());
						}
					} else {
						Integer stationId = stationMap.get(listPatrol.getStationCode());
						if (stationId == null) {
							throw new AiurtBootException("未找到站点,code:" + listPatrol.getStationCode());
						}
						stationIds.add(stationId.toString());
					}


					param.setPatrolIds(ids)
							.setOrganizationIds(stationIds)
							//.setTime(listPatrol.getTime().atTime(23, 59, 59))
							.setTime(LocalDateTime.ofInstant(listPatrol.getTime().toInstant(), ZoneId.of("Asia/Shanghai")))
							.setNote(listPatrol.getNote());

					patrolTaskService.manuallyAddTasks(request, param);
				}

				return Result.ok("文件导入成功！数据行数:" + taskList.size());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new AiurtBootException("文件导入失败:" + e.getMessage());
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


	@AutoLog("巡检计划池-导出巡检任务")
	@RequestMapping(value = "/exportTaskXls")
	@ApiOperation(value = "巡检计划池-导出excel", notes = "巡检计划池-导出excel")
	public void exportTaskXls(HttpServletRequest request, HttpServletResponse response,
	                          PatrolPoolParam param) {
		LocalDate now = LocalDate.now();
		param.setCreateStartTime(now.atTime(0, 0, 0))
				.setCreateEndTime(now.atTime(23, 59, 59));

		if (param.getStationId() == null && param.getLineId() != null) {
			List<Station> list = stationService.list(new LambdaQueryWrapper<Station>()
					.eq(Station::getDelFlag, CommonConstant.DEL_FLAG_0)
					.eq(Station::getLineCode, param.getLineId()).select(Station::getId));
			if (CollectionUtils.isNotEmpty(list)) {
				param.setStationIds(list.stream().map(Station::getId).collect(Collectors.toList()));
			}
		}

		//查询所有符合条件的值
		List<ExportTaskSubmitVO> voList = this.patrolTaskService.selectExportTaskList(param);

		XSSFWorkbook wb = ExportUtils.generateTaskBook(voList, host.concat("pollingCheck/PatrolTaskList?show=true&id="));
		ExportUtils.exportResponse(response, "巡检任务列表");
		try {
			ServletOutputStream os = response.getOutputStream();
			wb.write(os);
			os.close();
		} catch (IOException e) {
			throw new AiurtBootException("导出错误,请稍后重试");
		}
	}


	@AutoLog("巡检计划池-导出手动下发模板")
	@RequestMapping(value = "/exportManuallyAddTasks")
	@ApiOperation(value = "巡检计划池-导出手动下发模板", notes = "巡检计划池-导出手动下发模板")
	public ModelAndView exportManuallyAddTasks() {
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

		//名称
		String titleName = "手动下发任务导入模板";

		//模板值
		List<ImportTaskAddVO> tempTaskList = new ArrayList<>();
		ImportTaskAddVO vo = new ImportTaskAddVO();
		vo.setStationCode("01")
				.setPatrolName("2021年度巡检表")
				.setSystemName("无线通信系统")
				.setNote("备注信息:这是一条模板,请删除此条记录")
				.setTime(new Date());
		tempTaskList.add(vo);

		mv.addObject(NormalExcelConstants.FILE_NAME, titleName);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(titleName
				, "导出时间:".concat(LocalDate.now().toString())
				, ExcelType.XSSF));
		mv.addObject(NormalExcelConstants.CLASS, ImportTaskAddVO.class);
		mv.addObject(NormalExcelConstants.DATA_LIST, tempTaskList);
		return mv;
	}

}
