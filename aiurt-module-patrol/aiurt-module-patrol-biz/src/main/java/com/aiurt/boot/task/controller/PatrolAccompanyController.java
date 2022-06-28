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
import com.aiurt.boot.task.entity.PatrolAccompany;
import com.aiurt.boot.task.service.IPatrolAccompanyService;

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
 * @Description: patrol_accompany
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Api(tags="patrol_accompany")
@RestController
@RequestMapping("/patrolAccompany")
@Slf4j
public class PatrolAccompanyController extends BaseController<PatrolAccompany, IPatrolAccompanyService> {
	@Autowired
	private IPatrolAccompanyService patrolAccompanyService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolAccompany
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_accompany-分页列表查询")
	@ApiOperation(value="patrol_accompany-分页列表查询", notes="patrol_accompany-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolAccompany>> queryPageList(PatrolAccompany patrolAccompany,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolAccompany> queryWrapper = QueryGenerator.initQueryWrapper(patrolAccompany, req.getParameterMap());
		Page<PatrolAccompany> page = new Page<PatrolAccompany>(pageNo, pageSize);
		IPage<PatrolAccompany> pageList = patrolAccompanyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param patrolAccompany
	 * @return
	 */
	@AutoLog(value = "patrol_accompany-添加")
	@ApiOperation(value="patrol_accompany-添加", notes="patrol_accompany-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolAccompany patrolAccompany) {
		patrolAccompanyService.save(patrolAccompany);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param patrolAccompany
	 * @return
	 */
	@AutoLog(value = "patrol_accompany-编辑")
	@ApiOperation(value="patrol_accompany-编辑", notes="patrol_accompany-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolAccompany patrolAccompany) {
		patrolAccompanyService.updateById(patrolAccompany);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "patrol_accompany-通过id删除")
	@ApiOperation(value="patrol_accompany-通过id删除", notes="patrol_accompany-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolAccompanyService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "patrol_accompany-批量删除")
	@ApiOperation(value="patrol_accompany-批量删除", notes="patrol_accompany-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolAccompanyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "patrol_accompany-通过id查询")
	@ApiOperation(value="patrol_accompany-通过id查询", notes="patrol_accompany-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolAccompany> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolAccompany patrolAccompany = patrolAccompanyService.getById(id);
		if(patrolAccompany==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolAccompany);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolAccompany
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolAccompany patrolAccompany) {
        return super.exportXls(request, patrolAccompany, PatrolAccompany.class, "patrol_accompany");
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
        return super.importExcel(request, response, PatrolAccompany.class);
    }

}
