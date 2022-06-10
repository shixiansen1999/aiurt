package com.aiurt.boot.modules.device.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.TokenUtils;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.aiurt.boot.modules.device.entity.DeviceType;
import com.aiurt.boot.modules.device.service.IDeviceTypeService;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.service.ISubsystemService;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.mapper.SysUserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 设备分类
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "设备分类")
@RestController
@RequestMapping("/device/deviceType")
public class DeviceTypeController {
	@Autowired
	private IDeviceTypeService deviceTypeService;

	@Resource
	private ISysBaseAPI iSysBaseAPI;

	@Autowired
	private SysUserMapper userMapper;

	@Autowired
	private ISubsystemService subsystemService;

	/**
	 * 分页列表查询
	 *
	 * @param name
	 * @param status
	 * @param req
	 * @return
	 */
	@AutoLog(value = "设备分类-分页列表查询")
	@ApiOperation(value = "设备分类-分页列表查询", notes = "设备分类-分页列表查询")
	@GetMapping(value = "/list")
	public Result<List<Subsystem>> queryPageList(String name, Integer status, HttpServletRequest req) {

		List<Subsystem> subsystemList = subsystemService.lambdaQuery().eq(Subsystem::getDelFlag, CommonConstant.DEL_FLAG_0).list();

		if (CollectionUtils.isNotEmpty(subsystemList)) {

			Map<String, String> map = subsystemList.stream().collect(Collectors.toMap(Subsystem::getSystemCode, Subsystem::getSystemName));

			List<DeviceType> typeList = deviceTypeService.lambdaQuery()
					.eq(DeviceType::getDelFlag, CommonConstant.DEL_FLAG_0)
					.like(StringUtils.isNotBlank(name), DeviceType::getName, name)
					.eq(status != null, DeviceType::getStatus, status)
					.orderByDesc(DeviceType::getCreateTime)
					.list();

			List<SysUser> userList = userMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
					.eq(SysUser::getStatus, CommonConstant.STATUS_1));

			Map<String, String> userMap = null;
			if (CollectionUtils.isNotEmpty(userList)) {
				userMap = userList.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getRealname));
			}

			Map<String, String> finalUserMap = userMap;
			if (finalUserMap != null) {
				typeList.forEach(type -> {
					if (type.getCreateBy() != null) {
						String s = finalUserMap.get(type.getCreateBy());
						if (s != null) {
							type.setCreateBy(s);
						}
					}
					if (type.getUpdateBy() != null) {

						String s = finalUserMap.get(type.getUpdateBy());
						if (s != null) {
							type.setUpdateBy(s);
						}
					}
						type.setSystemName(map.get(type.getSystemCode()));
				});
			}
			Map<String, List<DeviceType>> typeMap = null;
			if (CollectionUtils.isNotEmpty(typeList)) {
				typeMap = typeList.stream().collect(Collectors.groupingBy(DeviceType::getSystemCode));
			}

			Map<String, List<DeviceType>> finalTypeMap = typeMap;

			subsystemList.forEach(l -> {
						if (finalTypeMap != null) {
							List<DeviceType> types = finalTypeMap.get(l.getSystemCode());
							l.setDeviceTypeList(types!=null?types:new ArrayList<>());
						} else {
							l.setDeviceTypeList(new ArrayList<>());
						}
					}
			);
		}

		return Result.ok(subsystemList);
	}

	/**
	 * 分页列表(下拉框)
	 *
	 * @return
	 */
	@AutoLog(value = "分页列表(下拉框)")
	@ApiOperation(value = "分页列表(下拉框)", notes = "分页列表(下拉框)")
	@GetMapping(value = "/typeList")
	public Result<List<DeviceType>> typeList() {
		Result<List<DeviceType>> result = new Result<List<DeviceType>>();
		QueryWrapper<DeviceType> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("code", "name").eq("del_flag", 0);
		List<DeviceType> deviceTypeList = deviceTypeService.list(queryWrapper);
		result.setSuccess(true);
		result.setResult(deviceTypeList);
		return result;
	}

	/**
	 * 添加
	 *
	 * @param deviceType
	 * @return
	 */
	@AutoLog(value = "设备分类-添加")
	@ApiOperation(value = "设备分类-添加", notes = "设备分类-添加")
	@PostMapping(value = "/add")
	public Result<DeviceType> add(@RequestBody DeviceType deviceType, HttpServletRequest req) {
		String userId = TokenUtils.getUserId(req, iSysBaseAPI);
		Result<DeviceType> result = new Result<DeviceType>();
		Integer existFlag = deviceTypeService.existCode(deviceType.getCode());
		if (existFlag == 1) {
			result.error500("分类编号重复");
		} else {
			try {
				deviceType.setCreateBy(userId);
				deviceTypeService.save(deviceType);
				result.success("添加成功！");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				result.error500("操作失败");
			}
		}
		return result;
	}

	/**
	 * 编辑
	 *
	 * @param deviceType
	 * @return
	 */
	@AutoLog(value = "设备分类-编辑")
	@ApiOperation(value = "设备分类-编辑", notes = "设备分类-编辑")
	@PutMapping(value = "/edit")
	public Result<DeviceType> edit(@RequestBody DeviceType deviceType, HttpServletRequest req) {
		String userId = TokenUtils.getUserId(req, iSysBaseAPI);
		Result<DeviceType> result = new Result<DeviceType>();
		DeviceType deviceTypeEntity = deviceTypeService.getById(deviceType.getId());
		if (deviceTypeEntity == null) {
			result.onnull("未找到对应实体");
		} else {
			deviceType.setUpdateBy(userId);
			boolean ok = deviceTypeService.updateById(deviceType);
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
	@AutoLog(value = "设备分类-通过id删除")
	@ApiOperation(value = "设备分类-通过id删除", notes = "设备分类-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			deviceTypeService.removeById(id);
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
	@AutoLog(value = "设备分类-批量删除")
	@ApiOperation(value = "设备分类-批量删除", notes = "设备分类-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<DeviceType> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<DeviceType> result = new Result<DeviceType>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.deviceTypeService.removeByIds(Arrays.asList(ids.split(",")));
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
	@AutoLog(value = "设备分类-通过id查询")
	@ApiOperation(value = "设备分类-通过id查询", notes = "设备分类-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DeviceType> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<DeviceType> result = new Result<DeviceType>();
		DeviceType deviceType = deviceTypeService.getById(id);
		if (deviceType == null) {
			result.onnull("未找到对应实体");
		} else {
			result.setResult(deviceType);
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
		QueryWrapper<DeviceType> queryWrapper = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				DeviceType deviceType = JSON.parseObject(deString, DeviceType.class);
				queryWrapper = QueryGenerator.initQueryWrapper(deviceType, request.getParameterMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<DeviceType> pageList = deviceTypeService.list(queryWrapper);
		pageList.forEach(x -> {
			if (StrUtil.isNotEmpty(x.getCreateBy())) {
				final SysUser sysUser = userMapper.selectById(x.getCreateBy());
				x.setCreateBy(sysUser.getRealname());
			}
			if (StrUtil.isNotEmpty(x.getUpdateBy())) {
				final SysUser sysUser = userMapper.selectById(x.getUpdateBy());
				x.setUpdateBy(sysUser.getRealname());
			}
		});
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "设备分类列表");
		mv.addObject(NormalExcelConstants.CLASS, DeviceType.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("设备分类列表数据", "导出时间:" + LocalDate.now(), ExcelType.XSSF));
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
				List<DeviceType> listDeviceTypes = ExcelImportUtil.importExcel(file.getInputStream(), DeviceType.class, params);
				deviceTypeService.saveBatch(listDeviceTypes);
				return Result.ok("文件导入成功！数据行数:" + listDeviceTypes.size());
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
