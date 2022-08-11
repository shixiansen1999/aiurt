package com.aiurt.modules.workarea.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.workarea.entity.WorkArea;
import com.aiurt.modules.workarea.service.IWorkAreaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: work_area
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
@Api(tags="work_area")
@RestController
@RequestMapping("/workarea/workArea")
@Slf4j
public class WorkAreaController extends BaseController<WorkArea, IWorkAreaService> {
	@Autowired
	private IWorkAreaService workAreaService;

	/**
	 * 分页列表查询
	 *
	 * @param workArea
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "work_area-分页列表查询")
	@ApiOperation(value="work_area-分页列表查询", notes="work_area-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<WorkArea>> queryPageList(WorkArea workArea,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<WorkArea> queryWrapper = QueryGenerator.initQueryWrapper(workArea, req.getParameterMap());
		Page<WorkArea> page = new Page<WorkArea>(pageNo, pageSize);
		IPage<WorkArea> pageList = workAreaService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param workArea
	 * @return
	 */
	@AutoLog(value = "work_area-添加")
	@ApiOperation(value="work_area-添加", notes="work_area-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody WorkArea workArea) {
		workAreaService.save(workArea);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param workArea
	 * @return
	 */
	@AutoLog(value = "work_area-编辑")
	@ApiOperation(value="work_area-编辑", notes="work_area-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody WorkArea workArea) {
		workAreaService.updateById(workArea);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "work_area-通过id删除")
	@ApiOperation(value="work_area-通过id删除", notes="work_area-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		workAreaService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "work_area-批量删除")
	@ApiOperation(value="work_area-批量删除", notes="work_area-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.workAreaService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "work_area-通过id查询")
	@ApiOperation(value="work_area-通过id查询", notes="work_area-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<WorkArea> queryById(@RequestParam(name="id",required=true) String id) {
		WorkArea workArea = workAreaService.getById(id);
		if(workArea==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(workArea);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param workArea
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WorkArea workArea) {
        return super.exportXls(request, workArea, WorkArea.class, "work_area");
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
        return super.importExcel(request, response, WorkArea.class);
    }

}
