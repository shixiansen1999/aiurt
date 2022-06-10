package com.aiurt.boot.modules.device.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.device.entity.Device;
import com.aiurt.boot.modules.device.entity.DeviceSmallType;
import com.aiurt.boot.modules.device.entity.DeviceType;
import com.aiurt.boot.modules.device.service.IDeviceService;
import com.aiurt.boot.modules.device.service.IDeviceSmallTypeService;
import com.aiurt.boot.modules.device.service.IDeviceTypeService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.MaterialBase;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IMaterialBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

 /**
 * @Description: 设备小类
 * @Author: qian
 * @Date:   2021-12-29
 * @Version: V1.0
 */
@Slf4j
@Api(tags="设备小类")
@RestController
@RequestMapping("/Device/deviceSmallType")
public class DeviceSmallTypeController {

	@Resource
	private IDeviceSmallTypeService deviceSmallTypeService;
	@Resource
	private IDeviceService deviceService;
	@Resource
	private IDeviceTypeService deviceTypeService;
	@Resource
	private IMaterialBaseService materialBaseService;

	/**
	  * 分页列表查询
	 * @param deviceSmallType
	 * @param req
	 * @return
	 */
	@AutoLog(value = "设备小类-分页列表查询")
	@ApiOperation(value="设备小类-分页列表查询", notes="设备小类-分页列表查询")
	@GetMapping(value = "/list")
	public Result<List<DeviceSmallType>> queryPageList(DeviceSmallType deviceSmallType, HttpServletRequest req) {
		Result<List<DeviceSmallType>> result = new Result<List<DeviceSmallType>>();
		QueryWrapper<DeviceSmallType> queryWrapper = QueryGenerator.initQueryWrapper(deviceSmallType, req.getParameterMap());
		final List<DeviceSmallType> list = deviceSmallTypeService.list(queryWrapper);
		result.setResult(list);
		result.setSuccess(true);
		return result;
	}

