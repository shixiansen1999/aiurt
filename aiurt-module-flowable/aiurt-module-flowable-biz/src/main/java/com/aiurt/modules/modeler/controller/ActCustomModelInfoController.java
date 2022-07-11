package com.aiurt.modules.modeler.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
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
 * @Description: flowable流程模板定义信息
 * @Author: aiurt
 * @Date:   2022-07-08
 * @Version: V1.0
 */
@Api(tags="flowable流程模板定义信息")
@RestController
@RequestMapping("/modeler/actCustomModelInfo")
@Slf4j
public class ActCustomModelInfoController extends BaseController<ActCustomModelInfo, IActCustomModelInfoService> {
	@Autowired
	private IActCustomModelInfoService actCustomModelInfoService;

	/**
	 * 分页列表查询
	 *
	 * @param actCustomModelInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询流程模板")
	@ApiOperation(value="flowable流程模板定义信息-分页列表查询", notes="flowable流程模板定义信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ActCustomModelInfo>> queryPageList(ActCustomModelInfo actCustomModelInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ActCustomModelInfo> queryWrapper = QueryGenerator.initQueryWrapper(actCustomModelInfo, req.getParameterMap());
		Page<ActCustomModelInfo> page = new Page<ActCustomModelInfo>(pageNo, pageSize);
		IPage<ActCustomModelInfo> pageList = actCustomModelInfoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param actCustomModelInfo
	 * @return
	 */
	@AutoLog(value = "flowable流程模板定义信息-添加")
	@ApiOperation(value="flowable流程模板定义信息-添加", notes="flowable流程模板定义信息-添加")
	@PostMapping(value = "/add")
	public Result<ActCustomModelInfo> add(@RequestBody ActCustomModelInfo actCustomModelInfo) {
		ActCustomModelInfo a = actCustomModelInfoService.add(actCustomModelInfo);
		return Result.OK("添加成功", a);
	}

	/**
	 *  编辑
	 *
	 * @param actCustomModelInfo
	 * @return
	 */
	@AutoLog(value = "flowable流程模板定义信息-编辑")
	@ApiOperation(value="flowable流程模板定义信息-编辑", notes="flowable流程模板定义信息-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ActCustomModelInfo actCustomModelInfo) {
		actCustomModelInfoService.updateById(actCustomModelInfo);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "flowable流程模板定义信息-通过id删除")
	@ApiOperation(value="flowable流程模板定义信息-通过id删除", notes="flowable流程模板定义信息-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		actCustomModelInfoService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "flowable流程模板定义信息-批量删除")
	@ApiOperation(value="flowable流程模板定义信息-批量删除", notes="flowable流程模板定义信息-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.actCustomModelInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "flowable流程模板定义信息-通过id查询")
	@ApiOperation(value="flowable流程模板定义信息-通过id查询", notes="flowable流程模板定义信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ActCustomModelInfo> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomModelInfo actCustomModelInfo = actCustomModelInfoService.getById(id);
		if(actCustomModelInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(actCustomModelInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param actCustomModelInfo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ActCustomModelInfo actCustomModelInfo) {
        return super.exportXls(request, actCustomModelInfo, ActCustomModelInfo.class, "flowable流程模板定义信息");
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
        return super.importExcel(request, response, ActCustomModelInfo.class);
    }

}
