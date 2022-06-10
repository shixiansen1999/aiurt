package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.RoleAdditionalUtils;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.EscalationPlan;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IEscalationPlanService;
import com.aiurt.boot.modules.secondLevelWarehouse.vo.EscalationPlanExportVO;
import com.aiurt.boot.modules.system.entity.SysDictItem;
import com.aiurt.boot.modules.system.service.ISysDictItemService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 提报计划表
 * @Author: Mr.zhao
 * @Date: 2021-11-09
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "提报计划表")
@RestController
@RequestMapping("/secondLevelWarehouse/escalationPlan")
public class EscalationPlanController {

	@Resource
	private IEscalationPlanService escalationPlanService;
	@Resource
	private ISysDictItemService sysDictItemService;
	@Resource
	private ISubsystemService subsystemService;
	@Resource
	private RoleAdditionalUtils roleAdditionalUtils;


	@AutoLog(value = "提报计划表-分页列表查询")
	@ApiOperation(value = "提报计划表-分页列表查询", notes = "提报计划表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EscalationPlan>> queryPageList(EscalationPlan escalationPlan,
	                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
	                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
	                                                   HttpServletRequest req) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		Result<IPage<EscalationPlan>> result = new Result<>();
		QueryWrapper<EscalationPlan> queryWrapper = QueryGenerator.initQueryWrapper(escalationPlan, req.getParameterMap());
		Page<EscalationPlan> page = new Page<>(pageNo, pageSize);

		queryWrapper.lambda().in(EscalationPlan::getDelFlag, CommonConstant.DEL_FLAG_0);

