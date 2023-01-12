package com.aiurt.boot.check.controller;

import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.aiurt.boot.check.service.IFixedAssetsCheckService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
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
 * @Description: fixed_assets_check
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Api(tags="固定资产盘点任务信息表")
@RestController
@RequestMapping("/check/fixedAssetsCheck")
@Slf4j
public class FixedAssetsCheckController extends BaseController<FixedAssetsCheck, IFixedAssetsCheckService> {
	@Autowired
	private IFixedAssetsCheckService fixedAssetsCheckService;

	/**
	 * 分页列表查询
	 *
	 * @param fixedAssetsCheck
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "fixed_assets_check-分页列表查询")
	@ApiOperation(value="固定资产盘点任务信息表-分页列表查询", notes="固定资产盘点任务信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FixedAssetsCheck>> queryPageList(FixedAssetsCheck fixedAssetsCheck,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FixedAssetsCheck> queryWrapper = QueryGenerator.initQueryWrapper(fixedAssetsCheck, req.getParameterMap());
		Page<FixedAssetsCheck> page = new Page<FixedAssetsCheck>(pageNo, pageSize);
		IPage<FixedAssetsCheck> pageList = fixedAssetsCheckService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param fixedAssetsCheck
	 * @return
	 */
	@AutoLog(value = "固定资产盘点任务信息表-添加")
	@ApiOperation(value="固定资产盘点任务信息表-添加", notes="固定资产盘点任务信息表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FixedAssetsCheck fixedAssetsCheck) {
		fixedAssetsCheckService.save(fixedAssetsCheck);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param fixedAssetsCheck
	 * @return
	 */
	@AutoLog(value = "固定资产盘点任务信息表-编辑")
	@ApiOperation(value="固定资产盘点任务信息表-编辑", notes="固定资产盘点任务信息表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FixedAssetsCheck fixedAssetsCheck) {
		fixedAssetsCheckService.updateById(fixedAssetsCheck);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "固定资产盘点任务信息表-通过id删除")
	@ApiOperation(value="固定资产盘点任务信息表-通过id删除", notes="固定资产盘点任务信息表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		fixedAssetsCheckService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "固定资产盘点任务信息表-批量删除")
	@ApiOperation(value="固定资产盘点任务信息表-批量删除", notes="固定资产盘点任务信息表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.fixedAssetsCheckService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "fixed_assets_check-通过id查询")
	@ApiOperation(value="固定资产盘点任务信息表-通过id查询", notes="固定资产盘点任务信息表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FixedAssetsCheck> queryById(@RequestParam(name="id",required=true) String id) {
		FixedAssetsCheck fixedAssetsCheck = fixedAssetsCheckService.getById(id);
		if(fixedAssetsCheck==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(fixedAssetsCheck);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param fixedAssetsCheck
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FixedAssetsCheck fixedAssetsCheck) {
        return super.exportXls(request, fixedAssetsCheck, FixedAssetsCheck.class, "fixed_assets_check");
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
        return super.importExcel(request, response, FixedAssetsCheck.class);
    }

}
