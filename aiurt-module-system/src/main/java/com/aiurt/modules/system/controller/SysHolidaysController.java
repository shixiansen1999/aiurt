package com.aiurt.modules.system.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.modules.system.entity.SysHolidays;
import com.aiurt.modules.system.service.ISysHolidaysService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

 /**
 * @Description: sys_holidays
 * @Author: aiurt
 * @Date:   2023-03-16
 * @Version: V1.0
 */
@Api(tags="sys_holidays")
@RestController
@RequestMapping("/holidays/sysHolidays")
@Slf4j
public class SysHolidaysController extends BaseController<SysHolidays, ISysHolidaysService> {
	@Autowired
	private ISysHolidaysService sysHolidaysService;

	/**
	 * 分页列表查询
	 *
	 * @param sysHolidays
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "sys_holidays-分页列表查询")
	@ApiOperation(value="sys_holidays-分页列表查询", notes="sys_holidays-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SysHolidays>> queryPageList(SysHolidays sysHolidays,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		//QueryWrapper<SysHolidays> queryWrapper = QueryGenerator.initQueryWrapper(sysHolidays, req.getParameterMap());
		LambdaQueryWrapper<SysHolidays> wrapper = new LambdaQueryWrapper<>();
		if (StrUtil.isNotEmpty(sysHolidays.getDate())) {
			wrapper.like(SysHolidays::getDate, sysHolidays.getDate());
		}
		if (StrUtil.isNotEmpty(sysHolidays.getName())) {
			wrapper.like(SysHolidays::getName, sysHolidays.getName());
		}
		Page<SysHolidays> page = new Page<SysHolidays>(pageNo, pageSize);
		IPage<SysHolidays> pageList = sysHolidaysService.page(page, wrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param sysHolidays
	 * @return
	 */
	@AutoLog(value = "sys_holidays-添加")
	@ApiOperation(value="sys_holidays-添加", notes="sys_holidays-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SysHolidays sysHolidays) {
		sysHolidaysService.save(sysHolidays);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sysHolidays
	 * @return
	 */
	@AutoLog(value = "sys_holidays-编辑")
	@ApiOperation(value="sys_holidays-编辑", notes="sys_holidays-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SysHolidays sysHolidays) {
		sysHolidaysService.updateById(sysHolidays);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "sys_holidays-通过id删除")
	@ApiOperation(value="sys_holidays-通过id删除", notes="sys_holidays-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sysHolidaysService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "sys_holidays-批量删除")
	@ApiOperation(value="sys_holidays-批量删除", notes="sys_holidays-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysHolidaysService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "sys_holidays-通过id查询")
	@ApiOperation(value="sys_holidays-通过id查询", notes="sys_holidays-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SysHolidays> queryById(@RequestParam(name="id",required=true) String id) {
		SysHolidays sysHolidays = sysHolidaysService.getById(id);
		if(sysHolidays==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sysHolidays);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sysHolidays
    */
	@ApiOperation(value="sys_holidays-导出excel", notes="sys_holidays-导出excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysHolidays sysHolidays) {
        return super.exportXls(request, sysHolidays, SysHolidays.class, "节假日表","date,name");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
	@ApiOperation(value="sys_holidays-通过excel导入数据", notes="sys_holidays-通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return sysHolidaysService.importExcel(request, response, SysHolidays.class);
    }

	 /**
	  * 节假日表模板下载
	  *
	  */
	 @AutoLog(value = "节假日表模板下载", operateType =  4, operateTypeAlias = "导出excel", module = ModuleType.INSPECTION)
	 @ApiOperation(value="节假日表模板下载", notes="节假日表模板下载")
	 @RequestMapping(value = "/exportTemplateXls",method = RequestMethod.GET)
	 public void exportTemplateXl(HttpServletResponse response, HttpServletRequest request) throws IOException {
		 XlsUtil.getExcel(response, "templates/holidays.xlsx", "节假日表导入模板.xlsx");
	 }
}
