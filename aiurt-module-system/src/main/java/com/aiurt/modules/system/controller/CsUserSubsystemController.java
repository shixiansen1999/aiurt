package com.aiurt.modules.system.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.system.entity.CsUserSubsystem;
import com.aiurt.modules.system.service.ICsUserSubsystemService;

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
 * @Description: 用户子系统表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Api(tags="用户子系统表")
@RestController
@RequestMapping("/system/csUserSubsystem")
@Slf4j
public class CsUserSubsystemController extends BaseController<CsUserSubsystem, ICsUserSubsystemService> {
	@Autowired
	private ICsUserSubsystemService csUserSubsystemService;

	/**
	 * 分页列表查询
	 *
	 * @param csUserSubsystem
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "用户子系统表-分页列表查询")
	@ApiOperation(value="用户子系统表-分页列表查询", notes="用户子系统表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CsUserSubsystem>> queryPageList(CsUserSubsystem csUserSubsystem,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CsUserSubsystem> queryWrapper = QueryGenerator.initQueryWrapper(csUserSubsystem, req.getParameterMap());
		Page<CsUserSubsystem> page = new Page<CsUserSubsystem>(pageNo, pageSize);
		IPage<CsUserSubsystem> pageList = csUserSubsystemService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param csUserSubsystem
	 * @return
	 */
	@AutoLog(value = "用户子系统表-添加")
	@ApiOperation(value="用户子系统表-添加", notes="用户子系统表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CsUserSubsystem csUserSubsystem) {
		csUserSubsystemService.save(csUserSubsystem);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param csUserSubsystem
	 * @return
	 */
	@AutoLog(value = "用户子系统表-编辑")
	@ApiOperation(value="用户子系统表-编辑", notes="用户子系统表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CsUserSubsystem csUserSubsystem) {
		csUserSubsystemService.updateById(csUserSubsystem);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户子系统表-通过id删除")
	@ApiOperation(value="用户子系统表-通过id删除", notes="用户子系统表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		csUserSubsystemService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户子系统表-批量删除")
	@ApiOperation(value="用户子系统表-批量删除", notes="用户子系统表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.csUserSubsystemService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "用户子系统表-通过id查询")
	@ApiOperation(value="用户子系统表-通过id查询", notes="用户子系统表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CsUserSubsystem> queryById(@RequestParam(name="id",required=true) String id) {
		CsUserSubsystem csUserSubsystem = csUserSubsystemService.getById(id);
		if(csUserSubsystem==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csUserSubsystem);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param csUserSubsystem
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CsUserSubsystem csUserSubsystem) {
        return super.exportXls(request, csUserSubsystem, CsUserSubsystem.class, "用户子系统表");
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
        return super.importExcel(request, response, CsUserSubsystem.class);
    }

}