	/**
	  *   添加
	 * @param deviceSmallType
	 * @return
	 */
	@AutoLog(value = "设备小类-添加")
	@ApiOperation(value="设备小类-添加", notes="设备小类-添加")
	@PostMapping(value = "/add")
	public Result<DeviceSmallType> add(@RequestBody DeviceSmallType deviceSmallType) {
		Result<DeviceSmallType> result = new Result<DeviceSmallType>();
		try {
			final DeviceSmallType byname = deviceSmallTypeService.getOne(Wrappers.<DeviceSmallType>query().lambda()
					.eq(DeviceSmallType::getName, deviceSmallType.getName())
					.eq(DeviceSmallType::getDeviceTypeId, deviceSmallType.getDeviceTypeId()).last("limit 1"));
			if (byname != null){
				result.error500("名称不能重复");
				return result;
			}
			final DeviceSmallType bycode = deviceSmallTypeService.getOne(Wrappers.<DeviceSmallType>query().lambda()
					.eq(DeviceSmallType::getCode, deviceSmallType.getCode())
					.eq(DeviceSmallType::getDeviceTypeId, deviceSmallType.getDeviceTypeId()).last("limit 1"));
			if (bycode != null){
				result.error500("数值不能重复");
				return result;
			}
			deviceSmallTypeService.save(deviceSmallType);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param deviceSmallType
	 * @return
	 */
	@AutoLog(value = "设备小类-编辑")
	@ApiOperation(value="设备小类-编辑", notes="设备小类-编辑")
	@PutMapping(value = "/edit")
	public Result<DeviceSmallType> edit(@RequestBody DeviceSmallType deviceSmallType) {
		Result<DeviceSmallType> result = new Result<DeviceSmallType>();
		DeviceSmallType deviceSmallTypeEntity = deviceSmallTypeService.getById(deviceSmallType.getId());
		if(deviceSmallTypeEntity==null) {
			result.onnull("未找到对应实体");
		}else {
			final DeviceSmallType byname = deviceSmallTypeService.getOne(Wrappers.<DeviceSmallType>query().lambda()
					.eq(DeviceSmallType::getName, deviceSmallType.getName())
					.eq(DeviceSmallType::getDeviceTypeId, deviceSmallType.getDeviceTypeId())
					.ne(DeviceSmallType::getId,deviceSmallType.getId()).last("limit 1"));
			if (byname != null){
				result.error500("名称不能重复");
				return result;
			}
			final DeviceSmallType bycode = deviceSmallTypeService.getOne(Wrappers.<DeviceSmallType>query().lambda()
					.eq(DeviceSmallType::getCode, deviceSmallType.getCode())
					.eq(DeviceSmallType::getDeviceTypeId, deviceSmallType.getDeviceTypeId())
					.ne(DeviceSmallType::getId,deviceSmallType.getId()).last("limit 1"));
			if (bycode != null){
				result.error500("数值不能重复");
				return result;
			}

			boolean ok = deviceSmallTypeService.updateById(deviceSmallType);
			if(ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}

	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "设备小类-通过id删除")
	@ApiOperation(value="设备小类-通过id删除", notes="设备小类-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			final DeviceSmallType deviceSmallType = deviceSmallTypeService.getById(id);
			if (deviceSmallType == null) {
				return Result.error("非法参数ID");
			}
			//查询设备和物资基本信息是否关联
			final DeviceType deviceType = deviceTypeService.getById(deviceSmallType.getDeviceTypeId());

			final Device device = deviceService.getOne(Wrappers.<Device>query().lambda()
					.eq(Device::getSmallTypeCode,deviceSmallType.getCode())
					.eq(Device::getSystemCode,deviceType.getSystemCode())
					.eq(Device::getTypeCode,deviceType.getCode()).last("limit 1"));
			if (device != null){
				return Result.error("删除失败!该设备小类已关联设备主数据!");
			}

			final MaterialBase materialBase = materialBaseService.getOne(Wrappers.<MaterialBase>query().lambda()
					.eq(MaterialBase::getSmallTypeCode, deviceSmallType.getCode())
					.eq(MaterialBase::getSystemCode, deviceType.getSystemCode())
					.eq(MaterialBase::getTypeCode, deviceType.getCode()).last("limit 1"));

			if (materialBase != null){
				return Result.error("删除失败!该设备小类已关联物资基础信息!");
			}

			deviceSmallTypeService.removeById(id);
		} catch (Exception e) {
			log.error("删除失败",e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}

	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "设备小类-批量删除")
	@ApiOperation(value="设备小类-批量删除", notes="设备小类-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<DeviceSmallType> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<DeviceSmallType> result = new Result<DeviceSmallType>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.deviceSmallTypeService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "设备小类-通过id查询")
	@ApiOperation(value="设备小类-通过id查询", notes="设备小类-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DeviceSmallType> queryById(@RequestParam(name="id",required=true) String id) {
		Result<DeviceSmallType> result = new Result<DeviceSmallType>();
		DeviceSmallType deviceSmallType = deviceSmallTypeService.getById(id);
		if(deviceSmallType==null) {
			result.onnull("未找到对应实体");
		}else {
			result.setResult(deviceSmallType);
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
      QueryWrapper<DeviceSmallType> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              DeviceSmallType deviceSmallType = JSON.parseObject(deString, DeviceSmallType.class);
              queryWrapper = QueryGenerator.initQueryWrapper(deviceSmallType, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<DeviceSmallType> pageList = deviceSmallTypeService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "设备小类列表");
      mv.addObject(NormalExcelConstants.CLASS, DeviceSmallType.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("设备小类列表数据", "导出人:Jeecg", "导出信息"));
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
              List<DeviceSmallType> listDeviceSmallTypes = ExcelImportUtil.importExcel(file.getInputStream(), DeviceSmallType.class, params);
              deviceSmallTypeService.saveBatch(listDeviceSmallTypes);
              return Result.ok("文件导入成功！数据行数:" + listDeviceSmallTypes.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
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

  @AutoLog("根据设备类型id查询小类")
  @ApiOperation(value="根据设备类型id查询小类", notes="根据设备类型id查询小类")
  @GetMapping("/queryListByTypeId")
  public Result<List<DeviceSmallType>> queryListByTypeId(@RequestParam("id")@NotNull(message = "设备类型id不能为空") Long id){
	  List<DeviceSmallType> list = deviceSmallTypeService.lambdaQuery().eq(DeviceSmallType::getDelFlag, CommonConstant.DEL_FLAG_0).eq(DeviceSmallType::getDeviceTypeId, id).list();
	  return Result.ok(list);
  }



}