		//权限控制
		List<String> systemCodes = roleAdditionalUtils.getListSystemCodesByUserId(user.getId());
		if (CollectionUtils.isNotEmpty(systemCodes)){
			queryWrapper.lambda().in(EscalationPlan::getSystemType,systemCodes);
		}
		IPage<EscalationPlan> pageList = escalationPlanService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}


	@AutoLog(value = "提报计划表-添加")
	@ApiOperation(value = "提报计划表-添加", notes = "提报计划表-添加")
	@PostMapping(value = "/add")
	public Result<EscalationPlan> add(@RequestBody EscalationPlan escalationPlan) {
		Result<EscalationPlan> result = new Result<EscalationPlan>();
		try {
			escalationPlanService.save(escalationPlan);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}


	@AutoLog(value = "提报计划表-编辑")
	@ApiOperation(value = "提报计划表-编辑", notes = "提报计划表-编辑")
	@PutMapping(value = "/edit")
	public Result<EscalationPlan> edit(@RequestBody EscalationPlan escalationPlan) {
		Result<EscalationPlan> result = new Result<EscalationPlan>();
		EscalationPlan escalationPlanEntity = escalationPlanService.getById(escalationPlan.getId());
		if (escalationPlanEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			boolean ok = escalationPlanService.updateById(escalationPlan);
			if (ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}


	@AutoLog(value = "提报计划表-通过id删除")
	@ApiOperation(value = "提报计划表-通过id删除", notes = "提报计划表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			escalationPlanService.removeById(id);
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
	@AutoLog(value = "提报计划表-批量删除")
	@ApiOperation(value = "提报计划表-批量删除", notes = "提报计划表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<EscalationPlan> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<EscalationPlan> result = new Result<EscalationPlan>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.escalationPlanService.removeByIds(Arrays.asList(ids.split(",")));
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
	@AutoLog(value = "提报计划表-通过id查询")
	@ApiOperation(value = "提报计划表-通过id查询", notes = "提报计划表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EscalationPlan> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<EscalationPlan> result = new Result<EscalationPlan>();
		EscalationPlan escalationPlan = escalationPlanService.getById(id);
		if (escalationPlan == null) {
			result.onnull("未找到对应实体");
		} else {
			result.setResult(escalationPlan);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * 导出excel
	 */
	@AutoLog(value = "提报计划表-导出excel")
	@ApiOperation(value = "提报计划表-导出excel", notes = "提报计划表-导出excel")
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
		// Step.1 组装查询条件

		List<EscalationPlanExportVO> pageList = null;
		EscalationPlan escalationPlan = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				escalationPlan = JSON.parseObject(deString, EscalationPlan.class);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

		//权限控制
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		//权限控制
		List<String> systemCodes = roleAdditionalUtils.getListSystemCodesByUserId(user.getId());

		pageList = escalationPlanService.selectExportXls(escalationPlan,systemCodes);

		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "提报计划表列表");
		mv.addObject(NormalExcelConstants.CLASS, EscalationPlanExportVO.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("提报计划表列表数据", "导出时间:".concat(LocalDate.now().toString()), ExcelType.XSSF));
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
	@AutoLog(value = "提报计划表-通过excel导入数据")
	@ApiOperation(value = "提报计划表-通过excel导入数据", notes = "提报计划表-通过excel导入数据")
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
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
				List<EscalationPlanExportVO> listEscalationPlans = ExcelImportUtil.importExcel(file.getInputStream(), EscalationPlanExportVO.class, params);

				List<SysDictItem> type = sysDictItemService.selectByDictCode("escalation_type");
				List<SysDictItem> funds = sysDictItemService.selectByDictCode("source_funds");
				List<Subsystem> systemList = subsystemService.list(new LambdaQueryWrapper<Subsystem>()
						.eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0)
						.select(Subsystem::getSystemCode, Subsystem::getSystemName)
				);
				if (CollectionUtils.isEmpty(funds)) {
					throw new AiurtBootException("未查询到资金出处,请添加后重新导入!");
				}
				if (CollectionUtils.isEmpty(type)) {
					throw new AiurtBootException("未查询到提报类型,请添加后重新导入!");
				}
				if (CollectionUtils.isEmpty(systemList)) {
					throw new AiurtBootException("未查询到系统名称,请添加后重新导入!");
				}


				Map<String, String> fundMap = funds.stream().collect(Collectors.toMap(SysDictItem::getItemText, SysDictItem::getItemValue));
				Map<String, String> typeMap = type.stream().collect(Collectors.toMap(SysDictItem::getItemText, SysDictItem::getItemValue));
				Map<String, String> systemMap = systemList.stream().collect(Collectors.toMap(Subsystem::getSystemName, Subsystem::getSystemCode));

				List<EscalationPlan> list = new ArrayList<>();

				for (EscalationPlanExportVO exportVO : listEscalationPlans) {
					if (StringUtils.isBlank(exportVO.getReportType())) {
						throw new AiurtBootException("提报类型不能为空");
					}
					if (!typeMap.containsKey(exportVO.getReportType())) {
						throw new AiurtBootException("未查询到提报类型,请添加后重新导入!");
					}
					if (StringUtils.isNotBlank(exportVO.getSourceFunds()) && !fundMap.containsKey(exportVO.getSourceFunds())) {
						throw new AiurtBootException("未查询到资金出处,请添加后重新导入!");
					}
					if (StringUtils.isBlank(exportVO.getSystemName())) {
						throw new AiurtBootException("系统名称不能为空");
					}
					if (!systemMap.containsKey(exportVO.getSystemName())) {
						throw new AiurtBootException("未查询到系统名称,请添加后重新导入!");
					}
					if (StringUtils.isBlank(exportVO.getReportYear())) {
						throw new AiurtBootException("年份信息不能为空!");
					}
					if (exportVO.getReportYear().trim().length() != 4) {
						throw new AiurtBootException("年份信息需为4位数字,例如:2021");
					}
					if (StringUtils.isBlank(exportVO.getSpecialtyType())) {
						throw new AiurtBootException("专业类型不能为空!");
					}
					if (StringUtils.isBlank(exportVO.getName())) {
						throw new AiurtBootException("物资名称不能为空!");
					}
					if (StringUtils.isBlank(exportVO.getBrand())) {
						throw new AiurtBootException("品牌不能为空!");
					}
					if (StringUtils.isBlank(exportVO.getUnit())) {
						throw new AiurtBootException("单位不能为空!");
					}

					EscalationPlan plan = new EscalationPlan();

					BeanUtils.copyProperties(exportVO, plan);
					try {
						plan.setReportType(Integer.parseInt(typeMap.get(exportVO.getReportType())));
					} catch (NumberFormatException e) {
						throw new AiurtBootException("类型错误,请勿设置提报类型的数据值为非数字型");
					}
					plan.setSystemType(systemMap.get(exportVO.getSystemName()));
					if (StringUtils.isNotBlank(exportVO.getSourceFunds())) {
						try {
							plan.setSourceFunds(Integer.parseInt(fundMap.get(exportVO.getSourceFunds())));
						} catch (NumberFormatException e) {
							throw new AiurtBootException("类型错误,请勿设置资金出处的数据值为非数字型");
						}
					}
					plan.setDelFlag(CommonConstant.DEL_FLAG_0);

					list.add(plan);
				}

				if (!escalationPlanService.saveBatch(list)) {
					throw new AiurtBootException("保存失败");
				}
				return Result.ok("文件导入成功！数据行数:" + listEscalationPlans.size());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				if (e.getMessage().contains("Data too long for column")) {
					throw new AiurtBootException("文件导入失败,原因:".concat("字段过长"));
				}
				throw new AiurtBootException("文件导入失败,原因:" + e.getMessage());
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



	/**
	 * 导出示例模板excel
	 */
	@AutoLog(value = "提报计划表-导出示例模板excel")
	@ApiOperation(value = "提报计划表-导出示例模板excel", notes = "提报计划表-导出示例模板excel")
	@RequestMapping(value = "/exportDemoXls",method = RequestMethod.GET)
	public ModelAndView exportDemoXls() {
		List<EscalationPlanExportVO> listEscalationPlans = new ArrayList<>();
		EscalationPlanExportVO vo = new EscalationPlanExportVO();
		vo.setReportYear("2021")
				.setReportType("普通")
				.setSpecialtyType("通信")
				.setSystemName("无线系统")
				.setName("MTP-3150对讲机天线")
				.setBrand("摩托罗拉")
				.setSpecifications("带背夹")
				.setParameter("2200毫安时")
				.setUnit("块")
				.setNums(10)
				.setArrivalNum(5)
				.setUnitPrice(new BigDecimal(700))
				.setTotalPrice(new BigDecimal(7000))
				.setSourceFunds("地铁")
				.setPurchaseTime(new Date())
				.setOrgId("部门名称")
				.setImgUrl("这是一条示例,请删除本行");
		listEscalationPlans.add(vo);

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "提报计划表导入模板");
		mv.addObject(NormalExcelConstants.CLASS, EscalationPlanExportVO.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("提报计划表导入模板", "导出时间:".concat(LocalDate.now().toString()), ExcelType.XSSF));
		mv.addObject(NormalExcelConstants.DATA_LIST, listEscalationPlans);
		return mv;
	}
}
