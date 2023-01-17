package com.aiurt.boot.check.controller;

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
import com.aiurt.boot.check.entity.FixedAssetsCheckDetail;
import com.aiurt.boot.check.service.IFixedAssetsCheckDetailService;

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
 * @Description: fixed_assets_check_detail
 * @Author: aiurt
 * @Date:   2023-01-17
 * @Version: V1.0
 */
@Api(tags="fixed_assets_check_detail")
@RestController
@RequestMapping("/check/fixedAssetsCheckDetail")
@Slf4j
public class FixedAssetsCheckDetailController extends BaseController<FixedAssetsCheckDetail, IFixedAssetsCheckDetailService> {
	@Autowired
	private IFixedAssetsCheckDetailService fixedAssetsCheckDetailService;
//
//	/**
//	 * 分页列表查询
//	 *
//	 * @param fixedAssetsCheckDetail
//	 * @param pageNo
//	 * @param pageSize
//	 * @param req
//	 * @return
//	 */
//	//@AutoLog(value = "fixed_assets_check_detail-分页列表查询")
//	@ApiOperation(value="fixed_assets_check_detail-分页列表查询", notes="fixed_assets_check_detail-分页列表查询")
//	@GetMapping(value = "/list")
//	public Result<IPage<FixedAssetsCheckDetail>> queryPageList(FixedAssetsCheckDetail fixedAssetsCheckDetail,
//								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
//								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
//								   HttpServletRequest req) {
//		QueryWrapper<FixedAssetsCheckDetail> queryWrapper = QueryGenerator.initQueryWrapper(fixedAssetsCheckDetail, req.getParameterMap());
//		Page<FixedAssetsCheckDetail> page = new Page<FixedAssetsCheckDetail>(pageNo, pageSize);
//		IPage<FixedAssetsCheckDetail> pageList = fixedAssetsCheckDetailService.page(page, queryWrapper);
//		return Result.OK(pageList);
//	}
//
//	/**
//	 *   添加
//	 *
//	 * @param fixedAssetsCheckDetail
//	 * @return
//	 */
//	@AutoLog(value = "fixed_assets_check_detail-添加")
//	@ApiOperation(value="fixed_assets_check_detail-添加", notes="fixed_assets_check_detail-添加")
//	@PostMapping(value = "/add")
//	public Result<String> add(@RequestBody FixedAssetsCheckDetail fixedAssetsCheckDetail) {
//		fixedAssetsCheckDetailService.save(fixedAssetsCheckDetail);
//		return Result.OK("添加成功！");
//	}
//
//	/**
//	 *  编辑
//	 *
//	 * @param fixedAssetsCheckDetail
//	 * @return
//	 */
//	@AutoLog(value = "fixed_assets_check_detail-编辑")
//	@ApiOperation(value="fixed_assets_check_detail-编辑", notes="fixed_assets_check_detail-编辑")
//	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<String> edit(@RequestBody FixedAssetsCheckDetail fixedAssetsCheckDetail) {
//		fixedAssetsCheckDetailService.updateById(fixedAssetsCheckDetail);
//		return Result.OK("编辑成功!");
//	}
//
//	/**
//	 *   通过id删除
//	 *
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "fixed_assets_check_detail-通过id删除")
//	@ApiOperation(value="fixed_assets_check_detail-通过id删除", notes="fixed_assets_check_detail-通过id删除")
//	@DeleteMapping(value = "/delete")
//	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
//		fixedAssetsCheckDetailService.removeById(id);
//		return Result.OK("删除成功!");
//	}
//
//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "fixed_assets_check_detail-批量删除")
//	@ApiOperation(value="fixed_assets_check_detail-批量删除", notes="fixed_assets_check_detail-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.fixedAssetsCheckDetailService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("批量删除成功!");
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	//@AutoLog(value = "fixed_assets_check_detail-通过id查询")
//	@ApiOperation(value="fixed_assets_check_detail-通过id查询", notes="fixed_assets_check_detail-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<FixedAssetsCheckDetail> queryById(@RequestParam(name="id",required=true) String id) {
//		FixedAssetsCheckDetail fixedAssetsCheckDetail = fixedAssetsCheckDetailService.getById(id);
//		if(fixedAssetsCheckDetail==null) {
//			return Result.error("未找到对应数据");
//		}
//		return Result.OK(fixedAssetsCheckDetail);
//	}
//
//    /**
//    * 导出excel
//    *
//    * @param request
//    * @param fixedAssetsCheckDetail
//    */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, FixedAssetsCheckDetail fixedAssetsCheckDetail) {
//        return super.exportXls(request, fixedAssetsCheckDetail, FixedAssetsCheckDetail.class, "fixed_assets_check_detail");
//    }
//
//    /**
//      * 通过excel导入数据
//    *
//    * @param request
//    * @param response
//    * @return
//    */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, FixedAssetsCheckDetail.class);
//    }

}
