package com.aiurt.boot.modules.manage.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.manage.entity.SituationUser;
import com.aiurt.boot.modules.manage.service.ISituationUserService;
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
 * @Description: cs_situation_user
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="发布对象")
@RestController
@RequestMapping("/manage/situationUser")
public class SituationUserController {
	@Autowired
	private ISituationUserService situationUserService;

	/**
	  * 分页列表查询
	 * @param situationUser
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "发布对象-分页列表查询")
	@ApiOperation(value="发布对象-分页列表查询", notes="发布对象-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SituationUser>> queryPageList(SituationUser situationUser,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<SituationUser>> result = new Result<IPage<SituationUser>>();
		QueryWrapper<SituationUser> queryWrapper = QueryGenerator.initQueryWrapper(situationUser, req.getParameterMap());
		Page<SituationUser> page = new Page<SituationUser>(pageNo, pageSize);
		IPage<SituationUser> pageList = situationUserService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param situationUser
	 * @return
	 */
	@AutoLog(value = "发布对象-添加")
	@ApiOperation(value="发布对象-添加", notes="发布对象-添加")
	@PostMapping(value = "/add")
	public Result<SituationUser> add(@RequestBody SituationUser situationUser) {
		Result<SituationUser> result = new Result<SituationUser>();
		try {
			situationUserService.save(situationUser);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  编辑
	 * @param situationUser
	 * @return
	 */
	@AutoLog(value = "发布对象-编辑")
	@ApiOperation(value="发布对象-编辑", notes="发布对象-编辑")
	@PutMapping(value = "/edit")
	public Result<SituationUser> edit(@RequestBody SituationUser situationUser) {
		Result<SituationUser> result = new Result<SituationUser>();
		SituationUser situationUserEntity = situationUserService.getById(situationUser.getId());
		if(situationUserEntity==null) {
			result.onnull("未找到对应实体");
		}else {
			boolean ok = situationUserService.updateById(situationUser);

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
	@AutoLog(value = "发布对象-通过id删除")
	@ApiOperation(value="发布对象-通过id删除", notes="发布对象-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			situationUserService.removeById(id);
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
	@AutoLog(value = "发布对象-批量删除")
	@ApiOperation(value="发布对象-批量删除", notes="发布对象-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<SituationUser> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SituationUser> result = new Result<SituationUser>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.situationUserService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "发布对象-通过id查询")
	@ApiOperation(value="发布对象-通过id查询", notes="发布对象-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SituationUser> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SituationUser> result = new Result<SituationUser>();
		SituationUser situationUser = situationUserService.getById(id);
		if(situationUser==null) {
			result.onnull("未找到对应实体");
		}else {
			result.setResult(situationUser);
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
      QueryWrapper<SituationUser> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              SituationUser situationUser = JSON.parseObject(deString, SituationUser.class);
              queryWrapper = QueryGenerator.initQueryWrapper(situationUser, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<SituationUser> pageList = situationUserService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "cs_situation_user列表");
      mv.addObject(NormalExcelConstants.CLASS, SituationUser.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("cs_situation_user列表数据", "导出人:Jeecg", "导出信息"));
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
              List<SituationUser> listSituationUsers = ExcelImportUtil.importExcel(file.getInputStream(), SituationUser.class, params);
              situationUserService.saveBatch(listSituationUsers);
              return Result.ok("文件导入成功！数据行数:" + listSituationUsers.size());
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
