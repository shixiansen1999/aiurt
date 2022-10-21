package com.aiurt.modules.faultenclosure.controller;

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
import com.aiurt.modules.faultenclosure.entity.FaultEnclosure;
import com.aiurt.modules.faultenclosure.service.IFaultEnclosureService;

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
 * @Description: fault_enclosure
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Api(tags="故障-附件表")
@RestController
@RequestMapping("/faultenclosure/faultEnclosure")
@Slf4j
public class FaultEnclosureController extends BaseController<FaultEnclosure, IFaultEnclosureService> {
	@Autowired
	private IFaultEnclosureService faultEnclosureService;

	/**
	 * 分页列表查询
	 *
	 * @param faultEnclosure
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "fault_enclosure-分页列表查询")
	@ApiOperation(value="fault_enclosure-分页列表查询", notes="fault_enclosure-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultEnclosure>> queryPageList(FaultEnclosure faultEnclosure,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FaultEnclosure> queryWrapper = QueryGenerator.initQueryWrapper(faultEnclosure, req.getParameterMap());
		Page<FaultEnclosure> page = new Page<FaultEnclosure>(pageNo, pageSize);
		IPage<FaultEnclosure> pageList = faultEnclosureService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param faultEnclosure
	 * @return
	 */
	@AutoLog(value = "fault_enclosure-添加")
	@ApiOperation(value="fault_enclosure-添加", notes="fault_enclosure-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FaultEnclosure faultEnclosure) {
		faultEnclosureService.save(faultEnclosure);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param faultEnclosure
	 * @return
	 */
	@AutoLog(value = "fault_enclosure-编辑")
	@ApiOperation(value="fault_enclosure-编辑", notes="fault_enclosure-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultEnclosure faultEnclosure) {
		faultEnclosureService.updateById(faultEnclosure);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "fault_enclosure-通过id删除")
	@ApiOperation(value="fault_enclosure-通过id删除", notes="fault_enclosure-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		faultEnclosureService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "fault_enclosure-批量删除")
	@ApiOperation(value="fault_enclosure-批量删除", notes="fault_enclosure-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultEnclosureService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "fault_enclosure-通过id查询")
	@ApiOperation(value="fault_enclosure-通过id查询", notes="fault_enclosure-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultEnclosure> queryById(@RequestParam(name="id",required=true) String id) {
		FaultEnclosure faultEnclosure = faultEnclosureService.getById(id);
		if(faultEnclosure==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultEnclosure);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultEnclosure
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FaultEnclosure faultEnclosure) {
        return super.exportXls(request, faultEnclosure, FaultEnclosure.class, "fault_enclosure");
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
        return super.importExcel(request, response, FaultEnclosure.class);
    }

}
