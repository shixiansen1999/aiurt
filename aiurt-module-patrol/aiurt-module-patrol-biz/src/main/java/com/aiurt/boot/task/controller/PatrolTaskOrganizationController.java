package com.aiurt.boot.task.controller;

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
import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.aiurt.boot.task.service.IPatrolTaskOrganizationService;

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
 * @Description: patrol_task_organization
 * @Author: aiurt
 * @Date:   2022-06-27
 * @Version: V1.0
 */
@Api(tags="patrol_task_organization")
@RestController
@RequestMapping("/patrolTaskOrganization")
@Slf4j
public class PatrolTaskOrganizationController extends BaseController<PatrolTaskOrganization, IPatrolTaskOrganizationService> {
	@Autowired
	private IPatrolTaskOrganizationService patrolTaskOrganizationService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolTaskOrganization
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	/*//@AutoLog(value = "patrol_task_organization-分页列表查询")
	@ApiOperation(value="patrol_task_organization-分页列表查询", notes="patrol_task_organization-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolTaskOrganization>> queryPageList(PatrolTaskOrganization patrolTaskOrganization,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolTaskOrganization> queryWrapper = QueryGenerator.initQueryWrapper(patrolTaskOrganization, req.getParameterMap());
		Page<PatrolTaskOrganization> page = new Page<PatrolTaskOrganization>(pageNo, pageSize);
		IPage<PatrolTaskOrganization> pageList = patrolTaskOrganizationService.page(page, queryWrapper);
		return Result.OK(pageList);
	}*/

	/**
	 *   添加
	 *
	 * @param patrolTaskOrganization
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_organization-添加")
	@ApiOperation(value="patrol_task_organization-添加", notes="patrol_task_organization-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolTaskOrganization patrolTaskOrganization) {
		patrolTaskOrganizationService.save(patrolTaskOrganization);
		return Result.OK("添加成功！");
	}*/

	/**
	 *  编辑
	 *
	 * @param patrolTaskOrganization
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_organization-编辑")
	@ApiOperation(value="patrol_task_organization-编辑", notes="patrol_task_organization-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolTaskOrganization patrolTaskOrganization) {
		patrolTaskOrganizationService.updateById(patrolTaskOrganization);
		return Result.OK("编辑成功!");
	}*/

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_organization-通过id删除")
	@ApiOperation(value="patrol_task_organization-通过id删除", notes="patrol_task_organization-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolTaskOrganizationService.removeById(id);
		return Result.OK("删除成功!");
	}*/

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "patrol_task_organization-批量删除")
	@ApiOperation(value="patrol_task_organization-批量删除", notes="patrol_task_organization-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolTaskOrganizationService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	/*//@AutoLog(value = "patrol_task_organization-通过id查询")
	@ApiOperation(value="patrol_task_organization-通过id查询", notes="patrol_task_organization-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolTaskOrganization> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolTaskOrganization patrolTaskOrganization = patrolTaskOrganizationService.getById(id);
		if(patrolTaskOrganization==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolTaskOrganization);
	}*/

    /**
    * 导出excel
    *
    * @param request
    * @param patrolTaskOrganization
    */
   /* @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolTaskOrganization patrolTaskOrganization) {
        return super.exportXls(request, patrolTaskOrganization, PatrolTaskOrganization.class, "patrol_task_organization");
    }
*/
    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    /*@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PatrolTaskOrganization.class);
    }*/

}
