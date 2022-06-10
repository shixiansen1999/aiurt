package com.aiurt.boot.modules.device.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.device.entity.Device;
import com.swsc.copsms.modules.device.entity.DeviceAssembly;
import com.swsc.copsms.modules.device.entity.DeviceType;
import com.swsc.copsms.modules.device.service.IDeviceAssemblyService;
import com.swsc.copsms.modules.device.service.IDeviceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.device.service.IDeviceTypeService;
import com.swsc.copsms.modules.system.service.ISysDictService;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="设备")
@RestController
@RequestMapping("/device/device")
public class DeviceController {
	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private IDeviceTypeService deviceTypeService;

	 @Autowired
	 private IDeviceAssemblyService deviceAssemblyService;

	 @Autowired
	 private ISysDictService sysDictService;


	/**
	  * 分页列表查询
	 * @param device
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "设备-分页列表查询")
	@ApiOperation(value="设备-分页列表查询", notes="设备-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Device>> queryPageList(Device device,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Device>> result = new Result<IPage<Device>>();
		QueryWrapper<Device> queryWrapper = QueryGenerator.initQueryWrapper(device, req.getParameterMap());
		Page<Device> page = new Page<Device>(pageNo, pageSize);
		IPage<Device> pageList = deviceService.page(page, queryWrapper);
		List<Device> list=pageList.getRecords();
		list.forEach(l->{
			QueryWrapper<DeviceType> deviceTypeQueryWrapper=new QueryWrapper<>();
			deviceTypeQueryWrapper.eq("code",l.getTypeCode());
			DeviceType deviceType=deviceTypeService.getOne(deviceTypeQueryWrapper,false);
			l.setTypeName(deviceType.getName());

			l.setSystemName("**系统");

			//设备组件
			QueryWrapper<DeviceAssembly> deviceAssemblyQueryWrapper=new QueryWrapper<>();
			deviceAssemblyQueryWrapper.eq("device_code",l.getCode());
			List<DeviceAssembly> deviceAssemblies=deviceAssemblyService.list(deviceAssemblyQueryWrapper);
			deviceAssemblies.forEach(da->{
				da.setAssemblyName(sysDictService.queryDictTextByKey("component_type", da.getType().toString()));
			});
			l.setDeviceAssembly(deviceAssemblies);
		});
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param device
	 * @return
	 */
	@AutoLog(value = "设备-添加")
	@ApiOperation(value="设备-添加", notes="设备-添加")
	@PostMapping(value = "/add")
	public Result<Device> add(@RequestBody Device device) {
		Result<Device> result = new Result<Device>();
		try {
			deviceService.save(device);
			List<DeviceAssembly> deviceAssembly=device.getDeviceAssembly();
			if(deviceAssembly!=null){
				deviceAssembly.forEach(d->{
					d.setDeviceCode(device.getCode());
					deviceAssemblyService.save(d);
				});
			}
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param device
	 * @return
	 */
	@AutoLog(value = "设备-编辑")
	@ApiOperation(value="设备-编辑", notes="设备-编辑")
	@PutMapping(value = "/edit")
	public Result<Device> edit(@RequestBody Device device) {
		Result<Device> result = new Result<Device>();
		Device deviceEntity = deviceService.getById(device.getId());
		if(deviceEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = deviceService.updateById(device);
			List<DeviceAssembly> deviceAssembly=device.getDeviceAssembly();
			if(deviceAssembly!=null){
				deviceAssembly.forEach(d->{
					DeviceAssembly deviceAssemblyEntity=deviceAssemblyService.getById(d.getId());
					if(deviceAssemblyEntity!=null){
						deviceAssemblyService.updateById(d);
					}else {
						d.setDeviceCode(device.getCode());
						deviceAssemblyService.save(d);
					}
				});
			}
			//TODO 返回false说明什么？
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
	@AutoLog(value = "设备-通过id删除")
	@ApiOperation(value="设备-通过id删除", notes="设备-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			deviceService.removeById(id);
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
	@AutoLog(value = "设备-批量删除")
	@ApiOperation(value="设备-批量删除", notes="设备-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<Device> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Device> result = new Result<Device>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.deviceService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "设备-通过id查询")
	@ApiOperation(value="设备-通过id查询", notes="设备-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Device> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Device> result = new Result<Device>();
		Device device = deviceService.getById(id);
		if(device==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(device);
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
      QueryWrapper<Device> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Device device = JSON.parseObject(deString, Device.class);
              queryWrapper = QueryGenerator.initQueryWrapper(device, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Device> pageList = deviceService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "设备列表");
      mv.addObject(NormalExcelConstants.CLASS, Device.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("设备列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Device> listDevices = ExcelImportUtil.importExcel(file.getInputStream(), Device.class, params);
              deviceService.saveBatch(listDevices);
              return Result.ok("文件导入成功！数据行数:" + listDevices.size());
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

	 /**
	  * @return
	  */
	 @AutoLog(value = "所属系统")
	 @ApiOperation(value="所属系统", notes="所属系统")
	 @GetMapping(value = "/querySys")
	 public Result<List<String>> querySys() {
		 Result<List<String>> result = new Result<List<String>>();
		 List<String> SysList = new ArrayList<String>() {
			 {
				 this.add("传输系统");
				 this.add("公务电话系统");
				 this.add("专用电话系统");
				 this.add("无线通信系统");
				 this.add("视频监视系统");
				 this.add("广播系统");
				 this.add("传输系统");
			 }
		 };
		 result.setSuccess(true);
		 result.setResult(SysList);
		 return result;
	 }

	 /**
	  * @return
	  */
	 @AutoLog(value = "设备类型")
	 @ApiOperation(value="设备类型", notes="设备类型")
	 @GetMapping(value = "/equipmentType")
	 public Result<List<String>> equipmentType() {
		 Result<List<String>> result = new Result<List<String>>();
		 List<String> SysList = new ArrayList<String>() {
			 {
				 this.add("视频");
				 this.add("机柜");
				 this.add("主机");
				 this.add("机房");
				 this.add("。。。");
			 }
		 };
		 result.setSuccess(true);
		 result.setResult(SysList);
		 return result;
	 }

	 /**
	  * @return
	  */
	 @AutoLog(value = "所属路线")
	 @ApiOperation(value="所属路线", notes="所属路线")
	 @GetMapping(value = "/ownedLine")
	 public Result<List<String>> ownedLine() {
		 Result<List<String>> result = new Result<List<String>>();
		 List<String> SysList = new ArrayList<String>() {
			 {
				 this.add("1号线");
				 this.add("2号线");
				 this.add("3号线");
				 this.add("4号线");
			 }
		 };
		 result.setSuccess(true);
		 result.setResult(SysList);
		 return result;
	 }

	 /**
	  * @return
	  */
	 @AutoLog(value = "所属站点")
	 @ApiOperation(value="所属站点", notes="所属站点")
	 @GetMapping(value = "/ownedSite")
	 public Result<List<String>> ownedSite() {
		 Result<List<String>> result = new Result<List<String>>();
		 List<String> SysList = new ArrayList<String>() {
			 {
				 this.add("1号线");
				 this.add("2号线");
				 this.add("3号线");
				 this.add("4号线");
			 }
		 };
		 result.setSuccess(true);
		 result.setResult(SysList);
		 return result;
	 }

	 /**
	  * @return
	  */
	 @AutoLog(value = "所属班次")
	 @ApiOperation(value="所属班次", notes="所属班次")
	 @GetMapping(value = "/subwayFrequency")
	 public Result<List<String>> subwayFrequency() {
		 Result<List<String>> result = new Result<List<String>>();
		 List<String> SysList = new ArrayList<String>() {
			 {
				 this.add("1号线");
				 this.add("2号线");
				 this.add("3号线");
				 this.add("4号线");
			 }
		 };
		 result.setSuccess(true);
		 result.setResult(SysList);
		 return result;
	 }

}
