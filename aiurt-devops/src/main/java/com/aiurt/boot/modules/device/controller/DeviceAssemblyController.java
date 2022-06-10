package com.aiurt.boot.modules.device.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.device.entity.DeviceAssembly;
import com.swsc.copsms.modules.device.service.IDeviceAssemblyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * @Description: 设备组件
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="设备组件")
@RestController
@RequestMapping("/device/deviceAssembly")
public class DeviceAssemblyController {
	@Autowired
	private IDeviceAssemblyService deviceAssemblyService;

	/**
	  * 组件类型(下拉)
	 * @return
	 */
	@AutoLog(value = "组件类型(下拉)")
	@ApiOperation(value="组件类型(下拉)", notes="组件类型(下拉)")
	@GetMapping(value = "/assemblyNameList")
	public Result<HashMap<Integer,String>> assemblyNameList() {
		//TODO 改用 /sys/dict/getDictItems/{dictCode}
		Result<HashMap<Integer,String>> result = new Result<HashMap<Integer,String>>();
		HashMap<Integer,String> hashMap=new HashMap<>();
		hashMap.put(1,"可用性组件");
		hashMap.put(2,"共享性组件");
		hashMap.put(3,"持久性组件");
		result.setSuccess(true);
		result.setResult(hashMap);
		return result;
	}

	 /**
	  * 分页列表查询
	  * @return
	  */
	 @AutoLog(value = "设备组件-分页列表查询")
	 @ApiOperation(value="设备组件-分页列表查询", notes="设备组件-分页列表查询")
	 @GetMapping(value = "/list")
	 public Result<IPage<DeviceAssembly>> queryPageList(DeviceAssembly deviceAssembly,
														@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														HttpServletRequest req) {
		 Result<IPage<DeviceAssembly>> result = new Result<IPage<DeviceAssembly>>();
		 QueryWrapper<DeviceAssembly> queryWrapper = QueryGenerator.initQueryWrapper(deviceAssembly, req.getParameterMap());
		 Page<DeviceAssembly> page = new Page<DeviceAssembly>(pageNo, pageSize);
		 IPage<DeviceAssembly> pageList = deviceAssemblyService.page(page, queryWrapper);
		 result.setSuccess(true);
		 result.setResult(pageList);
		 return result;
	 }

	/**
	  *   添加
	 * @param deviceAssembly
	 * @return
	 */
	@AutoLog(value = "设备组件-添加")
	@ApiOperation(value="设备组件-添加", notes="设备组件-添加")
	@PostMapping(value = "/add")
	public Result<DeviceAssembly> add(@RequestBody DeviceAssembly deviceAssembly) {
		Result<DeviceAssembly> result = new Result<DeviceAssembly>();
		try {
			deviceAssemblyService.save(deviceAssembly);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param deviceAssembly
	 * @return
	 */
	@AutoLog(value = "设备组件-编辑")
	@ApiOperation(value="设备组件-编辑", notes="设备组件-编辑")
	@PutMapping(value = "/edit")
	public Result<DeviceAssembly> edit(@RequestBody DeviceAssembly deviceAssembly) {
		Result<DeviceAssembly> result = new Result<DeviceAssembly>();
		DeviceAssembly deviceAssemblyEntity = deviceAssemblyService.getById(deviceAssembly.getId());
		if(deviceAssemblyEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = deviceAssemblyService.updateById(deviceAssembly);
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
	@AutoLog(value = "设备组件-通过id删除")
	@ApiOperation(value="设备组件-通过id删除", notes="设备组件-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			deviceAssemblyService.removeById(id);
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
	@AutoLog(value = "设备组件-批量删除")
	@ApiOperation(value="设备组件-批量删除", notes="设备组件-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<DeviceAssembly> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<DeviceAssembly> result = new Result<DeviceAssembly>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.deviceAssemblyService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "设备组件-通过id查询")
	@ApiOperation(value="设备组件-通过id查询", notes="设备组件-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DeviceAssembly> queryById(@RequestParam(name="id",required=true) String id) {
		Result<DeviceAssembly> result = new Result<DeviceAssembly>();
		DeviceAssembly deviceAssembly = deviceAssemblyService.getById(id);
		if(deviceAssembly==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(deviceAssembly);
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
      QueryWrapper<DeviceAssembly> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              DeviceAssembly deviceAssembly = JSON.parseObject(deString, DeviceAssembly.class);
              queryWrapper = QueryGenerator.initQueryWrapper(deviceAssembly, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<DeviceAssembly> pageList = deviceAssemblyService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "设备组件列表");
      mv.addObject(NormalExcelConstants.CLASS, DeviceAssembly.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("设备组件列表数据", "导出人:Jeecg", "导出信息"));
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
              List<DeviceAssembly> listDeviceAssemblys = ExcelImportUtil.importExcel(file.getInputStream(), DeviceAssembly.class, params);
              deviceAssemblyService.saveBatch(listDeviceAssemblys);
              return Result.ok("文件导入成功！数据行数:" + listDeviceAssemblys.size());
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

}
