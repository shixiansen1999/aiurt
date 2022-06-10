package com.aiurt.boot.modules.manage.controller;

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
import com.swsc.copsms.modules.manage.entity.SubsystemUser;
import com.swsc.copsms.modules.manage.service.ISubsystemUserService;
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
 * @Description: cs_subsystem_user
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="子系统-技术人员")
@RestController
@RequestMapping("/manage/subsystemUser")
public class SubsystemUserController {
	@Autowired
	private ISubsystemUserService subsystemUserService;

	/**
	  * 分页列表查询
	 * @param subsystemUser
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "cs_subsystem_user-分页列表查询")
	@ApiOperation(value="cs_subsystem_user-分页列表查询", notes="cs_subsystem_user-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SubsystemUser>> queryPageList(SubsystemUser subsystemUser,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<SubsystemUser>> result = new Result<IPage<SubsystemUser>>();
		QueryWrapper<SubsystemUser> queryWrapper = QueryGenerator.initQueryWrapper(subsystemUser, req.getParameterMap());
		Page<SubsystemUser> page = new Page<SubsystemUser>(pageNo, pageSize);
		IPage<SubsystemUser> pageList = subsystemUserService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param subsystemUser
	 * @return
	 */
	@AutoLog(value = "cs_subsystem_user-添加")
	@ApiOperation(value="cs_subsystem_user-添加", notes="cs_subsystem_user-添加")
	@PostMapping(value = "/add")
	public Result<SubsystemUser> add(@RequestBody SubsystemUser subsystemUser) {
		Result<SubsystemUser> result = new Result<SubsystemUser>();
		try {
			subsystemUserService.save(subsystemUser);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param subsystemUser
	 * @return
	 */
	@AutoLog(value = "cs_subsystem_user-编辑")
	@ApiOperation(value="cs_subsystem_user-编辑", notes="cs_subsystem_user-编辑")
	@PutMapping(value = "/edit")
	public Result<SubsystemUser> edit(@RequestBody SubsystemUser subsystemUser) {
		Result<SubsystemUser> result = new Result<SubsystemUser>();
		SubsystemUser subsystemUserEntity = subsystemUserService.getById(subsystemUser.getId());
		if(subsystemUserEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = subsystemUserService.updateById(subsystemUser);
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
	@AutoLog(value = "cs_subsystem_user-通过id删除")
	@ApiOperation(value="cs_subsystem_user-通过id删除", notes="cs_subsystem_user-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			subsystemUserService.removeById(id);
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
	@AutoLog(value = "cs_subsystem_user-批量删除")
	@ApiOperation(value="cs_subsystem_user-批量删除", notes="cs_subsystem_user-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<SubsystemUser> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SubsystemUser> result = new Result<SubsystemUser>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.subsystemUserService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "cs_subsystem_user-通过id查询")
	@ApiOperation(value="cs_subsystem_user-通过id查询", notes="cs_subsystem_user-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SubsystemUser> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SubsystemUser> result = new Result<SubsystemUser>();
		SubsystemUser subsystemUser = subsystemUserService.getById(id);
		if(subsystemUser==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(subsystemUser);
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
      QueryWrapper<SubsystemUser> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              SubsystemUser subsystemUser = JSON.parseObject(deString, SubsystemUser.class);
              queryWrapper = QueryGenerator.initQueryWrapper(subsystemUser, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<SubsystemUser> pageList = subsystemUserService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "cs_subsystem_user列表");
      mv.addObject(NormalExcelConstants.CLASS, SubsystemUser.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("cs_subsystem_user列表数据", "导出人:Jeecg", "导出信息"));
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
              List<SubsystemUser> listSubsystemUsers = ExcelImportUtil.importExcel(file.getInputStream(), SubsystemUser.class, params);
              subsystemUserService.saveBatch(listSubsystemUsers);
              return Result.ok("文件导入成功！数据行数:" + listSubsystemUsers.size());
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
