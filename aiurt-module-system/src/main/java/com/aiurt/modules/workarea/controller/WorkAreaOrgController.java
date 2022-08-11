package com.aiurt.modules.workarea.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.workarea.entity.WorkAreaOrg;
import com.aiurt.modules.workarea.service.IWorkAreaOrgService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: work_area_org
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
@Api(tags="work_area_org")
@RestController
@RequestMapping("/workarea/workAreaOrg")
@Slf4j
public class WorkAreaOrgController extends BaseController<WorkAreaOrg, IWorkAreaOrgService> {
	@Autowired
	private IWorkAreaOrgService workAreaOrgService;

	/**
	 * 分页列表查询
	 *
	 * @param workAreaOrg
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "work_area_org-分页列表查询")
	@ApiOperation(value="work_area_org-分页列表查询", notes="work_area_org-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WorkAreaOrg>> queryPageList(WorkAreaOrg workAreaOrg,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<WorkAreaOrg> queryWrapper = QueryGenerator.initQueryWrapper(workAreaOrg, req.getParameterMap());
		Page<WorkAreaOrg> page = new Page<WorkAreaOrg>(pageNo, pageSize);
		IPage<WorkAreaOrg> pageList = workAreaOrgService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param workAreaOrg
	 * @return
	 */
	@AutoLog(value = "work_area_org-添加")
	@ApiOperation(value="work_area_org-添加", notes="work_area_org-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WorkAreaOrg workAreaOrg) {
		workAreaOrgService.save(workAreaOrg);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param workAreaOrg
	 * @return
	 */
	@AutoLog(value = "work_area_org-编辑")
	@ApiOperation(value="work_area_org-编辑", notes="work_area_org-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WorkAreaOrg workAreaOrg) {
		workAreaOrgService.updateById(workAreaOrg);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "work_area_org-通过id删除")
	@ApiOperation(value="work_area_org-通过id删除", notes="work_area_org-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		workAreaOrgService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "work_area_org-批量删除")
	@ApiOperation(value="work_area_org-批量删除", notes="work_area_org-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.workAreaOrgService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "work_area_org-通过id查询")
	@ApiOperation(value="work_area_org-通过id查询", notes="work_area_org-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WorkAreaOrg> queryById(@RequestParam(name="id",required=true) String id) {
		WorkAreaOrg workAreaOrg = workAreaOrgService.getById(id);
		if(workAreaOrg==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(workAreaOrg);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param workAreaOrg
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WorkAreaOrg workAreaOrg) {
        return super.exportXls(request, workAreaOrg, WorkAreaOrg.class, "work_area_org");
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
        return super.importExcel(request, response, WorkAreaOrg.class);
    }

}
