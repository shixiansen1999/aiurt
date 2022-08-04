package com.aiurt.modules.device.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.entity.DeviceCompose;
import com.aiurt.modules.device.service.IDeviceComposeService;
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
 * @Description: device_compose
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="设备管理-设备主数据-设备组件")
@RestController
@RequestMapping("/deviceCompose")
@Slf4j
public class DeviceComposeController extends BaseController<DeviceCompose, IDeviceComposeService> {
	@Autowired
	private IDeviceComposeService deviceComposeService;

	/**
	 * 分页列表查询
	 *
	 * @param deviceCompose
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "设备管理-设备主数据-设备组件-分页列表查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/equipmentData/masterData")
	@ApiOperation(value="设备管理-设备主数据-设备组件-分页列表查询", notes="设备管理-设备主数据-设备组件-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<DeviceCompose>> queryPageList(DeviceCompose deviceCompose,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DeviceCompose> queryWrapper = QueryGenerator.initQueryWrapper(deviceCompose, req.getParameterMap());
		Page<DeviceCompose> page = new Page<DeviceCompose>(pageNo, pageSize);
		IPage<DeviceCompose> pageList = deviceComposeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param deviceCompose
	 * @return
	 */
	@AutoLog(value = "设备管理-设备主数据-设备组件-添加", operateType = 2, operateTypeAlias = "添加", permissionUrl = "/equipmentData/masterData")
	@ApiOperation(value="设备管理-设备主数据-设备组件-添加", notes="设备管理-设备主数据-设备组件-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody DeviceCompose deviceCompose) {
		deviceComposeService.save(deviceCompose);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param deviceCompose
	 * @return
	 */
	@AutoLog(value = "设备管理-设备主数据-设备组件-编辑", operateType = 3, operateTypeAlias = "修改", permissionUrl = "/equipmentData/masterData")
	@ApiOperation(value="设备管理-设备主数据-设备组件-编辑", notes="设备管理-设备主数据-设备组件-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody DeviceCompose deviceCompose) {
		deviceComposeService.updateById(deviceCompose);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "设备管理-设备主数据-设备组件-通过id删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/equipmentData/masterData")
	@ApiOperation(value="设备管理-设备主数据-设备组件-通过id删除", notes="设备管理-设备主数据-设备组件-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		DeviceCompose compose = deviceComposeService.getById(id);
		compose.setDelFlag(CommonConstant.DEL_FLAG_1);
		deviceComposeService.updateById(compose);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "设备管理-设备主数据-设备组件-批量删除", operateType = 4, operateTypeAlias = "删除", permissionUrl = "/equipmentData/masterData")
	@ApiOperation(value="设备管理-设备主数据-设备组件-批量删除", notes="设备管理-设备主数据-设备组件-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Arrays.asList(ids.split(",")).stream().forEach(id -> delete(id));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "设备管理-设备主数据-设备组件-通过id查询", operateType = 1, operateTypeAlias = "查询", permissionUrl = "/equipmentData/masterData")
	@ApiOperation(value="设备管理-设备主数据-设备组件-通过id查询", notes="设备管理-设备主数据-设备组件-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DeviceCompose> queryById(@RequestParam(name="id",required=true) String id) {
		DeviceCompose deviceCompose = deviceComposeService.getById(id);
		if(deviceCompose==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(deviceCompose);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param deviceCompose
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DeviceCompose deviceCompose) {
        return super.exportXls(request, deviceCompose, DeviceCompose.class, "device_compose");
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
        return super.importExcel(request, response, DeviceCompose.class);
    }

}
