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

import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.device.service.IDeviceTypeService;
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
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="device_type")
@RestController
@RequestMapping("/device/deviceType")
@Slf4j
public class DeviceTypeController extends BaseController<DeviceType, IDeviceTypeService> {
	@Autowired
	private IDeviceTypeService deviceTypeService;

	/**
	 * 分页列表查询
	 *
	 * @param deviceType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "device_type-分页列表查询")
	@ApiOperation(value="device_type-分页列表查询", notes="device_type-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<DeviceType>> queryPageList(DeviceType deviceType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DeviceType> queryWrapper = QueryGenerator.initQueryWrapper(deviceType, req.getParameterMap());
		Page<DeviceType> page = new Page<DeviceType>(pageNo, pageSize);
		IPage<DeviceType> pageList = deviceTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param deviceType
	 * @return
	 */
	@AutoLog(value = "device_type-添加")
	@ApiOperation(value="device_type-添加", notes="device_type-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody DeviceType deviceType) {
		deviceTypeService.save(deviceType);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param deviceType
	 * @return
	 */
	@AutoLog(value = "device_type-编辑")
	@ApiOperation(value="device_type-编辑", notes="device_type-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody DeviceType deviceType) {
		deviceTypeService.updateById(deviceType);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "device_type-通过id删除")
	@ApiOperation(value="device_type-通过id删除", notes="device_type-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		deviceTypeService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "device_type-批量删除")
	@ApiOperation(value="device_type-批量删除", notes="device_type-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.deviceTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "device_type-通过id查询")
	@ApiOperation(value="device_type-通过id查询", notes="device_type-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DeviceType> queryById(@RequestParam(name="id",required=true) String id) {
		DeviceType deviceType = deviceTypeService.getById(id);
		if(deviceType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(deviceType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param deviceType
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DeviceType deviceType) {
        return super.exportXls(request, deviceType, DeviceType.class, "device_type");
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
        return super.importExcel(request, response, DeviceType.class);
    }

}
