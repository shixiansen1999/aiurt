package com.aiurt.boot.modules.patrol.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.RoleAdditionalUtils;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.Patrol;
import com.aiurt.boot.modules.patrol.entity.PatrolContent;
import com.aiurt.boot.modules.patrol.param.PatrolPageParam;
import com.aiurt.boot.modules.patrol.service.IPatrolContentService;
import com.aiurt.boot.modules.patrol.service.IPatrolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 巡检标准
 * @Author: Mr.zhao
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

	private final RoleAdditionalUtils roleAdditionalUtils;

	private final IStationService stationService;

	/**
	 * 分页列表查询
	 *
	 * @param param    参数
	 * @param pageNo   页码
	 * @param pageSize 页面大小
	 * @return {@code Result<?>}
	 */
	@AutoLog(value = "巡检标准-分页列表查询")
	@ApiOperation(value = "巡检标准-分页列表查询", notes = "巡检标准-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(PatrolPageParam param,
	                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		return patrolService.pageList(param, pageNo, pageSize);
	}


	/**
	 * 全部列表查询
	 *
	 * @return {@code Result<?>}
	 */
	@AutoLog(value = "巡检标准-全部列表查询")
	@ApiOperation(value = "巡检标准-全部列表查询", notes = "巡检标准-全部列表查询")
	@GetMapping(value = "/allList")
	public Result<?> queryPageList() {

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		List<String> sysCodes = roleAdditionalUtils.getListSystemCodesByUserId(user.getId());
		List<String> departIds = roleAdditionalUtils.getListDepartIdsByUserId(user.getId());
		List<Integer> stationIds = null;
		if (CollectionUtils.isNotEmpty(departIds)){
			List<Station> stationList = stationService.lambdaQuery().select(Station::getId).in(Station::getTeamId, departIds).list();
			if (CollectionUtils.isEmpty(stationList)){
				return Result.ok(new ArrayList<>());
			}
			stationIds = stationList.stream().map(Station::getId).collect(Collectors.toList());
		}

		List<Patrol> list = this.patrolService.lambdaQuery()
				.eq(Patrol::getStatus, PatrolConstant.ENABLE)
				.select(Patrol::getTitle, Patrol::getId,Patrol::getTypes,Patrol::getOrganizationIds)
				.eq(Patrol::getDelFlag, CommonConstant.DEL_FLAG_0)
				.in(CollectionUtils.isNotEmpty(sysCodes),Patrol::getTypes, sysCodes)
				.orderByDesc(Patrol::getId)
				.list();

		if (CollectionUtils.isNotEmpty(departIds)){
			 if (CollectionUtils.isNotEmpty(list)){
				List<Integer> finalStationIds = stationIds;
				list = list.stream().filter(p->{
					if (StringUtils.isBlank(p.getOrganizationIds())||CollectionUtils.isEmpty(finalStationIds)){
						return false;
					}
					List<Integer> integerList = Arrays.stream(p.getOrganizationIds().split(PatrolConstant.SPL)).map(Integer::parseInt).collect(Collectors.toList());
					for (Integer integer : integerList) {
						if (finalStationIds.contains(integer)){
							return true;
						}
					}
					return false;
				}).collect(Collectors.toList());
			}
		}

		return Result.ok(list);
	}


	/**
	 * 详细的策略
	 *
	 * @param id id
	 * @return {@link Result}<{@link ?}>
	 */
	@AutoLog(value = "巡检标准-策略详情")
	@ApiOperation(value = "巡检标准-策略详情", notes = "巡检标准-策略详情")
	@GetMapping(value = "/detailStrategy")
	public Result<?> detailStrategy(@RequestParam("id") Long id) {
		return patrolService.detailStrategy(id);
	}


	/**
	 * 修改策略
	 *
	 * @param patrol 巡检标准
	 * @return {@link Result}<{@link ?}>
	 */
	@AutoLog(value = "巡检标准-修改策略")
	@ApiOperation(value = "巡检标准-修改策略", notes = "巡检标准-修改策略")
	@PostMapping(value = "/updateTactics")
	public Result<Patrol> updateTactics(@RequestBody Patrol patrol) {

		Result<Patrol> result = new Result<>();

		if (patrol == null) {
			throw new SwscException("未找到对应实体");
		}

		if (patrol.getTactics() != null) {
			//编辑时做频率限制
			if (StringUtils.isNotBlank(patrol.getDayOfWeek())) {
				Integer tactics = patrol.getTactics();
				if (tactics == 2) {
					if (patrol.getDayOfWeek().trim().length() > 3) {
						return result.error500("不能设置超过频率限制的天数");
					}
				}
				if (tactics == 3) {
					if (patrol.getDayOfWeek().trim().length() > 1) {
						return result.error500("不能设置超过频率限制的天数");
					}
				}
			}

		}

		Patrol patrolEntity = patrolService.getById(patrol.getId());
		if (patrolEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			if (patrolService.updateById(patrol)) {
				result.success("修改成功!");
			} else {
				result.error500("修改失败");
			}
		}

		return result;
	}


	/**
	 * 添加
	 *
	 * @param patrol 巡检标准
	 * @return {@code Result<Patrol>}
	 */
	@AutoLog(value = "巡检标准-添加")
	@ApiOperation(value = "巡检标准-添加", notes = "巡检标准-添加")
	@PostMapping(value = "/add")
	public Result<Patrol> add(@RequestBody Patrol patrol) {
		Result<Patrol> result = new Result<>();

		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		List<String> sysCodes = roleAdditionalUtils.getListSystemCodesByUserId(user.getId());
		if (CollectionUtils.isNotEmpty(sysCodes) && !sysCodes.contains(patrol.getTypes())) {
			return Result.error("您无添加此系统权限");
		}
		if (patrol.getOrganizationIds() != null) {
			List<String> departList = roleAdditionalUtils.getListDepartIdsByUserId(user.getId());
			if (CollectionUtils.isNotEmpty(departList)) {
				List<Station> stationList = stationService.lambdaQuery().select(Station::getId).in(Station::getTeamId, departList).list();
				if (CollectionUtils.isEmpty(stationList)) {
					return Result.error("您无添加此类站点权限");
				}
				List<Integer> stationIds = stationList.stream().map(Station::getId).collect(Collectors.toList());
				String[] split = patrol.getOrganizationIds().split(PatrolConstant.SPL);
				List<Integer> integerList = Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
				if (!stationIds.containsAll(integerList)) {
					return Result.error("您无添加此类站点权限");
				}
			}
		} else {
			return Result.error("站点不能为空");
		}
		patrol.setDelFlag(CommonConstant.DEL_FLAG_0);
		try {
			patrolService.save(patrol);
			result.success("添加成功!");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}


	/**
	 * 编辑
	 *
	 * @param patrol 巡检标准
	 * @return {@code Result<Patrol>}
	 */
	@AutoLog(value = "巡检标准-编辑")
	@ApiOperation(value = "巡检标准-编辑", notes = "巡检标准-编辑")
	@PutMapping(value = "/edit")
	public Result<Patrol> edit(@RequestBody @NotNull(message = "修改对象不能为空") Patrol patrol) {
		Result<Patrol> result = new Result<>();
		if (patrol.getId() == null) {
			return Result.error("id不能为空");
		}
		if (patrol.getTactics() != null) {
			//编辑时做频率限制
			if (StringUtils.isNotBlank(patrol.getDayOfWeek())) {
				Integer tactics = patrol.getTactics();
				if (PatrolConstant.PATROL_TACTICS_2.equals(tactics)) {
					if (patrol.getDayOfWeek().trim().length() > 3) {
						return result.error500("不能设置超过频率限制的天数");
					}
				}
				if (PatrolConstant.PATROL_TACTICS_3.equals(tactics)) {
					if (patrol.getDayOfWeek().trim().length() > 1) {
						return result.error500("不能设置超过频率限制的天数");
					}
				}
			}
		}

		Patrol patrolEntity = patrolService.getById(patrol.getId());
		if (patrolEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			if (patrolService.updateById(patrol)) {
				result.success("修改成功!");
			} else {
				result.error500("修改失败");
			}
		}

		return result;
	}

	/**
	 * 通过id删除
	 *
	 * @param id id
	 * @return {@code Result<?>}
	 */
	@AutoLog(value = "巡检标准-通过id删除")
	@ApiOperation(value = "巡检标准-通过id删除", notes = "巡检标准-通过id删除")
	@DeleteMapping(value = "/delete")
	@Transactional(rollbackFor = Exception.class)
	public Result<?> delete(@RequestParam(name = "id") Long id) {
		try {
			patrolService.updateById(new Patrol().setId(id).setDelFlag(CommonConstant.DEL_FLAG_1));
			patrolContentService.lambdaUpdate().eq(PatrolContent::getRecordId, id)
					.update(new PatrolContent().setDelFlag(CommonConstant.DEL_FLAG_1));

		} catch (Exception e) {
			log.error("删除失败,{}", e.getMessage());
			throw new SwscException("删除失败!");
		}
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids id
	 * @return {@code Result<Patrol>}
	 */
	@AutoLog(value = "巡检标准-批量删除")
	@ApiOperation(value = "巡检标准-批量删除", notes = "巡检标准-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	@Transactional(rollbackFor = Exception.class)
	public Result<Patrol> deleteBatch(@RequestParam(name = "ids") @NotBlank(message = "参数不能为空") String ids) {
		Result<Patrol> result = new Result<>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.patrolService.lambdaUpdate().in(Patrol::getId, ids)
					.update(new Patrol().setDelFlag(CommonConstant.DEL_FLAG_1));
			patrolContentService.lambdaUpdate().in(PatrolContent::getRecordId, ids)
					.update(new PatrolContent().setDelFlag(CommonConstant.DEL_FLAG_1));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	 * 通过id查询
	 *
	 * @param id id
	 * @return {@code Result<Patrol>}
	 */
	@AutoLog(value = "巡检标准-通过id查询")
	@ApiOperation(value = "巡检标准-通过id查询", notes = "巡检标准-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Patrol> queryById(@RequestParam(name = "id") String id) {
		Result<Patrol> result = new Result<>();
		Patrol patrol = patrolService.getById(id);
		if (patrol == null) {
			result.onnull("未找到对应实体");
		} else {
			result.setResult(patrol);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * 导出excel
	 *
	 * @param request  请求
	 * @param response 响应
	 * @return {@code ModelAndView}
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
	 * 导入excel
	 *
	 * @param request  请求
	 * @param response 响应
	 * @return {@code Result<?>}
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(1);
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
