package com.aiurt.modules.positionwifi.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.positionwifi.entity.CsPositionWifi;
import com.aiurt.modules.positionwifi.service.ICsPositionWifiService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.common.util.oConvertUtils;

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
 * @Description: wifi位置管理
 * @Author: aiurt
 * @Date:   2022-11-15
 * @Version: V1.0
 */
@Api(tags="wifi位置管理")
@RestController
@RequestMapping("/posWifi/csPositionWifi")
@Slf4j
public class CsPositionWifiController extends BaseController<CsPositionWifi, ICsPositionWifiService> {
	@Autowired
	private ICsPositionWifiService csPositionWifiService;

	/**
	 * 分页列表查询
	 *
	 * @param csPositionWifi
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "wiif位置管理-分页列表查询")
	@ApiOperation(value="wifi位置管理-分页列表查询", notes="wifi位置管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CsPositionWifi>> queryPageList(CsPositionWifi csPositionWifi,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CsPositionWifi> queryWrapper = QueryGenerator.initQueryWrapper(csPositionWifi, req.getParameterMap());
		Page<CsPositionWifi> page = new Page<CsPositionWifi>(pageNo, pageSize);
		IPage<CsPositionWifi> pageList = csPositionWifiService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param csPositionWifi
	 * @return
	 */
	@AutoLog(value = "wifi位置管理-添加")
	@ApiOperation(value="wifi位置管理-添加", notes="wifi位置管理-添加")
	@PostMapping(value = "/add")
	public Result<CsPositionWifi> add(@RequestBody CsPositionWifi csPositionWifi) {
		Result<CsPositionWifi> result = new Result<CsPositionWifi>();
		try {
			final int count = (int) csPositionWifiService.count(new LambdaQueryWrapper<CsPositionWifi>().eq(CsPositionWifi::getName, csPositionWifi.getName()).eq(CsPositionWifi::getDelFlag, 0).last("limit 1"));
			if (count > 0){
				return Result.error("wifi名称不能重复");
			}
			csPositionWifiService.save(csPositionWifi);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}

		return result;
	}

	/**
	 *  编辑
	 *
	 * @param csPositionWifi
	 * @return
	 */
	@AutoLog(value = "wiif位置管理-编辑")
	@ApiOperation(value="wifi位置管理-编辑", notes="wifi位置管理-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CsPositionWifi csPositionWifi) {
		csPositionWifiService.updateById(csPositionWifi);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "wifi位置管理-通过id删除")
	@ApiOperation(value="wifi位置管理-通过id删除", notes="wifi位置管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		csPositionWifiService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "wifi位置管理-批量删除")
	@ApiOperation(value="wifi位置管理-批量删除", notes="wifi位置管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.csPositionWifiService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "wifi位置管理-通过id查询")
	@ApiOperation(value="wifi位置管理-通过id查询", notes="wifi位置管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CsPositionWifi> queryById(@RequestParam(name="id",required=true) String id) {
		CsPositionWifi csPositionWifi = csPositionWifiService.getById(id);
		if(csPositionWifi==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csPositionWifi);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param csPositionWifi
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CsPositionWifi csPositionWifi) {
        return super.exportXls(request, csPositionWifi, CsPositionWifi.class, "wiif位置管理");
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
        return super.importExcel(request, response, CsPositionWifi.class);
    }

}
