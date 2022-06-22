package com.aiurt.boot.standard.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.entity.patrol.standard.PatrolStandardItems;
import com.aiurt.boot.standard.service.IPatrolStandardItemsService;

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
 * @Description: patrol_standard_items
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="patrol_standard_items")
@RestController
@RequestMapping("/patrolStandardItems")
@Slf4j
public class PatrolStandardItemsController extends BaseController<PatrolStandardItems, IPatrolStandardItemsService> {
	@Autowired
	private IPatrolStandardItemsService patrolStandardItemsService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolStandardItems
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_standard_items-分页列表查询")
	@ApiOperation(value="patrol_standard_items-分页列表查询", notes="patrol_standard_items-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolStandardItems>> queryPageList(PatrolStandardItems patrolStandardItems,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolStandardItems> queryWrapper = QueryGenerator.initQueryWrapper(patrolStandardItems, req.getParameterMap());
		Page<PatrolStandardItems> page = new Page<PatrolStandardItems>(pageNo, pageSize);
		IPage<PatrolStandardItems> pageList = patrolStandardItemsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param patrolStandardItems
	 * @return
	 */
	@AutoLog(value = "patrol_standard_items-添加")
	@ApiOperation(value="patrol_standard_items-添加", notes="patrol_standard_items-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolStandardItems patrolStandardItems) {
		patrolStandardItemsService.save(patrolStandardItems);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param patrolStandardItems
	 * @return
	 */
	@AutoLog(value = "patrol_standard_items-编辑")
	@ApiOperation(value="patrol_standard_items-编辑", notes="patrol_standard_items-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolStandardItems patrolStandardItems) {
		patrolStandardItemsService.updateById(patrolStandardItems);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "patrol_standard_items-通过id删除")
	@ApiOperation(value="patrol_standard_items-通过id删除", notes="patrol_standard_items-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolStandardItemsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "patrol_standard_items-批量删除")
	@ApiOperation(value="patrol_standard_items-批量删除", notes="patrol_standard_items-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolStandardItemsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "patrol_standard_items-通过id查询")
	@ApiOperation(value="patrol_standard_items-通过id查询", notes="patrol_standard_items-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolStandardItems> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolStandardItems patrolStandardItems = patrolStandardItemsService.getById(id);
		if(patrolStandardItems==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolStandardItems);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolStandardItems
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolStandardItems patrolStandardItems) {
        return super.exportXls(request, patrolStandardItems, PatrolStandardItems.class, "patrol_standard_items");
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
        return super.importExcel(request, response, PatrolStandardItems.class);
    }

}
