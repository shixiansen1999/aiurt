package com.aiurt.modules.position.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.service.ICsLineService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

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
 * @Description: cs_line
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Api(tags="系统管理-基础数据-位置管理-一级")
@RestController
@RequestMapping("/position/csLine")
@Slf4j
public class CsLineController extends BaseController<CsLine, ICsLineService> {
	@Autowired
	private ICsLineService csLineService;

	/**
	 * 分页列表查询
	 *
	 * @param csLine
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "cs_line-分页列表查询")
	@ApiOperation(value="cs_line-分页列表查询", notes="cs_line-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CsLine>> queryPageList(CsLine csLine,
											   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											   HttpServletRequest req) {
		QueryWrapper<CsLine> queryWrapper = QueryGenerator.initQueryWrapper(csLine, req.getParameterMap());
		Page<CsLine> page = new Page<CsLine>(pageNo, pageSize);
		IPage<CsLine> pageList = csLineService.page(page, queryWrapper.lambda().eq(CsLine::getDelFlag,0));
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param position
	 * @return
	 */
	@AutoLog(value = "cs_line-添加")
	@ApiOperation(value="cs_line-添加", notes="cs_line-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsStationPosition position) {
		CsLine csLine = entityChange(position);
		return csLineService.add(csLine);
	}

	/**
	 *  编辑
	 *
	 * @param position
	 * @return
	 */
	@AutoLog(value = "cs_line-编辑")
	@ApiOperation(value="cs_line-编辑", notes="cs_line-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody CsStationPosition position) {
		CsLine csLine = entityChange(position);
		return csLineService.update(csLine);
	}

	 /**
	  * position转换成csLine
	  * @param position
	  * @return
	  */
    public CsLine entityChange(CsStationPosition position){
		CsLine csLine = new CsLine();
		csLine.setLineType(position.getPositionType());
		csLine.setLineCode(position.getPositionCode());
		csLine.setLineName(position.getPositionName());
		csLine.setSort(position.getSort());
		csLine.setLevel(position.getLevel());
		return csLine;
	}
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "cs_line-通过id删除")
	@ApiOperation(value="cs_line-通过id删除", notes="cs_line-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		csLineService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "cs_line-批量删除")
	@ApiOperation(value="cs_line-批量删除", notes="cs_line-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.csLineService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "cs_line-通过id查询")
	@ApiOperation(value="cs_line-通过id查询", notes="cs_line-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CsLine> queryById(@RequestParam(name="id",required=true) String id) {
		CsLine csLine = csLineService.getById(id);
		if(csLine==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csLine);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param csLine
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CsLine csLine) {
        return super.exportXls(request, csLine, CsLine.class, "cs_line");
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
        return super.importExcel(request, response, CsLine.class);
    }

}
