package com.aiurt.boot.modules.patrol.controller;


import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.Patrol;
import com.aiurt.boot.modules.patrol.entity.PatrolContent;
import com.aiurt.boot.modules.patrol.service.IPatrolContentService;
import com.aiurt.boot.modules.patrol.service.IPatrolService;
import com.aiurt.boot.modules.patrol.vo.importdir.PatrolContentImportVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 巡检项内容
 * @Author: Mr.zhao
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "巡检项内容")
@RestController
@RequestMapping("/patrol.patrol_content/patrolContent")
public class PatrolContentController {

	@Resource
	private IPatrolContentService patrolContentService;

	@Resource
	private IPatrolService patrolService;



	@AutoLog(value = "巡检项内容-树型查询")
	@ApiOperation(value = "巡检项内容-树型查询", notes = "巡检项内容-分页列表查询")
	@GetMapping(value = "/tree")
	public Result<?> queryTree(@RequestParam(name = "id") Long id) {
		return patrolContentService.queryTree(id);
	}


	@AutoLog(value = "巡检项内容-分页列表查询")
	@ApiOperation(value = "巡检项内容-分页列表查询", notes = "巡检项内容-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> list(@RequestParam(name = "id") Long id) {
		return patrolContentService.queryList(id);
	}


	@AutoLog(value = "巡检项内容-添加")
	@ApiOperation(value = "巡检项内容-添加", notes = "巡检项内容-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody PatrolContent patrolContent) {

		if (patrolContent == null || patrolContent.getSequence() < 1) {
			return Result.error("排序不能小于1");
		}
		if (patrolContent.getCode() == null) {
			return Result.error("编号不能为空");
		}
		if (patrolContent.getRecordId() == null) {
			return Result.error("上级编号不能为空");
		}

		PatrolContent one = patrolContentService.lambdaQuery()
				.eq(PatrolContent::getDelFlag, CommonConstant.DEL_FLAG_0)
				.eq(PatrolContent::getRecordId, patrolContent.getRecordId())
				.last("limit 1")
				.and(query -> {
					query.eq(PatrolContent::getParentId, patrolContent.getParentId())
							.eq(PatrolContent::getSequence, patrolContent.getSequence())
							.or().eq(PatrolContent::getCode, patrolContent.getCode());
				})
				.one();
		if (one != null) {
			return Result.error("巡检项编号与同层显示顺序序号,不能重复");
		}
		if (patrolContent.getStatusItem() == null) {
			patrolContent.setStatusItem(CommonConstant.STATUS_DISABLE);
		}
		patrolContent.setDelFlag(CommonConstant.DEL_FLAG_0);

		if (patrolContentService.save(patrolContent)) {
			return Result.ok("添加成功");
		} else {
			return Result.error("添加失败");
		}
	}


	@AutoLog(value = "巡检项内容-编辑")
	@ApiOperation(value = "巡检项内容-编辑", notes = "巡检项内容-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody PatrolContent patrolContent) {
		if (patrolContent.getContent() != null) {
			patrolContent.setContent(patrolContent.getContent().trim());
		}
		if (patrolContent.getSequence() < 1) {
			return Result.error("排序不能小于1");
		}
		PatrolContent byId = patrolContentService.getById(patrolContent.getId());
		if (byId.getCode() != null && byId.getCode().equals(patrolContent.getCode()) && byId.getSequence() != null && byId.getSequence().equals(patrolContent.getSequence())) {
			//若序号和code相同,直接通过
		} else {
			PatrolContent one = patrolContentService.lambdaQuery()
					.eq(PatrolContent::getDelFlag, CommonConstant.DEL_FLAG_0)
					.eq(PatrolContent::getRecordId, patrolContent.getRecordId())
					.ne(PatrolContent::getId, patrolContent.getId())
					.last("limit 1")
					.and(query -> {
						query.eq(PatrolContent::getParentId, patrolContent.getParentId())
								.eq(PatrolContent::getSequence, patrolContent.getSequence())
								.or().eq(PatrolContent::getCode, patrolContent.getCode()
								);

					})
					.one();
			if (one != null) {
				return Result.error("巡检项编号与同层显示顺序序号,不能重复");
			}
		}
		if (patrolContent.getStatusItem() == null) {
			patrolContent.setStatusItem(CommonConstant.PATROL_STATUS_DISABLE);
		}

		patrolContentService.updateById(patrolContent);

		if (ObjectUtils.notEqual(byId.getCode(), patrolContent.getCode())) {
			PatrolContent content = new PatrolContent();
			content.setParentId(patrolContent.getCode());
			patrolContentService.lambdaUpdate()
					.eq(PatrolContent::getDelFlag, CommonConstant.DEL_FLAG_0)
					.eq(PatrolContent::getRecordId, patrolContent.getRecordId())
					.eq(PatrolContent::getParentId, byId.getCode())
					.update(content);
		}

		return Result.ok("修改成功");
	}


