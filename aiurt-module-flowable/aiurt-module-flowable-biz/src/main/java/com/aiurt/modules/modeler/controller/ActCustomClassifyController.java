package com.aiurt.modules.modeler.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.modeler.entity.ActCustomClassify;
import com.aiurt.modules.modeler.service.IActCustomClassifyService;
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
 * @Description: 流程分类
 * @Author: aiurt
 * @Date:   2022-07-08
 * @Version: V1.0
 */
@Api(tags="流程分类")
@RestController
@RequestMapping("/modeler/actCustomClassify")
@Slf4j
public class ActCustomClassifyController extends BaseController<ActCustomClassify, IActCustomClassifyService> {
	@Autowired
	private IActCustomClassifyService actCustomClassifyService;

	/**
	 * 分页列表查询
	 *
	 * @param actCustomClassify
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "流程分类-分页列表查询")
	@ApiOperation(value="流程分类-分页列表查询", notes="流程分类-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ActCustomClassify>> queryPageList(ActCustomClassify actCustomClassify,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ActCustomClassify> queryWrapper = QueryGenerator.initQueryWrapper(actCustomClassify, req.getParameterMap());
		Page<ActCustomClassify> page = new Page<ActCustomClassify>(pageNo, pageSize);
		IPage<ActCustomClassify> pageList = actCustomClassifyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param actCustomClassify
	 * @return
	 */
	@AutoLog(value = "流程分类-添加")
	@ApiOperation(value="流程分类-添加", notes="流程分类-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ActCustomClassify actCustomClassify) {
		actCustomClassifyService.save(actCustomClassify);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param actCustomClassify
	 * @return
	 */
	@AutoLog(value = "流程分类-编辑")
	@ApiOperation(value="流程分类-编辑", notes="流程分类-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ActCustomClassify actCustomClassify) {
		actCustomClassifyService.updateById(actCustomClassify);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "流程分类-通过id删除")
	@ApiOperation(value="流程分类-通过id删除", notes="流程分类-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		actCustomClassifyService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "流程分类-批量删除")
	@ApiOperation(value="流程分类-批量删除", notes="流程分类-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.actCustomClassifyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "流程分类-通过id查询")
	@ApiOperation(value="流程分类-通过id查询", notes="流程分类-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ActCustomClassify> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomClassify actCustomClassify = actCustomClassifyService.getById(id);
		if(actCustomClassify==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(actCustomClassify);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param actCustomClassify
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ActCustomClassify actCustomClassify) {
        return super.exportXls(request, actCustomClassify, ActCustomClassify.class, "流程分类");
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
        return super.importExcel(request, response, ActCustomClassify.class);
    }

}