	@AutoLog(value = "巡检项内容-通过id删除")
	@ApiOperation(value = "巡检项内容-通过id删除", notes = "巡检项内容-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id") String id) {
		try {
			PatrolContent byId = patrolContentService.getById(id);
			List<Long> ids = new ArrayList<>();
			ids.add(byId.getId());
			List<Long> codeList = new ArrayList<>();
			codeList.add(byId.getCode());
			while (true) {
				List<PatrolContent> list = patrolContentService.lambdaQuery()
						.eq(PatrolContent::getRecordId, byId.getRecordId())
						.in(PatrolContent::getParentId, codeList)
						.select(PatrolContent::getCode, PatrolContent::getId)
						.list();
				if (CollectionUtils.isNotEmpty(list)) {
					Map<Long, Long> map = list.stream().collect(Collectors.toMap(PatrolContent::getId, PatrolContent::getCode));
					ids.addAll(map.keySet());
					codeList.clear();
					codeList.addAll(map.values());
				} else {
					break;
				}
			}
			boolean flag = patrolContentService.removeByIds(ids);
			if (!flag) {
				log.error("删除失败!");
				return Result.error("删除失败!");
			}
		} catch (Exception e) {
			log.error("删除失败,{}", e.getMessage());
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
	public Result<PatrolContent> deleteBatch(@RequestParam(name = "ids") String ids) {
		Result<PatrolContent> result = new Result<>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.patrolContentService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}


	@AutoLog(value = "巡检项内容-通过id查询")
	@ApiOperation(value = "巡检项内容-通过id查询", notes = "巡检项内容-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolContent> queryById(@RequestParam(name = "id") String id) {
		Result<PatrolContent> result = new Result<>();
		PatrolContent patrolContent = patrolContentService.getById(id);
		if (patrolContent == null) {
			result.setResult(new PatrolContent());
			result.setSuccess(true);
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
	@RequestMapping(value = "/exportXlsDemo")
	public ModelAndView exportXlsDemo(HttpServletRequest request, HttpServletResponse response) {

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<PatrolContentImportVO> pageList = new ArrayList<>();

		PatrolContentImportVO vo = new PatrolContentImportVO();
		vo.setContent("巡检项内容")
				.setNote("备注信息")
				.setType(99)
				.setSequence(1)
				.setStatusItem(99)
				.setParentName(-1L)
				.setCode(-1L);
		pageList.add(vo);

		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "巡检项内容模板");
		mv.addObject(NormalExcelConstants.CLASS, PatrolContentImportVO.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("巡检项内容模板", "导出时间:".concat(LocalDate.now().toString()), ExcelType.XSSF));
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
	}


	/**
	 * 导出excel
	 *
	 * @param request  请求
	 * @param response 响应
	 * @param id       id
	 * @return {@code ModelAndView}
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) {
		Patrol byId = patrolService.getById(id);
		if (byId == null || StringUtils.isBlank(byId.getTitle())) {
			throw new SwscException("未查询到此巡检项");
		}
		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<PatrolContentImportVO> pageList = patrolContentService.selectExportList(id);
		for (PatrolContentImportVO importVO : pageList) {
			if (importVO.getParentName() == null) {
				importVO.setParentName(PatrolConstant.NUM_LONG_0);
			}
		}

		if (CollectionUtils.isEmpty(pageList)) {
			pageList = new ArrayList<>();
			PatrolContentImportVO vo = new PatrolContentImportVO();
			vo.setContent("巡检项内容")
					.setNote("备注信息")
					.setType(99)
					.setSequence(1)
					.setStatusItem(99)
					.setParentName(-1L)
					.setCode(-1L);
			pageList.add(vo);
		}
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "巡检项内容列表");
		mv.addObject(NormalExcelConstants.CLASS, PatrolContentImportVO.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(byId.getTitle(), "导出时间:".concat(LocalDate.now().toString()), ExcelType.XSSF));
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
	@Transactional(rollbackFor = Exception.class)
	@RequestMapping(value = "/importExcel")
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response,@RequestParam("patrolId") @NotNull(message = "巡检标准id不能为空") Long tempId) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		//String patrolId = request.getHeader("patrolId");
		//if (StringUtils.isBlank(patrolId)) {
		//	return Result.error("标准id不能为空");
		//}
		//Long tempId = null;
		//try {
		//	tempId = Long.valueOf(patrolId);
		//} catch (NumberFormatException e) {
		//	return Result.error("标准id不能为空");
		//}
		final Long id = tempId;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(1);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<PatrolContentImportVO> listPatrolContents = null;
				try {
					listPatrolContents = ExcelImportUtil.importExcel(file.getInputStream(), PatrolContentImportVO.class, params);
				} catch (Exception e) {
					throw new SwscException("文档格式错误");
				}
				listPatrolContents.forEach(l -> {
					if (l.getParentName() == null) {
						l.setParentName(PatrolConstant.NUM_LONG_0);
					}
					if (l.getStatusItem() == null) {
						l.setStatusItem(CommonConstant.PATROL_STATUS_DISABLE);
					}
				});
				List<PatrolContentImportVO> finalListPatrolContents = listPatrolContents;
				Optional.ofNullable(listPatrolContents).ifPresent(l -> {
					List<Long> codeList = new ArrayList<>();
					Map<Long, List<Integer>> sMap = new HashMap<>();
					l.forEach(c -> {
						if (c.getCode() == null) {
							throw new SwscException("巡检项code不能为空");
						}
						if (c.getContent() == null) {
							throw new SwscException("巡检项内容不能为空");
						}
						if (c.getSequence() == null) {
							throw new SwscException("巡检项排序不能为空");
						}

						if (codeList.contains(c.getCode())) {
							throw new SwscException("巡检项code不能重复");
						} else {
							codeList.add(c.getCode());
						}

						List<Integer> list = sMap.get(c.getCode());

						if (CollectionUtils.isEmpty(list)) {
							list = new ArrayList<>();
						}

						if (list.contains(c.getSequence())) {
							throw new SwscException("同级排序序号不能重复");
						} else {
							list.add(c.getSequence());
						}
						sMap.put(c.getCode(), list);
					});
					List<PatrolContent> patrolContents = this.patrolContentService.lambdaQuery()
							.eq(PatrolContent::getDelFlag, CommonConstant.DEL_FLAG_0)
							.eq(PatrolContent::getRecordId, id).list();
					if (CollectionUtils.isNotEmpty(patrolContents)) {
						Map<Long, List<PatrolContent>> listMap = patrolContents.stream().collect(Collectors.groupingBy(PatrolContent::getParentId));
						Map<Long, Long> contentMap = patrolContents.stream().collect(Collectors.toMap(PatrolContent::getCode, PatrolContent::getId));

						//记录父级编号是否存在
						List<Long> importParent = finalListPatrolContents.stream().map(PatrolContentImportVO::getParentName).collect(Collectors.toList());
						List<Long> importCode = finalListPatrolContents.stream().map(PatrolContentImportVO::getCode).collect(Collectors.toList());
						List<Long> sqlCode = patrolContents.stream().map(PatrolContent::getCode).collect(Collectors.toList());
						Set<Long> set = new HashSet<>();
						set.addAll(sqlCode);
						set.addAll(importCode);
						List<Long> collect = importParent.stream().filter(f -> {
							if (!Objects.equals(f, PatrolConstant.NUM_LONG_0) && !set.contains(f)) {
								return true;
							}
							return false;
						}).collect(Collectors.toList());
						if (CollectionUtils.isNotEmpty(collect)) {
							throw new SwscException("未找到父级编号为:".concat(StringUtils.join(collect, PatrolConstant.SPL)).concat(" 的值!"));
						}
						//判断同等级序号
						for (Long code : sMap.keySet()) {
							if (contentMap.containsKey(code)) {
								List<PatrolContent> contents = listMap.get(contentMap.get(code));
								if (CollectionUtils.isNotEmpty(contents)) {
									List<Integer> sequenceList = contents.stream().map(PatrolContent::getSequence).collect(Collectors.toList());
									List<Integer> list = sMap.get(code);
									for (Integer sequence : list) {
										if (sequenceList.contains(sequence)) {
											throw new SwscException("同级排序序号不能重复");
										}
									}
								}
							}
						}

					}

					List<PatrolContent> list = new ArrayList<>();
					for (PatrolContentImportVO c : finalListPatrolContents) {
						PatrolContent content = new PatrolContent();
						BeanUtils.copyProperties(c, content);
						content.setDelFlag(0).setRecordId(id).setParentId(c.getParentName());
						list.add(content);
					}
					boolean flag = this.patrolContentService.saveBatch(list);
					if (!flag) {
						throw new SwscException("请稍后重试!");
					}
				});
				return Result.ok("文件导入成功！数据行数:" + listPatrolContents.size());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new SwscException("文件导入失败！原因:".concat(e.getMessage()));
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
